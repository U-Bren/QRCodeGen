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
public final class DecimalArcDegree implements Degree, Comparable<DecimalArcDegree> {
	
	private final double value;
	
	public DecimalArcDegree(double value) {
		this.value = value;
	}
	
	public DecimalArcDegree(Degree d) {
		if (d == null) {
			throw new NullPointerException();
		}
		value = d.getValue();
	}
	
	@Override
	public double getValue() {
		return value;
	}
	
	@Override
	public int getDegree() {
		return (int) value;
	}
	
	@Override
	public int getMinute() {
		return (int) Math.abs((value - getDegree()) * 60);
	}
	
	@Override
	public double getDecimalSecond() {
		return (Math.abs(value - getDegree()) - getMinute() / 60.0) * 3600;
	}
	
	@Override
	public int compareTo(DecimalArcDegree other) {
		if (other == null) {
			throw new NullPointerException();
		}
		return Double.compare(this.value, other.value);
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof DecimalArcDegree)) {
			return false;
		}
		DecimalArcDegree otherDegree = (DecimalArcDegree) other;
		return Double.compare(this.value, otherDegree.value) == 0;
	}
	
	@Override
	public boolean lessThan(Degree other){
		return Double.compare(this.value, other.getValue()) < 0;
	}
	
	@Override
	public boolean lessThanOrEqualTo(Degree other){
		return Double.compare(this.value, other.getValue()) <= 0;
	}

	@Override
	public boolean greaterThan(Degree other){
		return Double.compare(this.value, other.getValue()) > 0;
	}
	
	@Override
	public boolean greaterThanOrEqualTo(Degree other){
		return Double.compare(this.value, other.getValue()) >= 0;
	}

	@Override
	public boolean equalTo(Degree other){
		return Double.compare(this.value, other.getValue()) == 0;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		long valueAsLongBits = Double.doubleToLongBits(value);
		result = 31 * result + (int) (valueAsLongBits ^ (valueAsLongBits >>> 32));
		return result;
	}
	
	@Override
	public String toString() {
		String dms = String.format("%1$d° %2$d' %3$s''", getDegree(), getMinute(), Double.toString(getDecimalSecond()));
		return Double.toString(getValue()).concat(" == ").concat(dms);
	}
}
