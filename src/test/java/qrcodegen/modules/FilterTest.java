/*
 Copyright 2011 Stefan Ganzer

 This file is part of QRCodeGen.

 QRCodeGen is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 QRCodeGen is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen.modules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Stefan
 */
public class FilterTest {

	private static final Charset charset = Charset.forName("ISO-8859-1");

	public FilterTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveInlinedPhoto2() throws FileNotFoundException, IOException {
		System.out.println("removeInlinedPhoto2");
		String result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_02.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_expected.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveInlinedPhotoSB2() throws FileNotFoundException, IOException {
		System.out.println("removeInlinedPhotoSB2");
		StringBuilder result = new StringBuilder(VCardPanel.readFile(getFileForName("vcard_photo_02.vcf"), charset));
		boolean gotMatch = Filter.PHOTO.apply(result);
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_expected.vcf"), charset);
		assertEquals(expResult, result.toString());
		assertTrue(gotMatch);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveInlinedPhoto3() throws FileNotFoundException, IOException {
		System.out.println("removeInlinedPhoto3");
		String result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_03.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_expected.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveInlinedPhoto4() throws FileNotFoundException, IOException {
		System.out.println("removeInlinedPhoto4");
		String result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_04.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_expected.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveInlinedPhoto5() throws FileNotFoundException, IOException {
		System.out.println("removeInlinedPhoto5");
		String result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_05.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_expected.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveInlinedPhoto6() throws FileNotFoundException, IOException {
		System.out.println("removeInlinedPhoto6");
		String result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_06.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_expected.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveInlinedPhoto7() throws FileNotFoundException, IOException {
		System.out.println("removeInlinedPhoto7");
		String result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_07.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_expected.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveInlinedPhoto8() throws FileNotFoundException, IOException {
		System.out.println("removeInlinedPhoto8");
		String result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_08.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_expected.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveInlinedPhotoURL() throws FileNotFoundException, IOException {
		System.out.println("removeInlinedPhotoURL");
		String result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_url_2.1.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_url_2.1.vcf"), charset);
		assertEquals(expResult, result);
		//
		result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_url_3.0.vcf"), charset));
		expResult = VCardPanel.readFile(getFileForName("vcard_photo_url_3.0.vcf"), charset);
		assertEquals(expResult, result);
		//
		result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_url_4.0.vcf"), charset));
		expResult = VCardPanel.readFile(getFileForName("vcard_photo_url_4.0.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveInlinedPhotoOutlook01() throws FileNotFoundException, IOException {
		System.out.println("removeInlinedPhotoOutlook01");
		String result = Filter.PHOTO.apply(VCardPanel.readFile(getFileForName("vcard_photo_outlook_01.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_outlook_expected.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveMsOlDesign() throws FileNotFoundException, IOException {
		System.out.println("removeMsOlDesign");
		String result = Filter.X_MS_OL_DESIGN.apply(VCardPanel.readFile(getFileForName("vcard_photo_outlook_01.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_outlook_01_sans_design.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveMsOlDesignSB() throws FileNotFoundException, IOException {
		System.out.println("removeMsOlDesignSB");
		StringBuilder result = new StringBuilder(VCardPanel.readFile(getFileForName("vcard_photo_outlook_01.vcf"), charset));
		boolean gotMatch = Filter.X_MS_OL_DESIGN.apply(result);
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_outlook_01_sans_design.vcf"), charset);
		assertEquals(expResult, result.toString());
		assertTrue(gotMatch);
	}
	
	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveEmptyLines() throws FileNotFoundException, IOException {
		System.out.println("removeEmptyLines");
		String result = Filter.EMPTY_LINES.apply(VCardPanel.readFile(getFileForName("vcard_photo_outlook_01.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_outlook_01_sans_empty_lines.vcf"), charset);
		assertEquals(expResult, result);
	}

	/**
	 * Test of removeInlinedPhoto method, of class VCardFilter.
	 */
	@Test
	public void testRemoveMsOlExtensions() throws FileNotFoundException, IOException {
		System.out.println("removeEmptyLines");
		String result = Filter.X_MS_OL_EXTENSIONS.apply(VCardPanel.readFile(getFileForName("vcard_photo_outlook_01.vcf"), charset));
		String expResult = VCardPanel.readFile(getFileForName("vcard_photo_outlook_01_sans_outlook_extensions.vcf"), charset);
		assertEquals(expResult, result);
	}

	
	private static File getFileForName(String name) {
		try {
			return new File(FilterTest.class.getResource(name).toURI());
		} catch (URISyntaxException use) {
			throw new IllegalArgumentException(use);
		}
	}
}
