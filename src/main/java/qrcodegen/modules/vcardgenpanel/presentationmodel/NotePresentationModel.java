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
import qrcodegen.modules.vcardgenpanel.model.AbstractModel;
import qrcodegen.modules.vcardgenpanel.model.VCardNoteModel;

/**
 *
 * @author Stefan Ganzer
 */
public class NotePresentationModel extends AbstractPresentationModel {

	public static final String NOTE_ELEMENT = VCardNoteModel.NOTE_ELEMENT;
	private static final String EMPTY_STRING = "";
	private final ModelListener modelListener = new ModelListener();
	private final VCardNoteModel noteModel;
	private InputValidity validity = InputValidity.EMPTY;

	public NotePresentationModel() {
		this(new VCardNoteModel());
	}

	public NotePresentationModel(VCardNoteModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		this.noteModel = model;
		noteModel.addPropertyChangeListener(modelListener);
	}

	public void setNote(String note) {
		if (note == null) {
			throw new NullPointerException();
		}
		if (note.isEmpty()) {
			noteModel.clear();
		} else {
			noteModel.setNote(note);
		}
	}

	public String getNote() {
		String result;
		if (noteModel.isNoteSet()) {
			result = noteModel.getNote();
		} else {
			result = EMPTY_STRING;
		}
		return result;
	}

	@Override
	public void update() {
		// nothing to do - the model is always in sync
	}

	@Override
	public void clear() {
		noteModel.clear();
	}

	@Override
	public InputValidity getValidity() {
		return noteModel.isNoteSet() ? InputValidity.EMPTY : InputValidity.VALID;
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
			final String propertyName = evt.getPropertyName();
			if (VCardNoteModel.NOTE_ELEMENT.equals(propertyName)) {
				if (noteModel.isNoteSet()) {
					setValidity(InputValidity.VALID);
				} else {
					setValidity(InputValidity.EMPTY);
				}
				firePropertyChange(CONTENT_CHANGED, evt.getOldValue(), evt.getNewValue());
			}
			firePropertyChange(evt);
		}
	}
}
