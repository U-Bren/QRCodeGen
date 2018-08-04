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

package qrcodegen.modules;

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
import qrcodegen.math.PreciseDecimalArcDegree;
import qrcodegen.uri.GeoURI;

/**
 *
 * @author Stefan Ganzer
 */
public class GeoURIModelTest {
	
	private GeoURIModel model;

    public GeoURIModelTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
		model = new GeoURIModel();
    }

    @After
    public void tearDown() {
    }

	@Test
	public void getZoomQueryShouldReturnEmptyStringIfZoomIsNotSet(){
		String expectedResult = "";
		String actualResult = model.getZoomQuery();
		assertThat(actualResult, equalTo(expectedResult));
	}
	
	@Test
	public void getZoomQueryShouldReturnZoomLevelIfZoomIsSet1(){
		model.setZoom(1);
		
		String expectedResult = "?z=1";
		String actualResult = model.getZoomQuery();
		assertThat(actualResult, equalTo(expectedResult));
	}
	
	@Test
	public void getZoomQueryShouldReturnZoomLevelIfZoomIsSet23(){
		model.setZoom(23);
		
		String expectedResult = "?z=23";
		String actualResult = model.getZoomQuery();
		assertThat(actualResult, equalTo(expectedResult));
	}
	
	@Test
	public void getGeoURIShouldReturnGeoURIWithCorrectValues(){
		double latitude = 89.5;
		double longitude = 175.6;
		double altitude = 10;
		
		model.setDecimalLatitude(latitude);
		model.setDecimalLongitude(longitude);
		model.setAltitude(altitude);
		
		GeoURI expectedResult = new GeoURI(new PreciseDecimalArcDegree(latitude), new PreciseDecimalArcDegree(longitude), altitude);
		GeoURI actualResult = model.getGeoURI();
		
		assertThat(actualResult, equalTo(expectedResult));	
	}
	
		@Test
	public void getExtendedGeoURIAsStringShouldReturnCorrectValues(){
		double latitude = 89.5;
		double longitude = 175.6;
		double altitude = 10;
		int zoom = 3;
		
		model.setDecimalLatitude(latitude);
		model.setDecimalLongitude(longitude);
		model.setAltitude(altitude);
		model.setZoom(zoom);
		
		String expectedResult = "geo:89.5,175.6,10?z=3";
		String actualResult = model.getExtendedGeoURIAsString();
		
		assertThat(actualResult, equalTo(expectedResult));	
	}

}