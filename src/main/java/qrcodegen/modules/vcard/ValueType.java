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

import java.util.HashMap;
import java.util.Map;

/**
 * Value types like text, uri, date, ..., language-tag.
 *
 * @author Stefan Ganzer
 */
public enum ValueType implements VCardValue {

	TEXT("text"), URI("uri"), DATE("date"), TIME("time"), DATE_TIME("date-time"),
	DATE_AND_OR_TIME("date-and-or-time"), TIMESTAMP("timestamp"),
	BOOLEAN("boolean"), INTEGER("integer"), FLOAT("float"),
	UTC_OFFSET("utc-offset"), LANGUAGE_TAG("language-tag");
	private final String text;
	private static final Map<String, ValueType> stringToEnum = new HashMap<String, ValueType>(12);

	private ValueType(String s) {
		assert s != null;
		this.text = s;
	}

	static {
		for (ValueType vt : values()) {
			stringToEnum.put(vt.getValueAsString(), vt);
		}
	}

	/**
	 * Returns the enum constant of this type with the specified name. The
	 * string must match exactly the value {@link #getValueAsString()} returns.
	 * Case matters.
	 *
	 * @param s the enum constant of this type with the specified name
	 *
	 * @return the enum constant of this type with the specified name
	 *
	 * @throws IllegalArgumentException if no enum constant of the specified
	 * name exists
	 * @throws NullPointerException if s is null
	 */
	public static ValueType fromString(String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		ValueType vt = stringToEnum.get(s);
		if (vt == null) {
			throw new IllegalArgumentException(s);
		}
		return vt;
	}

	@Override
	public String getValueAsString() {
		return text;
	}

	@Override
	public int elements() {
		return 1;
	}
}
