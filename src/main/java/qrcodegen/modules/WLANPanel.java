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
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.Printable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatterFactory;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultFormatterFactory;
import qrcodegen.ContentModule;
import qrcodegen.PrintUtilities;
import qrcodegen.QRView;
import qrcodegen.documentfilter.ChainedDocumentFilter;
import qrcodegen.documentfilter.DocumentNewLineFilter;
import qrcodegen.documentfilter.DocumentSizeFilter;
import qrcodegen.documentfilter.RegexDocumentFilter;
import qrcodegen.tools.StaticTools;
import qrcodegen.tools.TriState;
import qrcodegen.swing.AbstractGroupFormatter;
import qrcodegen.swing.BaseFormatter;
import qrcodegen.swing.CaretPositionListener;
import qrcodegen.swing.DisplayGroupFormatter;
import qrcodegen.swing.EditGroupFormatter;

/**
 * A ContentModule implementation for creating QR Codes from wireless networks'
 * credentials.
 *
 * @author Stefan
 */
public class WLANPanel extends javax.swing.JPanel implements ContentModule {

	private static final ResourceBundle res = ResourceBundle.getBundle("qrcodegen/modules/WLANPanel");
	private static final int MNEMONIC = getKeyCodeFromResourceBundle("MNEMONIC"); //NOI18N
	/**
	 * Arbitrary limit of input length. The actual limit for WAP-keys is 63
	 * characters, for SSID 32
	 */
	private static final int MAX_LENGTH = 99;
	private static final String DEFAULT_FONT_NAME = "Serif"; //NOI18N
	private static final int DEFAULT_FONT_SIZE = 11;
	private static final VerticalAlignment DEFAULT_ALIGNMENT = VerticalAlignment.CENTER;
	private static final Rotation DEFAULT_IMAGE_ROTATION = Rotation.R180;
	private static final Rotation DEFAULT_TEXT_ROTATION = Rotation.R0;
	private static final ChainedDocumentFilter SIZE_FILTER = new DocumentNewLineFilter(new DocumentSizeFilter(MAX_LENGTH));
	private static final Pattern HEX_PATTERN = Pattern.compile("^[a-fA-F0-9]*+$"); //NOI18N
	private static final int DEFAULT_GROUP_SIZE = 4;
	private static final String NEWLINE = "\n";
	private final PrintOptionsListener printOptionsListener = new PrintOptionsListener();
	private final DefaultFormatterFactory groupFormatterFactory;
	private final DefaultFormatterFactory defaultFormatterFactory;
	private final EditGroupFormatter editGroupFormatter;
	private final BaseFormatter baseFormatter;
	private final AbstractGroupFormatter displayGroupFormatter;
	private final WirelessNetworkContent model;
	private final QRView qrView;
	private FoldingCardConfigurator fcc;
	private transient ChangeEvent changeEvent;

	/**
	 * Constructs a new WLANPanel.
	 */
	public WLANPanel(QRView qrView) {
		if (qrView == null) {
			throw new NullPointerException();
		}
		this.qrView = qrView;
		editGroupFormatter = new EditGroupFormatter(DEFAULT_GROUP_SIZE);
		editGroupFormatter.setInvalidCharacters(NEWLINE);
		editGroupFormatter.setMaxValueLength(MAX_LENGTH);
		displayGroupFormatter = new DisplayGroupFormatter(DEFAULT_GROUP_SIZE);
		groupFormatterFactory = new DefaultFormatterFactory(displayGroupFormatter, displayGroupFormatter, editGroupFormatter);

		baseFormatter = new BaseFormatter();
		baseFormatter.setCommitsOnValidEdit(true);
		baseFormatter.setAllowsInvalid(false);
		baseFormatter.setOverwriteMode(false);
		baseFormatter.setInvalidCharacters(NEWLINE);
		baseFormatter.setMaxValueLength(MAX_LENGTH);
		defaultFormatterFactory = new DefaultFormatterFactory(baseFormatter);

		initComponents();

		setName(res.getString("WIRELESS NETWORK"));
		initSSIDField();
		initPasswordField();
		initNetworkTypeComboBox();
		initPrintOptionsButton();
		initPrintAsFoldingCardCheckBox();
		initHiddenNetworkCheckBox();
		initGroupPane();

		model = new WirelessNetworkContent(ssidField.getText(), passwordField.getText(), (NetworkType) networkType.getSelectedItem());
		model.addPropertyChangeListener(new ModelListener());
		initSelectors();
	}

