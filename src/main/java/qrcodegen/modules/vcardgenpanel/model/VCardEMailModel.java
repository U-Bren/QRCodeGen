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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import qrcodegen.modules.vcard.EMailProperty;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcard.TelProperty;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcardgenpanel.VCardPropertyProvider;
import qrcodegen.tools.CollectionTools;
import qrcodegen.tools.ExtendedIndexOutOfBoundsException;
import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardEMailModel extends AbstractModel implements VCardPropertyProvider {

	public static final String EMAIL_ELEMENT = "EMail";
	public static final String ENTRY_SELECTED = "EntrySelected";
	public static final String ENTRY_ADDED = "EntryAdded";
	public static final String ENTRY_REMOVED = "EntryRemoved";
	public static final String TYPE_PARAMETER = TypesModel.TYPE_PARAMETER;
	private static final int MIN_ENTRIES = 0;
	private static final int MAX_ENTRIES = 10;
	private final PropertyChangeListener entryListener = new EntryListener();
	private final Entry.EntryFactory<String> entryFactory = new Entry.EntryFactory<String>(Property.EMAIL, EMAIL_ELEMENT, entryListener);
	private final int minEntries;
	private final int maxEntries;
	private List<Entry<String>> eMailEntries = new ArrayList<Entry<String>>(3);
	private Entry<String> currentEntry = null;

	public VCardEMailModel() {
		this(MIN_ENTRIES, MAX_ENTRIES, 0);
	}

	public VCardEMailModel(int min, int max) {
		this(min, max, 0);
	}

	public VCardEMailModel(int numberOfEntries) {
		this(MIN_ENTRIES, MAX_ENTRIES, numberOfEntries);
	}

	public VCardEMailModel(int min, int max, int numberOfEntries) {
		if (min < MIN_ENTRIES) {
			throw new ExtendedIndexOutOfBoundsException(MIN_ENTRIES, max, min);
		}
		if (max > MAX_ENTRIES) {
			throw new ExtendedIndexOutOfBoundsException(min, MAX_ENTRIES, max);
		}
		if (min > max) {
			throw new IllegalArgumentException("min > max: " + min + " > " + max);
		}
		if (numberOfEntries < min || numberOfEntries > max) {
			throw new ExtendedIndexOutOfBoundsException(min, max, numberOfEntries);
		}

		minEntries = min;
		maxEntries = max;

		for (int i = 0; i < numberOfEntries; i++) {
			eMailEntries.add(entryFactory.newEntry());
		}
		if (numberOfEntries > 0) {
			currentEntry = eMailEntries.get(0);
		}
	}

	public void insertEntry() {
		if (!canAddEntry()) {
			throw new IllegalStateException();
		}
		throw new UnsupportedOperationException();
	}

	public void addEntry() {
		if (!canAddEntry()) {
			throw new IllegalStateException("at most max entries allowed: " + maxEntries);
		}
		int oldSize = eMailEntries.size();
		eMailEntries.add(entryFactory.newEntry());
		int newSize = eMailEntries.size();
		firePropertyChange(ENTRY_ADDED, oldSize, newSize);
		if (currentEntry == null) {
			selectEntry(0);
		}
	}

	public void removeEntry() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		if (!canRemoveEntry()) {
			throw new IllegalStateException("min entries required: " + minEntries);
		}
		int index = eMailEntries.indexOf(currentEntry);
		int oldSize = eMailEntries.size();
		currentEntry.removePropertyChangeListener(entryListener);
		eMailEntries.remove(currentEntry);
		int newSize = eMailEntries.size();
		firePropertyChange(ENTRY_REMOVED, oldSize, newSize);
		int newIndex = index < eMailEntries.size() ? index : eMailEntries.size() - 1;
		selectEntry(newIndex);
	}

	public boolean canAddEntry() {
		return eMailEntries.size() < maxEntries;
	}

	public boolean canRemoveEntry() {
		return eMailEntries.size() > minEntries;
	}

	public void selectEntry(int index) {
		if (index < 0 || index >= eMailEntries.size()) {
			throw new ExtendedIndexOutOfBoundsException(0, eMailEntries.size() - 1, index);
		}
		int oldIndex = currentEntry == null ? -1 : eMailEntries.indexOf(currentEntry);
		currentEntry = eMailEntries.get(index);
		firePropertyChange(ENTRY_SELECTED, oldIndex, index);
	}

	public int getSelectedEntry() {
		if (!hasEntries()) {
			throw new IllegalStateException();
		}
		return eMailEntries.indexOf(currentEntry);
	}

	public boolean isEntrySelected() {
		boolean result = currentEntry != null;
		assert result != eMailEntries.isEmpty() : "currentEntry: " + currentEntry + " - telEntries.size()==" + eMailEntries.size();
		return result;
	}

	public int getNumberOfEntries() {
		return eMailEntries.size();
	}

	public boolean hasEntries() {
		return !eMailEntries.isEmpty();
	}

	/**
	 * Returns true if there is no entry at all or if all entries return
	 * {@code isEMailSet() == false}, false otherwise.
	 *
	 * @return true if there is no entry at all or if all entries return
	 * {@code isEMailSet() == false}, false otherwise
	 */
	public boolean isEmpty() {
		boolean isNumberSet = false;
		for (Entry<String> entry : eMailEntries) {
			isNumberSet = entry.isValueSet();
			if (isNumberSet) {
				break;
			}
		}
		return !isNumberSet;
	}

	public void setEMail(String number) {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		currentEntry.setValue(number);
	}

	public String getEMail() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		return currentEntry.getValue();
	}

	public boolean isEMailSet() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		return currentEntry.isValueSet();
	}

	public void clearEMail() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		currentEntry.clearValue();
	}

	public void setTypeParameters(Set<TypeParameter> s) {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		currentEntry.setTypeParameters(s);
	}

	public void addType(TypeParameter type) {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		currentEntry.addType(type);
	}

	public void removeType(TypeParameter type) {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		currentEntry.removeType(type);
	}

	public Set<TypeParameter> getTypeParameters() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		return currentEntry.getTypeParameters();
	}

	public boolean isTypeSet(TypeParameter type) {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		return currentEntry.isTypeSet(type);
	}

	public boolean isTypesEmpty() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		return currentEntry.isTypesEmpty();
	}

	public void clearTypeParamters() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		currentEntry.clearTypes();
	}

	public void clearEntry() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		currentEntry.clear();
	}

	@Override
	public List<PropertyEntry> getPropertyEntries() {
		List<PropertyEntry> propertyEntries = new ArrayList<PropertyEntry>(eMailEntries.size());
		for (Entry<String> e : eMailEntries) {
			CollectionTools.addIfNonNull(propertyEntries, createEMailProperty(e));
		}

		return propertyEntries;
	}

	private EMailProperty createEMailProperty(Entry<String> entry) {
		if (!entry.isValueSet()) {
			return null;
		}
		EMailProperty.Builder builder = new EMailProperty.Builder(entry.getValue());
		builder.types(entry.getTypeParameters());
		return builder.build();
	}

	public void setFromPropertyEntries(List<EMailProperty> properties) {
		if (properties == null) {
			throw new NullPointerException();
		}

		int limit = Math.min(getNumberOfEntries(), properties.size());

		for (int i = 0; i < limit; i++) {
			EMailProperty tp = properties.get(i);
			selectEntry(i);
			setEMail(tp.getEMail());
			setTypeParameters(tp.getType());
		}
	}

	@Override
	public void clear() {
		for (int i = getNumberOfEntries() - 1; i >= 0; i--) {
			selectEntry(i);
			clearEntry();
		}
	}

	private class EntryListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
	}
}
