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
package qrcodegen.modules;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.DesignMode;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * Allows to display and print simple folding cards, with text/images on the
 * outside or the inside only. This class doesn't support duplex printing. To
 * put content on the card, use setUpperComponent and setLowerComponent. The
 * overall size of the card is determined by the size of the upper or the lower
 * component - use adjustSizeToUpperComponent (default) or
 * adjustSizeToLowerComponent to choose which one. The size of the other
 * component will then be made the same. To get the overall size of the card use
 * calculateOptimalSize(). The display and print size is always scaled down to
 * the available space, but never scaled up (0 &lt;= scaling factor &lt;= 1).
 *
 * @author Stefan Ganzer
 */
class FoldingCardPrinter extends JComponent implements Printable, Scrollable, DesignMode {

	/**
	 * Determines which of lowerComponent and upperComponent is used as the size
	 * model.
	 */
	enum AdjustSizeTo {

		LOWER_COMPONENT, UPPER_COMPONENT;
	}
	private static final int MINIMUM_HEIGHT = 20;
	private static final int MINIMUM_WIDTH = 20;
	private boolean drawBorder = true;
	private JComponent upperComponent;
	private JComponent lowerComponent;
	private ScaleToPaperSize scaleRuleForPrinting = ScaleToPaperSize.DOWN;
	private ScaleToPaperSize scaleRuleForDisplaying = ScaleToPaperSize.NONE;
	private AdjustSizeTo sizeAdjustment = AdjustSizeTo.UPPER_COMPONENT;
	private PageFormat currentPageFormat;
	private int maxUnitIncrement = 1;
	private boolean isDesignMode = false;

