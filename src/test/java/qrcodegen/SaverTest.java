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
package qrcodegen;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.ListResourceBundle;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Rule;
import org.junit.internal.runners.statements.ExpectException;
import org.junit.rules.ExpectedException;
import qrcodegen.swing.FileChooser;
import qrcodegen.swing.FileExtensionFilter;
import qrcodegen.swing.Saveable;

/**
 *
 * @author Stefan Ganzer
 */
public class SaverTest {

	private MockSaveable mockSaveable;
	private MockFileChooser mockFileChooser;
	private FileExtensionFilter txtFilter;
	private ResourceBundle res;

	public SaverTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
		mockSaveable = new MockSaveable();
		mockFileChooser = new MockFileChooser();
		txtFilter = new FileExtensionFilter("Text file", "txt");
		res = new SaverResourceBundle();
	}

	@After
	public void tearDown() {
		mockSaveable = null;
		mockFileChooser = null;
		txtFilter = null;
		res = null;
	}
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void constructorShouldThrowNullPointerExceptionIfFileChooserIsNullValue() {
		thrown.expect(NullPointerException.class);
		new Saver(null, mockSaveable, res);
	}

	@Test
	public void constructorShouldThrowNullPointerExceptionIfSaveActionIsNullValue() {
		thrown.expect(NullPointerException.class);
		new Saver(mockFileChooser, null, res);
	}

	@Test
	public void saveShouldInvokeSaveAsOnNewSaver() {

		File file = new File("testFile");
		File selectedFile = new File("testFile.txt");
		file.deleteOnExit();
		selectedFile.deleteOnExit();
		mockFileChooser = new MockFileChooser(FileChooser.APPROVE_OPTION, file, txtFilter);
		Saver saver = new Saver(mockFileChooser, mockSaveable, res);
		saver.save();
		assertThat(mockFileChooser.selectedFile, equalTo(selectedFile));
		assertThat(mockSaveable.uri, equalTo(selectedFile.toURI()));
		assertThat(mockFileChooser.showedSaveDialog, equalTo(Boolean.TRUE));
	}

	@Test
	public void saveShouldInvokeSaveAfterSuccessfullyInvokingSaveAsOnceOnNewSaver() {

		File file = new File("testFile");
		File selectedFile = new File("testFile.txt");
		file.deleteOnExit();
		selectedFile.deleteOnExit();
		mockFileChooser = new MockFileChooser(FileChooser.APPROVE_OPTION, file, txtFilter);
		Saver saver = new Saver(mockFileChooser, mockSaveable, res);
		saver.save();
		assertThat(mockFileChooser.selectedFile, equalTo(selectedFile));
		assertThat(mockSaveable.uri, equalTo(selectedFile.toURI()));
		assertThat(mockFileChooser.showedSaveDialog, equalTo(Boolean.TRUE));
		mockFileChooser.showedSaveDialog = false;
		saver.save();
		assertThat(mockFileChooser.showedSaveDialog, equalTo(Boolean.FALSE));
	}

	@Test
	public void resetShouldSetLastFileToNull() {
		File file = new File("testFile");
		File selectedFile = new File("testFile.txt");
		file.deleteOnExit();
		selectedFile.deleteOnExit();
		mockFileChooser = new MockFileChooser(FileChooser.APPROVE_OPTION, file, txtFilter);
		Saver saver = new Saver(mockFileChooser, mockSaveable, res);
		assertNull(saver.getLastFile());
		saver.save();
		assertThat(saver.getLastFile(), equalTo(selectedFile));
		saver.resetLastFile();
		assertNull(saver.getLastFile());
	}

	@Test
	public void shouldNotThrowNPEIfNoFileFilterIsSetOnFileChooser() {
		File file = new File("testFile");
		file.deleteOnExit();
		mockFileChooser = new MockFileChooser(FileChooser.APPROVE_OPTION, file, null);
		Saver saver = new Saver(mockFileChooser, mockSaveable, res);
		saver.save();
	}

	private static class MockSaveable implements Saveable {

		private URI uri;

		@Override
		public void saveTo(URI uri) {
			this.uri = uri;
		}
	}

	private static class MockFileChooser implements FileChooser {

		private MockFileChooser() {
		}

		private MockFileChooser(int showDialogReturnValue, File initialSelectedFile, FileExtensionFilter getFileExtensionFilterReturnValue) {
			this.showSaveDialogReturnValue = showDialogReturnValue;
			this.selectedFile = initialSelectedFile;
			this.getFileExtensionFilterReturnValue = getFileExtensionFilterReturnValue;
		}
		private int showSaveDialogReturnValue;
		private int showOpenDialogReturnValue;
		private File selectedFile;
		private FileExtensionFilter getFileExtensionFilterReturnValue;
		private boolean showedSaveDialog;
		private boolean showedOpenDialog;

		@Override
		public int showSaveDialog(Component parent) {
			showedSaveDialog = true;
			return showSaveDialogReturnValue;
		}

		@Override
		public int showOpenDialog(Component parent) {
			showedOpenDialog = true;
			return showOpenDialogReturnValue;
		}

		@Override
		public File getSelectedFile() {
			return selectedFile;
		}

		@Override
		public void setSelectedFile(File file) {
			if (file != null && file.getPath().isEmpty()) {
				return;
			}
			selectedFile = file;
		}

		@Override
		public boolean currentFilterIsFileExtensionFilter() {
			return getFileExtensionFilterReturnValue != null;
		}

		@Override
		public FileExtensionFilter getFileExtensionFilter() {
			return getFileExtensionFilterReturnValue;
		}
	}

	private static class SaverResourceBundle extends ListResourceBundle {

		@Override
		protected Object[][] getContents() {
			return new Object[][]{
						// LOCALIZE THE SECOND STRING OF EACH ARRAY (e.g., "OK")
						{"FILE EXISTS TITLE", "File exists"},
						{"FILE EXISTS MESSAGE", "File{0}{1}{0}exists. Overwrite?"},
						{"DIRECTORY WITH SAME NAME EXISTS TITLE", "Directory with same name"},
						{"DIRECTORY WITH SAME NAME MESSAGE", "Cannot write file{0}{1}{0}as there is already a directory of this name.{0}Please choose another name, or cancel this operation."}// END OF MATERIAL TO LOCALIZE
					};
		}
	}
}
