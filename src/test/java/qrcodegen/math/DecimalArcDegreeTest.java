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
import static org.hamcrest.number.OrderingComparison.*;

/**
 *
 * @author Stefan Ganzer
 */
public class DecimalArcDegreeTest {

	private Degree d_90_0_0;
	private Degree d_90_30_30;
	private Degree d_3;
	private Degree d_4;
	private Degree d_5;

	public DecimalArcDegreeTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		d_90_0_0 = new DecimalArcDegree(90.0);
		d_90_30_30 = new DecimalArcDegree(90.50833333);
		d_3 = new DecimalArcDegree(40.71872);
		d_4 = new DecimalArcDegree(-73.98905);
		d_5 = new DecimalArcDegree(51.2345);

	}

	@After
	public void tearDown() {
		d_90_0_0 = null;
		d_90_30_30 = null;
		d_3 = null;
		d_4 = null;
		d_5 = null;
	}

	@Test
	public void getterShouldReturnExpectedValues() {
		doTestGetter(d_90_0_0, 90, 0, 0, 0.0, 90.0, 0.0);
		doTestGetter(d_90_30_30, 90, 30, 30, 0.0001, 90.50833333, 0.0);
	}

	private static void doTestGetter(Degree degree, int expectedDegree, int expectedMinute, double expectedSecond, double deltaSecond, double expectedDecimalDegree, double deltaDegree) {
		assertThat(degree.getDegree(), equalTo(expectedDegree));
		assertThat(degree.getMinute(), equalTo(expectedMinute));
		assertEquals(expectedSecond, degree.getDecimalSecond(), deltaSecond);
		assertEquals(expectedDecimalDegree, degree.getValue(), deltaDegree);
	}

	@Test
	public void equalsShouldReturnTrueForEqualValues() {
		DecimalArcDegree ad_1 = (DecimalArcDegree) d_90_30_30;
		DecimalArcDegree ad_2 = new DecimalArcDegree(90.50833333);
		boolean isEqual = ad_1.equals(ad_2);
		assertTrue(isEqual);
	}

	@Test
	public void equalsShouldReturnFalseForDifferentValues() {
		DecimalArcDegree ad_1 = (DecimalArcDegree) d_90_30_30;
		DecimalArcDegree ad_2 = new DecimalArcDegree(90.508333333);
		boolean isEqual = ad_1.equals(ad_2);
		assertFalse(isEqual);
	}

	@Test
	public void shouldReturnSameHashCodeForEqualInstances() {
		DecimalArcDegree ad_1 = (DecimalArcDegree) d_90_30_30;
		DecimalArcDegree ad_2 = new DecimalArcDegree(90.50833333);
		boolean isEqual = ad_1.equals(ad_2);
		assertTrue(isEqual);

		int hash1 = ad_1.hashCode();
		int hash2 = ad_2.hashCode();

		assertThat(hash1, equalTo(hash2));
	}

	@Test
	public void shouldReturnFormattedToString() {
		String expectedResult = "90.0 == 90° 0' 0.0''";
		String result = d_90_0_0.toString();
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void shouldReturnNegativeValueForComparingToBiggerDegree() {
		DecimalArcDegree ad1 = (DecimalArcDegree) d_90_0_0;
		DecimalArcDegree ad2 = (DecimalArcDegree) d_90_30_30;
		int expectedResult = -1;
		int actualResult = ad1.compareTo(ad2);
		assertThat(actualResult, equalTo(expectedResult));
	}

	@Test
	public void shouldReturnPositiveValueForComparingToSmallerDegree() {
		DecimalArcDegree ad1 = (DecimalArcDegree) d_90_0_0;
		DecimalArcDegree ad2 = (DecimalArcDegree) d_90_30_30;
		int expectedResult = 1;
		int actualResult = ad2.compareTo(ad1);
		assertThat(actualResult, equalTo(expectedResult));
	}

	@Test
	public void shouldReturnZeroForComparingToEqualDegree() {
		DecimalArcDegree ad1 = new DecimalArcDegree(90.50833333);
		DecimalArcDegree ad2 = (DecimalArcDegree) d_90_30_30;
		int expectedResult = 0;
		int actualResult = ad1.compareTo(ad2);
		assertThat(actualResult, equalTo(expectedResult));
	}

	@Test
	public void shouldReturnPositiveMinuteBetweenZeroAndSixty() {
		int result = d_3.getMinute();
		assertThat(result, greaterThanOrEqualTo(Integer.valueOf(0)));
		assertThat(result, lessThan(Integer.valueOf(60)));

		result = d_4.getMinute();
		assertThat(result, greaterThanOrEqualTo(Integer.valueOf(0)));
		assertThat(result, lessThan(Integer.valueOf(60)));

		result = d_5.getMinute();
		assertThat(result, greaterThanOrEqualTo(Integer.valueOf(0)));
		assertThat(result, lessThan(Integer.valueOf(60)));
	}

	@Test
	public void shouldReturnPositiveSecondBetweenZeroAndSixty() {
		double result = d_3.getDecimalSecond();
		assertThat(result, greaterThanOrEqualTo(Double.valueOf(0)));
		assertThat(result, lessThan(Double.valueOf(60)));

		result = d_4.getDecimalSecond();
		assertThat(result, greaterThanOrEqualTo(Double.valueOf(0)));
		assertThat(result, lessThan(Double.valueOf(60)));

		result = d_5.getDecimalSecond();
		assertThat(result, greaterThanOrEqualTo(Double.valueOf(0)));
		assertThat(result, lessThan(Double.valueOf(60)));
	}

	@Test
	public void shouldImplementEquals() {
		CommonTests.testEquals(d_90_0_0, d_90_0_0, true);
		CommonTests.testEquals(d_90_0_0, d_90_30_30, false);
		CommonTests.testEquals(d_90_30_30, d_90_0_0, false);
	}

	@Test
	public void shouldImplementComparison() {
		CommonTests.testForLessGreaterEqualTo(d_90_30_30, d_90_30_30, false, true, true, true, false);
		CommonTests.testForLessGreaterEqualTo(d_90_0_0, d_90_30_30, true, true, false, false, false);
		CommonTests.testForLessGreaterEqualTo(d_90_30_30, d_90_0_0, false, false, false, true, true);
	}
}