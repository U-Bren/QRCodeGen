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
package qrcodegen.tools;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Stefan Ganzer
 */
public class ImageFileWriterTest {

	ImageFileWriter writer;
	RenderedImage image;
	File output;

	public ImageFileWriterTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() throws IOException {
		image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
		writer = new ImageFileWriter(image);
	}

	@After
	public void tearDown() {
		image = null;
		writer = null;
		output = null;
	}

	@Test
	public void shouldWriteToFileIfIsJPGFile() throws FileNotFoundException, IOException {
		String extension = ".jpg";
		output = File.createTempFile("imageFileWriterTest", extension, null);
		output.deleteOnExit();
		assertEquals(0L, output.length());

		writer.toFile(output);

		assertTrue(output.exists());
		assertNotEquals(0L, output.length());
	}

	@Test
	public void shouldWriteToFileIfIsGIFFile() throws FileNotFoundException, IOException {
		String extension = ".gif";
		output = File.createTempFile("imageFileWriterTest", extension, null);
		output.deleteOnExit();
		assertEquals(0L, output.length());

		writer.toFile(output);

		assertTrue(output.exists());
		assertNotEquals(0L, output.length());
	}

	@Test
	public void shouldWriteToFileIfIsPNGFile() throws FileNotFoundException, IOException {
		String extension = ".png";
		output = File.createTempFile("imageFileWriterTest", extension, null);
		output.deleteOnExit();
		assertEquals(0L, output.length());

		writer.toFile(output);

		assertTrue(output.exists());
		assertNotEquals(0L, output.length());
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowNPEIfImageIsNull() {
		writer = new ImageFileWriter(null);
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowNPEIfFileIsNull() throws FileNotFoundException, IOException {
		writer.toFile(null);
	}

	@Test
	public void shouldThrowFNFExceptionIfFileIsWriteProtected() throws FileNotFoundException, IOException {
		String extension = ".png";
		output = File.createTempFile("imageFileWriterTest", extension, null);
		output.deleteOnExit();
		assertEquals(0L, output.length());

		try {
			boolean readOnly = output.setReadOnly();
			assertTrue(readOnly);

			writer.toFile(output);
		} catch (FileNotFoundException fnfe) {
			return;
		} finally {
			output.setWritable(true);
			assertEquals(0L, output.length());
		}
		fail();
	}

	@Test(expected = IOException.class)
	public void shouldThrowIOExceptionForEmptyExtension() throws FileNotFoundException, IOException {
		String extension = "";
		output = File.createTempFile("imageFileWriterTest", extension, null);
		output.deleteOnExit();
		assertEquals(0L, output.length());

		try {
			writer.toFile(output);
		} catch (IOException ioe) {
			throw new IOException(ioe);
		} finally {
			assertTrue(output.exists());
			assertEquals(0L, output.length());
		}
	}

	@Test(expected = IOException.class)
	public void shouldThrowIOExceptionForNonImageExtension() throws FileNotFoundException, IOException {
		String extension = ".txt";
		output = File.createTempFile("imageFileWriterTest", extension, null);
		output.deleteOnExit();
		assertEquals(0L, output.length());

		try {
			writer.toFile(output);
		} catch (IOException ioe) {
			throw new IOException(ioe);
		} finally {
			assertTrue(output.exists());
			assertEquals(0L, output.length());
		}
	}
}
