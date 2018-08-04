/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcardgenpanel.model;

import java.util.Calendar;
import java.util.Locale;
import javax.swing.JFormattedTextField;
import qrcodegen.modules.vcard.BDayProperty;
import qrcodegen.modules.vcard.Calscale;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.tools.ExtendedIndexOutOfBoundsException;

/**
 *
 * @author Stefan Ganzer
 */
public class DateModel extends AbstractModel {

	public static final String YEAR_ELEMENT = "Year";
	public static final String MONTH_ELEMENT = "Month";
	public static final String DAY_ELEMENT = "Day";
	public static final String CALSCALE_ELEMENT = "Calscale";
	private static final int MAX_DAY = 31;
	private static final int MAX_MONTH = 12;
	private static final int MAX_YEAR = 9999;
	private static final int VALUE_NOT_SET = -1;
	/** The minimum year */
	private static final int MIN_YEAR = 0;
	private static final int MIN_MONTH = 1;
	private static final int MIN_DAY = 1;
	public static final String VALIDITY_PROPERTY = "Validity";
	/** The year, from 0000-9999. -1 means: no year set. */
	private int year = VALUE_NOT_SET;
	/** The month, from 01-12. -1 means: no month set. */
	private int month = VALUE_NOT_SET;
	/** The day, from 01-28/29/30/31, depending on month and leap year. -1
	 * means: no year set. */
	private int day = VALUE_NOT_SET;
	/** From 00-23. -1 means: no hour set. */
	/** The calscale, if set. Null means: use the default calscale, i.e.
	 * Calscale.GREGORIAN. */
	private Calscale calscale;

	/**
	 * Represents a date as used in VCard v4.
	 *
	 * The setter methods enforce the invariant, i.e. the date has to be a valid
	 * date according to VCard v4 definition.
	 *
	 * Trying to get a field value (year, month, day) while the field isn't set
	 * will throw an IllegalStateException.
	 *
	 * Invoking an isXxxSet method will never throw an exception.
	 */
	public DateModel() {
	}

	/**
	 * Sets the year.
	 *
	 * @param value a year, from {@value #MIN_YEAR} to {@value #MAX_YEAR}
	 *
	 * @throws IndexOutOfBoundsException if the value exceeds the range
	 */
	public void setYear(int value) {
		if (value < MIN_YEAR || value > MAX_YEAR) {
			throw new ExtendedIndexOutOfBoundsException(MIN_YEAR, MAX_YEAR, value);
		}
		setDateValue(value, month, day);
		assert isYearSet() : year;
	}

	/**
	 * Removes the year value.
	 */
	public void clearYear() {
		setDateValue(VALUE_NOT_SET, month, day);
		assert !isYearSet() : year;
	}

	/**
	 * Returns true if the year is set, false otherwise.
	 *
	 * @return true if the year is set, false otherwise
	 */
	public boolean isYearSet() {
		assert year >= VALUE_NOT_SET && year <= MAX_YEAR;
		return year != VALUE_NOT_SET;
	}

	/**
	 * Returns the year, if it is set.
	 *
	 * @return the year if it is set - a value from {@value #MIN_YEAR} to
	 * {@value #MAX_YEAR}
	 *
	 * @throws IllegalStateException if no year is set, or if the date is not
	 * valid
	 * @see #isYearSet()
	 */
	public int getYear() {
		if (!isYearSet()) {
			throw new IllegalStateException();
		}
		return year;
	}

	/**
	 * Sets the month.
	 *
	 * @param value a month, from {@value #MIN_MONTH} to {@value #MAX_MONTH}
	 *
	 * @throws ExtendedIndexOutOfBoundsException if the value exceeds the bounds
	 */
	public void setMonth(int value) {
		if (value < MIN_MONTH || value > MAX_MONTH) {
			throw new ExtendedIndexOutOfBoundsException(MIN_MONTH, MAX_MONTH, value);
		}
		setDateValue(year, value, day);
		assert isMonthSet() : month;
	}

	/**
	 * Removes the year value.
	 */
	public void clearMonth() {
		setDateValue(year, VALUE_NOT_SET, day);
		assert !isMonthSet() : month;
	}

	/**
	 * Returns true if the month is set, false otherwise.
	 *
	 * @return true if the month is set, false otherwise
	 */
	public boolean isMonthSet() {
		assert month >= VALUE_NOT_SET && month <= MAX_MONTH;
		return month != VALUE_NOT_SET;
	}

	/**
	 * Returns the month, if it is set.
	 *
	 * @return the month if it is set - a value from {@value #MIN_MONTH} to
	 * {@value #MAX_MONTH}
	 *
	 * @throws IllegalStateException if no month is set
	 * @see #isMonthSet()
	 */
	public int getMonth() {
		if (!isMonthSet()) {
			throw new IllegalStateException();
		}
		return month;
	}

