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
package qrcodegen.kml;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author Stefan Ganzer
 */
public class Coordinates {
	
	private final NumberFormat nf;
	private final double longitude;
	private final double latitude;
	private final Double altitude;
	
	public Coordinates() {
		this(0, 0, 0);
	}
	
	public Coordinates(String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(340);
		if (nf instanceof DecimalFormat) {
			DecimalFormat df = (DecimalFormat) nf;
			df.setDecimalSeparatorAlwaysShown(false);
		}
		
		String[] n = s.split(",", -1);
		if (n.length < 2 || n.length > 3) {
			throw new IllegalArgumentException("2 <= " + Integer.toString(n.length) + " <= 3");
		}
		
		longitude = Double.parseDouble(n[0]);
		latitude = Double.parseDouble(n[1]);
		
		if (n.length == 3) {
			altitude = Double.parseDouble(n[2]);
		} else {
			altitude = null;
		}
	}
	
	public Coordinates(double longitude, double latitude, double altitude) {
		nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(340);
		if (nf instanceof DecimalFormat) {
			DecimalFormat df = (DecimalFormat) nf;
			df.setDecimalSeparatorAlwaysShown(false);
		}
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
	}
	
	public double getLongitude() {
		return longitude;
	}
	
	public double getLatitude() {
		return latitude;
	}
	
	public boolean isAltitudeDefined() {
		return altitude != null;
	}
	
	public double getAltitude() {
		return altitude;
	}
	
	@Override
	public String toString() {
		return nf.format(longitude) + "," + nf.format(latitude) + "," + nf.format(altitude);
	}
}
