/*
 * Source: https://tips4java.wordpress.com/2010/02/21/formatted-text-field-tips/
 */
package qrcodegen.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFormattedTextField;
import javax.swing.SwingUtilities;


/**
 * This class will retain the caret positioning at the character where the mouse
 * was clicked.
 */
public class CaretPositionListener implements MouseListener {

	private boolean dynamicFormatting;

	/**
	 * Default constructor.
	 */
	public CaretPositionListener() {
	}

	/**
	 * Convenience constructor. This class is automatically added as a
	 * MouseListener to the specified formatted text fields.
	 */
	public CaretPositionListener(JFormattedTextField... components) {
		registerComponent(components);
	}

	/**
	 *
	 */
	public boolean isDynamicFormatting() {
		return dynamicFormatting;
	}

	/**
	 * Indicates that the formatting of the text in the formatted text field can
	 * change depending on whether the	text field has focus or not. The listner
	 * must be aware of this so the proper caret position can be calculated.
	 *
	 * @param dynamicFormatting when true dynamic formatting must be considered
	 */
	public void setDynamicFormatting(boolean dynamicFormatting) {
		this.dynamicFormatting = dynamicFormatting;
	}

	/**
	 * Remove listeners from the specified component
	 *
	 * @param component the component the listeners are removed from
	 */
	public void deregisterComponent(JFormattedTextField... components) {
		for (JFormattedTextField component : components) {
			component.removeMouseListener(this);
		}
	}

	/**
	 * Add the required listeners to the specified component
	 *
	 * @param component the component the listeners are added to
	 */
	public final void registerComponent(JFormattedTextField... components) {
		for (JFormattedTextField component : components) {
			component.addMouseListener(this);
		}
	}

	@Override
	public void mousePressed(final MouseEvent me) {
		//  Ignore double click to allow default behavour which will
		//  select the entire text
		if (me.getClickCount() > 1) {
			return;
		}

		final JFormattedTextField ftf = (JFormattedTextField) me.getSource();

		if (dynamicFormatting
				&& ftf.getValue() != null) {
			determineCaretPosition(ftf);
		} else {
			int offset = ftf.getCaretPosition();
			setCaretPosition(ftf, offset);
		}
	}

	private void determineCaretPosition(final JFormattedTextField ftf) {
		assert ftf != null;
		assert ftf.getValue() != null;
		
		int offset = ftf.getCaretPosition();
		String text = ftf.getText();
		String value = ftf.getValue().toString();

		if (text.equals(value)) {
			setCaretPosition(ftf, offset);
			return;
		}

		int textIndex = 0;
		int valueIndex = 0;

		//  Exclude formatting characters

		while (valueIndex < offset) {
			if (text.charAt(textIndex) == value.charAt(valueIndex)) {
				textIndex++;
				valueIndex++;
			} else {
				offset--;
				textIndex++;
			}
		}

		setCaretPosition(ftf, offset);
	}

	private void setCaretPosition(final JFormattedTextField ftf, final int offset) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ftf.setCaretPosition(offset);
				} catch (IllegalArgumentException e) {
				}
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
