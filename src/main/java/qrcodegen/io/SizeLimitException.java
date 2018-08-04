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
package qrcodegen.io;

import java.io.IOException;

/**
 *
 * @author Stefan Ganzer
 */
public class SizeLimitException extends IOException {

	private long limit;
	private long actualSize;

	/**
	 * Creates a new instance of
	 * <code>SizeLimitException</code> without detail message.
	 */
	public SizeLimitException() {
	}

	/**
	 * Constructs an instance of
	 * <code>SizeLimitException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public SizeLimitException(String msg) {
		super(msg);
	}

	public SizeLimitException(Throwable cause) {
		super(cause);
	}

	public SizeLimitException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SizeLimitException(long limit, long actualSize) {
		super("Limit: " + limit + ", actual size: " + actualSize);
		this.limit = limit;
		this.actualSize = actualSize;
	}

	public long getLimit() {
		return limit;
	}

	public long getActualSize() {
		return actualSize;
	}
}
