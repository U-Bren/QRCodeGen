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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Printable;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import qrcodegen.tools.StaticTools;

/**
 * A ContentModule implementation for creating QR Codes from valid email
 * addresses.
 *
 * @author Stefan Ganzer
 */
public class EMailPanel extends AbstractOneSourcePanel {

	private static final ResourceBundle res = ResourceBundle.getBundle("qrcodegen/modules/EMailPanel");
	/** All uppercase so an QR Code with com.google.zxing.qrcode.decoder.Mode ALPHANUMERIC can be generated. */
	private static final String MAIL_TO = "MAILTO:"; //NOI18N
	private static final int MNEMONIC = getKeyCodeFromResourceBundle("MAIL_MNEMONIC");
	// From http://www.regular-expressions.info/email.html,
	// that's the same pattern that Google uses in its Validators-class,
	// but with CASE_INSENSITIVE set to true
	private static final String PATTERN_STRING = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$"; //NOI18N
	private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING, Pattern.CASE_INSENSITIVE);
	/** Maximum number of characters allowed in the sourceField. */
	private static final int MAX_CHARS = 1000;

	public EMailPanel() {
		super(MAX_CHARS);
		setName(res.getString("EMAIL"));
		initSourceField();

	}

	/**
	 * This method is invoked by a constructor.
	 */
	private void initSourceField() {
		sourceLabel.setText(res.getString("EMAIL"));
		sourceLabel.setDisplayedMnemonic(MNEMONIC);

		final EMailVerifier emailVerifier = new EMailVerifier();
		sourceField.setInputVerifier(emailVerifier);
		sourceField.addActionListener(emailVerifier);
	}

	@Override
	public int getMnemonic() {
		return MNEMONIC;
	}

	@Override
	String getEncodedContent() {
		return MAIL_TO.concat(sourceField.getText());
	}

	@Override
	public Printable getPrintable(Image qrcode) {
		return null;
	}

	@Override
	public String getJobName() {
		return EMPTY_STRING;
	}

	private static int getKeyCodeFromResourceBundle(String key) {
		assert key != null;
		return StaticTools.getKeyCodeForString(res.getString(key));
	}


	/* The EMailVerifier is based on a example provided by Oracle, which can be found
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
	/**
	 * @author Stefan Ganzer
	 */
	private class EMailVerifier extends InputVerifier implements ActionListener {

		@Override
		public boolean shouldYieldFocus(JComponent input) {

			final boolean inputOk = makeItPretty(input);

			if (inputOk) {
				statusField.setText(EMPTY_STRING);
				setValidInput(true);
				setInvalidatedInputOnce(false);
			} else {
				statusField.setText(res.getString("NOT A VALID EMAIL ADDRESS"));
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

		private boolean makeItPretty(JComponent input) {
			return checkField(input, true);
		}

		private boolean checkField(JComponent input, boolean changeIt) {
			boolean result = false;

			if (input instanceof JTextField) {
				JTextField ftf = (JTextField) input;
				String value = ftf.getText();
				result = PATTERN.matcher(value).matches();
			}
			return result;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextField source = (JTextField) e.getSource();
			if (shouldYieldFocus(source)) {
				generate();
			}
		}
	}
}
