/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcardgenpanel.view;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Stefan Ganzer
 */
public class UpdateableDocumentListener implements DocumentListener {

		private final Updateable u;

		public UpdateableDocumentListener(Updateable u) {
			if (u == null) {
				throw new NullPointerException();
			}
			this.u = u;
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			u.updateField();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			u.updateField();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			u.updateField();
		}
	}