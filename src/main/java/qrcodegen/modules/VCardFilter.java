/*
 Copyright 2011, 2012 Stefan Ganzer

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
/*
 * $Revision$
 */
package qrcodegen.modules;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.SwingPropertyChangeSupport;

/**
 * This class allows to remove (filter) parts from VCards. This class is not
 * thread safe.
 *
 * @see Filter
 *
 * @author Stefan Ganzer
 */
class VCardFilter {

	/** The property name of this classes state property . */
	public static final String STATE = "state";
	/** The property name of this classes filter property . */
	public static final String FILTER = "filter";
	/** The property name of this classes card property . */
	public static final String CARD = "card";
	private static final String EMPTY_STRING = "";
	private final EventListenerList listenerList = new EventListenerList();
	private final PropertyChangeSupport pcs = new SwingPropertyChangeSupport(this, true);
	private String originalCard = null;
	private String filteredCard = null;
	private Set<Filter> filter = EnumSet.noneOf(Filter.class);
	private Set<Filter> found = EnumSet.noneOf(Filter.class);
	/** True if this filter is active, false otherwise. */
	private boolean isActive = true;
	private transient ChangeEvent changeEvent;

	/**
	 * Sets a card. If the new card is dif
	 *
	 * @param card
	 */
	public void setCard(String card) {
		if (card == null) {
			throw new NullPointerException();
		}
		String oldCard = originalCard;
		this.originalCard = card;
		if (!originalCard.equals(oldCard)) {// oldCard may be null
			resetResults();
			pcs.firePropertyChange(CARD, oldCard, originalCard);
		}
	}

	/**
	 * Returns the original card.
	 *
	 * @return the original card. Never returns null.
	 * @throws IllegalStateException if no card is set
	 * @see #setCard(java.lang.String)
	 * @see #isCardSet()
	 */
	public String getCard() {
		if (originalCard == null) {
			throw new IllegalStateException("No card is set");
		}
		return originalCard;
	}

	/**
	 * Returns true is a card has been set, false otherwise.
	 *
	 * @return true is a card has been set, false otherwise
	 */
	boolean isCardSet() {
		return originalCard != null;
	}

	/**
	 * Enables or disables this whole filter, without affecting the set filter
	 * modules. Activating or deactivating an already active or inactive filter
	 * has no result.
	 *
	 * @param newState true to activate this filter, false to deactive it
	 */
	void setIsActive(boolean newState) {
		boolean oldState = isActive;
		this.isActive = newState;
		if (oldState != newState) {
			resetResults();
			pcs.firePropertyChange(STATE, oldState, isActive);
		}
	}

	/**
	 * Returns true if this VCardFilter is active, false otherwise. If true, all
	 * set filters are applied when processCard() is invoked. If false, invoking
	 * getFilteredResult() after processCard() will return the original card.
	 *
	 * @return
	 */
	boolean isActive() {
		return isActive;
	}

	/**
	 *
	 * @param filter
	 * @throws NullPointerException if the given filter is null
	 */
	void setFilters(Set<Filter> filter) {
		if (filter == null) {
			throw new NullPointerException();
		}
		Set<Filter> oldFilter = this.filter;
		this.filter = filter.isEmpty() ? EnumSet.noneOf(Filter.class) : EnumSet.copyOf(filter);
		if (!oldFilter.equals(this.filter)) {
			resetResults();
			pcs.firePropertyChange(FILTER, null, null);
		}
	}

	/**
	 * Returns the filter set.
	 *
	 * The caller is free to change the returned set without affecting this
	 * VCardFilter.
	 *
	 * @return the filter set. Never returns null.
	 */
	Set<Filter> getFilters() {
		return EnumSet.copyOf(filter);
	}

	/**
	 *
	 * @param filter
	 * @throws NullPointerException if the given filter is null
	 */
	void setFilter(Filter filter) {
		if (filter == null) {
			throw new NullPointerException();
		}
		Set<Filter> oldFilter = this.filter;
		this.filter = EnumSet.of(filter);
		if (!oldFilter.equals(this.filter)) {
			resetResults();
			pcs.firePropertyChange(FILTER, null, null);
		}
	}

	/**
	 *
	 * @param filter
	 * @throws NullPointerException if the given filter is null
	 */
	void addFilter(Filter filter) {
		// implicit null check
		if (isFilterSet(filter)) {
			throw new IllegalArgumentException(filter + " is already set.");
		}
		this.filter.add(filter);
		resetResults();
		pcs.firePropertyChange(FILTER, null, null);
	}

	public void removeFilters() {
		Set<Filter> oldFilter = this.filter;
		this.filter = EnumSet.noneOf(Filter.class);
		if (!oldFilter.equals(this.filter)) {
			resetResults();
			pcs.firePropertyChange(FILTER, null, null);
		}
	}

	/**
	 *
	 * @param filter
	 * @throws NullPointerException if the given filter is null
	 */
	void removeFilter(Filter filter) {
		// implicit null check
		if (!isFilterSet(filter)) {
			throw new IllegalArgumentException();
		}
		this.filter.remove(filter);
		resetResults();
		pcs.firePropertyChange(FILTER, null, null);
	}

	/**
	 *
	 * @param filter
	 * @return
	 * @throws NullPointerException if the given filter is null
	 */
	boolean isFilterSet(Filter filter) {
		if (filter == null) {
			throw new NullPointerException();
		}
		return this.filter.contains(filter);
	}

	public void processCard() {
		if (originalCard == null) {
			throw new IllegalStateException("No card is set");
		}
		if (isActive) {
			filteredCard = filterText(originalCard);
		} else {
			filteredCard = originalCard;
		}
		// signal all listener that we have processed the input,
		// regardless of the result
		fireFilteredCardChanged();
	}

	public boolean hasFilteredResult() {
		return filteredCard != null;
	}

	public String getFilteredResult() {
		if (filteredCard == null) {
			throw new IllegalStateException();
		}
		return filteredCard;
	}

	Set<Filter> getFoundFilter() {
		return EnumSet.copyOf(found);
	}

	/**
	 * @throws NullPointerException if the given String is null
	 */
	private String filterText(String vCard) {
		assert vCard != null;
		StringBuilder sb = new StringBuilder(vCard);
		for (Filter f : filter) {
			boolean gotMatch = f.apply(sb);
			if (gotMatch) {
				found.add(f);
			}
		}
		return sb.toString();
	}

	/**
	 *
	 * @param listener
	 * @throws NullPointerException if listener is null
	 */
	public synchronized void addChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to register listener twice: " + listener);
		}
		listenerList.add(ChangeListener.class, listener);
	}

	/**
	 *
	 * @param listener
	 * @throws NullPointerException if listener is null
	 */
	public synchronized void removeChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (!Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to remove unregistered listener: " + listener);
		}
		listenerList.remove(ChangeListener.class, listener);
	}

	private void fireFilteredCardChanged() {
		final Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event:
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

	private void resetResults() {
		found = EnumSet.noneOf(Filter.class);
		filteredCard = null;
	}

	private void reset() {
		setCard(EMPTY_STRING);
		removeFilters();
	}
}
