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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * This utility allows to print a {@link java.awt.Component} which doesn't
 * exceed the size of one page. Loosely based on Marty Hall's
 * {@link <a href="http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Printing/PrintUtilities.java">http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Printing/PrintUtilities.java</a>}
 * It calls a components print() method, though, so it isn't necessary to
 * manually disable double buffering, because this is done in Component.paint()
 * which is called by Component.print().
 *
 * @author Stefan Ganzer
 */
public class PrintUtilities implements Printable {

	/** The component that this instance shall print. */
	private final Component componentToBePrinted;

	/**
	 * Constructs a new PrintUtilities instance.
	 * @param componentToBePrinted the component that you want to print
	 */
	public PrintUtilities(Component componentToBePrinted) {
		this.componentToBePrinted = requireNonNull(componentToBePrinted);
	}

	/**
	 * Prints the component that was passed to the constructor of this instance.
	 * @throws PrinterException 
	 */
	public void print() throws PrinterException {
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		if (printerJob.printDialog()) {
			printerJob.print();
		}
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex < 0) {
			throw new IllegalArgumentException(Integer.toString(pageIndex));
		}
		if (pageIndex > 0) {
			return (NO_SUCH_PAGE);
		} else {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			componentToBePrinted.print(g2d);
			g2d.dispose();
			return (PAGE_EXISTS);
		}
	}

	private static <T> T requireNonNull(T t) {
		if (t == null) {
			throw new NullPointerException();
		}
		return t;
	}
}