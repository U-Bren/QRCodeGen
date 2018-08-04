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
import net.jcip.annotations.Immutable;

/**
 *
 * @author Stefan Ganzer
 */
@Immutable
public final class PreciseArcDegree implements Degree, Comparable<PreciseArcDegree> {
	private static final BigDecimal BD_60 = BigDecimal.valueOf(60);
	private static final BigDecimal BD_3600 = BigDecimal.valueOf(3600);

	private final BigDecimal degree;
	private final BigDecimal minute;
	private final BigDecimal decSecond;

	public PreciseArcDegree(int degree, int minute, double second) {
		if (minute < 0) {
			throw new IllegalArgumentException(Integer.toString(minute) + " < 0");
		}
		if (second < 0) {
			throw new IllegalArgumentException(Double.toString(second) + " < 0");
		}
		this.degree = BigDecimal.valueOf(degree);
		this.minute = BigDecimal.valueOf(minute);
		this.decSecond = BigDecimal.valueOf(second);
	}

	public PreciseArcDegree(Degree d) {
		if (d == null) {
			throw new NullPointerException();
		}
		this.degree = BigDecimal.valueOf(d.getDegree());
		this.minute = BigDecimal.valueOf(d.getMinute());
		this.decSecond = BigDecimal.valueOf(d.getDecimalSecond());
	}

	@Override
	public double getValue() {
		return degree.signum() * (degree.abs().add(minute.divide(BD_60, MathContext.DECIMAL128)).add(decSecond.divide(BD_3600, MathContext.DECIMAL128))).doubleValue();
	}

	@Override
	public int getDegree() {
		return degree.intValue();
	}

	@Override
	public int getMinute() {
		return minute.intValue();
	}

	@Override
	public double getDecimalSecond() {
		return decSecond.doubleValue();
	}

	@Override
	public int compareTo(PreciseArcDegree other) {
		if (other == null) {
			throw new NullPointerException();
		}
		if(this.degree.compareTo(other.degree) < 0){
			return -1;
		}else if(this.degree.compareTo(other.degree) > 0){
			return +1;
		}
		
		if(this.minute.compareTo(other.minute) < 0){
			return -1;
		}else if(this.minute.compareTo(other.minute) > 0){
			return +1;
		}
		
		return this.decSecond.compareTo(other.decSecond);

	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof PreciseArcDegree)) {
			return false;
		}
		PreciseArcDegree otherDegree = (PreciseArcDegree) other;
		if(!this.degree.equals(otherDegree.degree)){
			return false;
		}
		if(!this.minute.equals(otherDegree.minute)){
			return false;
		}
		return this.decSecond.equals(otherDegree.decSecond);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + degree.hashCode();
		result = 31 * result + minute.hashCode();
		result = 31 * result + decSecond.hashCode();
		return result;
	}

	@Override
	public String toString() {
		String dms = String.format("%1$d° %2$d' %3$s''", getDegree(), getMinute(), Double.toString(getDecimalSecond()));
		return Double.toString(getValue()).concat(" == ").concat(dms);
	}

	@Override
	public boolean greaterThan(Degree other) {
		if (other instanceof PreciseArcDegree) {
			return this.compareTo((PreciseArcDegree) other) > 0;
		} else {
			return Double.compare(getValue(), other.getValue()) > 0;
		}
	}

	@Override
	public boolean greaterThanOrEqualTo(Degree other) {
		if (other instanceof PreciseArcDegree) {
			return this.compareTo((PreciseArcDegree) other) >= 0;
		} else {
			return Double.compare(getValue(), other.getValue()) >= 0;
		}
	}

	@Override
	public boolean lessThan(Degree other) {
		if (other instanceof PreciseArcDegree) {
			return this.compareTo((PreciseArcDegree) other) < 0;
		} else {
			return Double.compare(getValue(), other.getValue()) < 0;
		}
	}

	@Override
	public boolean lessThanOrEqualTo(Degree other) {
		if (other instanceof PreciseArcDegree) {
			return this.compareTo((PreciseArcDegree) other) <= 0;
		} else {
			return Double.compare(getValue(), other.getValue()) <= 0;
		}
	}

	@Override
	public boolean equalTo(Degree other) {
		if (other instanceof PreciseArcDegree) {
			return this.compareTo((PreciseArcDegree) other) == 0;
		} else {
			return Double.compare(getValue(), other.getValue()) == 0;
		}
	}
}
