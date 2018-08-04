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

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import qrcodegen.tools.StaticTools;
import qrcodegen.tools.SwingTools;

/**
 * A dialog to change the folding card containing the QR Code generated from the
 * WLAN credentials, and the credentials in plaintext.
 *
 * @author Stefan Ganzer
 */
public class WLANPrintSettings extends JDialog {

	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/WLANPrintSettings"); //NOI18N
	private static final String[] EMPTY_STRING_ARRAY = new String[0];
	/** Logical fonts guaranteed to be available on any Java platform. */
	private static final String[] JAVA_LOGICAL_FONTS = {"Monospaced", "SansSerif", "Serif"}; //NOI18N
	/** Font family names available on the current machine. */
	private static String[] FONT_FAMILY_NAMES = EMPTY_STRING_ARRAY;
	private static final String DEFAULT_FONT_NAME = "Serif"; //NOI18N
	private static final int DEFAULT_FONT_SIZE = 11;
	private static final VerticalAlignment DEFAULT_ALIGNMENT = VerticalAlignment.CENTER;
	private Font font = new Font(DEFAULT_FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE);
	private int fontSize = DEFAULT_FONT_SIZE;
	/** If option == Option.CANCEL changes are reverted to the freezed state when
	 * closing the window. */
	private Option option = Option.CANCEL;
	private final FoldingCardConfigurator fcc;

	private enum Option {

		OK, CANCEL;
	}

	/**
	 * Creates a new WLANPrintSettings dialog.
	 *
	 * @param parent
	 * @param modal
	 * @param fcc
	 * @param iconImages
	 */
	WLANPrintSettings(java.awt.Frame parent, boolean modal, FoldingCardConfigurator fcc, List<Image> iconImages) {
		super(parent, modal);
		if (fcc == null) {
			throw new NullPointerException();
		}
		if (iconImages == null) {
			throw new NullPointerException();
		}
		this.fcc = fcc;

		// Allow changes made to the FoldingCardConfigurator to be reverted
		this.fcc.freezeState();

		initComponents();
		initWindow(iconImages);
		initFontFamilyComboBox();
		initFontSizeSelector();
		initTextAlignmentButtons();
		initImageRotation();
		initTextRotation();
		initPageOptions();

		addFieldNameCheckBox.setMnemonic(getKeyCodeFromResourceBundle("ADD_FIELD_NAMES_MNEMONIC"));

		initUIToValues(this.fcc);

		//Register the listeners after we have set the ui elements state
		//to the values taken from the FoldingCardPrinter instance
		registerFontListener();
		registerAlignmentButtonsListener();
		registerRotationListener();
		registerPageOptionsListener();
		OptionButtonListener obl = new OptionButtonListener();
		initOkButton(obl);
		initCancelButton(obl);

		pack();
		SwingTools.restrictSizeToScreen(this);
	}

	/**
	 * Returns a new WLANPrintSettings dialog.
	 *
	 * @param parent
	 * @param modal
	 * @param fcc
	 * @param iconImages
	 *
	 * @return a new WLANPrintSettings dialog
	 */
	static WLANPrintSettings newInstance(java.awt.Frame parent, boolean modal, FoldingCardConfigurator fcc, List<Image> iconImages) {
		WLANPrintSettings instance = new WLANPrintSettings(parent, modal, fcc, iconImages);
		SwingTools.registerEscKeyForClosing(instance);
		// Initialize the FONT_FAMILY_NAMES
		instance.initFontFamilyNames();
		return instance;
	}

	/**
	 * Initializes the FONT_FAMILY_NAMES in a separate thread.
	 *
	 * This method is thread safe.
	 */
	private void initFontFamilyNames() {
		if (getFontFamilyNames().length == 0) {
			(new FontFamilyNameGatherer()).execute();
		}// nothing to do - this method has already run
	}

	/**
	 * Initializes the FONT_FAMILY_NAMES in a background thread and updates the
	 * fontFamily-JComboBox when done.
	 */
	private class FontFamilyNameGatherer extends SwingWorker<List<String>, Object> {

