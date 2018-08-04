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
package qrcodegen.modules.vcard.reader;

import java.util.Arrays;
import java.util.logging.Level;
import qrcodegen.modules.vcard.OrgProperty;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcard.VCardTools;

/**
 *
 * @author Stefan Ganzer
 */
public class OrgPropertyParser extends PropertyParser {

	private OrgProperty property;
	private boolean isValid = false;
	private boolean isDone = false;

	public OrgPropertyParser() {
		super(Property.ORG);
	}

	@Override
	public void parse() {
		try {
			super.parse();
			if (!hasValidParameter()) {
				isValid = false;
				return;
			}

			String value = getValue() == null ? EMPTY_STRING : getValue();

			try {
				String[] values = splitAndDeescapeComponentList(value);
				String organizationName = values.length == 0 ? EMPTY_STRING : values[0];
				OrgProperty.Builder builder = new OrgProperty.Builder(organizationName);

				if (values.length > 1) {
					String[] unitNames = Arrays.copyOfRange(values, 1, values.length);
					builder.unitNames(unitNames);
				}

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
	OrgProperty getPropertyEntry() {
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
