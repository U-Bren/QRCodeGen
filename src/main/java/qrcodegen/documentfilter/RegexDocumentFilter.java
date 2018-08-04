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

import java.util.regex.Pattern;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author Stefan Ganzer
 */
public class RegexDocumentFilter extends ChainedDocumentFilter {

	private final Pattern pattern;

	public RegexDocumentFilter(Pattern p) {
		this(p, null);
	}

	public RegexDocumentFilter(Pattern pattern, DocumentFilter filter) {
		super(filter);
		if (pattern == null) {
			throw new NullPointerException();
		}
		this.pattern = pattern;
	}

	@Override
	public void insertString(DocumentFilter.FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		//Reject the entire replacement if it would make the contents too long.

		if (str == null || pattern.matcher(str).matches()) {
			super.insertString(fb, offs, str, a);
		} else {
			provideErrorFeedback();
		}
	}

	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		//Reject the entire replacement if it would make the contents too long.
		if (str == null || pattern.matcher(str).matches()) {
			super.replace(fb, offs, length, str, a);
		} else {
			provideErrorFeedback();
		}
	}
}
