/*
 * Copyright (C) 2012 Stefan Ganzer
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
package qrcodegen;

import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import qrcodegen.tools.StaticTools;
import qrcodegen.tools.SwingTools;

/**
 *
 * @author Stefan Ganzer
 */
public class ErrorDialog extends javax.swing.JDialog {

	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/ErrorDialog");
	private static final String CONTENT_HTML = "text/html";
	private final Type type;
	private final MessageType messageType;

	public enum Type {

		CLOSE(RES.getString("CLOSE"), StaticTools.getKeyCodeFromResourceBundle(RES, "MNEMONIC_CLOSE")),
		EXIT(RES.getString("EXIT"), StaticTools.getKeyCodeFromResourceBundle(RES, "MNEMONIC_EXIT"));
		private final String text;
		private final int keyCode;

		private Type(String s, int keyCode) {
			assert s != null;
			this.text = s;
			this.keyCode = keyCode;
		}

		public String getText() {
			return text;
		}

		public int getKeyCode() {
			return keyCode;
		}
	}

	public enum MessageType {

		ERROR("OptionPane.errorIcon"),
		INFORMATION("OptionPane.informationIcon"),
		WARNING("OptionPane.warningIcon"),
		QUESTION("OptionPane.questionIcon"),
		PLAIN("");
		private final String key;

		private MessageType(String key) {
			this.key = key;
		}

		/**
		 * Returns the JOptionPane icon associated with this enum constant.
		 *
		 * @return the JOptionPane icon associated with this enum constant, or
		 * null if there is no icon for this enum constant (PLAIN)
		 */
		public Icon getIcon() {
			return UIManager.getIcon(key);
		}

		/**
		 * Returns the icon as {@link Image}.
		 *
		 * @return the icon as image, or null if there is no image for this
		 * icon, or if there is no icon in the first place
		 */
		public Image getImage() {
			Icon icon = getIcon();
			if (icon instanceof ImageIcon) {
				ImageIcon imageIcon = (ImageIcon) icon;
				return imageIcon.getImage();
			}
			return null;
		}

		/**
		 * Returns the key associated with this enum constant.
		 *
		 * @return the key associated with this enum constant
		 */
		public String getKey() {
			return key;
		}
	}

	public ErrorDialog() {
		this(null, null, true, Type.CLOSE, MessageType.PLAIN);

	}

	public ErrorDialog(Frame owner, boolean modal) {
		this(owner, null, modal, Type.CLOSE, MessageType.PLAIN);
	}

	public ErrorDialog(Frame owner, String title, boolean modal, Type type, MessageType messageType) {
		super(owner, title, modal);
		if (type == null) {
			throw new NullPointerException();
		}
		if (messageType == null) {
			throw new NullPointerException();
		}
		this.type = type;
		this.messageType = messageType;
		initDialog();
	}

