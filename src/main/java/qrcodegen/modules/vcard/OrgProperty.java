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
package qrcodegen.modules.vcard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import qrcodegen.tools.ExtendedIndexOutOfBoundsException;

/**
 * Implements the ORG property.
 *
 * The property value is a structured type consisting of the organization name,
 * followed by zero or more levels of organizational unit names.
 *
 * @author Stefan Ganzer
 */
public final class OrgProperty extends PropertyEntry {

	private final VCardList<VCardComponent> org;

	private OrgProperty(Builder builder) {
		super(builder);
		VCardComponentList<VCardComponent> listOfUnitNames = VCardComponentList.newArrayList();
		for (String s : builder.list) {
			if (s == null) {
				throw new NullPointerException();
			}
			listOfUnitNames.add(new VCardComponent(s));
		}
		org = UnmodifiableVCardList.newInstance(listOfUnitNames);
	}

	public static final class Builder extends Builder2 {

		private final List<String> list = new ArrayList<String>();

		public Builder(String organization) {
			super(Property.ORG);
			list.add(organization);
		}

		public Builder unitName(String unitName) {
			list.add(unitName);
			return this;
		}

		public Builder unitNames(String[] unitNames) {
			for (String s : unitNames) {
				list.add(s.trim());
			}
			return this;
		}

		@Override
		public OrgProperty build() {
			return new OrgProperty(this);
		}
	}

	@Override
	public VCardValue getValue() {
		assert org != null;
		return org;
	}

	@Override
	public String getValueAsString() {
		return org.getValueAsString();
	}

	/**
	 * Returns the organization in its original form.
	 *
	 * @return the organization in its original form. Never returns null.
	 */
	public String getOrganization() {
		return VCardTools.originalValuesOfvCardListToString(org);
	}

	/**
	 * Returns true if an organization is set, false otherwise.
	 *
	 * @return true if an organization is set, false otherwise
	 */
	public boolean hasOrganizationName() {
		return org.size() > 0;
	}

	/**
	 * Returns the organization name in its original form, if set.
	 *
	 * @return the organization name in its original form, if set
	 *
	 * @throws IllegalStateException if no organization is set
	 */
	public String getOrganizationName() {
		if (!hasOrganizationName()) {
			throw new IllegalStateException();
		}
		return org.get(0).getOriginalValue();
	}

	/**
	 * Returns true if this property has unit names, false otherwise.
	 *
	 * @return true if this property has unit names, false otherwise
	 */
	public boolean hasUnitNames() {
		return org.size() > 1;
	}

	/**
	 * Returns the {@code i}th unit name.
	 *
	 * @param i the zero-based index of the unit name to return. Note that
	 * {@code i == 0} does not return the organization name, but the first unit
	 * name.
	 *
	 * @return the {@code i}th unit name
	 *
	 * @throws IllegalStateException if this property has no unit names
	 * @throws IndexOutOfBoundsException
	 */
	public String getUnitName(int i) {
		if (!hasUnitNames()) {
			throw new IllegalStateException();
		}
		if (i < 0 || i > org.size() - 1) {
			throw new ExtendedIndexOutOfBoundsException(0, org.size() - 1, i);
		}
		return org.get(i + 1).getOriginalValue();
	}

	/**
	 * Returns the list of unit names.
	 *
	 * It's always save to call this method, as it returns an empty list if no
	 * unit names are set.
	 *
	 * @return the list of unit names
	 */
	public List<String> getUnitNames() {
		List<String> result;
		if (hasUnitNames()) {
			result = new ArrayList<String>(org.size() - 1);
			for (int i = 1; i < org.size(); i++) {
				result.add(org.get(i).getOriginalValue());
			}
		} else {
			result = Collections.emptyList();
		}
		return result;
	}
}
