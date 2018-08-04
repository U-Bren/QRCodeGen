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
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

/**
 * This class stores a rotation information along with the picture,
 * and allows to create rotated versions of it.
 * @author Stefan Ganzer
 */
public class RotatedBufferedImage extends BufferedImage {

	private final Rotation rotation;

	public RotatedBufferedImage(int width, int height, int imageType, Rotation rotation) {
		super(width, height, imageType);
		if (rotation == null) {
			throw new NullPointerException();
		}
		this.rotation = rotation;
	}

	public RotatedBufferedImage(int width, int height, int imageType, IndexColorModel cm, Rotation rotation) {
		super(width, height, imageType, cm);
		if (rotation == null) {
			throw new NullPointerException();
		}
		this.rotation = rotation;
	}

	public RotatedBufferedImage(ColorModel cm, WritableRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties, Rotation rotation) {
		super(cm, raster, isRasterPremultiplied, properties);
		if (rotation == null) {
			throw new NullPointerException();
		}
		this.rotation = rotation;
	}

	/**
	 * Returns the rotation associated with this RotatedBufferedImage.
	 * @return the rotation associated with this RotatedBufferedImage
	 */
	public Rotation getRotation() {
		assert rotation != null;
		return rotation;
	}

	/**
	 * Creates a new RotatedBufferedImage from this image, and rotates it to the given angle.
	 * The angle to rotate to is independent from this image' current angle, that is if this image'
	 * rotation is 270°, and you want the new image to have an angle of 0°, you specifiy {@link Rotation R0}.
	 * @param r the angle the new image shall have. This angle is independent from the current angle.
	 * @return a new RotatedBufferedImage with this image' content rotated to the given angle
	 */
	public RotatedBufferedImage rotateTo(Rotation r) {
		if (r == null) {
			throw new NullPointerException();
		}
		return getRotatedImage(this, rotation.rotateTo(r), r);
	}

	/**
	 * Returns a new RotatedBufferedImage that contains the given image rotated
	 * by rotation, and which {@link getRotation()} returns the given actualRotation.
	 * This way you can rotate an image from 90° for 270° to 0°, with rotation=270 and actualRotation=0.
	 * @param image
	 * @param rotation
	 * @param actualRotation
	 * @return 
	 */
	private static RotatedBufferedImage getRotatedImage(Image image, Rotation rotation, Rotation actualRotation) {
		if (image == null) {
			throw new NullPointerException();
		}
		if (rotation == null) {
			throw new NullPointerException();
		}
		if(actualRotation == null){
			throw new NullPointerException();
		}
	
		final Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
		Dimension sizeAfterRotation = rotation.getDimension(size);

		RotatedBufferedImage bi = new RotatedBufferedImage(sizeAfterRotation.width,
				sizeAfterRotation.height, BufferedImage.TYPE_INT_ARGB, actualRotation);
		Graphics2D g2d = bi.createGraphics();
		g2d.rotate(rotation.getRadiant(), size.width / 2.0, size.height / 2.0);
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		return bi;
	}
}