	public ErrorDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc, Type type, MessageType messageType) {
		super(owner, title, modal, gc);
		if (type == null) {
			throw new NullPointerException();
		}
		if (messageType == null) {
			throw new NullPointerException();
		}
		this.type = type;
		this.messageType = messageType;
		initDialog();
	}

	private void initDialog() {
		initComponents();

		SwingTools.registerEscKeyForClosing(this);

		iconLabel.setIcon(messageType.getIcon());
		iconLabel.setText("");

		detailedMessage.setContentType(CONTENT_HTML);

		closeButton.setAction(new CloseAction(type));

		copyButton.addActionListener(new CopyListener(detailedMessage));
		copyButton.setMnemonic(StaticTools.getKeyCodeFromResourceBundle(RES, "MNEMONIC_COPY_TO_CLIPBOARD"));
	}

	public void setContentType(String type) {
		detailedMessage.setContentType(type);
	}

	public void setDetailedMessage(String text) {
		detailedMessage.setText(text);
		detailedMessage.setCaretPosition(0);
		pack();
	}

	public void setErrorMessage(String title, String basicErrorMessage, String detailedErrorMessage, Level level, Throwable t) {
		detailedMessage.setContentType(CONTENT_HTML);
		setDetailedMessage(getDetailsAsHTML(title, basicErrorMessage, detailedErrorMessage, level, t));
	}

	public void showDialog() {
		setLocationRelativeTo(null);
		setVisible(true);

	}

	private class CloseAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private CloseAction(Type type) {
			super(type.getText());
			putValue(MNEMONIC_KEY, type.getKeyCode());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			ErrorDialog.this.dispose();
		}
	}

	/*
	 * The following code is based on code from
	 * org.jdesktop.swingx.plaf.basic.BasicErrorPaneUI.
	 */
	/*
	 * $Id: BasicErrorPaneUI.java 3927 2011-02-22 16:34:11Z kleopatra $
	 *
	 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
	 * Santa Clara, California 95054, U.S.A. All rights reserved.
	 *
	 * This library is free software; you can redistribute it and/or
	 * modify it under the terms of the GNU Lesser General Public
	 * License as published by the Free Software Foundation; either
	 * version 2.1 of the License, or (at your option) any later version.
	 *
	 * This library is distributed in the hope that it will be useful,
	 * but WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	 * Lesser General Public License for more details.
	 *
	 * You should have received a copy of the GNU Lesser General Public
	 * License along with this library; if not, write to the Free Software
	 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
	 */
	/**
	 * Creates and returns HTML representing the details of this incident info.
	 * This method is only called if the details needs to be generated: ie: the
	 * detailed error message property of the incident info is null.
	 *
	 * @param title the title of this incident. May be null.
	 * @param level the error level of this incident. May be null.
	 * @param t the throwable that caused this incident
	 *
	 * @return HTML representing the details of this incident info. Returns null
	 * if t is null
	 */
	private static String getDetailsAsHTML(String title, String basicErrorMessage, String detailedErrorMessage, Level level, Throwable t) {
		if (t != null) {
			Throwable localT = t;
			//convert the stacktrace into a more pleasent bit of HTML
			StringBuilder html = new StringBuilder("<html>");
			html.append("<h2>").append(escapeXml(title)).append("</h2>");
			html.append("<HR size='1' noshade>");
			html.append("<div></div>");
			html.append("<b>Basic Message:</b>");
			html.append("<pre>");
			html.append("    ").append(escapeXml(basicErrorMessage));
			html.append("</pre>");
			html.append("<b>Detailed Message:</b>");
			html.append("<pre>");
			html.append("    ").append(escapeXml(detailedErrorMessage));
			html.append("</pre>");
			html.append("<b>Exception:</b>");
			html.append("<pre>");
			html.append("    ").append(escapeXml(localT.toString()));
			html.append("</pre>");
			html.append("<b>Level:</b>");
			html.append("<pre>");
			html.append("    ").append(level);
			html.append("</pre>");
			html.append("<b>Stack Trace:</b>");
			while (localT != null) {
				html.append("<h4>").append(escapeXml(localT.toString())).append("</h4>");
				html.append("<pre>");
				for (StackTraceElement el : localT.getStackTrace()) {
					html.append("    ").append(el.toString().replace("<init>", "&lt;init&gt;")).append("\n");
				}
				html.append("</pre>");
				localT = localT.getCause();
			}
			html.append("</html>");
			return html.toString();
		} else {
			return null;
		}
	}

	/**
	 * Converts the incoming string to an escaped output string. This method is
	 * far from perfect, only escaping &lt;, &gt; and &amp; characters
	 *
	 * @return the incoming string as an escaped output string. Returns an empty
	 * string if input is null.
	 */
	private static String escapeXml(String input) {
		String result;
		if (input == null) {
			result = "";
		} else {
			result = input.replace("&", "&amp;");
			result = result.replace("<", "&lt;");
			result = result.replace(">", "&gt;");
		}
		return result;
	}

	/** This method is called from within the constructor to initialize the
	 * form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        closeButton = new JButton();
        jScrollPane3 = new JScrollPane();
        detailedMessage = new JEditorPane();
        copyButton = new JButton();
        iconLabel = new JLabel();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        closeButton.setText(RES.getString("ErrorDialog.closeButton.text")); // NOI18N

        jScrollPane3.setViewportView(detailedMessage);

        copyButton.setText(RES.getString("ErrorDialog.copyButton.text")); // NOI18N

        iconLabel.setText(RES.getString("ErrorDialog.iconLabel.text")); // NOI18N

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iconLabel)
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(copyButton)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(closeButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jScrollPane3, GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(iconLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(closeButton)
                    .addComponent(copyButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(ErrorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(ErrorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(ErrorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(ErrorDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the dialog */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				ErrorDialog dialog = new ErrorDialog(new javax.swing.JFrame(), "Test", true, Type.EXIT, MessageType.ERROR);
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
    private JButton closeButton;
    private JButton copyButton;
    private JEditorPane detailedMessage;
    private JLabel iconLabel;
    private JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}
