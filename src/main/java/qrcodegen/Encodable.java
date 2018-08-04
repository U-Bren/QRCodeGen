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
package qrcodegen;

/**
 * An Encodable is a class that provides data that can be represented as
 * QR-Code. To obtain that data as String, call {@link getContent()} on the
 * Encodable-instance.
 *
 * @author Stefan Ganzer
 */
public interface Encodable {

	/**
	 * Returns the content of this encodable as String.
	 * 
	 * @return the content of this encodable as String. Never returns null.
	 */
	String getContent();
}

class NullEncodable implements Encodable {

	private static final String EMPTY_STRING = "";
	private static final NullEncodable INSTANCE = new NullEncodable();

	static NullEncodable getInstance() {
		return INSTANCE;
	}

	// Don't let anyone instatiate this directly.
	private NullEncodable() {
	}

	@Override
	public String getContent() {
		return EMPTY_STRING;
	}
}