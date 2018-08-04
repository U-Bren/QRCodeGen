/*
 * Copyright (C) 2012, 2013 Stefan Ganzer
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
package qrcodegen.tools;

/**
 *
 * @author Stefan Ganzer
 */
public final class TextShortener implements Shortener<String> {

	private static final String EMPTY_STRING = "";
	/** {@value} */
	private static final int LIMIT_FOR_USING_ELLIPSIS = 14;
	/** {@value} */
	private static final String ELLIPSIS = " [...] ";
	/** {@value} */
	private static final int LEFT_SPACE_FOR_ELLIPSIS = 4;
	/** {@value} */
	private static final int RIGHT_SPACE_FOR_ELLIPSIS = 3;
	private final int maxLength;

	public TextShortener(int maxLength) {
		if (maxLength < 0) {
			throw new IllegalArgumentException(String.format("%1$d < 0", maxLength));
		}
		this.maxLength = maxLength;
	}

	@Override
	public String shorten(String s) {
		if (s == null || s.isEmpty() || maxLength == -1 || s.length() <= maxLength) {
			return s;
		} else if (maxLength == 0) {
			return EMPTY_STRING;
		} else if (maxLength < LIMIT_FOR_USING_ELLIPSIS) {
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
