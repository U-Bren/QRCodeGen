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
package qrcodegen.modules;

import java.awt.Dimension;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
//import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Stefan Ganzer
 */
public class ScaleToPaperSizeTest {

	public ScaleToPaperSizeTest() {
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
	public void noneShouldReturn1_0ForAllInputs() {
		Dimension from = new Dimension(100, 50);
		Dimension to = new Dimension(30, 30);
		double expectedScaleFactor = 1.0;
		double actual = ScaleToPaperSize.NONE.getScaleFactor(from, to);
		assertThat(actual, equalTo(expectedScaleFactor));
	}

	@Test
	public void fitShouldReturn1_0ForFromEqualTo() {
		Dimension from = new Dimension(100, 100);
		Dimension to = new Dimension(100, 100);
		double expectedScaleFactor = 1.0;
		double actual = ScaleToPaperSize.FIT.getScaleFactor(from, to);
		assertThat(actual, equalTo(expectedScaleFactor));
	}

	@Test
	public void fitShouldScaleDownForFromBiggerThanTo() {
		Dimension from = new Dimension(100, 90);
		Dimension to = new Dimension(50, 50);
		double expectedScaleFactor = 0.5;
		double actual = ScaleToPaperSize.FIT.getScaleFactor(from, to);
		double delta = 0.001;
		//assertThat(actual, equalTo());
		assertEquals(expectedScaleFactor, actual, delta);
	}

	@Test
	public void fitShouldScaleUpForFromSmallerThanTo() {
		Dimension from = new Dimension(50, 90);
		Dimension to = new Dimension(100, 100);
		double expectedScaleFactor = 1.1112;
		double actual = ScaleToPaperSize.FIT.getScaleFactor(from, to);
		double delta = 0.001;
		//assertThat(actual, equalTo());
		assertEquals(expectedScaleFactor, actual, delta);
	}

	@Test
	public void downShouldReturn1_0ForFromEqualTo() {
		Dimension from = new Dimension(100, 100);
		Dimension to = new Dimension(100, 100);
		double expectedScaleFactor = 1.0;
		double actual = ScaleToPaperSize.DOWN.getScaleFactor(from, to);
		assertThat(actual, equalTo(expectedScaleFactor));
	}

	@Test
	public void downShouldReturnNot1_0ForFromNotEqualTo() {
		Dimension from = new Dimension(100, 90);
		Dimension to = new Dimension(50, 50);
		double expectedScaleFactor = 0.5;
		double actual = ScaleToPaperSize.DOWN.getScaleFactor(from, to);
		double delta = 0.001;
		//assertThat(actual, equalTo());
		assertEquals(expectedScaleFactor, actual, delta);
	}

	@Test
	public void downShouldReturn1_0ForFromSmallerThanTo() {
		Dimension from = new Dimension(100, 90);
		Dimension to = new Dimension(100, 100);
		double expectedScaleFactor = 1.0;
		double actual = ScaleToPaperSize.DOWN.getScaleFactor(from, to);
		double delta = 0.001;
		//assertThat(actual, equalTo());
		assertEquals(expectedScaleFactor, actual, delta);
	}

	@Test
	public void upShouldReturn1_0ForFromEqualTo() {
		Dimension from = new Dimension(100, 100);
		Dimension to = new Dimension(100, 100);
		double expectedScaleFactor = 1.0;
		double actual = ScaleToPaperSize.UP.getScaleFactor(from, to);
		assertThat(actual, equalTo(expectedScaleFactor));
	}

	@Test
	public void upShouldReturnNot1_0ForFromNotEqualTo() {
		Dimension from = new Dimension(50, 50);
		Dimension to = new Dimension(100, 90);
		double expectedScaleFactor = 1.8;
		double actual = ScaleToPaperSize.UP.getScaleFactor(from, to);
		double delta = 0.001;
		//assertThat(actual, equalTo());
		assertEquals(expectedScaleFactor, actual, delta);
	}

	@Test
	public void downShouldReturn1_0ForFromBiggerThanTo() {
		Dimension from = new Dimension(100, 90);
		Dimension to = new Dimension(50, 50);
		double expectedScaleFactor = 1.0;
		double actual = ScaleToPaperSize.UP.getScaleFactor(from, to);
		double delta = 0.001;
		//assertThat(actual, equalTo());
		assertEquals(expectedScaleFactor, actual, delta);
	}
}
