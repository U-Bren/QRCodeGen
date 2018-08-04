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
package qrcodegen.modules.vcard;

import java.util.Comparator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Stefan Ganzer
 */
public class AdrPropertyTest {

	private static final String CRLF = "\r\n";

	public AdrPropertyTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testEmptyAddress() {
		doTest("", "", "", "", "", "", "", null, "ADR:;;;;;;" + CRLF);
	}

	@Test
	public void testEscapedCharacters() {
		doTest("a,b", "c\\d", "e;f", "g h", "", "", "", "a,b;c\\d;e;f;g h",
				"ADR;LABEL=\"a,b;c\\d;e;f;g h\":a\\,b;c\\\\d;e\\;f;g h;;;" + CRLF);
	}

	/**
	 * Test of getValueAsString method, of class AdrProperty.
	 */
	@Test
	public void testGetValueAsString() {
		System.out.println("getValueAsString");
		doTest("", "",
				"123 Main Street",
				"Any Town",
				"CA",
				"91921-1234",
				"U.S.A.",
				"Mr. John Q. Public, Esq.\nMail Drop: TNE QB\n123 Main Street\nAny Town, CA  91921-1234\nU.S.A.",
				"ADR;LABEL=\"Mr. John Q. Public, Esq.\\nMail Drop: TNE QB\\n123 Main Street\\nAn" + CRLF + " y Town, CA  91921-1234\\nU.S.A.\":;;123 Main Street;Any Town;CA;91921-1234;U" + CRLF + " .S.A." + CRLF);
	}

	private static void doTest(String poBox, String extAddress, String street, String locality, String region, String code, String country, String label, String expResult) {
		AdrProperty.Builder builder = AdrProperty.Builder.newInstance();
		builder.street(street);
		builder.locality(locality);
		builder.region(region);
		builder.poBox(poBox);
		builder.extAddress(extAddress);
		builder.code(code);
		builder.country(country);
		if (label != null) {
			builder.label(label);
		}
		AdrProperty prop = builder.build();

		assertEquals(street, prop.getStreet());
		assertEquals(locality, prop.getLocality());
		assertEquals(region, prop.getRegion());
		assertEquals(poBox, prop.getPoBox());
		assertEquals(extAddress, prop.getExtendedAddress());
		assertEquals(code, prop.getPostalCode());
		assertEquals(country, prop.getCountryName());
		if (label == null) {
			assertFalse(prop.hasLabel());
		} else {
			assertTrue(prop.hasLabel());
			assertEquals(label, prop.getLabel());
		}

		VCardProperty<AdrProperty> adr = VCardProperty.newInstance(Property.ADR);
		adr.addEntry(prop);
		assertEquals(expResult, adr.asString());
	}

	@Test
	public void testGetComparator() {
		AdrProperty.Builder adrBuilder = AdrProperty.Builder.newInstance();
		adrBuilder.pref(1);
		adrBuilder.country("baba");
		AdrProperty prop1 = adrBuilder.build();
		adrBuilder = AdrProperty.Builder.newInstance();
		adrBuilder.pref(10);
		adrBuilder.country("abba");
		AdrProperty prop2 = adrBuilder.build();

		Comparator<AdrProperty> c = AdrProperty.prefComparator();
		assertEquals(-1, c.compare(prop1, prop2));
		assertEquals(0, c.compare(prop1, prop1));
		assertEquals(1, c.compare(prop2, prop1));
	}
}
