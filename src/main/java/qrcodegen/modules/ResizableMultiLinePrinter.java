/*
 Copyright 2011,2012 Stefan Ganzer

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
/* The code in paintComponent() is based on the LineBreakSample.java, which you can find here:
 * http://docs.oracle.com/javase/tutorial/2d/text/examples/LineBreakSample.java
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package qrcodegen.modules;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * A resizable component to display and print multiline text. The text can also
 * be rotated in 90° steps, as defined by {@link Rotation}. The vertical
 * alignment of the text can also be set, as defined by {@link VerticalAlignment}.
 *
 * @author Stefan Ganzer
 */
public class ResizableMultiLinePrinter extends JComponent {

	public static final String PROP_ROTATION = "rotation";
	public static final String PROP_TEXT = "text";
	public static final String PROP_VERTICAL_ALIGNMENT = "verticalAlignment";
	private static final String LINE_BREAKS = "(\r\n)|\r|\n";
	private static final int MINIMUM_HEIGHT = 10;
	private static final int MINIMUM_WIDTH = 10;
	private static final Hashtable<TextAttribute, Object> textAttributes = new Hashtable<TextAttribute, Object>();
	private final Map<AttributedString, TextRenderingProperties> measurer = new WeakHashMap<AttributedString, TextRenderingProperties>();
	private final Map<?, ?> fontHints;
	private List<AttributedString> attributedStrings = Collections.emptyList();
	private String text;
	private boolean hasTextBlockHeight;
	private int textBlockHeight;
	private VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
	private Rotation rotation = Rotation.R0;

	/**
	 * Creates a new ResizableMultiLinePrinter.
	 *
	 */
	public ResizableMultiLinePrinter() {
		setMapOfTextAttributes(getFont());
		setOpaque(true);
		//TODO The 'advanced' part of: 
		//http://docs.oracle.com/javase/6/docs/api/java/awt/doc-files/DesktopProperties.html
		Toolkit tk = Toolkit.getDefaultToolkit();
		fontHints = (Map<?, ?>) (tk.getDesktopProperty("awt.font.desktophints"));
	}

	private void setMapOfTextAttributes(Font font) {
		if (font != null) {
			//Keys of this map are for instance TextAttribute.FAMILY, TextAttribute.WEIGHT,...
			Map<TextAttribute, ?> ta = font.getAttributes();
			//getAttributes returns a Hashtable which contains null-values -
			//this causes a NullPointerException if we try to putAll into textAttributes.
			//So we have to check every single key-value pair.

			for (Map.Entry<TextAttribute, ?> entry : ta.entrySet()) {
				if (entry.getValue() != null) {
					textAttributes.put(entry.getKey(), entry.getValue());
				}
			}
		}
		hasTextBlockHeight = false;
	}

	//creates a AttributedString for each line in a multi line string
	//maybe we could just use a different LineBreakMeasurer, see:
	//see the LineBreakMeasurer API
	private void setListOfAttributedStrings() {
		if (text == null || text.isEmpty()) {
			attributedStrings = Collections.emptyList();
		} else {
			String[] textLines = text.split(LINE_BREAKS);
			attributedStrings = new ArrayList<AttributedString>(textLines.length);
			for (String s : textLines) {
				if (s.isEmpty()) {//AttributedStrings have to contain at least one character
					s = " ";
				}
				AttributedString as = new AttributedString(s, textAttributes);
				attributedStrings.add(as);
			}
		}
		hasTextBlockHeight = false;
	}

	public String getText() {
		return text;
	}

	public void setText(String value) {
		String oldValue = text;
		text = value;

		if (!isEqual(oldValue, value)) {
			setListOfAttributedStrings();
			firePropertyChange(PROP_TEXT, oldValue, value);
			revalidate();
			repaint();
		}
	}

	public static boolean isEqual(Object oldValue, Object newValue) {
		if(oldValue == null){
			return newValue == null;
		}
		return oldValue.equals(newValue);
	}

	@Override
	public void doLayout() {
		hasTextBlockHeight = false;
		super.doLayout();

	}

	@Override
	public void setSize(Dimension d) {
		if(d == null){
			throw new NullPointerException();
		}
		setSize(d.width, d.height);
	}

	@Override
	public void setSize(int width, int height) {
		hasTextBlockHeight = false;
		super.setSize(width, height);
	}

	@Override
	public void setFont(Font font) {
		setMapOfTextAttributes(font);
		setListOfAttributedStrings();
		super.setFont(font);
	}

	@Override
	public Dimension getPreferredSize() {
		if (isPreferredSizeSet()) {
			return super.getPreferredSize();
		} else {
			if (hasTextBlockHeight) {
				return new Dimension(getWidth(), textBlockHeight);
			} else {
				return getMinimumSize();
			}
		}
	}

