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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author Stefan Ganzer
 */
public class ImageUtilities {

	private ImageUtilities() {
	}

	/**
	 * Returns the given image as a RotatedBufferedImage that is rotated by the
	 * given rotation.
	 *
	 * @param image the image to return a rotated version of
	 * @param rotation the rotation that is to be applied to the given image
	 *
	 * @return RotatedBufferedImage that contains the given image rotated by the
	 * given rotation
	 *
	 * @throws NullPointerException if the given image is null, or if the given
	 * rotation is null
	 * @throws IllegalArgumentException if the width or height of the given
	 * image is not known yet
	 *
	 */
	public static RotatedBufferedImage getRotatedImage(Image image, Rotation rotation) {
		if (image == null) {
			throw new NullPointerException();
		}
		if (rotation == null) {
			throw new NullPointerException();
		}

		final Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));

		if (size.width == -1 || size.height == -1) {
			throw new IllegalArgumentException("Image not rendered yet: " + size.toString());
		}

		final Dimension sizeAfterRotation = rotation.getDimension(size);

		RotatedBufferedImage rbi = new RotatedBufferedImage(sizeAfterRotation.width, sizeAfterRotation.height, BufferedImage.TYPE_INT_ARGB, rotation);
		Graphics2D g2d = rbi.createGraphics();
		g2d.rotate(rotation.getRadiant(), size.width / 2.0, size.height / 2.0);
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		return rbi;
	}

	public static JLabel getImageAsJLabel(Image image) {
		if (image == null) {
			throw new NullPointerException();
		}
		ImageIcon imageIcon = new ImageIcon(image);
		JLabel imageLabel = new JLabel(imageIcon);
		imageLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());
		return imageLabel;
	}

	/**
	 * Returns an arbitrary image as BufferedImage.
	 *
	 * @param source an arbitrary image. May not be null.
	 * @param type one of the image types defined in {@link BufferedImage}
	 *
	 * @return the given source image as buffered image. Never returns null.
	 *
	 * @throws NullPointerException if the given source image is null
	 * @throws IllegalArgumentException if the width or height of the given
	 * source image is not known yet
	 */
	public static BufferedImage getAsBufferedImage(Image source, int type) {
		if (source == null) {
			throw new NullPointerException();
		}

		int width = source.getWidth(null);
		int height = source.getHeight(null);

		if (width == -1 || height == -1) {
			throw new IllegalArgumentException("Image not rendered yet: width=" + width + ", height=" + height);
		}

		BufferedImage bufferedImage = new BufferedImage(width, height, type);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawImage(source, 0, 0, null);
		g2d.dispose();
		return bufferedImage;
	}
}
