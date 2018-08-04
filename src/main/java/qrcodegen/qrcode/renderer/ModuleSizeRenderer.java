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
package qrcodegen.qrcode.renderer;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;
import qrcodegen.CodeSizeException;
import qrcodegen.tools.ImmutableDimension;
import qrcodegen.tools.TriState;

/**
 *
 * @author Stefan Ganzer
 */
public class ModuleSizeRenderer extends AbstractRenderer {

	private int moduleSize;
	private ImmutableDimension actualDimension;
	private BitMatrix result;

	public ModuleSizeRenderer(ImmutableDimension sizeLimit) {
		super(sizeLimit);
	}

	public ModuleSizeRenderer() {
	}

	/*
	 * The following code is based on code from com.google.zxing.qrcode.QRCodeWriter:
	 *
	 * Copyright 2008 ZXing authors
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License"); you may
	 * not use this file except in compliance with the License. You may obtain a
	 * copy of the License at
	 *
	 * http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations
	 * under the License.
	 *
	 */
	@Override
	public void renderResult(QRCode code) throws CodeSizeException {
		ByteMatrix input = code.getMatrix();
		if (input == null) {
			throw new IllegalStateException();
		}
		int inputWidth = input.getWidth();
		int inputHeight = input.getHeight();
		int qrWidth = inputWidth + (QUIET_ZONE_SIZE << 1);
		int qrHeight = inputHeight + (QUIET_ZONE_SIZE << 1);

		ImmutableDimension newActualDimension = new ImmutableDimension(qrWidth * moduleSize, qrHeight * moduleSize);
		if (exceedsMaxImageDimension(newActualDimension)) {
			resetState();
			throw new CodeSizeException("Image dimensions exceed max. dimensions", null, getMaxImageDimension(), newActualDimension);
		} else {
			setActualDimension(newActualDimension);
		}

		// Padding includes both the quiet zone and the extra white pixels to accommodate the requested
		// dimensions. For example, if input is 25x25 the QR will be 33x33 including the quiet zone.
		// If the requested size is 200x160, the multiple will be 4, for a QR of 132x132. These will
		// handle all the padding from 100x100 (the actual QR) up to 200x160.
		int leftPadding = (actualDimension.getWidth() - (inputWidth * moduleSize)) / 2;
		int topPadding = (actualDimension.getHeight() - (inputHeight * moduleSize)) / 2;

		BitMatrix output = new BitMatrix(actualDimension.getWidth(), actualDimension.getHeight());

		for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += moduleSize) {
			// Write the contents of this row of the barcode
			for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += moduleSize) {
				if (input.get(inputX, inputY) == 1) {
					output.setRegion(outputX, outputY, moduleSize, moduleSize);
				}
			}
		}
		setResult(output);
	}

	@Override
	public BitMatrix getResult() {
		return result;
	}
	
	@Override
	public boolean hasResult(){
		return result != null;
	}

	private void setResult(BitMatrix matrix) {
		BitMatrix oldMatrix = this.result;
		this.result = matrix;
		pcs.firePropertyChange(RESULT_STATE_PROPERTY, oldMatrix, result);
	}

	@Override
	public void setRequestedDimension(ImmutableDimension dim) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ImmutableDimension getRequestedDimension() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	/**
	 * Sets the module size in pixels.
	 *
	 * @param size the module size in pixel. Size must be &gt;= 0.
	 *
	 * @throws IllegalArgumentException if size &lt; 1
	 * @throws IllegalStateException if modus != Modus.MODULE_SIZE
	 */
	public void setModuleSize(int size) {
		setModuleSizeValue(size, true);
	}

	private void setModuleSizeValue(int size, boolean resetState) {
		if (size < 1) {
			throw new IllegalArgumentException(Integer.toString(size));
		}
		int oldSize = moduleSize;
		this.moduleSize = size;
		if (oldSize != size) {
			if (resetState) {
				resetState();
			}
			pcs.firePropertyChange(MODULE_SIZE_PROPERTY, oldSize, size);
		}
	}

	@Override
	public int getModuleSize() {
		return moduleSize;
	}

	/**
	 * Sets the actual dimension.
	 *
	 * @param dim the actual dimension
	 */
	private void setActualDimension(ImmutableDimension dim) {
		ImmutableDimension oldActualDimension = actualDimension;
		actualDimension = dim;
		pcs.firePropertyChange(ACTUAL_DIMENSION_PROPERTY, oldActualDimension, actualDimension);
	}

	@Override
	public ImmutableDimension getActualDimension() {
		return actualDimension;
	}

	/**
	 * Returns {@link TriState TRUE} if the actual dimension of the symbol
	 * exceeds the requested dimension, {@link TriState FALSE} if the actual
	 * dimension is lower or equal to the requested dimension, or
	 * {@link TriState NOT_APPLICABLE} if modus is MODULE_SIZE.
	 *
	 * @return
	 */
	@Override
	public TriState exceedsRequestedDimension() {
		return TriState.NOT_APPLICABLE;
	}

	@Override
	public void resetState() {
		setActualDimension(null);
		setResult(null);
	}
}
