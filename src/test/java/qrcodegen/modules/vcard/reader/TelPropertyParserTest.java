/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcard.reader;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.TelProperty;

/**
 *
 * @author Stefan Ganzer
 */
public class TelPropertyParserTest {
	
	private static final String VALID_INPUT_EMPTY = "";
	private static final String VALID_INPUT_NULL = null;
	private TelPropertyParser parser;
	
	public TelPropertyParserTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
		parser = new TelPropertyParser();
	}
	
	@After
	public void tearDown() {
		parser = null;
	}

	/**
	 * Test of parse method, of class TelPropertyParser.
	 */
	@Test
	public void testParseValidEmptyInput() {
		System.out.println("parseValidEmptyInput");
		parser.reset(null, VALID_INPUT_EMPTY);
		parser.parse();
		assertTrue(parser.isValid());
		assertTrue(parser.getPropertyEntry().getValueAsString().isEmpty());
	}

	@Test
	public void testParseValidNullInput() {
		System.out.println("parseValidNullInput");
		parser.reset(null, VALID_INPUT_NULL);
		parser.parse();
		assertTrue(parser.isValid());
		assertTrue(parser.getPropertyEntry().getValueAsString().isEmpty());
	}
	/**
	 * Test of getPropertyEntry method, of class TelPropertyParser.
	 */


	/**
	 * Test of getProperty method, of class TelPropertyParser.
	 */
	@Test
	public void testGetProperty() {
		System.out.println("getProperty");
		Property expResult = Property.TEL;
		Property result = parser.getProperty();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isValid method, of class TelPropertyParser.
	 */


	/**
	 * Test of isDone method, of class TelPropertyParser.
	 */


	/**
	 * Test of reset method, of class TelPropertyParser.
	 */

}
