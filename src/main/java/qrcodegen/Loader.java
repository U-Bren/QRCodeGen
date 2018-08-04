/*
 * Copyright (C) 2012, 2013 Stefan Ganzer
 *
 * This file is part of QRCodeGen.
 *
 * QRCodeGen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QRCodeGen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import qrcodegen.ErrorDialog.MessageType;
import qrcodegen.ErrorDialog.Type;

/**
 * Sets the look&feel and instantiates QRView. Registers an
 * DefaultUncaughtExceptionHandler that displays an error message and terminates
 * the application in case of uncaught exceptions.
 *
 * @author Stefan Ganzer
 */
public class Loader {

	private static final Logger LOG = Logger.getLogger(Loader.class.getName());
	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/Loader");
	private static final String ERROR = RES.getString("ERROR");
	private static final String INFORMATION = RES.getString("INFORMATION");
	private static final String FATAL_ERROR = RES.getString("FATAL ERROR");
	private static final String BASIC_INFORMATION_MESSAGE_LOOK_AND_FEEL = RES.getString("CHANGING LOOK&FEEL FAILED");
	private static final String BASIC_ERROR_MESSAGE_STARTUP = MessageFormat.format(RES.getString("A FATAL ERROR OCCURED WHILE TRYING TO START QRCODEGEN."), "\n");
	private static final String BASIC_ERROR_MESSAGE_RUNNING = MessageFormat.format(RES.getString("A FATAL ERROR OCCURED WHILE RUNNING QRCODEGEN."), "\n");
	private static final String VERSION = "Version: 1.14.2";

	private Loader() {
	}

	public static void main(String... args) {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException cnf) {
			LOG.log(Level.CONFIG, "Changing look&feel", cnf);
//			handleException(INFORMATION, BASIC_INFORMATION_MESSAGE_LOOK_AND_FEEL, VERSION, cnf, Level.CONFIG, MessageType.INFORMATION, false);
		} catch (IllegalAccessException iae) {
			LOG.log(Level.CONFIG, "Changing look&feel", iae);
//			handleException(INFORMATION, BASIC_INFORMATION_MESSAGE_LOOK_AND_FEEL, VERSION, iae, Level.CONFIG, MessageType.INFORMATION, false);
		} catch (InstantiationException ie) {
			LOG.log(Level.CONFIG, "Changing look&feel", ie);
//			handleException(INFORMATION, BASIC_INFORMATION_MESSAGE_LOOK_AND_FEEL, VERSION, ie, Level.CONFIG, MessageType.INFORMATION, false);
		} catch (UnsupportedLookAndFeelException ulfe) {
			LOG.log(Level.CONFIG, "Changing look&feel", ulfe);
//			handleException(INFORMATION, BASIC_INFORMATION_MESSAGE_LOOK_AND_FEEL, VERSION, ulfe, Level.CONFIG, MessageType.INFORMATION, false);
		}
		java.awt.EventQueue.invokeLater(
				new Runnable() {
					@Override
					public void run() {
						try {
							QRView qrView = QRView.newInstance();
							qrView.setVisible(true);
							qrView.displayCodeView();
							qrView.requestFocus();
						} catch (Throwable t) {
							handleException(FATAL_ERROR, BASIC_ERROR_MESSAGE_STARTUP, VERSION, t, Level.SEVERE, MessageType.ERROR, true);
						}
					}
				});
	}

	public synchronized static void handleException(String title, String basicErrorMessage, String detailedErrorMessage, Throwable t, Level level, MessageType messageType, boolean exit) {
		LOG.log(level, null, t);
		ErrorDialog.Type type = exit ? Type.EXIT : Type.CLOSE;
		ErrorDialog ed = new ErrorDialog(null, title, true, type, messageType);
		ed.setErrorMessage(title, basicErrorMessage, detailedErrorMessage, level, t);
		ed.showDialog();
		if (exit) {
			System.exit(1);
		}
	}

	private static class ExceptionHandler implements Thread.UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			handleException(FATAL_ERROR, BASIC_ERROR_MESSAGE_RUNNING, VERSION, e, Level.SEVERE, MessageType.ERROR, true);
		}
	}
}
