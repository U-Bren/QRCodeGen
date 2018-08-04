/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class DocumentDigitFilter extends ChainedDocumentFilter {

	private static final Pattern pattern = Pattern.compile("^\\d*$");
	
	public DocumentDigitFilter() {
		super();
	}

	public DocumentDigitFilter(DocumentFilter nextFilter) {
		super(nextFilter);
	}

	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		//Reject the entire replacement if it would make the contents too long.
		if (str == null || pattern.matcher(str).matches()) {
			super.insertString(fb, offs, str, a);
		} else {
			//Toolkit.getDefaultToolkit().beep();
			provideErrorFeedback();
		}
	}

	@Override
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		//Reject the entire replacement if it would make the contents too long.
		if (str == null || pattern.matcher(str).matches()) {
			super.replace(fb, offs, length, str, a);
		} else {
			//Toolkit.getDefaultToolkit().beep();
			provideErrorFeedback();
		}
	}
}
