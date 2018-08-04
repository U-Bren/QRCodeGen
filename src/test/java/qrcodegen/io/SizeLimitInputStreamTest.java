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
package qrcodegen.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Stefan Ganzer
 */
public class SizeLimitInputStreamTest {

	private static final String LENGTH_10_BYTE = "abcdefghij";
	private static final Charset UTF_8 = Charset.forName("UTF-8");

	public SizeLimitInputStreamTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test(expected = SizeLimitException.class)
	public void shouldThrowExceptionAfterReadingOneByteIfLimitIsZeroByte() throws IOException {
		InputStream in = new SizeLimitInputStream(new ByteArrayInputStream(LENGTH_10_BYTE.getBytes(UTF_8)), 0);
		while (true) {
			int value = in.read();
			if (value == -1) {
				break;
			}
		}
	}

	@Test
	public void shouldNotThrowExceptionIfLimitEqualsInputLength() throws IOException {
		InputStream in = new SizeLimitInputStream(new ByteArrayInputStream(LENGTH_10_BYTE.getBytes(UTF_8)), 10L);
		while (true) {
			int value = in.read();
			if (value == -1) {
				break;
			}
		}
	}

	@Test(expected = SizeLimitException.class)
	public void shouldThrowExceptionIfLimitExceedsInputLength() throws IOException {
		InputStream in = new SizeLimitInputStream(new ByteArrayInputStream(LENGTH_10_BYTE.getBytes(UTF_8)), 9L);
		while (true) {
			int value = in.read();
			if (value == -1) {
				break;
			}
		}
	}

	@Test
	public void shouldCountReadBytesCorrectly() throws IOException {
		SizeLimitInputStream in = new SizeLimitInputStream(new ByteArrayInputStream(LENGTH_10_BYTE.getBytes(UTF_8)), Long.MAX_VALUE);
		while (true) {
			int value = in.read();
			if (value == -1) {
				break;
			}
		}
		long expected = 10;
		long actual = in.getCurrentSize();
		assertThat(actual, equalTo(expected));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionIfLimitIsLessThanZero() {
		InputStream in = new SizeLimitInputStream(new ByteArrayInputStream(LENGTH_10_BYTE.getBytes(UTF_8)), -1);
	}
}