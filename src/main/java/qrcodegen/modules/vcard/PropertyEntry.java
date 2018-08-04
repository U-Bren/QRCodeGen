/*
 Copyright 2012, 2013 Stefan Ganzer

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

import java.io.Serializable;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO Implement equals, hashcode and comparable.
// An efficient implementation could calculate the hashcode lazily or at creation time,
// and compare hashcode before performing a per-element comparison.
/**
 * This class is designed only for subclassing in this package, so no public
 * constructors are available.
 *
 * @author Stefan Ganzer
 */
public abstract class PropertyEntry {

	private static final Logger LOGGER = Logger.getLogger(PropertyEntry.class.getPackage().getName());
	private static final String VCARD_NEWLINE = "\r\n";
	private static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final String PROPERTY_DELIMITER = ":";
	private static final int FOLDING_LENGTH = 75;
	private static final Comparator<? extends PropertyEntry> DEFAULT_COMPARATOR = new PropertyComparator<PropertyEntry>();
	private final Property property;
	/* The parameters */
	/** The altID parameter of this entry. May be null. */
	private final VCardParameterValue altID;
	/** The label parameter. Is only used with an adr-property. May be null. */
	private final VCardParameterValue label;
	/** The preference parameter. May be null. */
	private final Pref pref;
	/** The type parameter of this valueType. Mustn't be null. */
	private final Type type;
	/** The valueType parameter. May be null. */
	private final ValueType valueParameter;

	public static abstract class Builder {

		private final Property property;
		private ValueType valueType;

		/**
		 * This builder class is designed only for subclassing in this package,
		 * so no public constructors are available.
		 *
		 * @param property
		 */
		Builder(Property property) {
			this.property = property;
		}

		/**
		 * Sets the value type (data type) of this properties' data.
		 *
		 * @param value the value type of this properties' data
		 *
		 * @return this Builder instance
		 */
		public Builder valueType(ValueType value) {
			this.valueType = value;
			return this;
		}

		String getAltID() {
			return null;
		}

		String getLabel() {
			return null;
		}

		// We use an Integer object so we can tell whether this
		// value has been set or not.
		Integer getPref() {
			return null;
		}

		ListType getListType() {
			return null;
		}

		Set<TypeParameter> getType() {
			return EnumSet.noneOf(TypeParameter.class);
		}

		ValueType getValueParameter() {
			return valueType;
		}

		/**
		 * Returns a new PropertyEntry instance.
		 *
		 * @return
		 *
		 * @throws IllegalStateException if any invariant is not met
		 */
		public abstract PropertyEntry build();
	}

	public static abstract class Builder2 extends Builder {

		private String altID;
		private Integer pref;
		private final Set<TypeParameter> type = EnumSet.noneOf(TypeParameter.class);
		private ListType listType;

		Builder2(Property property) {
			super(property);
		}

		/**
		 * Adds a type parameter (home, work,...). The allowed type parameters
		 * depend on the property.
		 *
		 * @param type
		 *
		 * @return
		 */
		public Builder2 type(TypeParameter type) {
			this.type.add(type);
			return this;
		}

		/**
		 * Adds a set of type parameters (home, work,...). The allowed type
		 * parameters depend on the property.
		 *
		 * @param types
		 *
		 * @return
		 */
		public Builder2 types(Set<TypeParameter> types) {
			this.type.addAll(types);
			return this;
		}

		public Builder2 altID(String value) {
			this.altID = value;
			return this;
		}

		public Builder2 pref(int value) {
			this.pref = value;
			return this;
		}

		public Builder2 listType(ListType tl) {
			this.listType = tl;
			return this;
		}

		@Override
		String getAltID() {
			return altID;
		}

		@Override
		Integer getPref() {
			return pref;
		}

		@Override
		Set<TypeParameter> getType() {
			return type;
		}

		@Override
		ListType getListType() {
			return listType;
		}
	}

