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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Stefan Ganzer
 */
/*
 * ActionListener to copy the content of the given JComponent to the system
 * clipboard.
 */
class CopyListener implements ActionListener {

	/** The resourse bundle for this class QRView */
	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/CopyListener");
	/** The singleton instance of the system clipboard */
	private static final Clipboard CLIPBOARD = Toolkit.getDefaultToolkit().getSystemClipboard();
	private final JComponent source;

	CopyListener(JComponent c) {
		if (c == null) {
			throw new NullPointerException();
		}
		this.source = c;
		// We could check here if the given component has a non-null TransferHandler.
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		TransferHandler handler = source.getTransferHandler();
		try {
			if (source instanceof JTextComponent) {
				JTextComponent textComponent = (JTextComponent) source;
				if (textComponent.getSelectionStart() == 0 && textComponent.getSelectionEnd() == 0) {
					int oldCarePosition = textComponent.getCaretPosition();
					textComponent.selectAll();
					handler.exportToClipboard(source, CLIPBOARD, TransferHandler.COPY);
					textComponent.setCaretPosition(oldCarePosition);
				} else {
					handler.exportToClipboard(source, CLIPBOARD, TransferHandler.COPY);
				}
			} else {
				handler.exportToClipboard(source, CLIPBOARD, TransferHandler.COPY);
			}
		} catch (IllegalStateException ise) {
			JOptionPane.showMessageDialog(source.getTopLevelAncestor(), RES.getString("THE CLIPBOARD IS CURRENTLY NOT AVAILABLE. PLEASE TRY AGAIN LATER."), RES.getString("CLIPBOARD"), JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
