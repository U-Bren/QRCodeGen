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
import java.util.Set;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.modules.vcardgenpanel.model.VCardEMailModel;
import qrcodegen.modules.vcardgenpanel.model.VCardTelModel;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardEMailPresentationModel extends AbstractPresentationModel {

	public static final String EMAIL_ELEMENT = VCardEMailModel.EMAIL_ELEMENT;
	public static final String ENTRY_SELECTED = VCardEMailModel.ENTRY_SELECTED;
	public static final String ENTRY_ADDED = VCardEMailModel.ENTRY_ADDED;
	public static final String ENTRY_REMOVED = VCardEMailModel.ENTRY_REMOVED;
	public static final String TYPE_PARAMETER = VCardEMailModel.TYPE_PARAMETER;
	private static final String EMPTY_STRING = "";
	private final VCardEMailModel eMailModel;
	private InputValidity validity = InputValidity.EMPTY;

	public VCardEMailPresentationModel(VCardEMailModel eMailModel) {
		if (eMailModel == null) {
			throw new NullPointerException();
		}
		this.eMailModel = eMailModel;
		eMailModel.addPropertyChangeListener(new ModelListener());
	}

	public void selectEntry(int index) {
		eMailModel.selectEntry(index);
	}

	public int getSelectedEntry() {
		return eMailModel.getSelectedEntry();
	}

	public boolean isEntrySelected() {
		return eMailModel.isEntrySelected();
	}

	public void setEMail(String value) {
		if (value.isEmpty()) {
			eMailModel.clearEMail();
		} else {
			eMailModel.setEMail(value);
		}
	}

	public String getEMail() {
		if (eMailModel.isEMailSet()) {
			return eMailModel.getEMail();
		} else {
			return EMPTY_STRING;
		}
	}

	public void setTypeParameters(Set<TypeParameter> s) {
		eMailModel.setTypeParameters(s);
	}

	public void addTypeParameter(TypeParameter type) {
		eMailModel.addType(type);
	}

	public Set<TypeParameter> getTypeParameters() {
		return eMailModel.getTypeParameters();
	}

	public void removeTypeParameter(TypeParameter type) {
		eMailModel.removeType(type);
	}

	public void clearTypeParameters() {
		eMailModel.clearTypeParamters();
	}

	public void setTypeParameterOther() {
		removeTypeParameter(TypeParameter.HOME);
		removeTypeParameter(TypeParameter.WORK);
	}

	public void setTypeParameterHome() {
		addTypeParameter(TypeParameter.HOME);
		removeTypeParameter(TypeParameter.WORK);
	}

	public void setTypeParameterWork() {
		addTypeParameter(TypeParameter.WORK);
		removeTypeParameter(TypeParameter.HOME);
	}

	@Override
	public void update() {
		// nothing to do
	}

	@Override
	public void clear() {
		// clear every entry - from last to first,
		// so when we are finished the first entry is selected
		// Don't remove any entry!
		eMailModel.clear();
	}

	@Override
	public InputValidity getValidity() {
		return validity;
	}

	private void setValidity(InputValidity iv) {
		assert iv != null;

		InputValidity oldValidity = this.validity;
		this.validity = iv;
		firePropertyChange(VALIDITY, oldValidity, iv);
	}

	private class ModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (VCardEMailModel.EMAIL_ELEMENT.equals(propertyName)
					|| VCardEMailModel.ENTRY_ADDED.equals(propertyName)
					|| VCardEMailModel.ENTRY_REMOVED.equals(propertyName)) {
				InputValidity iv = eMailModel.isEmpty() ? InputValidity.EMPTY : InputValidity.VALID;
				setValidity(iv);
			}
			firePropertyChange(evt);
			firePropertyChange(CONTENT_CHANGED, evt.getOldValue(), evt.getNewValue());
		}
	}
}
