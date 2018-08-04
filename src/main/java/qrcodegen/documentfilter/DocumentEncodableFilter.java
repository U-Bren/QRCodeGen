package qrcodegen.documentfilter;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;

/**
 * @author Stefan Ganzer
 */
public class DocumentEncodableFilter extends ChainedDocumentFilter {

	private final CharsetEncoder encoder;

	public DocumentEncodableFilter(Charset charset){
		this(charset, null);
	}
	
	/**
	 * Constructs a document filter that limits the input to maxChars
	 * characters.
	 *
	 * @param charset
	 * @param nextFilter  
	 */
	public DocumentEncodableFilter(Charset charset, DocumentFilter nextFilter) {
		super(nextFilter);
		if (charset == null) {
			throw new NullPointerException();
		}
		encoder = charset.newEncoder();
	}

	@Override
	public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		//Reject the entire replacement if it would make the contents too long.
		if (encoder.canEncode(str)) {
			super.insertString(fb, offs, str, a);
		} else {
			//Toolkit.getDefaultToolkit().beep();
			provideErrorFeedback();
		}
	}

	@Override
	public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
		//Reject the entire replacement if it would make the contents too long.
		if (encoder.canEncode(str)) {
			super.replace(fb, offs, length, str, a);
		} else {
			//Toolkit.getDefaultToolkit().beep();
			provideErrorFeedback();
		}
	}
}
