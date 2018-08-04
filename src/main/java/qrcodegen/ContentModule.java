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
package qrcodegen;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.print.Printable;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.swing.event.ChangeListener;

/**
 * A ContentModule is a plug-in for the main QRCodeGen module. It provides the
 * data to create a QR-Code from as String via the
 * {@link getContent()} method. The main program is informed via a ChangeEvent
 * that a ContentModule provides new data, or that the data has changed. Use {@link addChangeListener(ChangeListener)}
 * and {@link removeChangeListener(ChangeListener)} to register/unregister a
 * listener. The user interface of a ContentModule can be obtained by invoking
 * {@link getComponent}. The main program can use the mnemonic obtained via {@link getMnemonic}
 * to provide easy access to the ContentModule.
 *
 * @author Stefan Ganzer
 */
public interface ContentModule extends ContentGenerator {

	/**
	 * Returns this ContentModule's component.
	 *
	 * @return this ContentModule's component.
	 */
	Component getComponent();

	/**
	 * Returns the character that a ContentModule implementation suggests to be
	 * used as Mnemonic for being accessed from the main module. The character
	 * has to be a valid {@link java.awt.event.KeyEvent} code.
	 *
	 * @return a valid {@link java.awt.event.KeyEvent} code
	 */
	int getMnemonic();

	// TODO Remove the Image from getPrintable
	// Most modules don't need it, and the few that do need it
	// probably need even more information, so they'll store a reference
	// to the main module QRView (maybe they need the current code-image to
	// provide a print preview)
	/**
	 * A ContentModule is allowed to return null if it doesn't provide a custom
	 * Printable.
	 *
	 * @param qrcode the QR-Code the main module has generated from the
	 * Encodable this module provides
	 *
	 * @return a Printable or null if this ContentModule doesn't provide a
	 * custom Printable
	 */
	Printable getPrintable(Image qrcode);

	/**
	 * Returns the name of the print job, if the main module prints the QR Code
	 * generated from the data provided by a ContentModule implementation. This
	 * string can be displayed to the user, appear on the printout, ... so it
	 * should be a succinct description of the input data.
	 *
	 * @return the name of the print job. The return value mustn't be null.
	 */
	String getJobName();

	/**
	 * Returns true if this module requires specific encodings, false otherwise.
	 * A module returning true is obliged to return a non-null, non-empty set of
	 * encodings that is a subset of the encodings made available by the main
	 * program.
	 *
	 * VCard v4 for instance must use UTF-8, so such a module would return true.
	 *
	 * @return true if this module requires specific encodings, false otherwise
	 */
	boolean restrictsEncoding();

	/**
	 * If restrictsEncoding returns true, this method returns a non-null,
	 * non-empty set of encodings that is a subset of the encodings made
	 * available by the main program. Otherwise this method may return an empty
	 * set or even null.
	 *
	 * @return
	 */
	Set<Charset> getEncodingSubset();
}

/**
 * NullContentModule is a null-object implementation of the ContentModule
 * interface.
 *
 * @author Stefan Ganzer
 */
class NullContentModule implements ContentModule {

	private static final String EMPTY_STRING = "";
	private static final String NULL_CONTENT_MODULE_COMPONENT = "NullContentModuleComponent";
	private static final Set<Charset> AVAILABLE_ENCODINGS = Collections.unmodifiableSet(new LinkedHashSet<Charset>(Arrays.asList(Charset.forName("ISO-8859-1"), Charset.forName("Shift_JIS"), Charset.forName("UTF-8")))); //NOI18N
	private final Component component = newComponent();

	public static NullContentModule getInstance() {
		return new NullContentModule();
	}

	// Don't let anyone instantiate this directly.
	private NullContentModule() {
	}

	@Override
	public String getContent() {
		return EMPTY_STRING;
	}

	@Override
	public Component getComponent() {
		return component;
	}

	@Override
	public int getMnemonic() {
		return KeyEvent.VK_UNDEFINED;
	}

	@Override
	public Printable getPrintable(Image qrcode) {
		return null;
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		//nothing to do
	}

	@Override
	public void removeChangeListener(ChangeListener listener) {
		//nothing to do
	}

	@Override
	public String getJobName() {
		return EMPTY_STRING;
	}

	private static Component newComponent() {
		Component c = new Component() {

			private static final long serialVersionUID = 1L;
		};
		c.setName(NULL_CONTENT_MODULE_COMPONENT);
		return c;
	}

	@Override
	public boolean restrictsEncoding() {
		return false;
	}

	@Override
	public Set<Charset> getEncodingSubset() {
		return AVAILABLE_ENCODINGS;
	}
}