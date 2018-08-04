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
public abstract class VCardAbstractModel<T,V extends PropertyEntry> extends AbstractModel implements VCardPropertyProvider {

	public static final String ENTRY_SELECTED = "EntrySelected";
	public static final String ENTRY_ADDED = "EntryAdded";
	public static final String ENTRY_REMOVED = "EntryRemoved";
	public static final String TYPE_PARAMETER = TypesModel.TYPE_PARAMETER;
	public final String element_name;
	private static final int MIN_ENTRIES = 0;
	private static final int MAX_ENTRIES = 10;
	private final PropertyChangeListener entryListener = new EntryListener();
	private final Entry.EntryFactory<T> entryFactory;
	private final int minEntries;
	private final int maxEntries;
	private List<Entry<T>> entries;// = new ArrayList<Entry<T>>(3);
	private Entry<T> currentEntry = null;

//	public VCardAbstractModel() {
//		this(MIN_ENTRIES, MAX_ENTRIES, 0);
//	}
//
//	public VCardAbstractModel(int min, int max) {
//		this(min, max, 0);
//	}
//
//	public VCardAbstractModel(int numberOfEntries) {
//		this(MIN_ENTRIES, MAX_ENTRIES, numberOfEntries);
//	}

	public VCardAbstractModel(Property p, int min, int max, int numberOfEntries) {
		if(p == null){
			throw new NullPointerException();
		}
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
		element_name = p.toString();
		entries = new ArrayList<Entry<T>>(numberOfEntries);
		
		entryFactory = new Entry.EntryFactory<T>(p, element_name, entryListener);

		for (int i = 0; i < numberOfEntries; i++) {
			entries.add(entryFactory.newEntry());
		}
		if (numberOfEntries > 0) {
			currentEntry = entries.get(0);
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
		int oldSize = entries.size();
		entries.add(entryFactory.newEntry());
		int newSize = entries.size();
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
		int index = entries.indexOf(currentEntry);
		int oldSize = entries.size();
		currentEntry.removePropertyChangeListener(entryListener);
		entries.remove(currentEntry);
		int newSize = entries.size();
		firePropertyChange(ENTRY_REMOVED, oldSize, newSize);
		int newIndex = index < entries.size() ? index : entries.size() - 1;
		selectEntry(newIndex);
	}

	public boolean canAddEntry() {
		return entries.size() < maxEntries;
	}

	public boolean canRemoveEntry() {
		return entries.size() > minEntries;
	}

	public void selectEntry(int index) {
		if (index < 0 || index >= entries.size()) {
			throw new ExtendedIndexOutOfBoundsException(0, entries.size() - 1, index);
		}
		int oldIndex = currentEntry == null ? -1 : entries.indexOf(currentEntry);
		currentEntry = entries.get(index);
		firePropertyChange(ENTRY_SELECTED, oldIndex, index);
	}

	public int getSelectedEntry() {
		if (!hasEntries()) {
			throw new IllegalStateException();
		}
		return entries.indexOf(currentEntry);
	}

	public boolean isEntrySelected() {
		boolean result = currentEntry != null;
		assert result != entries.isEmpty() : "currentEntry: " + currentEntry + " - telEntries.size()==" + entries.size();
		return result;
	}

	public int getNumberOfEntries() {
		return entries.size();
	}

	public boolean hasEntries() {
		return !entries.isEmpty();
	}

	/**
	 * Returns true if there is no entry at all or if all entries return
	 * {@code isValueSet() == false}, false otherwise.
	 *
	 * @return true if there is no entry at all or if all entries return
	 * {@code isValueSet() == false}, false otherwise
	 */
	public boolean isEmpty() {
		boolean isNumberSet = false;
		for (Entry<T> entry : entries) {
			isNumberSet = entry.isValueSet();
			if (isNumberSet) {
				break;
			}
		}
		return !isNumberSet;
	}

	public void setValue(T t) {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		currentEntry.setValue(t);
	}

	public T getValue() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		return currentEntry.getValue();
	}

	public boolean isValueSet() {
		if (currentEntry == null) {
			throw new IllegalStateException();
		}
		return currentEntry.isValueSet();
	}

	public void clearValue() {
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
		List<PropertyEntry> propertyEntries = new ArrayList<PropertyEntry>(entries.size());
		for (Entry<T> e : entries) {
			CollectionTools.addIfNonNull(propertyEntries, createProperty(e));
		}
		return propertyEntries;
	}
	
	abstract V createProperty(Entry<T> entry);

	public abstract void setFromPropertyEntries(List<V> properties);
	
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