	@Override
	public Dimension getMinimumSize() {
		if (isMinimumSizeSet()) {
			return super.getMinimumSize();
		} else {
			return new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT);
		}
	}

	public void setVerticalAlignment(VerticalAlignment alignment) {
		if (alignment == null) {
			throw new NullPointerException();
		}
		VerticalAlignment oldValue = this.verticalAlignment;
		this.verticalAlignment = alignment;
		firePropertyChange(PROP_VERTICAL_ALIGNMENT, oldValue, this.verticalAlignment);
		if (oldValue != alignment) {
			revalidate();
			repaint();
		}
	}

	public VerticalAlignment getVerticalAlignment() {
		return this.verticalAlignment;
	}

	public void setRotation(Rotation r) {
		if (r == null) {
			throw new NullPointerException();
		}
		Rotation oldRotation = this.rotation;
		this.rotation = r;
		hasTextBlockHeight = false;
		firePropertyChange(PROP_ROTATION, oldRotation, this.rotation);
		if (oldRotation != this.rotation) {
			revalidate();
			repaint();
		}
	}

	public Rotation getRotation() {
		return rotation;
	}

	@Override
	public void print(Graphics g) {
		Color orig = getBackground();
		setBackground(Color.WHITE);
		// wrap in try/finally so that we always restore the state
		try {
			super.print(g);
		} finally {
			setBackground(orig);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		//we don't call super.paintComponent(), as we do everything by ourselves
		//super.paintComponent(g);
		final boolean isPrinting = isPaintingForPrint();
		Graphics2D g2d = (Graphics2D) g.create();
		if (!isPrinting) {
			if (fontHints != null) {
				g2d.addRenderingHints(fontHints);
			}
			g2d.setBackground(Color.WHITE);
			g2d.clearRect(0, 0, getWidth(), getHeight());
		}
		drawMultiLineText(g2d);
		g2d.dispose();
	}

	private void drawMultiLineText(Graphics2D g2d) {
		// if no font has been set on this component, use the default font
		// of the Graphics object
		if (textAttributes.isEmpty()) {
			setMapOfTextAttributes(g2d.getFont());
		}
		g2d.setColor(Color.BLACK);

		// The actual width and height of the text area depend on the rotation
		final Dimension actualDim = rotation.getDimension(getSize());
		final Insets inset = getInsets();
		final int leftInset = inset.left;
		final int topInset = inset.top;
		final int bottomInset = inset.bottom;
		final int rightInset = inset.right;

		if (!hasTextBlockHeight) {
			float drawPosY = 0;
			// We need to calculate the height of the text first,
			// which is almost the same thing as actually drawing the text
			for (AttributedString as : attributedStrings) {
				// index of the first character in the paragraph.
				int paragraphStart;
				// index of the first character after the end of the paragraph.
				int paragraphEnd;
				LineBreakMeasurer lineMeasurer;

				// See if we have already a LineBreakMeasurer for the current
				// AttributedString, and create one otherwise
				TextRenderingProperties trp = measurer.get(as);
				if (trp == null) {
					AttributedCharacterIterator paragraph = as.getIterator();
					paragraphStart = paragraph.getBeginIndex();
					paragraphEnd = paragraph.getEndIndex();
					FontRenderContext frc = g2d.getFontRenderContext();
					lineMeasurer = new LineBreakMeasurer(paragraph, frc);
					measurer.put(as, new TextRenderingProperties(paragraphStart, paragraphEnd, lineMeasurer));
				} else {
					paragraphStart = trp.getParagraphStart();
					paragraphEnd = trp.getParagraphEnd();
					lineMeasurer = trp.getLineBreakMeasurer();
				}

				// Set break width to width of Component.
				float breakWidth = (float) actualDim.width - (leftInset + rightInset);
				// Set position to the index of the first character in the paragraph.
				lineMeasurer.setPosition(paragraphStart);

				// Get lines until the entire paragraph has been displayed.
				while (lineMeasurer.getPosition() < paragraphEnd) {

					// Retrieve next layout. A cleverer program would also cache
					// these layouts until the component is re-sized.
					TextLayout layout = lineMeasurer.nextLayout(breakWidth);
					if (layout == null) {//we are already at the end of the text used by this LineBreakMeasurer
						break;
					}

					// Move y-coordinate by the ascent of the layout.
					drawPosY += layout.getAscent();

					// Move y-coordinate in preparation for next layout.
					drawPosY += layout.getDescent() + layout.getLeading();
				}
			}
			// Store the new height of the text block if needed
			textBlockHeight = (int) Math.ceil(drawPosY);
			hasTextBlockHeight = true;
		}

		assert hasTextBlockHeight;
		g2d.rotate(rotation.getRadiant(), getWidth() / 2.0, getHeight() / 2.0);
		g2d.translate((getWidth() - actualDim.width) / 2.0, (getHeight() - actualDim.height) / 2.0);

		float drawPosY;
		// Here we can be sure that the height of the text block is known
		switch (verticalAlignment) {
			case TOP:
				drawPosY = topInset;
				break;
			case CENTER:
				drawPosY = topInset + (actualDim.height - topInset - bottomInset - textBlockHeight) / 2;
				break;
			case BOTTOM:
				drawPosY = actualDim.height - bottomInset - textBlockHeight;
				break;
			default:
				throw new AssertionError(verticalAlignment);
		}
		for (AttributedString as : attributedStrings) {
			// index of the first character in the paragraph.
			int paragraphStart;
			// index of the first character after the end of the paragraph.
			int paragraphEnd;
			LineBreakMeasurer lineMeasurer;

			// See if we have already a LineBreakMeasurer for the current
			// AttributedString, and create one otherwise
			TextRenderingProperties trp = measurer.get(as);
			if (trp == null) {
				AttributedCharacterIterator paragraph = as.getIterator();
				paragraphStart = paragraph.getBeginIndex();
				paragraphEnd = paragraph.getEndIndex();
				FontRenderContext frc = g2d.getFontRenderContext();
				lineMeasurer = new LineBreakMeasurer(paragraph, frc);
				measurer.put(as, new TextRenderingProperties(paragraphStart, paragraphEnd, lineMeasurer));
			} else {
				paragraphStart = trp.getParagraphStart();
				paragraphEnd = trp.getParagraphEnd();
				lineMeasurer = trp.getLineBreakMeasurer();
			}

			// Set break width to width of Component.
			float breakWidth = (float) actualDim.width - (leftInset + rightInset);
			// Set position to the index of the first character in the paragraph.
			lineMeasurer.setPosition(paragraphStart);

			// Get lines until the entire paragraph has been displayed.
			while (lineMeasurer.getPosition() < paragraphEnd) {

				// Retrieve next layout. A cleverer program would also cache
				// these layouts until the component is re-sized.
				TextLayout layout = lineMeasurer.nextLayout(breakWidth);
				if (layout == null) {//we are already at the end of the text used by this LineBreakMeasurer
					break;
				}

				// Compute pen x position. If the paragraph is right-to-left we
				// will align the TextLayouts to the right edge of the panel.
				// Note: this won't occur for the English text in this sample.
				// Note: drawPosX is always where the LEFT of the text is placed.
				float drawPosX = layout.isLeftToRight()
						? leftInset : breakWidth - layout.getAdvance() - rightInset;

				// Move y-coordinate by the ascent of the layout.
				drawPosY += layout.getAscent();

				// Draw the TextLayout at (drawPosX, drawPosY).
				layout.draw(g2d, drawPosX, drawPosY);

				// Move y-coordinate in preparation for next layout.
				drawPosY += layout.getDescent() + layout.getLeading();
			}
		}
	}

	public boolean hasTextBlockHight() {
		return hasTextBlockHeight;
	}

	public int getTextBlockHeight() {
		return textBlockHeight;
	}

	private static class TextRenderingProperties {

		private final int paragraphStart, paragraphEnd;
		private final LineBreakMeasurer lbm;
		private transient final int hashCode;

		public TextRenderingProperties(int paragraphStart, int paragraphEnd, LineBreakMeasurer lbm) {
			if (lbm == null) {
				throw new NullPointerException();
			}
			this.paragraphStart = paragraphStart;
			this.paragraphEnd = paragraphEnd;
			this.lbm = lbm;
			hashCode = calculateHashCode();
		}

		public int getParagraphStart() {
			return paragraphStart;
		}

		public int getParagraphEnd() {
			return paragraphEnd;
		}

		public LineBreakMeasurer getLineBreakMeasurer() {
			return lbm;
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		//called from within the constructor
		private int calculateHashCode() {
			int result = 17;
			result = 31 * result + paragraphStart;
			result = 31 * result + paragraphEnd;
			result = 31 * result + lbm.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final TextRenderingProperties other = (TextRenderingProperties) obj;
			if (this.paragraphStart != other.paragraphStart) {
				return false;
			}
			if (this.paragraphEnd != other.paragraphEnd) {
				return false;
			}
			if (this.lbm != other.lbm && !this.lbm.equals(other.lbm)) {
				return false;
			}
			return true;
		}
	}

	public static void main(String... args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				String s = "Many people believe that Vincent van Gogh painted his best works "
						+ "during the two-year period he spent in Provence. Here is where he "
						+ "painted The Starry Night--which some consider to be his greatest "
						+ "work of all. However, as his artistic brilliance reached new "
						+ "heights in Provence, his physical and mental health plummeted. ";

				final JFrame frame = new JFrame("ResizableMultiLinePrinter");

				frame.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
						frame.dispose();
					}
				});
				ResizableMultiLinePrinter rmlp = new ResizableMultiLinePrinter();
				rmlp.setText(s);
				rmlp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));//new EmptyBorder(5, 5, 5, 5));
				rmlp.setMinimumSize(new Dimension(180, 300));
				rmlp.setRotation(Rotation.R270);
				rmlp.setVerticalAlignment(VerticalAlignment.CENTER);
				frame.add(rmlp);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
