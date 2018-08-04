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

/**
 * This BDayProperty can store either a date or a text value, but not both at
 * once.
 *
 * @author Stefan Ganzer
 */
public final class BDayProperty extends PropertyEntry {

	private final Date birthdayDate;
	private final VCardText birthdayText;

	private BDayProperty(Builder builder) {
		super(builder);

		int year = builder.year;
		int month = builder.month;
		int day = builder.day;
		String text = builder.text;

		if (year == -1 && month == -1 && day == -1 && text == null) {
			throw new IllegalArgumentException();
		}
		if ((year != -1 || month != -1 || day != -1) && text != null) {
			throw new IllegalArgumentException();
		}
		if (text == null) {
			birthdayDate = new Date(year, month, day);
			birthdayText = null;
		} else {
			birthdayText = new VCardText(builder.text);
			birthdayDate = null;
		}
	}

	public static final class Builder extends PropertyEntry.Builder {

		private String altID;
		private String text;
		private int year = -1;
		private int month = -1;
		private int day = -1;

		public Builder() {
			super(Property.BDAY);
		}

		/**
		 * Sets a birthday as text, like 'around 1800'.
		 *
		 * @param text a string describing the birtday, like 'around 1800'
		 *
		 * @return this Builder
		 */
		public Builder text(String text) {
			this.text = text;
			return this;
		}

		public Builder altID(String value) {
			this.altID = value;
			return this;
		}

		@Override
		public ValueType getValueParameter() {
			if (text != null && (year == -1 && month == -1 && day == -1)) {
				return ValueType.TEXT;
			} else if (text == null && (year != -1 || month != -1 || day != -1)) {
				return ValueType.DATE_AND_OR_TIME;
			} else {
				throw new IllegalStateException();
			}
		}

		@Override
		public String getAltID() {
			return altID;
		}

		@Override
		public BDayProperty build() {
			return new BDayProperty(this);
		}

		/**
		 * Sets the year.
		 *
		 * @param year a value between 0...9999
		 *
		 * @return this Builder
		 */
		public Builder year(int year) {
			this.year = year;
			return this;
		}

		/**
		 * Sets the month.
		 *
		 * @param month a value between 1...12
		 *
		 * @return this Builder
		 */
		public Builder month(int month) {
			this.month = month;
			return this;
		}

		/**
		 * Sets the day.
		 *
		 * @param day a value between 1...28/29/30/31
		 *
		 * @return this Builder
		 */
		public Builder day(int day) {
			this.day = day;
			return this;
		}
	}

	@Override
	VCardValue getValue() {
		if (birthdayDate == null) {
			assert birthdayText != null;
			return birthdayText;
		} else {
			return birthdayDate;
		}
	}

	@Override
	String getValueAsString() {
		if (birthdayDate == null) {
			assert birthdayText != null;
			return birthdayText.getValueAsString();
		} else {
			return birthdayDate.getValueAsString();
		}
	}

	/**
	 * Returns true if this property has a date value (combinations of year,
	 * month, day, ...), false if it has a text value.
	 *
	 * @return
	 */
	public boolean hasDateValue() {
		boolean result = birthdayDate != null;
		assert result == (birthdayText == null);
		return result;
	}

	/**
	 * Returns true if this property has a text value, false if it has a date
	 * value (combinations of year, month, day,...).
	 *
	 * @return
	 */
	public boolean hasTextValue() {
		boolean result = birthdayText != null;
		assert result == (birthdayDate == null);
		return result;
	}

	/**
	 * Returns true if the year is set, false otherwise.
	 *
	 * @return true if the year is set, false otherwise, regardless of the cause
	 * (date is a text value, for instance).
	 */
	public boolean hasYear() {
		return birthdayDate != null && birthdayDate.hasYear();
	}

	/**
	 * Returns the year.
	 *
	 * @return the year
	 *
	 * @throws IllegalStateException if the year is not set
	 */
	public int getYear() {
		if (!hasYear()) {
			throw new IllegalStateException();
		}
		return birthdayDate.getYear();
	}

	/**
	 * Returns true if the year is set, false otherwise.
	 *
	 * @return true if the year is set, false otherwise, regardless of the cause
	 * (date is a text value, for instance).
	 */
	public boolean hasMonth() {
		return birthdayDate != null && birthdayDate.hasMonth();
	}

	/**
	 * Returns the month.
	 *
	 * @return the month
	 *
	 * @throws IllegalStateException if the month is not set
	 */
	public int getMonth() {
		if (!hasMonth()) {
			throw new IllegalStateException();
		}
		return birthdayDate.getMonth();
	}

	/**
	 * Returns true if the year is set, false otherwise.
	 *
	 * @return true if the year is set, false otherwise, regardless of the cause
	 * (date is a text value, for instance).
	 */
	public boolean hasDay() {
		return birthdayDate != null && birthdayDate.hasDay();
	}

	/**
	 * Returns the day.
	 *
	 * @return the day
	 *
	 * @throws IllegalStateException if the day is not set
	 */
	public int getDay() {
		if (!hasDay()) {
			throw new IllegalStateException();
		}
		return birthdayDate.getDay();
	}

	/**
	 * Returns the date text.
	 *
	 * @return the original, i.e. unescaped date text
	 *
	 * @throws IllegalStateException if the text is not set
	 */
	public String getText() {
		if (!hasTextValue()) {
			throw new IllegalStateException();
		}
		return birthdayText.getOriginalValue();
	}
}