	private void initGroupPane() {
		new DocumentSizeFilter(1).installFilter(groupCharacterTextField);
		groupCharacterLabel.setLabelFor(groupCharacterTextField);
		groupCharacterTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String groupCharacterFieldValue = groupCharacterTextField.getText();
				char placeholder;
				if (groupCharacterFieldValue == null || groupCharacterFieldValue.isEmpty()) {
					placeholder = ' ';
				} else {
					placeholder = groupCharacterFieldValue.charAt(0);
				}
				TriState result = shouldReplacePlaceholderCharacter(placeholder);
				switch (result) {
					case TRUE:
						String oldPassword = model.getPassword();
						String newPassword = oldPassword.replace(String.valueOf(placeholder), "");
						model.setPassword(newPassword);
					//fall-through
					case NOT_APPLICABLE:
						editGroupFormatter.setPlaceholderCharacter(placeholder);
						displayGroupFormatter.setPlaceholderCharacter(placeholder);
						if (placeholder == ' ' && !"".equals(groupCharacterFieldValue)) {
							groupCharacterTextField.setText("");
						}
						break;
					case FALSE:
						restoreOldGroupCharacter();
						break;
					default:
						throw new AssertionError(result);
				}
			}
		});

		SpinnerNumberModel spinnerModel = (SpinnerNumberModel) groupSizeSpinner.getModel();
		spinnerModel.setValue(DEFAULT_GROUP_SIZE);
		groupSizeLabel.setLabelFor(groupSizeSpinner);
		groupSizeSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				SpinnerNumberModel snm = (SpinnerNumberModel) groupSizeSpinner.getModel();
				int groupSize = snm.getNumber().intValue();
				editGroupFormatter.setGroupSize(groupSize);
				displayGroupFormatter.setGroupSize(groupSize);
			}
		});

		formatPasswordCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				AbstractFormatterFactory formatterFactory;
				boolean enabled;

				if (e.getStateChange() == ItemEvent.SELECTED) {
					String groupCharacterFieldValue = groupCharacterTextField.getText();
					char placeholder;
					if (groupCharacterFieldValue == null || groupCharacterFieldValue.isEmpty()) {
						placeholder = ' ';
					} else {
						placeholder = groupCharacterFieldValue.charAt(0);
					}
					TriState result = shouldReplacePlaceholderCharacter(placeholder);
					switch (result) {
						case TRUE:
							String oldPassword = model.getPassword();
							String newPassword = oldPassword.replace(String.valueOf(placeholder), "");
							model.setPassword(newPassword);
						//fall-through
						case NOT_APPLICABLE:
							formatterFactory = groupFormatterFactory;
							enabled = true;
							break;
						case FALSE:
							formatterFactory = defaultFormatterFactory;
							enabled = false;
							break;
						default:
							throw new AssertionError(result);
					}
				} else {
					formatterFactory = defaultFormatterFactory;
					enabled = false;
				}

				passwordField.setFormatterFactory(formatterFactory);
				groupCharacterTextField.setEnabled(enabled);
				groupSizeSpinner.setEnabled(enabled);
				formatPasswordCheckBox.setSelected(enabled);
			}
		});

		boolean enabled = formatPasswordCheckBox.isSelected();
		groupCharacterTextField.setEnabled(enabled);
		groupSizeSpinner.setEnabled(enabled);
	}

	private void restoreOldGroupCharacter() {
		String oldPlaceholder = String.valueOf(editGroupFormatter.getPlaceholderCharacter());
		if (" ".equals(oldPlaceholder)) {
			oldPlaceholder = "";
		}
		if (!oldPlaceholder.equals(groupCharacterTextField.getText())) {
			groupCharacterTextField.setText(oldPlaceholder);
		}
	}

	private TriState shouldReplacePlaceholderCharacter(char c) {

		TriState result;
		
		Object passwordValue = passwordField.getValue();
		if (passwordValue == null) {
			result = TriState.NOT_APPLICABLE;
		} else {
			String passwordString = passwordValue.toString();
			if (passwordString.contains(String.valueOf(c))) {
				String message = MessageFormat.format(res.getString("REPLACE PLACEHOLDER CHARACTER QUESTION"), c, "\n");
				String title = res.getString("CONFIRM");
				int answer = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				result = answer == JOptionPane.OK_OPTION ? TriState.TRUE : TriState.FALSE;
			} else {
				result = TriState.NOT_APPLICABLE;
			}
		}
		return result;
	}

	private void initHiddenNetworkCheckBox() {
		hiddenNetworkCheckBox.setMnemonic(getKeyCodeFromResourceBundle("MNEMONIC_HIDDEN_NETWORK"));
		hiddenNetworkCheckBox.addItemListener(new HiddenNetworkListener());
	}

	private void initSelectors() {
		ssidSelector.setLabel(res.getString("SSID IS"));
		passwordSelector.setLabel(res.getString("PASSWORD IS"));

		ssidSelector.addActionListener(new SsidSelectorListener());
		passwordSelector.addActionListener(new PasswordSelectorListener());
	}

	private void initPrintAsFoldingCardCheckBox() {
		printAsFoldingCard.addItemListener(printOptionsListener);
		printAsFoldingCard.setMnemonic(getKeyCodeFromResourceBundle("FOLDING_CARD_MNEMONIC"));
	}

	private void initPrintOptionsButton() {
		printOptionsButton.addActionListener(printOptionsListener);
		printOptionsButton.setMnemonic(getKeyCodeFromResourceBundle("PRINT_OPTIONS_MNEMONIC"));
		printOptionsButton.setEnabled(false);
	}

	private void initNetworkTypeComboBox() {
		networkLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("NETWORK_LABEL_MNEMONIC"));
		networkType.setPrototypeDisplayValue(NetworkType.NO_ENCRYPTION);
		for (NetworkType nt : NetworkType.values()) {
			networkType.addItem(nt);
		}
		networkLabel.setLabelFor(networkType);
		networkType.addActionListener(new NetworkListener());
	}

	private void initPasswordField() {
		passwordLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("PASSWORD_LABEL_MNEMONIC"));
		passwordLabel.setLabelFor(passwordField);
		passwordField.setFormatterFactory(defaultFormatterFactory);
		passwordField.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		passwordField.addPropertyChangeListener(new PasswordFieldListener());
		passwordField.addMouseListener(new CaretPositionListener());
	}

	private void initSSIDField() {
		ssidLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("SSID_LABEL_MNEMONIC"));
		ssidLabel.setLabelFor(ssidField);
		AbstractDocument ad = (AbstractDocument) ssidField.getDocument();
		ad.addDocumentListener(new SSIDFieldListener());
		ad.setDocumentFilter(SIZE_FILTER);
	}

	@Override
	public String getContent() {
		if (!model.isUpToDate()) {
			model.update();
		}
		return model.getContent();
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public int getMnemonic() {
		return MNEMONIC;
	}

	@Override
	public boolean restrictsEncoding() {
		return false;
	}

	@Override
	public Set<Charset> getEncodingSubset() {
		return null;
	}

	private static int getKeyCodeFromResourceBundle(String key) {
		assert key != null;
		return StaticTools.getKeyCodeForString(res.getString(key));
	}

	private final class SSIDFieldListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			update();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			update();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			update();
		}

		private void update() {
			model.setSSID(ssidField.getText());
			ssidLengthField.setText(Integer.toString(ssidField.getText().length()));
		}
	}

	private final class PasswordFieldListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();
			if ("value".equals(propertyName)) {
				Object o = evt.getNewValue();
				if (o != null) {
					String password = o.toString();
					model.setPassword(password);
					passwordLengthField.setText(Integer.toString(password.length()));
				}
			}
		}
	}

	private final class NetworkListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			model.setNetworkType((NetworkType) networkType.getSelectedItem());
		}
	}

	private final class PrintOptionsListener implements ActionListener, ItemListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			updateFoldingCardConfigurator(qrView.getQRCodeAsImage());
			WLANPrintSettings wps = WLANPrintSettings.newInstance(null, true, fcc, qrView.getIconImages());
			wps.setVisible(true);
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();
			if (source == printAsFoldingCard) {
				printOptionsButton.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		}
	}

	private final class HiddenNetworkListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			int state = e.getStateChange();
			model.setIsHiddenNetwork(state == ItemEvent.SELECTED);
		}
	}

	private void updateFoldingCardConfigurator(Image codeImage) {
		if (fcc == null) {// create lazily a new instance
			fcc = new FoldingCardConfigurator(codeImage, DEFAULT_IMAGE_ROTATION);

			fcc.setNetworkKeyType(model.getPasswordType());
			fcc.setSSID(ssidField.getText());
			fcc.setNetworkKey(passwordField.getText());
			fcc.setNetworkType((NetworkType) networkType.getSelectedItem());
			fcc.setFont(new Font(DEFAULT_FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE));
			fcc.setVerticalAlignment(DEFAULT_ALIGNMENT);
			fcc.setTextRotation(DEFAULT_TEXT_ROTATION);

		} else {// update the existing instance
			fcc.setNetworkKeyType(model.getPasswordType());
			fcc.setSSID(ssidField.getText());
			fcc.setNetworkKey(passwordField.getText());
			fcc.setNetworkType((NetworkType) networkType.getSelectedItem());
			fcc.setImage(codeImage);
		}
	}

	@Override
	public synchronized void addChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to register listener twice: " + listener); //NOI18N
		}
		listenerList.add(ChangeListener.class, listener);
	}

	@Override
	public synchronized void removeChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (!Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to remove unregistered listener: " + listener); //NOI18N
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

	@Override
	public Printable getPrintable(final Image codeImage) {
		if (printAsFoldingCard.isSelected()) {
			updateFoldingCardConfigurator(codeImage);
			return fcc.getFoldingCardPrinter();
		} else {
			return new PrintUtilities(ImageUtilities.getImageAsJLabel(codeImage));
		}
	}

	@Override
	public String getJobName() {
		return "WiFi-Code"
				.concat(ssidField.getText() == null ? "" : " for "
				.concat(ssidField.getText())); //NOI18N
	}

	private class ModelListener implements PropertyChangeListener {

		private static final String VALID_HEX_CHARACTERS = "abcdefABCDEF0123456789";

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (WirelessNetworkContent.ACTUAL_PASSWORD_TYPE_PROPERTY.equals(propertyName)) {
				switch (model.getActualPasswordType()) {
					case HEX:
						passwordSelector.setEnabled(true);
						break;
					case STRING:
						passwordSelector.setEnabled(false);
						break;
					default:
						throw new AssertionError(model.getActualPasswordType());
				}
			} else if (WirelessNetworkContent.ACTUAL_SSID_TYPE_PROPERTY.equals(propertyName)) {
				switch (model.getActualSsidType()) {
					case HEX:
						ssidSelector.setEnabled(true);
						break;
					case STRING:
						ssidSelector.setEnabled(false);
						break;
					default:
						throw new AssertionError(model.getActualSsidType());
				}
			} else if (WirelessNetworkContent.SSID_TYPE_PROPERTY.equals(propertyName)) {
				switch (model.getSsidType()) {
					case HEX:
						new RegexDocumentFilter(HEX_PATTERN, SIZE_FILTER).installFilter(ssidField);
						break;
					case STRING:
						SIZE_FILTER.installFilter(ssidField);
						break;
					default:
						throw new AssertionError(model.getSsidType());
				}
				model.update();
			} else if (WirelessNetworkContent.PASSWORD_TYPE_PROPERTY.equals(propertyName)) {
				switch (model.getPasswordType()) {
					case HEX:
						editGroupFormatter.setValidCharacters(VALID_HEX_CHARACTERS);
						baseFormatter.setValidCharacters(VALID_HEX_CHARACTERS);
						break;
					case STRING:
						editGroupFormatter.clearValidCharacters();
						baseFormatter.clearValidCharacters();
						break;
					default:
						throw new AssertionError(model.getPasswordType());
				}
				model.update();
			} else if (WirelessNetworkContent.NETWORK_TYPE_PROPERTY.equals(propertyName)) {
				NetworkType type = model.getNetworkType();
				if (networkType.getSelectedItem() != type) {
					networkType.setSelectedItem(type);
				}
				boolean enablePasswordField = NetworkType.NO_ENCRYPTION != type;
				passwordField.setEnabled(enablePasswordField);
				model.update();
			} else if (WirelessNetworkContent.SSID_PROPERTY.equals(propertyName)) {
				String ssid = model.getSSID();
				if (!ssidField.getText().equals(ssid)) {
					ssidField.setText(ssid);
				}
				model.update();
			} else if (WirelessNetworkContent.PASSWORD_PROPERTY.equals(propertyName)) {
				String password = model.getPassword();
				if (!passwordField.getValue().toString().equals(password)) {
					passwordField.setValue(password);
				}
				model.update();
			} else if (WirelessNetworkContent.HIDDEN_NETWORK_PROPERTY.equals(propertyName)) {
				hiddenNetworkCheckBox.setSelected(model.getIsHiddenNetwork());
				model.update();
			} else if (WirelessNetworkContent.CONTENT_PROPERTY.equals(propertyName)) {
				fireContentChanged();
			}
		}
	}

	private class SsidSelectorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final String actionCommand = e.getActionCommand();

			if (AsciiHexSelector.ASCII_ACTION_COMMAND.equals(actionCommand)) {
				model.setSsidType(Type.STRING);
			} else if (AsciiHexSelector.HEX_ACTION_COMMAND.equals(actionCommand)) {
				model.setSsidType(Type.HEX);
			}
		}
	}

	private class PasswordSelectorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final String actionCommand = e.getActionCommand();

			if (AsciiHexSelector.ASCII_ACTION_COMMAND.equals(actionCommand)) {
				model.setPasswordType(Type.STRING);
			} else if (AsciiHexSelector.HEX_ACTION_COMMAND.equals(actionCommand)) {
				model.setPasswordType(Type.HEX);
			}
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        passwordLabel = new javax.swing.JLabel();
        networkLabel = new javax.swing.JLabel();
        printOptionsButton = new javax.swing.JButton();
        printAsFoldingCard = new javax.swing.JCheckBox();
        ssidLengthField = new javax.swing.JTextField();
        passwordLengthField = new javax.swing.JTextField();
        ssidSelector = new qrcodegen.modules.AsciiHexSelector();
        passwordSelector = new qrcodegen.modules.AsciiHexSelector();
        noteLabel = new javax.swing.JLabel();
        hiddenNetworkCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        formatPasswordCheckBox = new javax.swing.JCheckBox();
        groupCharacterLabel = new javax.swing.JLabel();
        groupCharacterTextField = new javax.swing.JTextField();
        groupSizeLabel = new javax.swing.JLabel();
        groupSizeSpinner = new javax.swing.JSpinner();

        ssidLabel.setText(res.getString("WLANPanel.ssidLabel.text")); // NOI18N

        passwordLabel.setText(res.getString("WLANPanel.passwordLabel.text")); // NOI18N

        passwordField.setColumns(40);

        networkLabel.setText(res.getString("WLANPanel.networkLabel.text")); // NOI18N

        ssidField.setColumns(40);

        printOptionsButton.setText(res.getString("WLANPanel.printOptionsButton.text")); // NOI18N

        printAsFoldingCard.setText(res.getString("WLANPanel.printAsFoldingCard.text")); // NOI18N

        ssidLengthField.setEditable(false);
        ssidLengthField.setColumns(2);
        ssidLengthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ssidLengthField.setText(res.getString("WLANPanel.ssidLengthField.text")); // NOI18N
        ssidLengthField.setFocusable(false);

        passwordLengthField.setEditable(false);
        passwordLengthField.setColumns(2);
        passwordLengthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        passwordLengthField.setText(res.getString("WLANPanel.passwordLengthField.text")); // NOI18N
        passwordLengthField.setFocusable(false);

        noteLabel.setText(res.getString("WLANPanel.noteLabel.text")); // NOI18N

        hiddenNetworkCheckBox.setText(res.getString("WLANPanel.hiddenNetworkCheckBox.text")); // NOI18N

        formatPasswordCheckBox.setText(res.getString("WLANPanel.formatPasswordCheckBox.text")); // NOI18N

        groupCharacterLabel.setText(res.getString("WLANPanel.groupCharacterLabel.text")); // NOI18N

        groupCharacterTextField.setColumns(1);
        groupCharacterTextField.setText(res.getString("WLANPanel.groupCharacterTextField.text")); // NOI18N
        groupCharacterTextField.setToolTipText(res.getString("WLANPanel.groupCharacterTextField.toolTipText")); // NOI18N

        groupSizeLabel.setText(res.getString("WLANPanel.groupSizeLabel.text")); // NOI18N

        groupSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(4, 2, 10, 1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(formatPasswordCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(groupCharacterLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupCharacterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(groupSizeLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(groupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(formatPasswordCheckBox)
                    .addComponent(groupCharacterLabel)
                    .addComponent(groupCharacterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(groupSizeLabel)
                    .addComponent(groupSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ssidLabel)
                                    .addComponent(passwordLabel)
                                    .addComponent(networkLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ssidField)
                                    .addComponent(passwordField)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(networkType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(hiddenNetworkCheckBox)
                                        .addGap(18, 18, Short.MAX_VALUE)
                                        .addComponent(printAsFoldingCard)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(printOptionsButton)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ssidLengthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(passwordLengthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(noteLabel)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ssidSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(passwordSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ssidLabel)
                            .addComponent(ssidField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ssidLengthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(passwordLabel)
                            .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passwordLengthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(networkLabel)
                            .addComponent(networkType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(printOptionsButton)
                            .addComponent(printAsFoldingCard)
                            .addComponent(hiddenNetworkCheckBox))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ssidSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passwordSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noteLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox formatPasswordCheckBox;
    private javax.swing.JLabel groupCharacterLabel;
    private javax.swing.JTextField groupCharacterTextField;
    private javax.swing.JLabel groupSizeLabel;
    private javax.swing.JSpinner groupSizeSpinner;
    private javax.swing.JCheckBox hiddenNetworkCheckBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel networkLabel;
    private final javax.swing.JComboBox networkType = new javax.swing.JComboBox();
    private javax.swing.JLabel noteLabel;
    private final javax.swing.JFormattedTextField passwordField = new javax.swing.JFormattedTextField();
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JTextField passwordLengthField;
    private qrcodegen.modules.AsciiHexSelector passwordSelector;
    private javax.swing.JCheckBox printAsFoldingCard;
    private javax.swing.JButton printOptionsButton;
    private final javax.swing.JTextField ssidField = new javax.swing.JTextField();
    private final javax.swing.JLabel ssidLabel = new javax.swing.JLabel();
    private javax.swing.JTextField ssidLengthField;
    private qrcodegen.modules.AsciiHexSelector ssidSelector;
    // End of variables declaration//GEN-END:variables
}
