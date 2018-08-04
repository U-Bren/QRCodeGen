/*
 * Copyright (C) 2013 Stefan Ganzer
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
package qrcodegen.math;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Stefan Ganzer
 */
public class ArcDegreeTest {

	private Degree d_90_0_0;
	private Degree d_90_30_30;

	public ArcDegreeTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		d_90_0_0 = new ArcDegree(90, 0, 0.0);
		d_90_30_30 = new ArcDegree(90, 30, 30.0);
	}

	@After
	public void tearDown() {
		d_90_0_0 = null;
		d_90_30_30 = null;
	}

	@Test
	public void getterShouldReturnExpectedValues() {
		doTestGetter(d_90_0_0, 90, 0, 0, 0.0, 90.0, 0.0001);
		doTestGetter(d_90_30_30, 90, 30, 30, 0.0001, 90.50833333, 0.0001);
	}

	private static void doTestGetter(Degree degree, int expectedDegree, int expectedMinute, double expectedSecond, double deltaSecond, double expectedDecimalDegree, double deltaDegree) {
		assertThat(degree.getDegree(), equalTo(expectedDegree));
		assertThat(degree.getMinute(), equalTo(expectedMinute));
		assertEquals(expectedSecond, degree.getDecimalSecond(), deltaSecond);
		assertEquals(expectedDecimalDegree, degree.getValue(), deltaDegree);
	}

	@Test
	public void equalsShouldReturnTrueForEqualValues() {
		Degree ad_1 = d_90_30_30;
		Degree ad_2 = new ArcDegree(90, 30, 30.0);
		boolean isEqual = ad_1.equals(ad_2);
		assertTrue(isEqual);
	}

	@Test
	public void equalsShouldReturnFalseForDifferentValues() {
		Degree ad_1 = d_90_30_30;
		Degree ad_2 = new ArcDegree(90, 30, 30.1);
		boolean isEqual = ad_1.equals(ad_2);
		assertFalse(isEqual);
	}

	@Test
	public void shouldReturnSameHashCodeForEqualInstances() {
		Degree ad_1 = d_90_30_30;
		Degree ad_2 = new ArcDegree(90, 30, 30.0);
		boolean isEqual = ad_1.equals(ad_2);
		assertTrue(isEqual);

		int hash1 = ad_1.hashCode();
		int hash2 = ad_2.hashCode();

		assertThat(hash1, equalTo(hash2));
	}

	@Test
	public void shouldReturnFormattedToString() {
		String expectedResult = "90.50833333333334 == 90° 30' 30.0''";
		String result = d_90_30_30.toString();
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void shouldReturnNegativeValueForComparingToBiggerDegree() {
		ArcDegree ad1 = (ArcDegree) d_90_0_0;
		ArcDegree ad2 = (ArcDegree) d_90_30_30;
		int expectedResult = -1;
		int actualResult = ad1.compareTo(ad2);
		assertThat(actualResult, equalTo(expectedResult));
	}

	@Test
	public void shouldReturnPositiveValueForComparingToSmallerDegree() {
		ArcDegree ad1 = (ArcDegree) d_90_0_0;
		ArcDegree ad2 = (ArcDegree) d_90_30_30;
		int expectedResult = 1;
		int actualResult = ad2.compareTo(ad1);
		assertThat(actualResult, equalTo(expectedResult));
	}

	@Test
	public void shouldReturnZeroForComparingToEqualDegree() {
		ArcDegree ad1 = new ArcDegree(90, 30, 30.000);
		ArcDegree ad2 = (ArcDegree) d_90_30_30;
		int expectedResult = 0;
		int actualResult = ad1.compareTo(ad2);
		assertThat(actualResult, equalTo(expectedResult));
	}

	@Test
	public void shouldImplementEquals() {
		CommonTests.testEquals(d_90_0_0, d_90_0_0, true);
		CommonTests.testEquals(d_90_0_0, d_90_30_30, false);
		CommonTests.testEquals(d_90_30_30, d_90_0_0, false);
		CommonTests.testEquals(new ArcDegree(10, 0, 0), new ArcDegree(40, 0, 0), false);
		CommonTests.testEquals(new ArcDegree(0, 20, 0), new ArcDegree(0, 40, 0), false);
		CommonTests.testEquals(new ArcDegree(0, 0, 30), new ArcDegree(0, 0, 40), false);
	}

	@Test
	public void shouldImplementComparison() {
		CommonTests.testForLessGreaterEqualTo(d_90_30_30, d_90_30_30, false, true, true, true, false);
		CommonTests.testForLessGreaterEqualTo(d_90_0_0, d_90_30_30, true, true, false, false, false);
		CommonTests.testForLessGreaterEqualTo(d_90_30_30, d_90_0_0, false, false, false, true, true);
	}
}