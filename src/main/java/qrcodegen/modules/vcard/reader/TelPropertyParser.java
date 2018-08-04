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

import java.util.logging.Level;
import java.util.logging.Logger;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.TelProperty;
import qrcodegen.modules.vcard.VCardTools;

/**
 *
 * @author Stefan Ganzer
 */
public class TelPropertyParser extends PropertyParser {

	private TelProperty property;
	private boolean isValid = false;
	private boolean isDone = false;

	TelPropertyParser() {
		super(Property.TEL);
	}

	@Override
	void parse() {
		// TODO For now we make no difference between a text or uri value.
		try {
			super.parse();
			if (!hasValidParameter()) {
				isValid = false;
				return;
			}

			String value = getValue() == null ? EMPTY_STRING : getValue();

			String telNumber = VCardTools.deEscape(value);
			try {
				TelProperty.Builder builder = new TelProperty.Builder(telNumber);
				builder.types(getTypeParameter());
				this.property = builder.build();
				isValid = true;
			} catch (IllegalArgumentException iae) {
				LOGGER.log(Level.SEVERE, null, iae);
			} catch (IllegalStateException ise) {
				LOGGER.log(Level.SEVERE, null, ise);
			}
		} finally {
			isDone = true;
		}
	}

	@Override
	TelProperty getPropertyEntry() {
		if (!isValid()) {
			throw new IllegalStateException();
		}
		return property;
	}

	@Override
	boolean isValid() {
		return isValid;
	}

	@Override
	boolean isDone() {
		return super.isDone() && isDone;
	}

	@Override
	public void reset(String params, String value) {
		super.reset(params, value);
		property = null;
		isValid = false;
		isDone = false;
	}
}
