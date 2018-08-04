/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.tools;

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
public class JCustomTextFieldTest {

	public JCustomTextFieldTest() {
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
	public void testConstruction() {
		System.out.println("construction");
		JCustomTextField instance = new JCustomTextField();
		assertTrue(instance.getText().isEmpty());
	}

	/**
	 * Test of setText method, of class JCustomTextField.
	 */
	@Test
	public void testSetText() {
		System.out.println("setText");
		String t = "abcdefg";
		JCustomTextField instance = new JCustomTextField();
		instance.setText(t);
		assertEquals(t, instance.getText());
	}
	
	@Test
	public void testSetNullText(){
		System.out.println("setNullText");
		String t = null;
		JCustomTextField instance = new JCustomTextField();
		instance.setText(t);
		assertTrue(instance.getText().isEmpty());		
	}

	/**
	 * Test of getOriginalText method, of class JCustomTextField.
	 */
	@Test
	public void testGetOriginalText() {
		System.out.println("getOriginalText");
		String text = "a text that is longer than clipLength";
		JCustomTextField instance = new JCustomTextField();
		instance.setText(text);
		instance.setClipLength(5);
		
		String expResult = text;
		String result = instance.getOriginalText();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setMenuText method, of class JCustomTextField.
	 */


	/**
	 * Test of getMenuText method, of class JCustomTextField.
	 */


	/**
	 * Test of setPopupMenuEnabled method, of class JCustomTextField.
	 */


	/**
	 * Test of isPopupMenuEnabled method, of class JCustomTextField.
	 */


	/**
	 * Test of setCopyMenuItemEnabled method, of class JCustomTextField.
	 */


	/**
	 * Test of setClipLength method, of class JCustomTextField.
	 */
	@Test
	public void testSetClipLength() {
		System.out.println("setClipLength");
		String text = "a text that is longer than clipLength";
		int maxLength = 6;
		JCustomTextField instance = new JCustomTextField();
		instance.setText(text);
		instance.setClipLength(maxLength);
		String expResult = "a text";
		String result = instance.getText();
		assertEquals(expResult, result);
		
		maxLength = text.length();
		instance.setClipLength(maxLength);
		expResult = text;
		result = instance.getText();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getClipLength method, of class JCustomTextField.
	 */


	/**
	 * Test of setHandleExportFailedException method, of class JCustomTextField.
	 */


	/**
	 * Test of getHandelExportFailedException method, of class JCustomTextField.
	 */


	/**
	 * Test of setExportFailedDialogTitle method, of class JCustomTextField.
	 */


	/**
	 * Test of getExportFailedDialogTitle method, of class JCustomTextField.
	 */


	/**
	 * Test of setExportFailedDialogMessage method, of class JCustomTextField.
	 */


	/**
	 * Test of getExportFailedDialogMessage method, of class JCustomTextField.
	 */

}
