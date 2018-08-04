/*
 Copyright 2011, 2012 Stefan Ganzer

 This file is part of QRCodeGen.

 QRCodeGen is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 QRCodeGen is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen.modules.vcard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardTools {

	private static final String NEW_LINE_SEQUENCES = "\r\n|[\r\n\u0085\u2028\u2029]";
	/** Matches {@code \}, {@code ,}, {@code ;}, {@code :}, and {@code "} */
	private static final Pattern TEXT_ESCAPER = Pattern.compile("([\\\\,])");
	private static final Pattern COMPONENT_ESCAPER = Pattern.compile("([\\\\,;])");
	private static final String ESCAPER_REPLACEMENT = "\\\\$1";
	private static final Pattern DEESCAPER = Pattern.compile("\\\\(.)");
	private static final String DEESCAPER_REPLACEMENT = "$1";
	private static final Pattern NEW_LINE_ESCAPER = Pattern.compile("\r\n|[\r\n]");
	private static final String NEW_LINE_REPLACEMENT = Matcher.quoteReplacement("\\n");
	private static final Pattern ESCAPED_NEW_LINE = Pattern.compile(NEW_LINE_REPLACEMENT);
	private static final String CRLF = "\r\n";
	private static final String WHITESPACE = " ";
	private static final String FOLDING_REPLACEMENT = CRLF + WHITESPACE;
	private static final Pattern FOLDING_REPLACEMENT_PATTERN = Pattern.compile("\r\n[ \t]");
	private static final Pattern TRAILING_NEW_LINE = Pattern.compile("(\r\n|[\r\n\u0085\u2028\u2029])$");
	/** Max file size: {@value} byte */
	private static final int MAX_FILE_SIZE = 200 * 1024;
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final String EMPTY_STRING = "";
	/** {@value} */
	private static final String ELLIPSIS = " [...] ";
	/** {@value} */
	private static final int LEFT_SPACE_FOR_ELLIPSIS = 4;
	/** {@value} */
	private static final int RIGHT_SPACE_FOR_ELLIPSIS = 3;
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	private static final String NEW_LINE = "\n";

	private VCardTools() {
	}

	/**
	 * Escapes backslash, comma, semicolon and newline characters.
	 *
	 * \ -&gt; \\ , -&gt; \, ; -&gt; \; LF, CRLF, CR, ... -&gt; \n
	 *
	 * @param input
	 *
	 * @return
	 */
	static String escapeComponent(CharSequence input) {
		return escape(input, true);
	}

	public static String[] deEscapeArray(CharSequence[] input) {
		if (input == null) {
			throw new NullPointerException();
		}
		String[] result = new String[input.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = deEscape(input[i]);
		}
		return result;
	}

	/**
	 * Escapes newline characters.
	 *
	 * LF, CRLF, CR, ... -&gt; \n
	 *
	 * @param input
	 *
	 * @return
	 */
	static String escapeNewline(CharSequence input) {
		String i = input.toString();

		i = NEW_LINE_ESCAPER.matcher(i).replaceAll(NEW_LINE_REPLACEMENT);
		return i;
	}

	public static String deEscapeNewline(CharSequence input) {
		String i = input.toString();

		i = ESCAPED_NEW_LINE.matcher(i).replaceAll(NEW_LINE);
		return i;
	}

	/**
	 * Escapes backslash, comma and newline characters.
	 *
	 * \ -&gt; \\ , -&gt; \, LF, CRLF, CR, ... -&gt; \n
	 *
	 * @param input
	 *
	 * @return
	 */
	static String escapeText(CharSequence input) {
		return escape(input, false);
	}

	private static String escape(CharSequence input, boolean component) {
		// implicit null-check
		String i = input.toString();

		if (component) {
			i = COMPONENT_ESCAPER.matcher(i).replaceAll(ESCAPER_REPLACEMENT);
		} else { // text
			i = TEXT_ESCAPER.matcher(i).replaceAll(ESCAPER_REPLACEMENT);
		}
		i = NEW_LINE_ESCAPER.matcher(i).replaceAll(NEW_LINE_REPLACEMENT);
		return i;
	}

	public static String deEscape(CharSequence input) {
		String i = input.toString();

		i = ESCAPED_NEW_LINE.matcher(i).replaceAll(NEW_LINE);
		i = DEESCAPER.matcher(i).replaceAll(DEESCAPER_REPLACEMENT);
		return i;
	}

	/**
	 *
	 * @param input the input string to fold. Mustn't be null
	 * @param limit maximum length of each line of the output, excluding the
	 * line break. Limit must be &gt;=6, as the current UTF-8 standard of 2012
	 * contains charactes defined by up to 6 byte, which must remain contiguous
	 * in one line.
	 *
	 * @return
	 * @link https://tools.ietf.org/html/rfc6350
	 */
	static String fold8bitContent(CharSequence input, int limit) {
		if (limit < 6) {
			throw new IllegalArgumentException("Limit must be >= 6, but is: " + limit);
		}
		// Implicit null-check
		StringBuilder sb = new StringBuilder(input);
		/*
		 * [Source: https://tools.ietf.org/html/rfc6350#section-3.2] A logical
		 * line MAY be continued on the next physical line anywhere between two
		 * characters by inserting a CRLF immediately followed by a single white
		 * space character (space (U+0020) or horizontal tab (U+0009)). The
		 * folded line MUST contain at least one character. Any sequence of CRLF
		 * followed immediately by a single white space character is ignored
		 * (removed) when processing the content type. For example, the line:
		 *
		 * NOTE:This is a long description that exists on a long line.
		 *
		 * can be represented as:
		 *
		 * NOTE:This is a long description that exists on a long line.
		 *
		 * It could also be represented as:
		 *
		 * NOTE:This is a long descrip tion that exists o n a long line.
		 *
		 * The process of moving from this folded multiple-line representation
		 * of a property definition to its single-line representation is called
		 * unfolding. Unfolding is accomplished by regarding CRLF immediately
		 * followed by a white space character (namely, HTAB (U+0009) or SPACE
		 * (U+0020)) as equivalent to no characters at all (i.e., the CRLF and
		 * single white space character are removed).
		 *
		 */
		// preferred positions for splitting are commas, semicolons and white spaces
		// according to RFC (mail headers), but we don't beachten this yet
		for (int i = 75; i < sb.length(); i = i + 75) {
			sb.insert(i, FOLDING_REPLACEMENT);
			i = i + 4; // '\''n''\r''\n'
		}
		return sb.toString();
	}

	/**
	 *
	 * @param charset
	 * @param input
	 * @param limit
	 *
	 * @return
	 *
	 * @throws CharacterCodingException
	 * @deprecated
	 */
	@Deprecated
	static String foldContent2(Charset charset, CharSequence input, int limit) throws CharacterCodingException {
		if (charset == null) {
			throw new NullPointerException();
		}

		//implicit null-check
		String i = input.toString();

		final CharsetEncoder charsetEncoder = charset.newEncoder();
		charsetEncoder.onMalformedInput(CodingErrorAction.REPORT);
		charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPORT);

		if (limit < (int) charsetEncoder.maxBytesPerChar()) {
			throw new IllegalArgumentException("Limit must be >= (int)charset.newEncoder().maxBytesPerChar() == " + charsetEncoder.maxBytesPerChar() + ", but is " + limit);
		}

		final CharsetDecoder charsetDecoder = charset.newDecoder();
		charsetDecoder.onMalformedInput(CodingErrorAction.REPORT);
		charsetDecoder.onUnmappableCharacter(CodingErrorAction.REPORT);

		final CharBuffer inputCharBuffer = CharBuffer.wrap(i);

		int maxBytes = (int) charsetEncoder.maxBytesPerChar() * i.length();
		maxBytes = maxBytes + Math.round((maxBytes / (float) limit) * 5);

		final ByteBuffer outputByteBuffer = ByteBuffer.allocate(maxBytes);

		final ByteBuffer foldingReplacement = charsetEncoder.encode(CharBuffer.wrap(FOLDING_REPLACEMENT));
		charsetEncoder.reset();

		final ByteBuffer oneLineByteBuffer = ByteBuffer.allocate(limit);
		while (inputCharBuffer.position() < inputCharBuffer.limit()) {
			oneLineByteBuffer.clear();

			CoderResult cr = charsetEncoder.encode(inputCharBuffer, oneLineByteBuffer, true);

			oneLineByteBuffer.flip();//limit = current Position, position = 0
			outputByteBuffer.put(oneLineByteBuffer);

			if (cr.isOverflow()) {//more lines to come
				outputByteBuffer.put(foldingReplacement);
				foldingReplacement.rewind();
			} else {//this was the only or the last line
				assert cr.isUnderflow() : cr.toString();
			}
		}

		outputByteBuffer.flip();
		return charsetDecoder.decode(outputByteBuffer).toString();
	}

	public static String unfoldContent(CharSequence input) {

		// implicit null-check
		String i = input.toString();
		return FOLDING_REPLACEMENT_PATTERN.matcher(i).replaceAll("");
	}

	static String foldContent(Charset charset, CharSequence input, int limit) throws CharacterCodingException {
		if (charset == null) {
			throw new NullPointerException();
		}

		//implicit null-check
		String i = input.toString();

		// Remove any trailing newline sequence, and append it after the folding is done
		Matcher m = TRAILING_NEW_LINE.matcher(i);
		String newLine = null;
		if (m.find()) {
			newLine = m.group(1);
			i = i.substring(0, m.start());
		}

		final CharsetEncoder charsetEncoder = charset.newEncoder();
		charsetEncoder.onMalformedInput(CodingErrorAction.REPORT);
		charsetEncoder.onUnmappableCharacter(CodingErrorAction.REPORT);

		if (limit < (int) charsetEncoder.maxBytesPerChar() + 1) {
			throw new IllegalArgumentException("Limit must be >= (int)charset.newEncoder().maxBytesPerChar() + 1 == " + (charsetEncoder.maxBytesPerChar() + 1) + ", but is " + limit);
		}

		final CharBuffer inputCharBuffer = CharBuffer.wrap(i);

		int maxBytes = (int) charsetEncoder.maxBytesPerChar() * i.length();
		maxBytes = maxBytes + Math.round((maxBytes / (float) limit) * FOLDING_REPLACEMENT.length());
		final int maxLineBreaks = Math.round((maxBytes / (float) limit) * FOLDING_REPLACEMENT.length());

		StringBuilder resultSB = new StringBuilder(i.length() + maxLineBreaks);

		ByteBuffer oneLineByteBuffer = ByteBuffer.allocate(limit);
		while (inputCharBuffer.position() < inputCharBuffer.limit()) {
			oneLineByteBuffer.clear();

			int oldPosition = inputCharBuffer.position();
			CoderResult cr = charsetEncoder.encode(inputCharBuffer, oneLineByteBuffer, true);

			resultSB.append(i.substring(oldPosition, inputCharBuffer.position()));

			if (cr.isOverflow()) {//more lines to come
				resultSB.append(FOLDING_REPLACEMENT);
				// Take the inserted space into account for the second and
				// all following lines.
				if (oneLineByteBuffer.limit() == limit) {
					oneLineByteBuffer = ByteBuffer.allocate(limit - 1);
				}
			} else {//this was the only or the last line
				assert cr.isUnderflow() : cr.toString();
			}
		}

		if (newLine != null) {
			resultSB.append(newLine);
		}

		return resultSB.toString();
	}
	
	public static String iterableAsDelimitedString(Iterable<?> i, String delimiter){
		StringBuilder result = new StringBuilder();
		Iterator<?> iter = i.iterator();
		while (iter.hasNext()) {
			result.append(String.valueOf(iter.next()));
			if (iter.hasNext()) {
				result.append(delimiter);
			}
		}
		return result.toString();		
	}

	public static String collectionAsDelimitedString(Collection<?> c, String delimiter) {
		StringBuilder result = new StringBuilder(c.size() * 50);
		Iterator<?> iter = c.iterator();
		while (iter.hasNext()) {
			result.append(String.valueOf(iter.next()));
			if (iter.hasNext()) {
				result.append(delimiter);
			}
		}
		return result.toString();
	}

	public static <T extends VCardValue> String asDelimitedString(Collection<T> c, String delimiter) {
		StringBuilder result = new StringBuilder(c.size() * 50);
		Iterator<T> iter = c.iterator();
		while (iter.hasNext()) {
			result.append(iter.next().getValueAsString());
			if (iter.hasNext()) {
				result.append(delimiter);
			}
		}
		return result.toString();
	}

	static boolean isQSafeChar(CharSequence c) {
		// implicit null-check
		String s = c.toString();
		boolean isQSafe = true;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			isQSafe = ch == 0x09 || ch == 0x20 || ch == 0x21 || (ch > 0x22 && ch < 0x7f) || ch > 0x7f;
			if (!isQSafe) {
				break;
			}
		}
		return isQSafe;
	}

	static boolean isSafeChar(CharSequence c) {
		// implicit null-check
		String s = c.toString();
		boolean isSafe = true;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			isSafe = ch == 0x09 || ch == 0x20 || ch == 0x21 || (ch > 0x22 && ch < 0x3A) || (ch > 0x3b && ch < 0x7f) || ch > 0x7f;
			if (!isSafe) {
				break;
			}
		}
		return isSafe;
	}

	enum CharSubset {

		OTHER, QSAFE, SAFE, VALUE;

		static CharSubset getSubset(CharSequence c) {
			// implicit null-check
			String s = c.toString();
			Set<CharSubset> set = EnumSet.of(CharSubset.SAFE);

			for (int i = 0; i < s.length(); i++) {
				char ch = s.charAt(i);
				// HTAB, SP, !, # ... ~, NON-ASCII, without ;:"
				boolean isSafe = ch == 0x09 // HTAB
						|| ch == 0x20 // SP
						|| ch == 0x21 // !
						|| (ch > 0x22 && ch < 0x3A) // without " :
						|| (ch > 0x3b && ch < 0x7f) // without ; DEL
						|| ch > 0x7f; // NON-ASCII
				if (isSafe) {
					set.add(CharSubset.SAFE);
				} else {
					boolean isQSafe = ch == 0x3a || ch == 0x3b; // : ;
					if (isQSafe) {
						set.add(CharSubset.QSAFE);
					} else {
						boolean isValue = ch == 0x22; // "
						if (isValue) {
							set.add(CharSubset.VALUE);
						} else {
							set.add(CharSubset.OTHER);
							break;
						}
					}
				}
			}
			if (set.contains(CharSubset.OTHER)) {
				return CharSubset.OTHER;
			} else if (set.contains(CharSubset.VALUE)) {
				return CharSubset.VALUE;
			} else if (set.contains(CharSubset.QSAFE)) {
				return CharSubset.QSAFE;
			} else if (set.contains(CharSubset.SAFE)) {
				return CharSubset.SAFE;
			} else {
				throw new AssertionError(Arrays.toString(set.toArray()));
			}
		}
	}

	enum CharSubsetCT {

		OTHER, COMPONENT, TEXT;

		static CharSubsetCT getSubset(CharSequence c) {
			// implicit null-check
			String s = c.toString();
			Set<CharSubsetCT> set = EnumSet.of(CharSubsetCT.COMPONENT);

			for (int i = 0; i < s.length(); i++) {
				char ch = s.charAt(i);
				// HTAB, SP ... ~, NON-ASCII, without ,\;
				boolean isComponent = ch == 0x09 // HTAB
						|| (ch > 0x1F && ch < 0x2C) // without ,
						|| (ch > 0x2C && ch < 0x3B) // without ;
						|| (ch > 0x3B && ch < 0x5C) // without \
						|| (ch > 0x5C && ch < 0x7f) // without DEL
						|| ch > 0x7f; // NON-ASCII
				if (isComponent) {
					set.add(CharSubsetCT.COMPONENT);
				} else {
					boolean isText = ch == ';';
					if (isText) {
						set.add(CharSubsetCT.TEXT);
					} else { // maybe it's an escaped character
						boolean isBackslash = ch == 0x5C;
						if (isBackslash) {
							if (i + 1 < s.length()) {
								char nextChar = s.charAt(i + 1);
								boolean isEscapedComponentChar = nextChar == 'n'
										|| nextChar == ','
										|| nextChar == '\\'
										|| nextChar == ';';
								if (isEscapedComponentChar) {
									i = i + 1;
									set.add(CharSubsetCT.COMPONENT);
								} else {
									set.add(CharSubsetCT.OTHER);
								}
							} else {// we are at the end of the input already
								set.add(CharSubsetCT.OTHER);
							}
						} else {// !isBackslash
							set.add(CharSubsetCT.OTHER);
						}
					}
				}
			}
			if (set.contains(CharSubsetCT.OTHER)) {
				return CharSubsetCT.OTHER;
			} else if (set.contains(CharSubsetCT.TEXT)) {
				return CharSubsetCT.TEXT;
			} else if (set.contains(CharSubsetCT.COMPONENT)) {
				return CharSubsetCT.COMPONENT;
			} else {
				throw new AssertionError(Arrays.toString(set.toArray()));
			}
		}
	}

	/**
	 * Reads a file and returns it as string. The line separators are replaced
	 * by \r\n (CRLF).
	 *
	 * @param f the file to read
	 * @param c the charset to use reading the file
	 *
	 * @return the read file as string
	 *
	 * @throws FileNotFoundException
	 * @throws IllegalCharacterException
	 * @throws IOException if an exception occurs while reading the file, or if
	 * the file length exceeds {@value #MAX_FILE_SIZE} bytes
	 * @throws NullPointerException if any of file or charset is null
	 */
	public static String readCard(File f, Charset c) throws FileNotFoundException, IllegalCharacterException, IOException {
		if (f == null) {
			throw new NullPointerException();
		}
		if (c == null) {
			throw new NullPointerException();
		}
		if (f.length() > MAX_FILE_SIZE) {
			throw new IOException("File too large: " + f.length() + " > " + MAX_FILE_SIZE);
		}
		BufferedReader reader = null;
		try {
			StringBuilder sb = new StringBuilder((int) f.length());
			reader = new BufferedReader(new CharacterFilter(new InputStreamReader(new FileInputStream(f), c)));
			while (true) {
				String s = reader.readLine();
				if (s == null) {
					break;
				} else {
					sb.append(s).append(CRLF);
				}
			}
			return sb.toString();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					Logger.getLogger(VCardTools.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		}
	}

	public static void writeCard(File f, String content) throws FileNotFoundException, IOException {
		if (f == null) {
			throw new NullPointerException();
		}
		if (content == null) {
			throw new NullPointerException();
		}

		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new StringReader(content));
			writer = new BufferedWriter(new PrintWriter(new OutputStreamWriter(new FileOutputStream(f), UTF_8)));
			while (true) {
				String s = reader.readLine();
				if (s == null) {
					break;
				} else {
					writer.append(s).append(CRLF);
				}
			}
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ex) {
					Logger.getLogger(VCardTools.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException swallowed) {
				}
			}
		}
	}

	/**
	 *
	 * @param list
	 *
	 * @return
	 */
	public static String originalValuesOfvCardListToString(VCardList<? extends VCardTextValue> list) {
		if (list == null) {
			throw new NullPointerException();
		}
		final String delimiter = list.delimiter();
		StringBuilder sb = new StringBuilder();
		for (int i = 0, size = list.size(); i < size; i++) {
			sb.append(list.get(i).getOriginalValue());
			if (i < size - 1) {
				sb.append(delimiter);
			}
		}
		return sb.toString();

	}

	public static String shorten(String s, int maxLength) {
		if (s == null || s.isEmpty() || maxLength == -1 || s.length() <= maxLength) {
			return s;
		} else if (maxLength == 0) {
			return EMPTY_STRING;
		} else if (maxLength < 73) {
			return s.substring(0, maxLength);
		} else {
			String result = s.substring(0, Math.round(maxLength / 2.0f) - LEFT_SPACE_FOR_ELLIPSIS)
					.concat(ELLIPSIS)
					.concat(s.substring(s.length() - maxLength / 2 + RIGHT_SPACE_FOR_ELLIPSIS, s.length()));
			assert result.length() == maxLength : result.length() + " vs. " + maxLength;
			return result;
		}
	}
}
