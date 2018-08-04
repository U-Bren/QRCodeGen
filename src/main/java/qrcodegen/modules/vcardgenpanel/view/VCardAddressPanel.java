/*
 Copyright 2012 Stefan Ganzer

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
package qrcodegen.modules.vcardgenpanel.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.DocumentFilter;
import qrcodegen.documentfilter.DocumentDoubleQuoteFilter;
import qrcodegen.documentfilter.DocumentEncodableFilter;
import qrcodegen.documentfilter.DocumentSizeFilter;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcardgenpanel.model.VCardAddressModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.AddressPresentationModel;
import qrcodegen.tools.StaticTools;
import qrcodegen.tools.SwingTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardAddressPanel extends VCardAbstractPanel {

	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/vcardgenpanel/view/VCardAddressPanel");
	private static final int MNEMONIC = getKeyCodeFromResourceBundle("MNEMONIC");
	private static final DocumentFilter ENCODABLE_FILTER = new DocumentEncodableFilter(Charset.forName("UTF8"));
	private static final DocumentFilter FILTER = new DocumentSizeFilter(100, ENCODABLE_FILTER);
	private static final DocumentFilter LABEL_FILTER = new DocumentSizeFilter(200, new DocumentDoubleQuoteFilter(ENCODABLE_FILTER));
	private final AddressPresentationModel presentationModel;

	/** Creates new form VCardNamePanel */
	public VCardAddressPanel() {
		this(new AddressPresentationModel(new VCardAddressModel()));
	}

	public VCardAddressPanel(AddressPresentationModel presentationModel) {
		if (presentationModel == null) {
			throw new NullPointerException();
		}
		this.presentationModel = presentationModel;
		initComponents();
		initFields();
		initFilters();
		initDocumentFilter();
		initButtons();
		setName(RES.getString("ADDRESS"));
		presentationModel.addPropertyChangeListener(new ModelListener());
	}

	private void initButtons() {
		ActionListener buttonListener = new ButtonListener();
		workHomeSelector.otherRadioButtonAddActionListener(buttonListener);
		workHomeSelector.homeRadioButtonAddActionListener(buttonListener);
		workHomeSelector.workRadioButtonAddActionListener(buttonListener);
	}

	private void initDocumentFilter() {
		streetField.getDocument().addDocumentListener(new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setStreet(streetField.getText());
			}
		}));

		countryField.getDocument().addDocumentListener(new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setCountryName(countryField.getText());
			}
		}));

		postalCodeField.getDocument().addDocumentListener(new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setPostalCode(postalCodeField.getText());
			}
		}));

		regionField.getDocument().addDocumentListener(new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setRegion(regionField.getText());
			}
		}));

		localityField.getDocument().addDocumentListener(new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setLocality(localityField.getText());
			}
		}));

		labelTextArea.getDocument().addDocumentListener(new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setLabel(labelTextArea.getText());
			}
		}));
	}

	private void initFields() {
		streetLabel.setLabelFor(streetField);
		countryLabel.setLabelFor(countryField);
		postalCodeLabel.setLabelFor(postalCodeField);
		regionLabel.setLabelFor(regionField);
		localityLabel.setLabelFor(localityField);
		labelLabel.setLabelFor(labelTextArea);
	}

	private void initFilters() {
		SwingTools.setDocumentFilter(streetField, FILTER);
		SwingTools.setDocumentFilter(countryField, FILTER);
		SwingTools.setDocumentFilter(postalCodeField, FILTER);
		SwingTools.setDocumentFilter(regionField, FILTER);
		SwingTools.setDocumentFilter(localityField, FILTER);
		SwingTools.setDocumentFilter(labelTextArea, LABEL_FILTER);
	}

	@Override
	public int getMnemonic() {
		return MNEMONIC;
	}

	private class ModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (AddressPresentationModel.STREET_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getStreet();
				if (!newValue.equals(streetField.getText())) {
					streetField.setText(newValue);
				}
			} else if (AddressPresentationModel.COUNTRY_NAME_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getCountryName();
				if (!newValue.equals(countryField.getText())) {
					countryField.setText(newValue);
				}
			} else if (AddressPresentationModel.POSTAL_CODE_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getPostalCode();
				if (!newValue.equals(postalCodeField.getText())) {
					postalCodeField.setText(newValue);
				}
			} else if (AddressPresentationModel.REGION_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getRegion();
				if (!newValue.equals(regionField.getText())) {
					regionField.setText(newValue);
				}
			} else if (AddressPresentationModel.LOCALITY_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getLocality();
				if (!newValue.equals(localityField.getText())) {
					localityField.setText(newValue);
				}
			} else if (AddressPresentationModel.LABEL_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getLabel();
				if (!newValue.equals(labelTextArea.getText())) {
					labelTextArea.setText(newValue);
				}
			} else if (AddressPresentationModel.TYPE_PARAMETER.equals(propertyName)) {
				Set<TypeParameter> c = presentationModel.getTypeParameters();
				if (c.contains(TypeParameter.HOME)) {
					if (!workHomeSelector.isHomeRadioButtonSelected()) {
						workHomeSelector.homeRadioButtonSetSelected(true);
					}
				} else if (c.contains(TypeParameter.WORK)) {
					if (!workHomeSelector.isWorkRadioButtonSelected()) {
						workHomeSelector.workRadioButtonSetSelected(true);
					}
				} else {
					if (!workHomeSelector.isOtherRadioButtonSelected()) {
						workHomeSelector.otherRadioButtonSetSelected(true);
					}
				}
			}
		}
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final String actionCommand = e.getActionCommand();
			if (WorkHomeSelector2.OTHER_ACTION_COMMAND.equals(actionCommand)) {
				presentationModel.setTypeParameterOther();
			} else if (WorkHomeSelector2.HOME_ACTION_COMMAND.equals(actionCommand)) {
				presentationModel.setTypeParameterHome();
			} else if (WorkHomeSelector2.WORK_ACTION_COMMAND.equals(actionCommand)) {
				presentationModel.setTypeParameterWork();
			}
		}
	}

	private static int getKeyCodeFromResourceBundle(String key) {
		assert key != null;
		return StaticTools.getKeyCodeForString(RES.getString(key));
	}

	/** This method is called from within the constructor to initialize the
	 * form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        streetField = new JTextField();
        streetLabel = new JLabel();
        localityLabel = new JLabel();
        localityField = new JTextField();
        regionLabel = new JLabel();
        regionField = new JTextField();
        postalCodeLabel = new JLabel();
        postalCodeField = new JTextField();
        countryField = new JTextField();
        countryLabel = new JLabel();
        labelLabel = new JLabel();
        jScrollPane1 = new JScrollPane();
        labelTextArea = new JTextArea();
        workHomeSelector = new WorkHomeSelector2();

        streetField.setColumns(20);
        streetField.setNextFocusableComponent(localityField);

        streetLabel.setText(RES.getString("VCardAddressPanel.streetLabel.text")); // NOI18N

        localityLabel.setText(RES.getString("VCardAddressPanel.localityLabel.text")); // NOI18N

        localityField.setColumns(10);
        localityField.setNextFocusableComponent(regionField);

        regionLabel.setText(RES.getString("VCardAddressPanel.regionLabel.text")); // NOI18N

        regionField.setColumns(10);
        regionField.setNextFocusableComponent(postalCodeField);

        postalCodeLabel.setText(RES.getString("VCardAddressPanel.postalCodeLabel.text")); // NOI18N

        postalCodeField.setColumns(5);
        postalCodeField.setNextFocusableComponent(countryField);

        countryField.setColumns(10);
        countryField.setNextFocusableComponent(labelTextArea);

        countryLabel.setText(RES.getString("VCardAddressPanel.countryLabel.text")); // NOI18N

        labelLabel.setText(RES.getString("VCardAddressPanel.labelLabel.text")); // NOI18N

        labelTextArea.setColumns(20);
        labelTextArea.setRows(5);
        labelTextArea.setToolTipText(RES.getString("VCardAddressPanel.labelTextArea.toolTipText")); // NOI18N
        labelTextArea.setNextFocusableComponent(workHomeSelector);
        jScrollPane1.setViewportView(labelTextArea);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(postalCodeLabel)
                            .addComponent(countryLabel)
                            .addComponent(regionLabel)
                            .addComponent(localityLabel)
                            .addComponent(streetLabel))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                            .addComponent(streetField)
                            .addComponent(localityField)
                            .addComponent(regionField)
                            .addComponent(postalCodeField)
                            .addComponent(countryField))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane1)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(workHomeSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(streetLabel)
                    .addComponent(streetField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLabel))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(localityLabel)
                            .addComponent(localityField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(regionLabel)
                            .addComponent(regionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(postalCodeLabel)
                            .addComponent(postalCodeField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(countryLabel)
                            .addComponent(countryField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(workHomeSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTextField countryField;
    private JLabel countryLabel;
    private JScrollPane jScrollPane1;
    private JLabel labelLabel;
    private JTextArea labelTextArea;
    private JTextField localityField;
    private JLabel localityLabel;
    private JTextField postalCodeField;
    private JLabel postalCodeLabel;
    private JTextField regionField;
    private JLabel regionLabel;
    private JTextField streetField;
    private JLabel streetLabel;
    private WorkHomeSelector2 workHomeSelector;
    // End of variables declaration//GEN-END:variables
}
