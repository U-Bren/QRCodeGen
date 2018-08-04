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
package qrcodegen.modules;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import qrcodegen.kml.Coordinates;
import qrcodegen.math.Degree;
import qrcodegen.math.PreciseArcDegree;
import qrcodegen.math.PreciseDecimalArcDegree;
import qrcodegen.tools.ExtendedIndexOutOfBoundsException;
import qrcodegen.tools.StaticTools;
import qrcodegen.tools.TriState;
import qrcodegen.uri.GeoURI;

/**
 *
 * @author Stefan Ganzer
 */
public class GeoURIModel {

	public static final String DECIMAL_LONGITUDE_PROPERTY = "DecimalLongitude";
	public static final String DECIMAL_LATITUDE_PROPERTY = "DecimalLatitude";
	public static final String LONGITUDE_PROPERTY = "Longitude";
	public static final String LATITUDE_PROPERTY = "Latitude";
	public static final String ALTITUDE_PROPERTY = "Altitude";
	public static final String LONGITUDE_DEGREE_PROPERTY = "LongitudeDegree";
	public static final String LONGITUDE_MINUTE_PROPERTY = "LongitudeMinute";
	public static final String LONGITUDE_DECIMAL_SECOND_PROPERTY = "LongitudeDecimalSecond";
	public static final String LATITUDE_DEGREE_PROPERTY = "LatitudeDegree";
	public static final String LATITUDE_MINUTE_PROPERTY = "LatitudeMinute";
	public static final String LATITUDE_DECIMAL_SECOND_PROPERTY = "LatitudeDecimalSecond";
	public static final String VALID_LONGITUDE_PROPERTY = "IsValidLongitude";
	public static final String VALID_LATITUDE_PROPERTY = "IsValidLatitude";
	public static final String NAME_PROPERTY = "Name";
	public static final String ADDRESS_PROPERTY = "Address";
	public static final String ZOOM_PROPERTY = "Zoom";
	private static final Degree NORTH_POLE = new PreciseDecimalArcDegree(90.0);
	private static final Degree SOUTH_POLE = new PreciseDecimalArcDegree(-90.0);
	private static final Degree LOWER_LONGITUDE_BOUND = new PreciseDecimalArcDegree(-180.0);
	private static final Degree UPPER_LONGITUDE_BOUND = new PreciseDecimalArcDegree(180.0);
	private static final String EMPTY_STRING = "";
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private Degree decimalLatitude;
	private Degree decimalLongitude;
	private Degree latitude;
	private Degree longitude;
	private Double altitude;
	private String name;
	private String address;
	private int zoom;
	private TriState isValidLongitude = TriState.NOT_APPLICABLE;
	private TriState isValidLatitude = TriState.NOT_APPLICABLE;

	public GeoURIModel() {
		decimalLatitude = new PreciseDecimalArcDegree(0);
		decimalLongitude = new PreciseDecimalArcDegree(0);

		latitude = new PreciseArcDegree(0, 0, 0);
		longitude = new PreciseArcDegree(0, 0, 0);

		altitude = null;
	}

	public void setCoordinates(Coordinates c) {
		if (c == null) {
			throw new NullPointerException();
		}
		setDecimalLatitude(c.getLatitude());
		setDecimalLongitude(c.getLongitude());
		if (c.isAltitudeDefined()) {
			setAltitude(c.getAltitude());
		} else {
			clearAltitude();
		}
	}

	public void setDecimalLongitude(double value) {
		setDecimalLongitude(new PreciseDecimalArcDegree(value), true);
	}

	private void setDecimalLongitude(Degree value, boolean propagateToArcDegree) {
		Degree oldValue = decimalLongitude;
		decimalLongitude = value;
		if (!StaticTools.bothNullOrEqual(oldValue, value)) {
			if (value == null) {
				setIsValidLongitude(TriState.NOT_APPLICABLE);
			} else if (value.greaterThanOrEqualTo(LOWER_LONGITUDE_BOUND) && value.lessThanOrEqualTo(UPPER_LONGITUDE_BOUND)) {
				setIsValidLongitude(TriState.TRUE);
			} else {
				setIsValidLongitude(TriState.FALSE);
			}
			if (propagateToArcDegree) {
				setLongitude(value, false);
			}
			pcs.firePropertyChange(DECIMAL_LONGITUDE_PROPERTY, oldValue, value);
		}

	}

	public void clearLongitude() {
		setDecimalLongitude(null, true);
	}

	public boolean isDecimalLongitudeSet() {
		return decimalLongitude != null;
	}

	private void setIsValidLongitude(TriState state) {
		assert state != null;
		TriState oldState = isValidLongitude;
		isValidLongitude = state;
		pcs.firePropertyChange(VALID_LONGITUDE_PROPERTY, oldState, state);
	}

	public TriState getIsValidLongitude() {
		assert isValidLongitude != null;
		return isValidLongitude;
	}

