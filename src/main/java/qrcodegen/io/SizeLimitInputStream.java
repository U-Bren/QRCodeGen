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
import java.io.InputStream;

/**
 *
 * @author Stefan Ganzer
 */
public class SizeLimitInputStream extends InputStream {

	private final InputStream in;
	private final long limit;
	private long sumOfBytes;


	public SizeLimitInputStream(InputStream in, long limit) {
		if (in == null) {
			throw new NullPointerException();
		}
		if (limit < 0) {
			throw new IllegalArgumentException(Long.toString(limit) + " < 0");
		}
		this.in = in;
		this.limit = limit;
		sumOfBytes = 0;
	}

	@Override
	public int read() throws IOException, SizeLimitException {
		int data = in.read();
		if (data > -1) {
			sumOfBytes = sumOfBytes + 1;
			if (sumOfBytes > limit) {
				throw new SizeLimitException(limit, sumOfBytes);
			}
		}
		return data;
	}
	
	public long getLimit(){
		return limit;
	}
	
	public long getCurrentSize(){
		return sumOfBytes;
	}
}
