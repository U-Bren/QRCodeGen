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
package qrcodegen.modules;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import qrcodegen.Encodable;
import qrcodegen.IllegalContentException;

/**
 * WirelessNetworkContent creates a string suitable for generating a WiFi QR
 * Code from network credentials.
 *
 * @author Stefan Ganzer
 */
public final class WirelessNetworkContent implements Encodable {

	public static final String PASSWORD_TYPE_PROPERTY = "PasswordType";
	public static final String ACTUAL_PASSWORD_TYPE_PROPERTY = "ActualPasswordType";
	public static final String PASSWORD_PROPERTY = "Password";
	public static final String SSID_TYPE_PROPERTY = "SsidType";
	public static final String SSID_PROPERTY = "SSID";
	public static final String ACTUAL_SSID_TYPE_PROPERTY = "ActualSsidType";
	public static final String NETWORK_TYPE_PROPERTY = "NetworkType";
	public static final String CONTENT_PROPERTY = "ContentChanged";
	public static final String HIDDEN_NETWORK_PROPERTY = "HiddenNetwork";
	private static final String DELIMITER = ";";
	private static final String EMPTY_STRING = "";
	private static final String NETWORK_TYPE_PREFIX = "T:";
	private static final String NEWLINE = "\n";
	private static final String PASSWORD_PREFIX = "P:";
	private static final String SSID_PREFIX = "S:";
	private static final String WIFI_PREFIX = "WIFI:";
	private static final String DQUOTE = "\"";
	private static final String HIDDEN_NETWORK_PREFIX = "H:";
	/** Matches {@code \}, {@code ,}, {@code ;}, {@code :}, and {@code "} */
	private static final Pattern ESCAPER = Pattern.compile("([\\\\,;:\"])");
	private static final String ESCAPER_REPLACEMENT = "\\\\$1";
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private NetworkType networkType = NetworkType.WEP;
	private String ssid = EMPTY_STRING;
	private String escapedSSID = EMPTY_STRING;
	private String password = EMPTY_STRING;
	private String escapedPassword = EMPTY_STRING;
	private Type ssidType = Type.STRING;
	private Type actualSsidType = Type.HEX;
	private Type passwordType = Type.STRING;
	private Type actualPasswordType = Type.HEX;
	private String result = wifiString();
	private boolean isHiddenNetwork = false;
	/** True if result reflects the set properties, false if result is outdated. */
	private boolean isUpToDate = true;

	public WirelessNetworkContent() {
	}

	/**
	 *
	 * @param ssid
	 * @param password
	 * @param networkType
	 *
	 * @throws IllegalContentException if ssid or password contain a new-line
	 * character
	 */
	public WirelessNetworkContent(String ssid, String password, NetworkType networkType) {
		setSSID(ssid);
		setPassword(password);
		setNetworkType(networkType);
	}

	public WirelessNetworkContent(String ssid, String password, NetworkType networkType, boolean isHiddenNetwork) {
		setSSID(ssid);
		setPassword(password);
		setNetworkType(networkType);
		setIsHiddenNetwork(isHiddenNetwork);
	}

	public WirelessNetworkContent(String ssid, Type ssidType, String password, Type passwordType, NetworkType networkType, boolean isHiddenNetwork) {
		if (ssidType == null) {
			throw new NullPointerException();
		}
		if (passwordType == null) {
			throw new NullPointerException();
		}
		setSsidType(ssidType);
		setSSID(ssid);
		setPasswordType(passwordType);
		setPassword(password);
		setNetworkType(networkType);
		setIsHiddenNetwork(isHiddenNetwork);
	}

	/**
	 * Sets the NetworkType.
	 *
	 * @param nt
	 *
	 * @throws NullPointerException if the given network type is null
	 */
	public void setNetworkType(NetworkType nt) {
		NetworkType oldType = this.networkType;
		isUpToDate = false;
		this.networkType = requireNonNull(nt);
		pcs.firePropertyChange(NETWORK_TYPE_PROPERTY, oldType, nt);
	}

