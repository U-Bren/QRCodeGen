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
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.modules.vcardgenpanel.model.VCardNameModel;

/**
 * This class is to be used with a VCardNamePanel and a VCardNameModel. It
 * updates the input from the view instantly with the model.
 *
 * @author Stefan Ganzer
 */
public class NamePresentationModel extends AbstractPresentationModel {

	public static final String FORMATTED_NAME_ELEMENT = VCardNameModel.FORMATTED_NAME_ELEMENT;
	public static final String LAST_NAME_ELEMENT = VCardNameModel.LAST_NAME_ELEMENT;
	public static final String FIRST_NAME_ELEMENT = VCardNameModel.FIRST_NAME_ELEMENT;
	public static final String ADDITIONAL_NAMES_ELEMENT = VCardNameModel.ADDITIONAL_NAMES_ELEMENT;
	public static final String HONORIFIC_PREFIXES_ELEMENT = VCardNameModel.HONORIFIC_PREFIXES_ELEMENT;
	public static final String HONORIFIC_SUFFIXES_ELEMENT = VCardNameModel.HONORIFIC_SUFFIXES_ELEMENT;
	public static final String NICKNAME_ELEMENT = VCardNameModel.NICKNAME_ELEMENT;
	public static final String ORG_ELEMENT = VCardNameModel.ORG_ELEMENT;
	public static final String UNIT_NAMES_ELEMENT = VCardNameModel.UNIT_NAMES_ELEMENT;
	private static final String EMPTY_STRING = "";
	private final PropertyChangeListener modelListener = new NameModelListener();
	private final VCardNameModel nameModel;
	private InputValidity validity = InputValidity.EMPTY;

	public NamePresentationModel(VCardNameModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		this.nameModel = model;
		nameModel.addPropertyChangeListener(modelListener);
	}

	public void setFormattedName(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			nameModel.clearFormattedName();
		} else {
			nameModel.setFormattedName(value);
		}
	}

	public String getFormattedName() {
		String result;
		if (nameModel.isFormattedNameSet()) {
			result = nameModel.getFormattedName();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setLastName(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			nameModel.clearLastName();
		} else {
			nameModel.setLastName(value);
		}
	}

	public String getLastName() {
		String result;
		if (nameModel.isLastNameSet()) {
			result = nameModel.getLastName();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setFirstName(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			nameModel.clearFirstName();
		} else {
			nameModel.setFirstName(value);
		}
	}

	public String getFirstName() {
		String result;
		if (nameModel.isFirstNameSet()) {
			result = nameModel.getFirstName();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setAdditionalNames(String values) {
		if (values == null) {
			throw new NullPointerException();
		}
		if (values.isEmpty()) {
			nameModel.clearAdditionalNames();
		} else {
			nameModel.setAdditionalNames(values);
		}
	}

	public String getAdditionalNames() {
		String result;
		if (nameModel.isAdditionalNamesSet()) {
			result = nameModel.getAdditionalNames();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setHonorificPrefixes(String values) {
		if (values == null) {
			throw new NullPointerException();
		}
		if (values.isEmpty()) {
			nameModel.clearHonorificPrefixes();
		} else {
			nameModel.setHonorificPrefixes(values);
		}
	}

	public String getHonorificPrefixes() {
		String result;
		if (nameModel.isHonorificPrefixesSet()) {
			result = nameModel.getHonorificPrefixes();
		} else {
			result = EMPTY_STRING;
		}
		return result;

	}

	public void setHonorificSuffixes(String values) {
		if (values == null) {
			throw new NullPointerException();
		}
		if (values.isEmpty()) {
			nameModel.clearHonorificSuffixes();
		} else {
			nameModel.setHonorificSuffixes(values);
		}
	}

	public String getHonorificSuffixes() {
		String result;
		if (nameModel.isHonorificSuffixesSet()) {
			result = nameModel.getHonorificSuffixes();
		} else {
			result = EMPTY_STRING;
		}
		return result;

	}

	public void setNickname(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			nameModel.clearNickname();
		} else {
			nameModel.setNickname(value);
		}
	}

	public String getNickname() {
		String result;
		if (nameModel.isNicknameSet()) {
			result = nameModel.getNickname();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setOrg(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			nameModel.clearOrg();
		} else {
			nameModel.setOrg(value);
		}
	}

	public String getOrg() {
		String result;
		if (nameModel.isOrgSet()) {
			result = nameModel.getOrg();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	public void setUnitNames(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.isEmpty()) {
			nameModel.clearUnitNames();
		} else {
			nameModel.setUnitNames(value);
		}
	}

	public String getUnitNames() {
		String result;
		if (nameModel.isUnitNamesSet()) {
			result = nameModel.getUnitNames();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	@Override
	public void update() {
		// nothing to do
	}

	@Override
	public void clear() {
		nameModel.clear();
	}

	@Override
	public InputValidity getValidity() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private void setValidity(InputValidity v) {
		assert v != null;

		InputValidity oldValidity = this.validity;
		this.validity = v;
		firePropertyChange(VALIDITY, oldValidity, v);
	}

	private class NameModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (!nameModel.isSet()) {
				setValidity(InputValidity.EMPTY);
			} else {
				if (!nameModel.isOrgPropertyEmptyOrValid()) {
					setValidity(InputValidity.INVALID);
				} else {
					setValidity(InputValidity.VALID);
				}
			}
			firePropertyChange(evt);
			firePropertyChange(CONTENT_CHANGED, evt.getOldValue(), evt.getNewValue());
		}
	}
}
