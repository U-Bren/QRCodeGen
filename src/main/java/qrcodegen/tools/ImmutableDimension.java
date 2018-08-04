/*
 * Copyright (C) 2012 Stefan Ganzer
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
public final class ImmutableDimension {

	private final int width;
	private final int height;

	public ImmutableDimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public ImmutableDimension(java.awt.Dimension dim) {
		if (dim == null) {
			throw new NullPointerException();
		}
		this.width = dim.width;
		this.height = dim.height;
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
		if (!(other instanceof ImmutableDimension)) {
			return false;
		}
		ImmutableDimension otherDim = (ImmutableDimension) other;
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
	public boolean exceeds(ImmutableDimension other) {
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

//	public ImmutableDimension combine(ImmutableDimension other) {
//		if (other == null) {
//			throw new NullPointerException();
//		}
//		if (this.equals(other)) {
//			return this;
//		}
//		int newWidth = Math.max(width, other.width);
//		int newHeight = Math.max(height, other.height);
//		return new ImmutableDimension(newWidth, newHeight);
//	}
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
