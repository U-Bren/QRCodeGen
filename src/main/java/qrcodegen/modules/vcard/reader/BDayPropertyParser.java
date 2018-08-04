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
package qrcodegen.modules.vcard.reader;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import qrcodegen.modules.vcard.BDayProperty;
import qrcodegen.modules.vcard.Calscale;
import qrcodegen.modules.vcard.Property;
import qrcodegen.modules.vcard.VCardTools;
import qrcodegen.modules.vcard.ValueType;

/**
 *
 * @author Stefan Ganzer
 */
final class BDayPropertyParser extends PropertyParser {

	private static final String YEAR = ("\\d\\d\\d\\d");
	private static final String MONTH = ("1[0-2]|0[1-9]");
	private static final String DAY = ("3[01]|[12][0-9]|0[1-9]");
	private static final Pattern DATE_1_PATTERN = Pattern.compile("^(" + YEAR + ")((" + MONTH + ")(" + DAY + "))?$");
	private static final Pattern DATE_2_PATTERN = Pattern.compile("^(" + YEAR + ")-(" + MONTH + ")$");
	private static final Pattern DATE_3_PATTERN = Pattern.compile("^--(" + MONTH + ")(" + DAY + ")?$");
	private static final Pattern DATE_4_PATTERN = Pattern.compile("^---(" + DAY + ")$");
	private BDayProperty property;
	private boolean isValid = false;
	private boolean isDone = false;

	BDayPropertyParser() {
		super(Property.BDAY);
	}

	@Override
	void parse() {
		try {
			super.parse();
			if (!hasValidParameter()) {
				isValid = false;
				return;
			}
			/* 
			 * At this time only CalScale.GREGORIAN is understood.
			 * RFC 6350 requires a VCard implementation to ignore a 
			 * date if it has a non-empty Calscale-parameter its value it
			 * does not understand.
			 */
			if (!isCalscaleGregorian()) {
				isValid = false;
				return;
			}
			try {
				// Get the single components
				BDayProperty.Builder builder = new BDayProperty.Builder();
				ValueType nonDefaultValueType = getValueType();
				String input = getValue();

				// Date in numbers (like 20010101)
				if (nonDefaultValueType == null || ValueType.DATE_AND_OR_TIME == nonDefaultValueType) {
					if (input == null) {
						isValid = false;
						return;
					}
					// default: date-and-or-time
					int year;
					int month;
					int day;

					Matcher matcher;
					if ((matcher = DATE_1_PATTERN.matcher(input)).matches()) {
						year = Integer.parseInt(matcher.group(1));
						month = matcher.group(3) == null || matcher.group(3).isEmpty() ? -1 : Integer.parseInt(matcher.group(3));
						day = matcher.group(4) == null || matcher.group(4).isEmpty() ? -1 : Integer.parseInt(matcher.group(4));
					} else if ((matcher = DATE_2_PATTERN.matcher(input)).matches()) {
						year = Integer.parseInt(matcher.group(1));
						month = Integer.parseInt(matcher.group(2));
						day = -1;
					} else if ((matcher = DATE_3_PATTERN.matcher(input)).matches()) {
						year = -1;
						month = Integer.parseInt(matcher.group(1));
						day = matcher.group(2) == null || matcher.group(2).isEmpty() ? -1 : Integer.parseInt(matcher.group(2));
					} else if ((matcher = DATE_4_PATTERN.matcher(input)).matches()) {
						year = -1;
						month = -1;
						day = Integer.parseInt(matcher.group(1));
					} else {
						// an empty date is not allowed by RFC6350
						// time and date-time as well as time zone are not supported yet
						isValid = false;
						return;
					}
					builder.year(year);
					builder.month(month);
					builder.day(day);
				} else { // Date as text (like 'in the last century')
					assert ValueType.TEXT == nonDefaultValueType : nonDefaultValueType;
					if (input == null) {
						builder.text(EMPTY_STRING);
					} else {
						String dateText = VCardTools.deEscape(input);
						builder.text(dateText);
					}
				}
				property = builder.build();
				isValid = true;
			} catch (IllegalArgumentException iae) {
				LOGGER.log(Level.SEVERE, null, iae);
			} catch (IllegalStateException ise) {
				LOGGER.log(Level.SEVERE, null, ise);
			}
		} finally {
			isDone = true;
		}
	}

	@Override
	BDayProperty getPropertyEntry() {
		if (!isDone()) {
			throw new IllegalStateException();
		}
		return property;
	}

	@Override
	boolean isDone() {
		return super.isDone() && isDone;
	}

	@Override
	boolean isValid() {
		return isCalscaleGregorian() && isValid;
	}

	@Override
	public void reset(String param, String value) {
		super.reset(param, value);
		property = null;
		isValid = false;
		isDone = false;
	}
}
