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
package qrcodegen.modules;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import qrcodegen.modules.Type;

/**
 *
 * @author Stefan Ganzer
 */
public class WirelessNetworkContentTest {

	private WirelessNetworkContent model;

	public WirelessNetworkContentTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		model = new WirelessNetworkContent();
	}

	@After
	public void tearDown() {
		model = null;
	}

	/**
	 * Test of setNetworkType method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of getNetworkType method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of setPassword method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of getPassword method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of getEscapedPassword method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of setPasswordType method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of getPasswordType method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of getActualPasswordType method, of class WirelessNetworkContent.
	 */
	@Test
	public void testGetActualPasswordType() {
		System.out.println("getActualPasswordType");

		assertEquals(Type.STRING, model.getPasswordType());
		model.setPassword("abcdef");
		assertEquals(Type.HEX, model.getActualPasswordType());

		model.setPassword("abcdefg");
		assertEquals(Type.STRING, model.getActualPasswordType());

		model.setPassword("");
		assertEquals(Type.HEX, model.getActualPasswordType());
	}

	/**
	 * Test of setSSID method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of getSSID method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of getEscapedSSID method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of setSsidType method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of getSsidType method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of getActualSsidType method, of class WirelessNetworkContent.
	 */
	@Test
	public void testGetActualSsidType() {
		System.out.println("getActualSsidType");

		assertEquals(Type.STRING, model.getSsidType());
		model.setSSID("abcdef");
		assertEquals(Type.HEX, model.getActualSsidType());

		model.setSSID("abcdefg");
		assertEquals(Type.STRING, model.getActualSsidType());

		model.setSSID("");
		assertEquals(Type.HEX, model.getActualSsidType());
	}

	@Test
	public void test() {
		doTest(Type.STRING, "mynetwork", Type.STRING, "mypass", NetworkType.WPA_WPA2, true,
				"WIFI:S:mynetwork;T:WPA;P:mypass;H:true;;");
	}

	@Test
	public void testNoPassword() {
		doTest(Type.STRING, "mynetwork", Type.STRING, "password will be omitted", NetworkType.NO_ENCRYPTION, false,
				"WIFI:S:mynetwork;;");
	}

	@Test
	public void testHexLikeInput() {
		doTest(Type.STRING, "abcdef", Type.STRING, "1234567890abcdef", NetworkType.WPA_WPA2, true,
				"WIFI:S:\"abcdef\";T:WPA;P:\"1234567890abcdef\";H:true;;");
	}

	@Test
	public void testHexInput() {
		doTest(Type.HEX, "abcdef", Type.HEX, "1234567890abcdef", NetworkType.WPA_WPA2, true,
				"WIFI:S:abcdef;T:WPA;P:1234567890abcdef;H:true;;");
	}

	@Test
	public void testAsciiInDQuote() {
		doTest(Type.STRING, "\"abcdef\"", Type.STRING, "password will be omitted", NetworkType.NO_ENCRYPTION, false,
				"WIFI:S:\\\"abcdef\\\";;");
	}

	@Test
	public void test1() {
		doTest(Type.STRING, "\"foo;bar\\baz\"", Type.STRING, "", NetworkType.NO_ENCRYPTION, false,
				"WIFI:S:\\\"foo\\;bar\\\\baz\\\";;");
	}

	@Test
	public void test2() {
		doTest(Type.STRING, "\"foo;bar\\baz,b:iz\"", Type.STRING, "", NetworkType.NO_ENCRYPTION, false,
				"WIFI:S:\\\"foo\\;bar\\\\baz\\,b\\:iz\\\";;");
	}

	private static void doTest(Type ssidType, String ssid, Type passwordType, String password, NetworkType type, boolean hidden, String expResult) {
		WirelessNetworkContent model = new WirelessNetworkContent(ssid, ssidType, password, passwordType, type, hidden);
		model.update();
		assertEquals(expResult, model.getContent());
		assertEquals(ssid, model.getSSID());
		assertEquals(password, model.getPassword());
		assertEquals(hidden, model.getIsHiddenNetwork());
		assertEquals(ssidType, model.getSsidType());
		assertEquals(passwordType, model.getPasswordType());
	}
	/**
	 * Test of update method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of getContent method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of escapeString method, of class WirelessNetworkContent.
	 */
	/**
	 * Test of addPropertyChangeListener method, of class
	 * WirelessNetworkContent.
	 */
	/**
	 * Test of addPropertyChangeListener method, of class
	 * WirelessNetworkContent.
	 */
}
