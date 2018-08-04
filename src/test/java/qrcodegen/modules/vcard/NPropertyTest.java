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
public class NPropertyTest {

	private static final String CRLF = "\r\n";
	
	public NPropertyTest() {
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
	
	@Test
	public void testEmptyName() {
		doTest("", "", "", "", "", "N:;;;;" + CRLF);
	}

	@Test
	public void testName() {
		doTest("a", "b", "c", "d", "e", "N:a;b;c;d;e" + CRLF);
		doTest("a,a", "b;b", "c\\c", "d.d", "e e", "N:a\\,a;b\\;b;c\\\\c;d.d;e e" + CRLF);
	}
	
	private static void doTest(String famName, String givenName, String addName, String honPref, String honSuf, String expResult) {
		NProperty.Builder builder = NProperty.Builder.newInstance();
		builder.familyName(famName);
		builder.givenName(givenName);
		builder.additionalName(addName);
		builder.honorificPrefix(honPref);
		builder.honorificSuffix(honSuf);
		NProperty prop = builder.build();
		
		assertEquals(famName, prop.getLastName());
		assertEquals(givenName, prop.getFirstName());
		assertEquals(addName, prop.getAdditionalNames());
		assertEquals(honPref, prop.getHonorificPrefixes());
		assertEquals(honSuf, prop.getHonorificSuffixes());
		
		VCardProperty<NProperty> n = VCardProperty.newInstance(Property.N);
		n.addEntry(prop);
		assertEquals(expResult, n.asString());
	}
}
