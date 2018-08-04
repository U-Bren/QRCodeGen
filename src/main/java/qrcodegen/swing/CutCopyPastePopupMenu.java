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
package qrcodegen.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFormattedTextField;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class CutCopyPastePopupMenu {

	private static final String RES_BUNDLE = "qrcodegen/swing/CutCopyPastePopupMenu";
	private final Locale locale;
	private final Logger logger;
	private final ResourceBundle res;
	private final Toolkit toolkit;
	private final Clipboard clipboard;
	private final StaticTools tools;

	public CutCopyPastePopupMenu(Toolkit toolkit, Clipboard clipboard, Logger logger, Locale locale, StaticTools tools) {
		if (toolkit == null) {
			throw new NullPointerException();
		}
		if (locale == null) {
			throw new NullPointerException();
		}
		if (logger == null) {
			throw new NullPointerException();
		}
		if (tools == null) {
			throw new NullPointerException();
		}
		this.toolkit = toolkit;
		this.locale = locale;
		this.logger = logger;
		this.res = ResourceBundle.getBundle(RES_BUNDLE, this.locale);
		this.clipboard = clipboard;
		this.tools = tools;
	}

	public JPopupMenu getNewPopupMenuFor(JTextComponent c) {
		final JPopupMenu menu = new JPopupMenu();
		Action copyAction = getConfiguredAction(c, DefaultEditorKit.copyAction);
		menu.add(copyAction);

		Action cutAction = getConfiguredAction(c, DefaultEditorKit.cutAction);
		menu.add(cutAction);

		Action pasteAction = getConfiguredAction(c, DefaultEditorKit.pasteAction);
		menu.add(pasteAction);

		//TODO
		//delete selected text without copying it to the clipboard

		Action selectAllAction;
		if (c instanceof JFormattedTextField) {
			final JFormattedTextField tf = (JFormattedTextField) c;
			selectAllAction = new SelectAllAction(tf);
			configureAction(selectAllAction, DefaultEditorKit.selectAllAction);

		} else {
			selectAllAction = getConfiguredAction(c, DefaultEditorKit.selectAllAction);

		}
		menu.addSeparator();
		menu.add(selectAllAction);

		menu.addPopupMenuListener(new PopupListener(clipboard, c, copyAction, cutAction, pasteAction, selectAllAction));

		return menu;
	}

	private static class SelectAllAction extends AbstractAction {

		private static final long serialVersionUID = 1L;
		private final JFormattedTextField tf;

		SelectAllAction(JFormattedTextField tf) {
			super();
			if (tf == null) {
				throw new NullPointerException();
			}
			this.tf = tf;
		}

		SelectAllAction(String name, Icon icon, JFormattedTextField tf) {
			super(name, icon);
			if (tf == null) {
				throw new NullPointerException();
			}
			this.tf = tf;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			tf.requestFocusInWindow();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					tf.selectAll();
				}
			});
		}
	}

	private void configureAction(Action action, String actionName) {
		assert action != null;
		assert actionName != null;

		// Platform dependend modifier
		final int mask = toolkit.getMenuShortcutKeyMask();
		String name = res.getString(actionName);
		String mnemonicResourceKey = "MNEMONIC_".concat(actionName);
		String acceleratorResourceKey = "KEYCODE_".concat(actionName);

		int accelerator = tools.getKeyCodeForString(res.getString(acceleratorResourceKey));
		KeyStroke acceleratorKey = KeyStroke.getKeyStroke(accelerator, mask);

		action.putValue(Action.NAME, name);
		action.putValue(Action.ACCELERATOR_KEY, acceleratorKey);
		if (!tools.osIsMac()) {
			int mnemonicKey = tools.getKeyCodeForString(res.getString(mnemonicResourceKey));
			action.putValue(Action.MNEMONIC_KEY, mnemonicKey);
		}
	}

	private Action getConfiguredAction(JTextComponent c, String actionName) {
		Action action = c.getActionMap().get(actionName);
		configureAction(action, actionName);
		return action;
	}

	public void installContextMenuOnAllJTextComponents(Container comp) {
		for (Component c : comp.getComponents()) {
			if (c instanceof JTextComponent) {
				JTextComponent jtc = (JTextComponent) c;
				if (jtc.getComponentPopupMenu() == null) {
					jtc.setComponentPopupMenu(getNewPopupMenuFor(jtc));
				} else {
					logger.log(Level.FINE, "{0} skipped; inheritsPopupMenu() == {1}", new Object[]{jtc, jtc.getInheritsPopupMenu()});
				}
			} else if (c instanceof Container) {
				installContextMenuOnAllJTextComponents((Container) c);
			}
		}
	}

	private static class PopupListener implements PopupMenuListener {

		private final JTextComponent textComponent;
		private final Action cutAction;
		private final Action copyAction;
		private final Action pasteAction;
		private final Action selectAllAction;
		private final Clipboard clipboard;

		private PopupListener(Clipboard clipboard, JTextComponent component, Action cut, Action copy, Action paste, Action selectAll) {
			this.textComponent = component;
			this.cutAction = cut;
			this.copyAction = copy;
			this.pasteAction = paste;
			this.selectAllAction = selectAll;
			this.clipboard = clipboard;
			assert this.clipboard != null;
			assert this.textComponent != null;
			assert this.cutAction != null;
			assert this.copyAction != null;
			assert this.pasteAction != null;
			assert this.selectAllAction != null;
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			boolean isTextSelected = textComponent.getSelectionStart() != textComponent.getSelectionEnd();
			cutAction.setEnabled(isTextSelected);
			copyAction.setEnabled(textComponent.isEditable() && isTextSelected);
			pasteAction.setEnabled(textComponent.isEditable() && clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor));
			selectAllAction.setEnabled(!textComponent.getText().isEmpty());
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			// do nothing
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			// do nothing
		}
	}
}
