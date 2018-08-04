/**
 * Source: http://www.oracle.com/technetwork/articles/javase/index-142890.html
 */
package qrcodegen.modules.vcardgenpanel.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Stefan Ganzer
 */
public abstract class AbstractModel {

	/**
	 * Convenience class that allow others to observe changes to the model
	 * properties
	 */
	protected PropertyChangeSupport propertyChangeSupport;

	/**
	 * Default constructor. Instantiates the PropertyChangeSupport class.
	 */
	public AbstractModel() {
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * Adds a property change listener to the observer list.
	 *
	 * @param l The property change listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(l);
	}

	/**
	 * Adds a property change listener to the observer list.
	 *
	 * @param propertyName
	 * @param l The property change listener
	 */
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener l) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, l);
	}

	/**
	 * Removes a property change listener from the observer list.
	 *
	 * @param l The property change listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(l);
	}

	/**
	 * Removes a property change listener from the observer list.
	 *
	 * @param propertyName
	 * @param l The property change listener
	 */
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener l) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, l);
	}

	/**
	 * Fires an event to all registered listeners informing them that a property
	 * in this model has changed.
	 *
	 * @param propertyName The name of the property
	 * @param oldValue The previous value of the property before the change
	 * @param newValue The new property value after the change
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Fires an event to all registered listeners informing them that a property
	 * in this model has changed.
	 *
	 * @param propertyName The name of the property
	 * @param oldValue The previous value of the property before the change
	 * @param newValue The new property value after the change
	 */
	protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Fires an event to all registered listeners informing them that a property
	 * in this model has changed.
	 *
	 * @param propertyName The name of the property
	 * @param oldValue The previous value of the property before the change
	 * @param newValue The new property value after the change
	 */
	protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	/**
	 * Fires an event to all registered listeners informing them that a property
	 * in this model has changed.
	 *
	 *
	 * @param event The event to fire
	 */
	protected void firePropertyChange(PropertyChangeEvent event) {
		propertyChangeSupport.firePropertyChange(event);
	}
}
