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
import qrcodegen.modules.vcardgenpanel.model.VCardEMailModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.VCardEMailPresentationModel;
import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardEMailPanel extends javax.swing.JPanel implements PropertyProviderViews {

	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/vcardgenpanel/view/VCardEMailPanel");
	private static final int MNEMONIC = getKeyCodeFromResourceBundle("MNEMONIC");
	//private final Color originalBackgroundColor;
	private final List<EMailPanel> emailPanels;
	private final VCardEMailPresentationModel presentationModel;
	private final PropertyChangeListener panelListener = new PanelListener();
	private EMailPanel currentPanel;

	/** Creates new form VCardNamePanel */
	public VCardEMailPanel() {
		this(new VCardEMailPresentationModel(new VCardEMailModel()));
	}

	public VCardEMailPanel(VCardEMailPresentationModel presentationModel) {
		if (presentationModel == null) {
			throw new NullPointerException();
		}
		this.presentationModel = presentationModel;
		initComponents();
		setName(RES.getString("EMAIL"));
		jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.PAGE_AXIS));
		emailPanels = Arrays.asList(
				new EMailPanel(),
				new EMailPanel(),
				new EMailPanel());
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

		for (Component p : emailPanels) {
			jPanel1.add(p);
			p.addPropertyChangeListener(panelListener);
		}
	}

	private class ModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (VCardEMailPresentationModel.ENTRY_SELECTED.equals(propertyName)) {
				if (presentationModel.isEntrySelected()) {
					int index = presentationModel.getSelectedEntry();
					currentPanel = emailPanels.get(index);
				}
			} else if (VCardEMailPresentationModel.EMAIL_ELEMENT.equals(propertyName)) {
				currentPanel.setEMail(presentationModel.getEMail());
			} else if (VCardEMailPresentationModel.TYPE_PARAMETER.equals(propertyName)) {
				Set<TypeParameter> c = presentationModel.getTypeParameters();
				if (c.contains(TypeParameter.HOME)) {
					currentPanel.setHomeSelected(true);
				} else if (c.contains(TypeParameter.WORK)) {
					currentPanel.setWorkSelected(true);
				} else {
					currentPanel.setOtherSelected(true);
				}
			}
		}
	}

	private class PanelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			Object source = evt.getSource();
			int index = emailPanels.indexOf(source);
			if (index != -1) {
				presentationModel.selectEntry(index);
				currentPanel = (EMailPanel) source;
			} else {
				System.err.println("VCardEMailPanel:PanelListener:index==-1");
			}
			if (EMailPanel.EMAIL_FIELD.equals(propertyName)) {
				presentationModel.setEMail(currentPanel.getEMail());
			}else if (EMailPanel.WORK_PARAMETER.equals(propertyName)) {
				presentationModel.setTypeParameterWork();
			} else if (EMailPanel.HOME_PARAMETER.equals(propertyName)) {
				presentationModel.setTypeParameterHome();
			} else if (EMailPanel.OTHER_PARAMETER.equals(propertyName)) {
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