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
import qrcodegen.modules.vcard.BDayProperty;
import qrcodegen.modules.vcard.Property;

/**
 *
 * @author Stefan Ganzer
 */
public class BDayPropertyParserTest {

	private static final String TEXT_DATE = ";VALUE=text";
	private static final String VALID_DATE_1 = "20010101";
	private static final String VALID_DATE_YEAR_MONTH = "2001-01";
	private static final String VALID_DATE_YEAR = "2001";
	private static final String VALID_DATE_MONTH = "--01";
	private static final String VALID_DATE_DAY = "---01";
	private static final String INVALID_DATE_YEAR = "81";
	private static final String INVALID_DATE_MONTH = "--1";
	private static final String INVALID_DATE_DAY = "---1";
	private static final String INVALID_DATE_HYPHENS = "----";
	private static final String INVALID_DATE_EMPTY = "";
	private static final String INVALID_DATE_SPACES = "   ";
	private static final String NULL = null;
	private String[] gregorian_date;
	private String[] julian_date;
	private BDayPropertyParser parser;

	public BDayPropertyParserTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		parser = new BDayPropertyParser();
		gregorian_date = new String[]{";VALUE=date-and-or-time;CALSCALE=gregorian", "20111113"};
		julian_date = new String[]{";VALUE=date-and-or-time;CALSCALE=julian", "20111113"};
	}

	@After
	public void tearDown() {
		parser = null;
		gregorian_date = null;
		julian_date = null;
	}

	/**
	 * Test of parse method, of class BDayPropertyParser.
	 */
	@Test
	public void testParseValidDate() {
		System.out.println("parseValidDate");
		parser.reset(null, VALID_DATE_1);
		parser.parse();
		assertTrue(parser.isValid());
	}

	@Test
	public void testParseValidDateYear() {
		System.out.println("parseValidDateYear");
		parser.reset(null, VALID_DATE_YEAR);
		parser.parse();
		assertTrue(parser.isValid());
	}

	@Test
	public void testParseValidDateMonth() {
		System.out.println("parseValidDateMonth");
		parser.reset(null, VALID_DATE_MONTH);
		parser.parse();
		assertTrue(parser.isValid());
	}

	@Test
	public void testParseValidDateDay() {
		System.out.println("parseValidDateDay");
		parser.reset(null, VALID_DATE_DAY);
		parser.parse();
		assertTrue(parser.isValid());
	}

	@Test
	public void testParseValidNullTextDate() {
		System.out.println("parseValidNullTextDate");
		parser.reset(TEXT_DATE, NULL);
		parser.parse();
		assertTrue(parser.isValid());
	}

	@Test
	public void testParseInvalidDateYear() {
		System.out.println("parseInvalidDateYear");
		parser.reset(null, INVALID_DATE_YEAR);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidDateMonth() {
		System.out.println("parseInvalidDateMonth");
		parser.reset(null, INVALID_DATE_MONTH);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidDateDay() {
		System.out.println("parseInvalidDateDay");
		parser.reset(null, INVALID_DATE_DAY);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseValidDateYearMonth() {
		System.out.println("parseValidDateYearMonth");
		parser.reset(null, VALID_DATE_YEAR_MONTH);
		parser.parse();
		assertTrue(parser.isValid());
	}

	@Test
	public void testParseInvalidDateHyphens() {
		System.out.println("parseInvalidDateHyphens");
		parser.reset(null, INVALID_DATE_HYPHENS);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidEmptyDate() {
		System.out.println("parseInvalidEmptyDate");
		parser.reset(null, INVALID_DATE_EMPTY);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidNullDate() {
		System.out.println("parseInvalidNullDate");
		parser.reset(null, NULL);
		parser.parse();
		assertFalse(parser.isValid());
	}

	@Test
	public void testParseInvalidDateSpaces() {
		System.out.println("parseInvalidDateSpaces");
		parser.reset(null, INVALID_DATE_SPACES);
		parser.parse();
		assertFalse(parser.isValid());
	}

	/**
	 * Test of getProperty method, of class BDayPropertyParser.
	 */
	@Test
	public void testGetProperty() {
		System.out.println("getProperty");
		Property expResult = Property.BDAY;
		Property result = parser.getProperty();
		assertEquals(expResult, result);
	}

	@Test
	public void testGregorianDate() {
		parser.reset(gregorian_date[0], gregorian_date[1]);
		parser.parse();
		assertTrue(parser.isValid());
		BDayProperty prop = parser.getPropertyEntry();
		assertEquals(2011, prop.getYear());
		assertEquals(11, prop.getMonth());
		assertEquals(13, prop.getDay());
	}

	@Test
	public void testNonGregorianDate() {
		parser.reset(julian_date[0], julian_date[1]);
		parser.parse();
		assertFalse(parser.isValid());
	}
	/**
	 * Test of getPropertyEntry method, of class BDayPropertyParser.
	 */
	/**
	 * Test of isDone method, of class BDayPropertyParser.
	 */
	/**
	 * Test of isValid method, of class BDayPropertyParser.
	 */
	/**
	 * Test of reset method, of class BDayPropertyParser.
	 */
}
