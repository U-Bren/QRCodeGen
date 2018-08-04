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

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcardgenpanel.PropertyProviderViews;
import qrcodegen.modules.vcardgenpanel.model.VCardTelModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.TelephonePresentationModel;
import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardTelephonePanel extends javax.swing.JPanel implements PropertyProviderViews {

	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/vcardgenpanel/view/VCardTelephonePanel");
	private static final int MNEMONIC = getKeyCodeFromResourceBundle("MNEMONIC");
	private final List<TelephonePanel> telephonePanels;
	private final TelephonePresentationModel presentationModel;
	private final PropertyChangeListener panelListener = new PanelListener();
	private TelephonePanel currentPanel;

	/** Creates new form VCardNamePanel */
	public VCardTelephonePanel() {
		this(new TelephonePresentationModel(new VCardTelModel()));
	}

	public VCardTelephonePanel(TelephonePresentationModel presentationModel) {
		if (presentationModel == null) {
			throw new NullPointerException();
		}
		this.presentationModel = presentationModel;
		initComponents();
		setName(RES.getString("TELEPHONE"));
		jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.PAGE_AXIS));
		telephonePanels = Arrays.asList(
				new TelephonePanel(),
				new TelephonePanel(),
				new TelephonePanel());
		initSubpanels();
		this.presentationModel.addPropertyChangeListener(new ModelListener());
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

	private void initSubpanels() {

		for (Component p : telephonePanels) {
			jPanel1.add(p);
			p.addPropertyChangeListener(panelListener);
		}
	}

	private class ModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (TelephonePresentationModel.ENTRY_SELECTED.equals(propertyName)) {
				if (presentationModel.isEntrySelected()) {
					int index = presentationModel.getSelectedEntry();
					currentPanel = telephonePanels.get(index);
				}
			} else if (TelephonePresentationModel.NUMBER_ELEMENT.equals(propertyName)) {
				currentPanel.setNumber(presentationModel.getNumber());
			} else if (TelephonePresentationModel.TYPE_PARAMETER.equals(propertyName)) {
				Set<TypeParameter> c = presentationModel.getTypeParameters();
				if (c.contains(TypeParameter.HOME)) {
					currentPanel.setHomeSelected(true);
				} else if (c.contains(TypeParameter.WORK)) {
					currentPanel.setWorkSelected(true);
				} else {
					currentPanel.setOtherSelected(true);
				}
				currentPanel.voiceCheckBoxSetSelected(c.contains(TypeParameter.VOICE));
				currentPanel.cellCheckBoxSetSelected(c.contains(TypeParameter.CELL));
				currentPanel.faxCheckBoxSetSelected(c.contains(TypeParameter.FAX));
			}
		}
	}

	private class PanelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			Object source = evt.getSource();
			int index = telephonePanels.indexOf(source);
			if (index != -1) {
				presentationModel.selectEntry(index);
				currentPanel = (TelephonePanel) source;
			} else {
				System.err.println("VCardTelephonePanel:PanelListener:index==-1");
			}
			if (TelephonePanel.NUMBER_FIELD.equals(propertyName)) {
				presentationModel.setNumber(currentPanel.getNumber());
			} else if (TelephonePanel.FAX_PARAMETER.equals(propertyName)) {
				presentationModel.setFaxParameter(currentPanel.isFaxSelected());
			} else if (TelephonePanel.CELL_PARAMETER.equals(propertyName)) {
				presentationModel.setCellParameter(currentPanel.isCellSelected());
			} else if (TelephonePanel.VOICE_PARAMETER.equals(propertyName)) {
				presentationModel.setVoiceParameter(currentPanel.isVoiceSelected());
			} else if (TelephonePanel.WORK_PARAMETER.equals(propertyName)) {
				presentationModel.setTypeParameterWork();
			} else if (TelephonePanel.HOME_PARAMETER.equals(propertyName)) {
				presentationModel.setTypeParameterHome();
			} else if (TelephonePanel.OTHER_PARAMETER.equals(propertyName)) {
				presentationModel.setTypeParameterOther();
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

        scrollPane = new JScrollPane();
        jPanel1 = new JPanel();

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGap(0, 144, Short.MAX_VALUE)
        );

        scrollPane.setViewportView(jPanel1);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(scrollPane)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel jPanel1;
    private JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}