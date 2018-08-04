/*
 Copyright 2012 Stefan Ganzer

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

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

/**
 * This CharacterFilter throws an IllegalCharacterException if it encounters any
 * control characters apart from HT, CR or LF.
 *
 * The blueprint for this class is from Arnold, Gosling, Holmes: 'The Java
 * Programming Language, Fourth Edition'
 *
 * @author Stefan Ganzer
 */
public class CharacterFilter extends FilterReader {

	public CharacterFilter(Reader reader) {
		super(reader);
	}

	/**
	 * @param buf
	 * @param offset
	 * @param count
	 *
	 * @return
	 *
	 * @throws IllegalCharacterException
	 * @throws IOException
	 */
	@Override
	public int read(char[] buf, int offset, int count) throws IllegalCharacterException, IOException {
		int nread = super.read(buf, offset, count);
		int last = offset + nread;
		for (int i = offset; i < last; i++) {
			char ch = buf[i];
			if (isIllegalCharacter(ch)) {
				throw new IllegalCharacterException(getCharacterName(ch));
			}
		}
		return nread;
	}

	public static boolean isIllegalCharacter(char ch) {
		return ch < 0x09 // NUL ... BS
				|| (ch > 0x0A && ch < 0x0D) // VT ... FF
				|| (ch > 0x0D && ch < 0x20) // SO ... US
				|| (ch == 0x7f);			// DEL
	}

	/**
	 * From Java 1.7 API: Character.getName(int codepoint)
	 *
	 * @param codepoint
	 *
	 * @return
	 *
	 * @see Character.getName(int)
	 */
	private static String getCharacterName(int codepoint) {
		return Character.UnicodeBlock.of(codepoint).toString().replace('_', ' ') + " " + Integer.toHexString(codepoint).toUpperCase(Locale.ENGLISH);
	}
}
