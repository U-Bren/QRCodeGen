/*
 * Copyright (C) 2013 Stefan Ganzer
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
package qrcodegen.math;

import net.jcip.annotations.Immutable;

/**
 *
 * @author Stefan Ganzer
 */
@Immutable
public final class ArcDegree implements Degree, Comparable<ArcDegree> {

	private final int degree;
	private final int minute;
	private final double decimalSecond;

	public ArcDegree(int degree, int minute, double second) {
		if (minute < 0) {
			throw new IllegalArgumentException(Integer.toString(minute) + " < 0");
		}
		if (second < 0) {
			throw new IllegalArgumentException(Double.toString(second) + " < 0");
		}
		this.degree = degree;
		this.minute = minute;
		this.decimalSecond = second;
	}

	public ArcDegree(Degree d) {
		if (d == null) {
			throw new NullPointerException();
		}
		this.degree = d.getDegree();
		this.minute = d.getMinute();
		this.decimalSecond = d.getDecimalSecond();
	}

	@Override
	public double getValue() {
		return Math.signum(degree) * (Math.abs(degree) + minute / 60.0 + decimalSecond / 3600.0);
	}

	@Override
	public int getDegree() {
		return degree;
	}

	@Override
	public int getMinute() {
		assert minute >= 0 : minute;
		return minute;
	}

	@Override
	public double getDecimalSecond() {
		assert decimalSecond >= 0 : decimalSecond;
		return decimalSecond;
	}

	@Override
	public int compareTo(ArcDegree other) {
		if (other == null) {
			throw new NullPointerException();
		}
		if (this.degree < other.degree) {
			return -1;
		} else if (this.degree > other.degree) {
			return +1;
		}

		if (this.minute < other.minute) {
			return -1;
		} else if (this.minute > other.minute) {
			return +1;
		}

		return Double.compare(this.decimalSecond, other.decimalSecond);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ArcDegree)) {
			return false;
		}
		ArcDegree otherDegree = (ArcDegree) other;
		if (this.degree != otherDegree.degree) {
			return false;
		}
		if (this.minute != otherDegree.minute) {
			return false;
		}
		return Double.compare(this.decimalSecond, otherDegree.decimalSecond) == 0;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + degree;
		result = 31 * result + minute;
		long secondAsLongBits = Double.doubleToLongBits(decimalSecond);
		result = 31 * result + (int) (secondAsLongBits ^ (secondAsLongBits >>> 32));
		return result;
	}

	@Override
	public String toString() {
		String dms = String.format("%1$d° %2$d' %3$s''", getDegree(), getMinute(), Double.toString(getDecimalSecond()));
		return Double.toString(getValue()).concat(" == ").concat(dms);
	}

	@Override
	public boolean greaterThan(Degree other) {
		if (other instanceof ArcDegree) {
			return this.compareTo((ArcDegree) other) > 0;
		} else {
			return Double.compare(getValue(), other.getValue()) > 0;
		}
	}

	@Override
	public boolean greaterThanOrEqualTo(Degree other) {
		if (other instanceof ArcDegree) {
			return this.compareTo((ArcDegree) other) >= 0;
		} else {
			return Double.compare(getValue(), other.getValue()) >= 0;
		}
	}

	@Override
	public boolean lessThan(Degree other) {
		if (other instanceof ArcDegree) {
			return this.compareTo((ArcDegree) other) < 0;
		} else {
			return Double.compare(getValue(), other.getValue()) < 0;
		}
	}

	@Override
	public boolean lessThanOrEqualTo(Degree other) {
		if (other instanceof ArcDegree) {
			return this.compareTo((ArcDegree) other) <= 0;
		} else {
			return Double.compare(getValue(), other.getValue()) <= 0;
		}
	}

	@Override
	public boolean equalTo(Degree other) {
		if (other instanceof ArcDegree) {
			return this.compareTo((ArcDegree) other) == 0;
		} else {
			return Double.compare(getValue(), other.getValue()) == 0;
		}
	}
}
