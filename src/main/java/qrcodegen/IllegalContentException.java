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
 *
 * @author Stefan Ganzer
 */
public class IllegalContentException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new instance of
	 * <code>IllegalContentException</code> without detail message.
	 */
	public IllegalContentException() {
	}

	/**
	 * Constructs an instance of
	 * <code>IllegalContentException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public IllegalContentException(String msg) {
		super(msg);
	}
}
