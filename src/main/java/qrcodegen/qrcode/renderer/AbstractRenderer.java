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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import qrcodegen.tools.ImmutableDimension;

/**
 *
 * @author Stefan Ganzer
 */
public abstract class AbstractRenderer implements Renderable {

	static final int QUIET_ZONE_SIZE = 4;
	private final ImmutableDimension maxImageDimension;
	final PropertyChangeSupport pcs;

	public AbstractRenderer(ImmutableDimension sizeLimit) {
		if (sizeLimit == null) {
			throw new NullPointerException();
		}
		this.pcs = new PropertyChangeSupport(this);
		this.maxImageDimension = sizeLimit;
	}

	public AbstractRenderer() {
		this(new ImmutableDimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
	}

	public boolean exceedsMaxImageDimension(ImmutableDimension dim) {
		return dim.exceeds(maxImageDimension);
	}

	public ImmutableDimension getMaxImageDimension() {
		return maxImageDimension;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
}
