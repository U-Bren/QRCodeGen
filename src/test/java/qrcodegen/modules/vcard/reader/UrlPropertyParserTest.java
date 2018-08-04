/*/*
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
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.UrlProperty;

/**
 *
 * @author Stefan Ganzer
 */
public class UrlPropertyParserTest {

	private static final String VALID_URL_1 = "http://de.wikipedia.org/wiki/Uniform_Resource_Identifier";
	private static final String INVALID_URL_1_SCHEME_MISSING = "de.wikipedia.org/wiki/Uniform_Resource_Identifier";
	private static final String INVALID_URL_EMPTY = "";
	private static final String INVALID_URL_NULL = null;
	
	private UrlPropertyParser parser;

	public UrlPropertyParserTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		parser = new UrlPropertyParser();
	}

	@After
	public void tearDown() {
		parser = null;
	}

	/**
	 * Test of parse method, of class UrlPropertyParser.
	 */
	@Test
	public void testParseValidUrl() {
		System.out.println("parseValidUrl");
		parser.reset(null, VALID_URL_1);
		parser.parse();
		assertTrue(parser.isValid());
	}

	@Test
	public void testParseInvalidUrl() {
		System.out.println("parseInvalidUrl");
		parser.reset(null, INVALID_URL_1_SCHEME_MISSING);
		parser.parse();
		assertFalse(parser.isValid());
	}
	
	@Test
	public void testParseInvalidEmptyUrl() {
		System.out.println("parseInvalidEmptyUrl");
		parser.reset(null, INVALID_URL_EMPTY);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidNullUrl() {
		System.out.println("parseInvalidNullUrl");
		parser.reset(null, INVALID_URL_NULL);
		parser.parse();
		assertFalse(parser.isValid());
	}
	/**
	 * Test of getPropertyEntry method, of class UrlPropertyParser.
	 */


	/**
	 * Test of getProperty method, of class UrlPropertyParser.
	 */
	@Test
	public void testGetProperty() {
		System.out.println("getProperty");
		Property expResult = Property.URL;
		Property result = parser.getProperty();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isValid method, of class UrlPropertyParser.
	 */


	/**
	 * Test of isDone method, of class UrlPropertyParser.
	 */


	/**
	 * Test of reset method, of class UrlPropertyParser.
	 */

}
