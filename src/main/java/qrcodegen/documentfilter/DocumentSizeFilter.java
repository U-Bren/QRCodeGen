/**
 * The following code is based on a code example from Oracle, which can be found
 * here:
 * http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TextComponentDemoProject/src/components/DocumentSizeFilter.java
 * The original code contains a fault, though, that will cause a NullPointerException if null is passed into this filter.
 * This happens for instance if JTextField.setText(null) is called to clear the text field.
 *
 *
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * - Neither the name of Oracle or the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package qrcodegen.documentfilter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * @author Oracle
 * {@link <a href="http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TextComponentDemoProject/src/components/DocumentSizeFilter.java">http://docs.oracle.com/javase/tutorial/uiswing/examples/components/TextComponentDemoProject/src/components/DocumentSizeFilter.java</a>}
 * @author Stefan Ganzer
 */
public class DocumentSizeFilter extends ChainedDocumentFilter {

	/** The maximum number of characters that are allowed */
	private final int maxCharacters;

	/**
	 * Constructs a document filter that limits the input to maxChars
	 * characters.
	 *
	 * @param maxChars the maximum number of characters this document filter
	 * allows
	 */
	public DocumentSizeFilter(int maxChars) {
		this(maxChars, null);
	}

	public DocumentSizeFilter(int maxChars, DocumentFilter nextFilter) {
		super(nextFilter);
		if (maxChars < 0) {
			throw new IllegalArgumentException(Integer.toString(maxChars));
		}
		maxCharacters = maxChars;

	}

	@Override
	public void insertString(DocumentFilter.FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		//Reject the entire replacement if it would make the contents too long.
		
		if (str == null || (fb.getDocument().getLength() + str.length()) <= maxCharacters) {
			super.insertString(fb, offs, str, a);
		} else {
			provideErrorFeedback();
		}
	}

	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		//Reject the entire replacement if it would make the contents too long.
		if (str == null || (fb.getDocument().getLength() + str.length() - length) <= maxCharacters) {
			super.replace(fb, offs, length, str, a);
		} else {
			provideErrorFeedback();
		}
	}
}