	/**
	 * Returns the current network type.
	 *
	 * @return the current network type. This method returns never {@code null}.
	 */
	public NetworkType getNetworkType() {
		return networkType;
	}

	/**
	 * Sets the password.
	 *
	 * @param password
	 *
	 * @throws IllegalContentException if the given password contains a new-line
	 * character
	 * @throws NullPointerException is the given password is {@code null}
	 * @throws IllegalArgumentException if the password contains non-hex
	 * characters while the password type is Type.HEX
	 * @see #getPasswordType()
	 * @see #setPasswordType(qrcodegen.modules.WirelessNetworkContent.Type) */
	public void setPassword(String password) {
		if (password == null) {
			throw new NullPointerException();
		}
		if (!this.password.equals(password)) {
			final Type actualType = Type.getTypeFor(password);
			switch (passwordType) {
				case HEX:
					if (actualType != Type.HEX) {
						throw new IllegalArgumentException(actualType.toString());
					}
				//fall-through
				case STRING: {
					String oldPassword = this.password;
					isUpToDate = false;
					this.password = password;
					this.escapedPassword = escapeString(this.password);
					setActualPasswordType(actualType);
					pcs.firePropertyChange(PASSWORD_PROPERTY, oldPassword, password);

				}
				break;
				default:
					throw new AssertionError(passwordType);

			}
		}
	}

	/**
	 * Returns the current password.
	 *
	 * @return the current password. This method returns never {@code null}.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the escaped form of the password.
	 *
	 * @return the escaped form of the password
	 *
	 * @see #escapeString(java.lang.String)
	 */
	public String getEscapedPassword() {
		return escapedPassword;
	}

	/**
	 * Sets the password type.
	 *
	 * Available options are STRING and HEX. A STRING type password is allowed
	 * to contain any characters, a HEX type only [0-9a-fA-F].
	 *
	 * @param type the password type
	 *
	 * @throws IllegalStateException if the actual password type is Type.STRING,
	 * and you try to set the type to Type.HEX
	 */
	public void setPasswordType(Type type) {
		if (type == null) {
			throw new NullPointerException();
		}
		if (type == Type.HEX && actualPasswordType == Type.STRING) {
			throw new IllegalStateException();
		}
		Type oldType = this.passwordType;
		isUpToDate = false;
		this.passwordType = type;
		pcs.firePropertyChange(PASSWORD_TYPE_PROPERTY, oldType, type);
	}

	/**
	 * Returns the password type.
	 *
	 * @return the password type
	 */
	public Type getPasswordType() {
		return passwordType;
	}

	/**
	 * Returns the actual password type.
	 *
	 * If the password contains only hex characters, this method will return
	 * {@code Type.HEX}, regardless of the set type.
	 *
	 * @return the actual password type
	 */
	public Type getActualPasswordType() {
		return actualPasswordType;
	}

	private void setActualPasswordType(Type type) {
		assert type != null;
		Type oldType = this.actualPasswordType;
		isUpToDate = false;
		actualPasswordType = type;
		pcs.firePropertyChange(ACTUAL_PASSWORD_TYPE_PROPERTY, oldType, type);
	}

	/**
	 * Sets the SSID.
	 *
	 * @param ssid
	 *
	 * @throws IllegalContentException if the given ssid contains a new-line
	 * character
	 * @throws NullPointerException if the given SSID is null
	 * @throws IllegalArgumentException if the ssid contains non-hex characters
	 * while the ssid type is Type.HEX
	 * @see #getSsidType()
	 * @see #setSsidType(qrcodegen.modules.WirelessNetworkContent.Type)
	 */
	public void setSSID(String ssid) {
		if (ssid == null) {
			throw new NullPointerException();
		}
		if (!this.ssid.equals(ssid)) {
			Type actualType = Type.getTypeFor(ssid);
			switch (ssidType) {
				case HEX:
					if (actualType != Type.HEX) {
						throw new IllegalArgumentException(actualType.toString());
					}
				//fall-through
				case STRING:
					String oldSSID = this.ssid;
					isUpToDate = false;
					this.ssid = ssid;
					escapedSSID = escapeString(ssid);
					setActualSsidType(actualType);
					pcs.firePropertyChange(SSID_PROPERTY, oldSSID, ssid);
					break;
				default:
					throw new AssertionError(ssidType);
			}
		}
	}

