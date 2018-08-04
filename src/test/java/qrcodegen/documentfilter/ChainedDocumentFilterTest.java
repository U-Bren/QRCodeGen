/*
 Copyright 2012, 2013 Stefan Ganzer

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
package qrcodegen.documentfilter;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.JTextComponent;
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
public class ChainedDocumentFilterTest {

	public ChainedDocumentFilterTest() {
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
	 * Test of installFilter method, of class ChainedDocumentFilter.
	 */
	@Test
	public void testInstallFilter_JTextComponent() {
		System.out.println("installFilter");
		JTextComponent component = new JTextField();
		AbstractDocument ad = (AbstractDocument) component.getDocument();
		assertEquals(null, ad.getDocumentFilter());
		ChainedDocumentFilter instance = new ChainedDocumentFilterImpl();
		instance.installFilter(component);
		assertEquals(instance, ad.getDocumentFilter());
	}

	private static class ChainedDocumentFilterImpl extends ChainedDocumentFilter {
	}
}