	/**
	 * Constructs a new PropertyEntry instance.
	 *
	 * @param builder
	 */
	PropertyEntry(Builder builder) {
		// Obligatory fields
		property = builder.property;
		if (property == null) {
			throw new NullPointerException();
		}

		// Optional fields
		// This leaks internal information when an exception is throw - maybe check the
		// values returned by the getXXX-methods before passing them on.
		String tempAltID = builder.getAltID();
		altID = tempAltID == null ? null : new VCardParameterValue(tempAltID);

		String tempLabel = builder.getLabel();
		label = tempLabel == null ? null : new VCardParameterValue(tempLabel);

		Integer tempPref = builder.getPref();
		pref = tempPref == null ? null : new Pref(tempPref);

		Set<TypeParameter> tempType = builder.getType();
		if (tempType == null) {
			throw new NullPointerException();
		}
		type = new Type(tempType, builder.getListType());

		valueParameter = builder.getValueParameter();

		// check all parameters if they are allowed for this property
		for (TypeParameter t : type.getType()) {
			if (!property.isTypeParameterAllowed(t)) {
				throw new IllegalStateException(t.toString() + " is not allowed with " + property);
			}
		}
	}

	boolean isAlternativeRepresentationOf(PropertyEntry other) {
		//   Two property instances are considered alternative representations of
		//   the same logical property if and only if their names as well as the
		//   value of their ALTID parameters are identical.  Property instances
		//   without the ALTID parameter MUST NOT be considered an alternative
		//   representation of any other property instance.
		// [source: https://tools.ietf.org/html/rfc6350#section-5.4]
		// two entries are equal only if they have the same property name
		// and the same non-empty altID
		if (other == null) {
			return false;
		} else if (this.property != other.property) {
			return false;
		} else if (this.altID == null || this.altID.getOriginalValue().isEmpty()) {
			return false;
		} else if (this.altID.equals(other.altID)) {
			return true;
		} else {
			return false;
		}
	}

	public Property getProperty() {
		return property;
	}

	VCardParameterValue getAltID() {
		return altID;
	}

	/**
	 * Returns true if this PropertyEntry has a label, false otherwise.
	 *
	 * @return true if this PropertyEntry has a label, false otherwise
	 */
	public boolean hasLabel() {
		return label != null;
	}

	/**
	 * Returns the original text of the label parameter.
	 *
	 * @return the original text of the label parameter
	 *
	 * @throws IllegalStateException if this PropertyEntry has no label
	 * @see #hasLabel()
	 */
	public String getLabel() {
		if (!hasLabel()) {
			throw new IllegalStateException();
		}
		return label.getOriginalValue();
	}

	public boolean isPrefSet() {
		return pref != null;
	}

	/**
	 * Returns the value of the PREF parameter.
	 *
	 * @return the value of the PREF parameter, an int {@code [1...100]}
	 *
	 * @throws IllegalStateException if the PREF parameter is not set
	 * @see #isPrefSet()
	 */
	public int getPref() {
		if (!isPrefSet()) {
			throw new IllegalStateException();
		}
		return pref.getValue();
	}

	ValueType getValueTypeParameter() {
		return valueParameter;
	}

	public Set<TypeParameter> getType() {
		return type.getType();
	}

	abstract VCardValue getValue();

	/**
	 * Returns this properties value as string.
	 *
	 * @return this properties value as string, without a trailing line break.
	 */
	abstract String getValueAsString();