	/**
	 * Sets the day.
	 *
	 * @param value a day from {@value #MIN_DAY} to {@value #MAX_DAY}
	 *
	 * @throws ExtendedIndexOutOfBoundsException if the value exceeds the bounds
	 */
	public void setDay(int value) {
		if (value < MIN_DAY || value > MAX_DAY) {
			throw new ExtendedIndexOutOfBoundsException(MIN_DAY, MAX_DAY, value);
		}
		setDateValue(year, month, value);
		assert isDaySet() : day;
	}

	/**
	 * Removes the day.
	 */
	public void clearDay() {
		setDateValue(year, month, VALUE_NOT_SET);
		assert !isDaySet() : day;
	}

	/**
	 * Returns true if the day is set, false otherwise.
	 *
	 * @return true if the day is set, false otherwise
	 */
	public boolean isDaySet() {
		assert day >= VALUE_NOT_SET && day <= MAX_DAY;
		return day != VALUE_NOT_SET;
	}

	/**
	 * Returns the day if it is set.
	 *
	 * @return the day if it is set - a value from {@value #MIN_DAY} to
	 * {@value #MAX_DAY}
	 *
	 * @throws IllegalStateException if no day is set
	 */
	public int getDay() {
		if (!isDaySet()) {
			throw new IllegalStateException();
		}
		return day;
	}

	/**
	 * Sets the date in an atomic way.
	 *
	 * Use {@code -1} for each parameter that is not to be set. Setting all
	 * parameters to {@code -1} is effectively the same as invoking {@link #clearDate()
	 * }.
	 *
	 * @param year
	 * @param month
	 * @param day
	 *
	 * @throws IllegalArgumentException if the given year, month and day don't
	 * constitute a valid date
	 */
	public void setDate(int year, int month, int day) {
		if (!(year == VALUE_NOT_SET || (year >= MIN_YEAR && year <= MAX_YEAR))) {
			throw new ExtendedIndexOutOfBoundsException(MIN_YEAR, MAX_YEAR, year);
		}
		if (!(month == VALUE_NOT_SET || (month >= MIN_MONTH && month <= MAX_MONTH))) {
			throw new ExtendedIndexOutOfBoundsException(MIN_MONTH, MAX_MONTH, month);

		}
		if (!(day == VALUE_NOT_SET || (day >= MIN_DAY && day <= MAX_DAY))) {
			throw new ExtendedIndexOutOfBoundsException(MIN_DAY, MAX_DAY, day);
		}
		setDateValue(year, month, day);
	}

	private void setDateValue(int year, int month, int day) {
		assert year >= VALUE_NOT_SET && year <= MAX_YEAR;
		assert month >= VALUE_NOT_SET && month <= MAX_MONTH;
		assert day >= VALUE_NOT_SET && day <= MAX_DAY;

		InputValidity newValidity = validateDate(year, month, day);
		if (newValidity == InputValidity.INVALID) {
			throw new IllegalArgumentException();
		}

		int oldYear = this.year;
		int oldMonth = this.month;
		int oldDay = this.day;

		boolean changedYear = oldYear != year;
		boolean changedMonth = oldMonth != month;
		boolean changedDay = oldDay != day;

		if (changedYear) {
			this.year = year;
		}
		if (changedMonth) {
			this.month = month;
		}
		if (changedDay) {
			this.day = day;
		}

		if (changedYear) {
			firePropertyChange(YEAR_ELEMENT, oldYear, year);
		}
		if (changedMonth) {
			firePropertyChange(MONTH_ELEMENT, oldMonth, month);
		}
		if (changedDay) {
			firePropertyChange(DAY_ELEMENT, oldDay, day);
		}
	}

	/**
	 * Removes the year, month and day value, and removes the calscale
	 * parameter.
	 */
	public void clear() {
		clearDate();
		clearCalscale();
		assert !hasCalscale();
	}

	/**
	 * Removes the year, month and day value, but doesn't remove the calscale
	 * parameter.
	 */
	public void clearDate() {
		setDateValue(VALUE_NOT_SET, VALUE_NOT_SET, VALUE_NOT_SET);
		assert !isYearSet() && !isMonthSet() && !isDaySet() : year + " " + month + " " + day;
	}

	/**
	 * Returns true if the calscale parameter is set, false otherwise.
	 *
	 * @return true if the calscale parameter is set, false otherwise
	 */
	public boolean hasCalscale() {
		return calscale != null;
	}

	/**
	 * Returns the calscale parameter, if it is set.
	 *
	 * @return the calscale parameter, if it is set. Returns never null.
	 *
	 * @throws IllegalStateException if calscale is not set
	 */
	public Calscale getCalscale() {
		if (!hasCalscale()) {
			throw new IllegalStateException();
		}
		return calscale;
	}

