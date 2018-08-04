/*
 Copyright 2011 Stefan Ganzer
 
 This file is part of QRCodeGen.

 QRCodeGen is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 QRCodeGen is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen;

import qrcodegen.tools.ImmutableDimension;
import java.awt.Dimension;
import java.util.ResourceBundle;

/**
 *
 * @author Stefan Ganzer
 */
public enum BarCodeSize {

	SMALL(120, 120, ResourceBundle.getBundle("qrcodegen/BarCodeSize").getString("SMALL")), MEDIUM(230, 230, ResourceBundle.getBundle("qrcodegen/BarCodeSize").getString("MEDIUM")), LARGE(350, 350, ResourceBundle.getBundle("qrcodegen/BarCodeSize").getString("LARGE"));
	private final ImmutableDimension dim;
	private final String name;

	private BarCodeSize(int width, int height, String name) {
		dim = new ImmutableDimension(width, height);
		this.name = name;
	}

	public Dimension getDimension() {
		return dim.asAwtDimension();
	}

	public ImmutableDimension getImmutableDimension() {
		return dim;
	}

	@Override
	public String toString() {
		return name;
	}
}
