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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;
import javax.swing.text.NavigationFilter;
import javax.swing.text.Position;
import qrcodegen.documentfilter.ChainedDocumentFilter;

/**
 *
 * @author Stefan Ganzer
 */
public abstract class AbstractGroupFormatter extends BaseFormatter {

	public static final String CONTAINS_LITERAL_CHARACTERS_PROPERTY = "ContainsLiteralCharacters";
	public static final String PLACEHOLDER_CHARACTER_PROPERTY = "PlaceholderCharacter";
	public static final String GROUP_SIZE_PROPERTY = "GroupSize";
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private GroupDocumentFilter documentFilter;
	private NavigationFilter navigationFilter;
	private char groupSeparator;
	private String groupSeparatorString;
	private boolean containsLiteralChars;
	private int groupSize;
	private int caretPosition;
	private boolean isInstalled;

	public AbstractGroupFormatter() {
		this(4);
	}

	public AbstractGroupFormatter(int groupSize) {
		if (groupSize < 1) {
			throw new IllegalArgumentException(groupSize + " < 1");
		}
		this.groupSize = groupSize;
		this.groupSeparator = ' '; // space character
		this.groupSeparatorString = String.valueOf(groupSeparator);
		this.containsLiteralChars = false;
		this.documentFilter = new GroupDocumentFilter();
		this.navigationFilter = new DefaultNavigationFilter();
		this.isInstalled = false;
		this.caretPosition = 0;
		setOverwriteMode(false);
		setCommitsOnValidEdit(true);
		setAllowsInvalid(false);
	}

	public void setGroupSize(int groupSize) {
		if (groupSize < 1) {
			throw new IllegalArgumentException(groupSize + "< 1");
		}
		int oldGroupSize = this.groupSize;
		this.groupSize = groupSize;
		if (oldGroupSize != groupSize) {
			reformat();
			pcs.firePropertyChange(GROUP_SIZE_PROPERTY, oldGroupSize, groupSize);
		}
	}

	private void reformat() {
		JFormattedTextField tf = getFormattedTextField();
		if (tf != null) {
			tf.setValue(tf.getValue());
		}
	}

	public int getGroupSize() {
		return groupSize;
	}

	public void setPlaceholderCharacter(char placeholder) {
		char oldPlaceholder = this.groupSeparator;
		this.groupSeparator = placeholder;
		if (oldPlaceholder != placeholder) {
			groupSeparatorString = String.valueOf(placeholder);
			reformat();
			pcs.firePropertyChange(PLACEHOLDER_CHARACTER_PROPERTY, oldPlaceholder, placeholder);
		}
	}

	public char getPlaceholderCharacter() {
		return this.groupSeparator;
	}

	String getPlaceholderString() {
		return groupSeparatorString;
	}

	public void setValueContainsLiteralCharacters(boolean containsLiteralChars) {
		boolean oldContainsLiteralChars = this.containsLiteralChars;
		this.containsLiteralChars = containsLiteralChars;
		pcs.firePropertyChange(CONTAINS_LITERAL_CHARACTERS_PROPERTY, oldContainsLiteralChars, containsLiteralChars);
	}

	public boolean getValueContainsLiteralCharacters() {
		return containsLiteralChars;
	}

	@Override
	public Object stringToValue(String text) throws ParseException {
		String value = text;
		if (!getValueContainsLiteralCharacters()) {
			value = stripLiteralCharacters(value);
		}
		return super.stringToValue(value);
	}

	//From DefaultFormatter
	private String stripLiteralCharacters(String text) {
		StringBuilder sb = null;
		int indexAfterLastGroupSeparator = 0;

		for (int currentIndex = 0, max = text.length(); currentIndex < max; currentIndex++) {
			if (groupSeparator == text.charAt(currentIndex)) {
				if (sb == null) {
					sb = new StringBuilder(max);
					if (currentIndex > 0) {
						sb.append(text.substring(0, currentIndex));
					}
				} else if (indexAfterLastGroupSeparator != currentIndex) {
					sb.append(text.substring(indexAfterLastGroupSeparator, currentIndex));
				}
				indexAfterLastGroupSeparator = currentIndex + 1;
			}
		}
		if (sb == null) {
			// Assume the mask isn't all literals.
			return text;
		} else if (indexAfterLastGroupSeparator != text.length()) {
			//Removed due to findbugs report.
//			if (sb == null) {
//				return text.substring(indexAfterLastGroupSeparator);
//			}
			sb.append(text.substring(indexAfterLastGroupSeparator));
		}
		return sb.toString();
	}

