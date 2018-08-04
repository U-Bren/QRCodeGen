/*
 Copyright 2011, 2012, 2013 Stefan Ganzer

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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Printable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import qrcodegen.ContentModule;
import qrcodegen.Saver;
import qrcodegen.modules.vcard.VCardTools;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.modules.vcardgenpanel.presentationmodel.VCardPresentationModel;
import qrcodegen.swing.ExtendedJFileChooser;
import qrcodegen.swing.FileExtensionFilter;
import qrcodegen.swing.Saveable;
import qrcodegen.tools.StaticTools;
import qrcodegen.tools.SwingTools;

/**
 * This class is designed to work in conjunction with a VCardController.
 *
 * @author Stefan Ganzer
 */
public class VCardView extends javax.swing.JPanel implements ContentModule {

	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/vcardgenpanel/view/VCardView");
	private static final int MNEMONIC = StaticTools.getKeyCodeFromResourceBundle(RES, "MNEMONIC_VCARD"); //NOI18N
	private static final FileNameExtensionFilter FILE_FILTER = new FileNameExtensionFilter(RES.getString("VCARD_FILE"), "vcf", "vcard"); //NOI18N
	private static final Logger LOGGER = Logger.getLogger(VCardView.class.getPackage().getName());
	private static final String NEWLINE = "\n";
	private static final String PANEL_STATUS = "PanelStatus";
	private static final String EMPTY_STRING = "";
	private final ImageIcon failureIcon = SwingTools.getSingleton().createImageIcon(this.getClass(), "/qrcodegen/modules/vcardgenpanel/view/images/Stop16.gif", null); //NOI18N
	private final PropertyChangeListener statusListener = new StatusListener();
	private final VCardPresentationModel presentationModel;
	private final Saver saver;
	private File currentImportDirectory = new File(System.getProperties().getProperty("user.home")); //NOI18N

	/** Creates new form VCardView */
	VCardView(VCardPresentationModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		initComponents();
		initButtons();
		setName(RES.getString("VCARD"));
		this.presentationModel = model;
		this.presentationModel.addPropertyChangeListener(new ModelListener());
		saver = new Saver(getExtendedFileChooser(), new ConcreteSaveable(), RES);
	}

	private ExtendedJFileChooser getExtendedFileChooser() {
		ExtendedJFileChooser fileChooser = new ExtendedJFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle(RES.getString("SELECT_VCARD"));
		// if currentDirectory is null, JFileChooser opens the user's home directory
		fileChooser.setCurrentDirectory(new File(System.getProperties().getProperty("user.home")));
		FileFilter filter = new FileExtensionFilter(RES.getString("VCARD_FILE"), "vcf", "vcard");
		fileChooser.setFileFilter(filter);

		return fileChooser;
	}

	private void initButtons() {
		generateButton.setMnemonic(StaticTools.getKeyCodeFromResourceBundle(RES, "MNEMONIC_BUTTON_GENERATE")); //NOI18N
		clearButton.setMnemonic(StaticTools.getKeyCodeFromResourceBundle(RES, "MNEMONIC_BUTTON_CLEAR")); //NOI18N
		exportButton.setMnemonic(StaticTools.getKeyCodeFromResourceBundle(RES, "MNEMONIC_BUTTON_EXPORT")); //NOI18N
		importButton.setMnemonic(StaticTools.getKeyCodeFromResourceBundle(RES, "MNEMONIC_BUTTON_IMPORT")); //NOI18N

		final ActionListener al = new ButtonListener();
		generateButton.addActionListener(al);
		clearButton.addActionListener(al);
		importButton.addActionListener(al);
		exportButton.addActionListener(al);
	}

	public static VCardView newInstance(VCardPresentationModel model) {
		return new VCardView(model);
	}

	/**
	 * Adds a component and tip represented by a title and/or icon, either of
	 * which can be null.
	 *
	 * @param title
	 * @param icon
	 * @param component
	 * @param tip
	 */
	public void addTab(String title, Icon icon, Component component, String tip) {
		tabbedPane.addTab(title, icon, component, tip);
		component.addPropertyChangeListener(PANEL_STATUS, statusListener); //NOI18N
	}

	public void setTabMnemonic(int tabIndex, int mnemonic) {
		tabbedPane.setMnemonicAt(tabIndex, mnemonic);
	}

	public void setTabIcon(int tabIndex, Icon icon) {
		tabbedPane.setIconAt(tabIndex, icon);
	}

	public int getMnemonic() {
		return MNEMONIC;
	}