	/**
	 * Sets the calscale parameter.
	 *
	 * @param value a calscale value
	 *
	 * @throws NullPointerException if value is null
	 */
	public void setCalscale(Calscale value) {
		if (value == null) {
			throw new NullPointerException();
		}
		Calscale oldValue = this.calscale;
		this.calscale = value;
		firePropertyChange(CALSCALE_ELEMENT, oldValue, value);
	}

	/**
	 * Removes the calscale parameter if it is set.
	 */
	public void clearCalscale() {
		Calscale oldValue = this.calscale;
		this.calscale = null;
		if (oldValue != null) {
			firePropertyChange(CALSCALE_ELEMENT, oldValue, null);
		}
		assert !hasCalscale() : calscale;
	}

	/**
	 * Returns true if no one of year, month and day is set, regardless of
	 * whether the calscale parameter is set.
	 *
	 * @return true if no one of year, month and day is set, regardless of
	 * whether the calscale parameter is set
	 */
	public boolean isEmpty() {
		return isEmptyDate();
	}

	/**
	 * Returns true if any one of year, month or day is set, regardless of
	 * whether the calscale parameter is set.
	 *
	 * @return true if any one of year, month or day is set, regardless of
	 * whether the calscale parameter is set
	 */
	public boolean isDateSet() {
		boolean result = isYearSet() || isMonthSet() || isDaySet();
		assert result == !(!isYearSet() && !isMonthSet() && !isDaySet()) : year + " " + month + " " + day;
		return result;
	}

	private boolean isEmptyDate() {
		boolean result = !isYearSet() && !isMonthSet() && !isDaySet();
		assert result == !(isYearSet() || isMonthSet() || isDaySet());
		return result;
	}

	private static InputValidity validateDate(int year, int month, int day) {
		InputValidity newValidity;
		if (isEmptyDate(year, month, day)) {
			newValidity = InputValidity.EMPTY;
		} else if (isSet(year) && isSet(month) && isSet(day)) {
			newValidity = isValidFullDate(year, month, day) ? InputValidity.VALID : InputValidity.INVALID;
		} else if (!isSet(year) && isSet(month) && isSet(day)) {
			newValidity = isValidMonthDay(month, day) ? InputValidity.VALID : InputValidity.INVALID;
		} else if (isSet(year) && !isSet(month) && isSet(day)) {
			newValidity = InputValidity.INVALID;
		} else {
			newValidity = InputValidity.VALID;
		}
		return newValidity;
	}

	private static boolean isEmptyDate(int year, int month, int day) {
		assert year >= VALUE_NOT_SET && year <= MAX_YEAR : year;
		assert month >= VALUE_NOT_SET && month <= MAX_MONTH : month;
		assert day >= VALUE_NOT_SET && day <= MAX_DAY : day;

		return year == VALUE_NOT_SET && month == VALUE_NOT_SET && day == VALUE_NOT_SET;
	}

	private static boolean isSet(int value) {
		return value != VALUE_NOT_SET;
	}

	private static boolean isValidFullDate(int year, int month, int day) {
		assert year >= MIN_YEAR && year <= MAX_YEAR : year;
		assert month >= MIN_MONTH && month <= MAX_MONTH : month;
		assert day >= MIN_DAY && day <= MAX_DAY : day;

		boolean isValid;
		Calendar cal = Calendar.getInstance(Locale.ENGLISH);
		cal.setLenient(false);
		try {
			cal.clear();
			cal.set(year, month - 1, day);
			cal.get(Calendar.DAY_OF_YEAR);
			isValid = true;
		} catch (IllegalArgumentException swallowed) {
			isValid = false;
		}
		return isValid;
	}

	private static boolean isValidMonthDay(int month, int day) {
		assert month >= MIN_MONTH && month <= MAX_MONTH : month;
		assert day >= MIN_DAY && day <= MAX_DAY : day;

		boolean valid = true;
		switch (month) {
			case 4:
			// fall-through
			case 6:
			// fall-through
			case 9:
			// fall-through
			case 11:
				if (day > 30) {
					valid = false;
				}
				break;
			case 2:
				if (day > 29) {
					valid = false;
				}
				break;
			default:
				assert day < 32 : "month: " + month + ", day: " + day;
		}
		return valid;
	}

	public BDayProperty.Builder getBDayBuilder() {
		BDayProperty.Builder builder;
		if (isEmpty()) {
			builder = null;
		} else {
			builder = new BDayProperty.Builder();
			if (isYearSet()) {
				builder.year(getYear());
			}
			if (isMonthSet()) {
				builder.month(getMonth());
			}
			if (isDaySet()) {
				builder.day(getDay());
			}
		}
		return builder;
	}
}
