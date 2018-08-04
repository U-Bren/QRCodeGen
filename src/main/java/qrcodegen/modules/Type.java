/*
 * Copyright (C) 2012 Stefan Ganzer
 *
 * This file is part of QRCodeGen.
 *
 * QRCodeGen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QRCodeGen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen.modules;

import java.util.regex.Pattern;

/**
 *
 * @author Stefan Ganzer
 */
public enum Type {
	STRING, HEX;
	private static final Pattern HEX_PATTERN = Pattern.compile("^[a-fA-F0-9]*+$");

	static Type getTypeFor(CharSequence input) {
		if (input == null) {
			throw new NullPointerException();
		}
		String s = input.toString();
		Type result;
		if (HEX_PATTERN.matcher(s).matches()) {
			result = HEX;
		} else {
			result = STRING;
		}
		return result;
	}
	
}
