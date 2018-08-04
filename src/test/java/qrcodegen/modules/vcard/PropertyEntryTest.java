/*
 Copyright (C) 2012 Stefan Ganzer

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
public class PropertyEntryTest {

	public PropertyEntryTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of isAlternativeRepresentationOf method, of class PropertyEntry.
	 */
	@Test
	public void testIsAlternativeRepresentationOf() {
		System.out.println("isAlternativeRepresentationOf");
		PropertyEntry other = NProperty.Builder.newInstance().altID("1").build();
		PropertyEntry instance = NProperty.Builder.newInstance().altID("1").build();
		boolean expResult = true;
		boolean result = instance.isAlternativeRepresentationOf(other);
		assertEquals(expResult, result);

		other = NProperty.Builder.newInstance().altID("1").build();
		instance = NProperty.Builder.newInstance().altID("2").build();
		expResult = false;
		result = instance.isAlternativeRepresentationOf(other);
		assertEquals(expResult, result);

		other = new FNProperty.Builder("").altID("1").build();
		instance = new FNProperty.Builder("").altID("1").build();
		expResult = true;
		result = instance.isAlternativeRepresentationOf(other);
		assertEquals(expResult, result);

		other = null;
		instance = new FNProperty.Builder("").altID("1").build();
		expResult = false;
		result = instance.isAlternativeRepresentationOf(other);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getProperty method, of class PropertyEntry.
	 */
	@Test
	public void testGetProperty() {
		System.out.println("getProperty");

		PropertyEntry instance = new FNProperty.Builder("").build();
		Property expResult = Property.FN;
		Property result = instance.getProperty();
		assertEquals(expResult, result);

		instance = new AdrProperty.Builder().build();
		expResult = Property.ADR;
		result = instance.getProperty();
		assertEquals(expResult, result);
	}

	@Test
	public void testGetValueAsString() {
		System.out.println("getValueAsString");
		PropertyEntry instance = NProperty.Builder.newInstance()
				.familyName("Musterfrau")
				.givenName("Melinda")
				.additionalName("Mia").additionalName("Sophie")
				.honorificPrefix("Dr.med.").honorificPrefix("Dr.med.dent.")
				.honorificSuffix("M.D.")
				.build();

		String result = instance.getValueAsString();
		String expResult = "Musterfrau;Melinda;Mia,Sophie;Dr.med.,Dr.med.dent.;M.D.";
		assertEquals(expResult, result);

		instance = NProperty.Builder.newInstance()
				.familyName("Musterfrau")
				.givenName("Melinda")
				.additionalName("Mia").additionalName("Sophie")
				.honorificPrefix("Dr.med.").honorificPrefix("Dr.med.dent.")
				.build();

		result = instance.getValueAsString();
		expResult = "Musterfrau;Melinda;Mia,Sophie;Dr.med.,Dr.med.dent.;";
		assertEquals(expResult, result);

	}

	/**
	 * Test of getAltID method, of class PropertyEntry.
	 */
	@Test
	public void testGetAltID() {
		System.out.println("getAltID");
		PropertyEntry instance = NProperty.Builder.newInstance().altID("1").build();
		VCardParameterValue expResult = new VCardParameterValue("1");
		VCardParameterValue result = instance.getAltID();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getAltID method, of class PropertyEntry.
	 */
	@Test
	public void testAsStringParametersSet() {
		System.out.println("asStringParametersSet");
		PropertyEntry instance = NProperty.Builder.newInstance().altID("1").build();
		String result = instance.asString();
		String expResult = "N;ALTID=1:;;;;\r\n";
		assertEquals(expResult, result);

		instance = new FNProperty.Builder("Martina Musterfrau").type(TypeParameter.HOME).altID("2").build();
		result = instance.asString();
		expResult = "FN;ALTID=2;TYPE=home:Martina Musterfrau\r\n";
		assertEquals(expResult, result);
	}
}
