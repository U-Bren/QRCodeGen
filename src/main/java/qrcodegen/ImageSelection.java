/*
Copyright 2011 Stefan Ganzer
 
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

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * ImageSelection allows to transfer {@link java.awt.Image}s via drag&drop
 * or via the clipboard.
 * @author Stefan Ganzer
 */
public final class ImageSelection implements Transferable, ClipboardOwner {

	private static final DataFlavor FLAVOR = DataFlavor.imageFlavor;
	private final Image image;

	public ImageSelection(Image image) {
		if (image == null) {
			throw new NullPointerException();
		}
		this.image = image;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		// according to the API doc an IOException is thrown 'if the data is no longer available
		// in the requested flavor' - this can't happen here
		if (isDataFlavorSupported(flavor)) {
			return image;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{FLAVOR};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		//as UnsupportedFlavorException accepts null, we accept it here, too
		return FLAVOR.equals(flavor);
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// we do nothing about it currently
	}
}