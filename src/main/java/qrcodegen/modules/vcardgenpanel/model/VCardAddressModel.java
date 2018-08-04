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
package qrcodegen.modules.vcardgenpanel.model;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import qrcodegen.modules.vcard.AdrProperty;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcardgenpanel.VCardPropertyProvider;
import qrcodegen.tools.CollectionTools;
import qrcodegen.tools.StaticTools;

/**
 * This class models a VCard address. It is designed to work in conjunction with
 * an AddressPresentationModel and a VCardAddressPanel (view).
 *
 * None of this classes setter methods accept null as parameter, if not stated
 * otherwise. All getter methods are guaranteed not to return null-values, if
 * not stated otherwise.
 *
 * @author Stefan Ganzer
 */
public class VCardAddressModel extends AbstractModel implements VCardPropertyProvider {

	public static final String STREET_ELEMENT = "Street"; //NOI18N
	public static final String LOCALITY_ELEMENT = "Locality"; //NOI18N
	public static final String REGION_ELEMENT = "Region"; //NOI18N
	public static final String POSTAL_CODE_ELEMENT = "PostalCode"; //NOI18N
	public static final String COUNTRY_NAME_ELEMENT = "State"; //NOI18N
	public static final String LABEL_ELEMENT = "Label"; //NOI18N
	public static final String TYPE_PARAMETER = "TypeParameter"; //NOI18N
	private static final Pattern VALUE_SPLITTER = Pattern.compile(","); //NOI18N
	private final Set<TypeParameter> types = EnumSet.noneOf(TypeParameter.class);
	private String street = null;
	private String locality = null;
	private String region = null;
	private String postalCode = null;
	private String countryName = null;
	private String label = null;

	public VCardAddressModel() {
	}

	/**
	 * Returns the street address.
	 *
	 * @return the street address. Never returns null.
	 *
	 * @throws IllegalStateException if the street address is not set
	 */
	public String getStreet() {
		if (!isStreetSet()) {
			throw new IllegalStateException();
		}
		return street;
	}

	/**
	 * Sets the street address.
	 *
	 * @param newStreet a string denoting a street address
	 *
	 * @see #clearStreet()
	 * @throws NullPointerException if newStreet is null
	 */
	public void setStreet(String newStreet) {
		if (newStreet == null) {
			throw new NullPointerException();
		}
		setStreetValue(newStreet);
	}

	/**
	 * Clears the street address.
	 */
	public void clearStreet() {
		setStreetValue(null);
	}

	/**
	 * Returns true if the street address is set, false otherwise.
	 *
	 * @return true if the street address is set, false otherwise
	 */
	public boolean isStreetSet() {
		return street != null;
	}

	private void setStreetValue(String value) {
		String oldStreet = this.street;
		this.street = value;

		firePropertyChange(STREET_ELEMENT, oldStreet, value);
	}

	public String getLocality() {
		if (!isLocalitySet()) {
			throw new IllegalStateException();
		}
		return locality;
	}

	public void setLocality(String newLocality) {
		if (newLocality == null) {
			throw new NullPointerException();
		}
		setLocalityValue(newLocality);
	}

	/**
	 * Clears the locality.
	 */
	public void clearLocality() {
		setLocalityValue(null);
	}

	/**
	 * Returns true if the locality is set, false otherwise.
	 *
	 * @return true if the locality is set, false otherwise
	 */
	public boolean isLocalitySet() {
		return locality != null;
	}

	private void setLocalityValue(String value) {
		String oldLocality = this.locality;
		this.locality = value;

		firePropertyChange(LOCALITY_ELEMENT, oldLocality, value);
	}

	public String getRegion() {
		if (!isRegionSet()) {
			throw new IllegalStateException();
		}
		return region;
	}

	public void setRegion(String newRegion) {
		if (newRegion == null) {
			throw new NullPointerException();
		}
		setRegionValue(newRegion);
	}

