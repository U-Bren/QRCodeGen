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

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author Stefan Ganzer
 */
class ContentCopyHandler extends TransferHandler {

	/** The action this transfer handler supports. */
	private static final int SOURCE_ACTIONS = TransferHandler.COPY;
	private static final long serialVersionUID = 1L;
	private final QRCodeGenerator generator;

	ContentCopyHandler(QRCodeGenerator g) {
		if (g == null) {
			throw new NullPointerException();
		}
		this.generator = g;
	}

	@Override
	public int getSourceActions(JComponent c) {
		return SOURCE_ACTIONS;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		if (support == null) {
			throw new NullPointerException();
		}
		return false;
	}

	@Override
	public Transferable createTransferable(JComponent c) {
		String content = generator.getContent();
		assert content != null;
		return new StringSelection(content);
	}
}
