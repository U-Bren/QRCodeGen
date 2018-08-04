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

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import qrcodegen.modules.vcard.Calscale;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcard.VCardTools;
import qrcodegen.modules.vcard.ValueType;

/**
 *
 * @author Stefan Ganzer
 */
public abstract class PropertyParser {

	static final Logger LOGGER = Logger.getLogger(PropertyParser.class.getPackage().getName());
	static final String EMPTY_STRING = "";
	private static final Pattern COMPONENT_SPLITTER = Pattern.compile("(?<!\\\\);");
	private static final Pattern VALUE_LIST_SPLITTER = Pattern.compile("(?<!\\\\),");
	private final Property property;
	private final ParameterParser paramParser;
	private String value;
	private String params;

	/**
	 * Constructs a new PropertyParser for the given property
	 *
	 * @param property
	 */
	PropertyParser(Property property) {
		if (property == null) {
			throw new NullPointerException();
		}
		this.property = property;
		this.paramParser = new ParameterParser(this.property, null);
	}

	/**
	 * Resets this PropertyParser.
	 *
	 * @param params the parameters to parse. May be null.
	 * @param value the value to parse. May be null.
	 */
	public void reset(String params, String value) {
		this.value = value;
		this.params = params;
		paramParser.reset(getProperty(), params);
//		isDone = paramParser.isDone();
	}

	/**
	 * Returns the value that is subject to parsing.
	 *
	 * @return the value. May return null.
	 */
	String getValue() {
		return value;
	}

	/**
	 * Returns the parameters that are subject to parsing.
	 *
	 * @return the parameters. May return null.
	 */
	String getParams() {
		return params;
	}

	void parse() {
		try {
			paramParser.parse();
			//		isDone = paramParser.isDone();
		} catch (VCardParseException ex) {
			LOGGER.log(Level.INFO, null, ex);
		}
	}

	Property getProperty() {
		assert property != null;
		return property;
	}

	abstract PropertyEntry getPropertyEntry();

	boolean isDone() {
		return paramParser.isDone();
//		return isDone;
	}

//	boolean isParamParsingDone(){
//		return paramParser.isDone();
//	}
//	
//	void setIsDone(boolean value){
//		isDone = value;
//	}
	//abstract boolean isParsingDone();
	/**
	 * True if the parameter has no illegal components. The parameter can have
	 * unknown components, though, or be empty.
	 *
	 * @return if the parameter has no illegal components
	 */
	boolean hasValidParameter() {
		return paramParser.isValidParameter();
	}

	boolean hasCompletelyKnownParameter() {
		return paramParser.isCompletelyKnownParameter();
	}

	boolean isCompletelyKnown() {
		return hasCompletelyKnownParameter();
	}

	boolean isValid() {
		return hasValidParameter();
	}

	boolean hasAltID() {
		return paramParser.hasAltID();
	}

	String getAltID() {
		return paramParser.getAltID();
	}

	/**
	 * Returns true if the property has a CALSCALE parameter, false otherwise.
	 *
	 * @return true if the property has a CALSCALE parameter, false otherwise
	 */
	boolean hasCalscaleParameter() {
		return paramParser.hasCalscaleParameter();
	}

	/**
	 * Returns true if the Calscale parameter is not set, or if the value is
	 * "gregorian", false otherwise.
	 *
	 * @return true if the Calscale parameter is not set, or if the value is
	 * "gregorian", false otherwise
	 */
	boolean isCalscaleGregorian() {
		return paramParser.isCalscaleGregorian();
	}

	Calscale getCalscaleParameter() {
		return paramParser.getCalscaleParameter();
	}

	/**
	 * Returns true if the property has a CALSCALE parameter whose value is
	 * unknown, false otherwise.
	 *
	 * @return true if the property has a CALSCALE parameter whose value is
	 * unknown, false otherwise
	 */
	boolean hasUnknownCalscaleParameter() {
		return paramParser.hasUnknownCalscaleParameter();
	}

	String getUnknownCalscaleParameter() {
		return paramParser.getUnknownCalscaleParameter();
	}

	boolean hasPref() {
		return paramParser.hasPref();
	}

	boolean hasValueType() {
		return paramParser.hasValueType();
	}

	ValueType getValueType() {
		return paramParser.getValueType();
	}

	boolean hasTypeParameter() {
		return paramParser.hasTypeParameter();
	}

	/**
	 * Returns the type parameters found by parsing the params-value.
	 *
	 * @return the type parameters found by parsing the params-value. Returns an
	 * empty set if the params-value was null or empty.
	 */
	Set<TypeParameter> getTypeParameter() {
		return paramParser.getTypeParameter();
	}

	boolean hasSortAsParameter() {
		return paramParser.hasSortAsParameter();
	}

	boolean hasLabelParameter() {
		return paramParser.hasLabelParameter();
	}

	String getLabelParameter() {
		return paramParser.getLabelParameter();
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
		return paramParser.hasUnknownParameters();
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
	boolean hasUnknownTypeParameters() {
		return paramParser.hasUnknownTypeParameter();
	}

	Set<String> getUnknownTypeParameters() {
		return paramParser.getUnknownTypeParameters();
	}

	Set<String> getUnknownParameters() {
		return paramParser.getUnknownParameters();
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
		return paramParser.hasIllegalParameters();
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
	boolean hasIllegalTypeParameters() {
		return paramParser.hasIllegalTypeParameter();
	}

	/**
	 *
	 * @param input
	 *
	 * @return
	 *
	 * @throws NullPointerException if input is null
	 */
	static String[] splitComponents(String input) {
		return COMPONENT_SPLITTER.split(input, -1);
	}

	/**
	 * Returns the content of a component list as an {@code String[]}.
	 *
	 * A component list consists of components separated by ";" semicolon.
	 *
	 * For instance: {@code  A;B;C} has three components, for which this method
	 * will return {@code String[] s = {"A", "B", "C"}}.
	 *
	 * {@code ;;;} has four components. The return array will be
	 * {@code String[] s = {"", "", "", ""}}
	 *
	 * @param input
	 *
	 * @return
	 */
	static String[] splitAndDeescapeComponentList(String input) {
		return VCardTools.deEscapeArray(COMPONENT_SPLITTER.split(input, -1));
	}

	static String[] splitAndDeescapeTextList(String input) {
		return VCardTools.deEscapeArray(splitValueList(input));
	}

	static String[] splitValueList(String input) {
		return VALUE_LIST_SPLITTER.split(input, -1);
	}
}
