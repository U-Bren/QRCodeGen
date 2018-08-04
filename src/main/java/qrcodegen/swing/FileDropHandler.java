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
package qrcodegen.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import qrcodegen.tools.CollectionTools;

/**
 *
 * @author Stefan Ganzer
 */
public class FileDropHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;
	private static final List<FileFilter> EMPTY_LIST = Collections.emptyList();
	private final Logger logger;
	private final ChangeSupport cs;
	private final List<FileFilter> fileFilter;
	private List<File> files;
	private List<File> filteredFiles;

	public FileDropHandler(Logger logger) {
		this(EMPTY_LIST, logger);
	}

	public FileDropHandler(FileFilter filter, Logger logger) {
		if (filter == null) {
			throw new NullPointerException();
		}
		if (logger == null) {
			throw new NullPointerException();
		}
		fileFilter = Arrays.asList(filter);
		if (fileFilter.contains(null)) {
			throw new IllegalArgumentException("list mustn't contain null");
		}
		this.logger = logger;
		this.files = Collections.emptyList();
		this.cs = new ChangeSupport(this, logger);
	}

	public FileDropHandler(List<? extends FileFilter> filter, Logger logger) {
		if (filter == null) {
			throw new NullPointerException();
		}
		if (logger == null) {
			throw new NullPointerException();

		}
		this.fileFilter = new ArrayList<FileFilter>(filter);
		if (fileFilter.contains(null)) {
			throw new IllegalArgumentException("list mustn't contain null");
		}
		this.logger = logger;
		this.files = Collections.emptyList();
		this.cs = new ChangeSupport(this, logger);
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}

		if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			return false;
		}

		boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;

		if (!copySupported) {
			return false;
		}
		support.setDropAction(COPY);
		return true;
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support) {
		if (!canImport(support)) {
			return false;
		}
		Transferable t = support.getTransferable();

		try {
			// from DataFlavor.javaFileListFlavor:
			//To transfer a list of files to/from Java (and the underlying platform) a DataFlavor
			//of this type/subtype and representation class of java.util.List is used.
			//Each element of the list is required/guaranteed to be of type java.io.File.
			@SuppressWarnings("unchecked")
			List<File> fileList = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
			files = fileList;
			filteredFiles = getFilesPassingTheFileFilters(fileList);
			cs.fireChangeEvent();
			return true;
		} catch (UnsupportedFlavorException e) {
			logger.log(Level.SEVERE, "Shouldn't have happened", e);
			return false;
		} catch (IOException e) {
			logger.log(Level.FINER, null, e);
			return false;
		}
	}

	/**
	 * Returns only the files that passed the file filters.
	 *
	 * @return the files that passed the file filters. Never returns null.
	 */
	public List<File> getFiles() {
		return CollectionTools.copyList(filteredFiles);
	}

	/**
	 * Returns all the files that were acquired by this drop handler, regardless
	 * whether they pass the file filter or not.
	 *
	 * @return all the files that were acquired by this drop handler. Never
	 * returns null.
	 */
	public List<File> getAllFiles() {
		return CollectionTools.copyList(files);
	}

	/**
	 * Returns this instances file filters.
	 *
	 * <em>The file filters are not guaranteed to be immutable, so changing the
	 * filters could affect this FileDropHandler.</em>
	 *
	 * @return a list of this instances file filters. The caller is free to
	 * change the list without affecting this instance, <em>but must not change
	 * the filter instances.</em> Never returns null.
	 */
	public List<FileFilter> getFileFilter() {
		// XXX FileFilter is not guaranteed to be immutable
		return CollectionTools.copyList(fileFilter);
	}

	/**
	 * Returns a new list of files containing only those passing this instances
	 * file filters. This method doesn't change the order of the files.
	 *
	 * @param fileList a list of files. Must not contain null.
	 *
	 * @return a list of files that passed the file filters. Never returns null.
	 *
	 * @throws NullPointerException if fileList is null
	 *
	 * @see #getFileFilter()
	 */
	List<File> getFilesPassingTheFileFilters(List<File> fileList) {
		final List<File> result = new ArrayList<File>(fileList.size());
		for (File f : fileList) {
			for (FileFilter filter : fileFilter) {
				if (filter.accept(f)) {
					result.add(f);
				}
			}
		}
		return result;
	}

	public void addChangeListener(ChangeListener listener) {
		cs.addChangeListener(listener);
	}

	public void removeChangeListener(ChangeListener listener) {
		cs.removeChangeListener(listener);
	}
}
