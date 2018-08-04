/*
 * Copyright (C) 2012 Stefan Ganzer
 *
 * This file is part of QRCodeGen.
 *
 * QRCodeGen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QRCodeGen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
public class KindPropertyTest {

	public KindPropertyTest() {
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
	 * Test of getValueAsString method, of class KindProperty.
	 */
	@Test
	public void testGetValueAsString() {
		KindProperty prop = new KindProperty.Builder().individual().build();
		assertEquals("individual", prop.getValueAsString());

		prop = new KindProperty.Builder().group().build();
		assertEquals("group", prop.getValueAsString());

		prop = new KindProperty.Builder().org().build();
		assertEquals("org", prop.getValueAsString());

		prop = new KindProperty.Builder().location().build();
		assertEquals("location", prop.getValueAsString());
	}
}
