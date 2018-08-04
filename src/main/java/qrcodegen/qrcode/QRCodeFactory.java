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
package qrcodegen.qrcode;

import com.google.zxing.qrcode.encoder.QRCode;

/**
 *
 * @author Stefan Ganzer
 */
public class QRCodeFactory {

	private static final NullQRCode NULL_INSTANCE = new NullQRCode();

	public static QRCodeInterface getNullInstance() {
		return NULL_INSTANCE;
	}

	public static QRCodeInterface getInstance(QRCode code) {
		QRCodeInterface instance;
		if (code == null) {
			instance = NULL_INSTANCE;
		} else {
			instance = new QRCodeAdapter(code);
		}
		return instance;
	}

	private QRCodeFactory() {
	}
}
