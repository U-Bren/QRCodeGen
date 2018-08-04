/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcardgenpanel.model;

import java.util.Calendar;
import java.util.Locale;
import qrcodegen.modules.vcard.Calscale;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.tools.ExtendedIndexOutOfBoundsException;

/**
 *
 * @author Stefan Ganzer
 */
public class TimeModel extends AbstractModel {

	public static final String HOUR_ELEMENT = "Hour";
	public static final String MINUTE_ELEMENT = "Minute";
	public static final String SECOND_ELEMENT = "Second";
	public static final String VALIDATION_PROPERTY = "Validation";
	private static final int MIN_HOUR = 0;
	private static final int MAX_HOUR = 23;
	private static final int MIN_MINUTE = 0;
	private static final int MAX_MINUTE = 59;
	private static final int MIN_SECOND = 0;
	private static final int MAX_SECOND = 59;
	private static final int NO_VALUE_SET = -1;
	/** From 00-23. -1 means: no hour set. */
	private int hour = NO_VALUE_SET;
	/** From 00-59. -1 means: no hour set. */
	private int minute = NO_VALUE_SET;
	/** From 00-59. -1 means: no second set. */
	private int second = NO_VALUE_SET;
	private boolean isValidDate = false;
	private InputValidity validity = InputValidity.EMPTY;

	public TimeModel() {
	}

	/**
	 * Sets the hour.
	 *
	 * @param value the hour, from {@value #MIN_HOUR} to {@value #MAX_HOUR}
	 *
	 * @throws ExtendedIndexOutOfBoundsException if the value exceeds the bounds
	 */
	public void setHour(int value) {
		if (value < MIN_HOUR || value > MAX_HOUR) {
			throw new ExtendedIndexOutOfBoundsException(MIN_HOUR, MAX_HOUR, value);
		}
		setTimeValue(value, minute, second);
		assert getHour() == value : getHour() + " " + value;
	}

	/**
	 * Removes the hour value.
	 */
	public void clearHour() {
		setTimeValue(NO_VALUE_SET, minute, second);
		assert !isHourSet() : hour;
	}

	/**
	 * Returns true if the hour is set, false otherwise.
	 *
	 * @return true if the hour is set, false otherwise
	 */
	public boolean isHourSet() {
		assert hour >= NO_VALUE_SET && hour <= MAX_HOUR;
		return hour != NO_VALUE_SET;
	}

	/**
	 * Returns the hour.
	 *
	 * @return the hour - a value from {@value #MIN_HOUR} to {@value #MAX_HOUR}
	 *
	 * @throws IllegalStateException if the hour is not set
	 */
	public int getHour() {
		if (!isHourSet()) {
			throw new IllegalStateException();
		}
		validate();
		if(validity == InputValidity.INVALID){
			throw new IllegalStateException();
		}
		return hour;
	}

	/**
	 * Sets the minute.
	 *
	 * @param value the minute, from {@value #MIN_MINUTE} to
	 * {@value #MAX_MINUTE}
	 *
	 * @throws ExtendedIndexOutOfBoundsException if the value exceeds the bounds
	 */
	public void setMinute(int value) {
		if (value < MIN_MINUTE || value > MAX_MINUTE) {
			throw new ExtendedIndexOutOfBoundsException(MIN_MINUTE, MAX_MINUTE, value);
		}
		setTimeValue(hour, value, second);
		assert getMinute() == value : getMinute() + " " + value;
	}

	/**
	 * Removes the minute value.
	 */
	public void clearMinute() {
		setTimeValue(hour, NO_VALUE_SET, second);
	}

	/**
	 * Returns true if the minute is set, false otherwise.
	 *
	 * @return true if the minute is set, false otherwise
	 */
	public boolean isMinuteSet() {
		assert minute >= NO_VALUE_SET && minute <= MAX_MINUTE;
		return minute != NO_VALUE_SET;
	}

	/**
	 * Returns the minute value.
	 *
	 * @return the minute - a value from {@value #MIN_MINUTE} to
	 * {@value #MAX_MINUTE}
	 *
	 * @throws IllegalStateException if the minute is not set
	 */
	public int getMinute() {
		if (!isMinuteSet()) {
			throw new IllegalStateException();
		}
		validate();
		if(validity == InputValidity.INVALID){
			throw new IllegalStateException();
		}
		return minute;
	}

