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
package qrcodegen;

import qrcodegen.tools.ImmutableDimension;

/**
 *
 * @author Stefan Ganzer
 */
public class CodeSizeException extends Exception {

	private static final long serialVersionUID = 1L;
	private ImmutableDimension value;
	private ImmutableDimension minDimension;
	private ImmutableDimension maxDimension;

	/**
	 * Creates a new instance of
	 * <code>CodeSizeException</code> without detail message.
	 */
	public CodeSizeException() {
	}

	/**
	 * Constructs an instance of
	 * <code>CodeSizeException</code> with the specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public CodeSizeException(String msg) {
		super(msg);
	}

	public CodeSizeException(String msg, ImmutableDimension minDimension, ImmutableDimension maxDimension, ImmutableDimension value) {
		super(msg);
		this.minDimension = minDimension;
		this.maxDimension = maxDimension;
		this.value = value;
	}

	public ImmutableDimension getMinDimension() {
		return minDimension;
	}

	public ImmutableDimension getMaxDimension() {
		return maxDimension;
	}

	public ImmutableDimension getDimension() {
		return value;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("; min: ").append(minDimension);
		sb.append("; max: ").append(maxDimension);
		sb.append("; value: ").append(value);
		return sb.toString();
	}
}
