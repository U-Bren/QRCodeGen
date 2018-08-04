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
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcard.TelProperty;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcardgenpanel.VCardPropertyProvider;
import qrcodegen.modules.vcardgenpanel.model.Entry.EntryFactory;
import qrcodegen.tools.CollectionTools;
import qrcodegen.tools.ExtendedIndexOutOfBoundsException;
import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardTelModel extends AbstractModel implements VCardPropertyProvider {

	public static final String NUMBER_ELEMENT = "Number";
	public static final String ENTRY_SELECTED = "EntrySelected";
	public static final String ENTRY_ADDED = "EntryAdded";
	public static final String ENTRY_REMOVED = "EntryRemoved";
	public static final String TYPE_PARAMETER = TypesModel.TYPE_PARAMETER;
	private static final int MIN_ENTRIES = 0;
	private static final int MAX_ENTRIES = 10;
	private final PropertyChangeListener entryListener = new EntryListener();
	private final int minEntries;
	private final int maxEntries;
	private final Entry.EntryFactory<String> entryFactory = new Entry.EntryFactory<String>(Property.TEL, NUMBER_ELEMENT, entryListener);
	private List<Entry<String>> telEntries = new ArrayList<Entry<String>>(3);
	private Entry<String> currentEntry = null;

	public VCardTelModel() {
		this(MIN_ENTRIES, MAX_ENTRIES, 0);
	}

	public VCardTelModel(int min, int max) {
		this(min, max, 0);
	}

	public VCardTelModel(int numberOfEntries) {
		this(MIN_ENTRIES, MAX_ENTRIES, numberOfEntries);
	}

	public VCardTelModel(int min, int max, int numberOfEntries) {
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
			telEntries.add(entryFactory.newEntry());
		}
		if (numberOfEntries > 0) {
			currentEntry = telEntries.get(0);
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
		int oldSize = telEntries.size();
		telEntries.add(entryFactory.newEntry());
		int newSize = telEntries.size();
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
		int index = telEntries.indexOf(currentEntry);
		int oldSize = telEntries.size();
		currentEntry.removePropertyChangeListener(entryListener);
		telEntries.remove(currentEntry);
		int newSize = telEntries.size();
		firePropertyChange(ENTRY_REMOVED, oldSize, newSize);
		int newIndex = index < telEntries.size() ? index : telEntries.size() - 1;
		selectEntry(newIndex);
	}

	public boolean canAddEntry() {
		return telEntries.size() < maxEntries;
	}

	public boolean canRemoveEntry() {
		return telEntries.size() > minEntries;
	}

	public void selectEntry(int index) {
		if (index < 0 || index >= telEntries.size()) {
			throw new ExtendedIndexOutOfBoundsException(0, telEntries.size() - 1, index);
		}
		int oldIndex = currentEntry == null ? -1 : telEntries.indexOf(currentEntry);
		currentEntry = telEntries.get(index);
		firePropertyChange(ENTRY_SELECTED, oldIndex, index);
	}

	public int getSelectedEntry() {
		if (!hasEntries()) {
			throw new IllegalStateException();
		}
		return telEntries.indexOf(currentEntry);
	}

	public boolean isEntrySelected() {
		boolean result = currentEntry != null;
		assert result != telEntries.isEmpty() : "currentEntry: " + currentEntry + " - telEntries.size()==" + telEntries.size();
		return result;
	}

	public int getNumberOfEntries() {
		return telEntries.size();
	}

	public boolean hasEntries() {
		return !telEntries.isEmpty();
	}

	/**
	 * Returns true if there is no entry at all or if all entries return
	 * {@code isNumberSet() == false}, false otherwise.
	 *
	 * @return true if there is no entry at all or if all entries return
	 * {@code isNumberSet() == false}, false otherwise
	 */
	public boolean isEmpty() {
		boolean isNumberSet = false;
		for (Entry<String> entry : telEntries) {
			isNumberSet = entry.isValueSet();
			if (isNumberSet) {
				break;
			}
		}
		return !isNumberSet;
	}

	public void setNumber(String number) {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		currentEntry.setValue(number);
	}

	public String getNumber() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		return currentEntry.getValue();
	}

	public boolean isNumberSet() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		return currentEntry.isValueSet();
	}

	public void clearNumber() {
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
		List<PropertyEntry> propertyEntries = new ArrayList<PropertyEntry>(telEntries.size());
		for (Entry<String> e : telEntries) {
			CollectionTools.addIfNonNull(propertyEntries, createTelProperty(e));
		}

		return propertyEntries;
	}

	private TelProperty createTelProperty(Entry<String> entry) {
		if (!entry.isValueSet()) {
			return null;
		}
		TelProperty.Builder builder = new TelProperty.Builder(entry.getValue());
		builder.types(entry.getTypeParameters());
		return builder.build();
	}

	public void setFromPropertyEntries(List<TelProperty> properties) {
		if (properties == null) {
			throw new NullPointerException();
		}

		int limit = Math.min(getNumberOfEntries(), properties.size());

		for (int i = 0; i < limit; i++) {
			TelProperty tp = properties.get(i);
			selectEntry(i);
			if (tp.hasTextValue()) {
				setNumber(tp.getText());
			}
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
