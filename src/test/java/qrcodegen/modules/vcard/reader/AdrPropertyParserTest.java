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
import qrcodegen.modules.vcard.AdrProperty;
import qrcodegen.modules.vcard.Property;

/**
 *
 * @author Stefan Ganzer
 */
public class AdrPropertyParserTest {

	private static final String VALID_ADDRESS_1 = ";;123 Main Street;Any Town;CA;91921-1234;U.S.A.";
	private static final String VALID_ADDRESS_2 = ";;123\\, Main Street;Any\\; Town;C\\\\A;91921-1234;U.S.A.";
	private static final String VALID_ADDRESS_EMPTY = ";;;;;;";
	private static final String INVALID_ADDRESS_COMPONENTS_MISSING = "123 Main Street;Any Town;CA;91921-1234;U.S.A.";
	private static final String INVALID_ADDRESS_EMPTY = null;
	private AdrPropertyParser parser;

	public AdrPropertyParserTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		parser = new AdrPropertyParser();
	}

	@After
	public void tearDown() {
		parser = null;
	}

	/**
	 * Test of parse method, of class AdrPropertyParser.
	 */
	@Test
	public void testParseValidAddress() {
		System.out.println("parseValidAddress");
		doTest(null, VALID_ADDRESS_1, "","","123 Main Street","Any Town","CA","91921-1234","U.S.A.",null);
		doTest(null, VALID_ADDRESS_2, "","","123, Main Street","Any; Town","C\\A","91921-1234","U.S.A.",null);
	}

	@Test
	public void testParseValidEmptyAddress() {
		System.out.println("parseValidEmptyAddress");
		doTest(null, VALID_ADDRESS_EMPTY, "","","","","","","",null);
	}

	@Test
	public void testParseInvalidAddress() {
		System.out.println("parseInvalidAddress");
		parser.reset(null, INVALID_ADDRESS_COMPONENTS_MISSING);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidEmptyAddress() {
		System.out.println("parseInvalidEmptyAddress");
		parser.reset(null, INVALID_ADDRESS_EMPTY);
		parser.parse();
		assertFalse(parser.isValid());
	}

	/**
	 * Test of getProperty method, of class AdrPropertyParser.
	 */
	@Test
	public void testGetProperty() {
		System.out.println("getProperty");
		Property expResult = Property.ADR;
		Property result = parser.getProperty();
		assertEquals(expResult, result);
	}
	
	private static void doTest(String params, String input, String poBox, String extAdr, String street, String locality,
			String region, String postalCode, String countryName, String label){
		AdrPropertyParser parser = new AdrPropertyParser();
		parser.reset(params, input);
		parser.parse();
		assertTrue(parser.isDone());
		assertTrue(parser.isValid());
		
		AdrProperty adr = parser.getPropertyEntry();
		assertEquals(poBox, adr.getPoBox());
		assertEquals(extAdr, adr.getExtendedAddress());
		assertEquals(street, adr.getStreet());
		assertEquals(locality, adr.getLocality());
		assertEquals(region, adr.getRegion());
		assertEquals(postalCode, adr.getPostalCode());
		assertEquals(countryName, adr.getCountryName());
		if(label == null){
			assertFalse(adr.hasLabel());
		}else{
			assertTrue(adr.hasLabel());
			assertEquals(label, adr.getLabel());
		}
	}
}
