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
/*
 * $Revision: 1148 $
 */
package qrcodegen.modules;

import java.awt.Component;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.print.Printable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import qrcodegen.ContentModule;
import qrcodegen.documentfilter.DocumentSizeFilter;
import qrcodegen.tools.FileReader;
import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardPanel extends javax.swing.JPanel implements ContentModule {

	private static final String EMPTY_STRING = ""; //NOI18N
	private static final String NEWLINE = String.format("%n"); //NOI18N
	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/VCardPanel");
	private static final String ERROR_WHILE_LOADING_FILE_ = RES.getString("ERROR WHILE LOADING FILE: ");
	private static final String FILE_NOT_FOUND_ = RES.getString("FILE NOT FOUND: ");
	private static final String FILE_TOO_LARGE_ = RES.getString("FILE TOO LARGE: ");
	private static final String NO_VCARD_LOADED = RES.getString("[NO VCARD LOADED]");
	/**
	 * An arbitrary size limit to prevent choking
	 */
	private static final long MAX_CARD_SIZE = 15000;
	private static final int MAX_TEXT_LENGTH = 15000;
	private static final int MNEMONIC = getKeyCodeFromResourceBundle("VCARD_MNEMONIC"); //NOI18N
	private static final FileNameExtensionFilter FILE_FILTER = new FileNameExtensionFilter(RES.getString("VCARD FILE"), "vcf", "vcard"); //NOI18N
	private final VCardFilter cardFilter = new VCardFilter();
	private final ConcreteListModel<File> list = new ConcreteListModel<File>();
	private List<JCheckBoxMenuItem> filterMenuItems;
	private Charset charset;
	private File currentDirectory = new File(System.getProperties().getProperty("user.home")); //NOI18N
	private transient ChangeEvent changeEvent;

	/**
	 * Creates new form VCardPanel
	 */
	public VCardPanel() {
		initComponents();
		setName(RES.getString("VCARD IMPORT"));
		initFileEncodingComboBox();
		initOpenButton();
		list.addListDataListener(new ListListener());
		this.setTransferHandler(new FileDropHandler());
		initTextArea();
		navigationBar.addChangeListener(new NavigationListener());
		initClearListButton();
		initCardStatusField();
		initCardFilter();
		initFilterMenu();
	}

	private void initFilterMenu() {
		setMnemonicsOnFilterMenuItems();

		filterMenuItems = Arrays.asList(menuItemPhotoFilter, menuItemCardpicture, menuItemDesign, menuItemEmptyLines);
		// We use a MouseAdapter instead of an ActionListener to get the
		// coordinates of the mouse click
		filterButton.addMouseListener(new MouseAdapter() {
			private static final int LEFT_BUTTON = 1;

			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getButton() == LEFT_BUTTON) {
					filterPopup.show(filterButton, evt.getX(), evt.getY());
				}
			}
		});

		ActionListener al = new MenuListener();
		menuItemActivateAll.addActionListener(al);
		menuItemDeactiveAll.addActionListener(al);

		ItemListener il = new MenuItemListener();
		menuItemActivateAllFilters.addItemListener(il);
		for (JCheckBoxMenuItem mi : filterMenuItems) {
			mi.addItemListener(il);
		}
		for (JCheckBoxMenuItem mi : filterMenuItems) {
			mi.setSelected(true);
		}
	}

	private void setMnemonicsOnFilterMenuItems() {
		menuItemActivateAllFilters.setMnemonic(getKeyCodeFromResourceBundle("MNEMONIC_MENU_ACTIVATE_ALL_FILTERS"));
		menuItemActivateAll.setMnemonic(getKeyCodeFromResourceBundle("MNEMONIC_MENU_ACTIVATE_ALL"));
		menuItemDeactiveAll.setMnemonic(getKeyCodeFromResourceBundle("MNEMONIC_MENU_DEACTIVATE_ALL"));

		menuItemPhotoFilter.setMnemonic(getKeyCodeFromResourceBundle("MNEMONIC_MENU_PHOTO_FILTER"));
		menuItemCardpicture.setMnemonic(getKeyCodeFromResourceBundle("MNEMONIC_MENU_CARDPICTURE"));
		menuItemDesign.setMnemonic(getKeyCodeFromResourceBundle("MNEMONIC_MENU_DESIGN"));
		menuItemEmptyLines.setMnemonic(getKeyCodeFromResourceBundle("MNEMONIC_MENU_EMPTY_LINES"));
	}

	private void initTextArea() {
		textAreaSplitPane.setOneTouchExpandable(true);
		textAreaSplitPane.setContinuousLayout(true);
		DocumentFilter df = new DocumentSizeFilter(MAX_TEXT_LENGTH);

		AbstractDocument ad = (AbstractDocument) filteredCardArea.getDocument();
		ad.addDocumentListener(new TextListener());
		ad.setDocumentFilter(df);

		AbstractDocument ad2 = (AbstractDocument) originalCardArea.getDocument();
		ad2.setDocumentFilter(df);
	}

	private void initClearListButton() {
		clearListButton.setMnemonic(getKeyCodeFromResourceBundle("CLEAR_LIST_MNEMONIC")); //NOI18N
		clearListButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				list.clear();
				currentCardStatusField.setText(RES.getString("[NO VCARD LOADED]"));
			}
		});
	}

	private void initCardFilter() {
		FilterListener filterListener = new FilterListener();
		cardFilter.addChangeListener(filterListener);
		cardFilter.addPropertyChangeListener(VCardFilter.FILTER, filterListener);
		//cardFilter.addPropertyChangeListener(VCardFilter.CARD, filterListener);
		cardFilter.addPropertyChangeListener(VCardFilter.STATE, filterListener);
	}

	private void initCardStatusField() {
		currentCardStatusLabel.setLabelFor(currentCardStatusField);
		currentCardStatusField.setText(NO_VCARD_LOADED);
	}

	private void initOpenButton() {
		openButton.setMnemonic(getKeyCodeFromResourceBundle("OPEN_FILES_MNEMONIC")); //NOI18N
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				list.addAll(getFiles());
			}
		});
	}

	private void initFileEncodingComboBox() {
		fileEncodingLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("FILE_ENCODING_MNEMONIC")); //NOI18N
		fileEncodingLabel.setLabelFor(fileEncodingBox);
		for (Map.Entry<String, Charset> entry : Charset.availableCharsets().entrySet()) {
			fileEncodingBox.addItem(entry.getKey());
		}
		charset = Charset.defaultCharset();
		fileEncodingBox.setSelectedItem(charset.name());
		fileEncodingBox.addActionListener(new FileEncodingListener());
	}

	private static int getKeyCodeFromResourceBundle(String key) {
		assert key != null;
		return StaticTools.getKeyCodeForString(RES.getString(key));
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public int getMnemonic() {
		return MNEMONIC;
	}

	@Override
	public String getContent() {
		return filteredCardArea.getText();
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
		return false;
	}

	@Override
	public Set<Charset> getEncodingSubset() {
		return null;
	}

	@Override
	public synchronized void addChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to register listener twice: " + listener); //NOI18N //NOI18N
		}
		listenerList.add(ChangeListener.class, listener);
	}

	@Override
	public synchronized void removeChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (!Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to remove unregistered listener: " + listener); //NOI18N //NOI18N
		}
		listenerList.remove(ChangeListener.class, listener);
	}

	private void fireStateChanged() {
		final Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event:
				if (changeEvent == null) {
					changeEvent = new ChangeEvent(this);
				}
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	private class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == menuItemActivateAll) {
				setSelected(true);
			} else if (e.getSource() == menuItemDeactiveAll) {
				setSelected(false);
			} else {
				throw new AssertionError(e.getSource());
			}
		}

		private void setSelected(boolean state) {
			for (JCheckBoxMenuItem mi : filterMenuItems) {
				mi.setSelected(state);
			}
		}
	}

	private class MenuItemListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getItemSelectable() == menuItemActivateAllFilters) {
				boolean b = e.getStateChange() == ItemEvent.SELECTED;
				cardFilter.setIsActive(b);
				for (JCheckBoxMenuItem mi : filterMenuItems) {
					mi.setEnabled(b);
				}
				menuItemActivateAll.setEnabled(b);
				menuItemDeactiveAll.setEnabled(b);
			} else if (e.getItemSelectable() == menuItemPhotoFilter) {
				updateFilter(e, Filter.PHOTO);
			} else if (e.getItemSelectable() == menuItemCardpicture) {
				updateFilter(e, Filter.X_MS_CARDPICTURE);
			} else if (e.getItemSelectable() == menuItemDesign) {
				updateFilter(e, Filter.X_MS_OL_DESIGN);
			} else if (e.getItemSelectable() == menuItemEmptyLines) {
				updateFilter(e, Filter.EMPTY_LINES);
			} else {
				throw new AssertionError(e.getSource());
			}
		}

		private void updateFilter(ItemEvent e, Filter f) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				cardFilter.addFilter(f);
			} else {
				cardFilter.removeFilter(f);
			}
		}
	}

	private class FileDropHandler extends TransferHandler {

		@Override
		public boolean canImport(TransferSupport support) {
			if (!support.isDrop()) {
				return false;
			}

			if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return false;
			}

			boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;

			if (!copySupported) {
				return false;
			}
			support.setDropAction(COPY);
			return true;
		}

		@Override
		public boolean importData(TransferSupport support) {
			if (!canImport(support)) {
				return false;
			}
			Transferable t = support.getTransferable();

			try {
				// from DataFlavor.javaFileListFlavor:
				//To transfer a list of files to/from Java (and the underlying platform) a DataFlavor
				//of this type/subtype and representation class of java.util.List is used.
				//Each element of the list is required/guaranteed to be of type java.io.File.
				@SuppressWarnings("unchecked")
				List<File> fileList = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
				importFiles(fileList);
				return true;
			} catch (UnsupportedFlavorException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		private void importFiles(List<File> fileList) {
			list.addAll(removeUnwantedFiles(fileList));
		}

		private List<File> removeUnwantedFiles(List<File> fileList) {
			final List<File> result = new ArrayList<File>(fileList.size());
			for (File f : fileList) {
				if (FILE_FILTER.accept(f)) {
					result.add(f);
				}
			}
			return result;
		}
	}

	private class ListListener implements ListDataListener {

		@Override
		public void intervalAdded(ListDataEvent e) {
			doAction(e);
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			doAction(e);
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			doAction(e);
		}

		private void doAction(ListDataEvent e) {
			final int currentPosition = navigationBar.getValue();
			int newPosition = Math.min(list.getSize(), currentPosition);
			final int lowerLimit = Math.min(list.getSize(), 1);
			if (lowerLimit == 0) {//the list is empty
				currentCardStatusField.setText(EMPTY_STRING);
			} else {
				if (newPosition == 0) {//navigitonBar was in initial state
					newPosition = lowerLimit;
				}
			}
			navigationBar.setRange(newPosition, lowerLimit, list.getSize());
		}
	}

	private class NavigationListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() instanceof NavigationBar) {
				readCard();
			}
		}
	}

	private void readCard() {
		try {
			cardFilter.setCard(EMPTY_STRING);
			final int position = navigationBar.getValue();
			if (position == 0) {//the list is empty
				currentCardStatusField.setText(NO_VCARD_LOADED);
				return;
			}
			File f = list.get(position - 1);
			if (f.length() >= MAX_CARD_SIZE) {
				currentCardStatusField.setText(FILE_TOO_LARGE_ + f.getAbsolutePath());
				return;
			}
			try {
				FileReader fr = new FileReader(f,charset);
				fr.readFile();
				cardFilter.setCard(fr.getContent());
//				cardFilter.setCard(readFile(f, charset));
				currentCardStatusField.setText(f.getAbsolutePath());
			} catch (FileNotFoundException fnfe) {
				cardFilter.setCard(EMPTY_STRING);
				currentCardStatusField.setText(FILE_NOT_FOUND_ + f.getAbsolutePath());
			} catch (IOException ioe) {
				cardFilter.setCard(EMPTY_STRING);
				currentCardStatusField.setText(ERROR_WHILE_LOADING_FILE_ + f.getAbsolutePath());
			}
		} finally {
			cardFilter.processCard();
		}

	}

	private class FilterListener implements ChangeListener, PropertyChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			updateFilterResults();

			filteredCardArea.setText(cardFilter.getFilteredResult());
			filteredCardArea.setCaretPosition(0);

			originalCardArea.setText(cardFilter.getCard());
			originalCardArea.setCaretPosition(0);
		}

		private void updateFilterResults() {
			Set<Filter> foundFilter = cardFilter.getFoundFilter();
			String s;
			if (foundFilter.isEmpty()) {
				s = RES.getString("[NO ELEMENTS FILTERED]");
			} else {
				StringBuilder sb = new StringBuilder(100);
				for (Filter f : cardFilter.getFoundFilter()) {
					if (sb.length() > 0) {
						sb.append(";"); //NOI18N
					}
					sb.append(f.toString());
				}
				s = sb.toString();
			}
			filterResults.setText(s);
			filterResults.setCaretPosition(0);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (VCardFilter.FILTER.equals(evt.getPropertyName())) {
				if (cardFilter.isCardSet()) {
					cardFilter.processCard();
				}
			} else if (VCardFilter.STATE.equals(evt.getPropertyName())) {
				if (cardFilter.isCardSet()) {
					cardFilter.processCard();
				}
			} else if (VCardFilter.CARD.equals(evt.getPropertyName())) {
				if (cardFilter.isCardSet()) {
					cardFilter.processCard();
				}
			}
		}
	}

	public static String readFile(File f, Charset c) throws FileNotFoundException, IOException {
		if (f == null) {
			throw new NullPointerException();
		}
		if (c == null) {
			throw new NullPointerException();
		}
		BufferedReader reader = null;
		final int initialSize = f.length() >= Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) f.length();
		StringBuilder sb = new StringBuilder(initialSize);
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), c));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append(NEWLINE);
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ioe) {
					//swallowed
				}
			}
		}
		return sb.toString();
	}

	private final class TextListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			fireStateChanged();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			fireStateChanged();
		}
	}

	private final class FileEncodingListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String charsetName = (String) fileEncodingBox.getSelectedItem();
			charset = Charset.forName(charsetName);
			// we need to re-read the current card
			// so the changed input encoding is reflected
			// But: setValue only fires a changedPropertyEvent
			// only if the value != oldValue
			// How do we solve this?
			readCard();
		}
	}

	private List<File> getFiles() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(RES.getString("SELECT VCARDS"));
		// if currentDirectory is null, JFileChooser opens the user's home directory
		chooser.setCurrentDirectory(currentDirectory);
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileFilter(FILE_FILTER);

		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) {
			return Collections.emptyList();
		}

		currentDirectory = chooser.getCurrentDirectory();
		File[] files = chooser.getSelectedFiles();
		return new ArrayList<File>(Arrays.asList(files));
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filterPopup = new javax.swing.JPopupMenu();
        menuItemActivateAllFilters = new javax.swing.JCheckBoxMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        menuItemActivateAll = new javax.swing.JMenuItem();
        menuItemDeactiveAll = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemPhotoFilter = new javax.swing.JCheckBoxMenuItem();
        menuItemCardpicture = new javax.swing.JCheckBoxMenuItem();
        menuItemDesign = new javax.swing.JCheckBoxMenuItem();
        menuItemEmptyLines = new javax.swing.JCheckBoxMenuItem();
        jPanel1 = new javax.swing.JPanel();
        filterResults = new javax.swing.JTextField();
        filterButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        fileEncodingBox = new javax.swing.JComboBox();
        fileEncodingLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        currentCardStatusField = new javax.swing.JTextField();
        textAreaSplitPane = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        originalCardArea = new javax.swing.JTextArea();
        jScrollPane = new javax.swing.JScrollPane();
        filteredCardArea = new javax.swing.JTextArea();
        originalLabel = new javax.swing.JLabel();
        filteredLabel = new javax.swing.JLabel();

        menuItemActivateAllFilters.setSelected(true);
        menuItemActivateAllFilters.setText(RES.getString("VCardPanel.menuItemActivateAllFilters.text")); // NOI18N
        filterPopup.add(menuItemActivateAllFilters);
        filterPopup.add(jSeparator2);

        menuItemActivateAll.setText(RES.getString("VCardPanel.menuItemActivateAll.text")); // NOI18N
        filterPopup.add(menuItemActivateAll);

        menuItemDeactiveAll.setText(RES.getString("VCardPanel.menuItemDeactiveAll.text")); // NOI18N
        filterPopup.add(menuItemDeactiveAll);
        filterPopup.add(jSeparator1);

        menuItemPhotoFilter.setText(RES.getString("VCardPanel.menuItemPhotoFilter.text")); // NOI18N
        filterPopup.add(menuItemPhotoFilter);

        menuItemCardpicture.setText(RES.getString("VCardPanel.menuItemCardpicture.text")); // NOI18N
        filterPopup.add(menuItemCardpicture);

        menuItemDesign.setText(RES.getString("VCardPanel.menuItemDesign.text")); // NOI18N
        filterPopup.add(menuItemDesign);

        menuItemEmptyLines.setText(RES.getString("VCardPanel.menuItemEmptyLines.text")); // NOI18N
        filterPopup.add(menuItemEmptyLines);

        setToolTipText(RES.getString("VCardPanel.toolTipText")); // NOI18N
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(515, 244));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        filterResults.setEditable(false);
        filterResults.setText(RES.getString("VCardPanel.filterResults.text")); // NOI18N
        filterResults.setToolTipText(RES.getString("VCardPanel.filterResults.toolTipText")); // NOI18N
        filterResults.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        filterResults.setFocusable(false);
        filterResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterResultsActionPerformed(evt);
            }
        });

        filterButton.setText(RES.getString("VCardPanel.filterButton.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(filterButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(filterResults))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filterButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(filterResults, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        fileEncodingBox.setModel(new javax.swing.DefaultComboBoxModel());
        fileEncodingBox.setToolTipText(RES.getString("VCardPanel.fileEncodingBox.toolTipText")); // NOI18N

        fileEncodingLabel.setText(RES.getString("VCardPanel.fileEncodingLabel.text")); // NOI18N
        fileEncodingLabel.setToolTipText(RES.getString("VCardPanel.fileEncodingLabel.toolTipText")); // NOI18N

        openButton.setText(RES.getString("VCardPanel.openButton.text")); // NOI18N
        openButton.setToolTipText(RES.getString("VCardPanel.openButton.toolTipText")); // NOI18N

        clearListButton.setText(RES.getString("VCardPanel.clearListButton.text")); // NOI18N
        clearListButton.setToolTipText(RES.getString("VCardPanel.clearListButton.toolTipText")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(fileEncodingLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fileEncodingBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(openButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clearListButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileEncodingBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileEncodingLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(openButton)
                    .addComponent(clearListButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        currentCardStatusLabel.setText(RES.getString("VCardPanel.currentCardStatusLabel.text")); // NOI18N

        currentCardStatusField.setEditable(false);
        currentCardStatusField.setText(NO_VCARD_LOADED);
        currentCardStatusField.setFocusable(false);

        navigationBar.setBorder(null);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(currentCardStatusLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentCardStatusField, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(navigationBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(currentCardStatusLabel)
                            .addComponent(currentCardStatusField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(navigationBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        textAreaSplitPane.setResizeWeight(0.5);

        originalCardArea.setColumns(20);
        originalCardArea.setEditable(false);
        originalCardArea.setRows(6);
        originalCardArea.setToolTipText(RES.getString("VCardPanel.originalCardArea.toolTipText")); // NOI18N
        originalCardArea.setFocusable(false);
        jScrollPane1.setViewportView(originalCardArea);

        textAreaSplitPane.setLeftComponent(jScrollPane1);

        filteredCardArea.setColumns(20);
        filteredCardArea.setRows(6);
        filteredCardArea.setToolTipText(RES.getString("VCardPanel.filteredCardArea.toolTipText")); // NOI18N
        filteredCardArea.setDropMode(javax.swing.DropMode.INSERT);
        jScrollPane.setViewportView(filteredCardArea);

        textAreaSplitPane.setRightComponent(jScrollPane);

        originalLabel.setText(RES.getString("VCardPanel.originalLabel.text")); // NOI18N

        filteredLabel.setText(RES.getString("VCardPanel.filteredLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(textAreaSplitPane)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(originalLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(filteredLabel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(originalLabel)
                    .addComponent(filteredLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textAreaSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	private void filterResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterResultsActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_filterResultsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton clearListButton = new javax.swing.JButton();
    private javax.swing.JTextField currentCardStatusField;
    private final javax.swing.JLabel currentCardStatusLabel = new javax.swing.JLabel();
    private javax.swing.JComboBox fileEncodingBox;
    private javax.swing.JLabel fileEncodingLabel;
    private javax.swing.JButton filterButton;
    private javax.swing.JPopupMenu filterPopup;
    private javax.swing.JTextField filterResults;
    private javax.swing.JTextArea filteredCardArea;
    private javax.swing.JLabel filteredLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JMenuItem menuItemActivateAll;
    private javax.swing.JCheckBoxMenuItem menuItemActivateAllFilters;
    private javax.swing.JCheckBoxMenuItem menuItemCardpicture;
    private javax.swing.JMenuItem menuItemDeactiveAll;
    private javax.swing.JCheckBoxMenuItem menuItemDesign;
    private javax.swing.JCheckBoxMenuItem menuItemEmptyLines;
    private javax.swing.JCheckBoxMenuItem menuItemPhotoFilter;
    private final qrcodegen.modules.NavigationBar navigationBar = new qrcodegen.modules.NavigationBar();
    private final javax.swing.JButton openButton = new javax.swing.JButton();
    private javax.swing.JTextArea originalCardArea;
    private javax.swing.JLabel originalLabel;
    private javax.swing.JSplitPane textAreaSplitPane;
    // End of variables declaration//GEN-END:variables
}
