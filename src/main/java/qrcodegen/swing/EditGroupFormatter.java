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
package qrcodegen.swing;

/**
 *
 * @author Stefan Ganzer
 */
public class EditGroupFormatter extends AbstractGroupFormatter {

	public EditGroupFormatter() {
		super();
	}

	public EditGroupFormatter(int groupSize) {
		super(groupSize);
	}

	@Override
	String format(String input, int globalOffset) {
		StringBuilder result = new StringBuilder(input.replace(getPlaceholderString(), EMPTY_STRING));

		int groupSize = getGroupSize();
		for (int i = groupSize - (globalOffset % (groupSize + 1)), limit = result.length() + result.length() / groupSize + (globalOffset % (groupSize + 1) == 0 ? 0 : 1); i < limit; i = i + (groupSize + 1)) {
			assert i >= 0 : i;
			assert i <= result.length() : i + " " + result.length();
			result.insert(i, getPlaceholderCharacter());
		}

		return result.toString();
	}
}
