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
interface VCardValue {

	/**
	 * The value of this VCardValue as string, without a line break (\n, \r\n,
	 * ...) at the end of the string.
	 *
	 * @return value as string, without a line break (\n, \r\n, ...) at the end
	 * of the string
	 */
	public String getValueAsString();

	/**
	 * The number of elements this VCardValue consists of.
	 *
	 * @return number of elements this VCardValue consists of
	 */
	public int elements();
}
