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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import qrcodegen.modules.vcard.NProperty;
import qrcodegen.modules.vcard.Property;

/**
 *
 * @author Stefan Ganzer
 */
public class NPropertyParserTest {

	private static final String VALID_INPUT = "Last,name;first name;additional\tname;honorificPrefixes;honorific\nsuffixes";
	private static final String INVALID_INPUT_CTRL_CHARACTERS = "Last,name\u000B;first name;additional\tname;honorificPrefixes;honorific\nsuffixes";
	private static final String VALID_INPUT_EMPTY_COMPONENTS = ";;;;";
	private static final String INVALID_INPUT_EMPTY = "";
	private static final String INVALID_INPUT_NULL = null;
	private NPropertyParser parser;

	public NPropertyParserTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		parser = new NPropertyParser();
	}

	@After
	public void tearDown() {
		parser = null;
	}

	/**
	 * Test of parse method, of class NPropertyParser.
	 */
	@Test
	public void testParseValidInput() {
		System.out.println("parseValidInput");
		parser.reset(null, VALID_INPUT);
		parser.parse();
		assertTrue(parser.isValid());
	}

	@Test
	public void testParseValidEmptyComponentsInput() {
		System.out.println("parseValidEmptyComponentsInput");
		parser.reset(null, VALID_INPUT_EMPTY_COMPONENTS);
		parser.parse();
		assertTrue(parser.isValid());
		assertTrue(parser.getPropertyEntry().getLastName().isEmpty());
		assertTrue(parser.getPropertyEntry().getFirstName().isEmpty());
		assertTrue(parser.getPropertyEntry().getAdditionalNames().isEmpty());
		assertTrue(parser.getPropertyEntry().getHonorificPrefixes().isEmpty());
		assertTrue(parser.getPropertyEntry().getHonorificSuffixes().isEmpty());
	}

	@Test
	public void testParseInvalidInput() {
		System.out.println("parseInvalidInput");
		parser.reset(null, INVALID_INPUT_CTRL_CHARACTERS);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidNullInput() {
		System.out.println("parseInvalidNullInput");
		parser.reset(null, INVALID_INPUT_NULL);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidEmptyInput() {
		System.out.println("parseInvalidEmptyInput");
		parser.reset(null, INVALID_INPUT_EMPTY);
		parser.parse();
		assertFalse(parser.isValid());
	}

	/**
	 * Test of getPropertyEntry method, of class NPropertyParser.
	 */


	/**
	 * Test of getProperty method, of class NPropertyParser.
	 */
	@Test
	public void testGetProperty() {
		System.out.println("getProperty");
		Property expResult = Property.N;
		Property result = parser.getProperty();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isValid method, of class NPropertyParser.
	 */


	/**
	 * Test of isDone method, of class NPropertyParser.
	 */


	/**
	 * Test of reset method, of class NPropertyParser.
	 */

}
