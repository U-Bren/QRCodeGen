/*
 Copyright 2011, 2012, 2013 Stefan Ganzer

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
package qrcodegen.tools;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;
import javax.swing.MutableComboBoxModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Stefan Ganzer
 */
public class SwingTools {

	private static SwingTools INSTANCE;
	private final Logger logger;
	private final Toolkit toolkit;
	private final boolean osIsMac;

	SwingTools(Logger logger, Toolkit toolkit, String osName) {
		if (logger == null) {
			throw new NullPointerException();
		}
		if (toolkit == null) {
			throw new NullPointerException();
		}
		if (osName == null) {
			throw new NullPointerException();
		}
		this.logger = logger;
		this.toolkit = toolkit;

		String osNameLowerCase = osName.toLowerCase();
		if (osNameLowerCase.indexOf("mac") != -1) {
			osIsMac = true;
		} else {
			osIsMac = false;
		}

	}

	public static synchronized SwingTools getSingleton() {
		if (INSTANCE == null) {
			INSTANCE = new SwingTools(Logger.getLogger(SwingTools.class.getPackage().getName()), Toolkit.getDefaultToolkit(), System.getProperty("os.name"));
		}
		return INSTANCE;
	}

	/**
	 * Hitting the ESC-key will send the given JDialog a
	 * {@link WindowEvent WindowEvent.WINDOW_CLOSING} event.
	 *
	 * @param d
	 */
	public static void registerEscKeyForClosing(final JDialog d) {
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				d.dispatchEvent(new WindowEvent(d, WindowEvent.WINDOW_CLOSING));
			}
		};
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		d.getRootPane().registerKeyboardAction(actionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	public static void restrictSizeToScreen(Component c) throws HeadlessException {
		// Implicit null-check
		final int width = c.getWidth();
		final int height = c.getHeight();

		int newWidth = width;
		int newHeight = height;

		GraphicsConfiguration gc = c.getGraphicsConfiguration();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		Insets insets = toolkit.getScreenInsets(gc);
		Dimension availableScreenSize =
				new Dimension(screenSize.width - (insets.left + insets.right),
				screenSize.height - (insets.top + insets.bottom));

		if (c.getX() + width > availableScreenSize.width) {
			newWidth = availableScreenSize.width - c.getX();
		}
		if (c.getY() + c.getHeight() > availableScreenSize.height) {

			newHeight = availableScreenSize.height - c.getY();
		}
		if (width != newWidth || height != newHeight) {
			c.setSize(newWidth, newHeight);
		}
	}

	public static void setDocumentFilter(JTextComponent c, DocumentFilter f) {
		if (c == null) {
			throw new NullPointerException();
		}
		AbstractDocument ad = (AbstractDocument) c.getDocument();
		ad.setDocumentFilter(f);
	}

	public ImageIcon createImageIcon(Class<?> aClass, String source, String description) {
		URL url = aClass.getResource(source);
		if (url != null) {
			return new ImageIcon(url, description);
		} else {
			logger.log(Level.SEVERE, "Image requested by {0} not found: {1}", new Object[]{aClass, source});
			return null;
		}
	}

	public void setMnemonic(AbstractButton b, int mnemonic) {
		if (b == null) {
			throw new NullPointerException();
		}
		if (osIsMac()) {
			return;
		}
		b.setMnemonic(mnemonic);
	}

	private boolean osIsMac() {
		return osIsMac;
	}

	/**
	 * Inserts a new number into a sorted (low to high) MutableComboBoxModel.
	 * For convenience reasons this method takes a ComboBoxModel, and throws an
	 * IllegalArgumentException if it isn't a MutableComboBoxModel. This
	 * implementation is only for short lists, as it compares the given number
	 * with each list value starting at index 0, till it finds the correct
	 * position.
	 *
	 * This method assumes that the given number isn't already in the model.
	 * Otherwise the model will contain the number more than once when it
	 * returns.
	 *
	 * This method doesn't know about size constraints of the model, so the
	 * caller is responsible for supplying only valid numbers.
	 *
	 * @param <E>
	 * @param model
	 * @param number
	 */
	public static <E extends Number> void insertElementInRightOrder(ComboBoxModel model, E number) {
		if (model == null) {
			throw new NullPointerException();
		}
		if (!(model instanceof MutableComboBoxModel)) {
			throw new IllegalArgumentException();
		}
		if (number == null) {
			throw new NullPointerException();
		}
		double newValue = number.doubleValue();
		MutableComboBoxModel mutable = (MutableComboBoxModel) model;
		for (int i = 0; i < mutable.getSize(); i++) {
			if (newValue < ((Number) mutable.getElementAt(i)).doubleValue()) {
				// We expect the list to be sorted when we begin
				mutable.insertElementAt(number, i);
				return;
			}
		}
		// The new value is bigger than all values the list contains, or equal
		// to highest value, so we add it at the end.
		mutable.addElement(number);
	}
}
