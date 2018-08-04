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
package qrcodegen.modules.vcard.reader;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import qrcodegen.modules.vcard.Calscale;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.PropertyParameter;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcard.VCardTools;
import qrcodegen.modules.vcard.ValueType;

/**
 * This class parses the parameter section of a vcard.
 *
 * Only parameters allowed for the given property and the defined or default
 * value type (VALUE=...) are considered to be valid. The input must be
 * non-null, but may be an empty string. Unknown parameters are ignored, illegal
 * parameters or values throw an ParseException.
 *
 * @author Stefan Ganzer
 */
class ParameterParser {

	private static final Locale ENGLISH = Locale.ENGLISH;
	private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\G;(VALUE|[-a-zA-Z]++)=((?:\"[^\"]*+\"|[^\";:]*+)*)");
	private static final Pattern VALUE_PATTERN = Pattern.compile("\"([^\"]*)\"|([^\",]+)");
	private static final Logger LOGGER = Logger.getLogger(ParameterParser.class.getPackage().getName());
	private final Matcher matcher = PARAMETER_PATTERN.matcher("");
	private final Matcher valueMatcher = VALUE_PATTERN.matcher("");
	private final Map<PropertyParameter, List<ValueHolder>> illegalParameters;
	private final Map<String, List<ValueHolder>> unknownParameters;
	private final Set<TypeParameter> illegalTypeParameters = EnumSet.noneOf(TypeParameter.class);
	private final Set<String> unknownTypeParameters = new HashSet<String>();
	private String unknownCalscaleValue;
	/** The input to parse. */
	private String input;
	/** The Property (N, ADR, BDAY, ...) the {@link #input} is checked against. */
	private Property property;
	/** True if parse() has been invoked successfully, false otherwise. */
	private boolean parsingDone = false;
	// The parameters this ParameterParser knows about
	private String altIDParameter;
	private Calscale calscaleParameter;
	private String labelParameter;
	private final List<String> pidParameter;
	private Integer prefParameter;
	private final List<String> sortAsParameter;
	private final Set<TypeParameter> typeParameter;
	private ValueType valueTypeParameter;
	private boolean parsingFailed = false;

//	static {
//		Handler handler = new ConsoleHandler();
//		handler.setLevel(Level.ALL);
//		LOGGER.addHandler(handler);
//		LOGGER.setLevel(Level.ALL);
//	}

	ParameterParser() {
		this(null, null);
	}

	/**
	 *
	 * @param property
	 * @param input
	 */
	ParameterParser(Property property, String input) {
		this.input = input;
		this.property = property;
		illegalParameters = new EnumMap<PropertyParameter, List<ValueHolder>>(PropertyParameter.class);
		unknownParameters = new HashMap<String, List<ValueHolder>>();
		pidParameter = new ArrayList<String>();
		typeParameter = EnumSet.noneOf(TypeParameter.class);
		sortAsParameter = new ArrayList<String>();
	}

	/**
	 *
	 * @param property
	 * @param input
	 */
	void reset(Property property, String input) {
		this.property = property;
		this.input = input;
		parsingDone = false;
		resetParameter();
		illegalParameters.clear();
		unknownParameters.clear();
		illegalTypeParameters.clear();
		unknownTypeParameters.clear();
		unknownCalscaleValue = null;
		parsingFailed = false;
	}

	private void resetParameter() {
		altIDParameter = null;
		calscaleParameter = null;
		labelParameter = null;
		pidParameter.clear();
		prefParameter = null;
		sortAsParameter.clear();
		typeParameter.clear();
		valueTypeParameter = null;
	}

	/**
	 * @throws IllegalStateException if {@code getProperty() == null} or
	 * {@code isDone() == true}
	 */
	void parse() throws VCardParseException {
		if (parsingDone) {
			throw new IllegalStateException();
		}
		if (property == null) {
			throw new IllegalStateException();
		}
		if (input == null || input.isEmpty()) {
			parsingDone = true;
			return;
		}
		assert input != null;
		matcher.reset(this.input);

		try {

			Map<PropertyParameter, List<ValueHolder>> ppvMap = getPPVMap();
			ValueType valueType = getValueType(property, ppvMap.remove(PropertyParameter.VALUE));
			if (valueType != null) {
				valueTypeParameter = valueType;
			} else {
				valueType = property.getDefaultValueType();
			}

			for (Map.Entry<PropertyParameter, List<ValueHolder>> entry : ppvMap.entrySet()) {
				PropertyParameter propertyParameter = entry.getKey();
				List<ValueHolder> values = entry.getValue();
				if (!property.isParamAllowedWithValueType(valueType, propertyParameter)) {
					assert illegalParameters.get(propertyParameter) == null;
					illegalParameters.put(propertyParameter, values);
				}
				Iterator<ValueHolder> iter = values.iterator();
				while (iter.hasNext()) {
					ValueHolder value = iter.next();
					switch (propertyParameter) {
						case TYPE: {
							parseTypeParameter(value);
							break;
						}
						case ALTID: {
							parseAltIDParameter(value);
							break;
						}
						case PREF: {
							parsePrefParameter(value);
							break;
						}
						case LABEL: {
							parseLabelParameter(value);
							break;
						}
						case CALSCALE: {
							parseCalscaleParameter(value);
							break;
						}
						default:
							LOGGER.log(Level.INFO, "Known valid parameter {0}, but no parser for it: {0}={1}", new Object[]{propertyParameter, value.getValue()});
							break;
					}
				}
			}
		} catch (VCardParseException vpe) {
			parsingFailed = true;
			throw new VCardParseException(vpe);
		} finally {
			parsingDone = true;
		}

	}

	/**
	 * Returns the input.
	 *
	 * @return the input. May return null.
	 */
	String getInput() {
		return input;
	}

	/**
	 * Returns true if an input is set, false otherwise.
	 *
	 * @return true if an input is set, false otherwise
	 */
	boolean isInputSet() {
		return input != null;
	}

	/**
	 * Returns the property.
	 *
	 * @return the property. May return null.
	 */
	Property getProperty() {
		return property;
	}

	/**
	 * Returns true if a property is set, false otherwise.
	 *
	 * @return true if a property is set, false otherwise
	 */
	boolean isPropertySet() {
		return property != null;
	}

	boolean isDone() {
		return parsingDone;
	}

	boolean isReset() {
		return !parsingDone;
	}

	/**
	 * Returns true if an exception was thrown during parsing, false otherwise.
	 *
	 * @return true if an exception was thrown during parsing, false otherwise
	 */
	boolean isParsingFailed() {
		return parsingFailed;
	}

	/**
	 * True if the parameter is valid and has no unknown components.
	 *
	 * @return
	 */
	boolean isCompletelyKnownParameter() {
		return isValidParameter() && !hasUnknownParameters() && !hasUnknownTypeParameter();
	}

	/**
	 * True if the parameter has no illegal components. The parameter can have
	 * unknown components, though, or be empty.
	 *
	 * @return if the parameter has no illegal components
	 */
	boolean isValidParameter() {
		return !parsingFailed && !hasIllegalParameters() && !hasIllegalTypeParameter();
	}

	boolean hasAltID() {
		return altIDParameter != null;
	}

	String getAltID() {
		return altIDParameter;
	}

	/**
	 * Returns true if there is a known calscale parameter.
	 *
	 * @return
	 */
	boolean hasKnownCalscaleParameter() {
		assert unknownCalscaleValue == null || calscaleParameter == null : unknownCalscaleValue + " " + calscaleParameter;
		return calscaleParameter != null;
	}

	/**
	 * Returns true if there is a known or unknown calscale parameter.
	 *
	 * @return
	 */
	boolean hasCalscaleParameter() {
		assert unknownCalscaleValue == null || calscaleParameter == null : unknownCalscaleValue + " " + calscaleParameter;
		return calscaleParameter != null || unknownCalscaleValue != null;
	}

	/**
	 * Returns true if the Calscale parameter is not set, or if the value is
	 * "gregorian", false otherwise.
	 *
	 * @return true if the Calscale parameter is not set, or if the value is
	 * "gregorian", false otherwise
	 */
	boolean isCalscaleGregorian() {
		assert unknownCalscaleValue == null || calscaleParameter == null : unknownCalscaleValue + " " + calscaleParameter;
		return unknownCalscaleValue == null
				&& (calscaleParameter == null || Calscale.GREGORIAN == calscaleParameter);
	}

	/**
	 * Returns the calscale parameter value.
	 *
	 * @return
	 *
	 * @throws IllegalStateException if there is no calscale parameter
	 */
	Calscale getCalscaleParameter() {
		assert unknownCalscaleValue == null || calscaleParameter == null : unknownCalscaleValue + " " + calscaleParameter;
		if (calscaleParameter == null) {
			throw new IllegalStateException();
		}
		return calscaleParameter;
	}

	boolean hasUnknownCalscaleParameter() {
		assert unknownCalscaleValue == null || calscaleParameter == null : unknownCalscaleValue + " " + calscaleParameter;
		return unknownCalscaleValue != null;
	}

	/**
	 *
	 * @return @throws IllegalStateException if there is no unknown calscale
	 * parameter
	 */
	String getUnknownCalscaleParameter() {
		assert unknownCalscaleValue == null || calscaleParameter == null : unknownCalscaleValue + " " + calscaleParameter;
		if (!hasUnknownCalscaleParameter()) {
			throw new IllegalStateException();
		}
		return unknownCalscaleValue;
	}

	boolean hasPref() {
		return prefParameter != null;
	}

	boolean hasValueType() {
		return valueTypeParameter != null;
	}

	ValueType getValueType() {
		return valueTypeParameter;
	}

	boolean hasTypeParameter() {
		return !typeParameter.isEmpty();
	}

	/**
	 * Returns the known type parameters this parser recognized. Returns only
	 * the type parameters that are legal for the property entry.
	 *
	 * Example: 'TYPE=work,fax,sms' would return TypeParameter.WORK and
	 * TypeParameter.FAX for TelProperty.
	 *
	 * @return the known type parameters this parser recognized. Returns only
	 * the type parameters that are legal for the property entry.
	 */
	Set<TypeParameter> getTypeParameter() {
		return EnumSet.copyOf(typeParameter);
	}

	/**
	 * Returns true if this parser recognized an type parameter that is not
	 * allowed for the property entry.
	 *
	 * Example: 'TYPE=voice' for FNProperty. Illegal type parameters will lead
	 * to an {@code isValidParameters() == true}.
	 *
	 * @return true if this parser recognized an type parameter that is not
	 * allowed for the property entry
	 *
	 * @see #isValidParameter()
	 */
	boolean hasIllegalTypeParameter() {
		return !illegalTypeParameters.isEmpty();
	}

	/**
	 * Returns the illegal type parameters that this parser recognized.
	 *
	 * @return
	 */
	Set<TypeParameter> getIllegalTypeParameters() {
		return EnumSet.copyOf(illegalTypeParameters);
	}

	/**
	 * Returns true if this parser recognized an unknown type parameter.
	 *
	 * Example: 'TYPE=SMS'.
	 *
	 * Unknown type parameters will not lead to an
	 * {@code isValidParameters() == false}.
	 *
	 * @return true if this parser recognized an unknown type parameter
	 *
	 * @see #isValidParameter()
	 */
	boolean hasUnknownTypeParameter() {
		return !unknownTypeParameters.isEmpty();
	}

	/**
	 * Returns the unknown type parameters this parser recognized.
	 *
	 * Example: 'TYPE="work,fax,sms" would return 'sms' for TelProperty.
	 *
	 * @return the unknown type parameters this parser recognized
	 */
	Set<String> getUnknownTypeParameters() {
		return new HashSet<String>(unknownTypeParameters);
	}

	boolean hasSortAsParameter() {
		return !sortAsParameter.isEmpty();
	}

	boolean hasLabelParameter() {
		return labelParameter != null;
	}

	String getLabelParameter() {
		return labelParameter;
	}

	/**
	 * Returns true if this parser recognized an unknown property parameter
	 * name.
	 *
	 * Example: 'TYP=work' instead of 'TYPE=work' on a TelProperty.
	 *
	 * Unknown type parameters will not lead to an
	 * {@code isValidParameters() == false}.
	 *
	 * @return true if this parser recognized an unknown parameter name
	 *
	 * @see #isValidParameter()
	 */
	boolean hasUnknownParameters() {
		return !unknownParameters.isEmpty();
	}

	Set<String> getUnknownParameters() {
		return new HashSet<String>(unknownParameters.keySet());
	}

	/**
	 * Returns true if this parser recognized a property parameter that is not
	 * allowed for the property entry.
	 *
	 * Example: LABEL parameter on a NProperty.
	 *
	 * Illegal parameters will lead to an {@code isValidParameter() == true}.
	 *
	 * @return true if this parser recognized a property parameter that is not
	 * allowed for the property entry
	 *
	 * @see #isValidParameter()
	 */
	boolean hasIllegalParameters() {
		return !illegalParameters.isEmpty();
	}

	Map<PropertyParameter, List<ValueHolder>> getIllegalParameters() {
		return new EnumMap<PropertyParameter, List<ValueHolder>>(illegalParameters);
	}

	private void parseLabelParameter(ValueHolder value) {
		assert value != null;
		labelParameter = VCardTools.deEscapeNewline(value.getValue());
	}

	private void parsePrefParameter(ValueHolder value) throws VCardParseException {
		assert value != null;
		String s = value.getValue();
		Integer pref;
		if (s.isEmpty()) {
			pref = null;
		} else {
			try {
				int i = Integer.parseInt(s);
				if (i < 1 || i > 100) {
					throw new VCardParseException(s);
				} else {
					pref = Integer.valueOf(i);
				}
			} catch (NumberFormatException nfe) {
				throw new VCardParseException(nfe);
			}
		}
		prefParameter = pref;
	}

	private void parseAltIDParameter(ValueHolder value) {
		assert value != null;
		String s = value.getValue();
		if (s.isEmpty()) {
			altIDParameter = null;
		} else {
			altIDParameter = s;
		}
	}

	private void parseTypeParameter(ValueHolder value) {
		assert value != null;
		String s = value.getValue();
		try {
			TypeParameter tp = TypeParameter.valueOf(s.toUpperCase(ENGLISH));
			if (property.isTypeParameterAllowed(tp)) {
				typeParameter.add(tp);
			} else {
				illegalTypeParameters.add(tp);
				LOGGER.log(Level.FINEST, "Illegal type parameter: {0}", tp);
			}
		} catch (IllegalArgumentException iae) {
			unknownTypeParameters.add(s);
			LOGGER.log(Level.FINEST, "Unknown type parameter: {0}", s);
		}
	}

	private void parseCalscaleParameter(ValueHolder value) {
		assert value != null;
		String s = value.getValue();
		if (s.isEmpty()) {
			calscaleParameter = null;
		} else {
			try {
				calscaleParameter = Calscale.fromString(s);
			} catch (IllegalArgumentException iae) {
				LOGGER.log(Level.FINEST, "Unknown calscale value: {0}", s);
				unknownCalscaleValue = s;
			}
		}
	}

	private static ValueType getValueType(Property property, List<ValueHolder> type) throws VCardParseException {
		assert property != null;
		ValueType result;
		if (type == null || type.isEmpty()) {
			result = null;
		} else {
			if (type.size() > 1) {
				throw new VCardParseException();
			} else {
				ValueType t = ValueType.fromString(type.get(0).getValue().toLowerCase(ENGLISH));
				if (property.isValueTypeAllowed(t)) {
					result = t;
				} else {
					LOGGER.log(Level.FINEST, "Value type {0} not allowed for {1}", new Object[]{t, property});
					throw new VCardParseException();
				}
			}
		}
		return result;
	}

	private Map<PropertyParameter, List<ValueHolder>> getPPVMap() {
		Map<PropertyParameter, List<ValueHolder>> result = new EnumMap<PropertyParameter, List<ValueHolder>>(PropertyParameter.class);

		while (matcher.find()) {
			String parameterName = matcher.group(1).toUpperCase(ENGLISH);
			String parameterValues = matcher.group(2);

			valueMatcher.reset(parameterValues);
			while (valueMatcher.find()) {
				String qSaveValue = valueMatcher.group(1);
				String saveValue = valueMatcher.group(2);

				ValueHolder nonNullValue;
				if (qSaveValue != null) {
					assert saveValue == null : saveValue;
					nonNullValue = new ValueHolder(qSaveValue, true);
				} else {
					assert saveValue != null;
					nonNullValue = new ValueHolder(saveValue, false);
				}
				assert nonNullValue != null;

				try {
					PropertyParameter propertyParameter = PropertyParameter.valueOf(parameterName);
					addToMap(result, propertyParameter, nonNullValue);
				} catch (IllegalArgumentException iae) {
					LOGGER.log(Level.FINEST, "Unknown parameter: {0}={1} ({2})", new String[]{parameterName, nonNullValue.getValue(), Boolean.valueOf(nonNullValue.isInDQuotes()).toString()});
					addToMap(unknownParameters, parameterName, nonNullValue);
				}
			}
		}
		return result;
	}

	private static <K, V> void addToMap(Map<K, List<V>> map, K key, V value) {
		List<V> values = map.get(key);
		if (values == null) {
			values = new ArrayList<V>(3);
		}
		values.add(value);
		map.put(key, values);
	}

	static class ValueHolder {

		private final String value;
		private final boolean isInDQuotes;

		ValueHolder(String value, boolean isInDQuotes) {
			assert value != null;
			this.value = value;
			this.isInDQuotes = isInDQuotes;
		}

		String getValue() {
			return value;
		}

		boolean isInDQuotes() {
			return isInDQuotes;
		}
	}
}
