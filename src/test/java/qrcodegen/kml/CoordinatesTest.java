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

package qrcodegen.kml;

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
public class CoordinatesTest {

    public CoordinatesTest() {
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
	public void shouldInterpretString(){
		String input = "-73.98905,40.71872,100";
		Coordinates c = new Coordinates(input);
		assertThat(c.getLongitude(),equalTo(Double.valueOf(-73.98905)));
		assertThat(c.getLatitude(), equalTo(Double.valueOf(40.71872)));
		assertThat(c.getAltitude(), equalTo(Double.valueOf(100)));
	}

	@Test
	public void shouldInterpretStringWithoutAltitude(){
		String input = "-73.98905,40.71872";
		Coordinates c = new Coordinates(input);
		assertThat(c.getLongitude(),equalTo(Double.valueOf(-73.98905)));
		assertThat(c.getLatitude(), equalTo(Double.valueOf(40.71872)));
		assertFalse(c.isAltitudeDefined());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfLessThanTwoComponents(){
		String input = "-73.98905";
		new Coordinates(input);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfMoreThanThreeComponents(){
		String input = "-73.98905,40.71872,100,95";
		new Coordinates(input);
	}
}