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

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Stefan Ganzer
 */
class Mapping {

	private static final Map<Property, Set<PropertyParameter>> propParamMap;
	private static final Map<Property, Set<TypeParameter>> propTypeMap;
	private static final Map<Property, List<ValueType>> propValueTypeMap;
	

	static {
		propParamMap = new EnumMap<Property, Set<PropertyParameter>>(Property.class);
		propParamMap.put(Property.FN, fnParamSet());
		propParamMap.put(Property.N, nParamSet());
		propParamMap.put(Property.NICKNAME, nicknameParamSet());
		propParamMap.put(Property.ADR, adrParamSet());
		propParamMap.put(Property.EMAIL, emailParamSet());
		propParamMap.put(Property.TEL, telParamSet());
		propParamMap.put(Property.NOTE, noteParamSet());
		propParamMap.put(Property.URL, urlParamSet());
		propParamMap.put(Property.BDAY, bdayParamSet());


		propTypeMap = new EnumMap<Property, Set<TypeParameter>>(Property.class);
		propTypeMap.put(Property.FN, fnTypeSet());
		propTypeMap.put(Property.NICKNAME, nicknameTypeSet());
		propTypeMap.put(Property.ADR, adrTypeSet());
		propTypeMap.put(Property.EMAIL, emailTypeSet());
		propTypeMap.put(Property.TEL, telTypeSet());
		propTypeMap.put(Property.NOTE, noteTypeSet());
		propTypeMap.put(Property.URL, homeWorkTypeSet());
		
		propValueTypeMap = new EnumMap<Property, List<ValueType>>(Property.class);
		

		assert typeIsSet();
	}

	private static Set<PropertyParameter> fnParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.TYPE,
				PropertyParameter.LANGUAGE,
				PropertyParameter.ALTID,
				PropertyParameter.PREF);
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

	private static Set<PropertyParameter> telParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.TYPE,
				PropertyParameter.PREF,
				PropertyParameter.ALTID);
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

	private static Set<PropertyParameter> bdayParamSet() {
		return EnumSet.of(
				PropertyParameter.VALUE,
				PropertyParameter.ALTID);
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

	private static Set<TypeParameter> homeWorkTypeSet() {
		return EnumSet.of(TypeParameter.HOME, TypeParameter.WORK);
	}

	static boolean isParamAllowedWithProp(Property prop, PropertyParameter param) {
		Set<PropertyParameter> set = propParamMap.get(prop);
		if (set == null) {
			return false;
		} else {
			return set.contains(param);
		}
	}

	static boolean isParamAllowedWithPropAndValueType(Property prop, ValueType vt, PropertyParameter param){
		// TODO the actual code
		return false;
	}
	
	static boolean isTypeAllowedWithProp(Property prop, TypeParameter type) {
		Set<TypeParameter> set = propTypeMap.get(prop);
		if (set == null) {
			return false;
		} else {
			return set.contains(type);
		}
	}

	static boolean isDefaultValueTypeForProp(Property prop, ValueType type){
		List<ValueType> l = propValueTypeMap.get(prop);
		// ValueType is an enum, so comparison by '==' is ok
		return l.get(0) == type;
	}
	
	/**
	 * Returns true if for all Property that contain the type-parameter there is
	 * an non-empty entry in the propTypeMap, false otherwise.
	 *
	 * @return
	 */
	private static boolean typeIsSet() {
		for (Map.Entry<Property, Set<PropertyParameter>> entry : propParamMap.entrySet()) {
			Set<PropertyParameter> propParamSet = entry.getValue();
			if (propParamSet != null) {
				if (propParamSet.contains(PropertyParameter.TYPE)) {
					Set<TypeParameter> typeSet = propTypeMap.get(entry.getKey());
					return typeSet != null && !typeSet.isEmpty();
				}
			}
		}
		return false;
	}
}
