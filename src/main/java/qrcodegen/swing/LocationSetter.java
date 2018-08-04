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
package qrcodegen.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;

/**
 *
 * @author Stefan Ganzer
 */
public class LocationSetter {

	private final Component component;
	private final Toolkit toolkit;

	public LocationSetter(Toolkit toolkit, Component c) {
		if(toolkit == null){
			throw new NullPointerException();
		}
		if (c == null) {
			throw new NullPointerException();
		}
		this.toolkit = toolkit;
		this.component = c;
	}

	public void setLocationNextTo(Component otherComponent, int maxFraction) {
		if (otherComponent == null) {
			throw new NullPointerException();
		}
		if (maxFraction < 1) {
			throw new IllegalArgumentException("fraction must be >= 1, but is: " + maxFraction);
		}
		HorizontalBorders screenBorders = getHorizontalScreenBorders();

		int newXPosition;
		int newYPosition = otherComponent.getY();

		int thisWidth = component.getWidth();
		int componentsRightCorner = otherComponent.getX() + otherComponent.getWidth();
		int componentsLeftCorner = otherComponent.getX();

		if (componentsRightCorner + thisWidth <= screenBorders.getRightBorder()) {
			newXPosition = componentsRightCorner;
		} else if (componentsLeftCorner - thisWidth >= screenBorders.getLeftBorder()) {
			newXPosition = componentsLeftCorner - thisWidth;
		} else {
			newXPosition = Math.max(
					componentsRightCorner - otherComponent.getWidth() / maxFraction,
					screenBorders.getRightBorder() - thisWidth);
		}
		component.setLocation(newXPosition, newYPosition);
	}

	private HorizontalBorders getHorizontalScreenBorders() {
		final Dimension screenSize = toolkit.getScreenSize();
		final Insets insets = toolkit.getScreenInsets(component.getGraphicsConfiguration());
		final int left = insets.left;
		final int right = screenSize.width - insets.right;
		return new HorizontalBorders(left, right);
	}

	private static class HorizontalBorders {

		private final int leftBorder;
		private final int rightBorder;

		private HorizontalBorders(int left, int right) {
			this.leftBorder = left;
			this.rightBorder = right;
		}

		int getLeftBorder() {
			return leftBorder;
		}

		int getRightBorder() {
			return rightBorder;
		}

		@Override
		public String toString() {
			return "left border: " + leftBorder + ", right border: " + rightBorder;
		}
	}
}
