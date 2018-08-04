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

import qrcodegen.kml.KMLFileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.*;
import qrcodegen.kml.Kml;

/**
 *
 * @author Stefan Ganzer
 */
public class KMLFileReaderTest {

	private final Logger logger = Logger.getLogger(KMLFileReaderTest.class.getName());
	private final File path;

	public KMLFileReaderTest() throws URISyntaxException {
		path = new File(KMLFileReaderTest.class.getResource("testFiles").toURI());
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
	public void readZippedFileShouldReadZippedKMLFile() throws ZipException, FileNotFoundException, IOException {
		File input = new File(path, "Google.kmz");
		KMLFileReader reader = new KMLFileReader(input, 1024 * 1024, logger);

		reader.readZippedFile();
		Kml kml = reader.getKml();
		assertNotNull(kml);
	}

	@Test(expected = ZipException.class)
	public void readZippedFileShouldThrowZipExceptionOnUncompressedFile() throws ZipException, FileNotFoundException, IOException {
		File input = new File(path, "doc.kml");
		KMLFileReader reader = new KMLFileReader(input, 1024 * 1024, logger);

		reader.readZippedFile();
	}

	@Test
	public void readUncompressedFileShouldReadUncompressedKMLFile() throws FileNotFoundException, IOException {
		File input = new File(path, "doc.kml");
		KMLFileReader reader = new KMLFileReader(input, 1024 * 1024, logger);

		reader.readUncompressedFile();
		Kml kml = reader.getKml();
		assertNotNull(kml);
	}

	@Test(expected = IOException.class)
	public void readUncompressedFileShouldThrowIOExceptionOnZippedFile() throws FileNotFoundException, IOException {
		File input = new File(path, "Google.kmz");
		KMLFileReader reader = new KMLFileReader(input, 1024 * 1024, logger);

		reader.readUncompressedFile();
	}

	@Test
	public void readShouldReadZippedKMLFile() throws ZipException, FileNotFoundException, IOException {
		File input = new File(path, "Google.kmz");
		KMLFileReader reader = new KMLFileReader(input, 1024 * 1024, logger);

		reader.readFile();
		Kml kml = reader.getKml();
		assertNotNull(kml);
	}

	@Test
	public void readShouldReadUncompressedKMLFile() throws ZipException, FileNotFoundException, IOException {
		File input = new File(path, "doc.kml");
		KMLFileReader reader = new KMLFileReader(input, 1024 * 1024, logger);

		reader.readFile();
		Kml kml = reader.getKml();
		assertNotNull(kml);
	}

	@Test(expected = NullPointerException.class)
	public void constructorShouldThrowNPEifFileIsNull() {
		new KMLFileReader(null, 0, logger);
	}
}