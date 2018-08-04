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
package qrcodegen.tools;

/**
 *
 * @author Stefan Ganzer
 */
public final class PositiveImmutableDimension {

	private final int width;
	private final int height;

	public PositiveImmutableDimension(int width, int height) {
		if (width < 0) {
			throw new IllegalArgumentException(String.format("width: %1$d < 0", width));
		}
		if (height < 0) {
			throw new IllegalArgumentException(String.format("height: %1$d < 0", height));
		}
		this.width = width;
		this.height = height;
	}

	public PositiveImmutableDimension(java.awt.Dimension dim) {
		if (dim == null) {
			throw new NullPointerException();
		}
		this.width = dim.width;
		this.height = dim.height;
		if (width < 0) {
			throw new IllegalArgumentException(String.format("width: %1$d < 0", width));
		}
		if (height < 0) {
			throw new IllegalArgumentException(String.format("height: %1$d < 0", height));
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public java.awt.Dimension asAwtDimension() {
		return new java.awt.Dimension(width, height);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PositiveImmutableDimension)) {
			return false;
		}
		PositiveImmutableDimension otherDim = (PositiveImmutableDimension) other;
		if (this.width == otherDim.width && this.height == otherDim.height) {
			return true;
		}
		return false;
	}

	/**
	 * Returns true if this dimension exceeds in either width or height or both
	 * the width or height of the other dimension.
	 *
	 * @param other
	 *
	 * @return
	 */
	public boolean exceeds(PositiveImmutableDimension other) {
		if (other == null) {
			throw new NullPointerException();
		}
		boolean result;
		if (width > other.width || height > other.height) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * Calculates and returns this dimension's area.
	 *
	 * @return this dimension's area. Never returns negative values.
	 */
	public int getArea() {
		return -1;
	}

	/**
	 * Returns true if this dimensions is in both width and heigth as least as
	 * big as the other dimension, in other words, this dimension contains the
	 * other dimension.
	 *
	 * @param other
	 *
	 * @return
	 */
//	public boolean contains(ImmutableDimension other) {
//	}
	/**
	 * Returns a new dimension
	 *
	 * @param other
	 *
	 * @return
	 */
	public PositiveImmutableDimension combine(PositiveImmutableDimension other) {
		if (other == null) {
			throw new NullPointerException();
		}
		if (this.equals(other)) {
			return this;
		}
		int newWidth = Math.max(width, other.width);
		int newHeight = Math.max(height, other.height);
		return new PositiveImmutableDimension(newWidth, newHeight);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + width;
		result = 31 * result + height;
		return result;
	}

	@Override
	public String toString() {
		return "[" + width + "," + height + "]";
	}
}
