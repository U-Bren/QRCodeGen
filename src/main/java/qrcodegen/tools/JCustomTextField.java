/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.tools;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.TransferHandler;
import javax.swing.text.Document;

/**
 *
 * @author Stefan Ganzer
 */
public final class JCustomTextField extends JTextField {

	private static final long serialVersionUID = 1L;
	private static final TextCopyHandler TRANSFER_HANDLER = new TextCopyHandler();
	/** The singleton instance of the system clipboard */
	private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenuItem copyMenuItem = new JMenuItem();
	private boolean enableCopyMenuItem = false;
	private boolean handleExportFailedException;
	private String exportFailedDialogTitle;
	private String exportFailedDialogMessage;
	private String originalText;
	private int maxLength = -1;

	public JCustomTextField() {
		super(null, null, 0);
		setTransferHandler(TRANSFER_HANDLER);
		initPopupMenu();
		setComponentPopupMenu(popupMenu);
	}

	private void initPopupMenu() {
		popupMenu.setEnabled(false);
		popupMenu.add(copyMenuItem);
		addMouseListener(new PopupListener());
		copyMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TransferHandler handler = JCustomTextField.this.getTransferHandler();
				try {
					handler.exportToClipboard(JCustomTextField.this, CLIPBOARD, TransferHandler.COPY);
				} catch (IllegalStateException ise) {
					if (handleExportFailedException) {
						JOptionPane.showMessageDialog(JCustomTextField.this.getTopLevelAncestor(),
								exportFailedDialogMessage, exportFailedDialogTitle,
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						throw new IllegalStateException(ise);
					}

				}
			}
		});
	}

	@Override
	public void setText(String t) {
		originalText = t;
		setMenuItemEnabled();
		// this is save to be called by the super-constructor with t == null
		// or t == "".
		// maxLength is uninitialized if this method is called by the superconstructors,
		// so this class has only one constructor that doesn't cause this
		// method to be called during construction.
		// This could fail in the future.
		super.setText(StaticTools.clipString(t, maxLength));
	}

	private void setMenuItemEnabled() {
		boolean b = enableCopyMenuItem && originalText != null && !originalText.isEmpty();
		copyMenuItem.setEnabled(b);
	}

	public String getOriginalText() {
		return originalText;
	}

	public void setMenuText(String t) {
		copyMenuItem.setText(t);
	}

	public String getMenuText() {
		return copyMenuItem.getText();
	}

	public void setPopupMenuEnabled(boolean b) {
		popupMenu.setEnabled(b);
	}

	public boolean isPopupMenuEnabled() {
		return popupMenu.isEnabled();
	}

	public void setCopyMenuItemEnabled(boolean b) {
		enableCopyMenuItem = b;
		setMenuItemEnabled();
	}

	public void setClipLength(int maxLength) {
		if (maxLength < -1) {
			throw new IllegalArgumentException("maxLength must be >= -1, but is: " + maxLength);
		}
		this.maxLength = maxLength;
		setText(getOriginalText());
	}

	public int getClipLength() {
		return maxLength;
	}

	public void setHandleExportFailedException(boolean b) {
		handleExportFailedException = b;
	}

	public boolean getHandelExportFailedException() {
		return handleExportFailedException;
	}

	public void setExportFailedDialogTitle(String newTitle) {
		exportFailedDialogTitle = newTitle;
	}

	public String getExportFailedDialogTitle() {
		return exportFailedDialogTitle;
	}

	public void setExportFailedDialogMessage(String newMessage) {
		exportFailedDialogMessage = newMessage;
	}

	public String getExportFailedDialogMessage() {
		return exportFailedDialogMessage;
	}

	private final class PopupListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// Under Windows, only few applications accept the DataFlavor.imageFlavor.
			// You can drag the image into Word and OpenOffice / LibreOffice, for instance,
			// but not into IrfanView or Gimp. But the transfer via the system clipboard,
			// initiated by the popup menu, works for them, too.
			// TODO Drag-support to native applications?
			// http://www.javaworld.com/javaworld/jw-08-1999/jw-08-draganddrop.html
			// http://www.rockhoppertech.com/java-drag-and-drop-faq.html
			JComponent comp = (JComponent) e.getSource();
			TransferHandler th = comp.getTransferHandler();
			th.exportAsDrag(comp, e, TransferHandler.COPY);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private static class TextCopyHandler extends TransferHandler {

		/** The data flavor this transfer handler supports. */
		private static final DataFlavor FLAVOR = DataFlavor.stringFlavor;
		/** The action this transfer handler supports. */
		private static final int SOURCE_ACTIONS = TransferHandler.COPY;
		private static final long serialVersionUID = 1L;
		/** True if drag AND drop is supported, false if only drag is supported. */
		private final boolean canImport;

		/**
		 * Returns an TextCopyHandler instance that only supports the 'drag'
		 * action of text from a JTextField.
		 * {@link #canImport(TransferHandler.TransferSupport)} for this instance
		 * will always return {@code false}.
		 */
		private TextCopyHandler() {
			this(false);
		}

		/**
		 * Returns an TextCopyHandler instance that supports the 'drag' action
		 * of text from a JTextField, and, if canImport is {@code true}, also
		 * the 'drop' action of text on a JTextField.
		 *
		 * @param canImport besides 'drag' also 'drop' is enabled for this
		 * TextCopyHandler if {@code true}, or only 'drag', but not 'drop' is
		 * enabled if {@code false}.
		 */
		private TextCopyHandler(boolean canImport) {
			this.canImport = canImport;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return SOURCE_ACTIONS;
		}

		@Override
		public boolean canImport(TransferHandler.TransferSupport support) {
			if (!support.isDataFlavorSupported(FLAVOR)) {
				return false;
			}
			return canImport;
		}

		@Override
		public Transferable createTransferable(JComponent jComponent) {
			if (jComponent instanceof JCustomTextField) {
				JCustomTextField field = (JCustomTextField) jComponent;
				String text = field.getOriginalText();
				return new StringSelection(text);
			}
			return null;
		}

		@Override
		public boolean importData(TransferHandler.TransferSupport support) {
			if (support == null) {
				throw new NullPointerException();
			}

			Component component = support.getComponent();
			if (component instanceof JTextField) {
				JTextField field = (JTextField) component;
				if (support.isDataFlavorSupported(FLAVOR)) {
					try {
						// The representing class for DataFlavor.stringFlavor is java.lang.String,
						// so this cast is safe
						String text = (String) support.getTransferable().getTransferData(FLAVOR);
						field.setText(text);
						return true;
					} catch (UnsupportedFlavorException ignored) {
						//this shouldn't happen, as we made sure we support this flavor
						throw new AssertionError(ignored);
					} catch (IOException ignored) {
						// nothing we can do about here
						Logger.getLogger("qrcodegen").throwing("qrcodegen.tools.JCustomTextField", "boolean importData(TransferHandler.TransferSupport)", ignored);
					}
				}
			}
			return false;
		}
	}
}