	public double getDecimalLongitude() {
		return decimalLongitude.getValue();
	}

	public void setDecimalLatitude(double value) {
		setDecimalLatitude(new PreciseDecimalArcDegree(value), true);
	}

	private void setDecimalLatitude(Degree value, boolean propagateToArcDegree) {
		Degree oldValue = decimalLatitude;
		decimalLatitude = value;
		if (!StaticTools.bothNullOrEqual(oldValue, value)) {
			if (value == null) {
				setIsValidLatitude(TriState.NOT_APPLICABLE);
			} else if (value.greaterThanOrEqualTo(SOUTH_POLE) && value.lessThanOrEqualTo(NORTH_POLE)) {
				setIsValidLatitude(TriState.TRUE);
			} else {
				setIsValidLatitude(TriState.FALSE);
			}
			if (propagateToArcDegree) {
				setLatitude(value, false);
			}
			pcs.firePropertyChange(DECIMAL_LATITUDE_PROPERTY, oldValue, value);
		}
	}

	public void clearLatitude() {
		setDecimalLatitude(null, true);
	}

	public boolean isDecimalLatitudeSet() {
		return decimalLatitude != null;
	}

	public double getDecimalLatitude() {
		return decimalLatitude.getValue();
	}

	private void setIsValidLatitude(TriState state) {
		assert state != null;
		TriState oldState = isValidLatitude;
		isValidLatitude = state;
		pcs.firePropertyChange(VALID_LATITUDE_PROPERTY, oldState, state);
	}

	public TriState getIsValidLatitude() {
		assert isValidLatitude != null;
		return isValidLatitude;
	}

	public void setLongitudeDegree(int value) {
		int degree = value;
		int minute = isLongitudeSet() ? longitude.getMinute() : 0;
		double decSecond = isLongitudeSet() ? longitude.getDecimalSecond() : 0;
		setLongitude(new PreciseArcDegree(degree, minute, decSecond), true);
	}

	public void setLongitudeMinute(int value) {
		int degree = isLongitudeSet() ? longitude.getDegree() : 0;
		int minute = value;
		double decSecond = isLongitudeSet() ? longitude.getDecimalSecond() : 0;
		setLongitude(new PreciseArcDegree(degree, minute, decSecond), true);
	}

	public void setLongitudeDecimalSecond(double value) {
		int degree = isLongitudeSet() ? longitude.getDegree() : 0;
		int minute = isLongitudeSet() ? longitude.getMinute() : 0;
		double decSecond = value;
		setLongitude(new PreciseArcDegree(degree, minute, decSecond), true);
	}

	public void setLongitude(int degree, int minute, double decSecond) {
		setLongitude(new PreciseArcDegree(degree, minute, decSecond), true);
	}

	public boolean isLongitudeSet() {
		return longitude != null;
	}

	private void setLongitude(Degree value, boolean propagateToDecimalLongitude) {
		Degree oldValue = longitude;
		longitude = value == null ? null : new PreciseArcDegree(value);
		if (propagateToDecimalLongitude && !StaticTools.bothNullOrEqual(oldValue, longitude)) {
			setDecimalLongitude(new PreciseDecimalArcDegree(value), false);
		}
		firePropertyChangeForLongitude(oldValue, longitude);
	}

	private void firePropertyChangeForLongitude(Degree oldValue, Degree value) {
		Integer oldDegree = oldValue == null ? null : oldValue.getDegree();
		Integer oldMinute = oldValue == null ? null : oldValue.getMinute();
		Double oldSecond = oldValue == null ? null : oldValue.getDecimalSecond();

		Integer degree = value == null ? null : value.getDegree();
		Integer minute = value == null ? null : value.getMinute();
		Double second = value == null ? null : value.getDecimalSecond();
		pcs.firePropertyChange(LONGITUDE_DEGREE_PROPERTY, oldDegree, degree);
		pcs.firePropertyChange(LONGITUDE_MINUTE_PROPERTY, oldMinute, minute);
		pcs.firePropertyChange(LONGITUDE_DECIMAL_SECOND_PROPERTY, oldSecond, second);
	}

	public int getLongitudeDegree() {
		return longitude.getDegree();
	}

	public int getLongitudeMinute() {
		return longitude.getMinute();
	}

	public double getLongitudeDecimalSecond() {
		return longitude.getDecimalSecond();
	}

	public void setLatitudeDegree(int value) {
		int degree = value;
		int minute = isLatitudeSet() ? latitude.getMinute() : 0;
		double decSecond = isLatitudeSet() ? latitude.getDecimalSecond() : 0;
		setLatitude(new PreciseArcDegree(degree, minute, decSecond), true);
	}

