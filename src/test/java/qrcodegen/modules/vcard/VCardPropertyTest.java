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

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import qrcodegen.modules.vcard.FNProperty.Builder;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardPropertyTest {

	private VCardProperty<FNProperty> fnProperty;
	private VCardProperty<NProperty> nProperty;

	public VCardPropertyTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		fnProperty = VCardProperty.newInstance(Property.FN);
		nProperty = VCardProperty.newInstance(Property.N);
	}

	@After
	public void tearDown() {
		fnProperty = null;
		nProperty = null;
	}

	/**
	 * Test of newInstance method, of class VCardProperty.
	 */
	@Test
	public void testGetProperty() {
		System.out.println("getProperty");
		assertEquals(Property.FN, fnProperty.getProperty());
	}

	/**
	 * Test of addEntry method, of class VCardProperty.
	 */
	@Test
	public void testAddEntry() {
		System.out.println("addEntry");

		FNProperty entry = new FNProperty.Builder("Melissa Musterfrau").build();
		Builder builder = new FNProperty.Builder("Dr. Melissa Musterfrau");
		builder.altID("1");
		FNProperty entry2 = builder.build();
		fnProperty.addEntry(entry);
		fnProperty.addEntry(entry2);

		List<FNProperty> entries = fnProperty.getEntries();
		assertEquals(2, entries.size());
		assertEquals(entry, entries.get(0));
		assertEquals(entry2, entries.get(1));

	}

	@Test
	public void testAddEntryIllegallyTwice() {
		System.out.println("addEntryIllegallyTwice");

		NProperty entry = NProperty.Builder.newInstance().familyName("Musterfrau").givenName("Meline").build();

		boolean result1 = nProperty.addEntry(entry);
		boolean result2 = nProperty.addEntry(entry);

		assertTrue(result1);
		assertFalse(result2);
	}

	@Test
	public void testAddIllegallyTwoEntries() {
		System.out.println("addIllegallyTwoEntries");

		NProperty entry1 = NProperty.Builder.newInstance().familyName("Musterfrau").givenName("Meline").build();
		NProperty.Builder builder = NProperty.Builder.newInstance();
		builder.familyName("Musterfrau").givenName("Meline").altID("1");
		NProperty entry2 = builder.build();

		boolean result1 = nProperty.addEntry(entry1);
		boolean result2 = nProperty.addEntry(entry2);

		assertTrue(result1);
		assertFalse(result2);
	}

	@Test
	public void testAddIllegallyTwoEntries2() {
		System.out.println("addIllegallyTwoEntries");

		NProperty entry1 = NProperty.Builder.newInstance().familyName("Musterfrau").givenName("Meline").build();

		NProperty.Builder builder = NProperty.Builder.newInstance();
		builder.familyName("Mustermann").givenName("Anton");
		NProperty entry2 = builder.build();

		boolean result1 = nProperty.addEntry(entry1);
		boolean result2 = nProperty.addEntry(entry2);

		assertTrue(result1);
		assertFalse(result2);
	}

	@Test
	public void testAddAlternativeEntries() {
		System.out.println("addAlternativeEntries");

		NProperty.Builder builder = NProperty.Builder.newInstance();
		builder.givenName("Meline").altID("1");
		NProperty entry1 = builder.build();

		builder = NProperty.Builder.newInstance();
		builder.familyName("Musterfrau").givenName("Meline").altID("1");
		NProperty entry2 = builder.build();

		nProperty.addEntry(entry1);
		nProperty.addEntry(entry2);
	}

//	@Test(expected = IllegalArgumentException.class)
//	public void testAddEntryWrongPropertyType(){
//		System.out.println("addEntryWrongPropertyType");
//		PropertyEntry entry = NProperty.Builder.newInstance().givenName("Melinda").build();
//		VCardProperty instance = VCardProperty.newInstance(Property.FN);
//		instance.addEntry(entry);
//	}
	/**
	 * Test of deleteEntry method, of class VCardProperty.
	 */


	/**
	 * Test of getEntries method, of class VCardProperty.
	 */


	/**
	 * Test of newInstance method, of class VCardProperty.
	 */


	/**
	 * Test of removeEntry method, of class VCardProperty.
	 */


	/**
	 * Test of toString method, of class VCardProperty.
	 */


	/**
	 * Test of asString method, of class VCardProperty.
	 */


	/**
	 * Test of isValid method, of class VCardProperty.
	 */
	@Test
	public void testIsValid() {
		System.out.println("isValid");

		NProperty.Builder builder = NProperty.Builder.newInstance();
		builder.givenName("Meline").altID("1");
		NProperty entry1 = builder.build();

		builder = NProperty.Builder.newInstance();
		builder.familyName("Musterfrau").givenName("Meline").altID("1");
		NProperty entry2 = builder.build();

		nProperty.addEntry(entry1);
		nProperty.addEntry(entry2);
		assertTrue(nProperty.isValid());

		assertFalse(fnProperty.isValid());
	}

	/**
	 * Test of compareTo method, of class VCardProperty.
	 */
	@Test
	public void testCompareTo() {
		System.out.println("compareTo");

		NProperty entry = NProperty.Builder.newInstance().familyName("Musterfrau").givenName("Meline").build();
		nProperty.addEntry(entry);

		FNProperty entry2 = new FNProperty.Builder("Melissa Musterfrau").build();
		fnProperty.addEntry(entry2);

		//TODO Comparison of PropertyEntry

		int expResult = 1;
		int result = nProperty.compareTo(fnProperty);
		assertEquals(expResult, result);

		expResult = -1;
		result = fnProperty.compareTo(nProperty);
		assertEquals(expResult, result);

		expResult = 0;
		result = fnProperty.compareTo(fnProperty);
		assertEquals(expResult, result);
	}

	/**
	 * Test of equals method, of class VCardProperty.
	 */
	@Test
	public void testEquals() {

		boolean expResult = false;
		boolean result = fnProperty.equals(nProperty);
		assertEquals(expResult, result);

		expResult = true;
		result = fnProperty.equals(fnProperty);
		assertEquals(expResult, result);
	}

	/**
	 * Test of hashCode method, of class VCardProperty.
	 */

}
