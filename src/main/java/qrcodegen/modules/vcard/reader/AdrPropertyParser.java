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
import qrcodegen.modules.vcard.AdrProperty;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.TypeParameter;

/**
 *
 * @author Stefan Ganzer
 */
public class AdrPropertyParser extends PropertyParser {

	private AdrProperty property;
	private boolean isValid = false;
	private boolean isDone = false;

	public AdrPropertyParser() {
		super(Property.ADR);
	}

	@Override
	void parse() {
		try {
			super.parse();
			if (!hasValidParameter()) {
				isValid = false;
				return;
			}

			if (getValue() == null) {
				isValid = false;
				return;
			}

			try {
				AdrProperty.Builder builder = AdrProperty.Builder.newInstance();
				for (TypeParameter t : getTypeParameter()) {
					builder.type(t);
				}
				if (hasLabelParameter()) {
					builder.label(getLabelParameter());
				}
				String[] components = splitComponents(getValue());
				if (components.length != 7) {
					isValid = false;
					return;
				}

				String[] country = splitAndDeescapeComponentList(components[6]);
				builder.country(country);

				String[] postalCode = splitAndDeescapeComponentList(components[5]);
				builder.code(postalCode);
				String[] region = splitAndDeescapeComponentList(components[4]);
				builder.region(region);

				String[] locality = splitAndDeescapeComponentList(components[3]);
				builder.locality(locality);

				String[] street = splitAndDeescapeComponentList(components[2]);
				builder.street(street);

				String[] extendedAddress = splitAndDeescapeComponentList(components[1]);
				builder.extAddress(extendedAddress);
				String[] postOfficeBox = splitAndDeescapeComponentList(components[0]);
				builder.poBox(postOfficeBox);

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
	AdrProperty getPropertyEntry() {
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
		isValid = false;
		isDone = false;
		property = null;
	}
}
