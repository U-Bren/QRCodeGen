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
package qrcodegen.uri;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import qrcodegen.math.DecimalArcDegree;
import qrcodegen.math.Degree;

/**
 * A geo URI according to RFC 5870. CRS = WGS84
 *
 * @author Stefan Ganzer
 */
public class GeoURI {

	private static final DecimalArcDegree NORTH_POLE = new DecimalArcDegree(90.0);
	private static final DecimalArcDegree SOUTH_POLE = new DecimalArcDegree(-90.0);
	private static final DecimalArcDegree LOWER_LONGITUDE_BOUND = new DecimalArcDegree(-180.0);
	private static final DecimalArcDegree UPPER_LONGITUDE_BOUND = new DecimalArcDegree(180.0);
	private final NumberFormat nf;
	private final DecimalArcDegree latitude;
	private final DecimalArcDegree longitude;
	private final Double altitude;

	public GeoURI(Degree latitude, Degree longitude) {
		this(latitude, longitude, null);
	}

	public GeoURI(Degree latitude, Degree longitude, double altitude) {
		this(latitude, longitude, Double.valueOf(altitude));
	}

	private GeoURI(Degree latitude, Degree longitude, Double altitude) {
		nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(340);
		if (nf instanceof DecimalFormat) {
			DecimalFormat df = (DecimalFormat) nf;
			df.setDecimalSeparatorAlwaysShown(false);
		}
		this.latitude = new DecimalArcDegree(latitude);

		DecimalArcDegree tempLongitude = new DecimalArcDegree(longitude);

		if (this.latitude.compareTo(SOUTH_POLE) < 0 || this.latitude.compareTo(NORTH_POLE) > 0) {
			throw new IllegalArgumentException(this.latitude.toString());
		}
		if (tempLongitude.compareTo(LOWER_LONGITUDE_BOUND) < 0 || tempLongitude.compareTo(UPPER_LONGITUDE_BOUND) > 0) {
			throw new IllegalArgumentException(tempLongitude.toString());
		}
		if (this.latitude.compareTo(SOUTH_POLE) == 0 || this.latitude.compareTo(NORTH_POLE) == 0) {
			tempLongitude = new DecimalArcDegree(0.0);
		}

		this.longitude = tempLongitude;
		this.altitude = altitude;
	}

	public Degree getLatitude() {
		return latitude;
	}

	public Degree getLongitude() {
		return longitude;
	}

	public double getAltitude() {
		if (!isAltitudeDefined()) {
			throw new IllegalStateException();
		}
		return altitude.doubleValue();
	}

	public boolean isAltitudeDefined() {
		return altitude != null;
	}

	@Override
	public String toString() {
		String alt = isAltitudeDefined() ? "," + nf.format(altitude) : "";
		return "geo:" + nf.format(latitude.getValue()) + "," + nf.format(longitude.getValue()) + alt;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof GeoURI)) {
			return false;
		}
		GeoURI otherURI = (GeoURI) other;
		return this.latitude.equals(otherURI.latitude)
				&& this.longitude.equals(otherURI.longitude)
				&& (altitude == null ? otherURI.altitude == null : this.altitude.equals(otherURI.altitude));
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = result + 31 * latitude.hashCode();
		result = result + 31 * longitude.hashCode();
		result = result + (altitude == null ? 0 : altitude.hashCode());
		return result;
	}
}
