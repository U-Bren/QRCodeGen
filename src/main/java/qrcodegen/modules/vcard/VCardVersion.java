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
package qrcodegen.modules.vcard;

/**
 *
 * @author Stefan Ganzer
 */
enum VCardVersion {

	V21(2, 0), V30(3, 0), V40(4, 0);
	private final int major;
	private final int minor;
	private final String versionString;

	private VCardVersion(int major, int minor) {
		this.major = major;
		this.minor = minor;
		this.versionString = String.format("%1$d.%2$d", major, minor);
	}

	int getMajor() {
		return major;
	}

	int getMinor() {
		return minor;
	}

	@Override
	public String toString() {
		return versionString;
	}
}