	FoldingCardPrinter() {
		setOpaque(true);
		setLayout(null);
		setAutoscrolls(true);
		// Source: javax.swing.JComponent.setAutoscrolls(boolean)
		MouseMotionListener doScrollRectToVisible = new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
				FoldingCardPrinter.this.scrollRectToVisible(r);
			}
		};
		addMouseMotionListener(doScrollRectToVisible);
		super.setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
	}

	@Override
	public Dimension getPreferredSize() {
		if (isPreferredSizeSet()) {
			return super.getPreferredSize();
		} else {
			return calculateOptimalSize();
//			if (canCalculateOptimalSize()) {
//				return calculateOptimalSize();
//			} else {
//				return getMinimumSize();
//			}
		}
	}

	/**
	 * Sets the upperComponent to the given component. If the given component is
	 * null, upperComponent is removed.
	 *
	 * @param component the new upperComponent, or null is upperComponent is to
	 * be removed
	 */
	public void setUpperComponent(JComponent component) {
		if (upperComponent != null) {
			super.remove(upperComponent);
		}
		this.upperComponent = component;
		if (upperComponent != null) {
			super.add(upperComponent);
		}
		revalidate();
		repaint();
	}

	/**
	 * Returns the upper component, or null if none is set.
	 *
	 * @return the upper component, or null if none is set
	 */
	public JComponent getUpperComponent() {
		return upperComponent;
	}

	/**
	 * Sets the lowerComponent to the given component. If the given component is
	 * null, lowerComponent is removed.
	 *
	 * @param component the new lowerComponent, or null is lowerComponent is to
	 * be removed
	 */
	public void setLowerComponent(JComponent component) {
		if (lowerComponent != null) {
			super.remove(lowerComponent);
		}
		this.lowerComponent = component;
		if (lowerComponent != null) {
			super.add(lowerComponent);
		}
		revalidate();
		repaint();
	}

	/**
	 * Returns the lower component, or null if none is set.
	 *
	 * @return the lower component, or null if none is set
	 */
	public JComponent getLowerComponent() {
		return lowerComponent;
	}

	/**
	 *
	 * @param c
	 *
	 * @return
	 *
	 * @throws UnsupportedOperationException use setUpperComponent and
	 * SetLowerComponent instead
	 * @see #setUpperComponent(javax.swing.JComponent)
	 * @see #setLowerComponent(javax.swing.JComponent)
	 */
	@Override
	public Component add(Component c) {
		throw new UnsupportedOperationException("Use setLowerComponent and setUpperComponent instead.");
	}

	/**
	 * Determines the component which is to be used as the size model.
	 *
	 * @param value the component which is to be used as the size model
	 *
	 * @throws NullPointerException if value is null
	 */
	void adjustSizeTo(AdjustSizeTo value) {
		if (value == null) {
			throw new NullPointerException();
		}
		AdjustSizeTo oldValue = sizeAdjustment;
		sizeAdjustment = value;
		firePropertyChange("adjustSizeToComponent", oldValue, sizeAdjustment);
		if (oldValue != sizeAdjustment) {
			revalidate();
			repaint();
		}
	}

	/**
	 * Returns a value indicating which of the components is used as the size
	 * model.
	 *
	 * @return a value indicating which of the components is used as the size
	 * model. The return value is never null.
	 */
	AdjustSizeTo getAdjustSizeTo() {
		assert sizeAdjustment != null;
		return sizeAdjustment;
	}

	void setScaleRuleForPrinting(ScaleToPaperSize value) {
		if (value == null) {
			throw new NullPointerException();
		}
		ScaleToPaperSize oldValue = scaleRuleForPrinting;
		scaleRuleForPrinting = value;
	}

	ScaleToPaperSize getScaleRuleForPrinting() {
		return scaleRuleForPrinting;
	}

	void setScaleRuleForDisplaying(ScaleToPaperSize value) {
		if (value == null) {
			throw new NullPointerException();
		}
		ScaleToPaperSize oldValue = scaleRuleForDisplaying;
		scaleRuleForDisplaying = value;
	}

	ScaleToPaperSize getScaleRuleForDisplaying() {
		return scaleRuleForDisplaying;
	}

	@Override
	public void setSize(Dimension d) {
		setSize(d.width, d.height);
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		layoutChildren();
	}

	@Override
	public void doLayout() {
		if (getLayout() == null) {//just in case someone set a layout manager on this component
			layoutChildren();
		}
		super.doLayout();
	}

	private JComponent getAdjustable() {
		JComponent result = null;
		switch (sizeAdjustment) {
			case UPPER_COMPONENT:
				result = upperComponent;
				break;
			case LOWER_COMPONENT:
				result = lowerComponent;
				break;
			default:
				throw new AssertionError(sizeAdjustment);
		}
		return result;
	}

	private void layoutChildren() {
		final JComponent adjustable = getAdjustable();
		if (adjustable == null) {
			return;
		}

		final int componentWidth = adjustable.getWidth();
		final int componentHeight = adjustable.getHeight();

		switch (sizeAdjustment) {
			case UPPER_COMPONENT:
				lowerComponent.setSize(componentWidth, componentHeight);
				break;
			case LOWER_COMPONENT:
				upperComponent.setSize(componentWidth, componentHeight);
				break;
			default:
				throw new AssertionError(sizeAdjustment);
		}

		Insets insets = getInsets();
		final int borderSpace = drawBorder ? 1 : 0;

		int componentX = insets.left + borderSpace;
		int uComponentY = insets.top + borderSpace;
		int lComponentY = insets.top + borderSpace + componentHeight + insets.bottom + borderSpace + insets.top;

		upperComponent.setLocation(componentX, uComponentY);
		lowerComponent.setLocation(componentX, lComponentY);
	}

	/**
	 * Returns the optimal size of this component. If the optimal size cannot be
	 * calculated because the reference component is set to null, an
	 * IllegalStateException is thrown.
	 *
	 * @return the optimal size of this component. Never returns null.
	 *
	 * @throws IllegalStateException if the optimal size cannot be calculated
	 */
	public Dimension calculateOptimalSize() {
		if (!canCalculateOptimalSize()) {
//			if (isDesignTime()) {
//				return new Dimension(50, 100);
//			} else {
//				throw new IllegalStateException("Cannot calculate optimal size");
//			}
			return new Dimension(50, 100);
		}
		final JComponent adjustable = getAdjustable();
		assert adjustable != null;

		Insets insets = getInsets();
		final int borderSpace = drawBorder ? 1 : 0;

		final int width = adjustable.getWidth() + insets.left + insets.right + 2 * borderSpace;
		final int height = 2 * adjustable.getHeight() + 2 * (insets.top + insets.bottom) + 3 * borderSpace;
		return new Dimension(width, height);
	}

	private boolean canCalculateOptimalSize() {
		Component c = null;
		switch (sizeAdjustment) {
			case UPPER_COMPONENT:
				c = upperComponent;
				break;
			case LOWER_COMPONENT:
				c = lowerComponent;
				break;
			default:
				throw new AssertionError(sizeAdjustment);
		}
		return c != null;
	}

	@Override
	public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
		if (pageIndex < 0) {
			throw new IllegalArgumentException(Integer.toString(pageIndex));
		}
		if (pageIndex > 0) {
			return (NO_SUCH_PAGE);
		} else {

			PageFormat oldPageFormat = null;
			try {
				oldPageFormat = currentPageFormat;
				currentPageFormat = pageFormat;
				print(g);
				return (PAGE_EXISTS);
			} finally {
				currentPageFormat = oldPageFormat;
			}
		}
	}

	@Override
	public void print(Graphics g) {

		//draw the picture on the printing device
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.translate(currentPageFormat.getImageableX(), currentPageFormat.getImageableY());

		// This is a very simple form of scaling, which delivers acceptable quality
		// only if scaleFactor is close to 1
		// TODO implement a better scaling mechanism
		//scale the image down to page size, but not up
		//keep the proportions
		//double scaleFactor = getScaleFactor(currentPageFormat.getImageableWidth(), currentPageFormat.getImageableHeight(), scaleRuleForPrinting);
		double scaleFactor = getScaleFactor(scaleRuleForPrinting, currentPageFormat.getImageableWidth(), currentPageFormat.getImageableHeight());
		if (scaleFactor != 1.0) {
			g2d.scale(scaleFactor, scaleFactor);
		}

		if (canCalculateOptimalSize()) {
			Dimension d = calculateOptimalSize();
			g2d.setClip(0, 0, d.width, d.height);
		}

		super.print(g2d);
		g2d.dispose();

	}

	@Override
	public void paint(Graphics g) {
		//double scaleFactor = getScaleFactor(getWidth(), getHeight(), scaleRuleForDisplaying);
		double scaleFactor = getScaleFactor(scaleRuleForDisplaying, getWidth(), getHeight());

		//draw the picture on the output device
		Graphics2D g2d = (Graphics2D) g.create();

		// This is a very simple form of scaling, which delivers acceptable quality
		// only if scaleFactor is close to 1
		// TODO implement a better scaling mechanism
		if (scaleFactor != 1.0) {
			g2d.scale(scaleFactor, scaleFactor);
		}
		super.paint(g2d);
		// Paint the border on top of the cards instead of
		// using the paintBorder method that is called after paint.
		paintMyBorder(g2d);

		g2d.dispose();
	}

	private void paintMyBorder(Graphics g) {


		Dimension d = calculateOptimalSize();

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(Color.BLACK);

		if (drawBorder) {
			final int borderSpace = 1;
			// Draw a rectangle around both cards
			g2d.fillRect(0, 0, d.width, borderSpace);
			g2d.fillRect(0, 0, borderSpace, d.height);
			g2d.fillRect(d.width - borderSpace, 0, 1, d.height);
			g2d.fillRect(0, d.height - borderSpace, d.width, borderSpace);

			// Draw a folding line between both cards
			g2d.fillRect(0, d.height / 2, d.width, 1);
		}

		g2d.dispose();
	}

	public Printable getPrintable() {
		return this;
	}

	private double getScaleFactor(ScaleToPaperSize rule, double toWidth, double toHeight) {
		//scale the image down to page size, but not up
		//keep the proportions
		double scaleFactor;
		Dimension imageDimension = calculateOptimalSize();
		scaleFactor = rule.getScaleFactor(imageDimension.width, imageDimension.height, toWidth, toHeight);
		return scaleFactor;
	}

	public void printMe() throws PrinterException {
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		printerJob.setPrintable(this);
		if (printerJob.printDialog()) {
			printerJob.print();
		}
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		// Source: =http://docs.oracle.com/javase/tutorial/uiswing/examples/components/ScrollDemoProject/src/components/ScrollablePicture.java
		//Get the current position.
		int currentPosition = 0;
		if (orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
		} else {
			currentPosition = visibleRect.y;
		}

		//Return the number of pixels between currentPosition
		//and the nearest tick mark in the indicated direction.
		if (direction < 0) {
			int newPosition = currentPosition
					- (currentPosition / maxUnitIncrement)
					* maxUnitIncrement;
			return (newPosition == 0) ? maxUnitIncrement : newPosition;
		} else {
			return ((currentPosition / maxUnitIncrement) + 1)
					* maxUnitIncrement
					- currentPosition;
		}
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		// Source: =http://docs.oracle.com/javase/tutorial/uiswing/examples/components/ScrollDemoProject/src/components/ScrollablePicture.java
		if (orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - maxUnitIncrement;
		} else {
			return visibleRect.height - maxUnitIncrement;
		}
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public void setDesignTime(boolean designTime) {
		this.isDesignMode = designTime;
	}

	@Override
	public boolean isDesignTime() {
		return isDesignMode;
	}

	public static void main(String[] args) throws PrinterException {

		String s = "Many people believe that Vincent van Gogh painted his best works "
				+ "during the two-year period he spent in Provence. Here is where he "
				+ "painted The Starry Night--which some consider to be his greatest "
				+ "work of all. However, as his artistic brilliance reached new "
				+ "heights in Provence, his physical and mental health plummeted. ";
		createTestFrame2(s);
	}

	private static void createTestFrame1(String s) throws HeadlessException, PrinterException {
		final JFrame frame = new JFrame("FlipCard Printer");

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});

		ResizableMultiLinePrinter rmlp1 = new ResizableMultiLinePrinter();
		rmlp1.setText(s);
		ResizableMultiLinePrinter rmlp2 = new ResizableMultiLinePrinter();
		rmlp2.setText(s);
		rmlp2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));//new EmptyBorder(5, 5, 5, 5));
		FoldingCardPrinter fcp = new FoldingCardPrinter();
		new BoxLayout(frame, BoxLayout.PAGE_AXIS);
		frame.add(fcp);
		rmlp1.setSize(300, 200);

		ImageIcon imageIcon = new ImageIcon(WLANPrintSettings.class.getResource("/qrcodegen/modules/test_qrcode_30%.png"));
		BufferedImage mirroredImage = ImageUtilities.getRotatedImage(imageIcon.getImage(), Rotation.R0);
		JLabel imageLabel = new JLabel(new ImageIcon(mirroredImage));
		imageLabel.setSize(mirroredImage.getWidth(), mirroredImage.getHeight());

		BufferedImage image2 = ImageUtilities.getRotatedImage(imageIcon.getImage(), Rotation.R270);
		JLabel imageLabel2 = new JLabel(new ImageIcon(image2));
		imageLabel2.setSize(mirroredImage.getWidth(), image2.getHeight());

		fcp.setUpperComponent(imageLabel);
		fcp.setLowerComponent(imageLabel2);//rmlp2);
		fcp.setSize(fcp.calculateOptimalSize());
		fcp.doLayout();
		frame.setSize(fcp.getWidth() + 50, fcp.getHeight() + 50);
		frame.setVisible(true);
		fcp.printMe();
	}

	private static void createTestFrame2(String s) throws HeadlessException, PrinterException {
		final JFrame frame = new JFrame("FlipCard Printer2");

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});


		ResizableMultiLinePrinter rmlp1 = new ResizableMultiLinePrinter();
		rmlp1.setText(s);
		ResizableMultiLinePrinter rmlp2 = new ResizableMultiLinePrinter();
		rmlp2.setText(s);
		rmlp2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));//new EmptyBorder(5, 5, 5, 5));
		FoldingCardPrinter fcp = new FoldingCardPrinter();

		JScrollPane fcpScrollPane = new JScrollPane(fcp);


		new BoxLayout(frame, BoxLayout.PAGE_AXIS);
		frame.add(fcpScrollPane);
		rmlp1.setSize(300, 200);

		ImageIcon imageIcon = new ImageIcon(WLANPrintSettings.class.getResource("/qrcodegen/modules/test_qrcode_30%.png"));
		BufferedImage mirroredImage = ImageUtilities.getRotatedImage(imageIcon.getImage(), Rotation.R0);
		JLabel imageLabel = new JLabel(new ImageIcon(mirroredImage));
		imageLabel.setSize(mirroredImage.getWidth(), mirroredImage.getHeight());

		BufferedImage image2 = ImageUtilities.getRotatedImage(imageIcon.getImage(), Rotation.R270);
		JLabel imageLabel2 = new JLabel(new ImageIcon(image2));
		imageLabel2.setSize(mirroredImage.getWidth(), image2.getHeight());

		fcp.setUpperComponent(imageLabel);
		fcp.setLowerComponent(imageLabel2);//rmlp2);
		fcp.setSize(fcp.calculateOptimalSize());
		fcp.doLayout();
		frame.setSize(fcp.getWidth() + 50, fcp.getHeight() + 50);
		frame.setVisible(true);
	}
}
