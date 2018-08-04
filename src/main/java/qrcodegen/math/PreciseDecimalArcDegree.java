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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import net.jcip.annotations.Immutable;

/**
 *
 * @author Stefan Ganzer
 */
@Immutable
public final class PreciseDecimalArcDegree implements Degree, Comparable<PreciseDecimalArcDegree> {

	private static final BigDecimal BD_60 = BigDecimal.valueOf(60);
	private static final BigDecimal BD_3600 = BigDecimal.valueOf(3600);
	private final BigDecimal val;

	public PreciseDecimalArcDegree(double value) {
		val = BigDecimal.valueOf(value);
	}

	public PreciseDecimalArcDegree(Degree d) {
		if (d == null) {
			throw new NullPointerException();
		}
		val = BigDecimal.valueOf(d.getValue());
	}

	@Override
	public double getValue() {
		return val.doubleValue();
	}

	@Override
	public int getDegree() {
		return val.intValue();
	}

	@Override
	public int getMinute() {
		return absoluteDecimals().multiply(BD_60).intValue();
		//return (int) Math.abs((value - getDegree()) * 60);
	}

	private BigDecimal absoluteDecimals() {
		return val.subtract(BigDecimal.valueOf(getDegree())).abs();
	}

	@Override
	public double getDecimalSecond() {
		BigDecimal minuteBySixty = BigDecimal.valueOf(getMinute()).divide(BD_60, MathContext.DECIMAL128);
		return absoluteDecimals().subtract(minuteBySixty).multiply(BigDecimal.valueOf(3600)).doubleValue();
		//return (Math.abs(value - getDegree()) - getMinute() / 60.0) * 3600;
	}

	@Override
	public int compareTo(PreciseDecimalArcDegree other) {
		if (other == null) {
			throw new NullPointerException();
		}
		return val.compareTo(other.val);
		//return Double.compare(this.value, other.value);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PreciseDecimalArcDegree)) {
			return false;
		}
		PreciseDecimalArcDegree otherDegree = (PreciseDecimalArcDegree) other;
		return val.equals(otherDegree.val);
		//return Double.compare(this.value, otherDegree.value) == 0;
	}

	@Override
	public int hashCode() {
//		int result = 17;
//		long valueAsLongBits = Double.doubleToLongBits(value);
//		result = 31 * result + (int) (valueAsLongBits ^ (valueAsLongBits >>> 32));
//		return result;
		return val.hashCode();
	}

	@Override
	public String toString() {
		String dms = String.format("%1$d° %2$d' %3$s''", getDegree(), getMinute(), Double.toString(getDecimalSecond()));
		return Double.toString(getValue()).concat(" == ").concat(dms);
	}

	@Override
	public boolean greaterThan(Degree other) {
		if (other instanceof PreciseDecimalArcDegree) {
			return this.compareTo((PreciseDecimalArcDegree) other) > 0;
		} else {
			return Double.compare(getValue(), other.getValue()) > 0;
		}
	}

	@Override
	public boolean greaterThanOrEqualTo(Degree other) {
		if (other instanceof PreciseDecimalArcDegree) {
			return this.compareTo((PreciseDecimalArcDegree) other) >= 0;
		} else {
			return Double.compare(getValue(), other.getValue()) >= 0;
		}
	}

	@Override
	public boolean lessThan(Degree other) {
		if (other instanceof PreciseDecimalArcDegree) {
			return this.compareTo((PreciseDecimalArcDegree) other) < 0;
		} else {
			return Double.compare(getValue(), other.getValue()) < 0;
		}
	}

	@Override
	public boolean lessThanOrEqualTo(Degree other) {
		if (other instanceof PreciseDecimalArcDegree) {
			return this.compareTo((PreciseDecimalArcDegree) other) <= 0;
		} else {
			return Double.compare(getValue(), other.getValue()) <= 0;
		}
	}

	@Override
	public boolean equalTo(Degree other) {
		if (other instanceof PreciseDecimalArcDegree) {
			return this.compareTo((PreciseDecimalArcDegree) other) == 0;
		} else {
			return Double.compare(getValue(), other.getValue()) == 0;
		}
	}
}
