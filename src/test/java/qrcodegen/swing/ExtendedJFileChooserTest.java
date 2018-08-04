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

import qrcodegen.swing.ExtendedJFileChooser;
import qrcodegen.swing.FileExtensionFilter;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
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
public class ExtendedJFileChooserTest {
	
	public ExtendedJFileChooserTest() {
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
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void getFileNameExtensionFilterShouldReturnNullIfIsFileFilter(){
		ExtendedJFileChooser chooser = new ExtendedJFileChooser();
		chooser.setFileFilter(new MockFileFilter());
		FileFilter result = chooser.getFileExtensionFilter();
		assertThat(result, nullValue());
	}
	
	@Test
	public void getFileNameExtensionFilterShouldReturnSameIfIsFileNameExtensionFilter(){
		FileFilter filter = new FileExtensionFilter("Text", "txt");
		ExtendedJFileChooser chooser = new ExtendedJFileChooser();
		chooser.setFileFilter(filter);
		FileFilter result = chooser.getFileExtensionFilter();
		assertThat(result, sameInstance(filter));
	}

	@Test
	public void getFileNameExtensionFilterShouldReturnNullForDefaultFilter(){
		ExtendedJFileChooser chooser = new ExtendedJFileChooser();
		FileFilter result = chooser.getFileExtensionFilter();
		assertThat(result, nullValue());
	}
	
	@Test
	public void hasFileNameExtensionFilterShouldReturnFalseForDefaultFilter(){
		ExtendedJFileChooser chooser = new ExtendedJFileChooser();
		boolean result = chooser.currentFilterIsFileExtensionFilter();
		assertThat(result, is(Boolean.FALSE));
	}
	
	@Test
	public void hasFileNameExtensionFilterShouldReturnTrueIfIsFileNameExtensionFilter(){
		FileFilter filter = new FileExtensionFilter("Text", "txt");
		ExtendedJFileChooser chooser = new ExtendedJFileChooser();
		chooser.setFileFilter(filter);
		boolean result = chooser.currentFilterIsFileExtensionFilter();
		assertThat(result, is(Boolean.TRUE));
	}
	
	private static class MockFileFilter extends FileFilter{

		@Override
		public boolean accept(File f) {
			return true;
		}

		@Override
		public String getDescription() {
			return "";
		}
		
	}

}
