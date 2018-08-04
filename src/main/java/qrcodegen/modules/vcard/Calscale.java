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
package qrcodegen.modules.vcard;

/**
 *
 * @author Stefan Ganzer
 */
public enum Calscale implements VCardValue {

	GREGORIAN("gregorian");
	private final String name;

	private Calscale(String name) {
		assert name != null;
		this.name = name;
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
	public static Calscale fromString(String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		if (GREGORIAN.name.equals(s)) {
			return GREGORIAN;
		}
		throw new IllegalArgumentException(s);
	}

	@Override
	public String getValueAsString() {
		return name;
	}

	@Override
	public int elements() {
		return 1;
	}
}
