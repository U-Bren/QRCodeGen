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
package qrcodegen.modules;

import java.awt.Dimension;
import java.util.ResourceBundle;

/**
 *
 * @author Stefan Ganzer
 */
/**
 * Determines whether and in which way a FoldingCardPrinter is scaled while
 * printing.
 */
enum ScaleToPaperSize {

	NONE(ResourceBundle.getBundle("qrcodegen/modules/ScaleToPaperSize").getString("NONE")) {
		@Override
		public double getScaleFactor(Dimension from, Dimension to) {
			return 1.0;
		}
		@Override
		public double getScaleFactor(double fromWidth, double fromHeight, double toWidth, double toHeight) {
			return 1.0;
		}
	}
	,
	DOWN(ResourceBundle.getBundle("qrcodegen/modules/ScaleToPaperSize").getString("SCALE DOWN ONLY")) {
		@Override
		public double getScaleFactor(Dimension from, Dimension to) {
			return getScaleFactor(from.width, from.height, to.width, to.height);
		}
		@Override
		public double getScaleFactor(double fromWidth, double fromHeight, double toWidth, double toHeight) {
			double ratioX = toWidth / fromWidth;
			double ratioY = toHeight / fromHeight;

			double scaleX = Math.min(1.0, ratioX);
			double scaleY = Math.min(1.0, ratioY);
		
			return Math.min(scaleX, scaleY);
		}
	},
	UP(ResourceBundle.getBundle("qrcodegen/modules/ScaleToPaperSize").getString("SCALE UP ONLY")) {
		@Override
		public double getScaleFactor(Dimension from, Dimension to) {
			return getScaleFactor(from.width, from.height, to.width, to.height);
		}
		@Override
		public double getScaleFactor(double fromWidth, double fromHeight, double toWidth, double toHeight) {
			double ratioX = toWidth / fromWidth;
			double ratioY = toHeight / fromHeight;

			double scaleX = Math.max(1.0, ratioX);
			double scaleY = Math.max(1.0, ratioY);

			return Math.min(scaleX, scaleY);
		}
	},
	FIT(ResourceBundle.getBundle("qrcodegen/modules/ScaleToPaperSize").getString("FIT TO SIZE")) {
		@Override
		public double getScaleFactor(Dimension from, Dimension to) {
			return getScaleFactor(from.width, from.height, to.width, to.height);
		}
		@Override
		public double getScaleFactor(double fromWidth, double fromHeight, double toWidth, double toHeight) {
			double ratioX = toWidth / fromWidth;
			double ratioY = toHeight / fromHeight;

			double scaleX = ratioX;
			double scaleY = ratioY;

			return Math.min(scaleX, scaleY);
		}
	};
	private final String name;

	private ScaleToPaperSize(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public abstract double getScaleFactor(Dimension from, Dimension to);
	
	public abstract double getScaleFactor(double fromWidth, double fromHeigth, double toWidth, double toHeight);
}
