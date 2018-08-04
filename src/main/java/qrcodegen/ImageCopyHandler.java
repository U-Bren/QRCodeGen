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
/*
 * $Revision: 523 $
 */
package qrcodegen;

import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

/**
 * A TransferHandler implementation that supports dragging images from JLabel.
 * For the drag operation to work the JLabel's image must be an instance of
 * {@link javax.swing.ImageIcon}.
 *
 * @author Stefan Ganzer
 */
public class ImageCopyHandler extends TransferHandler {

	/** The data flavor this transfer handler supports. */
	private static final DataFlavor FLAVOR = DataFlavor.imageFlavor;
	/** The action this transfer handler supports. */
	private static final int SOURCE_ACTIONS = TransferHandler.COPY;
	private static final long serialVersionUID = 1L;
	/** True if drag AND drop is supported, false if only drag is supported. */
	private final boolean canImport;

	/**
	 * Returns an ImageCopyHandler instance that only supports the 'drag' action
	 * of images from a JLabel.
	 * {@link #canImport(TransferHandler.TransferSupport)} for this instance
	 * will always return {@code false}.
	 */
	public ImageCopyHandler() {
		this(false);
	}

	/**
	 * Returns an ImageCopyHandler instance that supports the 'drag' action of
	 * images from a JLabel, and, if canImport is {@code true}, also the 'drop'
	 * action of images on a JLabel.
	 *
	 * @param canImport besides 'drag' also 'drop' is enabled for this
	 * ImageCopyHandler if {@code true}, or only 'drag', but not 'drop' is
	 * enabled if {@code false}.
	 */
	public ImageCopyHandler(boolean canImport) {
		this.canImport = canImport;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return SOURCE_ACTIONS;
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {
		if(!support.isDataFlavorSupported(FLAVOR)){
			return false;
		}
		return canImport;
	}

	@Override
	public Transferable createTransferable(JComponent jComponent) {
		if (jComponent instanceof JLabel) {
			JLabel label = (JLabel) jComponent;
			Icon icon = label.getIcon();
			if (icon instanceof ImageIcon) {
				Image image = ((ImageIcon) icon).getImage();
				return new ImageSelection(image);
			}
		}
		return null;
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
		if (support == null) {
			throw new NullPointerException();
		}

		Component component = support.getComponent();
		if (component instanceof JLabel) {
			JLabel label = (JLabel) component;
			if (support.isDataFlavorSupported(FLAVOR)) {
				try {
					// The representing class for DataFlavor.imageFlavor is java.awt.Image,
					// so this cast is safe
					Image image = (Image) support.getTransferable().getTransferData(FLAVOR);
					ImageIcon icon = new ImageIcon(image);
					label.setIcon(icon);
					return true;
				} catch (UnsupportedFlavorException ignored) {
					//this shouldn't happen, as we made sure we support this flavor
					throw new AssertionError(ignored);
				} catch (IOException ignored) {
					// nothing we can do about here
					Logger.getLogger("qrcodegen").throwing("qrcodegen.ImageCopyHandler", "boolean importData(TransferHandler.TransferSupport)", ignored);
				}
			}
		}
		return false;
	}
}
