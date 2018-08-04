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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import qrcodegen.tools.StaticTools;
import qrcodegen.swing.FileChooser;
import qrcodegen.swing.FileExtensionFilter;
import qrcodegen.swing.Saveable;
import qrcodegen.tools.Shortener;
import qrcodegen.tools.TextShortener;

/**
 *
 * @author Stefan Ganzer
 */
public class ReaderSaver {

	public static final String LAST_SAVED_FILE_PROPERTY = "LastSavedFile";
	public static final String LAST_READ_FILE_PROPERTY = "LastReadFile";
	private static final File EMPTY_FILE = new File("");
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final Shortener<String> shortener = new TextShortener(70);
	private final ResourceBundle res;
	private final FileChooser fileChooser;
	private final Saveable saveable;
	private final Readable readable;
	private File lastSavedFile;
	private File lastReadFile;

	public ReaderSaver(FileChooser fileChooser, Saveable saveable, ResourceBundle bundle) {
		this(fileChooser, null, saveable, bundle);
	}

	public ReaderSaver(FileChooser fileChooser, Readable readable, ResourceBundle bundle) {
		this(fileChooser, readable, null, bundle);
	}

	public ReaderSaver(FileChooser fileChooser, Readable readable, Saveable saveable, ResourceBundle bundle) {
		if (fileChooser == null) {
			throw new NullPointerException();
		}
		if (readable == null && saveable == null) {
			throw new IllegalArgumentException("At least one must be non-null");
		}
		if (bundle == null) {
			throw new NullPointerException();
		}
		this.fileChooser = fileChooser;
		this.readable = readable;
		this.saveable = saveable;
		this.res = bundle;
	}

	public void save() {
		if (saveable == null) {
			throw new UnsupportedOperationException();
		}
		if (lastSavedFile == null) {
			saveAs();
		} else {
			saveable.saveTo(lastSavedFile.toURI());
		}
	}

	public void saveAs() {
		if (saveable == null) {
			throw new UnsupportedOperationException();
		}
		boolean done = false;

		File selectedFile = lastSavedFile;

		while (!done) {
			if (selectedFile == null) {
				clearFileNameInputField();
			} else {
				fileChooser.setSelectedFile(new File(StaticTools.getFileNameWOExtension(selectedFile)));
			}

			// // // //
			///////////
			int result = fileChooser.showSaveDialog(null);
			///////////
			// // // //

			if (result == FileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
				FileExtensionFilter currentFilter = fileChooser.getFileExtensionFilter();
				if (currentFilter != null && !currentFilter.endsWithExtension(selectedFile)) {
					selectedFile = currentFilter.appendDefaultExtension(selectedFile);
					fileChooser.setSelectedFile(selectedFile);
				}

				if (selectedFile.exists()) {
					int confirmation;
					if (selectedFile.isDirectory()) { // Cannot happen when using JFileChooser
						String title = res.getString("DIRECTORY WITH SAME NAME TITLE");
						String message = MessageFormat.format(res.getString("DIRECTORY WITH SAME NAME MESSAGE"), "\n", selectedFile);
						confirmation = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION);
					} else {
						String title = res.getString("FILE EXISTS TITLE");
						String message = MessageFormat.format(res.getString("FILE EXISTS MESSAGE"), "\n", selectedFile.getName());
						confirmation = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_CANCEL_OPTION);

					}

					if (confirmation == JOptionPane.CANCEL_OPTION) {
						break;
					} else if (confirmation == JOptionPane.NO_OPTION) {
						continue;
					}// else write the file
				}
				saveable.saveTo(selectedFile.toURI());
				setLastSavedFile(selectedFile);
				done = true;
			} else {
				done = true;
			}
		}
	}

	public void read() {
		if (readable == null) {
			throw new UnsupportedOperationException();
		}
		if (lastReadFile == null) {
			readFrom();
		} else {
			readable.readFrom(lastReadFile.toURI());
		}
	}

	public void readFrom() {
		if (readable == null) {
			throw new UnsupportedOperationException();
		}
		boolean done = false;

		File selectedFile = lastReadFile;

		while (!done) {
			if (selectedFile == null) {
				clearFileNameInputField();
			} else {
				fileChooser.setSelectedFile(new File(StaticTools.getFileNameWOExtension(selectedFile)));
			}

			// // // //
			///////////
			int result = fileChooser.showOpenDialog(null);
			///////////
			// // // //

			if (result == FileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
				FileExtensionFilter currentFilter = fileChooser.getFileExtensionFilter();
				if (currentFilter != null && !currentFilter.endsWithExtension(selectedFile)) {
					selectedFile = currentFilter.appendDefaultExtension(selectedFile);
					fileChooser.setSelectedFile(selectedFile);
				}

				if (!selectedFile.exists()) {
					//int confirmation = JOptionPane.showConfirmDialog(this, java.text.MessageFormat.format(RES.getString("FILE {0} EXISTS. OVERWRITE?"), selectedFile.getName()), RES.getString("FILE EXISTS"), JOptionPane.YES_NO_CANCEL_OPTION);
					String title = res.getString("FILE DOES NOT EXIST TITLE");
					String message = MessageFormat.format(res.getString("FILE DOES NOT EXIST MESSAGE"), "\n", shortener.shorten(selectedFile.getName()));
					int confirmation = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION);
					if (confirmation == JOptionPane.CANCEL_OPTION) {
						break;
					} else if (confirmation == JOptionPane.OK_OPTION) {
						continue;
					}// else read the file
				}
				readable.readFrom(selectedFile.toURI());
				setLastReadFile(selectedFile);
				done = true;
			} else {
				done = true;
			}
		}
	}

	private void setLastReadFile(File f) {
		File oldFile = lastReadFile;
		lastReadFile = f;
		pcs.firePropertyChange(LAST_READ_FILE_PROPERTY, oldFile, lastReadFile);
	}

	private void setLastSavedFile(File f) {
		File oldFile = lastSavedFile;
		lastSavedFile = f;
		pcs.firePropertyChange(LAST_SAVED_FILE_PROPERTY, oldFile, lastSavedFile);
	}

	public File getLastReadFile() {
		return lastReadFile;
	}

	public File getLastSavedFile() {
		return lastSavedFile;
	}

	public void resetLastReadFile() {
		setLastReadFile(null);
	}

	public void resetLastSavedFile() {
		setLastSavedFile(null);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

	private void clearFileNameInputField() {
		// clears the file name field, but null doesn't
		fileChooser.setSelectedFile(EMPTY_FILE);
	}

	public interface Readable {

		void readFrom(URI uri);
	}
}
