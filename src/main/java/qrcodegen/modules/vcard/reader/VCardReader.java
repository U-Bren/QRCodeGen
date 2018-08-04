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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import qrcodegen.modules.vcard.FNProperty;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcard.VCard;
import qrcodegen.modules.vcard.VCardProperty;
import qrcodegen.modules.vcard.VCardTools;
import qrcodegen.tools.CollectionTools;
import qrcodegen.tools.StaticTools;

/**
 * Reads a single v4 VCard.
 *
 * @author Stefan Ganzer
 */
public class VCardReader {

	private static final Locale ENGLISH = Locale.ENGLISH;
	/** [group.]name(;param):valueCRLF */
	private static final Pattern MAIN_PATTERN = Pattern.compile("^(?:\\[([-a-zA-Z0-9]*+)\\.\\])?+([a-zA-Z]++)((?:;[-a-zA-Z]++=(?:(?:\"[^\"]*\"|[^\";:]+)*))*+):(.*)$", Pattern.CASE_INSENSITIVE);
	private static final Logger LOGGER = Logger.getLogger(VCardReader.class.getPackage().getName());
	private final Matcher mainMatcher = MAIN_PATTERN.matcher("");
	private final VCardParsingState newState = new NewState(this);
	private final VCardParsingState beginState = new BeginState(this);
	private final VCardParsingState versionState = new VersionState(this);
	private final VCardParsingState fnState = new FnState(this);
	private final VCardParsingState bodyState = new ContentState(this);
	private final VCardParsingState endState = new EndState(this);
	private final VCardParsingState validVCardState = new ValidVCardState(this);
	private final VCardParsingState malformedVCardState = new MalformedVCardState(this);
	private final Map<Property, VCardProperty<PropertyEntry>> mapOfVCardProperties = new EnumMap<Property, VCardProperty<PropertyEntry>>(Property.class);
	private final NPropertyParser nParser = new NPropertyParser();
	private final NicknamePropertyParser nicknameParser = new NicknamePropertyParser();
	private final BDayPropertyParser bdayParser = new BDayPropertyParser();
	private final AdrPropertyParser adrParser = new AdrPropertyParser();
	private final FnPropertyParser fnParser = new FnPropertyParser();
	private final EMailPropertyParser eMailParser = new EMailPropertyParser();
	private final TelPropertyParser telParser = new TelPropertyParser();
	private final NotePropertyParser noteParser = new NotePropertyParser();
	private final UrlPropertyParser urlParser = new UrlPropertyParser();
	private final OrgPropertyParser orgParser = new OrgPropertyParser();
//	private final KindPropertyParser kindParser = new KindPropertyParser();
	private final List<String> unknownProperties = new ArrayList<String>();
	private final Map<String, Set<String>> unknownTypeParameters = new HashMap<String, Set<String>>();
	private final Map<String, Set<String>> unknownParameters = new HashMap<String, Set<String>>();
	private VCardParsingState state = newState;
	private String malformedLine;
	private String input = null;
	private VCard vCard = null;

	/**
	 * Parses a VCard v4 and creates a VCard-object.
	 *
	 * @param vCard a VCard object as string
	 *
	 * @throws NullPointerException if the given vCard is null
	 *
	 */
	public VCardReader(String vCard) {
		if (vCard == null) {
			throw new NullPointerException();
		}
		input = vCard;
	}

