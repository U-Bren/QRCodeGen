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
import com.google.zxing.qrcode.encoder.QRCode;
import java.beans.PropertyChangeListener;
import qrcodegen.CodeSizeException;
import qrcodegen.tools.ImmutableDimension;
import qrcodegen.tools.TriState;

/**
 *
 * @author Stefan Ganzer
 */
public interface Renderable {

	public static final String REQUESTED_DIMENSION_PROPERTY = "RequestedDimension";
	public static final String ACTUAL_DIMENSION_PROPERTY = "ActualDimension";
	public static final String MODULE_SIZE_PROPERTY = "ModuleSize";
	public static final String RESULT_STATE_PROPERTY = "ResultState";
	public static final String EXCEEDS_REQUESTED_DIMENSION_PROPERTY = "ExceedsRequestedDimension";

	void renderResult(QRCode code) throws CodeSizeException;

	boolean hasResult();
	
	BitMatrix getResult();

	void setRequestedDimension(ImmutableDimension dim);

	ImmutableDimension getRequestedDimension();

	void setModuleSize(int size);

	int getModuleSize();

	ImmutableDimension getActualDimension();
	
	TriState exceedsRequestedDimension();
	
	void resetState();

	void addPropertyChangeListener(PropertyChangeListener listener);

	void removePropertyChangeListener(PropertyChangeListener listener);
}
