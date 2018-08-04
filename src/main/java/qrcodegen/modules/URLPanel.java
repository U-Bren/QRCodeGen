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
import java.awt.event.KeyEvent;
import java.awt.print.Printable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * A ContentModule implementation for creating QR Codes from valid URLs.
 *
 * @author Stefan Ganzer
 */
public class URLPanel extends AbstractOneSourcePanel {

	private static final ResourceBundle res = ResourceBundle.getBundle("qrcodegen/modules/URLPanel");
	/** All uppercase so an QR Code with com.google.zxing.qrcode.decoder.Mode
	 * ALPHANUMERIC can be generated. */
	private static final String HTTP = "HTTP://"; //NOI18N
	private static final int MNEMONIC = KeyEvent.VK_U;
	private static final String PATTERN_STRING = "^(https?|ftp)://(?:\\P{M}\\p{M}*)+"; //NOI18N
	private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING, Pattern.CASE_INSENSITIVE);
	/** Maximum number of characters allowed in the sourceField. A long input
	 * can crash the regex machine. */
	private static final int MAX_CHARS = 500;

	public URLPanel() {
		super(MAX_CHARS);
		setName(res.getString("URL"));
		initSourceField();
	}

	private void initSourceField() {
		sourceLabel.setText(res.getString("URL"));
		sourceLabel.setDisplayedMnemonic(MNEMONIC);

		final URLVerifier urlVerifier = new URLVerifier();
		sourceField.setInputVerifier(urlVerifier);
		sourceField.addActionListener(urlVerifier);
	}

	@Override
	public int getMnemonic() {
		return MNEMONIC;
	}

	@Override
	String getEncodedContent() {
		return sourceField.getText();
	}

	@Override
	public Printable getPrintable(Image qrcode) {
		return null;
	}

	@Override
	public String getJobName() {
		return EMPTY_STRING;
	}

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
	/**
	 * @author Stefan Ganzer
	 */
	private class URLVerifier extends InputVerifier implements ActionListener {

		@Override
		public boolean shouldYieldFocus(JComponent input) {
			final boolean inputOk = makeItPretty(input);

			if (inputOk) {
				statusField.setText(EMPTY_STRING);
				setValidInput(true);
				setInvalidatedInputOnce(false);
			} else {
				statusField.setText(res.getString("NOT A VALID URL"));
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
				JTextField ftf = (JTextField) input;
				String value = ftf.getText().trim();
				if (value.isEmpty()) {
					return false;
				}
				/*
				 * Test if the given value is an URL. If not, try to prepend
				 * 'http://', and test again. If it's still not an URL, restore
				 * the old value
				 */
				if (!PATTERN.matcher(value).matches()) {
					String oldValue = value;
					value = HTTP.concat(value);
					if (!PATTERN.matcher(value).matches()) {
						value = oldValue;
					}
				}
				try {
					// The result is intentionally ignored -
					// we just want to know if the URL is valid
					new URI(value);
					result = true;
				} catch (URISyntaxException swallowed) {
					// nothing to do about it here
				}
				if (result && changeIt) {
					if (!value.equals(ftf.getText())) {
						ftf.setText(value);
					}
				}
			}
			return result;
		}

		private boolean makeItPretty(JComponent input) {
			return checkField(input, true);
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
