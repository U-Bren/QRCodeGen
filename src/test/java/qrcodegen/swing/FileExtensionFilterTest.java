/*
 * Copyright (C) 2012 Stefan Ganzer
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
package qrcodegen.swing;

import qrcodegen.swing.FileExtensionFilter;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Stefan Ganzer
 */
public class FileExtensionFilterTest {

	public FileExtensionFilterTest() {
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

	@Test
	public void getDefaultExtensionShouldReturnExtensionIfUsing2ArgumentConstructor() {
		String extension = "txt";
		FileExtensionFilter f = new FileExtensionFilter(null, extension);
		String result = f.getDefaultExtension();
		assertThat(result, equalTo(extension));
	}

	@Test
	public void getDefaultExtensionShouldReturnFirstExtensionIfUsing2ArgumentConstructor() {
		String[] extension = {"jpeg", "jpg"};
		FileExtensionFilter f = new FileExtensionFilter(null, extension);
		String result = f.getDefaultExtension();
		assertThat(result, equalTo(extension[0]));
	}

	@Test
	public void getDefaultExtensionShouldReturnDesiredDefaultExtension() {
		String[] extension = {"jpeg", "jpg"};
		int indexDefaultExtension = 1;

		FileExtensionFilter f = new FileExtensionFilter(null, indexDefaultExtension, extension);
		String result = f.getDefaultExtension();
		assertThat(result, equalTo(extension[indexDefaultExtension]));
	}

	@Test
	public void endsWithDefaultExtensionShouldReturnFalseForNullStringValue() {
		String extension = "txt";
		FileExtensionFilter f = new FileExtensionFilter(null, extension);
		boolean result = f.endsWithDefaultExtension((String) null);
		assertThat(result, equalTo(Boolean.FALSE));
	}

	@Test
	public void endsWithDefaultExtensionShouldReturnFalseForNullFileValue() {
		String extension = "txt";
		FileExtensionFilter f = new FileExtensionFilter(null, extension);
		boolean result = f.endsWithDefaultExtension((File) null);
		assertThat(result, equalTo(Boolean.FALSE));
	}

	@Test
	public void appendDefaultExtensionShouldReturnStringWithDefaultExtensionForFileWithoutExtension() {
		String[] extension = {"jpeg", "jpg"};
		int indexDefaultExtension = 1;

		File file = new File("test");

		FileExtensionFilter filter = new FileExtensionFilter(null, indexDefaultExtension, extension);
		String result = filter.appendDefaultExtension(file.getPath());
		assertThat(result, equalTo("test.jpg"));
	}

	@Test
	public void appendDefaultExtensionShouldReturnStringWithDefaultExtensionForFileWithoutExtensionEndingWithDot() {
		String[] extension = {"jpeg", "jpg"};
		int indexDefaultExtension = 1;

		File file = new File("test.");

		FileExtensionFilter filter = new FileExtensionFilter(null, indexDefaultExtension, extension);
		String result = filter.appendDefaultExtension(file.getPath());
		assertThat(result, equalTo("test..jpg"));
	}

	@Test
	public void appendDefaultExtensionShouldReturnFileWithDefaultExtensionForFileWithoutExtensionEndingWithDot() {
		String[] extension = {"jpeg", "jpg"};
		int indexDefaultExtension = 0;

		File file = new File("test.");
		File expectedResult = new File("test..jpeg");

		FileExtensionFilter filter = new FileExtensionFilter(null, indexDefaultExtension, extension);
		File result = filter.appendDefaultExtension(file);
		assertThat(result, equalTo(expectedResult));
	}

	@Test
	public void endsWithExtensionShouldReturnFalseIfPathEndsWithDifferentExtension() {
		String[] extension = {"jpeg", "jpg"};
		int indexDefaultExtension = 0;

		File file = new File("test.txt");
		boolean expectedResult = false;

		FileExtensionFilter filter = new FileExtensionFilter(null, indexDefaultExtension, extension);
		boolean result = filter.endsWithExtension(file.getPath());
		assertThat(result, equalTo(expectedResult));
	}
	
	@Test
	public void endsWithExtensionShouldReturnFalseIfPathEndsWithNoExtension() {
		String[] extension = {"jpeg", "jpg"};
		int indexDefaultExtension = 0;

		File file = new File("test");
		boolean expectedResult = false;

		FileExtensionFilter filter = new FileExtensionFilter(null, indexDefaultExtension, extension);
		boolean result = filter.endsWithExtension(file.getPath());
		assertThat(result, equalTo(expectedResult));
	}
	@Test
	public void endsWithExtensionShouldReturnTrueIfPathEndsWithMatchingExtension() {
		String[] extension = {"jpeg", "jpg"};
		int indexDefaultExtension = 0;

		File file = new File("test.jPeg");
		boolean expectedResult = true;

		FileExtensionFilter filter = new FileExtensionFilter(null, indexDefaultExtension, extension);
		boolean result = filter.endsWithExtension(file.getPath());
		assertThat(result, equalTo(expectedResult));
	}
}
