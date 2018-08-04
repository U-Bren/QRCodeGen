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
package qrcodegen;

import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Static methods to make an JLabel containing an HTTP-link or an eMail-address
 * clickable, with the look&feel the user is expecting from a clickable link,
 * that is the link is presented as a blue underlined string, and the cursor
 * changes to a hand when it is moved over the link.
 *
 * @author Stefan Ganzer
 */
public class Linkify {

	/** The resourse bundle for this class AboutDialog */
	private static final ResourceBundle res = ResourceBundle.getBundle("qrcodegen/Linkify");
	private static final Logger LOGGER = Logger.getLogger(Linkify.class.getPackage().getName());
	private static final String A_HREF = "<a href=\"";
	private static final String HREF_CLOSED = "\">";
	private static final String HREF_END = "</a>";
	private static final String HTML = "<html>";
	private static final String HTML_END = "</html>";
	private static final String MAILTO = "mailto:";
	private static final MouseListener LISTENER = new LinkMouseListener();

	private enum LinkType {

		HTTP, MAIL;
	}

	private Linkify() {
		// static class - don't instantiate
	}

	/**
	 * <b>This method does no escaping! The text of the provided JLabel must be
	 * a valid HTTP-link!</b>
	 *
	 * @param l
	 */
	public static void makeHTTPLinkable(JLabel l) {
		makeHTTPLinkable(l, LISTENER);
	}

	/**
	 * <b>This method does no escaping! The text of the provided JLabel must be
	 * a valid HTTP-link!</b>
	 *
	 * @param c
	 * @param ml
	 */
	public static void makeHTTPLinkable(JLabel c, MouseListener ml) {
		if (c == null) {
			throw new NullPointerException();
		}
		if (ml == null) {
			throw new NullPointerException();
		}
		makeLinkable(c, ml, null, null, null, LinkType.HTTP);
	}

	/**
	 * <b>This method does no escaping! The text of the provided JLabel,
	 * address, subject and body mustn't contain any characters not allowed in
	 * an URI!</b>
	 *
	 * @param l the text of this JLabel is displayed to the user
	 * @param address the address to link to. May be a complete address scheme.
	 * If address is null or empty, the address is obtained from the JLabel via
	 * getText()
	 * @param subject a subject. May be null.
	 * @param body a mail body. May be null.
	 *
	 * @throws NullPointerException l is null
	 */
	public static void makeMailLinkable(JLabel l, String address, String subject, String body) {
		makeMailLinkable(l, LISTENER, address, subject, body);
	}

	/**
	 * <b>This method does no escaping! The text of the provided JLabel, subject
	 * and body mustn't contain any characters not allowed in an URI!</b>
	 *
	 * @param c the text of this JLabel is displayed to the user
	 * @param ml
	 * @param address the address to link to. May be a complete address scheme.
	 * If address is null or empty, the address is obtained from the JLabel via
	 * getText()
	 * @param subject a subject. May be null.
	 * @param body a mail body. May be null.
	 *
	 * @throws NullPointerException if c or ml is null
	 */
	public static void makeMailLinkable(JLabel c, MouseListener ml, String address, String subject, String body) {
		if (c == null) {
			throw new NullPointerException();
		}
		if (ml == null) {
			throw new NullPointerException();
		}
		makeLinkable(c, ml, address, subject, body, LinkType.MAIL);
	}

	private static void makeLinkable(JLabel c, MouseListener ml, String address, String subject, String body, LinkType type) {
		assert c != null;
		assert ml != null;

		switch (type) {
			case HTTP:
				c.setText(htmlIfy(linkIfy(c.getText())));
				break;
			case MAIL:
				c.setText(htmlIfy((linkIfyMail(c.getText(), address, subject, body))));
				break;
			default:
				throw new AssertionError(type);
		}

		c.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		c.addMouseListener(ml);
	}

	/**
	 * Returns the provided string without an enclosing href attribute.
	 *
	 * @param s
	 *
	 * @return the provided string without an enclosing href attribute
	 *
	 * @throws NullPointerException if s is null
	 * @throws IllegalArgumentException if s is not a hypertext reference
	 */
	public static String stripHRef(String s) {
		if (s == null) {
			throw new NullPointerException();
		}
		try {
			return s.substring(s.indexOf(A_HREF) + A_HREF.length(), s.indexOf(HREF_CLOSED));

		} catch (IndexOutOfBoundsException iobe) {
			throw new IllegalArgumentException("Not a valid link", iobe);
		}
	}

	//WARNING
	//This method requires that address, subject and body are plain strings that
	// need no further escaping
	private static String linkIfyMail(String displayAddress, String address, String subject, String body) {
		String addressPart = (address == null || address.isEmpty()) ? displayAddress : address;
		String subjectPart = (subject == null || subject.isEmpty()) ? "" : "subject=" + subject;
		String bodyPart = (body == null || body.isEmpty()) ? "" : "body=" + body;

		String optionIdentifier = (subjectPart.isEmpty() && bodyPart.isEmpty()) ? "" : "?";
		String optionSeparator = !(subjectPart.isEmpty() || bodyPart.isEmpty()) ? "&" : "";

		return A_HREF
				+ MAILTO
				+ addressPart + optionIdentifier + subjectPart + optionSeparator + bodyPart
				+ HREF_CLOSED
				+ displayAddress
				+ HREF_END;
	}
	//WARNING
	//This method requires that s is a plain string that needs
	//no further escaping

	private static String linkIfy(String s) {
		return A_HREF + s + HREF_CLOSED + s + HREF_END;
	}

	//WARNING
	//This method requires that s is a plain string that needs
	//no further escaping
	private static String htmlIfy(String s) {
		return HTML + s + HTML_END;
	}

	private static class LinkMouseListener extends MouseAdapter {

		private static final int LEFT_BUTTON = 1;

		@Override
		public void mouseClicked(java.awt.event.MouseEvent evt) {
			if (evt.getButton() == LEFT_BUTTON) {
				JLabel l = (JLabel) evt.getSource();
				try {
					URI uri = new java.net.URI(Linkify.stripHRef(l.getText()));
					new Thread(new LinkRunner(uri)).start();
				} catch (URISyntaxException use) {
					throw new AssertionError(use + ": " + l.getText()); //NOI18N
				}
			}
		}
	}

	private static class LinkRunner implements Runnable {

		private final URI uri;

		private LinkRunner(URI u) {
			if (u == null) {
				throw new NullPointerException();
			}
			uri = u;
		}

		@Override
		public void run() {
			try {
				Desktop desktop = java.awt.Desktop.getDesktop();
				desktop.browse(uri);
			} catch (UnsupportedOperationException uoe) {
				handleException(uri, uoe);
			} catch (IOException ioe) {
				handleException(uri, ioe);
			} catch (IllegalArgumentException iae) {
				handleException(uri, iae);
			}
		}

		private static void handleException(URI u, Exception e) {
			LOGGER.log(Level.FINER, u.toString(), e);
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, res.getString("SORRY, A PROBLEM OCCURRED WHILE TRYING TO OPEN THIS LINK IN YOUR SYSTEM'S STANDARD BROWSER."), res.getString("A PROBLEM OCCURED"), JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}

	public static boolean isBrowsingSupported() {
		if (!Desktop.isDesktopSupported()) {
			return false;
		}
		boolean result = false;
		Desktop desktop = java.awt.Desktop.getDesktop();
		if (desktop.isSupported(Desktop.Action.BROWSE)) {
			result = true;
		}
		return result;

	}
}