	/**
	 * Sets the second.
	 *
	 * @param value the second, from {@value #MIN_SECOND} to
	 * {@value #MAX_SECOND}
	 *
	 * @throws ExtendedIndexOutOfBoundsException if the value exceeds the bounds
	 */
	public void setSecond(int value) {
		if (value < MIN_SECOND || value > MAX_SECOND) {
			throw new ExtendedIndexOutOfBoundsException(MIN_SECOND, MAX_SECOND, value);
		}
		setTimeValue(hour, minute, value);
		assert getSecond() == value : getSecond() + " " + value;
	}

	/**
	 * Removes the second value.
	 */
	public void clearSecond() {
		setTimeValue(hour, minute, NO_VALUE_SET);
		assert !isSecondSet() : second;
	}

	/**
	 * Returns true if the second is set, false otherwise.
	 *
	 * @return true if the second is set, false otherwise
	 */
	public boolean isSecondSet() {
		assert second >= NO_VALUE_SET && second <= MAX_SECOND : second;
		return second != NO_VALUE_SET;
	}

	/**
	 * Returns the second value.
	 *
	 * @return the second, a value from {@value #MIN_SECOND} to
	 * {@value #MAX_SECOND}
	 *
	 * @throws IllegalStateException if the second is not set
	 */
	public int getSecond() {
		if (!isSecondSet()) {
			throw new IllegalStateException();
		}
		validate();
		if(validity == InputValidity.INVALID){
			throw new IllegalStateException();
		}
		return second;
	}

	private void setTimeValue(int hour, int minute, int second) {
		assert hour >= NO_VALUE_SET && hour <= MAX_HOUR : hour;
		assert minute >= NO_VALUE_SET && minute <= MAX_MINUTE : minute;
		assert second >= NO_VALUE_SET && second <= MAX_SECOND : second;

		int oldHour = this.hour;
		int oldMinute = this.minute;
		int oldSecond = this.second;

		boolean hourChanged = oldHour != hour;
		boolean minuteChanged = oldMinute != minute;
		boolean secondChanged = oldSecond != second;

		if (hourChanged) {
			this.hour = hour;
		}
		if (minuteChanged) {
			this.minute = minute;
		}
		if (secondChanged) {
			this.second = second;
		}

		if (hourChanged || minuteChanged || secondChanged) {
			setValidity(InputValidity.UNDEFINED);
		}

		if (hourChanged) {
			firePropertyChange(HOUR_ELEMENT, oldHour, hour);
		}
		if (minuteChanged) {
			firePropertyChange(MINUTE_ELEMENT, oldMinute, minute);
		}
		if (secondChanged) {
			firePropertyChange(SECOND_ELEMENT, oldSecond, second);
		}

		assert this.hour == hour;
		assert this.minute == minute;
		assert this.second == second;
	}

	/**
	 * Removes all of hour, minute and second.
	 */
	public void clear() {
		setTimeValue(NO_VALUE_SET, NO_VALUE_SET, NO_VALUE_SET);
		assert !isHourSet() && !isMinuteSet() && !isSecondSet() : hour + " " + minute + " " + second;
	}

	/**
	 * Returns true if no one of hour, minute or second is set, false otherwise.
	 *
	 * @return true if no one of hour, minute or second is set, false otherwise
	 */
	public boolean isEmpty() {
		return !isHourSet()
				&& !isMinuteSet()
				&& !isSecondSet();
	}

	/**
	 * Validates this time.
	 *
	 * @see #validity
	 */
	public void validate() {
		InputValidity newValidity;
		boolean isValidTimeOrEmpty = isValidTimeOrEmpty();
		boolean isEmpty = isEmpty();

		if (isEmpty) {
			newValidity = InputValidity.EMPTY;
		} else {
			newValidity = isValidTimeOrEmpty ? InputValidity.VALID : InputValidity.INVALID;
		}

		setValidation(!isEmpty && isValidTimeOrEmpty);
		setValidity(newValidity);
	}

	private boolean isValidTimeOrEmpty() {
		return !(isHourSet() && !isMinuteSet() && isSecondSet());
	}

	private void setValidation(boolean value) {
		boolean oldValue = this.isValidDate;
		this.isValidDate = value;
		firePropertyChange(VALIDATION_PROPERTY, oldValue, value);
	}

	private void setValidity(InputValidity value) {
		assert value != null;
		InputValidity oldValue = this.validity;
		this.validity = value;
		firePropertyChange("Validity", oldValue, value);
	}

	public boolean isValid() {
		return isValidDate;
	}

	/**
	 * Returns the result of the invocation of {@link #validate() }.
	 *
	 * @return the result of the invocation of {@link #validate() }. Never
	 * returns null.
	 *
	 * @see #validate()
	 */
	public InputValidity validity() {
		return validity;
	}
}
