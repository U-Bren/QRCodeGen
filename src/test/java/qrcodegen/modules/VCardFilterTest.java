/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules;

import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardFilterTest {

	private static final String NEWLINE = String.format("%n"); //NOI18N
	private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
	private VCardFilter cardFilter;

	public VCardFilterTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
		cardFilter = new VCardFilter();
	}

	@After
	public void tearDown() {
		cardFilter = null;
	}

	/**
	 * Test of setCard method, of class VCardFilter.
	 */
	@Test
	public void testSetCard() {
		System.out.println("setCard");

		assertFalse(cardFilter.isCardSet());
		String card = "test";
		cardFilter.setCard(card);
		assertTrue(cardFilter.isCardSet());
		assertEquals(card, cardFilter.getCard());
		
		cardFilter.processCard();
		assertTrue(cardFilter.hasFilteredResult());
		
		card = "new input";
		cardFilter.setCard(card);
		assertFalse(cardFilter.hasFilteredResult());
	}

	@Test(expected = NullPointerException.class)
	public void testSetCardNullArgument() {
		System.out.println("setCardNullArgument");

		assertFalse(cardFilter.isCardSet());
		String card = null;
		cardFilter.setCard(card);
	}

	/**
	 * Test of getCard method, of class VCardFilter.
	 */
	@Test
	public void testGetCard() {
		System.out.println("getCard");
		assertFalse(cardFilter.isCardSet());
		String card = "test";
		cardFilter.setCard(card);
		assertEquals(card, cardFilter.getCard());

		card = "";
		cardFilter.setCard(card);
		assertEquals(card, cardFilter.getCard());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetCardNoCardSet() {
		System.out.println("setCardNoCardSet");

		assertFalse(cardFilter.isCardSet());
		cardFilter.getCard();
	}

	@Test
	public void testHasFilteredResult(){
		System.out.println("hasFilteredResult");
		
		assertFalse(cardFilter.hasFilteredResult());
		cardFilter.setCard("test");
		assertFalse(cardFilter.hasFilteredResult());
		cardFilter.processCard();
		assertTrue(cardFilter.hasFilteredResult());
		cardFilter.setCard("new input");
		assertFalse(cardFilter.hasFilteredResult());
	}
	
	/**
	 * Test of isCardSet method, of class VCardFilter.
	 */
	@Test
	public void testIsCardSet() {
		System.out.println("isCardSet");

		assertFalse(cardFilter.isCardSet());
		cardFilter.setCard("test");
		assertTrue(cardFilter.isCardSet());
	}

	/**
	 * Test of isCardSet method, of class VCardFilter.
	 */
	@Test
	public void testIsCardSetAfterNullArgument() {
		System.out.println("isCardSetAfterNullArgument");

		assertFalse(cardFilter.isCardSet());
		boolean causedException = false;
		try {
			cardFilter.setCard(null);
		} catch (NullPointerException npe) {
			causedException = true;
		}
		assertTrue(causedException);
		assertFalse(cardFilter.isCardSet());
	}

	/**
	 * Test of setIsActive method, of class VCardFilter.
	 */
	@Test
	public void testSetIsActive() {
		System.out.println("setIsActive");

		assertTrue(cardFilter.isActive());

		boolean state = false;
		cardFilter.setIsActive(state);
		assertEquals(state, cardFilter.isActive());

		state = true;
		cardFilter.setIsActive(state);
		assertEquals(state, cardFilter.isActive());
	}
	
	@Test
	public void testSetIsActiveProgrammError(){
		System.out.println("setIsActiveProgrammError");
		
		String card = "test";
		cardFilter.setCard(card);
		cardFilter.setIsActive(false);
		cardFilter.processCard();
		// This caused an AssertionError due to an error in setIsActive(true)
		cardFilter.setIsActive(true);
	}

	@Test
	public void testSetIsActiveCardSet() {
		System.out.println("setIsActiveCardSet");

		assertTrue(cardFilter.isActive());
		cardFilter.setCard("test");
		assertTrue(cardFilter.isActive());
	}

	@Test
	public void testSetIsActiveNoCardSet() {
		System.out.println("setIsActiveNoCardSet");

		assertTrue(cardFilter.isActive());

		boolean state = false;
		cardFilter.setIsActive(state);
		cardFilter.setCard("test");

		state = true;
		cardFilter.setIsActive(state);
		assertEquals(state, cardFilter.isActive());
		assertEquals("test", cardFilter.getCard());
	}

	/**
	 * Test of isActive method, of class VCardFilter.
	 */
	@Test
	public void testIsActive() {
		System.out.println("isActive");
		assertTrue(cardFilter.isActive());

		boolean state = false;
		cardFilter.setIsActive(state);
		assertEquals(state, cardFilter.isActive());

		state = true;
		cardFilter.setIsActive(state);
		assertEquals(state, cardFilter.isActive());
	}

	/**
	 * Test of setFilters method, of class VCardFilter.
	 */
	@Test
	public void testSetFilters() {
		System.out.println("setFilters");

		assertTrue(cardFilter.getFilters().isEmpty());

		Set<Filter> filter = Collections.emptySet();
		cardFilter.setFilters(filter);
		assertTrue(cardFilter.getFilters().isEmpty());

		filter = EnumSet.allOf(Filter.class);
		cardFilter.setFilters(filter);
		assertFalse(cardFilter.getFilters().isEmpty());
		assertEquals(filter, cardFilter.getFilters());
	}

	/**
	 * Test of setFilters method, of class VCardFilter.
	 */
	@Test
	public void testSetFiltersEffects() {
		System.out.println("setFiltersEffects");

		assertTrue(cardFilter.getFilters().isEmpty());

		Set<Filter> filter = EnumSet.allOf(Filter.class);
		cardFilter.setFilters(filter);
		assertFalse(cardFilter.getFilters().isEmpty());
		assertEquals(filter, cardFilter.getFilters());

		cardFilter.setCard("test");
		cardFilter.processCard();
		assertTrue(cardFilter.hasFilteredResult());
		filter = EnumSet.of(Filter.EMPTY_LINES, Filter.PHOTO);
		cardFilter.setFilters(filter);
		assertFalse(cardFilter.hasFilteredResult());
		
	}
	
	
	/**
	 * Test of setFilter method, of class VCardFilter.
	 */
	@Test
	public void testSetFilter() {
		System.out.println("setFilter");

		Filter f = Filter.PHOTO;
		cardFilter.setFilter(f);
		assertTrue(cardFilter.isFilterSet(f));
		assertEquals(1, cardFilter.getFilters().size());
		assertEquals(EnumSet.of(f), cardFilter.getFilters());

		f = Filter.EMPTY_LINES;
		cardFilter.setFilter(f);
		assertTrue(cardFilter.isFilterSet(f));
		assertEquals(1, cardFilter.getFilters().size());
		assertEquals(EnumSet.of(f), cardFilter.getFilters());
	}

	/**
	 * Test of addFilter method, of class VCardFilter.
	 */
	@Test
	public void testAddFilter() {
		Filter f = Filter.PHOTO;
		cardFilter.addFilter(f);
		assertTrue(cardFilter.isFilterSet(f));
		assertEquals(1, cardFilter.getFilters().size());
		assertEquals(EnumSet.of(f), cardFilter.getFilters());

		f = Filter.EMPTY_LINES;
		cardFilter.addFilter(f);
		assertTrue(cardFilter.isFilterSet(f));
		assertEquals(2, cardFilter.getFilters().size());
		assertEquals(EnumSet.of(Filter.PHOTO, Filter.EMPTY_LINES), cardFilter.getFilters());
	}

	/**
	 * Test of removeFilters method, of class VCardFilter.
	 */
	@Test
	public void testRemoveFilters() {
		cardFilter.setFilters(EnumSet.allOf(Filter.class));
		assertEquals(EnumSet.allOf(Filter.class), cardFilter.getFilters());
		cardFilter.removeFilters();
		assertTrue(cardFilter.getFilters().isEmpty());
		assertFalse(cardFilter.isFilterSet(Filter.PHOTO));
	}

	/**
	 * Test of removeFilter method, of class VCardFilter.
	 */
	@Test
	public void testRemoveFilter() {
		cardFilter.setFilters(EnumSet.allOf(Filter.class));

		for (Filter f : EnumSet.allOf(Filter.class)) {
			assertTrue(cardFilter.isFilterSet(f));
			cardFilter.removeFilter(f);
			assertFalse(cardFilter.isFilterSet(f));
		}
		assertTrue(cardFilter.getFilters().isEmpty());
	}

	/**
	 * Test of removeFilter method, of class VCardFilter.
	 */
	@Test (expected = IllegalArgumentException.class)
	public void testRemoveFilterNotSet() {
		System.out.println("removeFilterNotSet");
		
		cardFilter.setFilter(Filter.PHOTO);
		cardFilter.removeFilter(Filter.EMPTY_LINES);
	}
	/**
	 * Test of isFilterSet method, of class VCardFilter.
	 */
	@Test
	public void testIsFilterSet() {
		System.out.println("isFilterSet");

		for(Filter f : EnumSet.allOf(Filter.class)){
			assertFalse(cardFilter.isFilterSet(f));
		}
		
		Filter filter = Filter.PHOTO;
		cardFilter.setFilter(filter);
		assertTrue(cardFilter.isFilterSet(filter));

		cardFilter.setFilters(EnumSet.allOf(Filter.class));
		for(Filter f : EnumSet.allOf(Filter.class)){
			assertTrue(cardFilter.isFilterSet(f));
		}
	}

	/**
	 * Test of processCard method, of class VCardFilter.
	 */
	@Test (expected = IllegalStateException.class)
	public void testProcessCardNoCardSet() {
		System.out.println("processCardNoCardSet");
		
		cardFilter.processCard();
	}

	/**
	 * Test of processCard method, of class VCardFilter.
	 */
	@Test
	public void testProcessCard() {
		System.out.println("processCard");
		
		String s = "test";
		cardFilter.setCard(s);
		cardFilter.processCard();
		assertTrue(cardFilter.hasFilteredResult());
		assertEquals(s, cardFilter.getFilteredResult());
	}

	/**
	 * Test of getFilteredResult method, of class VCardFilter.
	 */
	@Test (expected = IllegalStateException.class)
	public void testGetFilteredResultNoResult() {
		System.out.println("getFilteredResultNoResult");
		
		cardFilter.getFilteredResult(); // throws IllegalStateException
	}

	/**
	 * Test of getFilteredResult method, of class VCardFilter.
	 */
	@Test (expected = IllegalStateException.class)
	public void testGetFilteredResultNoResultAfterSetCard() {
		System.out.println("getFilteredResultNoResult");
		
		cardFilter.setCard("test");
		cardFilter.processCard();
		assertEquals("test", cardFilter.getFilteredResult());
		cardFilter.setCard("new input");
		cardFilter.getFilteredResult();// throws IllegalStateException
	}

	/**
	 * Test of getFilteredResult method, of class VCardFilter.
	 */
	@Test
	public void testGetFilteredResultNoFilterSet() {
		System.out.println("getFilteredResultNoFilterSet");
		
		String card = "test";
		cardFilter.setCard(card);
		cardFilter.processCard();
		assertEquals(card, cardFilter.getFilteredResult());
	}

	/**
	 * Test of getFilteredResult method, of class VCardFilter.
	 */
	@Test
	public void testGetFilteredResult() throws FileNotFoundException, IOException {
		System.out.println("getFilteredResult");
		
		String card = readFile(getFileForName("vcard_photo.vcf"), ISO_8859_1);
		cardFilter.setFilter(Filter.PHOTO);
		cardFilter.setCard(card);
		cardFilter.processCard();
		assertEquals(readFile(getFileForName("vcard_photo_expected.vcf"), ISO_8859_1), cardFilter.getFilteredResult());
	}
	
	@Test
	public void testGetFilteredResultSameAsInput(){
		System.out.println("getFilteredResultSameAsInput");
		
		String card = "test";
		cardFilter.setCard(card);
		
		cardFilter.setIsActive(false);
		cardFilter.processCard();
		assertSame(card, cardFilter.getFilteredResult());

		cardFilter.setIsActive(true);
		cardFilter.processCard();
		assertNotSame(card, cardFilter.getFilteredResult());
		assertEquals(card, cardFilter.getFilteredResult());
	}
	
	/**
	 * Test of getFoundFilter method, of class VCardFilter.
	 */
	@Test
	public void testGetFoundFilter()throws FileNotFoundException, IOException {
		System.out.println("getFilteredResult");

		assertEquals(EnumSet.noneOf(Filter.class), cardFilter.getFoundFilter());
		
		String card = readFile(getFileForName("vcard_photo.vcf"), ISO_8859_1);
		cardFilter.setFilter(Filter.PHOTO);
		cardFilter.setCard(card);
		cardFilter.processCard();
		assertEquals(EnumSet.of(Filter.PHOTO), cardFilter.getFoundFilter());
		
		card = readFile(getFileForName("vcard_photo_02.vcf"), ISO_8859_1);
		cardFilter.setCard(card);
		assertEquals(EnumSet.noneOf(Filter.class), cardFilter.getFoundFilter());
		cardFilter.processCard();
		assertEquals(EnumSet.of(Filter.PHOTO), cardFilter.getFoundFilter());
		
		cardFilter.addFilter(Filter.EMPTY_LINES);
		assertEquals(EnumSet.noneOf(Filter.class), cardFilter.getFoundFilter());
		cardFilter.processCard();
		assertEquals(EnumSet.of(Filter.PHOTO), cardFilter.getFoundFilter());
	}

//	/**
//	 * Test of addChangeListener method, of class VCardFilter.
//	 */
//	@Test
//	public void testAddChangeListener() {
//		System.out.println("addChangeListener");
//		ChangeListener listener = null;
//		VCardFilter instance = new VCardFilter();
//		instance.addChangeListener(listener);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of removeChangeListener method, of class VCardFilter.
//	 */
//	@Test
//	public void testRemoveChangeListener() {
//		System.out.println("removeChangeListener");
//		ChangeListener listener = null;
//		VCardFilter instance = new VCardFilter();
//		instance.removeChangeListener(listener);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of addPropertyChangeListener method, of class VCardFilter.
//	 */
//	@Test
//	public void testAddPropertyChangeListener() {
//		System.out.println("addPropertyChangeListener");
//		String propertyName = "";
//		PropertyChangeListener listener = null;
//		VCardFilter instance = new VCardFilter();
//		instance.addPropertyChangeListener(propertyName, listener);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of removePropertyChangeListener method, of class VCardFilter.
//	 */
//	@Test
//	public void testRemovePropertyChangeListener() {
//		System.out.println("removePropertyChangeListener");
//		String propertyName = "";
//		PropertyChangeListener listener = null;
//		VCardFilter instance = new VCardFilter();
//		instance.removePropertyChangeListener(propertyName, listener);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}

	private static File getFileForName(String name) {
		try {
			return new File(FilterTest.class.getResource(name).toURI());
		} catch (URISyntaxException use) {
			throw new IllegalArgumentException(use);
		}
	}
	public static String readFile(File f, Charset c) throws FileNotFoundException, IOException {
		if (f == null) {
			throw new NullPointerException();
		}
		if (c == null) {
			throw new NullPointerException();
		}
		BufferedReader reader = null;
		final int initialSize = f.length() >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) f.length();
		StringBuilder sb = new StringBuilder(initialSize);
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), c));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append(NEWLINE);
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					//swallowed
				}
			}
		}
		return sb.toString();
	}
}
