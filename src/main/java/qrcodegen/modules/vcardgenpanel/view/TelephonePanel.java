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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentListener;
import javax.swing.text.DocumentFilter;
import qrcodegen.documentfilter.DocumentEncodableFilter;
import qrcodegen.documentfilter.DocumentSizeFilter;
import qrcodegen.tools.SwingTools;

/**
 *
 * @author Stefan Ganzer
 */
public final class TelephonePanel extends javax.swing.JPanel {

	public static final String NUMBER_FIELD = "Number";
	public static final String FAX_PARAMETER = "Fax";
	public static final String CELL_PARAMETER = "Cell";
	public static final String VOICE_PARAMETER = "Voice";
	public static final String OTHER_PARAMETER = "Other";
	public static final String WORK_PARAMETER = "Work";
	public static final String HOME_PARAMETER = "Home";
	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/vcardgenpanel/view/TelephonePanel");
	private static final DocumentFilter FILTER = new DocumentEncodableFilter(Charset.forName("UTF8"), new DocumentSizeFilter(100));
	// Visual separators: -.()

	/** Creates new form VCardNamePanel */
	public TelephonePanel() {
		initComponents();
		initFilter();
		initWorkHomeSelectorActionListener();
		initItemListener();
		initTelephoneField();
	}

	public String getNumber() {
		return telephoneField.getText();
	}

	public void setNumber(String value) {
		if (!telephoneField.getText().equals(value)) {
			telephoneField.setText(value);
		}
	}

	public void faxCheckBoxSetSelected(boolean selected) {
		faxCheckBox.setSelected(selected);
	}

	public boolean isFaxSelected() {
		return faxCheckBox.isSelected();
	}

	public void voiceCheckBoxSetSelected(boolean selected) {
		voiceCheckBox.setSelected(selected);
	}

	public boolean isVoiceSelected() {
		return voiceCheckBox.isSelected();
	}

	public void cellCheckBoxSetSelected(boolean selected) {
		cellCheckBox.setSelected(selected);
	}

	public boolean isCellSelected() {
		return cellCheckBox.isSelected();
	}

	public void setOtherSelected(boolean selected) {
		workHomeSelector.otherRadioButtonSetSelected(selected);
	}

	public boolean isOtherSelected() {
		return workHomeSelector.isOtherRadioButtonSelected();
	}

	public void setHomeSelected(boolean selected) {
		workHomeSelector.homeRadioButtonSetSelected(selected);
	}

	public boolean isHomeSelected() {
		return workHomeSelector.isHomeRadioButtonSelected();
	}

	public void setWorkSelected(boolean selected) {
		workHomeSelector.workRadioButtonSetSelected(selected);
	}

	public boolean isWorkSelected() {
		return workHomeSelector.isWorkRadioButtonSelected();
	}

	private void initTelephoneField() {
		DocumentListener listener = new UpdateableDocumentListener(new Updateable() {
			@Override
			public void updateField() {
				//presentationModel.setNumber(telephoneField.getText());
				firePropertyChange(NUMBER_FIELD, null, null);
			}
		});
		telephoneField.getDocument().addDocumentListener(listener);
	}

	private void initItemListener() {
		final ItemListener listener = new TelTypeListener();
		cellCheckBox.addItemListener(listener);
		faxCheckBox.addItemListener(listener);
		voiceCheckBox.addItemListener(listener);
	}

	private void initWorkHomeSelectorActionListener() {
		final ActionListener al = new ButtonListener();
		workHomeSelector.otherRadioButtonAddActionListener(al);
		workHomeSelector.homeRadioButtonAddActionListener(al);
		workHomeSelector.workRadioButtonAddActionListener(al);
	}

	private void initFilter() {
		SwingTools.setDocumentFilter(telephoneField, FILTER);
	}

	private class TelTypeListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			final Object source = e.getItemSelectable();
			if (source == faxCheckBox) {
				firePropertyChange(FAX_PARAMETER, null, null);
			} else if (source == voiceCheckBox) {
				firePropertyChange(VOICE_PARAMETER, null, null);
			} else if (source == cellCheckBox) {
				firePropertyChange(CELL_PARAMETER, null, null);
			}
		}
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final String actionCommand = e.getActionCommand();
			if (WorkHomeSelector2.OTHER_ACTION_COMMAND.equals(actionCommand)) {
				firePropertyChange(OTHER_PARAMETER, null, null);
			} else if (WorkHomeSelector2.HOME_ACTION_COMMAND.equals(actionCommand)) {
				firePropertyChange(HOME_PARAMETER, null, null);
			} else if (WorkHomeSelector2.WORK_ACTION_COMMAND.equals(actionCommand)) {
				firePropertyChange(WORK_PARAMETER, null, null);
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

        telephoneLabel = new JLabel();
        telephoneField = new JTextField();
        jPanel1 = new JPanel();
        voiceCheckBox = new JCheckBox();
        cellCheckBox = new JCheckBox();
        faxCheckBox = new JCheckBox();
        workHomeSelector = new WorkHomeSelector2();

        telephoneLabel.setText(RES.getString("TelephonePanel.telephoneLabel.text_1")); // NOI18N

        telephoneField.setColumns(15);

        voiceCheckBox.setText(RES.getString("TelephonePanel.voiceCheckBox.text_1")); // NOI18N

        cellCheckBox.setText(RES.getString("TelephonePanel.cellCheckBox.text_1")); // NOI18N

        faxCheckBox.setText(RES.getString("TelephonePanel.faxCheckBox.text_1")); // NOI18N

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cellCheckBox)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(voiceCheckBox)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(faxCheckBox)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(Alignment.TRAILING, jPanel1Layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(voiceCheckBox)
                        .addComponent(cellCheckBox))
                    .addComponent(faxCheckBox, Alignment.TRAILING))
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(telephoneLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(telephoneField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(workHomeSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.CENTER)
                    .addComponent(telephoneLabel)
                    .addComponent(telephoneField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(workHomeSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JCheckBox cellCheckBox;
    private JCheckBox faxCheckBox;
    private JPanel jPanel1;
    private JTextField telephoneField;
    private JLabel telephoneLabel;
    private JCheckBox voiceCheckBox;
    private WorkHomeSelector2 workHomeSelector;
    // End of variables declaration//GEN-END:variables
}
