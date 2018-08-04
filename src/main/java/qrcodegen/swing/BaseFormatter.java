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

import java.beans.PropertyChangeSupport;
import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DocumentFilter;
import qrcodegen.documentfilter.ChainedDocumentFilter;

/**
 *
 * @author Stefan Ganzer
 */
public class BaseFormatter extends DefaultFormatter {

	public static final String MAX_VALUE_LENGTH_PROPERTY = "MaxValueLength";
	public static final String INVALID_CHARACTERS_PROPERTY = "InvalidCharacters";
	public static final String VALID_CHARACTERS_PROPERTY = "ValidCharacters";
	static final String EMPTY_STRING = "";
	private static final long serialVersionUID = 1L;
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private int caretPosition;
	private boolean isInstalled;
	private int maxValueLength;
	private DocumentFilter documentFilter;
	private String invalidCharacters;
	private String validCharacters;

	public BaseFormatter() {
		setOverwriteMode(false);
		setCommitsOnValidEdit(true);
		setAllowsInvalid(false);
		caretPosition = 0;
		isInstalled = false;
		maxValueLength = -1;
		invalidCharacters = null;
		validCharacters = null;
	}

	public void setMaxValueLength(int maxLength) {
		if (maxLength < -1) {
			throw new IllegalArgumentException(maxLength + " < -1");
		}
		int oldMaxValueLength = this.maxValueLength;
		this.maxValueLength = maxLength;
		pcs.firePropertyChange(MAX_VALUE_LENGTH_PROPERTY, oldMaxValueLength, maxLength);
	}

	public int getMaxValueLength() {
		return maxValueLength;
	}

	public void setInvalidCharacters(String s) {
		if(s == null){
			throw new NullPointerException();
		}
		setInvalidCharactersValue(s);
	}
	
	private void setInvalidCharactersValue(String s){
		String oldIllegalCharacters = this.invalidCharacters;
		this.invalidCharacters = s;
		pcs.firePropertyChange(INVALID_CHARACTERS_PROPERTY, oldIllegalCharacters, s);		
	}
	
	public void clearInvalidCharacters(){
		setInvalidCharactersValue(null);
	}

	public String getInvalidCharacters() {
		return invalidCharacters;
	}

	public void setValidCharacters(String s) {
		if(s == null){
			throw new NullPointerException();
		}
		setValidCharactersValue(s);
	}
	
	private void setValidCharactersValue(String s){
		String oldLegalCharacters = this.validCharacters;
		this.validCharacters = s;
		pcs.firePropertyChange(VALID_CHARACTERS_PROPERTY, oldLegalCharacters, s);	
	}
	
	public void clearValidCharacters(){
		setValidCharactersValue(null);
	}

	public String getValidCharacters() {
		return validCharacters;
	}

	@Override
	public void install(final JFormattedTextField ftf) {
		super.install(ftf);
		if (ftf != null) {
			restoreCaretPositionOn(ftf);
			isInstalled = true;
		}
	}

	@Override
	public void uninstall() {
		JFormattedTextField ftf = getFormattedTextField();
		if (ftf != null) {
			if (isInstalled) {
				saveCaretPositionFrom(ftf);
				isInstalled = false;
			}
		}
		super.uninstall();
	}

	@Override
	protected DocumentFilter getDocumentFilter() {
		if (documentFilter == null) {
			documentFilter = new BaseDocumentFilter();
		}
		return documentFilter;
	}

	private void restoreCaretPositionOn(JFormattedTextField ftf) {
		Object value = ftf.getValue();
		int max = value == null ? 0 : value.toString().length();
		int pos;
		if (caretPosition > max) {
			pos = max;
		} else {
			pos = caretPosition;
		}
		ftf.setCaretPosition(pos);
	}

	private void saveCaretPositionFrom(JFormattedTextField ftf) {
		caretPosition = ftf.getCaretPosition();
	}

	void commitEdit() throws ParseException {
		JFormattedTextField tf = getFormattedTextField();
		if (tf != null) {
			tf.commitEdit();
		}
	}

	void updateValue() {
		try {
			if (getCommitsOnValidEdit()) {
				commitEdit();
			}
			setEditValid(true);
		} catch (ParseException pe) {
			setEditValid(false);
		}
	}

	boolean isValidInput(String input) {
		for (char c : input.toCharArray()) {
			if (!isInLegalCharacters(c) || isInIllegalCharacters(c)) {
				return false;
			}
		}
		return true;
	}

	private boolean isInLegalCharacters(char c) {
		if (validCharacters == null) {
			return true;
		} else {
			return validCharacters.indexOf(c) != -1;
		}
	}

	private boolean isInIllegalCharacters(char c) {
		if (invalidCharacters == null) {
			return false;
		} else {
			return invalidCharacters.indexOf(c) != -1;
		}
	}

	// package-visible for testability
	class BaseDocumentFilter extends ChainedDocumentFilter {

		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String str, AttributeSet a) throws BadLocationException {
			if (str == null) {
				super.insertString(fb, offset, str, a);
			} else if (!isValidInput(str) || (getMaxValueLength() != -1 && fb.getDocument().getLength() + str.length() > getMaxValueLength())) {
				provideErrorFeedback();
				return;
			} else {
				super.insertString(fb, offset, str, a);
			}
			updateValue();
		}

		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet a) throws BadLocationException {

			if (str == null) {
				super.replace(fb, offset, length, str, a);
			} else if (!isValidInput(str) || (getMaxValueLength() != -1 && fb.getDocument().getLength() - length + str.length() > getMaxValueLength())) {
				provideErrorFeedback();
				return;
			} else {
				super.replace(fb, offset, length, str, a);
			}
			updateValue();
		}

		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
			super.remove(fb, offset, length);
			updateValue();
		}
	}
}
