/*
 Copyright 2012 Stefan Ganzer

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
package qrcodegen.modules.vcardgenpanel.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import qrcodegen.modules.vcard.BDayProperty;
import qrcodegen.modules.vcard.Calscale;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcardgenpanel.VCardPropertyProvider;
import qrcodegen.tools.CollectionTools;
import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardBDayModel extends AbstractModel implements VCardPropertyProvider {
	
	public static final String DATE_FORMAT_ELEMENT = "DateFormat";
	public static final String YEAR_ELEMENT = DateModel.YEAR_ELEMENT;
	public static final String MONTH_ELEMENT = DateModel.MONTH_ELEMENT;
	public static final String DAY_ELEMENT = DateModel.DAY_ELEMENT;
	public static final String DATE_ELEMENT = FreeformDateModel.DATE_ELEMENT;
	public static final String CALSCALE_ELEMENT = "Calscale";
	private static final int INITIAL_CAPACITY = 1;
	private static final DateFormat DEFAULT_DATE_FORMAT = DateFormat.DATE;
	private final DateModel date;
	private final TimeModel time;
	private final FreeformDateModel freeDate;
	private final PropertyChangeListener modelListener = new ModelListener();
	private DateFormat dateFormat = DateFormat.DATE;
	
	public VCardBDayModel() {
		this(new DateModel(), new TimeModel(), new FreeformDateModel());
	}
	
	public VCardBDayModel(DateModel dateModel, TimeModel timeModel, FreeformDateModel freeDateModel) {
		
		if (dateModel == null) {
			this.date = new DateModel();
		} else {
			this.date = dateModel;
		}
		
		if (timeModel == null) {
			this.time = new TimeModel();
		} else {
			this.time = timeModel;
		}
		
		if (freeDateModel == null) {
			this.freeDate = new FreeformDateModel();
		} else {
			this.freeDate = freeDateModel;
		}
		
		date.addPropertyChangeListener(modelListener);
		time.addPropertyChangeListener(modelListener);
		freeDate.addPropertyChangeListener(modelListener);
	}

	/**
	 * Sets the date format.
	 *
	 * @param format the date format
	 */
	public void setFormat(DateFormat format) {
		if (format == null) {
			throw new NullPointerException();
		}
		DateFormat oldFormat = dateFormat;
		dateFormat = format;
		firePropertyChange(DATE_FORMAT_ELEMENT, oldFormat, format);
	}

	/**
	 * Returns the date format.
	 *
	 * @return the date format. Never returns null.
	 */
	public DateFormat getFormat() {
		assert dateFormat != null;
		return dateFormat;
	}

	/**
	 * Sets the year.
	 *
	 * @param value a year
	 *
	 * @throws IllegalStateException if the current date format is not
	 * DateFormat.DATE
	 */
	public void setYear(int value) {
		if (!isDateFormat()) {
			throw new IllegalStateException();
		}
		date.setYear(value);
	}
	
	public boolean isYearSet() {
		return isDateFormat() && date.isYearSet();
	}
	
	public int getYear() {
		if (!isDateFormat()) {
			throw new IllegalStateException();
		}
		return date.getYear();
	}
	
	public void setMonth(int value) {
		if (!isDateFormat()) {
			throw new IllegalStateException();
		}
		date.setMonth(value);
	}
	
	public boolean isMonthSet() {
		return isDateFormat() && date.isMonthSet();
	}
	
	public int getMonth() {
		if (!isDateFormat()) {
			throw new IllegalStateException();
		}
		return date.getMonth();
	}
	
	public void setDay(int value) {
		if (!isDateFormat()) {
			throw new IllegalStateException();
		}
		date.setDay(value);
	}
	
	public boolean isDaySet() {
		return isDateFormat() && date.isDaySet();
	}
	
	public int getDay() {
		if (!isDateFormat()) {
			throw new IllegalStateException();
		}
		return date.getDay();
	}
	
	public void clearDate() {
		if (!isDateFormat()) {
			throw new IllegalStateException();
		}
		date.clear();
	}
	
	public void setDate(int year, int month, int day) {
		date.setDate(year, month, day);
	}

	/**
	 * Returns true if any one of year, month and day is set, regardless of
	 * whether the calscale parameter is set.
	 *
	 * @return true if any one of year, month and day is set, regardless of
	 * whether the calscale parameter is set
	 */
	public boolean isDateSet() {
		return date.isDateSet();
	}
	
	@Override
	public void clear() {
		date.clear();
		time.clear();
		freeDate.clear();
		setFormat(DEFAULT_DATE_FORMAT);
	}
	
	public boolean hasDate() {
		return isDateFormat() && !date.isEmpty();
	}
	
	public void setHour(int value) {
		if (!isTimeFormat()) {
			throw new IllegalStateException();
		}
		time.setHour(value);
	}
	
	public void clearHour() {
		if (!isTimeFormat()) {
			throw new IllegalStateException();
		}
		time.clearHour();
	}
	
	public int getHour() {
		if (!isTimeFormat()) {
			throw new IllegalStateException();
		}
		return time.getHour();
	}
	
	public boolean isHourSet() {
		return isTimeFormat() && time.isHourSet();
	}
	
	public void setMinute(int value) {
		if (!isTimeFormat()) {
			throw new IllegalStateException();
		}
		time.setMinute(value);
	}
	
	public void clearMinute() {
		if (!isTimeFormat()) {
			throw new IllegalStateException();
		}
		time.clearMinute();
	}
	
	public int getMinute() {
		if (!isTimeFormat()) {
			throw new IllegalStateException();
		}
		return time.getMinute();
	}
	
	public boolean isMinuteSet() {
		return isTimeFormat() && time.isMinuteSet();
	}
	
	public void setSecond(int value) {
		if (!isTimeFormat()) {
			throw new IllegalStateException();
		}
		time.setSecond(value);
	}
	
	public void clearSecond() {
		if (!isTimeFormat()) {
			throw new IllegalStateException();
		}
		time.clearSecond();
	}
	
	public int getSecond() {
		if (!isTimeFormat()) {
			throw new IllegalStateException();
		}
		return time.getSecond();
	}
	
	public boolean isSecondSet() {
		return isTimeFormat() && time.isSecondSet();
	}
	
	public void clearTime() {
		if (!isTimeFormat()) {
			throw new IllegalStateException();
		}
		time.clear();
	}
	
	public boolean hasTime() {
		return isTimeFormat() && !time.isEmpty();
	}
	
	public void setFreeformDate(String date) {
		freeDate.setDate(date);
	}
	
	public boolean isFreeFormDateSet() {
		return isFreeformFormat() && freeDate.isDateSet();
	}
	
	public String getFreeFormDate() {
		if (!isFreeformFormat()) {
			throw new IllegalStateException();
		}
		return freeDate.getDate();
	}
	
	public void clearFreeFormDate() {
		if (!isFreeformFormat()) {
			throw new IllegalStateException();
		}
		freeDate.clear();
	}
	
	public boolean hasCalscale() {
		return isDateFormat() && date.hasCalscale();
	}
	
	public Calscale getCalscale() {
		if (!isDateFormat()) {
			throw new IllegalStateException();
		}
		return date.getCalscale();
	}
	
	public void clearCalscale() {
		if (!isDateFormat()) {
			throw new IllegalStateException();
		}
		date.clearCalscale();
	}
	
	private boolean isDateFormat() {
		return dateFormat == DateFormat.DATE || dateFormat == DateFormat.DATE_TIME;
	}
	
	private boolean isTimeFormat() {
		return dateFormat == DateFormat.TIME || dateFormat == DateFormat.DATE_TIME;
	}
	
	private boolean isFreeformFormat() {
		return dateFormat == DateFormat.FREEFORM;
	}
	
	private BDayProperty getBDayProperty() {
		BDayProperty bdayProp;
		if (dateFormat == DateFormat.DATE) {
			if (date.isEmpty()) {
				bdayProp = null;
			} else {
				BDayProperty.Builder builder = new BDayProperty.Builder();
				if (date.isYearSet()) {
					builder.year(date.getYear());
				}
				if (date.isMonthSet()) {
					builder.month(date.getMonth());
				}
				if (date.isDaySet()) {
					builder.day(date.getDay());
				}
				bdayProp = builder.build();
			}
			
		} else {
			assert dateFormat == DateFormat.FREEFORM : dateFormat;
			if (freeDate.isDateSet()) {
				bdayProp = new BDayProperty.Builder().text(freeDate.getDate()).build();
			} else {
				bdayProp = null;
			}
		}
		
		return bdayProp;
	}
	
	@Override
	public List<PropertyEntry> getPropertyEntries() {
		List<PropertyEntry> result = new ArrayList<PropertyEntry>(INITIAL_CAPACITY);
		CollectionTools.addIfNonNull(result, getBDayProperty());
		return result;
	}
	
	public void setFromPropertyEntries(List<BDayProperty> properties) {
		if (properties == null) {
			throw new NullPointerException();
		}
		if (!properties.isEmpty()) {
			BDayProperty p = properties.get(0);
			if (p.hasDateValue()) {
				setFormat(DateFormat.DATE);
				int year;
				if (p.hasYear()) {
					year = p.getYear();
				} else {
					year = -1;
				}
				int month;
				if (p.hasMonth()) {
					month = p.getMonth();
				} else {
					month = -1;
				}
				int day;
				if (p.hasDay()) {
					day = p.getDay();
				} else {
					day = -1;
				}
				setDate(year, month, day);
			} else {
				setFormat(DateFormat.FREEFORM);
				setFreeformDate(p.getText());
			}
			
		}
	}
	
	private class ModelListener implements PropertyChangeListener {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange(evt);
		}
	}
}
