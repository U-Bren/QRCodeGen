/*
 Copyright 2011, 2012 Stefan Ganzer
 
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

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

/**
 * @author Stefan Ganzer
 */
public class PositiveIntegerDocumentFilter extends DocumentFilter {

	/** The mininum value this filter allows */
	private final int min;
	/** The maximum value this filter allows */
	private final int max;

	/**
	 * Constructs a document filter that limits the input to positive integers.
	 */
	public PositiveIntegerDocumentFilter() {
		this(0, Integer.MAX_VALUE);
	}

	public PositiveIntegerDocumentFilter(int min, int max) {
		if (min < 0) {
			throw new IllegalArgumentException(Integer.toString(min));
		}
		if (max < min) {
			throw new IllegalArgumentException("min: " + Integer.toString(min) + "max: " + Integer.toString(max));
		}
		this.min = min;
		this.max = max;
	}

	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		String s = constructResult(fb, offs, str);

		//Reject the entire replacement if it would make the contents too long.
		if (isValidInteger(s)) {
			super.insertString(fb, offs, str, a);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	@Override
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		String s = constructResult(fb, offs, str);

		if (isValidInteger(s)) {
			super.replace(fb, offs, length, str, a);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private boolean isValidInteger(String s) {
		// Is the string a positive Integer?
		boolean isValidInteger = false;
		try {
			int i = Integer.parseInt(s);
			if (i >= min && i <= max) {
				isValidInteger = true;
			}
		} catch (NumberFormatException nfe) {
			// swallowed
		}
		return isValidInteger;
	}

	private static String constructResult(FilterBypass fb, int offs, String str) throws BadLocationException {
		// Construct the string that would result in allowing the insertion
		String s = fb.getDocument().getText(0, offs).concat(str).concat(fb.getDocument().getText(offs, fb.getDocument().getLength() - offs));
		return s;
	}
}