	public void parseInput() {
		assert input != null;

		// We don't need to parse the input again if we already parsed the input once.
		if (!(state instanceof NewState)) {
			return;
		}

		malformedLine = null;
		unknownProperties.clear();

		String s = VCardTools.unfoldContent(input);

		// Parse the unfolded input
		BufferedReader br = null;
		try {
			br = new BufferedReader(new StringReader(s));
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				} else {
					parseLine(line);
				}
			}
		} catch (IOException ioe) {
			throw new AssertionError(ioe);
		} catch (VCardParseException pe) {
			// the client can obtain the state, doesn't need exceptions
			LOGGER.log(Level.FINEST, null, pe);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Unhandled Exception", e);
			throw new RuntimeException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					throw new AssertionError(ioe);
				}
			}
			state.endOfInput();
		}

		if (!(state instanceof ValidVCardState)) {
			LOGGER.log(Level.FINEST, "Not a valid vCard");
			//throw new VCardParseException();
			return;
		}
		/*
		 * Create a new VCard object from the parsed entries
		 */
		VCard.Builder builder = new VCard.Builder();
		for (Map.Entry<Property, VCardProperty<PropertyEntry>> entry : mapOfVCardProperties.entrySet()) {
			VCardProperty<PropertyEntry> vcardProperty = entry.getValue();
			List<PropertyEntry> propertyEntries = vcardProperty.getEntries();
			builder.propertyEntry(propertyEntries);
		}
		vCard = builder.build();
	}

	public boolean foundValidVCard() {
		return state instanceof ValidVCardState;
	}

	public VCard getVCard() {
		if (!foundValidVCard()) {
			throw new IllegalStateException(state.toString());
		}
		return vCard;
	}

	public String getMalformedLine() {
		return malformedLine;
	}

	public List<String> getUnknownProperties() {
		return new ArrayList<String>(unknownProperties);
	}

	public boolean hasUnknownProperties() {
		return !unknownProperties.isEmpty();
	}

	public boolean hasUnknownTypeParameters() {
		return !unknownTypeParameters.isEmpty();
	}

	public Map<String, Set<String>> getUnknownTypeParameters() {
		return CollectionTools.deepCopyMapOfSet(unknownTypeParameters);
	}

	/**
	 * Returns the input to parse.
	 *
	 * @return the input to parse
	 */
	public String getInput() {
		return input;
	}

	/**
	 * Parses one line of a VCard, and stores the property in an instance
	 * variable.
	 */
	private void parseLine(String line) throws VCardParseException {
		if (line.isEmpty()) {
			state.foundEmptyLine();
			return;
		}
		String group = null;
		String name = null;
		String params = null;
		String value = null;

		// [group.]name*(;param):valueCRLF
		mainMatcher.reset(line);
		if (mainMatcher.matches()) {
			group = mainMatcher.group(1);
			name = mainMatcher.group(2);
			params = mainMatcher.group(3);
			value = mainMatcher.group(4);
		} else {
			state.foundMalformedLine();
			malformedLine = line;
			LOGGER.log(Level.FINEST, "found malformed line: {0}", malformedLine);
			throw new VCardParseException(VCardTools.shorten(line, 80));
		}

		// group may be null
		// name may be null: (name)+
		assert params != null; // regex: (;ParameterName=ParameterValue)*
		assert value != null; // regex: (.*)

		LOGGER.log(Level.FINEST, "name: {0}", name);
		LOGGER.log(Level.FINEST, "params: {0}", params);
		LOGGER.log(Level.FINEST, "value: {0}", value);


		Property property;
		try {
			property = Property.valueOf(name.toUpperCase(ENGLISH));
		} catch (IllegalArgumentException iae) {
			state.foundUnknownProperty();
			unknownProperties.add(VCardTools.shorten(line, 80));
			return;
		}

		switch (property) {
			case BEGIN: {
				parseBeginProperty(group, params, value);
				break;
			}
			case VERSION: {
				parseVersionProperty(group, params, value);
				break;
			}
			/*
			 * Do parsing in the order in which the cards are written,
			 * so a card generated by parsing a card then writing it again will
			 * be equal to the original card.
			 * see Property
			 * see VCard#toString()
			 */
			case KIND: {
				//parseContentLine(line, params, value, kindParser);
				unknownProperties.add(VCardTools.shorten(line, 80));
				state.foundUnknownProperty();
				break;
			}
			case FN: {
				parseFnProperty(line, params, value);
				break;
			}
			case N: {
				parseContentLine(line, params, value, nParser);
				break;
			}
			case NICKNAME: {
				parseContentLine(line, params, value, nicknameParser);
				break;
			}
			case BDAY: {
				parseDateProperty(line, params, value, bdayParser);
				break;
			}
			case ADR: {
				parseContentLine(line, params, value, adrParser);
				break;
			}
			case TEL: {
				parseContentLine(line, params, value, telParser);
				break;
			}
			case EMAIL: {
				parseContentLine(line, params, value, eMailParser);
				break;
			}
			case ORG: {
				parseContentLine(line, params, value, orgParser);
				//unknownProperties.add(VCardTools.shorten(line, 80));
				//state.foundUnknownProperty();
				break;
			}
			case NOTE: {
				parseContentLine(line, params, value, noteParser);
				break;
			}
			case URL: {
				parseContentLine(line, params, value, urlParser);
				break;
			}
			case END: {
				parseEndProperty(group, params, value);
				break;
			}
			default:
				LOGGER.log(Level.INFO, "No parser for the known property {0}", property);
		}
		if (state instanceof MalformedVCardState) {
			assert malformedLine == null : malformedLine;
			malformedLine = line;
			throw new VCardParseException(VCardTools.shorten(line, 80));
		}
	}

	private VCardProperty<PropertyEntry> getTheSingleVCardPropertyInstanceFor(Property property) {
		VCardProperty<PropertyEntry> prop = mapOfVCardProperties.get(property);
		if (prop == null) {
			prop = VCardProperty.newInstance(property);
			mapOfVCardProperties.put(property, prop);
		}
		return prop;
	}

	void setState(VCardParsingState state) {
		if (state == null) {
			throw new NullPointerException();
		}
		this.state = state;
	}

	VCardParsingState getNewState() {
		return newState;
	}

	VCardParsingState getBeginState() {
		return beginState;
	}

	VCardParsingState getVersionState() {
		return versionState;
	}

	VCardParsingState getFnState() {
		return fnState;
	}

	VCardParsingState getBodyState() {
		return bodyState;
	}

	VCardParsingState getEndState() {
		return endState;
	}

	VCardParsingState getValidVCardState() {
		return validVCardState;
	}

	VCardParsingState getMalformedVCardState() {
		return malformedVCardState;
	}

	private void parseBeginProperty(String group, String params, String value) throws VCardParseException {
		if (group == null && params.isEmpty() && "VCARD".equals(value.toUpperCase(ENGLISH))) {
			state.foundBeginProperty();
		} else {
			state.foundMalformedLine();
		}
	}

	private void parseEndProperty(String group, String params, String value) throws VCardParseException {
		if (group == null && params.isEmpty() && "VCARD".equals(value.toUpperCase(ENGLISH))) {
			state.foundEndProperty();
		} else {
			state.foundMalformedLine();
		}
	}

	private void parseVersionProperty(String group, String params, String value) throws VCardParseException {
		// TODO Write VersionProperty
		// TODO Write VersionPropertyParser
		if (group == null && "4.0".equals(value)) {
			state.foundVersionProperty();
		} else {
			state.foundMalformedLine();
		}
	}

	private void parseFnProperty(String line, String params, String value) {
		fnParser.reset(params, value);
		fnParser.parse();
		if (fnParser.isValid()) {
			VCardProperty<PropertyEntry> prop = getTheSingleVCardPropertyInstanceFor(Property.FN);
			boolean addedSuccessfully = prop.addEntry(fnParser.getPropertyEntry());
			if (addedSuccessfully) {
				storeUnknownParameters(fnParser, line);
				state.foundFNProperty();
			} else {
				state.foundMalformedLine();
			}
		} else {
			state.foundMalformedLine();
		}
	}

	private void parseContentLine(String line, String params, String value, PropertyParser parser) {
		parser.reset(params, value);
		parser.parse();
		if (parser.isValid()) {
			VCardProperty<PropertyEntry> prop = getTheSingleVCardPropertyInstanceFor(parser.getProperty());
			PropertyEntry propertyEntry = parser.getPropertyEntry();
			boolean addedSuccessfully = prop.addEntry(propertyEntry);
			if (addedSuccessfully) {
				storeUnknownParameters(parser, line);
				state.foundContentProperty();
			} else {
				state.foundMalformedLine();
			}
		} else {
			state.foundMalformedLine();
		}
	}

	private void parseDateProperty(String line, String params, String value, PropertyParser parser) {
		parser.reset(params, value);
		parser.parse();
		if (parser.isValid()) {
			VCardProperty<PropertyEntry> prop = getTheSingleVCardPropertyInstanceFor(parser.getProperty());
			PropertyEntry propertyEntry = parser.getPropertyEntry();
			boolean addedSuccessfully = prop.addEntry(propertyEntry);
			if (addedSuccessfully) {
				storeUnknownParameters(parser, line);
				state.foundContentProperty();
			} else {
				state.foundMalformedLine();
			}
		} else if (!parser.hasIllegalParameters()
				&& !parser.hasIllegalTypeParameters()
				&& parser.hasUnknownCalscaleParameter()) {
			/*
			 * We can't check the date value without understanding the
			 * CALSCALE parameter, so we just pretend it's valid
			 */
			state.foundUnknownProperty();
		} else {
			state.foundMalformedLine();
		}
	}

	private void storeUnknownParameters(PropertyParser parser, String line) {
		assert parser != null;
		assert line != null;
		if (parser.hasUnknownTypeParameters()) {
			unknownTypeParameters.put(line, parser.getUnknownTypeParameters());
		}
		if (parser.hasUnknownParameters()) {
			unknownParameters.put(line, parser.getUnknownParameters());
		}
	}
}
