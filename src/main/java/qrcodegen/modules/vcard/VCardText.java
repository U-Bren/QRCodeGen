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

import qrcodegen.modules.vcard.VCardTools.CharSubsetCT;

/**
 *
 * @author Stefan Ganzer
 */
final class VCardText implements VCardTextValue {

	private static final VCardText EMPTY_TEXT = new VCardText("", 0);
	private final String originalValue;
	private final String escapedValue;
	private final int elements;

	static VCardText nullValue() {
		return EMPTY_TEXT;
	}

	private VCardText(String s, int elements) {
		if (s == null) {
			throw new NullPointerException();
		}
		assert elements == 0 || elements == 1 : elements;
		this.elements = elements;

		originalValue = s;
		escapedValue = VCardTools.escapeText(originalValue);
		CharSubsetCT cs = VCardTools.CharSubsetCT.getSubset(escapedValue);
		switch (cs) {
			case OTHER:
				throw new IllegalArgumentException("The given string contains illegal characters");
			case TEXT:
			//fall-through
			case COMPONENT:
				// the TEXT and COMPONENT are both valid CharSubset for a Text entry.
				break;
			default:
				throw new AssertionError(cs);
		}
	}

	/**
	 * Constructs a VCardText
	 *
	 * @param s a string containing only legal characters. Mustn't be null.
	 *
	 * @throws NullPointerException if the string is null
	 * @throws IllegalArgumentException if the given string contains characters
	 * not allowed in the CharSubsetCT.TEXT
	 * @see CharSubsetCT
	 */
	VCardText(String s) {
		this(s, 1);
	}

	@Override
	public String getValueAsString() {
		return escapedValue;
	}

	public String getValue() {
		return escapedValue;
	}

	/**
	 * Returns the original value of this VCardText.
	 *
	 * @return the original value of this VCardText. Never returns null.
	 */
	@Override
	public String getOriginalValue() {
		return originalValue;
	}

	@Override
	public String toString() {
		return escapedValue;
	}

	@Override
	public int elements() {
		return elements;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof VCardText)) {
			return false;
		}
		VCardText otherVCardText = (VCardText) other;
		return this.originalValue.equals(otherVCardText.originalValue);

	}

	@Override
	public int hashCode() {
		return originalValue.hashCode();
	}
}
