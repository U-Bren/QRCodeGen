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

import java.util.EnumSet;
import java.util.Set;
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
public class ListTypeTest {

	private Set<TypeParameter> parameter;
	private Set<TypeParameter> emptyParameter;
	private Set<TypeParameter> oneParameter;

	public ListTypeTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		parameter = EnumSet.of(TypeParameter.HOME, TypeParameter.WORK);
		emptyParameter = EnumSet.noneOf(TypeParameter.class);
		oneParameter = EnumSet.of(TypeParameter.VIDEO);
	}

	@After
	public void tearDown() {
		parameter = null;
	}

	@Test
	public void testParameterListAsString() {
		String result = ListType.PARAMETER_LIST.asString(parameter);
		String expResult = ";TYPE=work;TYPE=home";
		assertEquals(expResult, result);
	}

	@Test
	public void testValueListAsString() {
		String result = ListType.VALUE_LIST.asString(parameter);
		String expResult = ";TYPE=work,home";
		assertEquals(expResult, result);
	}

	@Test
	public void testParameterListAsStringOneParameter() {
		String result = ListType.PARAMETER_LIST.asString(oneParameter);
		String expResult = ";TYPE=video";
		assertEquals(expResult, result);
	}

	@Test
	public void testValueListAsStringOneParameter() {
		String result = ListType.VALUE_LIST.asString(oneParameter);
		String expResult = ";TYPE=video";
		assertEquals(expResult, result);
	}

	@Test
	public void testParameterListAsStringEmptyParameter() {
		String result = ListType.PARAMETER_LIST.asString(emptyParameter);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testValueListAsStringEmptyParameter() {
		String result = ListType.VALUE_LIST.asString(emptyParameter);
		assertTrue(result.isEmpty());
	}
}
