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

import java.io.IOException;

/**
 *
 * @author Stefan Ganzer
 */
public class IllegalCharacterException extends IOException {

	/**
	 * Creates a new instance of
	 * <code>IllegalCharacterException</code> without detail message.
	 */
	public IllegalCharacterException() {
	}

	/**
	 * Constructs an instance of
	 * <code>IllegalCharacterException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public IllegalCharacterException(String msg) {
		super(msg);
	}

	public IllegalCharacterException(Throwable cause) {
		super(cause);
	}

	public IllegalCharacterException(String message, Throwable cause) {
		super(message, cause);
	}
}
