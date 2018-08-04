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
public class DateTest {

	private final int[][] year_zero = {{0, 1, 1}};
	private final String[] year_zeroResult = {"00000101"};
	private final int[][] data = {{2012, 6, 25}, {1, 1, 1}, {9999, 12, 31}, {2012, 02, 29}};
	private final String[] dataResult = {"20120625", "00010101", "99991231", "20120229"};
	private final int[][] yearOnly = {{2012, -1, -1}, {0, -1, -1}, {9999, -1, -1}};
	private final String[] yearOnlyResult = {"2012", "0000", "9999"};
	private final int[][] monthOnly = {{-1, 1, -1}, {-1, 12, -1}};
	private final String[] monthOnlyResult = {"--01", "--12"};
	private final int[][] dayOnly = {{-1, -1, 1}, {-1, -1, 31}};
	private final String[] dayOnlyResult = {"----01", "----31"};
	private final int[][] yearMonth = {{0, 1, -1}, {9999, 12, -1}};
	private final String[] yearMonthResult = {"000001", "999912"};
	private final int[][] monthDay = {{-1, 1, 1}, {-1, 12, 31}, {-1, 2, 29}, {-1, 4, 30}};
	private final String[] monthDayResult = {"--0101", "--1231", "--0229", "--0430"};
	private final int[][] illegalData = {{2011, 02, 29}, {2012, -1, 1}, {-1, -1, -1}, {-1, 2, 30}, {10000, -1, -1}, {-1, 13, -1}, {-1, -1, 32}};

	public DateTest() {
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

	/**
	 * Test of getValueAsString method, of class Date.
	 */
	@Test
	public void testGetValueAsString() {
		System.out.println("getValueAsString");

		check(data, dataResult);
		check(yearOnly, yearOnlyResult);
		check(monthOnly, monthOnlyResult);
		check(dayOnly, dayOnlyResult);
		check(yearMonth, yearMonthResult);
		check(monthDay, monthDayResult);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetValueAsStringYearZero() {
		System.out.println("getValueAsStringYearZero");
		// TODO VCard date allows year zero, but not the Calendar we use
		// to check it (there is only 1 BC and AD 1, but no year 0)
		check(year_zero, year_zeroResult);
	}

	private static void check(int[][] data, String[] dataResult) {
		for (int i = 0; i < data.length; i++) {
			int[] c = data[i];
			Date instance = new Date(c[0], c[1], c[2]);
			String expResult = dataResult[i];
			String result = instance.getValueAsString();
			assertEquals(expResult, result);
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor() {
		System.out.println("constructor");
		for (int[] i : illegalData) {
			Date instance = new Date(i[0], i[1], i[2]);
		}
	}

	/**
	 * Test of elements method, of class Date.
	 */


	/**
	 * Test of getYear method, of class Date.
	 */


	/**
	 * Test of getMonth method, of class Date.
	 */


	/**
	 * Test of getDay method, of class Date.
	 */

}
