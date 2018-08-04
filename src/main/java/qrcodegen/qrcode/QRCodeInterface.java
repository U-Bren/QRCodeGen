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
package qrcodegen.qrcode;

import com.google.zxing.qrcode.decoder.Mode;
import com.google.zxing.qrcode.decoder.Version;

/**
 *
 * @author Stefan Ganzer
 */
public interface QRCodeInterface {

	public Mode getMode();

	public Version getVersion();

	public int getVersionNumber();

	/**
	 * Returns the number of total bytes in the QR Code.
	 *
	 * @return the number of total bytes in the QR Code, or 0 if generateCode()
	 * hasn't been called yet or after {@link #resetState()} has been called.
	 */
	public int getNumTotalBytes();

	/**
	 * Returns the number of bytes in the QR Code.
	 *
	 * @return the number of bytes in the QR Code, or 0 if generateCode()
	 * hasn't been called yet or after {@link #resetState()} has been called.
	 */
	public int getNumDataBytes();

	/**
	 * Returns the number of error correction bytes in the QR Code.
	 *
	 * @return the number of error correction bytes in the QR Code, or 0 if
	 * generateCode() hasn't been called yet or after {@link #resetState()} has
	 * been called.
	 */
	public int getNumECBytes();

	/**
	 * Returns the number of Reed-Solomon blocks in the QR Code.
	 *
	 * @return the number of Reed-Solomon blocks in the QR Code, or 0 if
	 * generateCode() hasn't been called yet or after {@link #resetState()} has
	 * been called.
	 */
	public int getNumRSBlocks();
}
