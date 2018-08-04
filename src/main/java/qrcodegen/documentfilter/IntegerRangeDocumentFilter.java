/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.documentfilter;

import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author Stefan Ganzer
 */
public class IntegerRangeDocumentFilter extends ChainedDocumentFilter {

	private final int min;
	private final int max;

	public IntegerRangeDocumentFilter() {
		this(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public IntegerRangeDocumentFilter(int min, int max) {
		super();

		if (min > max) {
			throw new IllegalArgumentException();
		}

		this.min = min;
		this.max = max;
	}

	public IntegerRangeDocumentFilter(int min, int max, DocumentFilter nextFilter) {
		super(nextFilter);

		if (min > max) {
			throw new IllegalArgumentException();
		}

		this.min = min;
		this.max = max;
	}

	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		if (str == null) {
			// return
		} else {
			String newValue;
			Document document = fb.getDocument();
			int documentLength = document.getLength();
			if (documentLength == 0) {
				newValue = str;
			} else {
				StringBuilder sb = new StringBuilder(documentLength + str.length());
				sb.append(document.getText(0, documentLength)).insert(offs, str);
				newValue = sb.toString();
			}

			if (validated(newValue)) {
				super.insertString(fb, offs, str, a);
			} else {
				provideErrorFeedback();
			}
		}
	}

	@Override
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {

		if (str == null) {
			// return
		} else {
			String newValue;
			Document document = fb.getDocument();
			int documentLength = document.getLength();

			if (documentLength == 0) {
				newValue = str;
			} else {
				StringBuilder sb = new StringBuilder(documentLength + str.length());
				sb.append(document.getText(0, documentLength)).replace(offs, offs + str.length(), str);
				newValue = sb.toString();
			}

			if (validated(newValue)) {
				super.replace(fb, offs, length, str, a);
			} else {
				provideErrorFeedback();
			}
		}
	}

	private boolean validated(String s) {

		// Allow zero length input
		if (s == null || s.isEmpty()) {
			return true;
		}

		boolean isValidNumber;

		try {
			int i = Integer.parseInt(s);
			if (i >= min && i <= max) {
				isValidNumber = true;
			} else {
				isValidNumber = false;
			}
		} catch (NumberFormatException nfe) {
			isValidNumber = false;
		}
		return isValidNumber;
	}
}
