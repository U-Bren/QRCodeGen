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
 *
 * @author Stefan Ganzer
 */
public class VCardContentPropertyException extends RuntimeException {

	/**
	 * Creates a new instance of
	 * <code>VCardContentPropertyException</code> without detail message.
	 */
	public VCardContentPropertyException() {
	}

	/**
	 * Constructs an instance of
	 * <code>VCardContentPropertyException</code> with the specified detail
	 * message.
	 *
	 * @param msg the detail message.
	 */
	public VCardContentPropertyException(String msg) {
		super(msg);
	}
}
