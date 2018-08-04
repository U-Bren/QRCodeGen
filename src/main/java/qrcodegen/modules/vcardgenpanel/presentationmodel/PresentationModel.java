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

import java.beans.PropertyChangeListener;
import qrcodegen.modules.vcardgenpanel.InputValidity;

/**
 *
 * @author Stefan Ganzer
 */
public interface PresentationModel {

	/**
	 * Synchronizes view with model, if necessary.
	 */
	void update();

	/**
	 * Clears view and model.
	 */
	void clear();

	/**
	 * Returns the validity state of this presentation model:
	 *
	 * VALID: View and Model are synchronized, input / data is valid and
	 * non-empty
	 *
	 * INVALID: Synchronization of View and Model failed, so View and Model are
	 * in an unsynchronus state
	 *
	 * UNDEFINED: The synchronization state is unknow. This can be for instance
	 * due to changes in the View that have not been syncronized with the Model
	 * yet.
	 *
	 * EMPTY: The View is empty and the according data in the model is not set.
	 *
	 * @return the validity of this PresentationModel
	 */
	InputValidity getValidity();

	/**
	 * Registers an PropertyChangeListener with this PresentationModel.
	 *
	 * @param listener
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);
}