	/**
	 * Returns the current SSID.
	 *
	 * @return the current SSID. This method returns never {@code null}.
	 */
	public String getSSID() {
		return ssid;
	}

	/**
	 * Returns the escaped form of the SSID.
	 *
	 * @return the escaped form of the SSID
	 *
	 * @see #escapeString(java.lang.String)
	 *
	 */
	public String getEscapedSSID() {
		return escapedSSID;
	}

	/**
	 * Sets the SSID type.
	 *
	 * Available options are STRING and HEX. A STRING type SSID is allowed to
	 * contain any character, a HEX type only [0-9a-fA-F].
	 *
	 * @param type
	 *
	 * @throws IllegalStateException if the actual SSID type is Type.STRING, and
	 * you try to set the type to Type.HEX
	 */
	public void setSsidType(Type type) {
		if (type == null) {
			throw new NullPointerException();
		}
		if (type == Type.HEX && actualSsidType == Type.STRING) {
			throw new IllegalStateException();
		}
		Type oldType = this.ssidType;
		isUpToDate = false;
		this.ssidType = type;
		pcs.firePropertyChange(SSID_TYPE_PROPERTY, oldType, type);
	}

	public Type getSsidType() {
		return ssidType;
	}

	/**
	 * Returns the actual type of the ssid.
	 *
	 * If the ssid contains only hex characters, this method will return
	 * {@code Type.HEX}, regardless of the set type.
	 *
	 * @return the actual type of the ssid
	 */
	public Type getActualSsidType() {
		return actualSsidType;
	}

	private void setActualSsidType(Type type) {
		assert type != null;
		Type oldType = this.actualSsidType;
		isUpToDate = false;
		actualSsidType = type;
		pcs.firePropertyChange(ACTUAL_SSID_TYPE_PROPERTY, oldType, type);
	}

	public void setIsHiddenNetwork(boolean isHidden) {
		boolean oldValue = this.isHiddenNetwork;
		this.isHiddenNetwork = isHidden;
		isUpToDate = false;
		pcs.firePropertyChange(HIDDEN_NETWORK_PROPERTY, oldValue, this.isHiddenNetwork);
	}

	public boolean getIsHiddenNetwork() {
		return isHiddenNetwork;
	}

	/**
	 * Updates this WirelessNetworkContent instance so {@link #getContent()}
	 * reflects any changes made by invoking setPassword, setSSID or
	 * setNetworkType.
	 */
	public void update() {
		String oldResult = result;
		result = wifiString();
		isUpToDate = true;
		pcs.firePropertyChange(CONTENT_PROPERTY, oldResult, result);
	}

	/**
	 * Returns this instance content as a string suitable for creating a WiFi QR
	 * Code from, according to the proposal by Google. If
	 * {@link  #getEscapedPassword} and {@link #getEscapedSSID} both return
	 * empty strings, an empty string is returned.
	 *
	 * @return this instance content as a string suitable for creating a WiFi QR
	 * Code from. This method never returns null.
	 *
	 * @throws IllegalStateException if update hasn't been called since changes
	 * have been made to any of this instance's property
	 *
	 * @see <a
	 * href="https://code.google.com/p/zxing/wiki/BarcodeContents#Wifi_Network_config_(Android)">https://code.google.com/p/zxing/wiki/BarcodeContents#Wifi_Network_config_(Android)</a>
	 */
	@Override
	public String getContent() {
		if (!isUpToDate) {
			throw new IllegalStateException();
		}
		if (escapedPassword.isEmpty() && escapedSSID.isEmpty()) {
			return EMPTY_STRING;
		} else {
			return result;
		}
	}