		@Override
		protected List<String> doInBackground() throws Exception {
			String[] fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
			setFontFamilyNames(fontFamilyNames);
			return Arrays.asList(fontFamilyNames);
		}

		@Override
		protected void done() {
			Object currentSelection = fontFamily.getSelectedItem();
			fontFamily.setModel(new DefaultComboBoxModel(getFontFamilyNames()));
			fontFamily.setSelectedItem(currentSelection);
			WLANPrintSettings.this.pack();
			SwingTools.restrictSizeToScreen(WLANPrintSettings.this);
		}
	}

	private void registerAlignmentButtonsListener() {
		ActionListener alignmentListener = new AlignmentListener();
		alignTopButton.addActionListener(alignmentListener);
		alignCenterButton.addActionListener(alignmentListener);
		alignBottomButton.addActionListener(alignmentListener);
	}

	private void registerFontListener() {
		FontListener fontListener = new FontListener();
		fontFamily.addItemListener(fontListener);
		fontSizeSpinner.addChangeListener(fontListener);
	}

	private void registerRotationListener() {
		RotationListener rl = new RotationListener();
		imageRotationComboBox.addItemListener(rl);
		textRotationComboBox.addItemListener(rl);
	}

	private void registerPageOptionsListener() {
		scalingComboBox.addItemListener(new ScalingListener());
	}

	private void initUIToValues(FoldingCardConfigurator fcc) {
		// Set the ui elements state to the values taken from
		// the FoldingCardConfigurator
		font = fcc.getFont();
		fontFamily.setSelectedItem(font.getFamily());
		fontSizeSpinner.setValue(font.getSize());
		setVerticalAlignmentButtonState(fcc.getVerticalAlignment());
		textRotationComboBox.setSelectedItem(fcc.getTextRotation());
		imageRotationComboBox.setSelectedItem(fcc.getImageRotation());
		addFieldNameCheckBox.setSelected(fcc.getAddFieldNames());
		scalingComboBox.setSelectedItem(fcc.getScaleRuleForPrinting());
	}

	private void initWindow(List<Image> iconImages) {
		setIconImages(iconImages);
		setTitle(RES.getString("CHANGE PRINT OPTIONS"));
		addWindowListener(new MyWindowListener());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void initCancelButton(OptionButtonListener obl) {
		cancelButton.setMnemonic(getKeyCodeFromResourceBundle("CANCEL_BUTTON_MNEMONIC"));
		cancelButton.addActionListener(obl);
	}

	private void initOkButton(OptionButtonListener obl) {
		okButton.setMnemonic(getKeyCodeFromResourceBundle("OK_BUTTON_MNEMONIC"));
		okButton.addActionListener(obl);
		getRootPane().setDefaultButton(okButton);
	}

	private void initFontFamilyComboBox() {
		fontFamilyLabel.setLabelFor(fontFamily);
		fontFamilyLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("FONT_FAMILY_MNEMONIC"));

		fontFamily.setSelectedItem(DEFAULT_FONT_NAME);
		fontFamily.setMaximumRowCount(5);
	}

	private void initFontSizeSelector() {
		fontSizeLabel.setLabelFor(fontSizeSpinner);
		fontSizeLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("FONT_SIZE_MNEMONIC"));

