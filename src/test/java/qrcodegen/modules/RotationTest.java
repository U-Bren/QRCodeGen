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
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Stefan Ganzer
 */
public class RotationTest {

	final int width = 100;
	final int height = 70;

	public RotationTest() {
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
	public void shouldReturnUnchangedDimensionForR0() {

		Dimension dim = new Dimension(width, height);
		Dimension expectedDim = new Dimension(width, height);
		Dimension actualDim = Rotation.R0.getDimension(dim);
		assertThat(actualDim, equalTo(expectedDim));
	}

	@Test
	public void shouldReturnDimensionForR90() {

		Dimension dim = new Dimension(width, height);
		Dimension expectedDim = new Dimension(height, width);
		Dimension actualDim = Rotation.R90.getDimension(dim);
		assertThat(actualDim, equalTo(expectedDim));
	}

	@Test
	public void shouldReturnUnchangedDimensionForR180() {

		Dimension dim = new Dimension(width, height);
		Dimension expectedDim = new Dimension(width, height);
		Dimension actualDim = Rotation.R180.getDimension(dim);
		assertThat(actualDim, equalTo(expectedDim));
	}

	@Test
	public void shouldReturnDimensionForR270() {

		Dimension dim = new Dimension(width, height);
		Dimension expectedDim = new Dimension(height, width);
		Dimension actualDim = Rotation.R270.getDimension(dim);
		assertThat(actualDim, equalTo(expectedDim));
	}

	@Test
	public void shouldRotate90DegreeFor0To90Degree() {
		Rotation start = Rotation.R0;
		Rotation end = Rotation.R90;
		Rotation expected = Rotation.R90;
		Rotation actual = start.rotateTo(end);
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void shouldRotate180DegreeFor270To90Degree() {
		Rotation start = Rotation.R270;
		Rotation end = Rotation.R90;
		Rotation expected = Rotation.R180;
		Rotation actual = start.rotateTo(end);
		assertThat(actual, equalTo(expected));
	}

	@Test
	public void shouldRotate0DegreeFor180To180Degree() {
		Rotation start = Rotation.R180;
		Rotation end = Rotation.R180;
		Rotation expected = Rotation.R0;
		Rotation actual = start.rotateTo(end);
		assertThat(actual, equalTo(expected));
	}
}
