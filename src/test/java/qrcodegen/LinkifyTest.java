/*
 Copyright 2012 Stefan Ganzer
 
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
package qrcodegen;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Stefan Ganzer
 */
public class LinkifyTest {

	private static final String URL = "https://sites.google.com/site/qrcodeforwn/";
	private static final String EMAIL = "portableqrcodegenerator@googlemail.com";
	private static final String EMAIL_ADDRESS = "Stefan%20Ganzer%20%3Cportableqrcodegenerator@gmail.com%3E";
	private static final String SUBJECT = "QR%20Code%20Generator";
	private static final String BODY = "Version:";
	private JLabel jLabel;
	private MouseListener mouseListener;

	public LinkifyTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		jLabel = new JLabel();
		mouseListener = new TestMouseListener();
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of makeHTTPLinkable method, of class Linkify.
	 */
	@Test
	public void testMakeHTTPLinkable() {
		System.out.println("makeHTTPLinkable");
		jLabel.setText(URL);
		Linkify.makeHTTPLinkable(jLabel, mouseListener);
		String expected =
				"<html><a href=\"https://sites.google.com/site/qrcodeforwn/\">"
				+ "https://sites.google.com/site/qrcodeforwn/</a></html>";
		assertEquals(expected, jLabel.getText());
	}

	/**
	 * Test of makeMailLinkable method, of class Linkify.
	 */
	@Test
	public void testMakeMailLinkableAddress() {
		System.out.println("makeMailLinkable");
		jLabel.setText(EMAIL);
		Linkify.makeMailLinkable(jLabel, mouseListener, null, null, null);
		String expected = "<html><a href=\"mailto:portableqrcodegenerator@googlemail.com\">"
				+ "portableqrcodegenerator@googlemail.com</a></html>";
		assertEquals(expected, jLabel.getText());
	}

	/**
	 * Test of makeMailLinkable method, of class Linkify.
	 */
	@Test
	public void testMakeMailLinkableAddressSubject() {
		System.out.println("makeMailLinkable");
		jLabel.setText(EMAIL);
		Linkify.makeMailLinkable(jLabel, mouseListener, null, SUBJECT, null);
		String expected = "<html><a href=\"mailto:portableqrcodegenerator@googlemail.com?subject=QR%20Code%20Generator\">"
				+ "portableqrcodegenerator@googlemail.com</a></html>";
		assertEquals(expected, jLabel.getText());
	}

	/**
	 * Test of makeMailLinkable method, of class Linkify.
	 */
	@Test
	public void testMakeMailLinkableAddressBody() {
		System.out.println("makeMailLinkable");
		jLabel.setText(EMAIL);
		Linkify.makeMailLinkable(jLabel, mouseListener, null, null, BODY);
		String expected = "<html><a href=\"mailto:portableqrcodegenerator@googlemail.com?body=Version:\">"
				+ "portableqrcodegenerator@googlemail.com</a></html>";
		assertEquals(expected, jLabel.getText());
	}

	/**
	 * Test of makeMailLinkable method, of class Linkify.
	 */
	@Test
	public void testMakeMailLinkableAddressSubjectBody() {
		System.out.println("makeMailLinkable");
		jLabel.setText(EMAIL);
		Linkify.makeMailLinkable(jLabel, mouseListener, null, SUBJECT, BODY);
		String expected = "<html><a href=\"mailto:portableqrcodegenerator@googlemail.com?subject=QR%20Code%20Generator&body=Version:\">"
				+ "portableqrcodegenerator@googlemail.com</a></html>";
		assertEquals(expected, jLabel.getText());
	}

		/**
	 * Test of makeMailLinkable method, of class Linkify.
	 */
	@Test
	public void testMakeMailLinkableFullAddressSubjectBody() {
		System.out.println("makeMailLinkableFullAddressSubjectBody");
		jLabel.setText(EMAIL);
		Linkify.makeMailLinkable(jLabel, mouseListener, EMAIL_ADDRESS, SUBJECT, BODY);
		String expected = "<html><a href=\"mailto:Stefan%20Ganzer%20%3Cportableqrcodegenerator@gmail.com%3E?subject=QR%20Code%20Generator&body=Version:\">"
				+ "portableqrcodegenerator@googlemail.com</a></html>";
		assertEquals(expected, jLabel.getText());
	}

	
	/**
	 * Test of stripHRef method, of class Linkify.
	 */
	@Test
	public void testGetPlainLink() {
		System.out.println("getPlainLink");
		String s = "<html><a href=\"https://sites.google.com/site/qrcodeforwn/\">"
				+ "https://sites.google.com/site/qrcodeforwn/</a></html>";
		String expResult = "https://sites.google.com/site/qrcodeforwn/";
		String result = Linkify.stripHRef(s);
		assertEquals(expResult, result);

		s = "<html><a href=\"mailto:portableqrcodegenerator@googlemail.com?subject=QR%20Code%20Generator\">"
				+ "portableqrcodegenerator@googlemail.com</a></html>";
		expResult = "mailto:portableqrcodegenerator@googlemail.com?subject=QR%20Code%20Generator";
		result = Linkify.stripHRef(s);
		assertEquals(expResult, result);

		s = "<html><a href=\"mailto:Stefan%20Ganzer%20%3Cportableqrcodegenerator@gmail.com%3E?subject=QR%20Code%20Generator&body=Version:\">"
				+ "portableqrcodegenerator@googlemail.com</a></html>";
		expResult = "mailto:Stefan%20Ganzer%20%3Cportableqrcodegenerator@gmail.com%3E?subject=QR%20Code%20Generator&body=Version:";
		result = Linkify.stripHRef(s);
		assertEquals(expResult, result);
	
	}

	private static class TestMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// ignore
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// ignore
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// ignore
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// ignore
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// ignore
		}
	}
}