	private void displayImportVCardDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setDialogTitle(RES.getString("SELECT_VCARD"));
		// if currentDirectory is null, JFileChooser opens the user's home directory
		fileChooser.setCurrentDirectory(currentImportDirectory);
		fileChooser.setFileFilter(FILE_FILTER);

		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			currentImportDirectory = fileChooser.getCurrentDirectory();
			File f = fileChooser.getSelectedFile();
			if (f != null) {
				presentationModel.importVCard(f);
			}
		}// else do nothing
	}

	// 2013-03-09 Implementation of ContentModule is a quick fix for printing
	// fault of the VCard v4 module. This probably needs to be reworked.
	@Override
	public Component getComponent() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Printable getPrintable(Image qrcode) {
		return null;
	}

	@Override
	public String getJobName() {
		return EMPTY_STRING;
	}

	@Override
	public boolean restrictsEncoding() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Set<Charset> getEncodingSubset() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void addChangeListener(ChangeListener listener) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void removeChangeListener(ChangeListener listener) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getContent() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private class ConcreteSaveable implements Saveable {

		@Override
		public void saveTo(URI uri) {
			presentationModel.exportVCard(new File(uri));
		}
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final Object source = e.getSource();
			if (source == clearButton) {
				presentationModel.clear();
			} else if (source == generateButton) {
				presentationModel.generate();
			} else if (source == importButton) {
				presentationModel.importVCard();
			} else if (source == exportButton) {
				presentationModel.exportVCard();
			}
		}
	}

	private class ModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (VCardPresentationModel.GENERATION_ENABLED_STATE.equals(propertyName)) {
				generateButton.setEnabled(presentationModel.isGenenerationEnabled());
			} else if (VCardPresentationModel.EXPORT_ENABLED_STATE.equals(propertyName)) {
				exportButton.setEnabled(presentationModel.isExportEnabled());
			} else if (VCardPresentationModel.CLEARING_ENABLED_STATE.equals(propertyName)) {
				clearButton.setEnabled(presentationModel.isClearingEnabled());
			} else if (VCardPresentationModel.IMPORT_VCARD.equals(propertyName)) {
				displayImportVCardDialog();
			} else if (VCardPresentationModel.EXPORT_VCARD.equals(propertyName)) {
				saver.saveAs();
			} else if (VCardPresentationModel.IGNORED_CONTENT.equals(propertyName)) {
				JOptionPane.showMessageDialog(VCardView.this, presentationModel.getIgnoredContentMessage(), RES.getString("INFORMATION"), JOptionPane.INFORMATION_MESSAGE);
			} else if (VCardPresentationModel.PARSING_FAILED.equals(propertyName)) {
				JOptionPane.showMessageDialog(VCardView.this, presentationModel.getParsingFailedMessage(), RES.getString("PARSING ABORTED"), JOptionPane.ERROR_MESSAGE);
			} else if (VCardPresentationModel.FILE_NOT_FOUND_EXCEPTION.equals(propertyName)) {
				String title = RES.getString("FILE NOT FOUND");
				String message = composeMessage("COULDN'T FIND THE FILE", evt.getNewValue());
				JOptionPane.showMessageDialog(VCardView.this, message, title, JOptionPane.ERROR_MESSAGE);
			} else if (VCardPresentationModel.READING_ILLEGAL_CHARACTERS_EXCEPTION.equals(propertyName)) {
				String title = RES.getString("IMPORT ABORTED");
				String message = composeMessage("READING ABORTED", evt.getNewValue());
				JOptionPane.showMessageDialog(VCardView.this, message, title, JOptionPane.ERROR_MESSAGE);
			} else if (VCardPresentationModel.READING_IO_EXCEPTION.equals(propertyName)) {
				String title = RES.getString("ERROR");
				String message = composeMessage("ERROR WHILE READING", evt.getNewValue());
				JOptionPane.showMessageDialog(VCardView.this, message, title, JOptionPane.ERROR_MESSAGE);
			} else if (VCardPresentationModel.WRITING_IO_EXCEPTION.equals(propertyName)) {
				String title = RES.getString("ERROR");
				String message = composeMessage("ERROR WHILE WRITING", evt.getNewValue());
				JOptionPane.showMessageDialog(VCardView.this, message, title, JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private static String composeMessage(String key, Object fileName) {
		assert key != null;
		assert fileName != null;
		String shortenedFileName;
		if (fileName instanceof File) {
			shortenedFileName = ((File) fileName).getAbsolutePath();
		} else if (fileName instanceof String) {
			shortenedFileName = (String) fileName;
		} else {
			shortenedFileName = fileName.toString();
		}
		shortenedFileName = VCardTools.shorten(shortenedFileName, 80);
		String message = MessageFormat.format(RES.getString(key), NEWLINE, shortenedFileName);
		return message;
	}

	/**
	 * Listens to PanelStatus events, and sets the icon of the tab accordingly.
	 */
	private class StatusListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() instanceof Component) {
				Component vpp = (Component) evt.getSource();
				int index = tabbedPane.indexOfComponent(vpp);
				if (index == -1) {
					LOGGER.log(Level.FINE, evt.toString());
				} else {
					if (evt.getNewValue() instanceof InputValidity) {
						InputValidity iv = (InputValidity) evt.getNewValue();
						switch (iv) {
							case EMPTY:
							//fall-through
							case UNDEFINED:
							//fall-through
							case VALID:
								tabbedPane.setIconAt(index, null);
								break;
							case INVALID:
								tabbedPane.setIconAt(index, failureIcon);
								break;
							default:
								throw new AssertionError(evt.toString());
						}
					}
				}
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

        tabbedPane = new JTabbedPane();
        generateButton = new JButton();
        clearButton = new JButton();
        infoLabel = new JLabel();
        importButton = new JButton();
        exportButton = new JButton();

        generateButton.setText(RES.getString("VCardView.generateButton.text_1")); // NOI18N
        generateButton.setEnabled(false);

        clearButton.setText(RES.getString("VCardView.clearButton.text_1")); // NOI18N

        infoLabel.setText(RES.getString("VCardView.infoLabel.text_1")); // NOI18N

        importButton.setText(RES.getString("VCardView.importButton.text_1")); // NOI18N

        exportButton.setText(RES.getString("VCardView.exportButton.text_1")); // NOI18N
        exportButton.setEnabled(false);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(tabbedPane, Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(infoLabel)
                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exportButton)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(importButton)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(clearButton)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(generateButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(clearButton)
                    .addComponent(generateButton)
                    .addComponent(infoLabel)
                    .addComponent(importButton)
                    .addComponent(exportButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton clearButton;
    private JButton exportButton;
    private JButton generateButton;
    private JButton importButton;
    private JLabel infoLabel;
    private JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
