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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import qrcodegen.documentfilter.DocumentEncodableFilter;
import qrcodegen.documentfilter.DocumentSizeFilter;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.modules.vcardgenpanel.model.VCardUrlModel;
import qrcodegen.modules.vcardgenpanel.presentationmodel.VCardUrlPresentationModel;
import qrcodegen.tools.SwingTools;

/**
 *
 * @author Stefan Ganzer
 */
public class URLPanel extends javax.swing.JPanel {

	public static final String PANEL_STATUS = "PanelStatus";
	private static final String HTTP_PREFIX = "HTTP://";
	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/vcardgenpanel/view/URLPanel");
	private static final DocumentFilter FILTER = new DocumentSizeFilter(100, new DocumentEncodableFilter(Charset.forName("UTF-8")));
	private final ImageIcon failureIcon = SwingTools.getSingleton().createImageIcon(this.getClass(), "/qrcodegen/modules/vcardgenpanel/view/images/Stop16.gif", null);
	private final VCardUrlPresentationModel presentationModel;

	/** Creates new form URLPanel */
	URLPanel() {
		this(new VCardUrlPresentationModel(new VCardUrlModel()));
	}

	/**
	 *
	 * @param model
	 */
	public URLPanel(VCardUrlPresentationModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		this.presentationModel = model;
		initComponents();
		initUrlField();
		initWorkHomeSelectorActionListener();
		presentationModel.addPropertyChangeListener(new ModelListener());
	}

	private void initWorkHomeSelectorActionListener() {
		final ActionListener al = new ButtonListener();
		workHomeSelector.otherRadioButtonAddActionListener(al);
		workHomeSelector.homeRadioButtonAddActionListener(al);
		workHomeSelector.workRadioButtonAddActionListener(al);
	}

	private void initUrlField() {
		final URLVerifier urlVerifier = new URLVerifier();
		urlField.setInputVerifier(urlVerifier);
		SwingTools.setDocumentFilter(urlField, FILTER);
		Document document = urlField.getDocument();
		document.addDocumentListener(new DocumentListener() {
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
				presentationModel.setUrl(urlField.getText());
			}
		});

	}

	//<editor-fold defaultstate="collapsed" desc="Copyright Oracle">
	/* The URLVerifier is based on a example provided by Oracle, which can be found
	 * here:
	 * http://download.oracle.com/javase/tutorial/uiswing/examples/misc/InputVerificationDemoProject/src/misc/InputVerificationDemo.java
	 */
	/*
	 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights
	 * reserved.
	 *
	 * Redistribution and use in source and binary forms, with or without
	 * modification, are permitted provided that the following conditions are
	 * met:
	 *
	 * - Redistributions of source code must retain the above copyright notice,
	 * this list of conditions and the following disclaimer.
	 *
	 * - Redistributions in binary form must reproduce the above copyright
	 * notice, this list of conditions and the following disclaimer in the
	 * documentation and/or other materials provided with the distribution.
	 *
	 * - Neither the name of Oracle or the names of its contributors may be used
	 * to endorse or promote products derived from this software without
	 * specific prior written permission.
	 *
	 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
	 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
	 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
	 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
	 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
	 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
	 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
	 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
	 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
	 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
	 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
	 */
	//</editor-fold>
	/**
	 * @author Stefan Ganzer
	 */
	private class URLVerifier extends InputVerifier {

		@Override
		public boolean shouldYieldFocus(JComponent input) {
			final boolean inputOk = makeItPretty(input);

			if (inputOk) {
			} else {
				Toolkit.getDefaultToolkit().beep();
				((JTextField) input).selectAll();
			}
			return inputOk;
		}

		//This method checks input, but should cause no side effects.
		@Override
		public boolean verify(JComponent input) {
			return checkField(input, false);
		}

		private boolean checkField(JComponent input, boolean changeIt) {
			boolean result = false;

			if (input instanceof JTextField) {
				result = isURLFieldValid(changeIt);
			}
			return result;
		}

		private boolean makeItPretty(JComponent input) {
			return checkField(input, true);
		}
	}

	private void setStatusLabel() {
		statusLabel.setIcon(failureIcon);
	}

	private void resetStatusLabel() {
		statusLabel.setIcon(null);
	}

	private boolean isURLFieldValid(boolean changeIt) {
		String value = urlField.getText().trim();
		try {
			if (value.isEmpty()) {
				return true;
			}
			// The result is intentionally ignored -
			// we just want to know if the URL is valid
			URI uri = new URI(value);
			if (uri.isAbsolute()) {
				return true;
			} else {
				if (changeIt) {
					String alternative = HTTP_PREFIX.concat(value);
					new URI(alternative);
					urlField.setText(alternative);
					return true;
				} else {
					return false;
				}
			}
		} catch (URISyntaxException swallowed) {
			// nothing to do about it here
			return false;
		}
	}

	private class ModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (VCardUrlPresentationModel.URL_ELEMENT.equals(propertyName)) {
				String newValue = presentationModel.getUrl();
				if (!newValue.equals(urlField.getText())) {
					urlField.setText(newValue);
				}
			} else if (VCardUrlPresentationModel.TYPE_PARAMETER.equals(propertyName)) {
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
			} else if (VCardUrlPresentationModel.VALIDITY.equals(propertyName)) {
				if (evt.getNewValue() instanceof InputValidity) {
					InputValidity iv = (InputValidity) evt.getNewValue();
					if (InputValidity.INVALID == iv) {
						setStatusLabel();
					} else {
						resetStatusLabel();
					}
				}
				firePropertyChange(PANEL_STATUS, evt.getOldValue(), evt.getNewValue());
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

	/** This method is called from within the constructor to initialize the
	 * form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        urlLabel = new JLabel();
        urlField = new JTextField();
        statusLabel = new JLabel();
        workHomeSelector = new WorkHomeSelector2();

        urlLabel.setText(RES.getString("URLPanel.urlLabel.text_1")); // NOI18N

        urlField.setColumns(15);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(urlLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(urlField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addComponent(statusLabel)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(workHomeSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.CENTER)
                    .addComponent(urlLabel)
                    .addComponent(urlField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(statusLabel)
                    .addComponent(workHomeSelector, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel statusLabel;
    private JTextField urlField;
    private JLabel urlLabel;
    private WorkHomeSelector2 workHomeSelector;
    // End of variables declaration//GEN-END:variables
}
