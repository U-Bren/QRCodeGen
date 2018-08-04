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
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import qrcodegen.tools.StaticTools;
import qrcodegen.swing.FileChooser;
import qrcodegen.swing.FileExtensionFilter;
import qrcodegen.swing.Saveable;

/**
 *
 * @author Stefan Ganzer
 */
public class Saver {

	public static final String LAST_FILE_PROPERTY = "LastFile";
	private static final File EMPTY_FILE = new File("");
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private final ResourceBundle res;
	private final FileChooser fileChooser;
	private final Saveable saveable;
	private File lastFile;

	public Saver(FileChooser fileChooser, Saveable saveable, ResourceBundle bundle) {
		if (fileChooser == null) {
			throw new NullPointerException();
		}
		if (saveable == null) {
			throw new NullPointerException();
		}
		if (bundle == null) {
			throw new NullPointerException();
		}
		this.fileChooser = fileChooser;
		this.saveable = saveable;
		this.res = bundle;
	}

	public void save() {
		if (lastFile == null) {
			saveAs();
		} else {
			saveable.saveTo(lastFile.toURI());
		}
	}

	public void saveAs() {
		boolean done = false;

		File selectedFile = lastFile;

		while (!done) {
			if (selectedFile == null) {
				clearFileNameInputField();
			} else {
				fileChooser.setSelectedFile(new File(StaticTools.getFileNameWOExtension(selectedFile)));
			}

			int result = fileChooser.showSaveDialog(null);

			if (result == FileChooser.APPROVE_OPTION) {
				selectedFile = fileChooser.getSelectedFile();
				FileExtensionFilter currentFilter = fileChooser.getFileExtensionFilter();
				if (currentFilter != null && !currentFilter.endsWithExtension(selectedFile)) {
					selectedFile = currentFilter.appendDefaultExtension(selectedFile);
					fileChooser.setSelectedFile(selectedFile);
				}

				if (selectedFile.exists()) {
					int confirmation;
					if (selectedFile.isDirectory()) {
						String title = res.getString("DIRECTORY WITH SAME NAME TITLE");
						String message = MessageFormat.format(res.getString("DIRECTORY WITH SAME NAME MESSAGE"), "\n", selectedFile);
						confirmation = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION);
					} else {
						String title = res.getString("FILE EXISTS TITLE");
						String message = MessageFormat.format(res.getString("FILE EXISTS MESSAGE"), "\n", selectedFile.getName());
						confirmation = JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_CANCEL_OPTION);

					}
					//int confirmation = JOptionPane.showConfirmDialog(this, java.text.MessageFormat.format(RES.getString("FILE {0} EXISTS. OVERWRITE?"), selectedFile.getName()), RES.getString("FILE EXISTS"), JOptionPane.YES_NO_CANCEL_OPTION);
					if (confirmation == JOptionPane.CANCEL_OPTION) {
						break;
					} else if (confirmation == JOptionPane.NO_OPTION) {
						continue;
					}// else write the file
				}
				saveable.saveTo(selectedFile.toURI());
				setLastFile(selectedFile);
				done = true;
			} else {
				done = true;
			}
		}
	}

	private void setLastFile(File f) {
		File oldFile = lastFile;
		lastFile = f;
		pcs.firePropertyChange(LAST_FILE_PROPERTY, oldFile, lastFile);
	}

	public File getLastFile() {
		return lastFile;
	}

	public void resetLastFile() {
		setLastFile(null);
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
}
