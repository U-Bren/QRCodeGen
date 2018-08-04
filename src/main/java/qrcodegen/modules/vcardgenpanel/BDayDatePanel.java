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
package qrcodegen.modules.vcardgenpanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import qrcodegen.documentfilter.IntegerRangeDocumentFilter;
import qrcodegen.modules.vcardgenpanel.view.Updateable;
import qrcodegen.modules.vcardgenpanel.view.UpdateableDocumentListener;
import qrcodegen.modules.vcardgenpanel.model.DateModel;
import qrcodegen.modules.vcardgenpanel.model.VCardBDayModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.DatePresentationModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.VCardBDayPresentationModel;
import qrcodegen.tools.SwingTools;

/**
 *
 * @author Stefan Ganzer
 */
public class BDayDatePanel extends javax.swing.JPanel {

	static final String PANEL_STATUS_PROPERTY = "panelStatus";
	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/vcardgenpanel/BDayDatePanel");
	private static final int MAX_YEAR = 9999;
	private static final int MIN_YEAR = 0;
	private static final int MAX_MONTH = 12;
	private static final int MIN_MONTH = 1;
	private static final int MAX_DAY = 31;
	private static final int MIN_DAY = 1;
	private final ImageIcon failureIcon = SwingTools.getSingleton().createImageIcon(this.getClass(), "/qrcodegen/modules/vcardgenpanel/view/images/Stop16.gif", null);
	private final VCardBDayPresentationModel presentationModel;

	public BDayDatePanel() {
		this(new VCardBDayPresentationModel(new VCardBDayModel()));
	}

	/** Creates new form BDayDatePanel
	 *
	 * @param model
	 */
	public BDayDatePanel(VCardBDayPresentationModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		initComponents();
		initLabel();
		initDocumentFilter();
		initDocumentListener();
		this.presentationModel = model;
		this.presentationModel.addPropertyChangeListener(new DateModelListener());
	}

	private void initLabel() {
		yearLabel.setLabelFor(yearField);
		monthLabel.setLabelFor(monthField);
		dayLabel.setLabelFor(dayField);
	}

	/**
	 * Updates the DatePresentationModel on input changes.
	 */
	private void initDocumentListener() {

		Updateable yearUpdateable = new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setYear(yearField.getText());
			}
		};

		Updateable monthUpdateable = new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setMonth(monthField.getText());
			}
		};
		Updateable dayUpdateable = new Updateable() {
			@Override
			public void updateField() {
				presentationModel.setDay(dayField.getText());
			}
		};
		((AbstractDocument) yearField.getDocument()).addDocumentListener(new UpdateableDocumentListener(yearUpdateable));
		((AbstractDocument) monthField.getDocument()).addDocumentListener(new UpdateableDocumentListener(monthUpdateable));
		((AbstractDocument) dayField.getDocument()).addDocumentListener(new UpdateableDocumentListener(dayUpdateable));
	}

	private void initDocumentFilter() {
		DocumentFilter yearFilter = new IntegerRangeDocumentFilter(MIN_YEAR, MAX_YEAR);//new DocumentSizeFilter(MAX_YEAR_LENGTH, digitFilter);
		DocumentFilter monthFilter = new IntegerRangeDocumentFilter(MIN_MONTH, MAX_MONTH);
		DocumentFilter dayFilter = new IntegerRangeDocumentFilter(MIN_DAY, MAX_DAY);

		SwingTools.setDocumentFilter(yearField, yearFilter);
		SwingTools.setDocumentFilter(monthField, monthFilter);
		SwingTools.setDocumentFilter(dayField, dayFilter);
	}

	@Override
	public void setEnabled(boolean enabled) {
		yearField.setEnabled(enabled);
		monthField.setEnabled(enabled);
		dayField.setEnabled(enabled);
		// set validity to undefined it is invalid when disabling this panel,
		// so any failure status message is removed
		super.setEnabled(enabled);
	}

	/**
	 * This method should only be called by the PropertyChangeListener
	 */
	private void setStatusLabel() {
		statusLabel.setIcon(failureIcon);
	}

	/**
	 * This method should only be called by the PropertyChangeListener
	 */
	private void resetStatusLabel() {
		statusLabel.setIcon(null);
	}

	private class DateModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (VCardBDayPresentationModel.YEAR_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getYear();
				if (!newValue.equals(yearField.getText())) {
					yearField.setText(presentationModel.getYear());
				}
			} else if (VCardBDayPresentationModel.MONTH_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getMonth();
				if (!newValue.equals(monthField.getText())) {
					monthField.setText(presentationModel.getMonth());
				}
			} else if (VCardBDayPresentationModel.DAY_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getDay();
				if (!newValue.equals(dayField.getText())) {
					dayField.setText(presentationModel.getDay());
				}
			} else if (VCardBDayPresentationModel.VALIDITY.equals(propertyName)) {
				InputValidity iv = (InputValidity) evt.getNewValue();
				if (iv != null) {
					if (InputValidity.INVALID == iv) {
						setStatusLabel();
					} else {
						resetStatusLabel();
					}
				}
			} else if (VCardBDayPresentationModel.DATE_FORMAT_ELEMENT.equals(propertyName)) {
				setEnabled(presentationModel.isDateEnabled());
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

        dayField = new JTextField();
        dayLabel = new JLabel();
        monthField = new JTextField();
        monthLabel = new JLabel();
        yearField = new JTextField();
        yearLabel = new JLabel();
        statusLabel = new JLabel();
        dateLabel = new JLabel();
        jLabel1 = new JLabel();

        dayField.setColumns(2);
        dayField.setToolTipText(RES.getString("BDayDatePanel.dayField.toolTipText")); 
        dayLabel.setText(RES.getString("BDayDatePanel.dayLabel.text")); 
        monthField.setColumns(2);
        monthField.setToolTipText(RES.getString("BDayDatePanel.monthField.toolTipText")); 
        monthLabel.setText(RES.getString("BDayDatePanel.monthLabel.text")); 
        yearField.setColumns(4);
        yearField.setToolTipText(RES.getString("BDayDatePanel.yearField.toolTipText")); 
        yearLabel.setText(RES.getString("BDayDatePanel.yearLabel.text")); 
        statusLabel.setText(RES.getString("BDayDatePanel.statusLabel.text")); 
        dateLabel.setText(RES.getString("BDayDatePanel.dateLabel.text")); 
        jLabel1.setText(RES.getString("BDayDatePanel.jLabel1.text")); 
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dateLabel)
                        .addGap(18, 18, 18)
                        .addComponent(yearLabel)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(yearField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(monthLabel)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(monthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(dayLabel)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(dayField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16)
                        .addComponent(statusLabel))
                    .addComponent(jLabel1))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(yearLabel)
                    .addComponent(monthLabel)
                    .addComponent(monthField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(dayLabel)
                    .addComponent(dayField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(statusLabel)
                    .addComponent(dateLabel)
                    .addComponent(yearField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel dateLabel;
    private JTextField dayField;
    private JLabel dayLabel;
    private JLabel jLabel1;
    private JTextField monthField;
    private JLabel monthLabel;
    private JLabel statusLabel;
    private JTextField yearField;
    private JLabel yearLabel;
    // End of variables declaration//GEN-END:variables
}
