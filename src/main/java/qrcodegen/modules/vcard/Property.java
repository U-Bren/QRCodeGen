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

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * An enum of the different properties a v4 VCard can have, like N (Name), ADR
 * (Address), and so on.
 *
 * @author Stefan Ganzer
 */
public enum Property {
	/*
	 * Cardinality,
	 * default ValueType,
	 * mapping ValueType / allowed property parameters,
	 * allowed type parameters
	 */
	// General properties

	BEGIN(Cardinality.EXACTLY_ONE, ValueType.TEXT, beginEndPropMap(), emptyTypeSet()),
	VERSION(Cardinality.EXACTLY_ONE, ValueType.TEXT, versionPropMap(), emptyTypeSet()),
	KIND(Cardinality.AT_MOST_ONE, ValueType.TEXT, kindPropMap(), emptyTypeSet()),
	// Identification properties
	FN(Cardinality.AT_LEAST_ONE, ValueType.TEXT, fnPropMap(), fnTypeSet()),
	N(Cardinality.AT_MOST_ONE, ValueType.TEXT, nPropMap(), emptyTypeSet()),
	NICKNAME(Cardinality.ANY, ValueType.TEXT, nicknamePropMap(), nicknameTypeSet()),
	BDAY(Cardinality.AT_MOST_ONE, ValueType.DATE_AND_OR_TIME, bdayPropMap(), emptyTypeSet()),
	// Delivery addressing properties
	ADR(Cardinality.ANY, ValueType.TEXT, adrPropMap(), adrTypeSet()),
	// Communications properties
	TEL(Cardinality.ANY, ValueType.TEXT, telPropMap(), telTypeSet()),
	EMAIL(Cardinality.ANY, ValueType.TEXT, emailPropMap(), emailTypeSet()),
	// Explanatory properties
	ORG(Cardinality.ANY, ValueType.TEXT, orgPropMap(), homeWorkTypeSet()),
	NOTE(Cardinality.ANY, ValueType.TEXT, notePropMap(), noteTypeSet()),
	URL(Cardinality.ANY, ValueType.URI, urlPropMap(), urlTypeSet()),
	END(Cardinality.EXACTLY_ONE, ValueType.TEXT, beginEndPropMap(), emptyTypeSet()),;
	private final Cardinality cardinality;
	private final Map<ValueType, Set<PropertyParameter>> map;
	private final Set<TypeParameter> set;
	private final ValueType defaultValueType;

	private Property(Cardinality c, ValueType defaultType, Map<ValueType, Set<PropertyParameter>> map, Set<TypeParameter> set) {
		this.cardinality = c;
		this.map = map;
		this.set = set;
		this.defaultValueType = defaultType;
	}

	/**
	 * Returns the cardinality of this property.
	 *
	 * @return the cardinality of this property
	 */
	public Cardinality getCardinality() {
		return cardinality;
	}

	/**
	 * Returns true if the given {@link ValueType} is allowed with this
	 * property, false otherwise.
	 *
	 * @param type a value type
	 *
	 * @return true if the given {@link ValueType} is allowed with this
	 * property, false otherwise
	 *
	 * @throws NullPointerException if type is null
	 */
	public boolean isValueTypeAllowed(ValueType type) {
		if (type == null) {
			throw new NullPointerException();
		}
		return map.containsKey(type);
	}

	/**
	 * Returns true if the given {@link TypeParameter} is allowed with this
	 * property, false otherwise
	 *
	 * @param type a type parameter
	 *
	 * @return true if the given {@link TypeParameter} is allowed with this
	 * property, false otherwise
	 *
	 * @throws NullPointerException if type is null
	 */
	public boolean isTypeParameterAllowed(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		return set.contains(type);
	}

	/**
	 * Returns true if the given {@link PropertyParameter} is allowed with the
	 * given {@link ValueType}, false otherwise.
	 *
	 * @param type a value type
	 * @param param a type parameter
	 *
	 * @return true if the given {@link PropertyParameter} is allowed with the
	 * given {@link ValueType}, false otherwise
	 *
	 * @throws NullPointerException if type or param is null
	 */
	public boolean isParamAllowedWithValueType(ValueType type, PropertyParameter param) {
		if (type == null) {
			throw new NullPointerException();
		}
		if (param == null) {
			throw new NullPointerException();
		}
		if (!isValueTypeAllowed(type)) {
			throw new IllegalArgumentException();
		}
		Set<PropertyParameter> set = map.get(type);
		return set.contains(param);
	}

	/**
	 * Returns true if the given {@link ValueType} is this properties default
	 * value type, false otherwise.
	 *
	 * @param type a value type
	 *
	 * @return true if the given {@link ValueType} is this properties default
	 * value type, false otherwise
	 */
	public boolean isDefaultValueType(ValueType type) {
		return defaultValueType == type;
	}

	/**
	 * Returns a set of this properties allowed {@link TypeParameter}s.
	 *
	 * @return a set of this properties allowed {@link TypeParameter}s. The
	 * client is free to modify the returned set without affecting this
	 * property.
	 */
	public Set<TypeParameter> allowedTypeParameters() {
		return EnumSet.copyOf(set);
	}

