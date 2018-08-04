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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class OrgPropertyTest {

	private static final String VALID_ORGANIZATION_NAME_1 = "ABC, Inc.";
	private static final String VALID_UNIT_NAMES_1 = "North American Dev.;Marketing";
	private static final String[] VALID_UNIT_NAMES_1_ARRAY = {"North American Dev.", "Marketing"};
	private static final String VALID_AS_STRING_1 = "ABC\\, Inc.;North American Dev.;Marketing";
	private static final String VALID_EMPTY_ORGANIZATION_NAME = "";
	private OrgProperty validOrgProperty1;
	private OrgProperty emptyOrgProperty;

	public OrgPropertyTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {

		OrgProperty.Builder orgBuilder = new OrgProperty.Builder(VALID_ORGANIZATION_NAME_1);
		orgBuilder.unitNames(VALID_UNIT_NAMES_1_ARRAY);
		validOrgProperty1 = orgBuilder.build();

		orgBuilder = new OrgProperty.Builder(VALID_EMPTY_ORGANIZATION_NAME);
		emptyOrgProperty = orgBuilder.build();

	}

	@After
	public void tearDown() {
		validOrgProperty1 = null;
		emptyOrgProperty = null;
	}


	/**
	 * Test of getOrganization method, of class OrgProperty.
	 */
	@Test
	public void testGetOrganization() {

		OrgProperty.Builder orgBuilder = new OrgProperty.Builder(VALID_ORGANIZATION_NAME_1);
		orgBuilder.unitName(VALID_UNIT_NAMES_1);
		OrgProperty org = orgBuilder.build();
		assertEquals(VALID_ORGANIZATION_NAME_1 + ";" + VALID_UNIT_NAMES_1, org.getOrganization());
	}

	/**
	 * Test of getOrganization method, of class OrgProperty.
	 */
	@Test
	public void testGetOrganization2() {

		OrgProperty.Builder orgBuilder = new OrgProperty.Builder(VALID_ORGANIZATION_NAME_1);
		orgBuilder.unitNames(VALID_UNIT_NAMES_1_ARRAY);
		OrgProperty org = orgBuilder.build();
		assertEquals(VALID_ORGANIZATION_NAME_1 + ";" + VALID_UNIT_NAMES_1, org.getOrganization());
	}

	/**
	 * Test of getOrganization method, of class OrgProperty.
	 */
	@Test
	public void testGetEmptyOrganization() {

		OrgProperty.Builder orgBuilder = new OrgProperty.Builder(VALID_EMPTY_ORGANIZATION_NAME);
		OrgProperty org = orgBuilder.build();
		assertEquals(VALID_EMPTY_ORGANIZATION_NAME, org.getOrganization());
	}

	/**
	 * Test of getOrganization method, of class OrgProperty.
	 */
	@Test
	public void testGetValueAsString() {

		OrgProperty.Builder orgBuilder = new OrgProperty.Builder(VALID_ORGANIZATION_NAME_1);
		orgBuilder.unitNames(VALID_UNIT_NAMES_1_ARRAY);
		OrgProperty org = orgBuilder.build();
		assertEquals(VALID_AS_STRING_1, org.getValueAsString());



	}

	@Test
	public void testHasOrganizationName() {
		System.out.println("hasOrganizationName");

		boolean expResult = true;
		boolean result = validOrgProperty1.hasOrganizationName();

		assertEquals(expResult, result);

		expResult = true;
		result = emptyOrgProperty.hasOrganizationName();

		assertEquals(expResult, result);

	}

	@Test
	public void testHasUnitNames() {
		System.out.println("hasUnitNames");

		boolean expResult = true;
		boolean result = validOrgProperty1.hasUnitNames();

		assertEquals(expResult, result);

		expResult = false;
		result = emptyOrgProperty.hasUnitNames();

		assertEquals(expResult, result);
	}

	@Test
	public void testGetOrganizationName() {
		System.out.println("getOrganizationName");

		String expResult = VALID_ORGANIZATION_NAME_1;
		String result = validOrgProperty1.getOrganizationName();

		assertEquals(expResult, result);

		expResult = VALID_EMPTY_ORGANIZATION_NAME;
		result = emptyOrgProperty.getOrganizationName();

		assertEquals(expResult, result);

	}

	@Test
	public void testGetUnitNames() {
		System.out.println("getUnitNames");

		List<String> expResult = Arrays.asList(VALID_UNIT_NAMES_1_ARRAY);
		List<String> result = validOrgProperty1.getUnitNames();

		assertEquals(expResult, result);

		expResult = Collections.emptyList();
		result = emptyOrgProperty.getUnitNames();

		assertEquals(expResult, result);

	}

	@Test
	public void testGetUnitName() {
		System.out.println("getUnitName");

		for (int i = 0; i < VALID_UNIT_NAMES_1_ARRAY.length; i++) {
			String expResult = VALID_UNIT_NAMES_1_ARRAY[i];
			String result = validOrgProperty1.getUnitName(i);
			assertEquals(expResult, result);
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testGetUnitNameIllegalStateException() {
		System.out.println("getUnitNameIllegalStateException");


		String result = emptyOrgProperty.getUnitName(1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetUnitNameException() {
		System.out.println("getUnitNameException");


		String result = validOrgProperty1.getUnitName(3);
	}
}
