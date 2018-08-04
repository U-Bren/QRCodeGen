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

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import qrcodegen.modules.vcard.VCardTools.CharSubset;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardToolsTest {

	private static final String CRLF_SPACE = "\r\n ";
	private static final Charset utf_8 = Charset.forName("UTF-8");

	public VCardToolsTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of escapeContent method, of class VCardController.
	 */
	@Test
	public void testEscapeComponent() {
		System.out.println("escapeComponent");
		List<? extends CharSequence> input = Arrays.asList("Eins, zwei; drei.", "One,\ntwo,\nthree.", "A\\B\\C", "Eins zwei drei.", "\n", " \n ", "", "\\\\", "A,B;C\\D\nE");
		List<String> expResult = Arrays.asList("Eins\\, zwei\\; drei.", "One\\,\\ntwo\\,\\nthree.", "A\\\\B\\\\C", "Eins zwei drei.", "\\n", " \\n ", "", "\\\\\\\\", "A\\,B\\;C\\\\D\\nE");
		for (int i = 0; i < input.size(); i++) {
			System.out.println(expResult.get(i));
			String result = VCardTools.escapeComponent(input.get(i));
			assertEquals(expResult.get(i), result);

			String deescapedResult = VCardTools.deEscape(expResult.get(i));
			assertEquals(input.get(i), deescapedResult);
		}
	}

	@Test(expected = NullPointerException.class)
	public void testEscapeComponentNullInput() {
		System.out.println("escapeComponentNullInput");
		VCardTools.escapeComponent(null);
	}

	@Test
	public void testFold8bitContent() {
		System.out.println("fold8bitContent");
		StringBuilder sb = new StringBuilder(76);
		for (int i = 0; i < 76; i++) {
			sb.append("A");
		}
		StringBuilder sbExpResult = new StringBuilder(sb).insert(75, "\r\n ");
		String result = VCardTools.fold8bitContent(sb, 75);
		assertEquals(sbExpResult.toString(), result);
	}

	@Test
	public void testFoldContent8bitInput() throws CharacterCodingException {
		System.out.println("fold8bitContent8bitInput");

		StringBuilder sb = new StringBuilder(76);
		for (int i = 0; i < 76; i++) {
			sb.append("A");
		}
		StringBuilder sbExpResult = new StringBuilder(sb).insert(75, "\r\n ");
		String result = VCardTools.foldContent(utf_8, sb, 75);
		assertEquals(sbExpResult.toString(), result);
		assertEquals(sb.toString(), VCardTools.unfoldContent(sbExpResult));

		sb = new StringBuilder("A");
		result = VCardTools.foldContent(utf_8, sb, 75);
		assertEquals(sb.toString(), result);
		assertEquals(sb.toString(), VCardTools.unfoldContent(result));

		sb = new StringBuilder("");
		result = VCardTools.foldContent(utf_8, sb, 75);
		assertEquals(sb.toString(), result);
		assertEquals(sb.toString(), VCardTools.unfoldContent(result));

		sb = new StringBuilder(75);
		for (int i = 0; i < 75; i++) {
			sb.append("A");
		}
		String expResult = sb.toString();
		result = VCardTools.foldContent(utf_8, sb, 75);
		assertEquals(expResult, result);
		assertEquals(sb.toString(), VCardTools.unfoldContent(expResult));

	}

	@Test
	public void testFoldContentMultbitInput() throws CharacterCodingException {
		System.out.println("fold8bitContentMultibitInput");

		// number of bytes > limit
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 2; i++) {
			// MAX_CODE_POINT denotes a character that consists of
			// two chars - a surrogate pair
			sb.append(Character.toChars(Character.MAX_CODE_POINT));
		}
		StringBuilder sbExpResult = new StringBuilder(sb).insert(2, "\r\n ");
		String result = VCardTools.foldContent(utf_8, sb, 5);
		assertEquals(sbExpResult.toString(), result);
		assertEquals(sb.toString(), VCardTools.unfoldContent(sbExpResult));

		// corner case: number of bytes == limit
		sb = new StringBuilder();
		sb.append(Character.toChars(Character.MAX_CODE_POINT));
		String expResult = sb.toString();
		result = VCardTools.foldContent(utf_8, sb, 5);
		assertEquals(expResult, result);
		assertEquals(sb.toString(), VCardTools.unfoldContent(expResult));
	}

	/**
	 * This tests for an error in the folding algorithm that didn't take the
	 * inserted space into account when folding the second and all following
	 * lines. As a result those lines could be one character longer than the
	 * limit.
	 *
	 * @throws CharacterCodingException
	 */
	@Test
	public void testFoldContentSpaceIgnoredError() throws CharacterCodingException {
		System.out.println("foldContentSpaceIgnoredError");

		// number of bytes == limit
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 150; i++) {
			sb.append("a");
		}
		StringBuilder sbExpResult = new StringBuilder(sb).insert(75, CRLF_SPACE).insert(75 + CRLF_SPACE.length() + 74, CRLF_SPACE);
		String result = VCardTools.foldContent(utf_8, sb, 75);

		assertEquals(sbExpResult.toString(), result);
		assertEquals(sb.toString(), VCardTools.unfoldContent(sbExpResult));
	}

	/**
	 * This tests for an error in the folding algorith that replaced the last
	 * character in a line even if it was a newline-character.
	 *
	 * @throws CharacterCodingException
	 */
	@Test
	public void testFoldContentFencepostError() throws CharacterCodingException {

		String input = "ADR;TYPE=home;LABEL=Dr. Marlene Musterfrau\\nLange Zeile 63\\n91056 Erlangen\\nFederal Republic of Germany:;;Lange Zeile 63;Erlangen;;91052;Deutschland\r\n";
		String result = VCardTools.foldContent(utf_8, input, 75);
		String expectedResult = "ADR;TYPE=home;LABEL=Dr. Marlene Musterfrau\\nLange Zeile 63\\n91056 Erlangen\\" + CRLF_SPACE + "nFederal Republic of Germany:;;Lange Zeile 63;Erlangen;;91052;Deutschland\r\n";

		assertEquals(expectedResult, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFoldContentWrongLimit() throws CharacterCodingException {
		System.out.println("foldContentWrongLimit");

		String input = "ABC";

		String result = VCardTools.foldContent(utf_8, input, (int) utf_8.newDecoder().maxCharsPerByte() - 1);
	}

	@Test(expected = NullPointerException.class)
	public void testFoldContentNullInput() throws CharacterCodingException {
		System.out.println("foldContentNullInput");

		String result = VCardTools.foldContent(utf_8, null, 75);
	}

	@Test(expected = NullPointerException.class)
	public void testFoldContentNullCharset() throws CharacterCodingException {
		System.out.println("foldContentNullCharset");

		String input = "ABC";

		String result = VCardTools.foldContent(null, input, 75);
	}

	@Test
	public void testIsQSafeChar() {
		System.out.println("isQSafeChar");

		String input = "ab;d e	!";
		boolean expResult = true;
		boolean result = VCardTools.isQSafeChar(input);
		assertEquals(expResult, result);

		input = "ab;\"de";
		expResult = false;
		result = VCardTools.isQSafeChar(input);
		assertEquals(expResult, result);
	}

	@Test
	public void testIsSafeChar() {
		System.out.println("isSafeChar");

		String input = "abd e	!";
		boolean expResult = true;
		boolean result = VCardTools.isSafeChar(input);
		assertEquals(expResult, result);

		input = "ab;\"de";
		expResult = false;
		result = VCardTools.isSafeChar(input);
		assertEquals(expResult, result);

		input = "ab:de";
		expResult = false;
		result = VCardTools.isSafeChar(input);
		assertEquals(expResult, result);

	}

	@Test
	public void testCharSubset() {
		System.out.println("charSubset");

		String input = "abd e !";
		CharSubset expectedResult = VCardTools.CharSubset.SAFE;
		CharSubset result = VCardTools.CharSubset.getSubset(input);
		assertEquals(expectedResult, result);

		input = "abd: e !";
		expectedResult = VCardTools.CharSubset.QSAFE;
		result = VCardTools.CharSubset.getSubset(input);
		assertEquals(expectedResult, result);

		input = "abd; e !";
		expectedResult = VCardTools.CharSubset.QSAFE;
		result = VCardTools.CharSubset.getSubset(input);
		assertEquals(expectedResult, result);

		input = "ab:d; \"e\" !";
		expectedResult = VCardTools.CharSubset.VALUE;
		result = VCardTools.CharSubset.getSubset(input);
		assertEquals(expectedResult, result);

		input = "abd\n";
		expectedResult = VCardTools.CharSubset.OTHER;
		result = VCardTools.CharSubset.getSubset(input);
		assertEquals(expectedResult, result);
	}

	@Test
	public void testCharSubsetEmptyString() {
		System.out.println("charSubsetEmptyString");

		String input = "";
		CharSubset expectedResult = VCardTools.CharSubset.SAFE;
		CharSubset result = VCardTools.CharSubset.getSubset(input);
		assertEquals(expectedResult, result);
	}

	@Test
	public void testCharSubsetCT() {
		System.out.println("charSubsetCT");

		String input = "abd e !";
		VCardTools.CharSubsetCT expectedResult = VCardTools.CharSubsetCT.COMPONENT;
		VCardTools.CharSubsetCT result = VCardTools.CharSubsetCT.getSubset(input);
		assertEquals(expectedResult, result);

		input = "abd\\\\ e !";
		expectedResult = VCardTools.CharSubsetCT.COMPONENT;
		result = VCardTools.CharSubsetCT.getSubset(input);
		assertEquals(expectedResult, result);

		input = "abd\\, e !";
		expectedResult = VCardTools.CharSubsetCT.COMPONENT;
		result = VCardTools.CharSubsetCT.getSubset(input);
		assertEquals(expectedResult, result);

		input = "abd\\; e !";
		expectedResult = VCardTools.CharSubsetCT.COMPONENT;
		result = VCardTools.CharSubsetCT.getSubset(input);
		assertEquals(expectedResult, result);

		input = "abd\\n e !";
		expectedResult = VCardTools.CharSubsetCT.COMPONENT;
		result = VCardTools.CharSubsetCT.getSubset(input);
		assertEquals(expectedResult, result);

		input = "abd; e !";
		expectedResult = VCardTools.CharSubsetCT.TEXT;
		result = VCardTools.CharSubsetCT.getSubset(input);
		assertEquals(expectedResult, result);

		input = "abd\\n \\e !";
		expectedResult = VCardTools.CharSubsetCT.OTHER;
		result = VCardTools.CharSubsetCT.getSubset(input);
		assertEquals(expectedResult, result);

		input = "\t";
		expectedResult = VCardTools.CharSubsetCT.COMPONENT;
		result = VCardTools.CharSubsetCT.getSubset(input);
		assertEquals(expectedResult, result);

		input = "\u000B";
		expectedResult = VCardTools.CharSubsetCT.OTHER;
		result = VCardTools.CharSubsetCT.getSubset(input);
		assertEquals(expectedResult, result);
	}

	@Test
	public void testCharSubsetCTEmptyString() {
		System.out.println("charSubsetCTEmptyString");

		String input = "";
		VCardTools.CharSubsetCT expectedResult = VCardTools.CharSubsetCT.COMPONENT;
		VCardTools.CharSubsetCT result = VCardTools.CharSubsetCT.getSubset(input);
		assertEquals(expectedResult, result);
	}

	@Test(expected = NullPointerException.class)
	public void testCharSubsetCTNullInput() {
		System.out.println("charSubsetCTNullInput");

		String input = null;
		VCardTools.CharSubsetCT expectedResult = VCardTools.CharSubsetCT.COMPONENT;
		VCardTools.CharSubsetCT result = VCardTools.CharSubsetCT.getSubset(input);
		assert false;
	}

	@Test
	public void testShortenSameAsInput() {
		System.out.println("testShortenSameAsInput");

		String input = "";
		int maxLenght = 80;
		String result = VCardTools.shorten(input, maxLenght);
		assertSame(input, result);

		input = null;
		maxLenght = 80;
		result = VCardTools.shorten(input, maxLenght);
		assertSame(input, result);

		input = "abcd";
		maxLenght = -1;
		result = VCardTools.shorten(input, maxLenght);
		assertSame(input, result);

		input = "abcd";
		maxLenght = 4;
		result = VCardTools.shorten(input, maxLenght);
		assertSame(input, result);
	}

	@Test
	public void testShorten() {
		System.out.println("testShorten");

		String input = "abcdefghijklmnopqrstuvwxyzäöüßabcdefghijklmnopqrstuvwxyzäöüßabcdefghijklmnopqrstuvwxyzäöüßabcdefghijklmnopqrstuvwxyzäöüßabcdefghijklmnopqrstuvwxyzäöüß";
		int maxLenght = 11;
		String expResult = "abcdefghijk";
		String result = VCardTools.shorten(input, maxLenght);
		assertEquals(expResult, result);

		maxLenght = 79;
		expResult = "abcdefghijklmnopqrstuvwxyzäöüßabcdef [...] yzäöüßabcdefghijklmnopqrstuvwxyzäöüß";
		result = VCardTools.shorten(input, maxLenght);
		assertEquals(expResult, result);

		maxLenght = 80;
		expResult = "abcdefghijklmnopqrstuvwxyzäöüßabcdef [...] xyzäöüßabcdefghijklmnopqrstuvwxyzäöüß";
		result = VCardTools.shorten(input, maxLenght);
		assertEquals(expResult, result);
	}

	@Test
	public void testCollectionsAsDelimitedString() {
		Collection<?> col = Arrays.asList("a", "b", "c");
		String delimiter = ";";
		String expectedResult = "a;b;c";
		String result = VCardTools.collectionAsDelimitedString(col, delimiter);
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void testCollectionsAsDelimitedStringWithEmptyCollection() {
		Collection<?> col = Arrays.asList();
		String delimiter = ";";
		String expectedResult = "";
		String result = VCardTools.collectionAsDelimitedString(col, delimiter);
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void testCollectionsAsDelimitedStringWithCollectionContainingOneElement() {
		Collection<?> col = Arrays.asList("a");
		String delimiter = ";";
		String expectedResult = "a";
		String result = VCardTools.collectionAsDelimitedString(col, delimiter);
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void testIterableAsDelimitedString() {
		Collection<?> col = Arrays.asList("a", "b", "c");
		String delimiter = ";";
		String expectedResult = "a;b;c";
		String result = VCardTools.iterableAsDelimitedString(col, delimiter);
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void testIterableAsDelimitedStringWithEmptyCollection() {
		Collection<?> col = Arrays.asList();
		String delimiter = ";";
		String expectedResult = "";
		String result = VCardTools.iterableAsDelimitedString(col, delimiter);
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void testIterableAsDelimitedStringWithCollectionContainingOneElement() {
		Collection<?> col = Arrays.asList("a");
		String delimiter = ";";
		String expectedResult = "a";
		String result = VCardTools.iterableAsDelimitedString(col, delimiter);
		assertThat(result, equalTo(expectedResult));
	}
}