	/**
	 * Returns a set of this properties allowed {@link PropertyParameter}s.
	 *
	 * @param type a value type
	 *
	 * @return a set of this properties allowed {@link PropertyParameter}s. The
	 * client is free to modify the returned set without affecting this
	 * property.
	 */
	public Set<PropertyParameter> allowedPropertyParameters(ValueType type) {
		return EnumSet.copyOf(map.get(type));
	}

	/**
	 * Returns this properties default {@link ValueType}.
	 *
	 * @return this properties default {@link ValueType}. Never returns null.
	 */
	public ValueType getDefaultValueType() {
		assert defaultValueType != null;
		return defaultValueType;
	}

	/*
	 * xxxPropMaps
	 */
	private static Map<ValueType, Set<PropertyParameter>> emptyMap() {
		return Collections.emptyMap();
	}

	private static Map<ValueType, Set<PropertyParameter>> beginEndPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, EnumSet.noneOf(PropertyParameter.class));
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> fnPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, fnParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> nPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, nParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> bdayPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, bdayTextParamSet());
		map.put(ValueType.DATE_AND_OR_TIME, bdayDateAndOrTimeParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> adrPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, adrParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> kindPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, kindParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> nicknamePropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, nicknameParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> emailPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, emailParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> telPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, telTextParamSet());
		map.put(ValueType.URI, telUriParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> notePropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, noteParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> urlPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.URI, urlParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> orgPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, orgParamSet());
		return map;
	}

	private static Map<ValueType, Set<PropertyParameter>> versionPropMap() {
		Map<ValueType, Set<PropertyParameter>> map = new EnumMap<ValueType, Set<PropertyParameter>>(ValueType.class);
		map.put(ValueType.TEXT, versionParamSet());
		return map;
	}

	private static Set<PropertyParameter> fnParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.TYPE,
				PropertyParameter.LANGUAGE,
				PropertyParameter.ALTID,
				PropertyParameter.PREF);
	}

	private static Set<PropertyParameter> kindParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE);
	}

	private static Set<PropertyParameter> nParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.LANGUAGE,
				PropertyParameter.ALTID);
	}

	private static Set<PropertyParameter> nicknameParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.TYPE,
				PropertyParameter.LANGUAGE,
				PropertyParameter.ALTID,
				PropertyParameter.PREF);
	}

	private static Set<PropertyParameter> adrParamSet() {
		return EnumSet.of(
				PropertyParameter.LABEL,
				PropertyParameter.VALUE,
				PropertyParameter.LANGUAGE,
				PropertyParameter.ALTID,
				PropertyParameter.TYPE,
				PropertyParameter.PREF);
	}

	private static Set<PropertyParameter> emailParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.PREF,
				PropertyParameter.TYPE,
				PropertyParameter.ALTID);
	}

	private static Set<PropertyParameter> telTextParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.TYPE,
				PropertyParameter.PREF,
				PropertyParameter.ALTID);
	}

	private static Set<PropertyParameter> telUriParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.TYPE,
				PropertyParameter.PREF,
				PropertyParameter.ALTID //PropertyParameter.MEDIATYPE
				);
	}

	private static Set<PropertyParameter> noteParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.LANGUAGE,
				PropertyParameter.PREF,
				PropertyParameter.TYPE,
				PropertyParameter.ALTID);
	}

	private static Set<PropertyParameter> urlParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.PREF,
				PropertyParameter.TYPE,
				PropertyParameter.ALTID);
	}

	private static Set<PropertyParameter> orgParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.LANGUAGE,
				PropertyParameter.PREF,
				PropertyParameter.ALTID,
				PropertyParameter.TYPE);
	}

	private static Set<PropertyParameter> bdayTextParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.ALTID);
	}

	private static Set<PropertyParameter> bdayDateAndOrTimeParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.ALTID,
				PropertyParameter.CALSCALE);
	}

	private static Set<PropertyParameter> versionParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE);
	}

	private static Set<TypeParameter> emptyTypeSet() {
		return Collections.emptySet();
	}

	private static Set<TypeParameter> fnTypeSet() {
		return EnumSet.of(TypeParameter.HOME, TypeParameter.WORK);
	}

	private static Set<TypeParameter> nicknameTypeSet() {
		return EnumSet.of(TypeParameter.HOME, TypeParameter.WORK);
	}

	private static Set<TypeParameter> adrTypeSet() {
		return EnumSet.of(TypeParameter.HOME, TypeParameter.WORK);
	}

	private static Set<TypeParameter> emailTypeSet() {
		return EnumSet.of(TypeParameter.HOME, TypeParameter.WORK);
	}

	private static Set<TypeParameter> telTypeSet() {
		return EnumSet.of(TypeParameter.HOME, TypeParameter.WORK,
				TypeParameter.CELL, TypeParameter.FAX, TypeParameter.PAGER,
				TypeParameter.TEXT, TypeParameter.TEXTPHONE,
				TypeParameter.VIDEO, TypeParameter.VOICE);
	}

	private static Set<TypeParameter> noteTypeSet() {
		return EnumSet.of(TypeParameter.HOME, TypeParameter.WORK);
	}

	private static Set<TypeParameter> urlTypeSet() {
		return EnumSet.of(TypeParameter.HOME, TypeParameter.WORK);
	}

	private static Set<TypeParameter> homeWorkTypeSet() {
		return EnumSet.of(TypeParameter.HOME, TypeParameter.WORK);
	}
}