	/**
	 * Returns this PropertyEntry as CRLF delimited string.
	 *
	 * @return this PropertyEntry as CRLF delimited string
	 */
	public String asString() {
		StringBuilder sb = new StringBuilder();
		sb.append(property.toString());
		if (!property.isDefaultValueType(valueParameter)) {
			appendIfValueNonNullNonEmpty(sb, PropertyParameter.VALUE, valueParameter);
		}
		appendIfValueNonNullNonEmpty(sb, PropertyParameter.ALTID, altID);
		appendIfValueNonNullNonEmpty(sb, PropertyParameter.PREF, pref);
		sb.append(type.getValueAsString());
		appendIfValueNonNullNonEmpty(sb, PropertyParameter.LABEL, label);
		sb.append(PROPERTY_DELIMITER);
		sb.append(getValueAsString());
		sb.append(VCARD_NEWLINE);
		try {
			return VCardTools.foldContent(UTF_8, sb, FOLDING_LENGTH);
		} catch (CharacterCodingException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Returns a Comparator that compares two PropertyEntry instances regarding
	 * their PREF parameter.
	 *
	 * @param <T>
	 *
	 * @return a Comparator that compares two PropertyEntry instances regarding
	 * their PREF parameter
	 */
	/*
	 * The cast is safe because DEFAULT_COMPARATOR is stateless and its type
	 * parameter is of type <? extends PropertyEntry> so it's safe to share one
	 * instance across all specialisations of PropertyEntry.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends PropertyEntry> Comparator<T> prefComparator() {
		return (Comparator<T>) DEFAULT_COMPARATOR;
	}

	private void appendIfValueNonNullNonEmpty(StringBuilder sb, Object parameter, VCardValue v) {
		assert sb != null;
		assert parameter != null;

		if (v == null || v.elements() == 0) {
			return;
		}
		sb.append(";");
		sb.append(parameter).append("=");
		sb.append(v.getValueAsString());
	}

	private static class Type implements VCardValue {

		private static final ListType DEFAULT_TYPE_LIST = ListType.VALUE_LIST;
		private final Set<TypeParameter> parameter;
		private final ListType listType;

		Type(Set<TypeParameter> parameter) {
			this(parameter, DEFAULT_TYPE_LIST);
		}

		Type(Set<TypeParameter> parameter, ListType listType) {
			if (parameter == null) {
				throw new NullPointerException();
			}
			this.parameter = EnumSet.copyOf(parameter);
			this.listType = listType == null ? DEFAULT_TYPE_LIST : listType;
		}

		@Override
		public String getValueAsString() {
			return listType.asString(parameter);
		}

		@Override
		public int elements() {
			return parameter.size();
		}

		Set<TypeParameter> getType() {
			return EnumSet.copyOf(parameter);
		}
	}

	/**
	 * Immutable class encapsulating the PREF property parameter.
	 *
	 * Instances of this class can be freely shared.
	 */
	private static final class Pref implements VCardValue, Comparable<Pref> {

		private final int value;

		Pref(int value) {
			if (value < 1 || value > 100) {
				throw new IllegalArgumentException("Value must between 1 and 100, but is: " + value);
			}
			this.value = value;
		}

		int getValue() {
			return value;
		}

		@Override
		public String getValueAsString() {
			return Integer.toString(value);
		}

		@Override
		public int elements() {
			return 1;
		}

		@Override
		public int compareTo(Pref pref) {
			if (pref == null) {
				throw new NullPointerException();
			}
			if (this == pref) {
				return 0;
			}
			if (this.value < pref.value) {
				return -1;
			} else if (this.value > pref.value) {
				return +1;
			} else {
				return 0;
			}
		}

		@Override
		public int hashCode() {
			return 31 * 17 + value;

		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Pref)) {
				return false;
			}
			final Pref otherPref = (Pref) obj;
			if (this.value != otherPref.value) {
				return false;
			}
			return true;
		}
	}

	private static final class PropertyComparator<T extends PropertyEntry> implements Comparator<T>, Serializable {

		private static final long serialVersionUID = 1L;

		@Override
		public int compare(T o1, T o2) {
			int pref1 = o1.isPrefSet() ? o1.getPref() : Integer.MAX_VALUE;
			int pref2 = o2.isPrefSet() ? o2.getPref() : Integer.MAX_VALUE;
			if (pref1 == pref2) {
				return 0;
			} else if (pref1 > pref2) {
				return +1;
			} else {
				return -1;
			}
		}
	};
}
