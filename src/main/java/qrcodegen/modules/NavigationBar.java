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
package qrcodegen.modules;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * A component that allows the sequential navigation through a list, and that
 * allows to jump to a specific entry by entering its number into field. It
 * prevents the user from entering non-number values or values that are not
 * within the set limits.
 *
 * @author Stefan Ganzer
 */
public class NavigationBar extends JPanel implements ChangeListener, PropertyChangeListener {

	private static final ResourceBundle res = ResourceBundle.getBundle("qrcodegen/modules/NavigationBar");
	private static final long serialVersionUID = 1L;
	private final BoundedRangeModel rangeModel = new DefaultBoundedRangeModel();
	private final NumberFormatter formatter = new NumberFormatter(new DecimalFormat("#0"));
	private transient ChangeEvent changeEvent;

	/**
	 * Constructs a new NavigationBar.
	 */
	public NavigationBar() {
		initComponents();

		rangeModel.setRangeProperties(0, 0, 0, 0, true);
		rangeModel.addChangeListener(this);

		formattedTextField.setFormatterFactory(new DefaultFormatterFactory(formatter));
		formattedTextField.addPropertyChangeListener("value", this);

		downButton.setMnemonic(KeyEvent.VK_LEFT);
		upButton.setMnemonic(KeyEvent.VK_RIGHT);
		downButton.addActionListener(new DownButtonListener());
		upButton.addActionListener(new UpButtonListener());
	}

	/**
	 * Sets the lower limit of this navigation bar.
	 *
	 * @param value
	 */
	public void setMinimum(int value) {
		rangeModel.setMinimum(value);
	}

	/**
	 * Returns the lower limit of this navigation bar.
	 *
	 * @return
	 */
	public int getMinumum() {
		return rangeModel.getMinimum();
	}

	/**
	 * Sets the upper limit of this navigation bar.
	 *
	 * @param value
	 */
	public void setMaximum(int value) {
		rangeModel.setMaximum(value);
	}

	/**
	 * Returns the upper limit of this navigation bar.
	 *
	 * @return
	 */
	public int getMaximum() {
		return rangeModel.getMaximum();
	}

	/**
	 * Sets the current value.
	 *
	 * @param value
	 */
	public void setValue(int value) {
		rangeModel.setValue(value);
	}

	/**
	 * Returns the current value.
	 *
	 * @return
	 */
	public int getValue() {
		return rangeModel.getValue();
	}

	/**
	 * Sets the current value, the lower and the upper limit at the same time.
	 * Use this method if you want only one PropertyChangeEvent to be fired.
	 *
	 * @param value
	 * @param minimum
	 * @param maximum
	 */
	public void setRange(int value, int minimum, int maximum) {
		rangeModel.setRangeProperties(value, 0, minimum, maximum, true);
	}

	private void updateUIState() {
		lowerLimitLabel.setText(String.format("[%1$d]", rangeModel.getMinimum()));
		upperLimitLabel.setText(String.format("[%1$d]", rangeModel.getMaximum()));

		downButton.setEnabled(canGoDown());
		upButton.setEnabled(canGoUp());
	}

	private boolean canGoUp() {
		return rangeModel.getValue() < rangeModel.getMaximum();
	}

	private boolean canGoDown() {
		return rangeModel.getValue() > rangeModel.getMinimum();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		formattedTextField.setValue(rangeModel.getValue());
		updateUIState();
		fireStateChanged();
	}

	private class DownButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			setValue(rangeModel.getValue() - 1);
		}
	}

	private class UpButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			setValue(rangeModel.getValue() + 1);
		}
	}

	public synchronized void addChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to register listener twice: " + listener);
		}
		listenerList.add(ChangeListener.class, listener);
	}

	public synchronized void removeChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (!Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to remove unregistered listener: " + listener);
		}
		listenerList.remove(ChangeListener.class, listener);
	}

	private void fireStateChanged() {
		final Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event:
				// Lazily create the event:
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() instanceof JFormattedTextField) {
			JFormattedTextField source = (JFormattedTextField) evt.getSource();
			if ("value".equals(evt.getPropertyName())) {
				Number number = (Number) source.getValue();
				if (number != null) {
					if (number.intValue() < rangeModel.getMinimum()) {
						setOnError(source, rangeModel.getMinimum());
					} else if (number.intValue() > rangeModel.getMaximum()) {
						setOnError(source, rangeModel.getMaximum());
					} else {
						setValue(number.intValue());
					}
				}
			} else {
				throw new AssertionError(evt.getPropertyName());
			}
		} else {
			throw new AssertionError(evt.getSource());
		}
	}

	private void setOnError(JFormattedTextField field, int value) {
		//we need to set the textfield manually as we don't know if
		//the rangeModel will fire a changeEvent
		setValue(value);
		field.setValue(value);
		Toolkit.getDefaultToolkit().beep();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lowerLimitLabel = new javax.swing.JLabel();
        downButton = new BasicArrowButton(SwingConstants.WEST);
        formattedTextField = new javax.swing.JFormattedTextField();
        upButton = new BasicArrowButton(SwingConstants.EAST);
        upperLimitLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lowerLimitLabel.setText(res.getString("NavigationBar.lowerLimitLabel.text")); // NOI18N
        add(lowerLimitLabel);

        downButton.setToolTipText(res.getString("NavigationBar.downButton.toolTipText")); // NOI18N
        downButton.setEnabled(false);
        add(downButton);

        formattedTextField.setColumns(3);
        formattedTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        formattedTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        add(formattedTextField);

        upButton.setToolTipText(res.getString("NavigationBar.upButton.toolTipText")); // NOI18N
        upButton.setEnabled(false);
        add(upButton);

        upperLimitLabel.setText(res.getString("NavigationBar.upperLimitLabel.text")); // NOI18N
        add(upperLimitLabel);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton downButton;
    private javax.swing.JFormattedTextField formattedTextField;
    private javax.swing.JLabel lowerLimitLabel;
    private javax.swing.JButton upButton;
    private javax.swing.JLabel upperLimitLabel;
    // End of variables declaration//GEN-END:variables
}
