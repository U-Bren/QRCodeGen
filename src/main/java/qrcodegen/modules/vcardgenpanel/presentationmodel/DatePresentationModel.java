/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcardgenpanel.presentationmodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.modules.vcardgenpanel.model.AbstractModel;
import qrcodegen.modules.vcardgenpanel.model.DateModel;

/**
 *
 * @author Stefan Ganzer
 */
public class DatePresentationModel extends AbstractModel {

	public static final String YEAR_ELEMENT = "Year";
	public static final String MONTH_ELEMENT = "Month";
	public static final String DAY_ELEMENT = "Day";
	public static final String CALSCALE_ELEMENT = "Calscale";
	public static final String ENABLED = "Enabled";
	public static final String VALIDITY = "Validity";
	private static final int NO_VALUE_SET = -1;
	private static final String EMPTY_STRING = "";
	private final DateModel model;
	private String yearAsString;
	private String monthAsString;
	private String dayAsString;
	private boolean isEnabled;
	private InputValidity validity;

	public DatePresentationModel(DateModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		this.model = model;
		model.addPropertyChangeListener(new ModelPropertyChangeListener());
	}

	public void setYear(String year) {
		if (year == null) {
			throw new NullPointerException();
		}
		String oldYear = this.yearAsString;
		this.yearAsString = year;
		firePropertyChange(YEAR_ELEMENT, oldYear, year);
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
	}

	public String getDay() {
		return dayAsString;
	}

	public void clear() {
		setYear(EMPTY_STRING);
		setMonth(EMPTY_STRING);
		setDay(EMPTY_STRING);
		updateModel();
	}

	public void setEnabled(boolean enabled) {
		boolean oldEnabled = this.isEnabled;
		this.isEnabled = enabled;
		if (!enabled && validity == InputValidity.INVALID) {
			setValidity(InputValidity.UNDEFINED);
		}
		firePropertyChange(ENABLED, oldEnabled, enabled);
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void updateModel() {
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
			model.setDate(year, month, day);
			if (model.isEmpty()) {
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
			setValidity(newValidity);
		}
	}

	private class ModelPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();
			if (DateModel.YEAR_ELEMENT.equals(propertyName)) {
				if (model.isYearSet()) {
					setYear(Integer.toString(model.getYear()));
				} else {
					setYear(EMPTY_STRING);
				}
			} else if (DateModel.MONTH_ELEMENT.equals(propertyName)) {
				if (model.isMonthSet()) {
					setMonth(Integer.toString(model.getMonth()));
				} else {
					setMonth(EMPTY_STRING);
				}
			} else if (DateModel.DAY_ELEMENT.equals(propertyName)) {
				if (model.isDaySet()) {
					setDay(Integer.toString(model.getDay()));
				} else {
					setDay(EMPTY_STRING);
				}
			}
		}
	}

	private void setValidity(InputValidity v) {
		assert v != null;

		InputValidity oldValidity = this.validity;
		this.validity = v;
		firePropertyChange(VALIDITY, oldValidity, v);
	}
}
