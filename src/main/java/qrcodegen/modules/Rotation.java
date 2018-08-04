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
import qrcodegen.tools.ImmutableDimension;

/**
 * This class can be used with {@link java.awt.Graphics2D rotation(double)}.
 *
 * @author Stefan Ganzer
 */
public enum Rotation {

	R0(0, 0.0) {
		@Override
		public Dimension getDimension(Dimension dim) {
			if (dim == null) {
				throw new NullPointerException();
			}
			return new Dimension(dim.width, dim.height);
		}
	}, R90(90, Math.toRadians(90)) {
		@Override
		public Dimension getDimension(Dimension dim) {
			if (dim == null) {
				throw new NullPointerException();
			}
			return new Dimension(dim.height, dim.width);
		}
	}, R180(180, Math.toRadians(180)) {
		@Override
		public Dimension getDimension(Dimension dim) {
			if (dim == null) {
				throw new NullPointerException();
			}
			return new Dimension(dim.width, dim.height);
		}
	}, R270(270, Math.toRadians(270)) {
		@Override
		public Dimension getDimension(Dimension dim) {
			if (dim == null) {
				throw new NullPointerException();
			}
			return new Dimension(dim.height, dim.width);
		}
	};
	private final int degree;
	private final double radiant;

	private Rotation(int degree, double radiant) {
		this.degree = degree;
		this.radiant = radiant;
	}

	public int getDegree() {
		return degree;
	}

	public double getRadiant() {
		return radiant;
	}

	public Rotation rotateTo(Rotation r) {
		if (r == null) {
			throw new NullPointerException();
		}
		
		int newDegree = 360 - getDegree() + r.getDegree();
		if (newDegree >= 360) {
			newDegree = newDegree - 360;
		}

		return Rotation.fromInt(newDegree);
	}

	public abstract Dimension getDimension(Dimension dim);

	@Override
	public String toString() {
		return String.format("%1$d°", degree);
	}

	public static Rotation fromInt(int i) {
		Rotation result;
		switch (i) {
			case 0:
				result = R0;
				break;
			case 90:
				result = R90;
				break;
			case 180:
				result = R180;
				break;
			case 270:
				result = R270;
				break;
			default:
				throw new IllegalArgumentException("Only 0°, 90°, 180° and 270° are allowed, but not " + i);
		}
		return result;
	}

	public static Rotation fromString(String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		Rotation result;
		if ("0".equals(s) || "0°".equals(s)) {
			result = R0;
		} else if ("90".equals(s) || "90°".equals(s)) {
			result = R90;
		} else if ("180".equals(s) || "180°".equals(s)) {
			result = R180;
		} else if ("270".equals(s) || "270°".equals(s)) {
			result = R270;
		} else {
			throw new IllegalArgumentException(s);
		}
		return result;
	}
}