	/**
	 * Clears the region.
	 */
	public void clearRegion() {
		setRegionValue(null);
	}

	/**
	 * Returns true if the region is set, false otherwise.
	 *
	 * @return true if the region is set, false otherwise
	 */
	public boolean isRegionSet() {
		return region != null;
	}

	private void setRegionValue(String value) {
		String oldRegion = this.region;
		this.region = value;

		firePropertyChange(REGION_ELEMENT, oldRegion, value);
	}

	public String getPostalCode() {
		if (!isPostalCodeSet()) {
			throw new IllegalStateException();
		}
		return postalCode;
	}

	public void setPostalCode(String newPostalCode) {
		if (newPostalCode == null) {
			throw new NullPointerException();
		}
		setPostalCodeValue(newPostalCode);
	}

	/**
	 * Clears the postalcode.
	 */
	public void clearPostalCode() {
		setPostalCodeValue(null);
	}

	/**
	 * Returns true if the postalcode is set, false otherwise.
	 *
	 * @return true if the postalcode is set, false otherwise
	 */
	public boolean isPostalCodeSet() {
		return postalCode != null;
	}

	private void setPostalCodeValue(String value) {
		String oldPostalCode = this.postalCode;
		this.postalCode = value;

		firePropertyChange(POSTAL_CODE_ELEMENT, oldPostalCode, value);
	}

	public String getCountryName() {
		if (!isCountryNameSet()) {
			throw new IllegalStateException();
		}
		return countryName;
	}

	public void setCountryName(String newCountryName) {
		if (newCountryName == null) {
			throw new NullPointerException();
		}
		setCountryNameValue(newCountryName);
	}

	/**
	 * Clears the country name.
	 */
	public void clearCountryName() {
		setCountryNameValue(null);
	}

	/**
	 * Returns true if the country name is set, false otherwise.
	 *
	 * @return true if the country name is set, false otherwise
	 */
	public boolean isCountryNameSet() {
		return countryName != null;
	}

	private void setCountryNameValue(String value) {
		String oldCountryName = this.countryName;
		this.countryName = value;

		firePropertyChange(COUNTRY_NAME_ELEMENT, oldCountryName, value);
	}

	public String getLabel() {
		if (!isLabelSet()) {
			throw new IllegalStateException();
		}
		return label;
	}

	public void setLabel(String newLabel) {
		if (newLabel == null) {
			throw new NullPointerException();
		}
		setLabelValue(newLabel);
	}

	/**
	 * Clears the label.
	 */
	public void clearLabel() {
		setLabelValue(null);
	}

	/**
	 * Returns true if the label is set, false otherwise.
	 *
	 * @return true if the label is set, false otherwise
	 */
	public boolean isLabelSet() {
		return label != null;
	}

	private void setLabelValue(String value) {
		String oldLabel = this.label;
		this.label = value;

		firePropertyChange(LABEL_ELEMENT, oldLabel, value);
	}

	/**
	 * Sets all fields (street, locality, ...) to an empty string, but doesn't
	 * touch the type parameters.
	 *
	 * @see #clearTypes()
	 * @see #clear()
	 */
	public void clearFields() {
		clearStreet();
		clearLocality();
		clearRegion();
		clearPostalCode();
		clearCountryName();
		clearLabel();
	}

	/**
	 * Returns true if none of street address, locality, region, postal code,
	 * country name or label is set, false if at least one of them is set.
	 *
	 * @return true if none of street address, locality, region, postal code,
	 * country name or label is set, false if at least one of them is set
	 */
	public boolean isFieldsEmpty() {
		return street == null
				&& locality == null
				&& region == null
				&& postalCode == null
				&& countryName == null
				&& label == null;
	}

