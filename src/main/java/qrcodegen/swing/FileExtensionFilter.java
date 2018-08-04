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
import java.util.Locale;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import qrcodegen.tools.ExtendedIndexOutOfBoundsException;

/**
 *
 * @author Stefan Ganzer
 */
public final class FileExtensionFilter extends FileFilter {

	private final FileNameExtensionFilter filter;
	private final int indexDefaultExtension;

	public FileExtensionFilter(String description, String... extensions) {
		this(description, 0, extensions);
	}

	public FileExtensionFilter(String description, int indexDefaultExtension, String... extensions) {
		filter = new FileNameExtensionFilter(description, extensions);
		if (indexDefaultExtension < 0 || indexDefaultExtension >= extensions.length) {
			throw new ExtendedIndexOutOfBoundsException(0, extensions.length, indexDefaultExtension);
		}
		this.indexDefaultExtension = indexDefaultExtension;
	}

	@Override
	public boolean accept(File f) {
		return filter.accept(f);
	}

	@Override
	public String getDescription() {
		return filter.getDescription();
	}

	public String[] getExtension() {
		return filter.getExtensions();
	}

	/**
	 * Returns the default extension. If you for instance create a filter for
	 * "jpg", "jpeg", you could set the default extension to "jpg", and create
	 * files with this name. If the default extension was not set during
	 * construction, set first extension is returned instead.
	 *
	 * @return the default extension, or the first extension if the default
	 * extension was not set during construction
	 */
	public String getDefaultExtension() {
		return getExtension()[indexDefaultExtension];
	}

	public boolean endsWithDefaultExtension(String path) {
		boolean result;
		if (path == null) {
			result = false;
		} else {
			String lowerCaseFileName = path.toLowerCase(Locale.ENGLISH);
			result = lowerCaseFileName.endsWith("." + getDefaultExtension().toLowerCase(Locale.ENGLISH));
		}
		return result;
	}

	public boolean endsWithDefaultExtension(File file) {
		boolean result;
		if (file == null) {
			result = false;
		} else {
			result = endsWithDefaultExtension(file.getName());
		}
		return result;
	}

	public boolean endsWithExtension(String path) {
		if (path == null) {
			throw new NullPointerException();
		}
		boolean result = false;
		String lowerCasePath = path.toLowerCase(Locale.ENGLISH);
		for (String s : filter.getExtensions()) {
			String extension = ".".concat(s.toLowerCase(Locale.ENGLISH));
			if (lowerCasePath.endsWith(extension)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public boolean endsWithExtension(File file) {
		if (file == null) {
			throw new NullPointerException();
		}
		return endsWithExtension(file.getPath());
	}

	public String appendDefaultExtension(String path) {
		if (path == null) {
			throw new NullPointerException();
		}
		return path.concat(".").concat(getDefaultExtension());
	}

	public File appendDefaultExtension(File file) {
		if (file == null) {
			throw new NullPointerException();
		}
		return new File(appendDefaultExtension(file.getPath()));
	}
}
