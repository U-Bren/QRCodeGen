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
public class QRCodeAdapter implements QRCodeInterface {

	private final QRCode code;

	public QRCodeAdapter(QRCode code) {
		if (code == null) {
			throw new NullPointerException();
		}
		this.code = code;
	}

	@Override
	public Mode getMode() {
		return code.getMode();
	}

	@Override
	public Version getVersion() {
		return code.getVersion();
	}

	@Override
	public int getVersionNumber() {
		return code.getVersion().getVersionNumber();
	}

	@Override
	public int getNumTotalBytes() {
		return code.getVersion().getTotalCodewords();
	}

	@Override
	public int getNumDataBytes() {
		return getNumTotalBytes() - getNumECBytes();
	}

	@Override
	public int getNumECBytes() {
		Version.ECBlocks ecBlocks = code.getVersion().getECBlocksForLevel(code.getECLevel());
		return ecBlocks.getTotalECCodewords();
	}

	@Override
	public int getNumRSBlocks() {
		Version.ECBlocks ecBlocks = code.getVersion().getECBlocksForLevel(code.getECLevel());
		return ecBlocks.getNumBlocks();
	}
}
