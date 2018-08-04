/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcard;

import java.util.ArrayList;
import java.util.List;
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
public class VCardValueListTest {

	private VCardList<VCardValue> valueList;

	public VCardValueListTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		valueList = new VCardValueList<VCardValue>(new ArrayList<VCardValue>());
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of newArrayList method, of class VCardValueList.
	 */
	@Test
	public void testNewArrayList() {
		System.out.println("newArrayList");
		VCardList<VCardValue> result = VCardValueList.newArrayList();
		assertNotNull(result);
		// TODO review the generated test code and remove the default call to fail.
	}

	/**
	 * Test of delimiter method, of class VCardValueList.
	 */
	@Test
	public void testDelimiter() {
		System.out.println("delimiter");
		String expResult = ",";
		String result = valueList.delimiter();
		assertEquals(expResult, result);
	}
	/**
	 * Test of toString method, of class VCardArrayList.
	 */
	@Test
	public void testGetValueAsString() {
		System.out.println("getValueAsString");
		
		valueList.add(new VCardText("Mustermann"));
		valueList.add(new VCardText("Erika"));
		
		String expResult = "Mustermann,Erika";
		String result = valueList.getValueAsString();
		assertEquals(expResult, result);
	}

	@Test
	public void testGetValueAsStringEmptyList() {
		System.out.println("getValueAsStringEmptyList");
			
		String expResult = "";
		String result = valueList.getValueAsString();
		assertEquals(expResult, result);
	}}
