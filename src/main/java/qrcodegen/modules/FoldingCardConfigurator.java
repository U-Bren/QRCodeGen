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
package qrcodegen.modules;

import java.awt.Font;
import java.awt.Image;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * This class manages the configuration of a FoldingCardPrinter used to print
 * WLAN settings.
 *
 * @author Stefan Ganzer
 */
public class FoldingCardConfigurator {

	private static final int DEFAULT_INSET_TOP = 5;
	private static final int DEFAULT_INSET_LEFT = 5;
	private static final int DEFAULT_INSET_BOTTOM = 5;
	private static final int DEFAULT_INSET_RIGHT = 5;
	private static final String DEFAULT_FONT_NAME = "Serif";
	private static final int DEFAULT_FONT_SIZE = 11;
	private static final VerticalAlignment DEFAULT_ALIGNMENT = VerticalAlignment.CENTER;
	private final ResourceBundle res = ResourceBundle.getBundle("qrcodegen/modules/FoldingCardConfigurator");
	private final ResizableMultiLinePrinter rmlp;
	private final FoldingCardPrinter fcp;
	private Font font = new Font(DEFAULT_FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE);
	private Rotation imageRotation = Rotation.R0;
	private String ssid;
	private String networkKey;
	private NetworkType networkType;
	private boolean addFieldNames = true;
	private Type passwordType;
	/** A saved state of this FoldingCardConfigurator that is used to revert any
	 * changes made to it */
	private SavedState freezedState;

	public FoldingCardConfigurator(Image codeImage, Rotation imageRotation) {
		if (codeImage == null) {
			throw new NullPointerException();
		}
		if (imageRotation == null) {
			throw new NullPointerException();
		}
		this.imageRotation = imageRotation;

		fcp = new FoldingCardPrinter();
		rmlp = new ResizableMultiLinePrinter();

		rmlp.setVerticalAlignment(DEFAULT_ALIGNMENT);
		rmlp.setBorder(BorderFactory.createEmptyBorder(DEFAULT_INSET_TOP, DEFAULT_INSET_LEFT, DEFAULT_INSET_BOTTOM, DEFAULT_INSET_RIGHT));
		fcp.setUpperComponent(ImageUtilities.getImageAsJLabel(ImageUtilities.getRotatedImage(codeImage, this.imageRotation)));
		fcp.setLowerComponent(rmlp);
		fcp.adjustSizeTo(FoldingCardPrinter.AdjustSizeTo.UPPER_COMPONENT);
		fcp.setSize(fcp.getPreferredSize());
	}

	/**
	 * Freezes the current state of this FoldingCardConfigurator. Any changes
	 * can be reverted to this state by calling revertChanges()
	 *
	 * @see #commitChanges()
	 * @see #revertChanges()
	 */
	public void freezeState() {
		freezedState = new SavedState(font, imageRotation, rmlp.getRotation(), rmlp.getVerticalAlignment(), addFieldNames, fcp.getScaleRuleForPrinting());
	}

	/**
	 * Makes any changes made to this FoldingCardConfigurator permanent, i.e.
	 * they cannot be reverted by invoking revertChanges() after calling this
	 * method.
	 *
	 * @see #freezeState()
	 * @see #revertChanges()
	 */
	public void commitChanges() {
		freezedState = null;
	}

	/**
	 * Reverts any changes made to this FoldingCardConfigurator to the state
	 * saved by invoking freezeState().
	 *
	 * @throws IllegalStateException if freezeState() hasn't been called before,
	 * or if changes have been made permanent by invoking commitChanges.
	 * @see #freezeState()
	 * @see #commitChanges()
	 */
	public void revertChanges() {
		if (freezedState == null) {
			throw new IllegalStateException("No saved state available");
		}
		setFont(freezedState.font);
		setVerticalAlignment(freezedState.verticalTextAlignment);
		setAddFieldNames(freezedState.addFieldNames);
		setImageRotation(freezedState.imageRotation);
		setTextRotation(freezedState.textRotation);
		setScaleRuleForPrinting(freezedState.scaleRuleForPrinting);
	}

	/**
	 * Sets the vertical alignment of the ResizableMultiLinePrinter.
	 *
	 * @param alignment
	 */
	public void setVerticalAlignment(VerticalAlignment alignment) {
		if (alignment == null) {
			throw new NullPointerException();
		}
		rmlp.setVerticalAlignment(alignment);
	}

	/**
	 * Gets the vertical alignment of the ResizableMultiLinePrinter.
	 *
	 * @return the vertical alignment of the ResizableMultiLinePrinter
	 */
	public VerticalAlignment getVerticalAlignment() {
		return rmlp.getVerticalAlignment();
	}

	/**
	 * True if the field names ("SSID", "Password", ...) are to be put in front
	 * of the respective field values.
	 *
	 * @param addFieldNames
	 */
	public void setAddFieldNames(boolean addFieldNames) {
		this.addFieldNames = addFieldNames;
		rmlp.setText(getText());
	}

	/**
	 * Returns {@code true} if the field names ("SSID", "Password", ...) are to
	 * be put in front of the respective field values, {code false} if only the
	 * values are to be shown.
	 *
	 * @return
	 */
	public boolean getAddFieldNames() {
		return addFieldNames;
	}

	public void setSSID(String ssid) {
		if (ssid == null) {
			throw new NullPointerException();
		}
		this.ssid = ssid;
		rmlp.setText(getText());
	}

