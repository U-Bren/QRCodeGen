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
package qrcodegen.qrcode.renderer;

import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.QRCode;
import qrcodegen.tools.ImmutableDimension;
import qrcodegen.tools.TriState;

/**
 *
 * @author Stefan Ganzer
 */
public abstract class AbstractSizeRenderer extends AbstractRenderer {

	/** Minimum size - Version 1 = 21 modules + 8 modules for quiet zone */
	private static final int MIN_SIZE = 21 + 8;
	private ImmutableDimension requestedDimension = new ImmutableDimension(120, 120);
	/** The actual size of the code. This can be set by the user (== dimension)
	 * or calculated. */
	private ImmutableDimension actualDimension;
	private BitMatrix result = null;
	private int moduleSize = 1;
	/** True if the last invocation of generate() made the actual dimension
	 * exceed the requested dimension. NOT_APPLICABLE in Modus.MODULE_SIZE and
	 * after resetState() */
	private TriState exceedsRequestedDimension = TriState.NOT_APPLICABLE;

	public AbstractSizeRenderer(ImmutableDimension sizeLimit) {
		super(sizeLimit);
	}

	public AbstractSizeRenderer() {
		super();
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
	public void renderResult(QRCode code) {
		ByteMatrix input = code.getMatrix();
		if (input == null) {
			throw new IllegalStateException();
		}
		int inputWidth = input.getWidth();
		int inputHeight = input.getHeight();
		int qrWidth = inputWidth + (QUIET_ZONE_SIZE << 1);
		int qrHeight = inputHeight + (QUIET_ZONE_SIZE << 1);
		int multiple;

		int requestedOutputWidth = Math.max(requestedDimension.getWidth(), qrWidth);
		int requestedOutputHeight = Math.max(requestedDimension.getHeight(), qrHeight);

		multiple = Math.min(requestedOutputWidth / qrWidth, requestedOutputHeight / qrHeight);
		setModuleSizeValue(multiple, false);
		setActualDimension(calculateActualDimension(qrWidth, qrHeight, multiple, requestedOutputWidth, requestedOutputHeight));


		// Padding includes both the quiet zone and the extra white pixels to accommodate the requested
		// dimensions. For example, if input is 25x25 the QR will be 33x33 including the quiet zone.
		// If the requested size is 200x160, the multiple will be 4, for a QR of 132x132. These will
		// handle all the padding from 100x100 (the actual QR) up to 200x160.
		//int leftPadding = usePadding ? (suggestedOutputWidth - (inputWidth * multiple)) / 2 :  QUIET_ZONE_SIZE * multiple;
		//int topPadding = usePadding ? (suggestedOutputHeight - (inputHeight * multiple)) / 2 : QUIET_ZONE_SIZE * multiple;
		int leftPadding = (actualDimension.getWidth() - (inputWidth * multiple)) / 2;
		int topPadding = (actualDimension.getHeight() - (inputHeight * multiple)) / 2;

		BitMatrix output = new BitMatrix(actualDimension.getWidth(), actualDimension.getHeight());

		for (int inputY = 0, outputY = topPadding; inputY < inputHeight; inputY++, outputY += multiple) {
			// Write the contents of this row of the barcode
			for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
				if (input.get(inputX, inputY) == 1) {
					output.setRegion(outputX, outputY, multiple, multiple);
				}
			}
		}
		setResult(output);
	}

	/**
	 * Hook used in renderResult
	 *
	 * @param qrWidth
	 * @param qrHeight
	 * @param multiple
	 * @param requestedOutputWidth
	 * @param requestedOutputHeight
	 *
	 * @return
	 */
	public abstract ImmutableDimension calculateActualDimension(int qrWidth, int qrHeight, int multiple, int requestedOutputWidth, int requestedOutputHeight);

	@Override
	public BitMatrix getResult() {
		return result;
	}

	@Override
	public boolean hasResult() {
		return result != null;
	}

	/**
	 * Sets the dimension of the QR Code to create.
	 *
	 * @param d the dimension of the QR Code to create
	 *
	 * @throws NullPointerException if d is null
	 * @throws IllegalStateException if {@code getModus() == Modus.MODULE_SIZE}
	 */
	@Override
	public void setRequestedDimension(ImmutableDimension d) {
		if (d == null) {
			throw new NullPointerException();
		}
		setRequestedDimensionValue(d, true);
	}

	private void setRequestedDimensionValue(ImmutableDimension d, boolean resetState) {
		assert d != null;
		if (d.getWidth() < MIN_SIZE || d.getHeight() < MIN_SIZE) {
			throw new IllegalArgumentException(d.toString());
		}
		ImmutableDimension oldDimension = requestedDimension;
		requestedDimension = d;
		if (oldDimension == null || !oldDimension.equals(d)) {
			if (resetState) {
				resetState();
			}
			pcs.firePropertyChange(REQUESTED_DIMENSION_PROPERTY, oldDimension, d);
		}
	}

	/**
	 * Returns the dimension of the QR Code to create.
	 *
	 * @return the dimension of the QR Code to create. Returns never null.
	 */
	@Override
	public ImmutableDimension getRequestedDimension() {
		assert requestedDimension != null;
		return requestedDimension;
	}

	@Override
	public void setModuleSize(int size) {
		throw new UnsupportedOperationException("Not supported yet.");
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
		setExceedsRequestedDimension(actualDimension);
		pcs.firePropertyChange(ACTUAL_DIMENSION_PROPERTY, oldActualDimension, actualDimension);
	}

	@Override
	public TriState exceedsRequestedDimension() {
		return exceedsRequestedDimension;
	}

	private void setExceedsRequestedDimension(ImmutableDimension dim) {
		assert requestedDimension != null;
		TriState oldValue = exceedsRequestedDimension;
		if (dim == null) {
			exceedsRequestedDimension = TriState.NOT_APPLICABLE;
		} else {
			exceedsRequestedDimension = TriState.fromBoolean(dim.exceeds(requestedDimension));
		}
		pcs.firePropertyChange(EXCEEDS_REQUESTED_DIMENSION_PROPERTY, oldValue, exceedsRequestedDimension);
	}

	private void setResult(BitMatrix matrix) {
		BitMatrix oldMatrix = this.result;
		this.result = matrix;
		pcs.firePropertyChange(RESULT_STATE_PROPERTY, oldMatrix, this.result);
	}

	/**
	 * Returns the QRCodeGenerator to a state before invoking
	 * {@link #generateCode()}, i.e. invoking {@link #getImage()} after calling
	 * this method will throw an IllegalStateException.
	 */
	@Override
	public void resetState() {
		setActualDimension(null);
		setResult(null);
	}

	@Override
	public ImmutableDimension getActualDimension() {
		return actualDimension;
	}
}
