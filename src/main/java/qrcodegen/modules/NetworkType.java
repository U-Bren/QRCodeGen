/*
 Copyright 2011 Stefan Ganzer
 
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
package qrcodegen.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * NetworkType provides values for the different network types a MEGACARD like
 * WiFi code as proposed by Google can contain.
 *
 * @author Stefan Ganzer
 * @see <a
 * href="https://code.google.com/p/zxing/wiki/BarcodeContents#Wifi_Network_config_(Android)">https://code.google.com/p/zxing/wiki/BarcodeContents#Wifi_Network_config_(Android)</a>
 */
public enum NetworkType {

	WEP("WEP", "WEP"), WPA_WPA2("WPA/WPA2", "WPA"), NO_ENCRYPTION(ResourceBundle.getBundle("qrcodegen/modules/NetworkType").getString("NO ENCRYPTION"), "nopass");
	private static final Map<String, NetworkType> stringToEnum = new HashMap<String, NetworkType>(3);
	private final String displayName;
	private final String forQRCode;

	static {
		for (NetworkType nt : values()) {
			stringToEnum.put(nt.getForQRCode(), nt);
		}
	}

	private NetworkType(String s1, String s2) {
		this.displayName = requireNonNull(s1);
		this.forQRCode = requireNonNull(s2);
	}

	/**
	 * Returns this NetworkType's string value that can be used in composing a
	 * WiFi QR Code. <ul> <li> WEP: WEP <li> WPA_WPA2: WPA <li> NO_ENCRYPTION:
	 * nopass </ul>
	 *
	 * @return this NetworkType's string value that can be used in composing a
	 * WiFi QR Code
	 */
	public String getForQRCode() {
		return forQRCode;
	}

	@Override
	public String toString() {
		return displayName;
	}

	/**
	 * Returns the enum constant of this type with the specified name. The
	 * string must match exactly the value {@link #getForQRCode()} returns. Case
	 * matters.
	 *
	 * @param s the enum constant of this type with the specified name
	 *
	 * @return the enum constant of this type with the specified name
	 *
	 * @throws IllegalArgumentException if no enum constant of the specified
	 * name exists
	 * @throws NullPointerException if s is null
	 */
	public static NetworkType fromString(String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		NetworkType nt = stringToEnum.get(s);
		if (nt == null) {
			throw new IllegalArgumentException(s);
		}
		return nt;
	}

	private static <T> T requireNonNull(T t) {
		if (t == null) {
			throw new NullPointerException();
		}
		return t;
	}
}
