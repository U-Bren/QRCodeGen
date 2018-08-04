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
package qrcodegen.modules.vcard.reader;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardParseException extends Exception {

	private static final long serialVersionUID = 1L;
	private int lineNumber;
	private String line;

	/**
	 * Creates a new instance of
	 * <code>VCardParseException</code> without detail message.
	 */
	public VCardParseException() {
	}

	/**
	 * Constructs an instance of
	 * <code>VCardParseException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public VCardParseException(String msg) {
		super(msg);
	}

	public VCardParseException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public VCardParseException(int lineNumber, String line, String msg, Throwable cause) {
		super(msg, cause);
		this.lineNumber = lineNumber;
		this.line = line;
	}

	public VCardParseException(Throwable cause) {
		super(cause);
	}
}
