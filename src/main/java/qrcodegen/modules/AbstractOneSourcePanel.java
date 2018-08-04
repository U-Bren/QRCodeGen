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
package qrcodegen.modules;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import qrcodegen.ContentModule;
import qrcodegen.documentfilter.DocumentNewLineFilter;
import qrcodegen.documentfilter.DocumentSizeFilter;
import qrcodegen.tools.StaticTools;

/**
 * An abstract class suitable for creating content modules whose input data is a
 * single line of text.
 *
 * @author Stefan Ganzer
 */
public abstract class AbstractOneSourcePanel extends javax.swing.JPanel implements ContentModule {

	private static final ResourceBundle res = ResourceBundle.getBundle("qrcodegen/modules/AbstractOneSourcePanel");
	static final String EMPTY_STRING = "";
	private boolean validInput = false;
	private boolean invalidatedInputOnce = true;
	private transient ChangeEvent changeEvent;

	/**
	 * Constructs an AbstractOneSourcePanel that doesn't limit the number of
	 * characters that can be entered into the sourceField.
	 *
	 */
	public AbstractOneSourcePanel() {
		initComponents();
		initSourceField(-1);
		initGenerateButton();
	}

	/**
	 * Constructs an AbstractOneSourcePanel that limits the number of characters
	 * that can be entered into the sourceField.
	 *
	 * @param maxChars the maximum the number of characters that can be entered
	 * into the sourceField
	 */
	public AbstractOneSourcePanel(int maxChars) {
		if (maxChars < -1) {
			throw new IllegalArgumentException("maxChars must be >= -1, but is: "
					+ maxChars);
		}
		initComponents();
		initSourceField(maxChars);
		initGenerateButton();
	}

	private void initSourceField(int maxChars) {
		assert maxChars >= -1 : maxChars;
		sourceLabel.setLabelFor(sourceField);
		AbstractDocument ad = (AbstractDocument) sourceField.getDocument();
		ad.addDocumentListener(new InputListener());
		if (maxChars == -1) {
			ad.setDocumentFilter(new DocumentNewLineFilter());
		} else {
			ad.setDocumentFilter(new DocumentNewLineFilter(new DocumentSizeFilter(maxChars)));
		}
	}

	private void initGenerateButton() {
		generateButton.setMnemonic(getKeyCodeFromResourceBundle("GENERATE_BUTTON_MNEMONIC"));
		generateButton.addActionListener(new GenerateListener());
	}

	@Override
	public String getContent() {
		if (isValidInput()) {
			return getEncodedContent();
		} else {
			return EMPTY_STRING;
		}
	}

	@Override
	public Component getComponent() {
		return this;
	}

	abstract String getEncodedContent();
	
	@Override
	public boolean restrictsEncoding(){
		return false;
	}
	
	@Override
	public Set<Charset> getEncodingSubset(){
		return null;
	}

	/**
	 * Returns true if the content of {@link #sourceField} is valid, false if it
	 * is invalid or it hasn't been checked yet.
	 *
	 * @return true if the content of {@link #sourceField} is valid
	 */
	boolean isValidInput() {
		return validInput;
	}

	/**
	 * Set validInput to true if the content of {@link #sourceField} is valid,
	 * false if it invalid or it has been changed but not been checked yet.
	 *
	 * @param validInput true if the content of {@link #sourceField} is valid,
	 * false otherwise
	 */
	void setValidInput(boolean validInput) {
		this.validInput = validInput;
	}

	/**
	 * Returns true if the content of {@link #sourceField} has been changed and
	 * not been checked yet (so isValid() returns false), or it has been checked
	 * and found to be invalid. This is can be used to prevent firing multiple
	 * change events.
	 *
	 * @return true if the content of {@link #sourceField} has been changed and
	 * not been checked yet (so isValid() returns false), or it has been checked
	 * and found to be invalid
	 * @see #setInvalidatedInputOnce(boolean)
	 */
	boolean isInvalidatedInputOnce() {
		return invalidatedInputOnce;
	}

	/**
	 * Set invalidatedInputOnce to true if the content of {@link #sourceField}
	 * has changed but not been checked yet, set to false if the content has
	 * been checked and found to be valid.
	 *
	 * @param invalidatedInputOnce true if the content of {@link #sourceField}
	 * has changed but not been checked yet, false if the content has been
	 * checked and found to be valid.
	 * @see #isInvalidatedInputOnce()
	 */
	void setInvalidatedInputOnce(boolean invalidatedInputOnce) {
		this.invalidatedInputOnce = invalidatedInputOnce;
	}

	void generate() {
		generateButton.doClick();
	}

	private static int getKeyCodeFromResourceBundle(String key) {
		assert key != null;
		return StaticTools.getKeyCodeForString(res.getString(key));
	}

	private class GenerateListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (isValidInput()) {
				fireContentChanged();
			} else {
				sourceField.selectAll();
			}
			sourceField.requestFocusInWindow();
		}
	}

	@Override
	public synchronized void addChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to register listener twice: " + listener);
		}
		listenerList.add(ChangeListener.class, listener);
	}

	@Override
	public synchronized void removeChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (!Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to remove unregistered listener: " + listener);
		}
		listenerList.remove(ChangeListener.class, listener);
	}

	private void fireContentChanged() {
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

	private class InputListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			doAction();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			doAction();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			doAction();
		}

		private void doAction() {
			statusField.setText(EMPTY_STRING);
			setValidInput(false);
			if (!isInvalidatedInputOnce()) {
				setInvalidatedInputOnce(true);
				fireContentChanged();
			}
		}
	}

	/** This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourceLabel.setText(res.getString("AbstractOneSourcePanel.sourceLabel.text")); // NOI18N

        generateButton.setText(res.getString("AbstractOneSourcePanel.generateButton.text")); // NOI18N

        statusField.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sourceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sourceField, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addComponent(statusField, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(generateButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sourceLabel)
                    .addComponent(generateButton)
                    .addComponent(sourceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton generateButton = new javax.swing.JButton();
    final javax.swing.JTextField sourceField = new javax.swing.JTextField();
    final javax.swing.JLabel sourceLabel = new javax.swing.JLabel();
    final javax.swing.JTextField statusField = new javax.swing.JTextField();
    // End of variables declaration//GEN-END:variables
}
