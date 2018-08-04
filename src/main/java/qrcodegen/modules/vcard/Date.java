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
package qrcodegen.modules.vcard;

import java.util.Calendar;
import java.util.Locale;
import qrcodegen.tools.ExtendedIndexOutOfBoundsException;

/**
 *
 * @author Stefan Ganzer
 */
final class Date implements VCardValue {

	private final int year;
	private final int month;
	private final int day;

	public static final class Builder {

		private int year;
		private int month;
		private int day;

		Builder year(int year) {
			this.year = year;
			return this;
		}

		Builder month(int month) {
			this.month = month;
			return this;
		}

		Builder day(int day) {
			this.day = day;
			return this;
		}

		Date build() {
			return new Date(this);
		}
	}

	private Date(Builder builder) {
		this.year = builder.year;
		this.month = builder.month;
		this.day = builder.day;
	}

	/**
	 * Create a new Date instance.
	 *
	 * The following combinations are allowed:
	 * <pre><code>
	 * year [month [day]]
	 * month [day]
	 * day
	 * </code></pre>That is the only combination not allowed is
	 * <code>year day</code>.
	 *
	 * @param year must be 0 &lt;= year &lt;= 9999, or -1 if the year isn't set
	 * @param month must be 1 &lt;= month &lt;= 12, or -1 if the month isn't set
	 * @param day must be 0 &lt;= day &lt;= 28/29/30/31 dependending on month
	 * and or year, or -1 if the day isn't set
	 */
	Date(int year, int month, int day) {
		if (year < -1 || year > 9999) {
			throw new ExtendedIndexOutOfBoundsException(0, 9999, year);
		}
		if (!(month == -1 || (month > 0 && month < 13))) {
			throw new ExtendedIndexOutOfBoundsException(1, 12, month);
		}
		if (!(day == -1 || (day > 0 && day < 32))) {
			throw new ExtendedIndexOutOfBoundsException(1, 31, day);
		}
		if (year != -1 && month == -1 && day != -1) {
			throw new IllegalArgumentException("'year day' with undefined month is not allowed");
		}
		if (year == -1 && month == -1 && day == -1) {
			throw new IllegalArgumentException("undefined date is not allowed");
		}
		if (year != -1 && month != -1 && day != -1) {
			Calendar cal = Calendar.getInstance(Locale.ENGLISH);
			cal.setLenient(false);
			try {
				cal.clear();
				cal.set(year, month - 1, day);
				assert cal.get(Calendar.YEAR) == year : Calendar.YEAR;
				assert cal.get(Calendar.MONTH) == month - 1 : Calendar.MONTH;
				assert cal.get(Calendar.DAY_OF_MONTH) == day : Calendar.DAY_OF_MONTH;
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException();
			}
		} else if (year == -1 && month != -1 && day != -1) {
			switch (month) {
				case 4:
				// fall-through
				case 6:
				// fall-through
				case 9:
				// fall-through
				case 11:
					if (day > 30) {
						throw new ExtendedIndexOutOfBoundsException(1, 30, day);
					}
					break;
				case 2:
					if (day > 29) {
						throw new ExtendedIndexOutOfBoundsException(1, 29, day);
					}
					break;
				default:
					assert day < 32 : "month: " + month + ", day: " + day;
			}
		}
		this.year = year;
		this.month = month;
		this.day = day;
	}

	/**
	 * Returns this date in the
	 *
	 * @return
	 */
	@Override
	public String getValueAsString() {
		assert !(year != -1 && month == -1 && day != -1) : year + " " + month + " " + day;

		StringBuilder sb = new StringBuilder();
		if (year == -1) {
			sb.append("--");
		} else {
			sb.append(String.format("%1$04d", year));
		}
		if (month == -1) {
			if (year == -1) {
				sb.append("--");
			} // else append nothing
		} else {
			sb.append(String.format("%1$02d", month));
		}
		if (day != -1) {
			sb.append(String.format("%1$02d", day));
		}

		String result = sb.toString();
		assert !result.equals("------");
		return result;
	}

	@Override
	public int elements() {
		return 1;
	}

	/**
	 * Returns true if the year of this Date is set, false otherwise.
	 *
	 * @return true if the day of this Date is set, false otherwise
	 */
	boolean hasYear() {
		return year != -1;
	}

	/**
	 * Returns the year.
	 *
	 * The year is a number from {@code 0...9999} or {@code -1} if the year is
	 * not defined.
	 *
	 * @return the year
	 */
	int getYear() {
		return year;
	}

	/**
	 * Returns true if the month of this Date is set, false otherwise.
	 *
	 * @return true if the day of this Date is set, false otherwise
	 */
	boolean hasMonth() {
		return month != -1;
	}

	/**
	 * Returns the month.
	 *
	 * The month is a number from {@code 1...12} or {@code -1} if the month is
	 * not defined.
	 *
	 * @return the month
	 */
	int getMonth() {
		return month;
	}

	/**
	 * Returns true if the day of this Date is set, false otherwise.
	 *
	 * @return true if the day of this Date is set, false otherwise
	 */
	boolean hasDay() {
		return day != -1;
	}

	/**
	 * Returns the day.
	 *
	 * The day is a number from {@code 1...28/29/30/31} or {@code -1} if the day
	 * is not defined.
	 *
	 * @return the day
	 */
	int getDay() {
		return day;
	}
}
