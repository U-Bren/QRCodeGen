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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.SwingPropertyChangeSupport;
import qrcodegen.tools.TriState;

/**
 *
 * @author Stefan Ganzer
 */
public class ClipboardService {

	public static final String AVAILABLE_AS_STRING_FLAVOR_PROPERTY = "AvailableAsStringFlavor";
	private final PropertyChangeSupport pcs = new SwingPropertyChangeSupport(this);
	private final Clipboard clipboard;
	private final Logger logger;
	private volatile Exception lastException;
	private volatile String contentAsString;
	private TriState isAvailableAsStringFlavor;
	private TriState clearedClipboardSuccessfully;

	public ClipboardService(Clipboard clipboard, Logger logger) {
		if (clipboard == null) {
			throw new NullPointerException();
		}
		if (logger == null) {
			throw new NullPointerException();
		}
		this.clipboard = clipboard;
		this.logger = logger;
		this.clipboard.addFlavorListener(new ClipboardFlavorListener());
		isAvailableAsStringFlavor = TriState.NOT_APPLICABLE;
		clearedClipboardSuccessfully = TriState.NOT_APPLICABLE;
	}

	public void acquireContentsAsString() {
		lastException = null;
		contentAsString = null;

		try {
			getStringContentFromClipboard();
		} catch (IllegalStateException ise) {
			logger.log(Level.SEVERE, null, ise);
		} catch (UnsupportedFlavorException ex) {
			lastException = ex;
			logger.log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			lastException = ex;
			logger.log(Level.SEVERE, null, ex);
		}
	}

	// Work in progress
	@Deprecated
	public void acquireContentAsStringInBackground() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				lastException = null;
				contentAsString = null;

				final int limitInMillis = 2000;
				int sleepTimeInMillis = 500;
				while (sleepTimeInMillis < limitInMillis) {
					try {
						getStringContentFromClipboard();
					} catch (IllegalStateException ise) {
						logger.throwing("ClipboardService", "acquire()", ise);
						try {
							Thread.sleep(sleepTimeInMillis);
						} catch (InterruptedException ie) {
							logger.throwing("ClipboardService", "acquire()", ie);
							Thread.currentThread().interrupt();
							return;
						}
						sleepTimeInMillis = sleepTimeInMillis + sleepTimeInMillis;
					} catch (UnsupportedFlavorException ufe) {
						logger.log(Level.SEVERE, null, ufe);
						sleepTimeInMillis = limitInMillis;
					} catch (IOException ioe) {
						logger.throwing("ClipboardService", "acquire()", ioe);
						sleepTimeInMillis = limitInMillis;
					}
				}
			}
		}).start();
	}

	public void clearClipboard() {
		try {
			clipboard.setContents(new StringSelection(null), null);
			clearedClipboardSuccessfully = TriState.TRUE;
		} catch (IllegalStateException ise) {
			logger.throwing("ClipboardService", "clearClipboard()", ise);
			clearedClipboardSuccessfully = TriState.FALSE;
		}
	}

	public TriState clearedClipboardSuccessfully() {
		return clearedClipboardSuccessfully;
	}

	public String getContentsAsString() {
		return contentAsString;
	}

	public boolean threwException() {
		return lastException != null;
	}

	public Exception getLastException() {
		return lastException;
	}

	private void setStringFlavorState(TriState state) {
		assert state != null;
		TriState oldState = isAvailableAsStringFlavor;
		isAvailableAsStringFlavor = state;
		pcs.firePropertyChange(AVAILABLE_AS_STRING_FLAVOR_PROPERTY, oldState, state);
	}

	private void getStringContentFromClipboard() throws UnsupportedFlavorException, IOException {
		final DataFlavor stringFlavor = DataFlavor.stringFlavor;

		Transferable contents = clipboard.getContents(null);
		boolean hasTransferableContents = contents != null && contents.isDataFlavorSupported(stringFlavor);
		if (hasTransferableContents) {
			contentAsString = (String) contents.getTransferData(stringFlavor);
		}
	}

	private class ClipboardFlavorListener implements FlavorListener {

		@Override
		public void flavorsChanged(FlavorEvent e) {

			new Thread(new Runnable() {
				@Override
				public void run() {
					final int limitInMillis = 2000;
					int sleepTimeInMillis = 500;
					boolean obtainedDataFlavor = false;

					while (sleepTimeInMillis < limitInMillis) {
						try {
							setStringFlavorState(TriState.fromBoolean(clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)));
							obtainedDataFlavor = true;
							sleepTimeInMillis = limitInMillis;
						} catch (IllegalStateException ise) {
							logger.throwing("ClipboardService.ClipboardFlavorListener", "run()", ise);
							try {
								Thread.sleep(sleepTimeInMillis);
							} catch (InterruptedException ex) {
								logger.throwing("ClipboardService.ClipboardFlavorListener", "run()", ise);
								Thread.currentThread().interrupt();
								return;
							}
							sleepTimeInMillis = sleepTimeInMillis + sleepTimeInMillis;
						}
					}
					if (!obtainedDataFlavor) {
						setStringFlavorState(TriState.NOT_APPLICABLE);
					}
				}
			}).start();
		}
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}
}
