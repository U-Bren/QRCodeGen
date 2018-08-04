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
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import qrcodegen.modules.vcard.Calscale;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.PropertyParameter;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcard.ValueType;

/**
 *
 * @author Stefan Ganzer
 */
public class ParameterParserTest {

	private static final String TYPE_PARAM_WORK = ";TYPE=work";
	private static final String TYPE_PARAM_QUOTED_WORK = ";TYPE=\"work\"";
	private static final String TYPE_PARAMS_VALUE_LIST_FAX_VOICE = ";TYPE=FAX,VOICE";
	private static final String INVALID_TYPE_PARAMS_FAX_VOICE = ";TYPE=\"FAX,VOICE\"";
	private static final String TYPE_PARAMS_VALUE_LIST_QUOTED_WORK_FAX_VOICE_CELL = ";TYPE=\"work\",\"FAX\",\"VOICE\",\"cell\"";
	private static final String TYPE_PARAMS_VALUE_LIST_PARTLY_QUOTED_WORK_FAX_VOICE_CELL = ";TYPE=\"work\",FAX,\"VOICE\",\"cell\"";
	private static final String TYPE_PARAMS_FAX_VOICE_SMS = ";TYPE=FAX,\"VOICE\",\"sms\"";
	private static final String UNKNOWN_TYPE_PARAM_QUIRKS = ";TYPE=quirks";
	private static final String UNKNOWN_PARAMETER = ";TYP=work";
	private static final String TYPE_PARAMS_WORK_FAX_VOICE = ";TYPE=work;TYPE=fax;TYPE=voice";
	private static final String TYPE_PARAMS_PARTLY_QUOTED_WORK_FAX_VOICE = ";TYPE=work;TYPE=\"fax\";TYPE=voice";
	private ParameterParser parser;

