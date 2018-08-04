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
import qrcodegen.modules.vcard.FNProperty;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.VCardTools;

/**
 *
 * @author Stefan Ganzer
 */
public class FnPropertyParser extends PropertyParser {

	private FNProperty property;
	private boolean isValid = false;
	private boolean isDone = false;

	public FnPropertyParser() {
		super(Property.FN);
	}

	@Override
	void parse() {
		try {
			super.parse();
			if (!hasValidParameter()) {
				isValid = false;
				return;
			}

			String value = getValue() == null ? "" : getValue();

			String formattedName = VCardTools.deEscape(value);
			try {
				FNProperty.Builder builder = new FNProperty.Builder(formattedName);
				builder.types(getTypeParameter());
				property = builder.build();
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
	FNProperty getPropertyEntry() {
		if (!isValid()) {
			throw new IllegalStateException();
		}
		assert property != null;
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
