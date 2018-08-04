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

/**
 * Implements the Property Value Data Type "component". A component may contain
 * any of the following characters: "\\" / "\," / "\;" / "\n" / WSP / NON-ASCII
 * / %x21-2B / %x2D-3A / %x3C-5B / %x5D-7E
 *
 * [source: https://tools.ietf.org/html/rfc6350#section-4]
 *
 * @author Stefan Ganzer
 * @see https://tools.ietf.org/html/rfc6350#section-4
 */
final class VCardComponent implements VCardTextValue {

	private static final VCardComponent EMPTY_TEXT = new VCardComponent("", 0);
	private final String originalValue;
	private final String escapedValue;
	private final int elements;

	static VCardComponent nullValue() {
		return EMPTY_TEXT;
	}

	private VCardComponent(String s, int elements) {
		if (s == null) {
			throw new NullPointerException();
		}
		assert elements == 0 || elements == 1 : elements;
		this.elements = elements;

		originalValue = s;
		this.escapedValue = VCardTools.escapeComponent(originalValue);
		VCardTools.CharSubsetCT cs = VCardTools.CharSubsetCT.getSubset(escapedValue);
		switch (cs) {
			case OTHER:
			//fall-through
			case TEXT:
				throw new IllegalArgumentException("The given string contains illegal characters");
			case COMPONENT:
				// the only valid CharSubset for a component.
				break;
			default:
				throw new AssertionError(cs);
		}
	}

	VCardComponent(String s) {
		this(s, 1);
	}

	@Override
	public String getValueAsString() {
		return escapedValue;
	}

	public String getValue() {
		return escapedValue;
	}

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
		if (!(other instanceof VCardComponent)) {
			return false;
		}
		VCardComponent otherVCardText = (VCardComponent) other;
		return this.originalValue.equals(otherVCardText.originalValue);

	}

	@Override
	public int hashCode() {
		return originalValue.hashCode();
	}
}