	@Override
	public String valueToString(Object value) {
		String sValue = (value == null) ? EMPTY_STRING : value.toString();
		return format(sValue, 0);
	}

	@Override
	public void install(final JFormattedTextField ftf) {
		super.install(ftf);
		if (ftf != null) {
			Object value = ftf.getValue();

			try {
				stringToValue(valueToString(value));
			} catch (ParseException pe) {
				Logger.getLogger(AbstractGroupFormatter.class.getName()).log(Level.WARNING, null, pe);
				setEditValid(false);
			}
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

	private void restoreCaretPositionOn(JFormattedTextField ftf) {
		Object value = ftf.getValue();
		int max = value == null ? 0 : valueToString(value).length();
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

	@Override
	protected DocumentFilter getDocumentFilter() {
		return documentFilter;
	}

	@Override
	protected NavigationFilter getNavigationFilter() {
		return navigationFilter;
	}

	private void setCaretPosition(int position) {
		assert position >= 0 : position;
		JFormattedTextField ftf = getFormattedTextField();
		if (ftf != null) {
			ftf.setCaretPosition(position);
		}
	}

	private int getNextCaretPosition(int offset) {
		int newOffset = offset;
		while (!isNavigatable(newOffset, SwingConstants.WEST) && newOffset > 0) {
			newOffset = newOffset - 1;
		}
		return newOffset;
	}

	private boolean isNavigatable(int offset, int direction) {
		boolean navigatable;
		if (direction == SwingConstants.EAST) {
			navigatable = offset % (groupSize + 1) != 0;
		} else if (direction == SwingConstants.WEST) {
			navigatable = groupSize - (offset % (groupSize + 1)) != 0;
		} else {
			navigatable = true;
		}
		return navigatable;
	}

	private boolean isNavigatable(int offset, int direction, Position.Bias bias) {
		boolean navigatable;
		if (direction == SwingConstants.EAST) {
			navigatable = offset % (groupSize + 1) != 0;
		} else if (direction == SwingConstants.WEST) {
			navigatable = groupSize - (offset % (groupSize + 1)) != 0;
		} else {
			navigatable = true;
		}
		return navigatable;
	}

	abstract String format(String input, int globalOffset);

	private class DefaultNavigationFilter extends NavigationFilter {

		@Override
		public void setDot(FilterBypass fb, int dot, Position.Bias bias) {
			fb.setDot(dot, bias);
		}

		@Override
		public void moveDot(FilterBypass fb, int dot, Position.Bias bias) {
			fb.moveDot(dot, bias);
		}

		// From DefaultFormatter
		@Override
		public int getNextVisualPositionFrom(JTextComponent text, int pos,
				Position.Bias bias,
				int direction,
				Position.Bias[] biasRet)
				throws BadLocationException {

			int value = text.getUI().getNextVisualPositionFrom(text, pos, bias,
					direction, biasRet);

			if (value == -1) {
				return -1;
			}
			if (!getAllowsInvalid() && (direction == SwingConstants.EAST
					|| direction == SwingConstants.WEST)) {
				int last = -1;

				while (!isNavigatable(value, direction) && value != last) {
					last = value;
					value = text.getUI().getNextVisualPositionFrom(
							text, value, bias, direction, biasRet);
				}
				int max = getFormattedTextField().getDocument().getLength();
				if (last == value || value == max) {
					if (value == 0) {
						biasRet[0] = Position.Bias.Forward;
						value = 0;
					}
					if (value >= max && max > 0) {
						// Pending: should not assume forward!
						biasRet[0] = Position.Bias.Forward;
						value = max;
					}
				}
			}
			return value;
		}
	}

	// Package-private for testability
	class GroupDocumentFilter extends ChainedDocumentFilter {

		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String str, AttributeSet a) throws BadLocationException {
			if (str == null) {
				super.insertString(fb, offset, str, a);
			} else if (!isValidInput(str)) {
				provideErrorFeedback();
			} else {
				final int docLength = fb.getDocument().getLength();
				String insertion = format(str, offset);

				//reformat tail, if necessary
				final int tailStart = offset;
				final int maxValueLength = getMaxValueLength();
				if (tailStart < docLength) {
					String tail = fb.getDocument().getText(tailStart, docLength - tailStart);

					String start = stripLiteralCharacters(fb.getDocument().getText(0, offset));
					if (maxValueLength != -1 && start.length() + stripLiteralCharacters(insertion).length() + stripLiteralCharacters(tail).length() > maxValueLength) {
						provideErrorFeedback();
						return;
					}

					String formattedTail = format(tail, offset + insertion.length());
					fb.replace(tailStart, tail.length(), formattedTail, a);
				} else {
					String start = stripLiteralCharacters(fb.getDocument().getText(0, offset));
					if (maxValueLength != -1 && start.length() + stripLiteralCharacters(insertion).length() > maxValueLength) {
						provideErrorFeedback();
						return;
					}
				}
				//insert new part
				super.insertString(fb, offset, insertion, a);
				AbstractGroupFormatter.this.setCaretPosition(offset + insertion.length());
				updateValue();
			}
		}

		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet a) throws BadLocationException {

			if (str == null) {
				super.replace(fb, offset, length, str, a);
			} else if (!isValidInput(str)) {
				provideErrorFeedback();
			} else {
				final int docLength = fb.getDocument().getLength();

				String insertion = format(str, offset);

				//reformat tail, if necessary
				final int tailStart = offset + length;
				final int maxValueLength = getMaxValueLength();
				if (tailStart < docLength) {
					String tail = fb.getDocument().getText(tailStart, docLength - tailStart);

					String start = stripLiteralCharacters(fb.getDocument().getText(0, offset));
					if (maxValueLength != -1 && start.length() + stripLiteralCharacters(insertion).length() + stripLiteralCharacters(tail).length() > maxValueLength) {
						provideErrorFeedback();
						return;
					}

					String formattedTail = format(tail, offset + insertion.length());
					fb.replace(tailStart, tail.length(), formattedTail, a);
				} else {
					String start = stripLiteralCharacters(fb.getDocument().getText(0, offset));
					if (maxValueLength != -1 && start.length() + stripLiteralCharacters(insertion).length() > maxValueLength) {
						provideErrorFeedback();
						return;
					}
				}
				//insert new part
				super.replace(fb, offset, length, insertion, a);
				AbstractGroupFormatter.this.setCaretPosition(offset + insertion.length());
				updateValue();
			}
		}

		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
			final int docLength = fb.getDocument().getLength();

			//reformat tail, if necessary
			int removeLength;
			int removeOffset;
			if (groupSeparatorString.equals(fb.getDocument().getText(offset, 1))) {
				// cursor is after the last separator, user deletes one character
				// "abcd-"   -> "abc"
				if (length == 1) {
					removeLength = length + 1;
					removeOffset = offset - 1;
					// user selected more than one character for deletion, here: efg
					// "abcd-efg"   -> "abcd-"
				} else {
					removeLength = length - 1;
					removeOffset = offset + 1;
				}
				// "abcd-ef"   -> "abcd-e"
				// "abcd-efg"  -> "abcd-e"
			} else {
				removeLength = length;
				removeOffset = offset;
			}
			final int tailStart = removeOffset + removeLength;
			if (tailStart < docLength) {
				String tail = fb.getDocument().getText(tailStart, docLength - tailStart);
				String formattedTail = format(tail, removeOffset);
				fb.replace(tailStart, docLength - tailStart, formattedTail, null);
			}

			super.remove(fb, removeOffset, removeLength);
			updateValue();
			setCaretPosition(getNextCaretPosition(removeOffset));
		}
	}
}
