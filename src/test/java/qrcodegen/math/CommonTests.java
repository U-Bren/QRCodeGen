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
package qrcodegen.math;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Stefan Ganzer
 */
public class CommonTests {

	private CommonTests() {
	}

	public static void testEquals(Degree a, Degree b, boolean expectedResult) {
		boolean actualResult = a.equals(b);
		assertThat(actualResult, equalTo(expectedResult));
	}

	public static void testForLessGreaterEqualTo(Degree a, Degree b, boolean less, boolean lessOrEqual, boolean equalTo, boolean greaterOrEqual, boolean greater) {
		assertThat(a.lessThan(b), equalTo(less));
		assertThat(a.lessThanOrEqualTo(b), equalTo(lessOrEqual));
		assertThat(a.equalTo(b), equalTo(equalTo));
		assertThat(a.greaterThanOrEqualTo(b), equalTo(greaterOrEqual));
		assertThat(a.greaterThan(b), equalTo(greater));
	}
}
