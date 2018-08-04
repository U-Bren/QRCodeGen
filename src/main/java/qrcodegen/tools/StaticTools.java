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
package qrcodegen.tools;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.ComboBoxModel;
import javax.swing.KeyStroke;
import javax.swing.MutableComboBoxModel;

/**
 *
 * @author Stefan Ganzer
 */
public class StaticTools {

	private static final char EXTENSION_SEPARATOR = '.';
	private static final String EMPTY_STRING = "";
	private static final int CTRL_MASK = KeyEvent.CTRL_DOWN_MASK | KeyEvent.CTRL_MASK;
	private static StaticTools INSTANCE;
	private final boolean osIsMac;
	private final Toolkit toolkit;

	public static synchronized StaticTools getSingleton() {
		if (INSTANCE == null) {
			INSTANCE = new StaticTools(Toolkit.getDefaultToolkit(), System.getProperty("os.name"));
		}
		return INSTANCE;
	}

	StaticTools(Toolkit toolkit, String osName) {
		if (toolkit == null) {
			throw new NullPointerException();
		}
		if (osName == null) {
			throw new NullPointerException();
		}
		this.toolkit = toolkit;
		String osNameLowerCase = osName.toLowerCase();
		if (osNameLowerCase.indexOf("mac") != -1) {
			osIsMac = true;
		} else {
			osIsMac = false;
		}
	}

	/**
	 * Returns the KeyCode for the given String. This method never returns null.
	 *
	 * @param s the string to return a KeyCode for
	 *
	 * @return the KeyCode for the given string
	 *
	 * @throws NullPointerException if s is null
	 * @throws IllegalArgumentException if there is no KeyCode for the given
	 * String
	 * @see KeyEvent
	 */
	public static int getKeyCodeForString(String s) {
		return getKeyStrokeForString(s).getKeyCode();
	}

	/**
	 * Returns the KeyStroke for the given String. This method never returns
	 * null.
	 *
	 * @param s the string to return a KeyCode for
	 *
	 * @return the KeyStroke for the given string
	 *
	 * @throws NullPointerException if s is null
	 * @throws IllegalArgumentException if there is no KeyStroke for the given
	 * String
	 */
	public static KeyStroke getKeyStrokeForString(String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		KeyStroke keyStroke = KeyStroke.getKeyStroke(s);
		if (keyStroke == null) {
			throw new IllegalArgumentException("There is no KeyStroke for the given input >" + s + "<.");
		}
		int modifiers = keyStroke.getModifiers();
		if (isBitSet(modifiers, CTRL_MASK)) {
			modifiers = clearBit(modifiers, CTRL_MASK);
			modifiers = setBit(modifiers, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
			int keyCode = keyStroke.getKeyCode();
			keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
		}
		return keyStroke;
	}

	/**
	 *
	 * @param s
	 *
	 * @return
	 */
	public KeyStroke getPlatformSpecificKeyStrokeForString(String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		KeyStroke keyStroke = KeyStroke.getKeyStroke(s);
		if (keyStroke == null) {
			throw new IllegalArgumentException("There is no KeyStroke for the given input >" + s + "<.");
		}
		int modifiers = keyStroke.getModifiers();
		if (isBitSet(modifiers, CTRL_MASK)) {
			modifiers = clearBit(modifiers, CTRL_MASK);
			modifiers = setBit(modifiers, toolkit.getMenuShortcutKeyMask());
			int keyCode = keyStroke.getKeyCode();
			keyStroke = KeyStroke.getKeyStroke(keyCode, modifiers);
		}
		return keyStroke;
	}

	private static boolean isBitSet(int i, int mask) {
		return (i & mask) == mask;
	}

	private static int setBit(int i, int mask) {
		return i | mask;
	}

	private static int clearBit(int i, int mask) {
		return i & ~mask;
	}

	/**
	 * Returns a localized KeyCode for the given key.
	 *
	 * @param bundle the {@link ResourceBundle} to look up the key
	 * @param key the key to look up the key code for
	 *
	 * @return a key code. Never returns null.
	 *
	 * @throws NullPointerException if bundle or key is null
	 * @throws MissingResourceException if no object for the given key can be
	 * found
	 * @throws ClassCastException if the object found for the given key is not a
	 * string
	 * @throws IllegalArgumentException if there is no KeyCode for the given
	 * String
	 *
	 */
	public static int getKeyCodeFromResourceBundle(ResourceBundle bundle, String key) {
		if (bundle == null) {
			throw new NullPointerException();
		}
		if (key == null) {
			throw new NullPointerException();
		}
		String s = bundle.getString(key);
		return getKeyCodeForString(s);
	}


	/*
	 * The following method is from
	 * http://docs.oracle.com/javase/tutorial/uiswing/examples/components/FileChooserDemo2Project/src/components/Utils.java
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
	 * Returns the extension of a file, or null if the file has no extension.
	 * The extension is the last dot-separated part (without the dot) of the
	 * given file's filename. The extension is returned in lower case in
	 * Locale.ENGLISH:
	 *
	 * @param f a file to return the extension from
	 *
	 * @return the given file's extension (without the '.' (dot)) in lower case
	 * (Locale.ENGLISH), or an empty string if it has no extension
	 *
	 * @throws NullPointerException if the given file is null
	 */
	public static String getExtension(File f) {
		String ext = EMPTY_STRING;
		String s = f.getName(); // implicit null-check
		int i = s.lastIndexOf(EXTENSION_SEPARATOR);

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase(Locale.ENGLISH);
		}
		return ext;
	}

	public static String getFileNameWOExtension(File f) {
		String ext;
		String s = f.getName(); // implicit null-check
		int i = s.lastIndexOf(EXTENSION_SEPARATOR);

		if (i > -1) {
			ext = s.substring(0, i);
		} else {
			ext = f.getName();
		}
		return ext;
	}

	/**
	 * Clips the given string s at maxLength. If {@code maxLength == -1}, or
	 * <code>s.length() &lt;= maxLength</code>, no clipping is done.
	 *
	 * @param s the string to clip. If s == null, null will be returned.
	 * @param maxLength the maximum length of the returned string. If {@code maxLength
	 * == -1}, no clipping is done.
	 *
	 * @return a new string of maxLength. The original string is returned if
	 * {@code maxLength == -1}, or if the given string is shorter than. If the
	 * given string is null, this method will return null.
	 */
	public static String clipString(String s, int maxLength) {
		if (maxLength < -1) {
			throw new IllegalArgumentException("maxLength must be >= -1, but is: " + maxLength);
		}
		if (s == null) {
			return s;
		} else if (s.length() <= maxLength || maxLength == -1) {
			return s;
		} else {
			return s.substring(0, maxLength);
		}
	}

	public static List<Image> getImagesAsList(String[] source, Class<?> aClass) {
		final List<Image> result = new ArrayList<Image>(source.length);
		for (String s : source) {
			result.add(Toolkit.getDefaultToolkit().createImage(aClass.getResource(s)));
		}
		return result;
	}

	public static String nonNullString(String s) {
		return s == null ? EMPTY_STRING : s;
	}

	public static Image loadImage(Class<?> aClass, String source) {
		URL url = aClass.getResource(source);
		return Toolkit.getDefaultToolkit().createImage(url);
	}

	public boolean osIsMac() {
		return osIsMac;
	}

	public static boolean bothNullOrEqual(Object a, Object b) {
		return (a == b) || (a != null && b != null && a.equals(b));
	}
}
