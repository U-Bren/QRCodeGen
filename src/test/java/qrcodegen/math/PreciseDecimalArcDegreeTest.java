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
public class PreciseDecimalArcDegreeTest {

	private Degree d_90_0_0;
	private Degree d_90_30_30;
	private Degree d_3;
	private Degree d_4;
	private Degree d_5;

	public PreciseDecimalArcDegreeTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		d_90_0_0 = new PreciseDecimalArcDegree(90.0);
		d_90_30_30 = new PreciseDecimalArcDegree(90.508333333333);
		d_3 = new PreciseDecimalArcDegree(40.71872);
		d_4 = new PreciseDecimalArcDegree(-73.98905);
		d_5 = new PreciseDecimalArcDegree(51.2345);
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
	public void shouldReturnDegree() {
		int expectedResult = -73;
		int actualResult = d_4.getDegree();
		assertThat(actualResult, equalTo(expectedResult));

		expectedResult = 40;
		actualResult = d_3.getDegree();
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
	public void shouldCalculateMinuteAndSecondsWithHighPrecision() {
		int exptectedDegree = 51;
		int expectedMinute = 14;
		double expectedSecond = 4.2;

		int actualDegree = d_5.getDegree();
		int actualMinute = d_5.getMinute();
		double actualSecond = d_5.getDecimalSecond();

		assertThat(actualDegree, equalTo(exptectedDegree));
		assertThat(actualMinute, equalTo(expectedMinute));
		assertThat(actualSecond, equalTo(expectedSecond));
	}

	@Test
	public void shouldReturnFormattedToString() {
		String expectedResult = "51.2345 == 51° 14' 4.2''";
		String result = d_5.toString();
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void shouldImplementEquals() {
		CommonTests.testEquals(d_3, d_3, true);
		CommonTests.testEquals(d_3, d_4, false);
		CommonTests.testEquals(d_4, d_3, false);
	}

	@Test
	public void shouldImplementComparison() {
		CommonTests.testForLessGreaterEqualTo(d_3, d_3, false, true, true, true, false);
		CommonTests.testForLessGreaterEqualTo(d_90_0_0, d_90_30_30, true, true, false, false, false);
		CommonTests.testForLessGreaterEqualTo(d_90_30_30, d_90_0_0, false, false, false, true, true);
	}
}
