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
import java.util.Set;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.TypeParameter;

/**
 *
 * @param <T>
 *
 * @author Stefan Ganzer
 */
public class Entry<T> {

	private final String propertyName;
	private final TypesModel types;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private T value;

	public static class EntryFactory<T> {

		private final String propertyName;
		private final Property property;
		private final PropertyChangeListener listener;

		public EntryFactory(Property p, String propertyName, PropertyChangeListener listener) {
			if(p == null){
				throw new NullPointerException();
			}
			if(propertyName == null){
				throw new NullPointerException();
			}
			if(listener == null){
				throw new NullPointerException();
			}
			this.property = p;
			this.propertyName = propertyName;
			this.listener = listener;
		}

		public EntryFactory(Property p, PropertyChangeListener listener) {
			if(p == null){
				throw new NullPointerException();
			}
			if(listener == null){
				throw new NullPointerException();
			}
			this.property = p;
			this.propertyName = p.toString();
			this.listener = listener;
		}

		public Entry<T> newEntry() {
			return new Entry<T>(property, propertyName, listener);
		}
	}

	private Entry(Property p, String propertyName, PropertyChangeListener listener) {
		if (p == null) {
			throw new NullPointerException();
		}
		if (propertyName == null) {
			throw new NullPointerException();
		}
		if (listener == null) {
			throw new NullPointerException();
		}
		this.propertyName = propertyName;
		types = new TypesModel(p);
		types.addPropertyChangeListener(new TypesListener());
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void setValue(T value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setValuePrivate(value);
	}

	public T getValue() {
		if (!isValueSet()) {
			throw new IllegalStateException();
		}
		return value;
	}

	public boolean isValueSet() {
		return value != null;
	}

	public void clearValue() {
		setValuePrivate(null);
	}

	private void setValuePrivate(T value) {
		T oldValue = this.value;
		this.value = value;
		pcs.firePropertyChange(propertyName, oldValue, value);
	}

	public void setTypeParameters(Set<TypeParameter> s) {
		types.setTypeParameters(s);
	}

	public void addType(TypeParameter type) {
		types.addType(type);
	}

	public void removeType(TypeParameter type) {
		types.removeType(type);
	}

	public Set<TypeParameter> getTypeParameters() {
		return types.getTypeParameters();
	}

	public boolean isTypeSet(TypeParameter type) {
		return types.isTypeSet(type);
	}

	public boolean isTypesEmpty() {
		return types.isTypesEmpty();
	}

	public void clearTypes() {
		types.clearTypes();
	}

	public void clear() {
		clearValue();
		clearTypes();
	}

	private class TypesListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			pcs.firePropertyChange(evt);
		}
	}
}
