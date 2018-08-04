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

import java.util.EnumSet;
import java.util.Set;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.TypeParameter;

/**
 *
 * @author Stefan Ganzer
 */
public class TypesModel extends AbstractModel {

	public static final String TYPE_PARAMETER = "TypeParameter"; //NOI18N
	private final Set<TypeParameter> types = EnumSet.noneOf(TypeParameter.class);
	private final Property property;

	public TypesModel(Property property) {
		if (property == null) {
			throw new NullPointerException();
		}
		this.property = property;

	}

	/**
	 * Sets the type parameters to those contained in the given set.
	 *
	 * This method removes all type parameters that are not in the given set,
	 * and sets those of the given set instead.
	 *
	 * @param s a set of type parameters
	 *
	 * @throws NullPointerException if the given set is null
	 */
	public void setTypeParameters(Set<TypeParameter> s) {
		if (s == null) {
			throw new NullPointerException();
		}
		EnumSet<TypeParameter> newTypeParameters = EnumSet.copyOf(s);
		if (types.equals(s)) {
			//nothing to do
		} else {
			types.clear();
			for (TypeParameter tp : newTypeParameters) {
				if (!property.isTypeParameterAllowed(tp)) {
					throw new IllegalArgumentException();
				}
				types.add(tp);
			}
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	/**
	 * Adds the given type parameter to this address. This method only fires an
	 * event if the given type has not been a part of this address prior to
	 * invoking this method.
	 *
	 * @param type
	 *
	 * @throws NullPointerException if type is null
	 */
	public void addType(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		if (!property.isTypeParameterAllowed(type)) {
			throw new IllegalArgumentException();
		}
		boolean changed = types.add(type);
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	/**
	 * Removes the given type parameter from this address, if present. This
	 * method only fires an event if the given type has been a part of this
	 * address.
	 *
	 * @param type
	 *
	 * @throws NullPointerException if type is null
	 */
	public void removeType(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		if (!property.isTypeParameterAllowed(type)) {
			throw new IllegalArgumentException();
		}
		boolean changed = types.remove(type);
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	/**
	 * Returns the type parameters of this address as set.
	 *
	 * @return a {@literal Set<TypeParameters>}. Returns an empty set of this
	 * address has no type parameters. The caller is free to modify the returned
	 * set without changing this address.
	 */
	public Set<TypeParameter> getTypeParameters() {
		return EnumSet.copyOf(types);
	}

	/**
	 * Clears all type parameters, if present. This doesn't change any other
	 * fields, though.
	 *
	 * @see #clear()
	 * @see #clearFields()
	 */
	public void clearTypes() {
		boolean changed = !types.isEmpty();
		types.clear();
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	/**
	 * Returns true if no type is set, false otherwise.
	 *
	 * @return true if no type is set, false otherwise
	 */
	public boolean isTypesEmpty() {
		return types.isEmpty();
	}

	/**
	 * Returns true if the given type is set, false otherwise.
	 *
	 * @param type the type parameter to check
	 *
	 * @return true if the given type is set, false otherwise
	 *
	 * @throws NullPointerException if the given type is null
	 */
	public boolean isTypeSet(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		return types.contains(type);
	}
}
