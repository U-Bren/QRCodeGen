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

import qrcodegen.modules.vcard.VCardTools.CharSubset;

/**
 *
 * @author Stefan Ganzer
 */
final class VCardParameterValue implements VCardValue {

	private static final VCardParameterValue EMPTY_TEXT = new VCardParameterValue("", 0);
	private static final String DOUBLE_QUOTE = "\"";
	private final String originalValue;
	private final String escapedValue;
	private final int elements;

	static VCardParameterValue nullValue() {
		return EMPTY_TEXT;
	}

	private VCardParameterValue(String s, int elements) {
		if (s == null) {
			throw new NullPointerException();
		}
		assert elements == 0 || elements == 1 : elements;
		this.elements = elements;

		originalValue = s;
		String tempEscapedValue = VCardTools.escapeNewline(originalValue);
		CharSubset cs = VCardTools.CharSubset.getSubset(tempEscapedValue);
		switch (cs) {
			case OTHER:
			// fall-through
			case VALUE:
				throw new IllegalArgumentException("The given string contains illegal characters");
			case QSAFE:
				escapedValue = DOUBLE_QUOTE.concat(tempEscapedValue).concat(DOUBLE_QUOTE);
				break;
			case SAFE:
				escapedValue = tempEscapedValue;
				break;
			default:
				throw new AssertionError(cs);
		}
	}

	VCardParameterValue(String s) {
		this(s, 1);
	}

	@Override
	public String getValueAsString() {
		return escapedValue;
	}

	public String getValue() {
		return escapedValue;
	}

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
		if (!(other instanceof VCardParameterValue)) {
			return false;
		}
		VCardParameterValue otherVCardText = (VCardParameterValue) other;
		return this.originalValue.equals(otherVCardText.originalValue);

	}

	@Override
	public int hashCode() {
		return originalValue.hashCode();
	}
}
