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

import java.awt.Component;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author Stefan Ganzer
 */
public class PrintingTask extends SwingWorker<String, Object> {

	private final PrinterJob printerJob;
	private final ResourceBundle res;
	private final Component parent;
	private volatile String message;
	private int type;

	public PrintingTask(PrinterJob printerJob, Component parent, ResourceBundle bundle) {
		if (printerJob == null) {
			throw new NullPointerException();
		}
		if (bundle == null) {
			throw new NullPointerException();
		}
		this.printerJob = printerJob;
		this.parent = parent;
		this.res = bundle;
		type = JOptionPane.INFORMATION_MESSAGE;
	}

	@Override
	protected String doInBackground() {
		try {
			printerJob.print();
			message = null;
			if (printerJob.isCancelled()) {
				message = res.getString("PRINTING CANCELED.");
			} else {
				message = res.getString("PRINTING COMPLETE.");
			}
		} catch (PrinterException ex) {
			message = res.getString("SORRY, A PRINTER ERROR OCCURRED.");
			type = JOptionPane.ERROR_MESSAGE;
		} catch (SecurityException ex) {
			message = res.getString("SORRY, CANNOT ACCESS THE PRINTER DUE TO SECURITY REASONS.");
			type = JOptionPane.WARNING_MESSAGE;
		}
		return null;
	}

	@Override
	protected void done() {
		if (type != JOptionPane.INFORMATION_MESSAGE) {
			message(type, message);
		}
	}

	private void message(int messageType, String message) {
		JOptionPane.showMessageDialog(parent, message, res.getString("PRINTING"), messageType);
	}
}