	public void setNetworkKey(String key) {
		if (key == null) {
			throw new NullPointerException();
		}
		this.networkKey = key;
		rmlp.setText(getText());
	}

	public void setNetworkKeyType(Type type) {
		if (type == null) {
			throw new NullPointerException();
		}
		this.passwordType = type;
	}

	public void setNetworkType(NetworkType type) {
		if (type == null) {
			throw new NullPointerException();
		}
		this.networkType = type;
		rmlp.setText(getText());
	}

	/**
	 * Sets the font of the ResizableMultiLinePrinter.
	 *
	 * @param font
	 */
	public void setFont(Font font) {
		if (font == null) {
			throw new NullPointerException();
		}
		this.font = new Font(font.getFamily(), font.getStyle(), font.getSize());
		rmlp.setFont(this.font);
	}

	/**
	 * Returns the font of the ResizableMultiLinePrinter.
	 *
	 * @return
	 */
	public Font getFont() {
		return new Font(font.getFamily(), font.getStyle(), font.getSize());
	}

	public void setFontSize(float size) {
		this.font = font.deriveFont(size);
		rmlp.setFont(this.font);
	}

	public void setFontFamily(String familyName) {
		if (familyName == null) {
			throw new NullPointerException();
		}
		// Check for valid family name?
		font = new Font(familyName, Font.PLAIN, font.getSize());
		rmlp.setFont(font);
	}

	/**
	 * Sets the rotation of the ResizableMultiLinePrinter.
	 *
	 * @param r
	 */
	public void setTextRotation(Rotation r) {
		if (r == null) {
			throw new NullPointerException();
		}
		rmlp.setRotation(r);
	}

	/**
	 * Returns the rotation of the ResizableMultiLinePrinter.
	 *
	 * @return
	 */
	public Rotation getTextRotation() {
		return rmlp.getRotation();
	}

	public void setImage(Image image) {
		if (image instanceof RotatedBufferedImage) {
			fcp.setUpperComponent(ImageUtilities.getImageAsJLabel(image));
		} else {
			fcp.setUpperComponent(ImageUtilities.getImageAsJLabel(ImageUtilities.getRotatedImage(image, imageRotation)));
		}
		fcp.setSize(fcp.getPreferredSize());
	}

	public void setImageRotation(Rotation r) {
		if (r == null) {
			throw new NullPointerException();
		}
		this.imageRotation = r;
		Image i = ((ImageIcon) ((JLabel) fcp.getUpperComponent()).getIcon()).getImage();
		RotatedBufferedImage rbi = (RotatedBufferedImage) i;
		rbi = rbi.rotateTo(r);
		setImage(rbi);
	}

	public Rotation getImageRotation() {
		return imageRotation;
	}

	void setScaleRuleForPrinting(ScaleToPaperSize value) {
		if (value == null) {
			throw new NullPointerException();
		}
		fcp.setScaleRuleForPrinting(value);
	}

	ScaleToPaperSize getScaleRuleForPrinting() {
		return fcp.getScaleRuleForPrinting();
	}

	public String getText() {
		StringBuilder sb = new StringBuilder(240);
		if (addFieldNames) {
			sb.append("SSID: ").append(ssid).append("\n");
			if (networkType != NetworkType.NO_ENCRYPTION) {
				sb.append(getTypeString(passwordType)).append(res.getString("PASSWORD")).append(": ").append(networkKey).append("\n");
			}
			sb.append(res.getString("NETWORK_TYPE")).append(": ").append(networkType);

		} else {
			sb.append(ssid).append("\n");
			if (networkType != NetworkType.NO_ENCRYPTION) {
				sb.append("(").append(getTypeString(passwordType)).append(") ").append(networkKey).append("\n");
			}
			sb.append(networkType);
		}
		return sb.toString();
	}

	/**
	 * Returns the FoldingCardPrinter associated with this
	 * FoldingCardConfigurator. The caller is responsible to make any changes
	 * only through this FoldingCardConfigurator, and to only read its values.
	 *
	 * @return the FoldingCardPrinter associated with this
	 * FoldingCardConfigurator
	 */
	public FoldingCardPrinter getFoldingCardPrinter() {
		return fcp;
	}

	private static String getTypeString(Type type) {
		String result;
		switch (type) {
			case STRING:
				result = "ASCII";
				break;
			case HEX:
				result = "HEX";
				break;
			default:
				throw new AssertionError(type);
		}
		return result;
	}

	/**
	 * SavedState is used to save the state of a ResizableMultiLinePrinter.
	 */
	private static class SavedState {

		private final Font font;
		private final Rotation imageRotation;
		private final Rotation textRotation;
		private final VerticalAlignment verticalTextAlignment;
		private final boolean addFieldNames;
		private final ScaleToPaperSize scaleRuleForPrinting;

		SavedState(Font font, Rotation imageRotation, Rotation textRotation, VerticalAlignment verticalTextAlignment, boolean addFieldNames, ScaleToPaperSize scaleRuleForPrinting) {
			this.font = new Font(font.getFamily(), font.getStyle(), font.getSize());
			this.imageRotation = imageRotation;
			this.textRotation = textRotation;
			this.verticalTextAlignment = verticalTextAlignment;
			this.addFieldNames = addFieldNames;
			this.scaleRuleForPrinting = scaleRuleForPrinting;
		}
	}
}
