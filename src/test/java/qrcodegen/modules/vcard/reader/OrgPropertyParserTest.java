/*
 * Copyright (C) 2012 Stefan Ganzer
 *
 * This file is part of QRCodeGen.
 *
 * QRCodeGen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QRCodeGen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen.modules.vcard.reader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import qrcodegen.modules.vcard.OrgProperty;

/**
 *
 * @author Stefan Ganzer
 */
public class OrgPropertyParserTest {

	private static final String VALID_AS_STRING_1 = "ABC\\, Inc.;North American Dev.;Marketing";
	private static final String VALID_EMPTY_INPUT_1 = "";
	private static final String VALID_EMPTY_INPUT_2 = ";;";
	private OrgPropertyParser parser;

	public OrgPropertyParserTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		parser = new OrgPropertyParser();
	}

	@After
	public void tearDown() {
		parser = null;
	}

	/**
	 * Test of parse method, of class OrgPropertyParser.
	 */
	@Test
	public void testParse() {
		parser.reset(null, VALID_AS_STRING_1);
		parser.parse();
		assertTrue(parser.isDone());
		assertTrue(parser.isValid());

		parser.reset(null, VALID_EMPTY_INPUT_1);
		parser.parse();
		assertTrue(parser.isDone());
		assertTrue(parser.isValid());

		parser.reset(null, VALID_EMPTY_INPUT_2);
		parser.parse();
		assertTrue(parser.isDone());
		assertTrue(parser.isValid());

	}

	/**
	 * Test of getPropertyEntry method, of class OrgPropertyParser.
	 */
	@Test
	public void testGetPropertyEntry() {
		parser.reset(null, VALID_AS_STRING_1);
		parser.parse();
		OrgProperty property = parser.getPropertyEntry();

		String expResult = "ABC, Inc.";
		String result = property.getOrganizationName();
		assertEquals(expResult, result);

		expResult = "North American Dev.";
		result = property.getUnitName(0);
		assertEquals(expResult, result);

		expResult = "Marketing";
		result = property.getUnitName(1);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getPropertyEntry method, of class OrgPropertyParser.
	 */
	@Test
	public void testGetPropertyEntryEmptyInput1() {
		parser.reset(null, VALID_EMPTY_INPUT_1);
		parser.parse();
		OrgProperty property = parser.getPropertyEntry();

		String expResult = "";
		String result = property.getOrganizationName();
		assertEquals(expResult, result);
		
		assertFalse(property.hasUnitNames());
	}
	/**
	 * Test of getPropertyEntry method, of class OrgPropertyParser.
	 */
	@Test
	public void testGetPropertyEntryEmptyInput2() {
		parser.reset(null, VALID_EMPTY_INPUT_2);
		parser.parse();
		OrgProperty property = parser.getPropertyEntry();

		String expResult = "";
		String result = property.getOrganizationName();
		assertEquals(expResult, result);
		
		assertTrue(property.hasUnitNames());
		assertEquals(2, property.getUnitNames().size());
		
		expResult = "";
		result = property.getUnitName(0);
		assertEquals(expResult, result);

		expResult = "";
		result = property.getUnitName(1);
		assertEquals(expResult, result);

	}
	/**
	 * Test of reset method, of class OrgPropertyParser.
	 */
	@Test
	public void testReset() {
		assertFalse(parser.isDone());
		assertFalse(parser.isValid());

		parser.reset(null, VALID_AS_STRING_1);

		assertFalse(parser.isDone());
		assertFalse(parser.isValid());
		assertEquals(VALID_AS_STRING_1, parser.getValue());

		parser.parse();

		assertTrue(parser.isDone());
		assertTrue(parser.isValid());
	}
}
