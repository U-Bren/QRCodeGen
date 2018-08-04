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
import qrcodegen.modules.vcardgenpanel.model.VCardTelModel;

/**
 *
 * @author Stefan Ganzer
 */
public class TelephonePresentationModel extends AbstractPresentationModel {

	public static final String NUMBER_ELEMENT = VCardTelModel.NUMBER_ELEMENT;
	public static final String ENTRY_SELECTED = VCardTelModel.ENTRY_SELECTED;
	public static final String ENTRY_ADDED = VCardTelModel.ENTRY_ADDED;
	public static final String ENTRY_REMOVED = VCardTelModel.ENTRY_REMOVED;
	public static final String TYPE_PARAMETER = VCardTelModel.TYPE_PARAMETER;
	private static final String EMPTY_STRING = "";
	private final VCardTelModel telModel;
	private InputValidity validity = InputValidity.EMPTY;

	public TelephonePresentationModel(VCardTelModel telModel) {
		if (telModel == null) {
			throw new NullPointerException();
		}
		this.telModel = telModel;
		telModel.addPropertyChangeListener(new ModelListener());
	}

	public void selectEntry(int index) {
		telModel.selectEntry(index);
	}

	public int getSelectedEntry() {
		return telModel.getSelectedEntry();
	}

	public boolean isEntrySelected() {
		return telModel.isEntrySelected();
	}

	public void setNumber(String number) {
		if (number.isEmpty()) {
			telModel.clearNumber();
		} else {
			telModel.setNumber(number);
		}
	}

	public String getNumber() {
		if (telModel.isNumberSet()) {
			return telModel.getNumber();
		} else {
			return EMPTY_STRING;
		}
	}

	public void setTypeParameters(Set<TypeParameter> s) {
		telModel.setTypeParameters(s);
	}

	public void addTypeParameter(TypeParameter type) {
		telModel.addType(type);
	}

	public Set<TypeParameter> getTypeParameters() {
		return telModel.getTypeParameters();
	}

	public void removeTypeParameter(TypeParameter type) {
		telModel.removeType(type);
	}

	public void clearTypeParameters() {
		telModel.clearTypeParamters();
	}

	public void setFaxParameter(boolean set) {
		setTypeParameter(set, TypeParameter.FAX);
	}

	public boolean isFaxParameterSet() {
		return telModel.isTypeSet(TypeParameter.FAX);
	}

	public boolean isVoiceParameterSet() {
		return telModel.isTypeSet(TypeParameter.VOICE);
	}

	public boolean isCellParameterSet() {
		return telModel.isTypeSet(TypeParameter.CELL);
	}

	public void setCellParameter(boolean set) {
		setTypeParameter(set, TypeParameter.CELL);
	}

	public void setVoiceParameter(boolean set) {
		setTypeParameter(set, TypeParameter.VOICE);
	}

	private void setTypeParameter(boolean set, TypeParameter p) {
		assert p != null;
		if (set) {
			addTypeParameter(p);
		} else {
			removeTypeParameter(p);
		}
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
		telModel.clear();
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
			if (VCardTelModel.NUMBER_ELEMENT.equals(propertyName)
					|| VCardTelModel.ENTRY_ADDED.equals(propertyName)
					|| VCardTelModel.ENTRY_REMOVED.equals(propertyName)) {
				InputValidity iv = telModel.isEmpty() ? InputValidity.EMPTY : InputValidity.VALID;
				setValidity(iv);
			}
			firePropertyChange(evt);
			firePropertyChange(CONTENT_CHANGED, evt.getOldValue(), evt.getNewValue());
		}
	}
}
