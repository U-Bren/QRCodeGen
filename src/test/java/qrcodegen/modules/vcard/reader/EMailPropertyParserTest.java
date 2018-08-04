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
import qrcodegen.modules.vcard.EMailProperty;
import qrcodegen.modules.vcard.Property;

/**
 *
 * @author Stefan Ganzer
 */
public class EMailPropertyParserTest {

	private static final String VALID_ADDRESS = "unknown@unknown.com";
	private static final String VALID_ADDRESS_EMPTY = "";
	private static final String VALID_ADDRESS_NULL = null;
	private static final String INVALID_ADDRESS = "unknown@\u000Bunknown.com";
	private EMailPropertyParser parser;

	public EMailPropertyParserTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		parser = new EMailPropertyParser();
	}

	@After
	public void tearDown() {
		parser = null;
	}

	/**
	 * Test of parse method, of class EMailPropertyParser.
	 */
	@Test
	public void testParseValidAddress() {
		System.out.println("parseValidAddress");
		parser.reset(null, VALID_ADDRESS);
		parser.parse();
		assertTrue(parser.isValid());
	}

	@Test
	public void testParseValidEmptyAddress() {
		System.out.println("parseValidEmptyAddress");
		parser.reset(null, VALID_ADDRESS_EMPTY);
		parser.parse();
		assertTrue(parser.isValid());
		assertTrue(parser.getPropertyEntry().getValueAsString().isEmpty());
	}

	@Test
	public void testParseValidNullAddress() {
		System.out.println("parseValidNullAddress");
		parser.reset(null, VALID_ADDRESS_NULL);
		parser.parse();
		assertTrue(parser.isValid());
		assertTrue(parser.getPropertyEntry().getValueAsString().isEmpty());
	}

	@Test
	public void testParseInvalidAddress() {
		System.out.println("parseInvalidAddress");
		parser.reset(null, INVALID_ADDRESS);
		parser.parse();
		assertFalse(parser.isValid());
	}

	/**
	 * Test of getPropertyEntry method, of class EMailPropertyParser.
	 */


	/**
	 * Test of getProperty method, of class EMailPropertyParser.
	 */
	@Test
	public void testGetProperty() {
		System.out.println("getProperty");
		Property expResult = Property.EMAIL;
		Property result = parser.getProperty();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isValid method, of class EMailPropertyParser.
	 */


	/**
	 * Test of isDone method, of class EMailPropertyParser.
	 */


	/**
	 * Test of reset method, of class EMailPropertyParser.
	 */

}
