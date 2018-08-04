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
package qrcodegen.kml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.naming.LimitExceededException;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import qrcodegen.io.SizeLimitException;
import qrcodegen.io.SizeLimitInputStream;
import qrcodegen.kml.Kml;

/**
 *
 * @author Stefan Ganzer
 */
public class KMLFileReader {

	private final Logger logger;
	private final File file;
	private final long limit;
	private Kml kml;

	public KMLFileReader(File file, long limit, Logger logger) {
		if (file == null) {
			throw new NullPointerException();
		}
		if (limit < 0) {
			throw new IllegalArgumentException(Long.toString(limit) + " < 0");
		}
		if (logger == null) {
			throw new NullPointerException();
		}
		this.logger = logger;
		this.limit = limit;
		this.file = file;
	}

	public void readFile() throws ZipException, FileNotFoundException, IOException {
		try {
			readZippedFile();
		} catch (ZipException ze) {
			readUncompressedFile();
		}

	}

	public void readZippedFile() throws ZipException, FileNotFoundException, IOException {
		ZipFile z = null;
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		try {
			z = new ZipFile(file, ZipFile.OPEN_READ);
			ZipEntry doc = z.getEntry("doc.kml");
			if (doc == null) {
				throw new FileNotFoundException(file.getAbsolutePath() + " doesn't contain a doc.kml file");
			}
			if (doc.getSize() == -1 || doc.getSize() > limit) {
				throw new SizeLimitException(limit, doc.getSize());
			}
			kml = JAXB.unmarshal(new SizeLimitInputStream(z.getInputStream(doc), limit), Kml.class);
		} catch (DataBindingException dbe) {
			throw new IOException(dbe);
		} finally {
			if (z != null) {
				try {
					z.close();
				} catch (IOException ioe) {
					logger.throwing("KMLFileReader", "readZippedFile()", ioe);
				}
			}
		}
	}

	public void readUncompressedFile() throws FileNotFoundException, IOException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath());
		}
		if (file.length() > limit) {
			throw new IOException(new SizeLimitException(limit, file.length()));
		}
		InputStream in = null;
		try {
			in = new SizeLimitInputStream(new FileInputStream(file), limit);
			kml = JAXB.unmarshal(in, Kml.class);
		} catch (DataBindingException dbe) {
			throw new IOException(dbe);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ioe) {
					logger.throwing("KMLFileReader", "readUncompressedFile()", ioe);
				}
			}
		}
	}

	public Kml getKml() {
		return kml;
	}
}