		fontSizeSpinner.setValue(DEFAULT_FONT_SIZE);
	}

	private void initImageRotation() {
		imageRotationLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("IMAGE_ROTATION_MNEMONIC"));
		imageRotationLabel.setLabelFor(imageRotationComboBox);
	}

	private void initPageOptions() {
		scalingLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("SCALING_MNEMONIC"));
		scalingLabel.setLabelFor(scalingComboBox);
		scalingComboBox.addItem(ScaleToPaperSize.NONE);
		scalingComboBox.addItem(ScaleToPaperSize.DOWN);
		scalingComboBox.addItem(ScaleToPaperSize.FIT);
	}

	private void initTextAlignmentButtons() {
		textAlignmentLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("TEXT_ALIGNMENT_MNEMONIC"));
		textAlignmentLabel.setLabelFor(alignTopButton);
		alignTopButton.setMnemonic(getKeyCodeFromResourceBundle("TEXT_ALIGNMENT_TOP_MNEMONIC"));
		alignCenterButton.setMnemonic(getKeyCodeFromResourceBundle("TEXT_ALIGNMENT_CENTER_MNEMONIC"));
		alignBottomButton.setMnemonic(getKeyCodeFromResourceBundle("TEXT_ALIGNMENT_BOTTOM_MNEMONIC"));
		ButtonGroup group = new ButtonGroup();
		group.add(alignTopButton);
		group.add(alignCenterButton);
		group.add(alignBottomButton);
		alignCenterButton.setSelected(true);
	}

	private void initTextRotation() {
		textRotationLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("TEXT_ROTATION_MNEMONIC"));
		textRotationLabel.setLabelFor(textRotationComboBox);
	}

	/**
	 * This method returns FONT_FAMILY_NAMES, not a copy of it. The caller is
	 * not allowed to modify the returned array. Never returns null.
	 *
	 * @return {@link #FONT_FAMILY_NAMES}
	 */
	private static synchronized String[] getFontFamilyNames() {
		return FONT_FAMILY_NAMES;
	}

	/**
	 * This method assigns the given data to {@link #FONT_FAMILY_NAMES}. The
	 * caller is not allowed to modify the provided data afterwards.
	 *
	 * @param data the array to assign to {@link #FONT_FAMILY_NAMES}. The given
	 * array mustn't be null.
	 */
	private static synchronized void setFontFamilyNames(String[] data) {
		assert data != null;
		FONT_FAMILY_NAMES = data;
	}

	private void setVerticalAlignmentButtonState(VerticalAlignment verticalAlignment) {
		switch (verticalAlignment) {
			case TOP:
				alignTopButton.setSelected(true);
				break;
			case CENTER:
				alignCenterButton.setSelected(true);
				break;
			case BOTTOM:
				alignBottomButton.setSelected(true);
				break;
			default:
				throw new AssertionError();
		}
	}

	// Restores the state of the FoldingCardPrinter fcp to the saved state
	private void restoreSavedState() {
		fcc.revertChanges();
	}

	private static int getKeyCodeFromResourceBundle(String key) {
		assert key != null;
		return StaticTools.getKeyCodeForString(RES.getString(key));
	}

	private class OptionButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			assert e.getSource() == okButton || e.getSource() == cancelButton : e.getSource();

			if (e.getSource() == okButton) {
				option = Option.OK;
			} // else option == Option.CANCEL by default
			WLANPrintSettings.this.dispatchEvent(new WindowEvent(WLANPrintSettings.this, WindowEvent.WINDOW_CLOSING));//dispose();
		}
	}

	private class FontListener implements ItemListener, ChangeListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}
			if (e.getSource() == fontFamily) {
				String fontName = (String) fontFamily.getSelectedItem();
				font = new Font(fontName, Font.PLAIN, fontSize);
				fcc.setFont(font);
			}
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			SpinnerModel model = fontSizeSpinner.getModel();
			if (model instanceof SpinnerNumberModel) {
				fontSize = ((SpinnerNumberModel) model).getNumber().intValue();
				font = font.deriveFont((float) fontSize);
				fcc.setFont(font);
			} else {
				throw new AssertionError("Expected a SpinnerNumberModel, but is: " + model.toString());
			}
		}
	}

	private class RotationListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}
			if (e.getSource() == imageRotationComboBox) {
				Rotation r = (Rotation) e.getItem();
				fcc.setImageRotation(r);
			}
			if (e.getSource() == textRotationComboBox) {
				Rotation r = (Rotation) e.getItem();
				fcc.setTextRotation(r);
			}
		}
	}

	private class ScalingListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}
			if (e.getSource() == scalingComboBox) {
				ScaleToPaperSize s = (ScaleToPaperSize) e.getItem();
				fcc.setScaleRuleForPrinting(s);
			}
		}
	}

	private class AlignmentListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == alignTopButton) {
				fcc.setVerticalAlignment(VerticalAlignment.TOP);
			} else if (e.getSource() == alignCenterButton) {
				fcc.setVerticalAlignment(VerticalAlignment.CENTER);
			} else if (e.getSource() == alignBottomButton) {
				fcc.setVerticalAlignment(VerticalAlignment.BOTTOM);
			}
		}
	}

	private class MyWindowListener implements WindowListener {

		@Override
		public void windowOpened(WindowEvent e) {
			// nothing to do
		}

		@Override
		public void windowClosing(WindowEvent e) {
			dispose();
			if (option == Option.CANCEL) {
				restoreSavedState();
			}
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// nothing to do
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// nothing to do
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// nothing to do
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// nothing to do
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// nothing to do
		}
	}

	private void initComponents() {
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		alignTopButton = new javax.swing.JToggleButton();
		alignCenterButton = new javax.swing.JToggleButton();
		alignBottomButton = new javax.swing.JToggleButton();
		textAlignmentLabel = new javax.swing.JLabel();
		fontFamily = new javax.swing.JComboBox();
		fontSizeLabel = new javax.swing.JLabel();
		fontSizeSpinner = new javax.swing.JSpinner();
		fontFamilyLabel = new javax.swing.JLabel();
		textRotationLabel = new javax.swing.JLabel();
		textRotationComboBox = new javax.swing.JComboBox();
		addFieldNameCheckBox = new javax.swing.JCheckBox();
		imageRotationLabel = new javax.swing.JLabel();
		imageRotationComboBox = new javax.swing.JComboBox();
		scalingLabel = new javax.swing.JLabel();
		scalingComboBox = new javax.swing.JComboBox();
		fcpScrollPane = new javax.swing.JScrollPane();
		initFcpScrollPane();
		initButtons();
		initPanel();
		pack();
	}

	private void initPanel() {

		JPanel textOptionsPanel = createTextOptionsPanel();
		JPanel codeOptionsPanel = createCodeOptionsPanel();
		JPanel pageOptionsPanel = createPageOptionsPanel();

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap()
				/*
				 * FIX The maximum size should be also
				 * Grouplayout.PREFERRED_SIZE, but this only works for code size
				 * <= 478 pixel. For bigger codes, the preferred size reported
				 * by the JScrollPane is a bit to small for the image, so that
				 * scrollbars are displayed. You can no longer increase the size
				 * of the scrollPane, even so there would be enough screen
				 * space.
				 */
				.addComponent(fcpScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(codeOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(textOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(pageOptionsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(okButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(cancelButton))).addContainerGap()));

		// Vertical layout
		GroupLayout.SequentialGroup vControlsAndButtonsGroup = layout.createSequentialGroup().addComponent(codeOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(textOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(pageOptionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(cancelButton).addComponent(okButton));


		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(fcpScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addGroup(vControlsAndButtonsGroup)).addContainerGap();
		layout.setVerticalGroup(vGroup);
	}

	private void initFcpScrollPane() {
		FoldingCardPrinter foldingCardPrinter = fcc.getFoldingCardPrinter();
		fcpScrollPane.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		fcpScrollPane.setAutoscrolls(true);
		fcpScrollPane.setViewportView(foldingCardPrinter);
	}

	private void initButtons() {
		okButton.setText(RES.getString("WLANPrintSettings.okButton.text")); // NOI18N
		cancelButton.setText(RES.getString("WLANPrintSettings.cancelButton.text")); // NOI18N
	}

	private JPanel createScalingPanel() {
		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(scalingLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(scalingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(scalingLabel).addComponent(scalingComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap(19, Short.MAX_VALUE)));
		return panel;
	}

	private JPanel createImageRotationPanel() {
		imageRotationLabel.setText(RES.getString("WLANPrintSettings.imageRotationLabel.text")); // NOI18N
		imageRotationComboBox.setModel(new DefaultComboBoxModel(Rotation.values()));

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(imageRotationLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(imageRotationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(imageRotationLabel).addComponent(imageRotationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		return panel;
	}

	private JPanel createTextAlignmentPanel() {
		alignTopButton.setIcon(SwingTools.getSingleton().createImageIcon(getClass(), "/qrcodegen/modules/images/AlignTop16.gif", null));
		alignTopButton.setToolTipText(RES.getString("WLANPrintSettings.alignTopButton.toolTipText")); // NOI18N

		alignCenterButton.setIcon(SwingTools.getSingleton().createImageIcon(getClass(), "/qrcodegen/modules/images/AlignCenter16.gif", null)); // NOI18N
		alignCenterButton.setToolTipText(RES.getString("WLANPrintSettings.alignCenterButton.toolTipText")); // NOI18N

		alignBottomButton.setIcon(SwingTools.getSingleton().createImageIcon(getClass(), "/qrcodegen/modules/images/AlignBottom16.gif", null)); // NOI18N
		alignBottomButton.setToolTipText(RES.getString("WLANPrintSettings.alignBottomButton.toolTipText")); // NOI18N

		textAlignmentLabel.setText(RES.getString("WLANPrintSettings.textAlignmentLabel.text")); // NOI18N

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addComponent(textAlignmentLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(alignTopButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(alignCenterButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(alignBottomButton).addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER).addComponent(alignTopButton).addComponent(textAlignmentLabel).addComponent(alignBottomButton).addComponent(alignCenterButton)).addContainerGap()));


		return panel;
	}

	private JPanel createPageOptionsPanel() {
		scalingLabel.setText(RES.getString("WLANPrintSettings.scalingLabel.text"));
		scalingComboBox.setModel(new DefaultComboBoxModel());

		JPanel scalingPanel = createScalingPanel();
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), RES.getString("WLANPrintSettings.jPanel6.border.title")));
		javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(panel);
		panel.setLayout(jPanel6Layout);
		jPanel6Layout.setHorizontalGroup(
				jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addContainerGap().addComponent(scalingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel6Layout.setVerticalGroup(
				jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel6Layout.createSequentialGroup().addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(scalingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
		return panel;
	}

	private JPanel createTextOptionsPanel() {


		textRotationLabel.setText(RES.getString("WLANPrintSettings.textRotationLabel.text")); // NOI18N

		textRotationComboBox.setModel(new DefaultComboBoxModel(Rotation.values()));

		addFieldNameCheckBox.setText(RES.getString("WLANPrintSettings.addFieldNameCheckBox.text")); // NOI18N
		addFieldNameCheckBox.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addFieldNameCheckBoxActionPerformed(evt);
			}
		});
		JPanel textAlignmentPanel = createTextAlignmentPanel();
		JPanel fontSettingsPanel = createFontSettingsPanel();
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), RES.getString("WLANPrintSettings.jPanel3.border.title"))); // NOI18N
		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(panel);
		panel.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(
				jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(textAlignmentPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(fontSettingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(jPanel3Layout.createSequentialGroup().addGap(10, 10, 10).addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(addFieldNameCheckBox).addGroup(jPanel3Layout.createSequentialGroup().addComponent(textRotationLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(textRotationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))).addContainerGap()));
		jPanel3Layout.setVerticalGroup(
				jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(fontSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(textAlignmentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(textRotationLabel).addComponent(textRotationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addComponent(addFieldNameCheckBox).addContainerGap()));

		return panel;
	}

	private JPanel createFontSettingsPanel() {
		fontFamilyLabel.setText(RES.getString("WLANPrintSettings.fontFamilyLabel.text")); // NOI18N
		fontSizeLabel.setText(RES.getString("WLANPrintSettings.fontSizeLabel.text")); // NOI18N

		fontFamily.setModel(new DefaultComboBoxModel((getFontFamilyNames().length == 0 ? JAVA_LOGICAL_FONTS : getFontFamilyNames())));
		fontSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(11, 6, 48, 1));

		JPanel panel = new JPanel();
		GroupLayout layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(fontFamilyLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(fontFamily, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(fontSizeLabel).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(fontSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(fontFamilyLabel).addComponent(fontFamily, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(fontSizeLabel).addComponent(fontSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
		return panel;
	}

	private JPanel createCodeOptionsPanel() {
		JPanel panel = new JPanel();
		JPanel imageRotationPanel = createImageRotationPanel();
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), RES.getString("WLANPrintSettings.jPanel4.border.title"))); // NOI18N

		javax.swing.GroupLayout codeOptionsPanelLayout = new javax.swing.GroupLayout(panel);
		panel.setLayout(codeOptionsPanelLayout);
		codeOptionsPanelLayout.setHorizontalGroup(
				codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(codeOptionsPanelLayout.createSequentialGroup().addContainerGap().addComponent(imageRotationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(191, 191, 191)));
		codeOptionsPanelLayout.setVerticalGroup(
				codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(codeOptionsPanelLayout.createSequentialGroup().addContainerGap().addComponent(imageRotationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));


		return panel;
	}

	private void addFieldNameCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addFieldNameCheckBoxActionPerformed
		fcc.setAddFieldNames(addFieldNameCheckBox.isSelected());
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/*
		 * Set the Nimbus look and feel
		 */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
//		try {
//			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//				if ("Nimbus".equals(info.getName())) {
//					javax.swing.UIManager.setLookAndFeel(info.getClassName());
//					break;
//				}
//			}
//		} catch (ClassNotFoundException ex) {
//			java.util.logging.Logger.getLogger(WLANPrintSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//		} catch (InstantiationException ex) {
//			java.util.logging.Logger.getLogger(WLANPrintSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//		} catch (IllegalAccessException ex) {
//			java.util.logging.Logger.getLogger(WLANPrintSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
//			java.util.logging.Logger.getLogger(WLANPrintSettings.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//		}
		//</editor-fold>

		/*
		 * Create and display the dialog
		 */
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				List<Image> iconImages = Collections.emptyList();
				WLANPrintSettings dialog = new WLANPrintSettings(new javax.swing.JFrame(), true, getTestFlipCardConfigurator(), iconImages);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {

					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

	private static FoldingCardConfigurator getTestFlipCardConfigurator() {

		ImageIcon imageIcon = new ImageIcon(WLANPrintSettings.class.getResource("/qrcodegen/modules/test_qrcode.png"));

		FoldingCardConfigurator fcc = new FoldingCardConfigurator(imageIcon.getImage(), Rotation.R180);

		fcc.setSSID("Test SSID");
		fcc.setNetworkKey("This is a sample test password which is 60 characters long.");
		fcc.setNetworkType(NetworkType.WPA_WPA2);
		fcc.setFont(new Font(DEFAULT_FONT_NAME, Font.PLAIN, DEFAULT_FONT_SIZE));
		fcc.setVerticalAlignment(DEFAULT_ALIGNMENT);
		fcc.setTextRotation(Rotation.R0);

		return fcc;

	}
	private javax.swing.JCheckBox addFieldNameCheckBox;
	private javax.swing.JToggleButton alignBottomButton;
	private javax.swing.JToggleButton alignCenterButton;
	private javax.swing.JToggleButton alignTopButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JScrollPane fcpScrollPane;
	private javax.swing.JComboBox fontFamily;
	private javax.swing.JLabel fontFamilyLabel;
	private javax.swing.JLabel fontSizeLabel;
	private javax.swing.JSpinner fontSizeSpinner;
	private javax.swing.JComboBox imageRotationComboBox;
	private javax.swing.JLabel imageRotationLabel;
	private javax.swing.JButton okButton;
	private javax.swing.JComboBox scalingComboBox;
	private javax.swing.JLabel scalingLabel;
	private javax.swing.JLabel textAlignmentLabel;
	private javax.swing.JComboBox textRotationComboBox;
	private javax.swing.JLabel textRotationLabel;
}
