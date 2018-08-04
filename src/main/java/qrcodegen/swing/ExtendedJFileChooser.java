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
package qrcodegen.swing;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author Stefan Ganzer
 */
public class ExtendedJFileChooser extends JFileChooser implements FileChooser {

	private static final long serialVersionUID = 1L;

	public ExtendedJFileChooser() {
	}

	public ExtendedJFileChooser(File currentDirectory) {
		super(currentDirectory);
	}

	public ExtendedJFileChooser(FileSystemView fsv) {
		super(fsv);
	}

	public ExtendedJFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

	public ExtendedJFileChooser(File currentDirectory, FileSystemView fsv) {
		super(currentDirectory, fsv);
	}

	public ExtendedJFileChooser(String currentDirectoryPath, FileSystemView fsv) {
		super(currentDirectoryPath, fsv);
	}

	/**
	 * Returns the currently selected file filter.
	 *
	 * @return
	 */
	@Override
	public FileExtensionFilter getFileExtensionFilter() {
		FileFilter filter = getFileFilter();
		FileExtensionFilter result;
		if (filter instanceof FileExtensionFilter) {
			result = (FileExtensionFilter) filter;
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * Returns true if the currently selected file filter is an instance of
	 * <code>FileExtensionFilter</code>.
	 *
	 * @return
	 */
	@Override
	public boolean currentFilterIsFileExtensionFilter() {
		return (getFileFilter() instanceof FileExtensionFilter);
	}
}
