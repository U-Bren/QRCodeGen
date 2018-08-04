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
import com.google.zxing.qrcode.encoder.QRCode;

/**
 *
 * @author Stefan Ganzer
 */
public final class VersionAdapter {

	private final Version version;
	private final QRCode code;

	public VersionAdapter() {
		code = null;
		version = null;
	}

	public VersionAdapter(QRCode code) {
		this.code = code;
		if(code == null){
			version = null;
		}else{
			version = code.getVersion();
		}
	}
	
	public QRCode getQRCode() {
		return code;
	}

	public Version getVersion() {
		return version;
	}

	/**
	 * Returns the version of this QR Code.
	 *
	 * @return the of this QR Code, or -1 if generateCode() hasn't been called
	 * yet or after {@link #resetState()} has been called.
	 */
	public int getVersionNumber() {
		return code == null ? -1 : version.getVersionNumber();
	}

	/**
	 * Returns the number of total bytes in the QR Code.
	 *
	 * @return the number of total bytes in the QR Code, or -1 if generateCode()
	 * hasn't been called yet or after {@link #resetState()} has been called.
	 */
	public int getNumTotalBytes() {
		return code == null ? -1 : version.getTotalCodewords();//code.getNumTotalBytes();
	}

	/**
	 * Returns the number of bytes in the QR Code.
	 *
	 * @return the number of bytes in the QR Code, or -1 if generateCode()
	 * hasn't been called yet or after {@link #resetState()} has been called.
	 */
	public int getNumDataBytes() {
		return code == null ? -1 : getNumTotalBytes() - getNumECBytes();//code.getNumDataBytes();
	}

	/**
	 * Returns the number of error correction bytes in the QR Code.
	 *
	 * @return the number of error correction bytes in the QR Code, or -1 if
	 * generateCode() hasn't been called yet or after {@link #resetState()} has
	 * been called.
	 */
	public int getNumECBytes() {
		int numECBytes;
		if (code == null) {
			numECBytes = -1;
		} else {
			Version.ECBlocks ecBlocks = version.getECBlocksForLevel(code.getECLevel());
			numECBytes = ecBlocks.getTotalECCodewords();//code.getNumECBytes();		
		}
		return numECBytes;
	}

	/**
	 * Returns the number of Reed-Solomon blocks in the QR Code.
	 *
	 * @return the number of Reed-Solomon blocks in the QR Code, or -1 if
	 * generateCode() hasn't been called yet or after {@link #resetState()} has
	 * been called.
	 */
	public int getNumRSBlocks() {
		int numRSBlocks;
		if (code == null) {
			numRSBlocks = -1;
		} else {
			Version.ECBlocks ecBlocks = version.getECBlocksForLevel(code.getECLevel());
			numRSBlocks = ecBlocks.getNumBlocks();
		}
		return numRSBlocks;//code.getNumRSBlocks();
	}

	/**
	 * Returns the {@link Mode} in which data is encoded after the last
	 * invocation of {@link #generateCode()}.
	 *
	 * @return the {@link Mode} in which data is encoded after the last
	 * invocation of {@link #generateCode()}. May be null if generateCode()
	 * hasn't been called yet or after {@link #resetState()} has been called.
	 */
	public Mode getMode() {
		return code == null ? null : code.getMode();
	}
}
