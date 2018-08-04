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

/**
 *
 * @author Stefan Ganzer
 */
public class KindPropertyParserTest {

	private String[] valid_input_individual_1;
	private String[] valid_input_group_1;
	private String[] valid_input_org_1;
	private String[] valid_input_location_1;
	private String[] invalid_input_1;
	private String[] invalid_input_2;
	private String[] invalid_empty_input;
	private KindPropertyParser parser;

	public KindPropertyParserTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		valid_input_individual_1 = new String[]{";VALUE=TEXT", "individual"};
		valid_input_group_1 = new String[]{null, "group"};
		valid_input_org_1 = new String[]{null, "org"};
		valid_input_location_1 = new String[]{null, "location"};
		invalid_input_1 = new String[]{null, "x-name"};
		invalid_input_2 = new String[]{";VALUE=date", "org"};
		invalid_empty_input = new String[]{null, null};
		parser = new KindPropertyParser();
	}

	@After
	public void tearDown() {
		valid_input_individual_1 = null;
		valid_input_group_1 = null;
		valid_input_org_1 = null;
		valid_input_location_1 = null;
		invalid_input_1 = null;
		invalid_input_2 = null;
		invalid_empty_input = null;
		parser = null;
	}

	/**
	 * Test of parse method, of class KindPropertyParser.
	 */
	@Test
	public void testParseValidInputIndividual1() {
		parser.reset(valid_input_individual_1[0], valid_input_individual_1[1]);
		parser.parse();
		assertTrue(parser.isValid());
	}

	/**
	 * Test of parse method, of class KindPropertyParser.
	 */
	@Test
	public void testParseValidInputGroup1() {
		parser.reset(valid_input_group_1[0], valid_input_group_1[1]);
		parser.parse();
		assertTrue(parser.isValid());
	}

	/**
	 * Test of parse method, of class KindPropertyParser.
	 */
	@Test
	public void testParseValidInputOrg1() {
		parser.reset(valid_input_org_1[0], valid_input_org_1[1]);
		parser.parse();
		assertTrue(parser.isValid());
	}

	/**
	 * Test of parse method, of class KindPropertyParser.
	 */
	@Test
	public void testParseValidInputLocation1() {
		parser.reset(valid_input_location_1[0], valid_input_location_1[1]);
		parser.parse();
		assertTrue(parser.isValid());
	}

	@Test
	public void testParseInvalidInput1() {
		parser.reset(invalid_input_1[0], invalid_input_1[1]);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidInput2() {
		parser.reset(invalid_input_2[0], invalid_input_2[1]);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidEmptyInput() {
		parser.reset(invalid_empty_input[0], invalid_empty_input[1]);
		parser.parse();
		assertFalse(parser.isValid());
	}
}