	/**
	 * Returns true if no changes have been made to any of this instance's
	 * property since the last call to update, false otherwise.
	 *
	 * @return true if no changes have been made to any of this instance's
	 * property since the last call to update, false otherwise
	 */
	public boolean isUpToDate() {
		return isUpToDate;
	}

	/**
	 * Returns the input string with all backslashes (\), commas (,), semicolons
	 * (;), colons (:), and double-quotes (") escaped. Escaping will occur in
	 * this order.
	 *
	 * @param input
	 *
	 * @return
	 *
	 * @throws IllegalContentException if the given input string contains a
	 * new-line character
	 * @throws NullPointerException if input is null
	 */
	public static String escapeString(String input) {
		if (input.isEmpty()) {
			return input;
		}
		if (input.contains(NEWLINE)) {
			throw new IllegalContentException("The input string contains a new line character");
		}

		return ESCAPER.matcher(input).replaceAll(ESCAPER_REPLACEMENT);
	}

	/**
	 * Returns this instance content as a string suitable for creating a WiFi QR
	 * Code from, according to the proposal by Google.
	 * {@link <a href="https://code.google.com/p/zxing/wiki/BarcodeContents#Wifi_Network_config_(Android)">https://code.google.com/p/zxing/wiki/BarcodeContents#Wifi_Network_config_(Android)</a>}
	 *
	 * @return this instance content as a string suitable for creating a WiFi QR
	 * Code from
	 */
	private String wifiString() {
		// In https://code.google.com/p/zxing/wiki/BarcodeContents#Wifi_Network_config_%28Android%29
		// only S(SID) is required, everything else is optional
		StringBuilder sb = new StringBuilder(escapedPassword.length() + escapedSSID.length() + 30);
		sb.append(WIFI_PREFIX);
		sb.append(SSID_PREFIX).append(getSsidForWifiString()).append(DELIMITER);
		if (NetworkType.NO_ENCRYPTION != networkType) {
			sb.append(NETWORK_TYPE_PREFIX).append(networkType.getForQRCode()).append(DELIMITER);
		}
		if (NetworkType.NO_ENCRYPTION != networkType && !password.isEmpty()) {
			sb.append(PASSWORD_PREFIX).append(getPasswordForWifiString()).append(DELIMITER);
		}
		if (isHiddenNetwork) {// Optional. Only append if network is hidden.
			sb.append(HIDDEN_NETWORK_PREFIX).append(Boolean.toString(isHiddenNetwork)).append(DELIMITER);
		}
		sb.append(DELIMITER);
		return sb.toString();
	}

	private String getSsidForWifiString() {
		String result;
		switch (ssidType) {
			case HEX:
				result = escapedSSID;
				break;
			case STRING:
				result = encloseInDQuoteIfActualTypeIsHex(actualSsidType, escapedSSID);
				break;
			default:
				throw new AssertionError(ssidType);
		}
		return result;
	}

	private String getPasswordForWifiString() {
		String result;
		switch (passwordType) {
			case HEX:
				result = escapedPassword;
				break;
			case STRING:
				result = encloseInDQuoteIfActualTypeIsHex(actualPasswordType, escapedPassword);
				break;
			default:
				throw new AssertionError(passwordType);
		}
		return result;
	}

	private static <T> T requireNonNull(T t) {
		if (t == null) {
			throw new NullPointerException();
		}
		return t;
	}

	private static String encloseInDQuoteIfActualTypeIsHex(Type actualType, CharSequence input) {
		assert actualType != null;
		String s = input.toString();
		String result;
		switch (actualType) {
			case HEX:
				result = DQUOTE.concat(s).concat(DQUOTE);
				break;
			case STRING:
				result = s;
				break;
			default:
				throw new AssertionError(actualType);
		}
		return result;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}
}
