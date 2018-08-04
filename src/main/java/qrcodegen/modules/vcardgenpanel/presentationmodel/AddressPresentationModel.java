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
package qrcodegen.modules.vcardgenpanel.presentationmodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumSet;
import java.util.Set;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.modules.vcardgenpanel.model.VCardAddressModel;

/**
 *
 * @author Stefan Ganzer
 */
public class AddressPresentationModel extends AbstractPresentationModel {

	public static final String STREET_ELEMENT = VCardAddressModel.STREET_ELEMENT; //NOI18N
	public static final String LOCALITY_ELEMENT = VCardAddressModel.LOCALITY_ELEMENT; //NOI18N
	public static final String REGION_ELEMENT = VCardAddressModel.REGION_ELEMENT; //NOI18N
	public static final String POSTAL_CODE_ELEMENT = VCardAddressModel.POSTAL_CODE_ELEMENT; //NOI18N
	public static final String COUNTRY_NAME_ELEMENT = VCardAddressModel.COUNTRY_NAME_ELEMENT; //NOI18N
	public static final String LABEL_ELEMENT = VCardAddressModel.LABEL_ELEMENT; //NOI18N
	public static final String TYPE_PARAMETER = VCardAddressModel.TYPE_PARAMETER; //NOI18N
	private static final String EMPTY_STRING = "";
	private final VCardAddressModel addressModel;
	private InputValidity validity;

	public AddressPresentationModel(VCardAddressModel addressModel) {
		if (addressModel == null) {
			throw new NullPointerException();
		}
		this.addressModel = addressModel;
		if (addressModel.isAnyFieldSet()) {
			validity = InputValidity.VALID;
		} else {
			validity = InputValidity.EMPTY;
		}
		addressModel.addPropertyChangeListener(new ModelListener());
	}

	public String getStreet() {
		String result;
		if (addressModel.isStreetSet()) {
			result = addressModel.getStreet();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setStreet(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			addressModel.clearStreet();
		} else {
			addressModel.setStreet(value);
		}
	}

	public String getLocality() {
		String result;
		if (addressModel.isLocalitySet()) {
			result = addressModel.getLocality();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setLocality(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			addressModel.clearLocality();
		} else {
			addressModel.setLocality(value);
		}
	}

	public String getRegion() {
		String result;
		if (addressModel.isRegionSet()) {
			result = addressModel.getRegion();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setRegion(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			addressModel.clearRegion();
		} else {
			addressModel.setRegion(value);
		}
	}

	public String getPostalCode() {
		String result;
		if (addressModel.isPostalCodeSet()) {
			result = addressModel.getPostalCode();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setPostalCode(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			addressModel.clearPostalCode();
		} else {
			addressModel.setPostalCode(value);
		}
	}

	public String getCountryName() {
		String result;
		if (addressModel.isCountryNameSet()) {
			result = addressModel.getCountryName();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setCountryName(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			addressModel.clearCountryName();
		} else {
			addressModel.setCountryName(value);
		}
	}

	public String getLabel() {
		String result;
		if (addressModel.isLabelSet()) {
			result = addressModel.getLabel();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setLabel(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			addressModel.clearLabel();
		} else {
			addressModel.setLabel(value);
		}
	}

	public void addTypeParameter(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		addressModel.addType(type);
	}

	public Set<TypeParameter> getTypeParameters() {
		return EnumSet.copyOf(addressModel.getTypeParameters());
	}

	public void removeTypeParameter(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		addressModel.removeType(type);
	}

	public void setTypeParameterOther() {
		addressModel.removeType(TypeParameter.HOME);
		addressModel.removeType(TypeParameter.WORK);
	}

	public void setTypeParameterHome() {
		addressModel.addType(TypeParameter.HOME);
		addressModel.removeType(TypeParameter.WORK);
	}

	public void setTypeParameterWork() {
		addressModel.addType(TypeParameter.WORK);
		addressModel.removeType(TypeParameter.HOME);
	}

	@Override
	public void update() {
		// nothing to do
	}

	@Override
	public void clear() {
		addressModel.clear();
	}

	@Override
	public InputValidity getValidity() {
		assert validity != null;
		return validity;
	}

	private void setValidity(InputValidity v) {
		assert v != null;

		InputValidity oldValidity = this.validity;
		this.validity = v;
		firePropertyChange(VALIDITY, oldValidity, v);
	}

	private class ModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (addressModel.isAnyFieldSet()) {
				setValidity(InputValidity.VALID);
			} else {
				setValidity(InputValidity.EMPTY);
			}
			firePropertyChange(evt);
			firePropertyChange(CONTENT_CHANGED, evt.getOldValue(), evt.getNewValue());
		}
	}
}
