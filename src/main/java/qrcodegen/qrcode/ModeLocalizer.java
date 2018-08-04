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
package qrcodegen.qrcode;

import com.google.zxing.qrcode.decoder.Mode;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author Stefan Ganzer
 */
public class ModeLocalizer {

	private final Locale locale;
	/** The resource bundle for this class */
	private final ResourceBundle res;
	private final String alphanumeric;
	private final String byte_;
	private final String eci;
	private final String fnc1_1;
	private final String fnc1_2;
	private final String hanzi;
	private final String kanji;
	private final String numeric;
	private final String structuredAppend;
	private final String terminator;
	private final String undefined;

	public ModeLocalizer(Locale locale) {
		if (locale == null) {
			throw new NullPointerException();
		}
		this.locale = locale;
		res = ResourceBundle.getBundle("qrcodegen/qrcode/ModeLocalizer", locale);
		alphanumeric = res.getString("ALPHANUMERIC");
		byte_ = res.getString("BYTE");
		eci = res.getString("ECI");
		fnc1_1 = res.getString("FNC1, 1ST POSITION");
		fnc1_2 = res.getString("FNC1, 2ND POSITION");
		hanzi = res.getString("HANZI");
		kanji = res.getString("KANJI");
		numeric = res.getString("NUMERIC");
		structuredAppend = res.getString("STRUCTURED APPEND");
		terminator = res.getString("TERMINATOR");
		undefined = res.getString("UNDEFINED");
	}

	/**
	 * Returns the locale used by this ModeLocalizer.
	 *
	 * @return the locale used by this ModeLocalizer. Never returns null.
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * Returns the given mode as string.
	 *
	 * @param mode the {@link Mode} to return as string. May be null.
	 *
	 * @return the given mode as string. Never returns null.
	 */
	public String getModeAsLocalizedString(Mode mode) {
		String text;
		if (mode == null) {
			text = undefined;
		} else {
			switch (mode) {
				case ALPHANUMERIC:
					text = alphanumeric;
					break;
				case BYTE:
					text = byte_;
					break;
				case ECI: // Extended Channel Interpretation
					text = eci;
					break;
				case FNC1_FIRST_POSITION:
					text = fnc1_1;
					break;
				case FNC1_SECOND_POSITION:
					text = fnc1_2;
					break;
				case HANZI:
					text = hanzi;
					break;
				case KANJI:
					text = kanji;
					break;
				case NUMERIC:
					text = numeric;
					break;
				case STRUCTURED_APPEND:
					text = structuredAppend;
					break;
				case TERMINATOR:
					text = terminator;
					break;
				default:
					throw new AssertionError(mode);
			}
		}
		return text;
	}
}
