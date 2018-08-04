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
public class PreciseArcDegreeTest {

	private Degree d_90_0_0;
	private Degree d_90_30_30;
	private Degree d_3;
	private Degree d_4;
	private Degree d_5;

	public PreciseArcDegreeTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		d_90_0_0 = new PreciseArcDegree(90, 0, 0.0);
		d_90_30_30 = new PreciseArcDegree(90, 30, 30);
		d_3 = new PreciseArcDegree(40, 43, 7.392);
		d_4 = new PreciseArcDegree(-73, 59, 20.58);
		d_5 = new PreciseArcDegree(51, 14, 4.2);
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
	public void shouldReturnMinute() {
		int expectedResult = 43;
		int actualResult = d_3.getMinute();
		assertThat(actualResult, equalTo(expectedResult));

		expectedResult = 59;
		actualResult = d_4.getMinute();
		assertThat(actualResult, equalTo(expectedResult));
	}

	@Test
	public void shouldReturnDecimalSecond() {
		double expectedResult = 7.392;
		double actualResult = d_3.getDecimalSecond();
		assertEquals(expectedResult, actualResult, 0.0);

		expectedResult = 20.58;
		actualResult = d_4.getDecimalSecond();
		assertEquals(expectedResult, actualResult, 0.0);
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
	public void shouldCalculateValueWithHighPrecision() {
		double expectedResult = 51.2345;
		double actualResult = d_5.getValue();

		assertEquals(expectedResult, actualResult, 0.0);
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
		CommonTests.testEquals(new PreciseArcDegree(10, 0, 0), new PreciseArcDegree(40, 0, 0), false);
		CommonTests.testEquals(new PreciseArcDegree(0, 20, 0), new PreciseArcDegree(0, 40, 0), false);
		CommonTests.testEquals(new PreciseArcDegree(0, 0, 30), new PreciseArcDegree(0, 0, 40), false);
	}

	@Test
	public void shouldImplementComparison() {
		CommonTests.testForLessGreaterEqualTo(d_3, d_3, false, true, true, true, false);
		CommonTests.testForLessGreaterEqualTo(d_90_0_0, d_90_30_30, true, true, false, false, false);
		CommonTests.testForLessGreaterEqualTo(d_90_30_30, d_90_0_0, false, false, false, true, true);
	}
}
