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
package qrcodegen.tools;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Stefan Ganzer
 */
public class ImageFileWriter {

	private final RenderedImage image;

	public ImageFileWriter(RenderedImage image) {
		if (image == null) {
			throw new NullPointerException();
		}
		this.image = image;
	}

	/**
	 * Writes the image to the given file. The format is defined be the file
	 * extension, that is the file has to have a valid extension that a writer
	 * exists for.
	 *
	 * @param file the file to write the given image to
	 *
	 * @throws IllegalArgumentException if the file is a directory, or if
	 * {@link #hasValidExtension(java.io.File)} returns false
	 * @throws NullPointerException if file is null
	 * @throws FileNotFoundException if the given file can not be written to or
	 * if it can not be created
	 * @throws IOException if writing fails due to an IO error or because no
	 * writer plugin is available for the file format defined by the file
	 * extension
	 */
	public void toFile(File file) throws FileNotFoundException, IOException {
		if (file == null) {
			throw new NullPointerException();
		}
		File f = new File(file.getAbsolutePath());
		if (f.isDirectory()) {
			throw new IllegalArgumentException("The file denotes a directory: " + f.getAbsolutePath());
		}
		/* ImageIO.write swallows the IOException that is thrown by the
		 * underlying FileImageOutputStream if the file cannot be created or
		 * be written to, and throws a NullPointerException instead.
		 * So we test if we can write to or create the file before calling that
		 * method. See http://stackoverflow.com/a/11154485/1003100
		 */
		if (f.exists()) {
			if (!f.canWrite()) {
				throw new FileNotFoundException("No write access to the existing file: " + f.getAbsolutePath());
			}
		} else {
			if (!f.createNewFile()) {
				throw new FileNotFoundException("Cannot create the file: " + f.getAbsolutePath());
			}
		}
		String formatName = StaticTools.getExtension(f);

		boolean foundWriter = ImageIO.write(image, formatName, f);
		if (!foundWriter) {
			throw new IOException("No appropriate writer was found for " + formatName); //NOI18N
		}
	}
}
