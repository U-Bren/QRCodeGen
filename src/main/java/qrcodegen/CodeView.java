/*
 Copyright 2011, 2012 Stefan Ganzer

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
package qrcodegen;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import qrcodegen.tools.ImmutableDimension;

/**
 *
 * @author Stefan Ganzer
 */
class CodeView extends javax.swing.JDialog {

	/** The resourse bundle for this class QRView */
	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/CodeView");
	private final ActionListener copyListener;

	static CodeView newInstance(Frame parent, boolean modal) {
		CodeView cv = new CodeView(parent, modal);
		return cv;
	}

	/** Creates new form CodeView */
	CodeView(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		copyListener = new CopyListener(pictureLabel);
		int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_C, mask | KeyEvent.SHIFT_DOWN_MASK);
		copyQRCodeMenuItem.setAccelerator(ks);
	}

	/**
	 * Registers an action that is performed when TAB or Control-TAB is released
	 * when this CodeView is activated and has the focus. This can be used to
	 * return the focus to the main window.
	 *
	 * @param action an Action that is performed when TAB or Control-TAB is
	 * released when this CodeView is activated and has the focus.
	 *
	 * @throws NullPointerException if action is null
	 */
	void registerActionOnTabbing(Action action) {
		if (action == null) {
			throw new NullPointerException();
		}

		int mapType = JComponent.WHEN_IN_FOCUSED_WINDOW;
		String actionName = "requestFocusInMainWindow";
		KeyStroke tab = KeyStroke.getKeyStroke("released TAB");
		KeyStroke ctrlTab = KeyStroke.getKeyStroke("control released TAB");

		setFocusTraversalKeysEnabled(false);
		registerKeyBindingWithPictureLabel(mapType, actionName, tab, action);
		registerKeyBindingWithPictureLabel(mapType, actionName, ctrlTab, action);
	}

	void setPictureText(String s) {
		pictureLabel.setText(s);
	}

	String getPictureText() {
		return pictureLabel.getText();
	}

	void setQRCode(Image newImage) {
		ImageIcon icon;
		if (newImage == null) {
			icon = null;
		} else {
			icon = new ImageIcon(newImage);
			ImmutableDimension dim = new ImmutableDimension(icon.getIconWidth(), icon.getIconHeight());
			if (!pictureLabel.getSize().equals(dim.asAwtDimension())) {
				setPictureSize(dim);
			}
		}
		pictureLabel.setIcon(icon);
	}

	void setPictureSize(ImmutableDimension d) {
		// implicit null-check
		pictureLabel.setSize(d.asAwtDimension());
		pictureLabel.setPreferredSize(d.asAwtDimension());
		setTitle(String.format("QR Code (%1$d x %2$d)", d.getWidth(), d.getHeight()));
		pack();
	}

	void setPictureTransferHandler(TransferHandler newHandler) {
		pictureLabel.setTransferHandler(newHandler);
	}

	void addOpenPopupMouseListener(MouseListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		pictureLabel.addMouseListener(listener);
	}

	void addDragQRCodeMouseMotionListener(MouseMotionListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		pictureLabel.addMouseMotionListener(listener);
	}

	void addCopyQRCodeActionListener(ActionListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		copyQRCodeMenuItem.addActionListener(listener);
	}

	JLabel getPictureLabel() {
		return pictureLabel;
	}

	void addMouseListenerToPictureLabel(MouseListener listener) {
		pictureLabel.addMouseListener(listener);
	}

	void showPopup(Component c, int x, int y) {
		picturePopup.show(c, x, y);
	}

	ActionListener getCopyToClipboardActionListener() {
		return copyListener;
	}

	/**
	 *
	 * @param mapType
	 * @param actionName
	 * @param keyStroke
	 * @param action
	 *
	 * @throws IllegalArgumentException if mapType is not one of
	 * JComponent.WHEN_FOCUSED, WHEN_ANCESTOR_OF_FOCUSED_COMPONENT or
	 * WHEN_IN_FOCUSED_WINDOW
	 * @throws NullPointerException if any one of actionName, keyStroke or
	 * action is null
	 */
	void registerKeyBindingWithPictureLabel(int mapType, String actionName, KeyStroke keyStroke, Action action) {
		if (!isRegisterKeyboardActionMapType(mapType)) {
			throw new IllegalArgumentException("Illegal mapType: " + mapType);
		}
		if (actionName == null) {
			throw new NullPointerException();
		}
		if (keyStroke == null) {
			throw new NullPointerException();
		}
		if (action == null) {
			throw new NullPointerException();
		}
		pictureLabel.getInputMap(mapType).put(keyStroke, actionName);
		pictureLabel.getActionMap().put(actionName, action);
	}

	private boolean isRegisterKeyboardActionMapType(int mapType) {
		return mapType == JComponent.WHEN_FOCUSED
				|| mapType == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
				|| mapType == JComponent.WHEN_IN_FOCUSED_WINDOW;
	}

	/** This method is called from within the constructor to initialize the
	 * form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        picturePopup = new javax.swing.JPopupMenu();
        copyQRCodeMenuItem = new javax.swing.JMenuItem();
        pictureLabel = new javax.swing.JLabel();

        copyQRCodeMenuItem.setText(RES.getString("CodeView.copyQRCodeMenuItem.text")); // NOI18N
        picturePopup.add(copyQRCodeMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(RES.getString("CodeView.title")); // NOI18N

        pictureLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pictureLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pictureLabel.setComponentPopupMenu(picturePopup);
        pictureLabel.setPreferredSize(new java.awt.Dimension(120, 120));
        pictureLabel.setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pictureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pictureLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default
		 * look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(CodeView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(CodeView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(CodeView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(CodeView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the dialog */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				CodeView dialog = new CodeView(new javax.swing.JFrame(), true);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem copyQRCodeMenuItem;
    private javax.swing.JLabel pictureLabel;
    private javax.swing.JPopupMenu picturePopup;
    // End of variables declaration//GEN-END:variables
}
