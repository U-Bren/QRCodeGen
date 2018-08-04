/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.documentfilter;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

/**
 * A single DocumentFilter can be set on an AbstractDocument. There may be times
 * when you wish to perform multiple filtering of the data as it is added to a
 * Document. By extending the ChainedDocumentFilter instead of the
 * DocumentFilter you will receive added flexibility in that the filter can be
 * used stand alone or with other ChainedDocumentFilters.
 *
 * Whenever one filter step fails, the chaining of the filters is also
 * terminated. In this case is it recommended you invoke the
 * provideErrorFeedback() method to provide user feedback.
 *
 * @see https://tips4java.wordpress.com/2009/10/18/chaining-document-filters/
 */
public abstract class ChainedDocumentFilter extends DocumentFilter {

	private static final LookAndFeel LOOK_AND_FEEL = UIManager.getLookAndFeel();
	/** The next DocumentFilter in the chain. */
	private DocumentFilter nextFilterInChain;

	/**
	 * Standard constructor for standalone usage
	 */
	public ChainedDocumentFilter() {
		this(null);
	}

	/**
	 * Constructor used when further filtering is required after this filter has
	 * been applied.
	 *
	 * @param filter
	 */
	public ChainedDocumentFilter(DocumentFilter filter) {
		setNextFilter(filter);
	}

	/**
	 * Get the next filter in the chain.
	 *
	 * @return the next filter in the chain
	 */
	public DocumentFilter getNextFilter() {
		return nextFilterInChain;
	}

	/**
	 * Set the next filter in the chain
	 *
	 * @param filter
	 */
	public final void setNextFilter(DocumentFilter filter) {
		this.nextFilterInChain = filter;
	}

	/**
	 * Install this filter on the AbstractDocument
	 *
	 * @param component the text components that will use this filter
	 */
	public void installFilter(JTextComponent component) {
		if (component == null) {
			throw new NullPointerException();
		}
		Document doc = component.getDocument();
		try {
			((AbstractDocument) doc).setDocumentFilter(this);
		} catch (ClassCastException cce) {
			throw new IllegalArgumentException("The given component has no AbstractDocument associated with it.", cce);
		}
	}

	/**
	 * Install this filter on the AbstractDocument
	 *
	 * @param components the text components that will use this filter
	 */
	public void installFilter(JTextComponent... components) {
		if (components == null) {
			throw new NullPointerException();
		}
		for (JTextComponent component : components) {
			Document doc = component.getDocument();
			try {
				((AbstractDocument) doc).setDocumentFilter(this);
			} catch (ClassCastException cce) {
				throw new IllegalArgumentException("The given component has no AbstractDocument associated with it.", cce);
			}
		}
	}

	/**
	 * Remove this filter from the AbstractDocument
	 *
	 * @param component remove the filter from the specified text component
	 */
	public void uninstallFilter(JTextComponent component) {
		if (component == null) {
			throw new NullPointerException();
		}
		Document doc = component.getDocument();
		try {
			((AbstractDocument) doc).setDocumentFilter(this);
		} catch (ClassCastException cce) {
			throw new IllegalArgumentException("The given component has no AbstractDocument associated with it.", cce);
		}
	}

	/**
	 * Remove this filter from the AbstractDocument
	 *
	 * @param components remove the filter from the specified text components
	 */
	public void uninstallFilter(JTextComponent... components) {
		if (components == null) {
			throw new NullPointerException();
		}
		for (JTextComponent component : components) {
			uninstallFilter(component);
		}
	}

	/**
	 * Provide appropriate LAF feedback when a filter error occurs.
	 */
	public static void provideErrorFeedback() {

		if (LOOK_AND_FEEL == null) {
			Toolkit.getDefaultToolkit().beep();
		} else {
			KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			Component component = fm.getFocusOwner();
			LOOK_AND_FEEL.provideErrorFeedback(component);
		}
	}

	@Override
	public void insertString(DocumentFilter.FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
		if (nextFilterInChain == null) {
			super.insertString(fb, offs, str, a);
		} else {
			nextFilterInChain.insertString(fb, offs, str, a);
		}
	}

	@Override
	public void replace(DocumentFilter.FilterBypass fb, int offs, int length, String str, AttributeSet a)
			throws BadLocationException {
		if (nextFilterInChain == null) {
			super.replace(fb, offs, length, str, a);
		} else {
			nextFilterInChain.replace(fb, offs, length, str, a);
		}
	}

	@Override
	public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
			throws BadLocationException {
		if (nextFilterInChain == null) {
			super.remove(fb, offset, length);
		} else {
			nextFilterInChain.remove(fb, offset, length);
		}
	}
}
