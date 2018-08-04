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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentListener;
import javax.swing.text.DocumentFilter;
import qrcodegen.documentfilter.DocumentEncodableFilter;
import qrcodegen.documentfilter.DocumentSizeFilter;
import qrcodegen.modules.vcardgenpanel.PropertyProviderViews;
import qrcodegen.modules.vcardgenpanel.model.VCardNameModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.NamePresentationModel;
import qrcodegen.tools.StaticTools;
import qrcodegen.tools.SwingTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardNamePanel extends javax.swing.JPanel implements PropertyProviderViews {

	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/vcardgenpanel/view/VCardNamePanel");
	private static final int MNEMONIC = getKeyCodeFromResourceBundle("MNEMONIC");
	private static final DocumentFilter FILTER = new DocumentSizeFilter(100, new DocumentEncodableFilter(Charset.forName("UTF-8")));
	private final NamePresentationModel presentationModel;

	/** Creates new form VCardNamePanel */
	public VCardNamePanel() {
		this(new NamePresentationModel(new VCardNameModel()));
	}

	public VCardNamePanel(NamePresentationModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		this.presentationModel = model;
		initComponents();
		initFields();
		initFilter();
		initDocumentListener();
		setName(RES.getString("NAME"));
		model.addPropertyChangeListener(new PresentationModelListener());
	}

	private void initDocumentListener() {
		DocumentListener formattedNameListener = new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setFormattedName(formattedNameField.getText());
			}
		});

		DocumentListener lastNameListener = new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setLastName(familyNameField.getText());
			}
		});

		DocumentListener firstNameListener = new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setFirstName(givenNameField.getText());
			}
		});

		DocumentListener honorificPrefixesListener = new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setHonorificPrefixes(honorificPrefixesField.getText());
			}
		});

		DocumentListener honorificSuffixesListener = new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setHonorificSuffixes(honorificSuffixesField.getText());
			}
		});

		DocumentListener additionalNamesListener = new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setAdditionalNames(additionalNamesField.getText());
			}
		});

		DocumentListener nicknameListener = new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setNickname(nicknameField.getText());
			}
		});

		DocumentListener orgListener = new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setOrg(organizationField.getText());
			}
		});

		DocumentListener unitNamesListener = new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setUnitNames(unitNamesField.getText());
			}
		});


		formattedNameField.getDocument().addDocumentListener(formattedNameListener);
		familyNameField.getDocument().addDocumentListener(lastNameListener);
		givenNameField.getDocument().addDocumentListener(firstNameListener);
		additionalNamesField.getDocument().addDocumentListener(additionalNamesListener);
		honorificPrefixesField.getDocument().addDocumentListener(honorificPrefixesListener);
		honorificSuffixesField.getDocument().addDocumentListener(honorificSuffixesListener);
		additionalNamesField.getDocument().addDocumentListener(additionalNamesListener);
		nicknameField.getDocument().addDocumentListener(nicknameListener);
		organizationField.getDocument().addDocumentListener(orgListener);
		unitNamesField.getDocument().addDocumentListener(unitNamesListener);

	}

	private void initFields() {
		formattedNameLabel.setLabelFor(formattedNameField);
		familyNameLabel.setLabelFor(familyNameField);
		additionalNamesLabel.setLabelFor(additionalNamesField);
		givenNameLabel.setLabelFor(givenNameField);
		honorificPrefixesLabel.setLabelFor(honorificPrefixesField);
		honorificSuffixesLabel.setLabelFor(honorificSuffixesField);
		nicknameLabel.setLabelFor(nicknameField);
		organizationLabel.setLabelFor(organizationField);
		unitNamesLabel.setLabelFor(unitNamesField);
	}

	private void initFilter() {
		SwingTools.setDocumentFilter(formattedNameField, FILTER);
		SwingTools.setDocumentFilter(givenNameField, FILTER);
		SwingTools.setDocumentFilter(additionalNamesField, FILTER);
		SwingTools.setDocumentFilter(familyNameField, FILTER);
		SwingTools.setDocumentFilter(honorificPrefixesField, FILTER);
		SwingTools.setDocumentFilter(honorificSuffixesField, FILTER);
		SwingTools.setDocumentFilter(nicknameField, FILTER);
		SwingTools.setDocumentFilter(organizationField, FILTER);
		SwingTools.setDocumentFilter(unitNamesField, FILTER);
	}

	@Override
	public JPanel getJPanel() {
		return this;
	}

	@Override
	public int getMnemonic() {
		return MNEMONIC;
	}

	private static int getKeyCodeFromResourceBundle(String key) {
		assert key != null;
		return StaticTools.getKeyCodeForString(RES.getString(key));
	}

	private class PresentationModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (NamePresentationModel.FORMATTED_NAME_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getFormattedName();
				if (!newValue.equals(formattedNameField.getText())) {
					formattedNameField.setText(newValue);
				}
			} else if (NamePresentationModel.FIRST_NAME_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getFirstName();
				if (!newValue.equals(givenNameField.getText())) {
					givenNameField.setText(newValue);
				}
			} else if (NamePresentationModel.LAST_NAME_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getLastName();
				if (!newValue.equals(familyNameField.getText())) {
					familyNameField.setText(newValue);
				}
			} else if (NamePresentationModel.ADDITIONAL_NAMES_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getAdditionalNames();
				if (!newValue.equals(additionalNamesField.getText())) {
					additionalNamesField.setText(newValue);
				}
			} else if (NamePresentationModel.HONORIFIC_PREFIXES_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getHonorificPrefixes();
				if (!newValue.equals(honorificPrefixesField.getText())) {
					honorificPrefixesField.setText(newValue);
				}
			} else if (NamePresentationModel.HONORIFIC_SUFFIXES_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getHonorificSuffixes();
				if (!newValue.equals(honorificSuffixesField.getText())) {
					honorificSuffixesField.setText(newValue);
				}
			} else if (NamePresentationModel.NICKNAME_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getNickname();
				if (!newValue.equals(nicknameField.getText())) {
					nicknameField.setText(newValue);
				}
			} else if (NamePresentationModel.ORG_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getOrg();
				if (!newValue.equals(organizationField.getText())) {
					organizationField.setText(newValue);
				}
			} else if (NamePresentationModel.UNIT_NAMES_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getUnitNames();
				if (!newValue.equals(unitNamesField.getText())) {
					unitNamesField.setText(newValue);
				}
			}
		}
	}

	/** This method is called from within the constructor to initialize the
	 * form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        formattedNameLabel = new JLabel();
        formattedNameField = new JTextField();
        familyNameLabel = new JLabel();
        familyNameField = new JTextField();
        honorificSuffixesLabel = new JLabel();
        givenNameLabel = new JLabel();
        honorificSuffixesField = new JTextField();
        givenNameField = new JTextField();
        additionalNamesField = new JTextField();
        honorificPrefixesLabel = new JLabel();
        additionalNamesLabel = new JLabel();
        honorificPrefixesField = new JTextField();
        nicknameLabel = new JLabel();
        nicknameField = new JTextField();
        organizationLabel = new JLabel();
        organizationField = new JTextField();
        unitNamesLabel = new JLabel();
        unitNamesField = new JTextField();

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 498, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 94, Short.MAX_VALUE)
        );

        formattedNameLabel.setText(RES.getString("VCardNamePanel.formattedNameLabel.text_1")); // NOI18N
        formattedNameLabel.setToolTipText(RES.getString("VCardNamePanel.formattedNameLabel.toolTipText_1")); // NOI18N

        formattedNameField.setColumns(30);
        formattedNameField.setToolTipText(RES.getString("VCardNamePanel.formattedNameField.toolTipText_1")); // NOI18N

        familyNameLabel.setText(RES.getString("VCardNamePanel.familyNameLabel.text_1")); // NOI18N

        familyNameField.setColumns(15);

        honorificSuffixesLabel.setText(RES.getString("VCardNamePanel.honorificSuffixesLabel.text_1")); // NOI18N

        givenNameLabel.setText(RES.getString("VCardNamePanel.givenNameLabel.text_1")); // NOI18N

        honorificSuffixesField.setColumns(15);
        honorificSuffixesField.setToolTipText(RES.getString("VCardNamePanel.honorificSuffixesField.toolTipText_1")); // NOI18N

        givenNameField.setColumns(15);

        additionalNamesField.setColumns(15);
        additionalNamesField.setToolTipText(RES.getString("VCardNamePanel.additionalNamesField.toolTipText_1")); // NOI18N

        honorificPrefixesLabel.setText(RES.getString("VCardNamePanel.honorificPrefixesLabel.text_1")); // NOI18N

        additionalNamesLabel.setText(RES.getString("VCardNamePanel.additionalNamesLabel.text_1")); // NOI18N

        honorificPrefixesField.setColumns(15);
        honorificPrefixesField.setToolTipText(RES.getString("VCardNamePanel.honorificPrefixesField.toolTipText_1")); // NOI18N

        nicknameLabel.setText(RES.getString("VCardNamePanel.nicknameLabel.text_1")); // NOI18N

        nicknameField.setColumns(15);
        nicknameField.setToolTipText(RES.getString("VCardNamePanel.nicknameField.toolTipText_1")); // NOI18N

        organizationLabel.setText(RES.getString("VCardNamePanel.organizationLabel.text")); // NOI18N

        organizationField.setText(RES.getString("VCardNamePanel.organizationField.text")); // NOI18N

        unitNamesLabel.setText(RES.getString("VCardNamePanel.unitNamesLabel.text")); // NOI18N

        unitNamesField.setText(RES.getString("VCardNamePanel.unitNamesField.text")); // NOI18N
        unitNamesField.setToolTipText(RES.getString("VCardNamePanel.unitNamesField.toolTipText")); // NOI18N

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(givenNameLabel)
                    .addComponent(familyNameLabel)
                    .addComponent(honorificPrefixesLabel)
                    .addComponent(nicknameLabel)
                    .addComponent(formattedNameLabel)
                    .addComponent(organizationLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(formattedNameField)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                                    .addComponent(honorificPrefixesField, Alignment.LEADING)
                                    .addComponent(familyNameField, Alignment.LEADING)
                                    .addComponent(givenNameField, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(additionalNamesLabel)
                                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(additionalNamesField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(honorificSuffixesLabel)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                            .addComponent(unitNamesField)
                                            .addComponent(honorificSuffixesField)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                                    .addComponent(organizationField, Alignment.LEADING)
                                    .addComponent(nicknameField, Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addComponent(unitNamesLabel)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(formattedNameLabel)
                    .addComponent(formattedNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(givenNameLabel)
                    .addComponent(givenNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(additionalNamesLabel)
                    .addComponent(additionalNamesField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(familyNameLabel)
                    .addComponent(familyNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(honorificPrefixesLabel)
                    .addComponent(honorificPrefixesField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(honorificSuffixesLabel)
                    .addComponent(honorificSuffixesField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(nicknameLabel)
                    .addComponent(nicknameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(organizationField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(organizationLabel)
                    .addComponent(unitNamesLabel)
                    .addComponent(unitNamesField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JTextField additionalNamesField;
    private JLabel additionalNamesLabel;
    private JTextField familyNameField;
    private JLabel familyNameLabel;
    private JTextField formattedNameField;
    private JLabel formattedNameLabel;
    private JTextField givenNameField;
    private JLabel givenNameLabel;
    private JTextField honorificPrefixesField;
    private JLabel honorificPrefixesLabel;
    private JTextField honorificSuffixesField;
    private JLabel honorificSuffixesLabel;
    private JPanel jPanel1;
    private JTextField nicknameField;
    private JLabel nicknameLabel;
    private JTextField organizationField;
    private JLabel organizationLabel;
    private JTextField unitNamesField;
    private JLabel unitNamesLabel;
    // End of variables declaration//GEN-END:variables
}
