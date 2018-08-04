/*
 * Copyright (C) 2013 Stefan Ganzer
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Stefan Ganzer
 */
public class ChangeSupport {

	private final Logger logger;
	private final List<ChangeListener> listeners;
	private final ChangeEvent event;

	public ChangeSupport(Object source, Logger logger) {
		if (source == null) {
			throw new NullPointerException();
		}
		if (logger == null) {
			throw new NullPointerException();
		}
		this.listeners = new CopyOnWriteArrayList<ChangeListener>();
		this.logger = logger;
		this.event = new ChangeEvent(source);
	}

	public void fireChangeEvent() {
		fireChangeEvent(event);
	}

	public void fireChangeEvent(ChangeEvent event) {
		for (ChangeListener l : listeners) {
			try {
				l.stateChanged(event);
			} catch (RuntimeException e) {
				logger.log(Level.WARNING, "Removing listener that caused exception", e);
				removeChangeListener(l);
			}
		}
	}

	/**
	 * Adds the given change listener.
	 *
	 * @param listener a change listener. May be null.
	 */
	public void addChangeListener(ChangeListener listener) {
		if (listener == null) {
			return;
		}
		listeners.add(listener);
	}

	/**
	 * Removes the given change listener.
	 *
	 * @param listener a change listener. May be null.
	 */
	public void removeChangeListener(ChangeListener listener) {
		if (listener == null) {
			return;
		}
		listeners.remove(listener);
	}

	public boolean hasChangeListeners() {
		return !listeners.isEmpty();
	}

	public List<ChangeListener> getChangeListeners() {
		return new ArrayList<ChangeListener>(listeners);
	}
}