	public ParameterParserTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		parser = new ParameterParser();
	}

	@After
	public void tearDown() {
		parser = null;
	}

	/**
	 * Test of reset method, of class ParameterParser.
	 */
	@Test
	public void testReset() {
		System.out.println("reset");

		final Property property = Property.TEL;
		final String input = TYPE_PARAM_WORK;

		assertNull(parser.getProperty());
		assertEquals(null, parser.getInput());

		parser.reset(property, input);
		assertEquals(property, parser.getProperty());
		assertEquals(input, parser.getInput());
	}

	/**
	 * Test of parse method, of class ParameterParser.
	 */
	@Test
	public void testParseAllValid() throws VCardParseException {
		System.out.println("parseAllValid");
		final Property property = Property.TEL;
		final String input = TYPE_PARAM_WORK;
		final Set<TypeParameter> expectedTypeParameters = EnumSet.of(TypeParameter.WORK);

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertFalse(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());
		assertFalse(parser.getTypeParameter().isEmpty());

		assertEquals(expectedTypeParameters, parser.getTypeParameter());
	}

	@Test
	public void testParseAllValidQuoted() throws VCardParseException {
		System.out.println("parseAllValidQuoted");
		final Property property = Property.TEL;
		final String input = TYPE_PARAM_QUOTED_WORK;
		final Set<TypeParameter> expectedTypeParameters = EnumSet.of(TypeParameter.WORK);

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertFalse(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());
		assertFalse(parser.getTypeParameter().isEmpty());

		assertEquals(expectedTypeParameters, parser.getTypeParameter());
	}

	/**
	 * Test of parse method, of class ParameterParser.
	 */
	@Test
	public void testParseInvalid() throws VCardParseException {
		System.out.println("parseInvalid");
		final Property property = Property.TEL;
		final String input = INVALID_TYPE_PARAMS_FAX_VOICE;
		final Set<TypeParameter> expectedTypeParameters = EnumSet.noneOf(TypeParameter.class);
		final Set<String> expectedUnknownTypeParameters = new HashSet<String>();
		expectedUnknownTypeParameters.add("FAX,VOICE");

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertTrue(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());

		assertTrue(parser.getTypeParameter().isEmpty());
		assertEquals(expectedTypeParameters, parser.getTypeParameter());

		assertTrue(parser.getUnknownParameters().isEmpty());
		assertFalse(parser.getUnknownTypeParameters().isEmpty());
		assertEquals(expectedUnknownTypeParameters, parser.getUnknownTypeParameters());

		assertTrue(parser.getIllegalParameters().isEmpty());
		assertTrue(parser.getIllegalTypeParameters().isEmpty());
	}

	/**
	 * Test of parse method, of class ParameterParser.
	 */
	@Test
	public void testParseAllValid2ValueList() throws VCardParseException {
		System.out.println("parseAllValid2ValueList");
		final Property property = Property.TEL;
		final String input = TYPE_PARAMS_VALUE_LIST_FAX_VOICE;
		final Set<TypeParameter> expectedTypeParameters = EnumSet.of(TypeParameter.FAX, TypeParameter.VOICE);

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertFalse(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());

		assertFalse(parser.getTypeParameter().isEmpty());
		assertEquals(expectedTypeParameters, parser.getTypeParameter());

		assertTrue(parser.getUnknownParameters().isEmpty());
		assertTrue(parser.getUnknownTypeParameters().isEmpty());

		assertTrue(parser.getIllegalParameters().isEmpty());
		assertTrue(parser.getIllegalTypeParameters().isEmpty());
	}

	@Test
	public void testParseAllValid3() throws VCardParseException {
		final Property property = Property.TEL;
		final String input = TYPE_PARAMS_WORK_FAX_VOICE;
		final Set<TypeParameter> expectedTypeParameters = EnumSet.of(TypeParameter.WORK, TypeParameter.FAX, TypeParameter.VOICE);

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertFalse(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());

		assertFalse(parser.getTypeParameter().isEmpty());
		assertEquals(expectedTypeParameters, parser.getTypeParameter());

		assertTrue(parser.getUnknownParameters().isEmpty());
		assertTrue(parser.getUnknownTypeParameters().isEmpty());

		assertTrue(parser.getIllegalParameters().isEmpty());
		assertTrue(parser.getIllegalTypeParameters().isEmpty());

	}

	@Test
	public void testParseAllValid3PartlyQuoted() throws VCardParseException {
		final Property property = Property.TEL;
		final String input = TYPE_PARAMS_PARTLY_QUOTED_WORK_FAX_VOICE;
		final Set<TypeParameter> expectedTypeParameters = EnumSet.of(TypeParameter.WORK, TypeParameter.FAX, TypeParameter.VOICE);

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertFalse(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());

		assertFalse(parser.getTypeParameter().isEmpty());
		assertEquals(expectedTypeParameters, parser.getTypeParameter());

		assertTrue(parser.getUnknownParameters().isEmpty());
		assertTrue(parser.getUnknownTypeParameters().isEmpty());

		assertTrue(parser.getIllegalParameters().isEmpty());
		assertTrue(parser.getIllegalTypeParameters().isEmpty());

	}

	@Test
	public void testParseAllValidValueListQuoted() throws VCardParseException {
		final Property property = Property.TEL;
		final String input = TYPE_PARAMS_VALUE_LIST_QUOTED_WORK_FAX_VOICE_CELL;
		final Set<TypeParameter> expectedTypeParameters = EnumSet.of(TypeParameter.WORK, TypeParameter.FAX, TypeParameter.VOICE, TypeParameter.CELL);

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertFalse(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());

		assertFalse(parser.getTypeParameter().isEmpty());
		assertEquals(expectedTypeParameters, parser.getTypeParameter());

		assertTrue(parser.getUnknownParameters().isEmpty());
		assertTrue(parser.getUnknownTypeParameters().isEmpty());

		assertTrue(parser.getIllegalParameters().isEmpty());
		assertTrue(parser.getIllegalTypeParameters().isEmpty());

	}

	@Test
	public void testParseAllValidValueListPartlyQuoted() throws VCardParseException {
		final Property property = Property.TEL;
		final String input = TYPE_PARAMS_VALUE_LIST_PARTLY_QUOTED_WORK_FAX_VOICE_CELL;
		final Set<TypeParameter> expectedTypeParameters = EnumSet.of(TypeParameter.WORK, TypeParameter.FAX, TypeParameter.VOICE, TypeParameter.CELL);

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertFalse(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());

		assertFalse(parser.getTypeParameter().isEmpty());
		assertEquals(expectedTypeParameters, parser.getTypeParameter());

		assertTrue(parser.getUnknownParameters().isEmpty());
		assertTrue(parser.getUnknownTypeParameters().isEmpty());

		assertTrue(parser.getIllegalParameters().isEmpty());
		assertTrue(parser.getIllegalTypeParameters().isEmpty());

	}

	/**
	 * Test of parse method, of class ParameterParser.
	 */
	@Test
	public void testParseIllegalTypeParameter() throws VCardParseException {
		System.out.println("parseIllegalTypeParameter");
		final Property property = Property.N;
		final String input = TYPE_PARAM_WORK;

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertFalse(parser.hasUnknownTypeParameter());
		assertTrue(parser.hasIllegalParameters());
		assertTrue(parser.hasIllegalTypeParameter());
		assertFalse(parser.isValidParameter());
		assertTrue(parser.getTypeParameter().isEmpty());
		if (parser.hasIllegalTypeParameter()) {
			for (TypeParameter t : parser.getIllegalTypeParameters()) {
				System.out.println(t);
			}
		}
		if (parser.hasIllegalParameters()) {
			for (Map.Entry<PropertyParameter, List<ParameterParser.ValueHolder>> entry : parser.getIllegalParameters().entrySet()) {
				System.out.println(entry.getKey() + ":");
				for (ParameterParser.ValueHolder h : entry.getValue()) {
					System.out.println("--->" + h.getValue() + ":" + h.isInDQuotes());
				}
			}
		}
	}

	@Test
	public void testParseKnownAndUnknownTypeParameter() throws VCardParseException {
		System.out.println("parseKnownAndUnknownTypeParameter");
		final Property property = Property.TEL;
		final String input = TYPE_PARAMS_FAX_VOICE_SMS;
		final Set<TypeParameter> expectedTypeParameters = EnumSet.of(TypeParameter.FAX, TypeParameter.VOICE);
		final Set<String> expectedUnknownTypeParameter = new HashSet<String>();
		expectedUnknownTypeParameter.add("sms");

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertTrue(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());

		assertFalse(parser.getTypeParameter().isEmpty());
		assertEquals(expectedTypeParameters, parser.getTypeParameter());

		assertTrue(parser.getUnknownParameters().isEmpty());
		assertFalse(parser.getUnknownTypeParameters().isEmpty());

		assertTrue(parser.getIllegalParameters().isEmpty());
		assertTrue(parser.getIllegalTypeParameters().isEmpty());

		assertEquals(expectedUnknownTypeParameter, parser.getUnknownTypeParameters());

	}

	@Test
	public void testParseIllegalParameter() throws VCardParseException {
		System.out.println("parseIllegalParameter");
		final Property property = Property.BEGIN;
		final String input = TYPE_PARAMS_FAX_VOICE_SMS;
		final Set<TypeParameter> expectedTypeParameters = EnumSet.noneOf(TypeParameter.class);

		final Set<String> expectedUnknownTypeParameter = new HashSet<String>(1);
		expectedUnknownTypeParameter.add("sms");

		final Map<PropertyParameter, List<ParameterParser.ValueHolder>> expectedIllegalParameters = new EnumMap<PropertyParameter, List<ParameterParser.ValueHolder>>(PropertyParameter.class);
		List<ParameterParser.ValueHolder> l = new ArrayList<ParameterParser.ValueHolder>(1);
		l.add(new ParameterParser.ValueHolder("FAX", false));
		l.add(new ParameterParser.ValueHolder("VOICE", true));
		l.add(new ParameterParser.ValueHolder("sms", true));
		expectedIllegalParameters.put(PropertyParameter.TYPE, l);

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertTrue(parser.hasUnknownTypeParameter());
		assertTrue(parser.hasIllegalParameters());
		assertTrue(parser.hasIllegalTypeParameter());
		assertFalse(parser.isValidParameter());

		assertTrue(parser.getTypeParameter().isEmpty());
		assertEquals(expectedTypeParameters, parser.getTypeParameter());

		assertTrue(parser.getUnknownParameters().isEmpty());
		assertFalse(parser.getUnknownTypeParameters().isEmpty());

		assertFalse(parser.getIllegalParameters().isEmpty());
		assertFalse(parser.getIllegalTypeParameters().isEmpty());

		assertEquals(expectedUnknownTypeParameter, parser.getUnknownTypeParameters());
		for (int i = 0; i < expectedIllegalParameters.size(); i++) {
			assertEquals(expectedIllegalParameters.get(PropertyParameter.TYPE).get(i).getValue(),
					parser.getIllegalParameters().get(PropertyParameter.TYPE).get(i).getValue());
		}

	}

	/**
	 * Test of parse method, of class ParameterParser.
	 */
	@Test
	public void testParseUnknownTypeParameter() throws VCardParseException {
		System.out.println("parseUnknownTypeParameter");
		final Property property = Property.FN;
		final String input = UNKNOWN_TYPE_PARAM_QUIRKS;

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertTrue(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());
		assertTrue(parser.getTypeParameter().isEmpty());
		if (parser.hasIllegalTypeParameter()) {
			for (TypeParameter t : parser.getIllegalTypeParameters()) {
				System.out.println(t);
			}
		}
		if (parser.hasIllegalParameters()) {
			for (Map.Entry<PropertyParameter, List<ParameterParser.ValueHolder>> entry : parser.getIllegalParameters().entrySet()) {
				System.out.println(entry.getKey() + ":");
				for (ParameterParser.ValueHolder h : entry.getValue()) {
					System.out.println("--->" + h.getValue() + ":" + h.isInDQuotes());
				}
			}
		}
	}

	/**
	 * Test of parse method, of class ParameterParser.
	 */
	@Test
	public void testParseUnknownParameterType() throws VCardParseException {
		System.out.println("parseUnknownParameterType");
		final Property property = Property.FN;
		final String input = UNKNOWN_PARAMETER;

		parser.reset(property, input);
		parser.parse();
		assertTrue(parser.hasUnknownParameters());
		assertFalse(parser.hasUnknownTypeParameter());
		assertFalse(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertTrue(parser.isValidParameter());
		assertTrue(parser.getTypeParameter().isEmpty());
		assertFalse(parser.getUnknownParameters().isEmpty());
		assertTrue(parser.getUnknownTypeParameters().isEmpty());
		assertTrue(parser.getIllegalParameters().isEmpty());
		assertTrue(parser.getIllegalTypeParameters().isEmpty());

		if (parser.hasIllegalTypeParameter()) {
			for (TypeParameter t : parser.getIllegalTypeParameters()) {
				System.out.println(t);
			}
		}
		if (parser.hasIllegalParameters()) {
			for (Map.Entry<PropertyParameter, List<ParameterParser.ValueHolder>> entry : parser.getIllegalParameters().entrySet()) {
				System.out.println(entry.getKey() + ":");
				for (ParameterParser.ValueHolder h : entry.getValue()) {
					System.out.println("--->" + h.getValue() + ":" + h.isInDQuotes());
				}
			}
		}
	}

	/**
	 * Test of parse method, of class ParameterParser.
	 */
	@Test
	public void testParseIllegalAndUnknownTypeParameter() throws VCardParseException {
		System.out.println("parseIllegalAndUnknownTypeParameter");
		// N-properties must not have any TYPE parameter
		final Property property = Property.N;
		final String input = UNKNOWN_TYPE_PARAM_QUIRKS;

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertTrue(parser.hasUnknownTypeParameter());
		assertTrue(parser.hasIllegalParameters());
		assertFalse(parser.hasIllegalTypeParameter());
		assertFalse(parser.isValidParameter());
		assertTrue(parser.getTypeParameter().isEmpty());
		if (parser.hasIllegalTypeParameter()) {
			for (TypeParameter t : parser.getIllegalTypeParameters()) {
				System.out.println(t);
			}
		}
		if (parser.hasIllegalParameters()) {
			for (Map.Entry<PropertyParameter, List<ParameterParser.ValueHolder>> entry : parser.getIllegalParameters().entrySet()) {
				System.out.println(entry.getKey() + ":");
				for (ParameterParser.ValueHolder h : entry.getValue()) {
					System.out.println("--->" + h.getValue() + ":" + h.isInDQuotes());
				}
			}
		}
	}

	/**
	 * Test of parse method, of class ParameterParser.
	 */
	@Test
	public void testParseIllegalButKnownTypeParameter() throws VCardParseException {
		System.out.println("parseIllegalButKnownTypeParameter");
		// N-properties must not have any TYPE parameter
		final Property property = Property.N;
		final String input = TYPE_PARAM_WORK;

		parser.reset(property, input);
		parser.parse();
		assertFalse(parser.hasUnknownParameters());
		assertFalse(parser.hasUnknownTypeParameter());
		assertTrue(parser.hasIllegalParameters());
		assertTrue(parser.hasIllegalTypeParameter());
		assertFalse(parser.isValidParameter());
		assertTrue(parser.getTypeParameter().isEmpty());
		if (parser.hasIllegalTypeParameter()) {
			for (TypeParameter t : parser.getIllegalTypeParameters()) {
				System.out.println(t);
			}
		}
		if (parser.hasIllegalParameters()) {
			for (Map.Entry<PropertyParameter, List<ParameterParser.ValueHolder>> entry : parser.getIllegalParameters().entrySet()) {
				System.out.println(entry.getKey() + ":");
				for (ParameterParser.ValueHolder h : entry.getValue()) {
					System.out.println("--->" + h.getValue() + ":" + h.isInDQuotes());
				}
			}
		}
	}
	/**
	 * Test of isDone method, of class ParameterParser.
	 */
	/**
	 * Test of isReset method, of class ParameterParser.
	 */
	/**
	 * Test of isCompletelyKnownParameter method, of class ParameterParser.
	 */
	/**
	 * Test of isValidParameter method, of class ParameterParser.
	 */
	/**
	 * Test of hasAltID method, of class ParameterParser.
	 */
	/**
	 * Test of getAltID method, of class ParameterParser.
	 */
	/**
	 * Test of hasCalscaleParameter method, of class ParameterParser.
	 */
	/**
	 * Test of isCalscaleGregorian method, of class ParameterParser.
	 */
	/**
	 * Test of getCalscaleParameter method, of class ParameterParser.
	 */
	/**
	 * Test of hasUnknownCalscaleParameter method, of class ParameterParser.
	 */
	/**
	 * Test of getUnknownCalscaleParameter method, of class ParameterParser.
	 */
	/**
	 * Test of hasPref method, of class ParameterParser.
	 */
	/**
	 * Test of hasValueType method, of class ParameterParser.
	 */
	/**
	 * Test of getValueType method, of class ParameterParser.
	 */
	/**
	 * Test of hasTypeParameter method, of class ParameterParser.
	 */
	/**
	 * Test of getTypeParameter method, of class ParameterParser.
	 */
	/**
	 * Test of hasIllegalTypeParameter method, of class ParameterParser.
	 */
	/**
	 * Test of getIllegalTypeParameters method, of class ParameterParser.
	 */
	/**
	 * Test of hasUnknownTypeParameter method, of class ParameterParser.
	 */
	/**
	 * Test of getUnknownTypeParameters method, of class ParameterParser.
	 */
	/**
	 * Test of hasSortAsParameter method, of class ParameterParser.
	 */
	/**
	 * Test of hasLabelParameter method, of class ParameterParser.
	 */
	/**
	 * Test of getLabelParameter method, of class ParameterParser.
	 */
	/**
	 * Test of hasUnknownParameters method, of class ParameterParser.
	 */
	/**
	 * Test of getUnknownParameters method, of class ParameterParser.
	 */
	/**
	 * Test of hasIllegalParameters method, of class ParameterParser.
	 */
	/**
	 * Test of getIllegalParameters method, of class ParameterParser.
	 */
}
