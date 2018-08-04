/*
 * Copyright (C) 2012, 2013 Stefan Ganzer
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
package qrcodegen.swing;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import static org.hamcrest.CoreMatchers.*;
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
public class EditGroupFormatterTest {

	private TestableEditGroupFormatter formatter;
	private TestableEditGroupFormatter.TestableDocumentFilter filter;
	private MyFilterBypass fb;

	public EditGroupFormatterTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		formatter = new TestableEditGroupFormatter();
		filter = formatter.filter;
		fb = new MyFilterBypass();
	}

	@After
	public void tearDown() {
		formatter = null;
		filter = null;
		fb = null;
	}

	@Test
	public void shouldInsertOddSizedInputIntoEmptyDocument() throws BadLocationException {

		final String input = "abcdefghi";
		String[] s = {null, "a b c d e f g h i ", "ab cd ef gh i", "abc def ghi ", "abcd efgh i", "abcde fghi"};

		for (int i = 0; i < s.length; i++) {
			String expectedResult = s[i];
			if (expectedResult == null) {
				continue;
			}
			formatter = new TestableEditGroupFormatter();
			filter = formatter.filter;
			fb = new MyFilterBypass();
			formatter.setGroupSize(i);
			filter.insertString(fb, 0, input, null);
			assertThat(filter.result, equalTo(expectedResult));
		}
	}

	@Test
	public void shouldInsertOddSizedInputAtEndOfNonEmptyDocument() throws BadLocationException {

		final String str = "ABCDE";
		final String[] existingInputs = {null, "a b c d e ", "ab cd e", "abc de", "abcd e", "abcde"};
		final String[] expectedResults = {null, "a b c d e A B C D E ", "ab cd eA BC DE ", "abc deA BCD E", "abcd eABC DE", "abcde ABCDE "};

		for (int i = 0; i < expectedResults.length; i++) {
			String expectedResult = expectedResults[i];
			if (expectedResult == null) {
				continue;
			}
			formatter = new TestableEditGroupFormatter();
			filter = formatter.filter;
			fb = new MyFilterBypass(existingInputs[i]);

			formatter.setGroupSize(i);
			filter.insertString(fb, fb.getDocument().getLength(), str, null);
			assertThat(filter.result, equalTo(expectedResult));
		}
	}

	@Test
	public void shouldInsertOddSizedInputInsideNonEmptyDocument() throws BadLocationException {

		final int offset = 3;
		final String str = "ABCDE";
		final String[] existingInputs = {null, "a b c d e ", "ab cd e", "abc de", "abcd e", "abcde"};
		final String[] expectedResults = {null, "a b A B C D E c d e ", "ab AB CD Ec de ", "abc ABC DEd e", "abcA BCDE de", "abcAB CDEde "};

		for (int i = 0; i < expectedResults.length; i++) {
			String expectedResult = expectedResults[i];
			if (expectedResult == null) {
				continue;
			}
			formatter = new TestableEditGroupFormatter();
			filter = formatter.filter;
			fb = new MyFilterBypass(existingInputs[i]);

			formatter.setGroupSize(i);
			filter.insertString(fb, offset, str, null);
			assertThat(filter.result, equalTo(expectedResult));
		}
	}

	@Test
	public void shouldReplaceInEmptyDocument() throws BadLocationException {
		final String input = "abcdefghi";
		String[] s = {null, "a b c d e f g h i ", "ab cd ef gh i", "abc def ghi ", "abcd efgh i", "abcde fghi"};

		for (int i = 0; i < s.length; i++) {
			String expectedResult = s[i];
			if (expectedResult == null) {
				continue;
			}
			formatter = new TestableEditGroupFormatter();
			filter = formatter.filter;
			fb = new MyFilterBypass();
			formatter.setGroupSize(i);
			filter.replace(fb, 0, 0, input, null);
			assertThat(filter.result, equalTo(expectedResult));
		}
	}

	@Test
	public void shouldReplaceAtEndOfNonEmptyDocument() throws BadLocationException {
		final String str = "ABCDE";
		final String[] existingInputs = {null, "a b c d e ", "ab cd e", "abc de", "abcd e", "abcde"};
		final String[] expectedResults = {null, "a b c d e A B C D E ", "ab cd eA BC DE ", "abc deA BCD E", "abcd eABC DE", "abcde ABCDE "};

		for (int i = 0; i < expectedResults.length; i++) {
			String expectedResult = expectedResults[i];
			if (expectedResult == null) {
				continue;
			}
			formatter = new TestableEditGroupFormatter();
			filter = formatter.filter;
			fb = new MyFilterBypass(existingInputs[i]);

			formatter.setGroupSize(i);
			filter.replace(fb, fb.getDocument().getLength(), 0, str, null);
			assertThat(filter.result, equalTo(expectedResult));
		}
	}

	@Test
	public void shouldReplaceInsideNonEmptyDocumentWithoutDeleting() throws BadLocationException {
		final int offset = 3;
		final String str = "ABCDE";
		final String[] existingInputs = {null, "a b c d e ", "ab cd e", "abc de", "abcd e", "abcde"};
		final String[] expectedResults = {null, "a b A B C D E c d e ", "ab AB CD Ec de ", "abc ABC DEd e", "abcA BCDE de", "abcAB CDEde "};

		for (int i = 0; i < expectedResults.length; i++) {
			String expectedResult = expectedResults[i];
			if (expectedResult == null) {
				continue;
			}
			formatter = new TestableEditGroupFormatter();
			filter = formatter.filter;
			fb = new MyFilterBypass(existingInputs[i]);

			formatter.setGroupSize(i);
			filter.replace(fb, offset, 0, str, null);
			assertThat(filter.result, equalTo(expectedResult));
		}
	}

	@Test
	public void shouldReplaceInsideNonEmptyDocumentWithDeleting() throws BadLocationException {
		final int offset = 3;
		final int delete = 2;
		final String str = "ABCDE";
		final String[] existingInputs = {null, "a b c d e ", "ab cd e", "abc de", "abcd e", "abcde"};
		final String[] expectedResults = {null, "a b A B C D E d e ", "ab AB CD Ee ", "abc ABC DEe ", "abcA BCDE e", "abcAB CDE"};

		for (int i = 0; i < expectedResults.length; i++) {
			String expectedResult = expectedResults[i];
			if (expectedResult == null) {
				continue;
			}
			formatter = new TestableEditGroupFormatter();
			filter = formatter.filter;
			fb = new MyFilterBypass(existingInputs[i]);

			formatter.setGroupSize(i);
			filter.replace(fb, offset, delete, str, null);
			assertThat(filter.result, equalTo(expectedResult));
		}
	}

	@Test
	public void shouldRemoveAtEndOfNonEmptyDocument() throws BadLocationException {
		final int delete = 3;
		final String[] existingInputs = {null, "a b c d e ", "ab cd e", "abc de", "abcd e", "abcde"};
		final String[] expectedResults = {null, "a b c d ", "ab c", "abc ", "abc", "ab"};

		for (int i = 0; i < expectedResults.length; i++) {
			String expectedResult = expectedResults[i];
			if (expectedResult == null) {
				continue;
			}
			formatter = new TestableEditGroupFormatter();
			filter = formatter.filter;
			fb = new MyFilterBypass(existingInputs[i]);

			formatter.setGroupSize(i);
			filter.remove(fb, fb.getDocument().getLength() - delete, delete);
			assertThat(filter.result, equalTo(expectedResult));
		}

	}

	private static class TestableEditGroupFormatter extends EditGroupFormatter {

		private final TestableDocumentFilter filter = new TestableDocumentFilter();

		@Override
		protected DocumentFilter getDocumentFilter() {
			return filter;
		}

		private class TestableDocumentFilter extends GroupDocumentFilter {

			private String result;

			@Override
			public void insertString(DocumentFilter.FilterBypass fb, int offset, String str, AttributeSet a) throws BadLocationException {
				super.insertString(fb, offset, str, a);
				result = fb.getDocument().getText(0, fb.getDocument().getLength());
			}

			@Override
			public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet a) throws BadLocationException {
				super.replace(fb, offset, length, str, a);
				result = fb.getDocument().getText(0, fb.getDocument().getLength());
			}

			@Override
			public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
				super.remove(fb, offset, length);
				result = fb.getDocument().getText(0, fb.getDocument().getLength());
			}
		}
	}

	private static class MyFilterBypass extends DocumentFilter.FilterBypass {

		private final AbstractDocument d;

		MyFilterBypass() {
			this(null);
		}

		MyFilterBypass(String init) {
			d = new PlainDocument();
			try {
				d.insertString(0, init, null);
			} catch (BadLocationException ex) {
				throw new AssertionError(ex);
			}
		}

		@Override
		public Document getDocument() {
			return d;
		}

		@Override
		public void remove(int offset, int length) throws BadLocationException {
			d.remove(offset, length);
		}

		@Override
		public void insertString(int offset, String string, AttributeSet attr) throws BadLocationException {
			d.insertString(offset, string, attr);
		}

		@Override
		public void replace(int offset, int length, String string, AttributeSet attrs) throws BadLocationException {
			d.replace(offset, length, string, attrs);
		}
	}
}