	public void setLatitudeMinute(int value) {
		int degree = isLatitudeSet() ? latitude.getDegree() : 0;
		int minute = value;
		double decSecond = isLatitudeSet() ? latitude.getDecimalSecond() : 0;
		setLatitude(new PreciseArcDegree(degree, minute, decSecond), true);
	}

	public void setLatitudeDecimalSecond(double value) {
		int degree = isLatitudeSet() ? latitude.getDegree() : 0;
		int minute = isLatitudeSet() ? latitude.getMinute() : 0;
		double decSecond = value;
		setLatitude(new PreciseArcDegree(degree, minute, decSecond), true);
	}

	public void setLatitude(int degree, int minute, double decSecond) {
		setLatitude(new PreciseArcDegree(degree, minute, decSecond), true);
	}

	public boolean isLatitudeSet() {
		return latitude != null;
	}

	private void setLatitude(Degree value, boolean propagateToDecimalLatitude) {
		Degree oldValue = latitude;
		latitude = value == null ? null : new PreciseArcDegree(value);
		if (propagateToDecimalLatitude && !StaticTools.bothNullOrEqual(oldValue, latitude)) {
			setDecimalLatitude(new PreciseDecimalArcDegree(value), false);
		}
		firePropertyChangeForLatitude(oldValue, latitude);
	}

	private void firePropertyChangeForLatitude(Degree oldValue, Degree value) {
		Integer oldDegree = oldValue == null ? null : oldValue.getDegree();
		Integer oldMinute = oldValue == null ? null : oldValue.getMinute();
		Double oldSecond = oldValue == null ? null : oldValue.getDecimalSecond();

		Integer degree = value == null ? null : value.getDegree();
		Integer minute = value == null ? null : value.getMinute();
		Double second = value == null ? null : value.getDecimalSecond();
		pcs.firePropertyChange(LATITUDE_DEGREE_PROPERTY, oldDegree, degree);
		pcs.firePropertyChange(LATITUDE_MINUTE_PROPERTY, oldMinute, minute);
		pcs.firePropertyChange(LATITUDE_DECIMAL_SECOND_PROPERTY, oldSecond, second);
	}

	public int getLatitudeDegree() {
		return latitude.getDegree();
	}

	public int getLatitudeMinute() {
		return latitude.getMinute();
	}

	public double getLatitudeDecimalSecond() {
		return latitude.getDecimalSecond();
	}

	public void clearAltitude() {
		Double oldValue = this.altitude;
		this.altitude = null;
		pcs.firePropertyChange(ALTITUDE_PROPERTY, oldValue, this.altitude);
	}

	public boolean isAltitudeDefined() {
		return altitude != null;
	}

	public void setAltitude(double value) {
		Double oldValue = this.altitude;
		this.altitude = Double.valueOf(value);
		pcs.firePropertyChange(ALTITUDE_PROPERTY, oldValue, this.altitude);
	}

	public double getAltitude() {
		if (!isAltitudeDefined()) {
			throw new IllegalStateException();
		}
		return altitude;
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		pcs.firePropertyChange(NAME_PROPERTY, oldName, name);
	}
	
	public boolean isNameSet(){
		return name != null;
	}

	public void clearName() {
		setName(null);
	}

	public String getName() {
		return name;
	}

	public void setAddress(String address) {
		String oldAddress = this.address;
		this.address = address;
		pcs.firePropertyChange(ADDRESS_PROPERTY, oldAddress, address);
	}
	
	public boolean isAddressSet(){
		return address != null;
	}

	public void clearAddress() {
		setAddress(null);
	}

	public String getAddress() {
		return address;
	}

	public void setZoom(int zoom) {
		if (zoom < 1 || zoom > 23) {
			throw new ExtendedIndexOutOfBoundsException(1, 23, zoom);
		}
		setZoomValue(zoom);
	}

	private void setZoomValue(int zoom) {
		int oldZoom = this.zoom;
		this.zoom = zoom;
		pcs.firePropertyChange(ZOOM_PROPERTY, oldZoom, zoom);
	}

	public void clearZoom() {
		setZoomValue(-1);
	}

	public boolean isZoomSet() {
		return zoom > 0;
	}

	public int getZoom() {
		if (!isZoomSet()) {
			throw new IllegalStateException();
		}
		return zoom;
	}

	public GeoURI getGeoURI() {
		if (isAltitudeDefined()) {
			return new GeoURI(decimalLatitude, decimalLongitude, altitude.doubleValue());
		} else {
			return new GeoURI(decimalLatitude, decimalLongitude);
		}
	}
	
	public String getExtendedGeoURIAsString(){
		String s = getGeoURI().toString();
		s = s.concat(getZoomQuery());
		return s;
	}
	
	public String getZoomQuery(){
		// See https://developer.android.com/guide/appendix/g-app-intents.html
		String result;
		if(isZoomSet()){
			result = "?z=".concat(Integer.toString(zoom));
		}else{
			result = EMPTY_STRING;
		}
		return result;
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
}
