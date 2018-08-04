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
package qrcodegen.modules.vcardgenpanel;

import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JPanel;
import qrcodegen.modules.vcard.PropertyEntry;

/**
 *
 * @author Stefan Ganzer
 */
public interface VCardPropertyProvider {

	/**
	 * Clears all input fields of this panel.
	 */
	public void clear();

//	/**
//	 * Returns a map of VCardProperties this object provides. This map may not be
//	 * null, and it may not contain null-keys or null-values.
//	 *
//	 * @return
//	 */
//	public Map<Property, VCardProperty<? extends PropertyEntry>> getVCardProperty();
	List<PropertyEntry> getPropertyEntries();

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
