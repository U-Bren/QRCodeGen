/*
 * Copyright (C) 2012, 2013 Stefan Ganzer
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
package qrcodegen.modules.vcardgenpanel.model;

import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class FreeformDateModel extends AbstractModel {

	public static final String DATE_ELEMENT = "Date";
	private String date;

	public FreeformDateModel() {
	}

	/**
	 * Sets the date.
	 *
	 * @param value a free form date - mustn't be null.
	 *
	 * @throws NullPointerException if date is null
	 */
	public void setDate(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setDateValue(value);
		assert value.equals(date) : value + " " + date;
		assert isDateSet();
	}

	/**
	 * Removes the date value.
	 *
	 * {@link isDateSet()} will return false after invocation of this method.
	 *
	 * @see isDateSet()
	 */
	public void clear() {
		setDateValue(null);
		assert !isDateSet() : date;
	}

	/**
	 * Returns the date value.
	 *
	 * @return the date value. Never returns null.
	 *
	 * @throws IllegalStateException if no date is set
	 * @see #isDateSet()
	 */
	public String getDate() {
		if (!isDateSet()) {
			throw new IllegalStateException();
		}
		return date;
	}

	/**
	 * Returns true if a date is set, even an empty one. False otherwise.
	 *
	 * @return true if a date is set, even an empty one. False otherwise
	 */
	public boolean isDateSet() {
		return date != null;
	}

	private void setDateValue(String value) {
		String oldDate = this.date;
		boolean dateChanged = !StaticTools.bothNullOrEqual(oldDate, value);

		if (dateChanged) {
			this.date = value;
			firePropertyChange(DATE_ELEMENT, oldDate, value);
		}
	}
}
