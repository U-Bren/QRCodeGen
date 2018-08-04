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
package qrcodegen.documentfilter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author Stefan Ganzer
 */
public class DocumentDoubleQuoteFilter extends ChainedDocumentFilter {

	private static final String DOUBLE_QUOTE = "\"";

	public DocumentDoubleQuoteFilter() {
		super();
	}

	public DocumentDoubleQuoteFilter(DocumentFilter nextFilter) {
		super(nextFilter);
	}

	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		//Reject the entire replacement if it contains a double quo
		if (str != null && str.contains(DOUBLE_QUOTE)) {
			provideErrorFeedback();
		} else {
			super.insertString(fb, offs, str, a);
		}
	}

	@Override
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		//Reject the entire replacement if it contains a double quote
		if (str != null && str.contains(DOUBLE_QUOTE)) {
			provideErrorFeedback();
		} else {
			super.replace(fb, offs, length, str, a);
		}
	}
}