	/**
	 * Returns true if any one of street address, locality, region, postal code,
	 * country name or label is set, false if none of them is set.
	 *
	 * @return true if any one of street address, locality, region, postal code,
	 * country name or label is set, false if none of them is set
	 */
	public boolean isAnyFieldSet() {
		boolean result = isStreetSet()
				|| isLocalitySet()
				|| isRegionSet()
				|| isPostalCodeSet()
				|| isCountryNameSet()
				|| isLabelSet();
		return result;
	}

	/**
	 * Adds the given type parameter to this address. This method only fires an
	 * event if the given type has not been a part of this address prior to
	 * invoking this method.
	 *
	 * @param type
	 *
	 * @throws NullPointerException if type is null
	 */
	public void addType(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		boolean changed = types.add(type);
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	/**
	 * Removes the given type parameter from this address, if present. This
	 * method only fires an event if the given type has been a part of this
	 * address.
	 *
	 * @param type
	 *
	 * @throws NullPointerException if type is null
	 */
	public void removeType(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		boolean changed = types.remove(type);
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	/**
	 * Returns the type parameters of this address as set.
	 *
	 * @return a {@literal Set<TypeParameters>}. Returns an empty set of this
	 * address has no type parameters. The caller is free to modify the returned
	 * set without changing this address.
	 */
	public Set<TypeParameter> getTypeParameters() {
		return EnumSet.copyOf(types);
	}

	/**
	 * Clears all type parameters, if present. This doesn't change any other
	 * fields, though.
	 *
	 * @see #clear()
	 * @see #clearFields()
	 */
	public void clearTypes() {
		boolean changed = !types.isEmpty();
		types.clear();
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	public boolean isTypesEmpty() {
		return types.isEmpty();
	}
	
	/**
	 * Clears all fields, i.e. all fields are set to an empty string, and all
	 * type parameters are removed.
	 *
	 * @see #clearFields()
	 * @see #clearTypes()
	 */
	@Override
	public void clear() {
		clearFields();
		clearTypes();
	}

	/**
	 * Returns true if all fields are empty and no types are set, false
	 * otherwise.
	 *
	 * @return true if all fields are empty and no types are set, false
	 * otherwise
	 */
	public boolean isEmpty() {
		return isFieldsEmpty() && isTypesEmpty();
	}

	/**
	 * Sets all to the values provided by the given AdrProperty.
	 *
	 * @param property an AdrProperty. This mustn't be null.
	 *
	 * @throws NullPointerException if property is null
	 */
	public void setFromPropertyEntry(AdrProperty property) {
		if (property == null) {
			throw new NullPointerException();
		}
		setStreet(property.getStreet());
		setLocality(property.getLocality());
		setRegion(property.getRegion());
		setPostalCode(property.getPostalCode());
		setCountryName(property.getCountryName());
		if (property.hasLabel()) {
			setLabel(property.getLabel());
		}
		for (TypeParameter tp : property.getType()) {
			addType(tp);
		}
	}

	@Override
	public List<PropertyEntry> getPropertyEntries() {
		List<PropertyEntry> list = new ArrayList<PropertyEntry>(1);
		CollectionTools.addIfNonNull(list, getAdrProperty());
		return list;
	}

	private AdrProperty getAdrProperty() {

		if (!isAnyFieldSet()) {
			return null;
		}
		AdrProperty.Builder builder = AdrProperty.Builder.newInstance();
		if (isPostalCodeSet()) {
			builder.code(VALUE_SPLITTER.split(getPostalCode()));
		}
		if (isRegionSet()) {
			builder.region(VALUE_SPLITTER.split(getRegion()));
		}
		if (isLocalitySet()) {
			builder.locality(VALUE_SPLITTER.split(getLocality()));
		}
		if (isStreetSet()) {
			builder.street(VALUE_SPLITTER.split(getStreet()));
		}
		if (isCountryNameSet()) {
			builder.country(VALUE_SPLITTER.split(getCountryName()));
		}
		builder.types(types);
		if (isLabelSet()) {
			builder.label(label);
		}
		return builder.build();
	}
}
