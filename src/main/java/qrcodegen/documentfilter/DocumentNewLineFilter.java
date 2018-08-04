/*
 * Copyright 2011, 2012 Stefan Ganzer
 *
 * This file is part of QRCodeGen.
 *
 * QRCodeGen is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * QRCodeGen is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen.documentfilter;

import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

/**
 * A DocumentFilter implementation that prevents entering the newline character.
 *
 * @author Stefan Ganzer
 */
public class DocumentNewLineFilter extends ChainedDocumentFilter {

	private static final String NEWLINE = "\n";
	private static final Pattern LINE_TERMINATOR = Pattern.compile("[\n\r\u0085\u2028\u2029]");

	public DocumentNewLineFilter(DocumentFilter nextFilter) {
		super(nextFilter);
	}

	public DocumentNewLineFilter() {
		this(null);
	}

	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		if (str != null && str.contains(NEWLINE)) {
			provideErrorFeedback();
		} else {
			super.insertString(fb, offs, str, a);
		}
	}

	@Override
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		if (str != null && str.contains((NEWLINE))) {
			provideErrorFeedback();
		} else {
			super.replace(fb, offs, length, str, a);
		}
	}
}
