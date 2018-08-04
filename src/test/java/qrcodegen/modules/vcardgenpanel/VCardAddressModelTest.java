/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcardgenpanel;

import qrcodegen.modules.vcardgenpanel.model.VCardAddressModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import qrcodegen.modules.vcard.AdrProperty;
import qrcodegen.modules.vcard.TypeParameter;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardAddressModelTest {

	private VCardAddressModel instance;

	public VCardAddressModelTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		instance = new VCardAddressModel();
	}

	@After
	public void tearDown() {
		instance = null;
	}

	/**
	 * Test of getStreet method, of class VCardAddressModel.
	 */


	/**
	 * Test of setStreet method, of class VCardAddressModel.
	 */


	/**
	 * Test of getLocality method, of class VCardAddressModel.
	 */


	/**
	 * Test of setLocality method, of class VCardAddressModel.
	 */


	/**
	 * Test of getRegion method, of class VCardAddressModel.
	 */


	/**
	 * Test of setRegion method, of class VCardAddressModel.
	 */


	/**
	 * Test of getPostalCode method, of class VCardAddressModel.
	 */


	/**
	 * Test of setPostalCode method, of class VCardAddressModel.
	 */


	/**
	 * Test of getCountryName method, of class VCardAddressModel.
	 */


	/**
	 * Test of setCountryName method, of class VCardAddressModel.
	 */


	/**
	 * Test of getLabel method, of class VCardAddressModel.
	 */


	/**
	 * Test of setLabel method, of class VCardAddressModel.
	 */


	/**
	 * Test of clearFields method, of class VCardAddressModel.
	 */


	/**
	 * Test of addType method, of class VCardAddressModel.
	 */


	/**
	 * Test of removeType method, of class VCardAddressModel.
	 */


	/**
	 * Test of getTypeParameters method, of class VCardAddressModel.
	 */


	/**
	 * Test of clearTypes method, of class VCardAddressModel.
	 */


	/**
	 * Test of clear method, of class VCardAddressModel.
	 */
	@Test
	public void testClear() {
		System.out.println("clear");
		VCardAddressModel instance = new VCardAddressModel();
		instance.setStreet("Fifth Avenue");
		instance.setLocality("Big Apple");

		assertFalse(instance.isEmpty());
		instance.clear();
		assertTrue(instance.isEmpty());
	}

	/**
	 * Test of isEmpty method, of class VCardAddressModel.
	 */
	@Test
	public void testIsClear() {
		System.out.println("isClear");
		boolean result = instance.isEmpty();
		assertTrue(result);

		instance.setStreet("Fifth Avenue");
		instance.setLocality("Big Apple");
		assertFalse(instance.isEmpty());
	}

	/**
	 * Test of setFromPropertyEntry method, of class VCardAddressModel.
	 */
	@Test
	public void testSetFromPropertyEntry() {
		System.out.println("setFromPropertyEntry");
		final String street = "Lange Zeile 1";
		final String locality = "Erlangen";
		final String locality2 = "Sieglitzhof";
		final String code = "91052";
		final String label = "Dr. Martina Musterfrau\nInstitute of Computer Science: 1A\nLange Zeile 1\n91052 Erlangen";

		AdrProperty property = AdrProperty.Builder.newInstance().street(street).locality(locality).locality(locality2).code(code).label(label).build();
		instance.setFromPropertyEntry(property);
		assertEquals(street, instance.getStreet());
		assertEquals(locality + "," + locality2, instance.getLocality());
		assertEquals(code, instance.getPostalCode());
		assertEquals(label, instance.getLabel());
	}

	/**
	 * Test of getPropertyEntries method, of class VCardAddressModel.
	 */

}
