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
package qrcodegen.modules.vcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents an actual VCard property, like a name property (Property.N), an
 * address property (Property.ADR), or a note property (Property.NOTE). A
 * property can consist of zero or more entries, depending on the property. Two
 * instances of VCardProperty are equal if they have the same property. The
 * entries are not part of the comparison. This may change in a future version.
 *
 * @param <T> a class extending PropertyEntry
 *
 * @author Stefan Ganzer
 */
public final class VCardProperty<T extends PropertyEntry> implements Comparable<VCardProperty<? extends PropertyEntry>> {

	private final Property property;
	private final List<T> entries = new ArrayList<T>();

	public static <T extends PropertyEntry> VCardProperty<T> newInstance(Property p) {
		return new VCardProperty<T>(p);

	}

	private VCardProperty(Property p) {
		if (p == null) {
			throw new NullPointerException();
		}
		this.property = p;
	}

	/**
	 * Adds an entry to this VCardProperty.
	 *
	 * @param entry a PropertyEntry to add to this VCardProperty
	 *
	 * @return true if the entry was added successfully, false otherwise
	 */
	public boolean addEntry(T entry) {
		if (entry == null) {
			throw new NullPointerException();
		}
		if (entry.getProperty() != this.property) {
			throw new IllegalArgumentException("Entry must be of property " + property + ", but is " + entry.getProperty());
		}
		//   Two property instances are considered alternative representations of
		//   the same logical property if and only if their names as well as the
		//   value of their ALTID parameters are identical.  Property instances
		//   without the ALTID parameter MUST NOT be considered an alternative
		//   representation of any other property instance.
		// [source: https://tools.ietf.org/html/rfc6350#section-5.4]
		switch (property.getCardinality()) {
			case EXACTLY_ONE:
			// fall-through
			case AT_MOST_ONE:
				for (PropertyEntry e : entries) {
					if (!e.isAlternativeRepresentationOf(entry)) {
						return false;
						//throw new IllegalArgumentException("This propery may not contain more than one entry.");
					}
				}
				break;
			case AT_LEAST_ONE:
			// fall-through
			case ANY:
				break;
			default:
				throw new AssertionError(property.getCardinality());
		}
		boolean result = entries.add(entry);
		return result;
	}

	/**
	 * Removes an PropertyEntry from this VCardProperty.
	 *
	 * @param index the index of the PropertyEntry to remove
	 *
	 * @return the PropertyEntry that was removed from this VCardProperty
	 */
	public T removeEntry(int index) {
		return entries.remove(index);
	}

	/**
	 * Removes the given PropertyEntry from this VCardProperty. Returns true if
	 * this VCardProperty contained the specified element (or equivalently, if
	 * this VCardProperty changed as a result of the call).
	 *
	 * @param entry the PropertyEntry to remove
	 *
	 * @return
	 */
	public boolean deleteEntry(T entry) {
		return entries.remove(entry);
	}

	/**
	 * Returns all PropertyEntries that belong to this VCardProperty.
	 *
	 * @return
	 */
	public List<T> getEntries() {
		if (entries.isEmpty()) {
			return Collections.emptyList();
		} else {
			return new ArrayList<T>(entries);
		}
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (T entry : entries) {
			sb.append(entry.asString());
		}
		return sb.toString();
	}

	public String asString() {
		StringBuilder sb = new StringBuilder();
		for (T entry : entries) {
			sb.append(entry.asString());
		}
		return sb.toString();
	}

	public Property getProperty() {
		assert property != null;
		return property;
	}

	public boolean isValid() {
		boolean isValid;
		switch (property.getCardinality()) {
			case EXACTLY_ONE: {
				assert entriesCountingTowardsCardinality(entries) == 1;
				isValid = entries.size() >= 1;
				break;
			}
			case AT_MOST_ONE: {
				assert entriesCountingTowardsCardinality(entries) <= 1;
				isValid = true;
				break;
			}
			case AT_LEAST_ONE:
				isValid = entries.size() >= 1;
				break;
			case ANY:
				isValid = true;
				break;
			default:
				throw new AssertionError(property.getCardinality());
		}
		return isValid;
	}

	@Override
	public int compareTo(VCardProperty<? extends PropertyEntry> o) {
		return this.property.compareTo(o.property);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof VCardProperty)) {
			return false;
		}
		VCardProperty<?> otherProperty = (VCardProperty) other;
		return this.property == otherProperty.property;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31 * hash + this.property.hashCode();
		return hash;
	}

	private static <T extends PropertyEntry> int entriesCountingTowardsCardinality(List<T> entries) {
		//   Two property instances are considered alternative representations of
		//   the same logical property if and only if their names as well as the
		//   value of their ALTID parameters are identical.  Property instances
		//   without the ALTID parameter MUST NOT be considered an alternative
		//   representation of any other property instance.
		// [source: https://tools.ietf.org/html/rfc6350#section-5.4]
		int entriesWOAltID = 0;
		Set<VCardParameterValue> discriminableAltIDs = new HashSet<VCardParameterValue>();
		for (T e : entries) {
			VCardParameterValue altID = e.getAltID();
			if (altID == null) {
				entriesWOAltID = entriesWOAltID + 1;
			} else {
				discriminableAltIDs.add(e.getAltID());
			}
		}
		return entriesWOAltID + discriminableAltIDs.size();
	}
}
