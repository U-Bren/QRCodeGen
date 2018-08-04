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
package qrcodegen.modules.vcardgenpanel.presentationmodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.modules.vcardgenpanel.model.AbstractModel;
import qrcodegen.modules.vcardgenpanel.model.DateFormat;
import qrcodegen.modules.vcardgenpanel.model.DateModel;
import qrcodegen.modules.vcardgenpanel.model.VCardBDayModel;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardBDayPresentationModel extends AbstractPresentationModel {

	public static final String DATE_FORMAT_ELEMENT = "DateFormat";
	public static final String YEAR_ELEMENT = "Year";
	public static final String MONTH_ELEMENT = "Month";
	public static final String DAY_ELEMENT = "Day";
	public static final String FREEFORM_DATE_ELEMENT = "FreeformDate";
	public static final String ENABLED = "Enabled";
	public static final String FREEFORM_DATE_VALIDITY = "FreeformDateValidity";
	public static final String DATE_VALIDITY = "DateValidity";
	private static final String EMPTY_STRING = "";
	private static final int NO_VALUE_SET = -1;
	private final VCardBDayModel bdayModel;
	private String yearAsString = EMPTY_STRING;
	private String monthAsString = EMPTY_STRING;
	private String dayAsString = EMPTY_STRING;
	private InputValidity dateValidity = InputValidity.EMPTY;
	private InputValidity freeformDateValidity = InputValidity.EMPTY;
	private InputValidity validity = InputValidity.EMPTY;

	public VCardBDayPresentationModel(VCardBDayModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		this.bdayModel = model;
		bdayModel.addPropertyChangeListener(new ModelPropertyChangeListener());
	}

	public void setDateFormat(DateFormat format) {
		if (format == null) {
			throw new NullPointerException();
		}
		bdayModel.setFormat(format);
	}

	public DateFormat getDateFormat() {
		return bdayModel.getFormat();
	}

	public boolean isDateEnabled() {
		return DateFormat.DATE == bdayModel.getFormat();
	}

	public boolean isFreeformDateEnabled() {
		return DateFormat.FREEFORM == bdayModel.getFormat();

	}

	public void setYear(String year) {
		if (year == null) {
			throw new NullPointerException();
		}
		String oldYear = this.yearAsString;
		this.yearAsString = year;
		firePropertyChange(YEAR_ELEMENT, oldYear, year);
		setDateValidity(InputValidity.UNDEFINED);
	}

	public String getYear() {
		return yearAsString;
	}

	public void setMonth(String month) {
		if (month == null) {
			throw new NullPointerException();
		}
		String oldMonth = this.monthAsString;
		this.monthAsString = month;
		firePropertyChange(MONTH_ELEMENT, oldMonth, month);
		setDateValidity(InputValidity.UNDEFINED);
	}

	public String getMonth() {
		return monthAsString;
	}

	public void setDay(String day) {
		if (day == null) {
			throw new NullPointerException();
		}
		String oldDay = this.dayAsString;
		this.dayAsString = day;
		firePropertyChange(DAY_ELEMENT, oldDay, day);
		setDateValidity(InputValidity.UNDEFINED);
	}

	public String getDay() {
		return dayAsString;
	}

	public void setFreeformDate(String date) {
		if (date == null) {
			throw new NullPointerException();
		}
		if (date.isEmpty()) {
			bdayModel.clearFreeFormDate();
		} else {
			bdayModel.setFreeformDate(date);
		}
	}

	public String getFreeformDate() {
		if (bdayModel.isFreeFormDateSet()) {
			return bdayModel.getFreeFormDate();
		} else {
			return EMPTY_STRING;
		}
	}

	@Override
	public void clear() {
		//bdayModel.clear();
		setDateFormat(DateFormat.DATE);
		setYear(EMPTY_STRING);
		setMonth(EMPTY_STRING);
		setDay(EMPTY_STRING);
		updateYearMonthDay();
		setDateFormat(DateFormat.FREEFORM);
		setFreeformDate(EMPTY_STRING);
		setDateFormat(DateFormat.DATE);
	}

	@Override
	public void update() {
		if (DateFormat.DATE == bdayModel.getFormat()) {
			updateYearMonthDay();
		}
	}

	public void updateYearMonthDay() {
		InputValidity newValidity = InputValidity.UNDEFINED;
		try {
			int year;
			int month;
			int day;
			if (yearAsString.isEmpty()) {
				year = NO_VALUE_SET;
			} else {
				year = Integer.parseInt(yearAsString);
			}
			if (monthAsString.isEmpty()) {
				month = NO_VALUE_SET;
			} else {
				month = Integer.parseInt(monthAsString);
			}
			if (dayAsString.isEmpty()) {
				day = NO_VALUE_SET;
			} else {
				day = Integer.parseInt(dayAsString);
			}
			bdayModel.setDate(year, month, day);
			if (!bdayModel.isDateSet()) {
				newValidity = InputValidity.EMPTY;
			} else {
				newValidity = InputValidity.VALID;
			}
		} catch (NumberFormatException nfe) {
			newValidity = InputValidity.INVALID;
			throw new NumberFormatException(nfe.toString());
		} catch (IllegalArgumentException iae) {
			newValidity = InputValidity.INVALID;
		} finally {
			setDateValidity(newValidity);
		}
	}

	@Override
	public InputValidity getValidity() {
		return validity;
	}

	private void setValidity(InputValidity v) {
		assert v != null;

		InputValidity oldValidity = this.validity;
		this.validity = v;
		firePropertyChange(VALIDITY, oldValidity, v);
	}

	private void setDateValidity(InputValidity validity) {
		assert validity != null;
		dateValidity = validity;
		if (isDateEnabled()) {
			setValidity(dateValidity);
		}
	}

	private class ModelPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (VCardBDayModel.YEAR_ELEMENT.equals(propertyName)) {
				if (bdayModel.isYearSet()) {
					setYear(Integer.toString(bdayModel.getYear()));
				} else {
					setYear(EMPTY_STRING);
				}
			} else if (VCardBDayModel.MONTH_ELEMENT.equals(propertyName)) {
				if (bdayModel.isMonthSet()) {
					setMonth(Integer.toString(bdayModel.getMonth()));
				} else {
					setMonth(EMPTY_STRING);
				}
			} else if (VCardBDayModel.DAY_ELEMENT.equals(propertyName)) {
				if (bdayModel.isDaySet()) {
					setDay(Integer.toString(bdayModel.getDay()));
				} else {
					setDay(EMPTY_STRING);
				}
			} else if (VCardBDayModel.DATE_ELEMENT.equals(propertyName)) {
				firePropertyChange(FREEFORM_DATE_ELEMENT, evt.getOldValue(), evt.getNewValue());
				if (bdayModel.isFreeFormDateSet()) {
					freeformDateValidity = InputValidity.VALID;
				} else {
					freeformDateValidity = InputValidity.EMPTY;
				}
				setValidity(freeformDateValidity);
			} else if (VCardBDayModel.DATE_FORMAT_ELEMENT.equals(propertyName)) {
				firePropertyChange(DATE_FORMAT_ELEMENT, evt.getOldValue(), evt.getNewValue());
				DateFormat df = getDateFormat();
				if (DateFormat.DATE == df) {
					setValidity(dateValidity);
				} else if (DateFormat.FREEFORM == df) {
					setValidity(freeformDateValidity);
				}
			}
			firePropertyChange(CONTENT_CHANGED, evt.getOldValue(), evt.getNewValue());
		}
	}
}
