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
package qrcodegen;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Mode;
import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import qrcodegen.QRCodeGenerator.Modus;
import qrcodegen.modules.EMailPanel;
import qrcodegen.modules.GeoURIPanel;
import qrcodegen.modules.TextPanel;
import qrcodegen.modules.URLPanel;
import qrcodegen.modules.VCardPanel;
import qrcodegen.modules.WLANPanel;
import qrcodegen.modules.vcardgenpanel.controller.VCardController;
import qrcodegen.qrcode.ModeLocalizer;
import qrcodegen.swing.CutCopyPastePopupMenu;
import qrcodegen.swing.ExtendedJFileChooser;
import qrcodegen.swing.FileExtensionFilter;
import qrcodegen.swing.LocationSetter;
import qrcodegen.swing.PrintingTask;
import qrcodegen.swing.Saveable;
import qrcodegen.tools.DelayedAction;
import qrcodegen.tools.ImageFileWriter;
import qrcodegen.tools.ImmutableDimension;
import qrcodegen.tools.Shortener;
import qrcodegen.tools.StaticTools;
import qrcodegen.tools.SwingTools;
import qrcodegen.tools.TextShortener;
import qrcodegen.tools.TriState;

/**
 * This is the main class of QRCodeGen.
 *
 * It registers modules that provide the data QR Codes are generated from, and
 * generates the code if the data is complete or has changed. It provides the
 * capability to print the codes, and to export them via drag&drop or to the
 * system clipboard.
 *
 * @author Stefan Ganzer
 */
public class QRView extends javax.swing.JFrame implements ChangeListener {

	/** The resourse bundle for this class QRView */
	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/QRView");
	private static final String VERSION = "1.14.2";
	private static final String UNDEFINED = RES.getString("UNDEFINED");
	/** The code window is allowed to obscure (lay in front of) at most
	 * 1/MAX_OBSCURED_FRACTION of the main window. */
	private static final int MAX_OBSCURED_FRACTION = 3;
	/** Default size of generated QR Codes */
	private static final int DEFAULT_SIZE = 350;
	/** Minumum allowed size for QR Codes. Version 1 : 21 + 8 (for quiet zone) =
	 * {@value}. */
	private static final int MINIMUM_SIZE = 29;
	/** Maximum allowed size for QR Codes. Arbitrary value for performance
	 * reasons. */
	private static final int MAXIMUM_SIZE = 800;
	/** Minimum allowed module size / pixel. */
	private static final int MIN_MODULE_SIZE = 1;
	/** The maximum module size */
	private static final int MAX_MODULE_SIZE = MAXIMUM_SIZE / MINIMUM_SIZE;
	/** Default module size / pixel. */
	private static final int DEFAULT_MODULE_SIZE = 1;
	private static final int MODULE_SIZE_STEP = 1;
	/** Path to the images this application uses as window icons */
	private static final String[] ICONS = {"images/qrcode_48.png", "images/qrcode_32.png", "images/qrcode_24.png", "images/qrcode_16.png"}; //NOI18N
	/** Images used as window icon, as icon in the taskbar, task switcher and so
	 * on */
	private static final List<Image> ICON_LIST = StaticTools.getImagesAsList(ICONS, QRView.class);
	/** Error correction level, from lowest to hightes level of protection
	 * according to the QR Code standard */
	private static final ImageIcon EMPTY_ICON_16 = new ImageIcon(new BufferedImage(16, 16, IndexColorModel.TRANSLUCENT));
	private static final ImageIcon HINT_ICON = new ImageIcon(StaticTools.loadImage(QRView.class, "images/TipOfTheDay16.gif"));
	private static final ImageIcon STOP_ICON = new ImageIcon(StaticTools.loadImage(QRView.class, "images/Stop16.gif"));
	private static final int[] ERROR_CORRECTION = {1, 0, 3, 2};
	/** Corresponds to S, M and L in Google's online generator at
	 * http://zxing.appspot.com/generator/ */
	private static final int[] BARCODE_SIZE = {120, 230, 350};
	/** The character encodings available to choose from by default. */
	private static final Set<Charset> AVAILABLE_ENCODINGS;
	/** The default encoding scheme. Produces the smallest QR Codes. */
	private static final Charset DEFAULT_ENCODING = Charset.forName("ISO-8859-1"); //NOI18N
	/** The default modus. */
	private static final QRCodeGenerator.Modus DEFAULT_MODUS = QRCodeGenerator.Modus.BEST_FIT;
	/** True if there are printers available on the system this instance is
	 * running, false otherwise. */
	private static final boolean SYSTEM_HAS_PRINTERS = PrinterJob.lookupPrintServices().length > 0;
	/** Limit the length of text in resultField, as very long strings ruin the
	 * layout. */
	private static final int MAX_RESULT_LENGTH = 4000;
	private static final Logger LOGGER = Logger.getLogger(QRView.class.getName());
	private static final String EMPTY_STRING = "";
	private static final String SIZE_WARINING_TOOLTIP_TEXT = RES.getString("QRView.sizeWarningLabel.toolTipText");
	private static final String ENCODING_WARNING_TOOLTIP_TEXT = RES.getString("QRView.encodingWarningLabel.toolTipText");
	private static final Shortener<String> SHORTENER = new TextShortener(80);
	/**
	 * These {@link ContentModule}s provide the data from which the QR-Code is
	 * generated, and a UI to create the data
	 */
	private final ContentModule[] contentGenerators = {new WLANPanel(this), new TextPanel(), new URLPanel(), new EMailPanel(), new VCardPanel(), VCardController.newInstance(), new GeoURIPanel()};
	/** Creates the actual QR Codes from the content provided by the
	 * {@link #contentGenerators} */
	private final QRCodeGenerator generator;
	/** A number formatter for the code size. */
	private final NumberFormat sizeFormat = NumberFormat.getIntegerInstance();
	/** A file chooser instance used for saving code images. */
	private final JFileChooser fileChooser = new JFileChooser();
	/** A separate windowd that displays the QR Code. */
	private final CodeView codeView = new CodeView(this, false);
	/** Listens to changes of the selected character encoding. */
	private final ActionListener charEncodingListener = new EncodingListener();
	private final ChangeListener moduleSizeListener = new ModuleSizeListener();
	private final ActionListener sizeListener = new SizeListener();
	private final ModeLocalizer modeLocalizer = new ModeLocalizer(Locale.getDefault());
	private final Saver saver;
	private final DelayedAction delayedAction = new DelayedAction(Executors.newScheduledThreadPool(1), 10, 100, TimeUnit.MILLISECONDS);
	private final Callable<Void> action;
	/** The page format used when printing */
	private PageFormat pageFormat;
	/** The currently selected barcode size */
	private int size = DEFAULT_SIZE;
	private ContentModule currentModule;
	private Charset oldCharset = DEFAULT_ENCODING;
	private volatile long delay = 0;

	private enum MessageType {

		ERROR(JOptionPane.ERROR_MESSAGE),
		INFORMATION(JOptionPane.INFORMATION_MESSAGE),
		WARNING(JOptionPane.WARNING_MESSAGE),
		QUESTION(JOptionPane.QUESTION_MESSAGE),
		PLAIN(JOptionPane.PLAIN_MESSAGE);
		private final int type;

		private MessageType(int type) {
			this.type = type;
		}

		int type() {
			return type;
		}
	}

	static {
		/*
		 * ISO-8859-1 and UTF-8 are guaranteed to be available on every JVM, but
		 * not Shift_JIS, so we have to be careful here
		 */
		AVAILABLE_ENCODINGS = new LinkedHashSet<Charset>(3);
		AVAILABLE_ENCODINGS.add(DEFAULT_ENCODING);
		try {
			AVAILABLE_ENCODINGS.add(Charset.forName("Shift_JIS"));
		} catch (UnsupportedCharsetException e) {
			// This JVM doesn't support Shift_JIS - that's ok, and
			// nothing we can do about it.
			LOGGER.log(Level.INFO, "No Shift-JIS", e);
		}
		AVAILABLE_ENCODINGS.add(Charset.forName("UTF-8")); //NOI18N
	}

	static QRView newInstance() {
		QRView qrView = new QRView();
		// Register everything that leaks 'this'
		qrView.enableFocusTransferFromCodeViewBackToQRView();
		return qrView;
	}

	/**
	 * Creates new form QRView
	 */
	QRView() {
		// Invoke the code generated by NetBeans' Matisse
		initComponents();
		initWindow();
		initCodeOptionsPanel();
		initCodeDetailsPanel();
		initFileField();
		initResultField();
		initContentPane();
		initFileMenu();
		initEditMenu();
		initWindowMenu();
		initHelpMenu();
		final PropertyChangeListener listener = new GeneratorListener();
		generator = initGenerator(listener);
		initTransferHandler();
		initPopupMenus();
		initCopyMenuListeners();
		initModusComboBox();
		initModuleSizeSpinner();
		initWarningLabels();
		saver = new Saver(getExtendedFileChooser(), new ConcretSaveable(), RES);
		saver.addPropertyChangeListener(listener);
		action = new Callable<Void>() {
			@Override
			public Void call() throws InterruptedException, InvocationTargetException {
				if (SwingUtilities.isEventDispatchThread()) {
					generator.setContent(currentModule.getContent());

				} else {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							generator.setContent(currentModule.getContent());

						}
					});
				}
				return null;
			}
		};
		pack();
	}

	private void enableFocusTransferFromCodeViewBackToQRView() {
		Action action = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// The use of this method is discouraged, as it is not
				// guaranteed to succeed. But we try it, anyway.
				QRView.this.requestFocus();
			}
		};
		codeView.registerActionOnTabbing(action);
	}

	public void displayCodeView() {
		assert DEFAULT_SIZE <= MAXIMUM_SIZE;
		codeView.setPictureSize(new ImmutableDimension(DEFAULT_SIZE, DEFAULT_SIZE));
		//codeView.setLocationNextTo(this, MAX_OBSCURED_FRACTION);
		new LocationSetter(Toolkit.getDefaultToolkit(), codeView).setLocationNextTo(this, MAX_OBSCURED_FRACTION);
		codeView.setVisible(true);
	}

	private void initTransferHandler() {
		codeView.setPictureTransferHandler(new ImageCopyHandler());
	}

	private void initCopyMenuListeners() {
		copyQRCodeMenuItem.addActionListener(codeView.getCopyToClipboardActionListener());
		codeView.addCopyQRCodeActionListener(codeView.getCopyToClipboardActionListener());

		ActionListener copyDataListener = new CopyListener(resultField);
		copyDataMenuItem.addActionListener(copyDataListener);
	}

	private void initEditMenu() {
		SwingTools.getSingleton().setMnemonic(editMenu, getKeyCodeFromResourceBundle("MENU_MNEMONIC_EDIT"));
		SwingTools.getSingleton().setMnemonic(copyQRCodeMenuItem, getKeyCodeFromResourceBundle("EDIT_MENU_MNEMONIC_COPY_QRCODE_ITEM"));
		SwingTools.getSingleton().setMnemonic(copyDataMenuItem, getKeyCodeFromResourceBundle("EDIT_MENU_MNEMONIC_COPY_DATA_ITEM"));
//		copyQRCodeMenuItem.setMnemonic(getKeyCodeFromResourceBundle("EDIT_MENU_MNEMONIC_COPY_QRCODE_ITEM"));
//		copyDataMenuItem.setMnemonic(getKeyCodeFromResourceBundle("EDIT_MENU_MNEMONIC_COPY_DATA_ITEM"));

		copyQRCodeMenuItem.setAccelerator(getKeysStrokeFromResourceBundle("EDIT_MENU_ACCELERATOR_COPY_QRCODE_ITEM"));
	}

	private void initHelpMenu() {
		SwingTools.getSingleton().setMnemonic(helpMenu, getKeyCodeFromResourceBundle("HELP_MENU_MNEMONIC"));
		SwingTools.getSingleton().setMnemonic(aboutMenuItem, getKeyCodeFromResourceBundle("HELP_MENU_MNEMONIC_ABOUT_ITEM")); //NOI18N
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JDialog d = new AboutDialog(QRView.this, true, VERSION);
				d.setVisible(true);
			}
		});
	}

	private void initWindowMenu() {
		SwingTools.getSingleton().setMnemonic(windowMenu, getKeyCodeFromResourceBundle("MENU_MNEMONIC_WINDOW"));
		SwingTools.getSingleton().setMnemonic(openQRCodeWindowMenuItem, getKeyCodeFromResourceBundle("WINDOW_MENU_MNEMONIC_OPEN_ITEM"));
		openQRCodeWindowMenuItem.setAccelerator(getKeysStrokeFromResourceBundle("WINODW_MENU_ACCELERATOR_OPEN_ITEM"));

		openQRCodeWindowMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!codeView.isVisible()) {
					codeView.setVisible(true);
				}
			}
		});
	}

	private void initFileMenu() {
		SwingTools.getSingleton().setMnemonic(fileMenu, getKeyCodeFromResourceBundle("FILE_MENU_MNEMONIC"));
		SwingTools.getSingleton().setMnemonic(saveMenuItem, getKeyCodeFromResourceBundle("FILE_MENU_MNEMONIC_SAVE_ITEM"));
		SwingTools.getSingleton().setMnemonic(saveAsMenuItem, getKeyCodeFromResourceBundle("FILE_MENU_MNEMONIC_SAVE_AS_ITEM"));
		SwingTools.getSingleton().setMnemonic(pageSetupMenuItem, getKeyCodeFromResourceBundle("FILE_MENU_MNEMONIC_PAGE_SETUP_ITEM"));
		SwingTools.getSingleton().setMnemonic(printMenuItem, getKeyCodeFromResourceBundle("FILE_MENU_MNEMONIC_PRINT_ITEM"));
		SwingTools.getSingleton().setMnemonic(exitMenuItem, getKeyCodeFromResourceBundle("FILE_MENU_MNEMONIC_EXIT_ITEM"));

		saveMenuItem.setAccelerator(getKeysStrokeFromResourceBundle("FILE_MENU_ACCELERATOR_SAVE_ITEM"));
		saveAsMenuItem.setAccelerator(getKeysStrokeFromResourceBundle("FILE_MENU_ACCELERATOR_SAVE_AS_ITEM"));
		printMenuItem.setAccelerator(getKeysStrokeFromResourceBundle("FILE_MENU_ACCELERATOR_PRINT_ITEM"));

		saveMenuItem.addActionListener(new SaveListener());
		saveAsMenuItem.addActionListener(new SaveAsListener());
		printMenuItem.addActionListener(new PrintListener());
		pageSetupMenuItem.setAccelerator(getKeysStrokeFromResourceBundle("FILE_MENU_ACCELERATOR_PAGE_SETUP_ITEM"));
		pageSetupMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				PrinterJob pj = PrinterJob.getPrinterJob();
				pageFormat = pj.pageDialog(getPageFormat());
			}
		});
		pageSetupMenuItem.setEnabled(SYSTEM_HAS_PRINTERS);
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				QRView.this.dispatchEvent(new WindowEvent(QRView.this, WindowEvent.WINDOW_CLOSING));
			}
		});

	}

	private void initFileField() {
		fileLabel.setLabelFor(fileField);
	}

	private ExtendedJFileChooser getExtendedFileChooser() {
		ExtendedJFileChooser chooser = new ExtendedJFileChooser();
		chooser.setDialogTitle(RES.getString("SAVE QRCODE AS IMAGE"));
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setMultiSelectionEnabled(false);

		FileFilter defaultFilter = new FileExtensionFilter(RES.getString("PNG FILTER"), "png");

		chooser.addChoosableFileFilter(new FileExtensionFilter(RES.getString("BMP FILTER"), "bmp"));
		chooser.addChoosableFileFilter(new FileExtensionFilter(RES.getString("GIF FILTER"), "gif"));
		chooser.addChoosableFileFilter(defaultFilter);

		chooser.setFileFilter(defaultFilter);

		return chooser;
	}

	private void initCodeDetailsPanel() {
		charactersLabel.setLabelFor(charactersField);
		versionLabel.setLabelFor(versionField);
		modeLabel.setLabelFor(modeField);
		totalBytesLabel.setLabelFor(totalBytesField);
		dataBytesLabel.setLabelFor(dataBytesField);
		ecBytesLabel.setLabelFor(ecBytesField);
		rsBlocksLabel.setLabelFor(rsBlocksField);
	}

	private void initCodeOptionsPanel() {
		initBarcodeSizeComboBox();
		initErrorCorrectionComboBox();
		initCharacterEncodingComboBox();
	}

	private void initBarcodeSizeComboBox() {
		sizeLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("SIZE_LABEL_MNEMONIC")); //NOI18N
		for (int i : BARCODE_SIZE) {
			assert i >= MINIMUM_SIZE && i <= MAXIMUM_SIZE : i;
			barcodeSize.addItem(Integer.valueOf(i));
		}
		barcodeSize.setEditable(true);
		sizeLabel.setLabelFor(barcodeSize);

		JTextField ftf = new JTextField();
		ftf.setColumns(Integer.toString(MAXIMUM_SIZE).length());

		CodeSizeVerifier csv = new CodeSizeVerifier();
		ftf.setInputVerifier(csv);
		ftf.addActionListener(csv);

		barcodeSize.setEditor(new CustomComboBoxEditor(ftf));
		assert DEFAULT_SIZE <= MAXIMUM_SIZE;
		barcodeSize.setSelectedItem(Integer.valueOf(DEFAULT_SIZE));
		barcodeSize.addActionListener(sizeListener);
	}

	private void initWarningLabels() {
		encodingWarningLabel.setText(EMPTY_STRING);
		sizeWarningLabel.setText(EMPTY_STRING);
		encodingWarningLabel.setIcon(EMPTY_ICON_16);
		sizeWarningLabel.setIcon(EMPTY_ICON_16);
		asciiHintLabel.setText(EMPTY_STRING);
		asciiHintLabel.setIcon(EMPTY_ICON_16);
	}

	private void initCharacterEncodingComboBox() {
		charEncodingLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("CHAR_ENCODING_MNEMONIC")); //NOI18N
		charEncodingLabel.setLabelFor(charEncoding);
		charEncoding.setPrototypeDisplayValue(DEFAULT_ENCODING);
		for (Charset c : AVAILABLE_ENCODINGS) {
			charEncoding.addItem(c);
		}
		charEncoding.setSelectedItem(DEFAULT_ENCODING);
		charEncoding.addActionListener(charEncodingListener);
	}

	private void initContentPane() {
		int tabIndex = 0;
		CutCopyPastePopupMenu ccp = new CutCopyPastePopupMenu(Toolkit.getDefaultToolkit(), Toolkit.getDefaultToolkit().getSystemClipboard(), LOGGER, Locale.getDefault(), StaticTools.getSingleton());
		for (ContentModule cm : contentGenerators) {
			Component c = cm.getComponent();
			// 2013-03-09
			// Introduced due to printing fault in VCard v4 module, as
			// VCardView didn't implement ContentModule.
			// TODO I maybe need to rework this.
			assert c instanceof ContentModule : c;
			tabbedPane.addTab(c.getName(), c);
			tabbedPane.setMnemonicAt(tabIndex, cm.getMnemonic());
			tabIndex = tabIndex + 1;
			if (c instanceof Container) {
				ccp.installContextMenuOnAllJTextComponents((Container) c);
			}
		}
		ContentModule currentContentModule = getModuleForSelectedTab();
		currentContentModule.addChangeListener(this);
		tabbedPane.addChangeListener(new TabSelectionListener());
	}

	private void initErrorCorrectionComboBox() {
		correctionLabel.setDisplayedMnemonic(getKeyCodeFromResourceBundle("CORRECTION_LABEL_MNEMONIC")); //NOI18N
		for (int i : ERROR_CORRECTION) {
			errorCorrectionLevel.addItem(ErrorCorrectionLevel.forBits(i));
		}
		correctionLabel.setLabelFor(errorCorrectionLevel);
		errorCorrectionLevel.addActionListener(new ErrorCorrectionListener());
	}

	private void initModusComboBox() {
		for (QRCodeGenerator.Modus m : QRCodeGenerator.Modus.values()) {
			modus.addItem(m);
		}
		modus.setSelectedItem(DEFAULT_MODUS);
		modus.addActionListener(new ModusActionListener());
	}

	private void initModuleSizeSpinner() {
		moduleSize.setModel(new SpinnerNumberModel(DEFAULT_MODULE_SIZE, MIN_MODULE_SIZE, MAX_MODULE_SIZE, MODULE_SIZE_STEP));

	}

	private QRCodeGenerator initGenerator(PropertyChangeListener listener) {
		ContentModule module;
		if (tabbedPane.getSelectedIndex() == -1) {
			module = NullContentModule.getInstance();
		} else {
			module = (ContentModule) tabbedPane.getSelectedComponent();
		}
		currentModule = module;

		// Initialize the QRCodeGenerator instance with the current settings
		QRCodeGenerator gen = new QRCodeGenerator(new ImmutableDimension(MAXIMUM_SIZE, MAXIMUM_SIZE));
		Charset cs = (Charset) charEncoding.getSelectedItem();
		if (cs == null) {
			gen.removeCharacterEncoding();
		} else {
			gen.setCharacterEncoding(cs);
		}
		gen.setErrorCorrectionLevel(((ErrorCorrectionLevel) errorCorrectionLevel.getSelectedItem()));
		gen.setRequestedDimension(new ImmutableDimension(size, size));
		gen.setModus(DEFAULT_MODUS);
		gen.addPropertyChangeListener(listener);
		return gen;
	}

	private void initPopupMenus() {
		// TODO use setComponentPopupMenu instead
		PopupListener pl = new PopupListener();
		codeView.addMouseListenerToPictureLabel(pl);
		codeView.addDragQRCodeMouseMotionListener(pl);
	}

	private void initResultField() {
		resultLabel.setLabelFor(resultField);
		resultField.setToolTipText(java.text.MessageFormat.format(RES.getString("THE FIRST {0} CHARACTERS OF THE DATA."), new Object[]{MAX_RESULT_LENGTH}));
	}

	private void initWindow() {
		setTitle(RES.getString("PORTABLE QR-CODE GENERATOR"));
		//setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				delayedAction.shutdown();
			}
		});

		setIconImages(ICON_LIST);
		setLocationByPlatform(true);
	}

	private void setEncodingSubset(Set<Charset> set) {
		assert set != null;
		// can getSelectedItem return null?
		// Yes, at least if an empty list is allowed
		oldCharset = (Charset) charEncoding.getSelectedItem();
		Set<Charset> subset = new LinkedHashSet<Charset>(AVAILABLE_ENCODINGS);
		subset.retainAll(set);
		assert !subset.isEmpty();
		setCharEncoding(subset);
		if (oldCharset != null) {
			charEncoding.setSelectedItem(oldCharset);
		}
		/*
		 * We need to bypass the EncodingListener so oldCharset is not resetLastFile
		 * to null prematurely.
		 */
		generator.setCharacterEncoding((Charset) charEncoding.getSelectedItem());
	}

	private void setCharEncoding(Set<Charset> s) {
		/*
		 * This method does not fire ActionEvents, and so does not update
		 * the character encoding in the generator!
		 */
		assert s != null;
		charEncoding.removeActionListener(charEncodingListener);
		charEncoding.removeAllItems();
		for (Charset c : s) {
			charEncoding.addItem(c);
		}
		charEncoding.addActionListener(charEncodingListener);
	}

	private void resetAvailableCharsets() {
		assert AVAILABLE_ENCODINGS != null;
		Charset currentEncoding = (Charset) charEncoding.getSelectedItem();
		assert currentEncoding != null;
		setCharEncoding(AVAILABLE_ENCODINGS);
		if (oldCharset != null) {
			charEncoding.setSelectedItem(oldCharset);
		} else {
			charEncoding.setSelectedItem(currentEncoding);
		}
	}

	/**
	 * Returns the last used page format, or a default one if
	 * {@link #pageFormat} is null.
	 *
	 * @return
	 */
	private PageFormat getPageFormat() {
		if (pageFormat == null) {
			PrinterJob pj = PrinterJob.getPrinterJob();
			pageFormat = pj.defaultPage();
		}
		return pageFormat;
	}

	/**
	 * Returns an image containing the generated QR-Code, or an empty image if
	 * no QR-Code has been generated.
	 *
	 * @return an image that has the dimensions determined by barcodeSize
	 */
	public BufferedImage getQRCodeAsImage() {
		BufferedImage image;
		if (generator.isValidState()) {
			image = generator.getImage();
		} else {
			BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			image = bufferedImage;
		}
		return image;
	}

	/**
	 * Generates a QR Code from the data the current {@link #generator}
	 * provides, and displays it.
	 */
	private void generateQRCode() {
		boolean enableButtons = false;
		removeErrorMessage();
		if (generator.hasContent()) {
			if (generator.getContentLength() == 0) {
				generator.setContent(null);
				return;
			}
			//<editor-fold defaultstate="collapsed" desc="assertions">
			assert (generator.getCharacterEncoding() == null
					? charEncoding.getSelectedItem() == null
					: generator.getCharacterEncoding().equals(charEncoding.getSelectedItem())) :
					generator.getCharacterEncoding() + " " + charEncoding.getSelectedItem();
			assert (generator.getErrorCorrectionLevel() == null
					? errorCorrectionLevel.getSelectedItem() == null
					: generator.getErrorCorrectionLevel().equals(errorCorrectionLevel.getSelectedItem())) :
					generator.getErrorCorrectionLevel() + " " + errorCorrectionLevel.getSelectedItem();
			assert (generator.getModus().equals(modus.getSelectedItem())) :
					generator.getModus() + " " + modus.getSelectedItem();
			assert (generator.getModus() == Modus.BEST_FIT || generator.getModus() == Modus.FIXED_SIZE
					? generator.getRequestedDimension().getHeight() == size
					: true) :
					generator.getRequestedDimension() + " " + size;
			assert (generator.getModus() == Modus.BEST_FIT || generator.getModus() == Modus.FIXED_SIZE
					? generator.getRequestedDimension().getWidth() == size
					: true) :
					generator.getRequestedDimension() + " " + size;
			assert (generator.getModus() == Modus.MODULE_SIZE
					? generator.getModuleSize() == ((Number) moduleSize.getModel().getValue()).intValue()
					: true) :
					generator.getModuleSize() + " " + moduleSize.getModel().getValue();
			//</editor-fold>
			long startTime = System.nanoTime();
			try {
				generator.generateCode();
				assert (generator.getModuleSize() == ((Number) moduleSize.getModel().getValue()).intValue()) :
						generator.getModuleSize() + " " + moduleSize.getModel().getValue();
				enableButtons = true;
			} catch (WriterException ex) {
				codeView.setPictureText(RES.getString("INPUT DATA TOO LONG"));
				// Opening a dialog in case of an exception sometimes makes
				// this application freeze - maybe there are too few resources.
				// So for now we display a text instead of the QR Code
				// that's maybe even the better solution.
				//JOptionPane.showMessageDialog(QRView.this, "Sorry, a problem occurred while trying to create the QR-Code.", "A problem occured", JOptionPane.ERROR_MESSAGE);
				LOGGER.log(Level.INFO, "WriterException", ex);
			} catch (CodeSizeException cse) {
				if (cse.getMaxDimension() != null && cse.getDimension() != null) {
					String message = MessageFormat.format(RES.getString("QR CODE TOO LARGE"), cse.getDimension().getWidth(), cse.getDimension().getHeight(), cse.getMaxDimension().getWidth(), cse.getMaxDimension().getHeight()); //NOI18N
					codeView.setPictureText(message);
				}
				LOGGER.log(Level.INFO, "CodeSizeException", cse);
			} catch (OutOfMemoryError oome) {
				String message = RES.getString("OOPS - OUT OF MEMORY") + " (" + oome.getLocalizedMessage() + ")"; //NOI18N
				codeView.setPictureText(message);
				LOGGER.log(Level.WARNING, "Out of memory", oome);
			} finally {
				long stopTime = System.nanoTime();
				delay = stopTime - startTime;
			}
		}
		menuItemsSetEnabled(enableButtons);
	}

	private void removeErrorMessage() {
		// If there was a(n) (error) message displayed instead of a code, remove it.
		if (codeView.getPictureText() != null) {
			codeView.setPictureText(null);
		}
	}

	private void menuItemsSetEnabled(boolean state) {
		saveMenuItem.setEnabled(state);
		saveAsMenuItem.setEnabled(state);
		printMenuItem.setEnabled(SYSTEM_HAS_PRINTERS && state);
	}

	//TODO Move code details to a new module?
	//We could pass in the generator - no other information is needed
	private void updateCodeDetails() {

		updateModeField();
		updateVersionField();

		int characters = generator.hasContent() ? generator.getContentLength() : 0;
		int totalBytes = generator.getNumTotalBytes();
		int dataBytes = generator.getNumDataBytes();
		int ecBytes = generator.getNumECBytes();
		int rsBlocks = generator.getNumRSBlocks();

		charactersField.setText(Integer.toString(characters));
		totalBytesField.setText(Integer.toString(totalBytes));
		dataBytesField.setText(Integer.toString(dataBytes));
		ecBytesField.setText(Integer.toString(ecBytes));
		rsBlocksField.setText(Integer.toString(rsBlocks));
	}

	private void updateVersionField() {
		int version = generator.getVersion();
		String text = version == 0 ? UNDEFINED : Integer.toString(version);
		versionField.setText(text);
		LOGGER.log(Level.FINEST, "qr code version = {0}", version);
	}

	private void updateModeField() {
		Mode mode = generator.getMode();
		String text = modeLocalizer.getModeAsLocalizedString(mode);
		modeField.setText(text);
		LOGGER.log(Level.FINEST, "qr code mode = {0}", mode);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		assert e.getSource() instanceof Encodable;
		try {
			//generator.setContent(currentModule.getContent());
			delayedAction.addAction(action, delay);
		} catch (Exception ex) {
			Logger.getLogger(QRView.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Returns a localized KeyCode for the given key.
	 *
	 * @param key
	 *
	 * @return
	 */
	private static int getKeyCodeFromResourceBundle(String key) {
		assert key != null;
		String s = RES.getString(key);
		return StaticTools.getKeyStrokeForString(s).getKeyCode();
	}

	/**
	 * Returns a localized KeyStroke for the given key.
	 *
	 * @param key
	 *
	 * @return
	 */
	private static KeyStroke getKeysStrokeFromResourceBundle(String key) {
		assert key != null;
		String s = RES.getString(key);
		return StaticTools.getKeyStrokeForString(s);
	}

	private class SaveListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			saver.save();
		}
	}

	private class SaveAsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			saver.saveAs();
		}
	}

	private class ConcretSaveable implements Saveable {

		@Override
		public void saveTo(URI uri) {
			File file = new File(uri);
			try {
				new ImageFileWriter(getQRCodeAsImage()).toFile(file);
			} catch (IOException ex) {
				LOGGER.log(Level.FINER, "Error writing QR Code to image file", ex); //NOI18N
				String shortenedFileName = SHORTENER.shorten(file.getAbsolutePath());
				String message = MessageFormat.format(RES.getString("ERROR WRITING THE FILE."), "\n", shortenedFileName);
				String title = RES.getString("ERROR");
				JOptionPane.showMessageDialog(fileChooser, message, title, JOptionPane.ERROR_MESSAGE);
			}

		}

		public boolean getResult() {
			throw new UnsupportedOperationException();
		}

		public Exception getException() {
			throw new UnsupportedOperationException();
		}
	}

	private class PrintListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			assert SYSTEM_HAS_PRINTERS;
			Printable printable = getModuleForSelectedTab().getPrintable(getQRCodeAsImage());
			// Modules are not obliged to provide a Printable
			if (printable == null) {
				// Create a new JLabel that has the size of the image
				// so the code is printed adjacent to the paper insets
				ImageIcon imageIcon = new ImageIcon(generator.getImage());
				//ImageIcon imageIcon = (ImageIcon) pictureLabel.getIcon();
				JLabel imageLabel = new JLabel(imageIcon);
				imageLabel.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());

				printable = new PrintUtilities(imageLabel);
			}
			PrinterJob printerJob = PrinterJob.getPrinterJob();
			printerJob.setPrintable(printable, getPageFormat());
			printerJob.setJobName("QRCodeGen - " + getModuleForSelectedTab().getJobName()); //NOI18N
			if (printerJob.printDialog()) {
				PrintingTask pt = new PrintingTask(printerJob, QRView.this, RES);
				pt.execute();
			}
		}
	}

	/**
	 * Returns the ContentModule for the currently selected tab, or a dummy
	 * NullContentModule if there is no tab.
	 *
	 * @return
	 */
	private ContentModule getModuleForSelectedTab() {
		if (tabbedPane.getSelectedIndex() == -1) {
			return NullContentModule.getInstance();
		} else {
			return (ContentModule) tabbedPane.getSelectedComponent();
		}

	}

	private final class TabSelectionListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() instanceof JTabbedPane) {
				ContentModule module;
				boolean wasEncodingRestricted = currentModule.restrictsEncoding();
				JTabbedPane tp = (JTabbedPane) e.getSource();
				if (tp.getSelectedIndex() == -1) {
					module = NullContentModule.getInstance();
				} else {
					assert contentGenerators[tp.getSelectedIndex()].getComponent() == tp.getSelectedComponent() : contentGenerators[tp.getSelectedIndex()] + " : " + tp.getSelectedComponent();
					module = contentGenerators[tp.getSelectedIndex()];
				}
				if (module.restrictsEncoding()) {
					Set<Charset> charSubset = module.getEncodingSubset();
					setEncodingSubset(charSubset);
				} else {
					if (wasEncodingRestricted) {
						resetAvailableCharsets();
					}
				}
				currentModule.removeChangeListener(QRView.this);
				currentModule = module;
				currentModule.addChangeListener(QRView.this);
				//generator.setEncodable(module);
				generator.setContent(currentModule.getContent());
				saver.resetLastFile();
			} else {
				LOGGER.log(Level.WARNING, "TabSelectionListener receives events from {0}", e.getSource());
				throw new AssertionError(e.getSource());
			}
		}
	}

	private final class PopupListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// Under Windows, only few applications accept the DataFlavor.imageFlavor.
			// You can drag the image into Word and OpenOffice / LibreOffice, for instance,
			// but not into IrfanView or Gimp. But the transfer via the system clipboard,
			// initiated by the popup menu, works for them, too.
			// TODO Drag-support to native applications?
			// http://www.javaworld.com/javaworld/jw-08-1999/jw-08-draganddrop.html
			// http://www.rockhoppertech.com/java-drag-and-drop-faq.html
			JComponent comp = (JComponent) e.getSource();
			TransferHandler th = comp.getTransferHandler();
			th.exportAsDrag(comp, e, TransferHandler.COPY);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				codeView.showPopup(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private final class SizeListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// We only want to react to events caused by selecting an item
			// that is in the list, not if a new entry was entered by the user.
			// See also: CodeSizeVerifier
			if (barcodeSize.getSelectedIndex() != -1) {
				Integer i = (Integer) barcodeSize.getSelectedItem();
				size = i.intValue();
				generator.setRequestedDimension(new ImmutableDimension(size, size));
			}
		}
	}

	private final class EncodingListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			assert charEncoding.getSelectedItem() != null;
			oldCharset = null;
			generator.setCharacterEncoding((Charset) charEncoding.getSelectedItem());
		}
	}

	private final class ErrorCorrectionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			assert errorCorrectionLevel.getSelectedItem() != null;
			generator.setErrorCorrectionLevel((ErrorCorrectionLevel) (errorCorrectionLevel.getSelectedItem()));
		}
	}

	private final class ModusActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			assert modus.getSelectedItem() != null;
			generator.setModus((QRCodeGenerator.Modus) (modus.getSelectedItem()));
		}
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
	private class CodeSizeVerifier extends InputVerifier implements ActionListener {

		@Override
		public boolean shouldYieldFocus(JComponent input) {

			final boolean inputOk = makeItPretty(input);

			if (inputOk) {
			} else {
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
			boolean wasValid = true;
			int value = DEFAULT_SIZE;

			JTextField jtf = (JTextField) input;
			try {
				String s = jtf.getText();
				value = sizeFormat.parse(s).intValue();

			} catch (ParseException pe) {
				wasValid = false;
			}

			if (value < MINIMUM_SIZE || value > MAXIMUM_SIZE) {
				wasValid = false;
				if (changeIt) {
					if (value < MINIMUM_SIZE) {
						value = MINIMUM_SIZE;
					} else {
						value = MAXIMUM_SIZE;
					}

				}
			}

			if (changeIt) {
				jtf.setText(sizeFormat.format(value));
				jtf.selectAll();
				barcodeSize.setSelectedItem(value);

				if (barcodeSize.getSelectedIndex() == -1) {
					SwingTools.insertElementInRightOrder(barcodeSize.getModel(), value);
					barcodeSize.setSelectedItem(value);
				}
			}
			return wasValid;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JTextField source = (JTextField) e.getSource();
			shouldYieldFocus(source);
			source.selectAll();
		}
	}

	private class ModuleSizeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			Integer size = (Integer) moduleSize.getModel().getValue();
			generator.setModuleSize(size.intValue());
		}
	}

	private class GeneratorListener implements PropertyChangeListener {
		//<editor-fold defaultstate="collapsed">

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			LOGGER.log(Level.FINEST, propertyName);
			if (propertyName.equals(QRCodeGenerator.ACTUAL_DIMENSION_PROPERTY)) {
				String s;
				if (evt.getNewValue() == null) {
					s = null;
					LOGGER.log(Level.FINEST, "actual dimension = null");
				} else {
					ImmutableDimension newDim = (ImmutableDimension) evt.getNewValue();
					s = Integer.toString(newDim.getWidth());
					LOGGER.log(Level.FINEST, "actual dimension = {0}", newDim.toString());
				}
				if (!actualSizeField.getText().equals(s)) {
					actualSizeField.setText(s);
				}
			} else if (propertyName.equals(QRCodeGenerator.REQUESTED_DIMENSION_PROPERTY)) {
				ImmutableDimension newDim = (ImmutableDimension) evt.getNewValue();
				Integer size = Integer.valueOf(newDim.getWidth());
				LOGGER.log(Level.FINEST, "requested dimension = {0}", newDim.toString());
				if (!barcodeSize.getModel().getSelectedItem().equals(size)) {
					barcodeSize.getModel().setSelectedItem(size);
				}
				codeView.setPictureSize(newDim);
				generateQRCode();
			} else if (propertyName.equals(QRCodeGenerator.MODULE_SIZE_PROPERTY)) {
				Integer size = (Integer) evt.getNewValue();
				if (!size.equals(moduleSize.getModel().getValue())) {
					LOGGER.log(Level.FINEST, "setting module size from {0} to {1}", new String[]{evt.getOldValue().toString(), size.toString()});
					moduleSize.getModel().setValue(size);
				}
				LOGGER.log(Level.FINEST, "module size = {0}", size.toString());
				generateQRCode();
			} else if (propertyName.equals(QRCodeGenerator.MODUS_PROPERTY)) {
				Modus m = (Modus) evt.getNewValue();
				if (!modus.getSelectedItem().equals(m)) {
					modus.setSelectedItem(m);
				}
				switch (m) {
					case BEST_FIT:
					// fall-trough
					case FIXED_SIZE:
						moduleSize.setEnabled(false);
						barcodeSize.setEnabled(true);
						assert generator.getRequestedDimension().getHeight() == generator.getRequestedDimension().getWidth() : generator.getRequestedDimension();
						barcodeSize.setSelectedItem(generator.getRequestedDimension().getHeight());
						moduleSize.removeChangeListener(moduleSizeListener);
						barcodeSize.addActionListener(sizeListener);
						break;
					case MODULE_SIZE:
						moduleSize.setEnabled(true);
						barcodeSize.setEnabled(false);
						moduleSize.addChangeListener(moduleSizeListener);
						barcodeSize.removeActionListener(sizeListener);
						barcodeSize.setSelectedIndex(-1);
						break;
					default:
						throw new AssertionError(m);
				}
				LOGGER.log(Level.FINEST, "modus = {0}", m.toString());
				generateQRCode();
			} else if (propertyName.equals(QRCodeGenerator.ERROR_CORRECTION_LEVEL_PROPERTY)) {
				ErrorCorrectionLevel newECLevel = (ErrorCorrectionLevel) evt.getNewValue();
				if (!newECLevel.equals(errorCorrectionLevel.getSelectedItem())) {
					errorCorrectionLevel.setSelectedItem(newECLevel);
				}
				LOGGER.log(Level.FINEST, "error correction level = {0}", newECLevel.toString());
				generateQRCode();
			} else if (propertyName.equals(QRCodeGenerator.CHARACTER_ENCODING_PROPERTY)) {
				Charset newCharset = evt.getNewValue() == null ? null : (Charset) evt.getNewValue();
				Charset currentCharset = charEncoding.getSelectedItem() == null ? null : (Charset) charEncoding.getSelectedItem();;
				if (currentCharset == null) {
					if (newCharset != null) {
						charEncoding.setSelectedItem(newCharset);
					}
				} else {
					if (!currentCharset.equals(newCharset)) {
						charEncoding.setSelectedItem(newCharset);
					}
				}
				LOGGER.log(Level.FINEST, "old charset = {0}, new charset = {1}", new Charset[]{currentCharset, newCharset});
				generateQRCode();
			} else if (propertyName.equals(QRCodeGenerator.WAS_ENCODABLE_PROPERTY)) {
				TriState state = (TriState) evt.getNewValue();
				switch (state) {
					case FALSE:
						encodingWarningLabel.setIcon(STOP_ICON);
						encodingWarningLabel.setToolTipText(ENCODING_WARNING_TOOLTIP_TEXT);
						break;
					default:
						encodingWarningLabel.setIcon(EMPTY_ICON_16);
						encodingWarningLabel.setToolTipText(null);
				}
			} else if (propertyName.equals(QRCodeGenerator.EXCEEDS_REQUESTED_DIMENSION_PROPERTY)) {
				TriState state = (TriState) evt.getNewValue();
				switch (state) {
					case TRUE:
						sizeWarningLabel.setIcon(HINT_ICON);
						sizeWarningLabel.setToolTipText(SIZE_WARINING_TOOLTIP_TEXT);
						break;
					default:
						sizeWarningLabel.setIcon(EMPTY_ICON_16);
						sizeWarningLabel.setToolTipText(null);
				}
			} else if (propertyName.equals(QRCodeGenerator.WAS_ASCII_ONLY_PROPERTY)) {
				TriState state = (TriState) evt.getNewValue();
				switch (state) {
					case TRUE:
						asciiField.setText("ASCII only");
						asciiHintLabel.setIcon(EMPTY_ICON_16);
						asciiHintLabel.setToolTipText(null);
						break;
					case FALSE:
						asciiField.setText("non-ASCII");
						System.out.println(generator.getCharacterEncoding());
						if (generator.getCharacterEncoding().equals(DEFAULT_ENCODING)) {
							asciiHintLabel.setIcon(HINT_ICON);
							asciiHintLabel.setToolTipText(RES.getString("QRView.asciiHintLabel.toolTipText"));
						} else {
							asciiHintLabel.setIcon(EMPTY_ICON_16);
							asciiHintLabel.setToolTipText(null);
						}
						break;
					case NOT_APPLICABLE:
						asciiField.setText(RES.getString("QRView.asciiField.text"));
						asciiHintLabel.setIcon(EMPTY_ICON_16);
						asciiHintLabel.setToolTipText(null);
						break;
					default:
						throw new AssertionError(state);
				}
			} else if (propertyName.equals(QRCodeGenerator.CONTENT_PROPERTY)) {
				resultField.setText(generator.getContent());
				generateQRCode();
			} else if (propertyName.equals(QRCodeGenerator.RESULT_PROPERTY)) {
				if (generator.isValidState()) {
					codeView.setQRCode(generator.getImage());
				} else {
					codeView.setQRCode(null);
				}
				updateCodeDetails();
			} else if (propertyName.equals(Saver.LAST_FILE_PROPERTY)) {
				File f = (File) evt.getNewValue();
				String fileName;
				int caretPosition;
				if (f == null) {
					fileName = "";
					caretPosition = 0;
				} else {
					fileName = f.getPath();
					caretPosition = fileName.length();
				}
				fileField.setText(fileName);
				fileField.setCaretPosition(caretPosition);
			}
		}
		//</editor-fold>
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        picturePopup = new javax.swing.JPopupMenu();
        copyQRCodePopupMenuItem = new javax.swing.JMenuItem();
        resultLabel = new javax.swing.JLabel();
        javax.swing.JPanel codeOptionsPanel = new javax.swing.JPanel();
        charEncodingLabel = new javax.swing.JLabel();
        sizeLabel = new javax.swing.JLabel();
        correctionLabel = new javax.swing.JLabel();
        moduleSize = new javax.swing.JSpinner();
        modus = new javax.swing.JComboBox();
        modusLabel = new javax.swing.JLabel();
        moduleSizeLabel = new javax.swing.JLabel();
        actualSizeField = new javax.swing.JTextField();
        actualSizeLabel = new javax.swing.JLabel();
        sizeWarningLabel = new javax.swing.JLabel();
        encodingWarningLabel = new javax.swing.JLabel();
        tabbedPane = new javax.swing.JTabbedPane();
        codeDetailsPanel = new javax.swing.JPanel();
        codeDetails1Panel = new javax.swing.JPanel();
        charactersLabel = new javax.swing.JLabel();
        modeField = new javax.swing.JTextField();
        charactersField = new javax.swing.JTextField();
        modeLabel = new javax.swing.JLabel();
        asciiField = new javax.swing.JTextField();
        versionField = new javax.swing.JTextField();
        versionLabel = new javax.swing.JLabel();
        asciiHintLabel = new javax.swing.JLabel();
        codeDetails2Panel = new javax.swing.JPanel();
        totalBytesField = new javax.swing.JTextField();
        dataBytesField = new javax.swing.JTextField();
        rsBlocksLabel = new javax.swing.JLabel();
        ecBytesLabel = new javax.swing.JLabel();
        rsBlocksField = new javax.swing.JTextField();
        ecBytesField = new javax.swing.JTextField();
        dataBytesLabel = new javax.swing.JLabel();
        totalBytesLabel = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        fileField = new javax.swing.JTextField();
        fileLabel = new javax.swing.JLabel();
        resultField = new qrcodegen.tools.JCustomTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        pageSetupMenuItem = new javax.swing.JMenuItem();
        printMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        copyQRCodeMenuItem = new javax.swing.JMenuItem();
        copyDataMenuItem = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();
        openQRCodeWindowMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        copyQRCodePopupMenuItem.setText(RES.getString("QRView.copyQRCodePopupMenuItem.text")); // NOI18N
        picturePopup.add(copyQRCodePopupMenuItem);

        resultLabel.setText(RES.getString("QRView.resultLabel.text")); // NOI18N

        codeOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), RES.getString("QRView.codeOptionsPanel.border.title"))); // NOI18N

        charEncoding.setModel(new javax.swing.DefaultComboBoxModel());

        barcodeSize.setModel(new javax.swing.DefaultComboBoxModel());

        charEncodingLabel.setText(RES.getString("QRView.charEncodingLabel.text")); // NOI18N

        errorCorrectionLevel.setModel(new javax.swing.DefaultComboBoxModel());

        sizeLabel.setText(RES.getString("QRView.sizeLabel.text")); // NOI18N

        correctionLabel.setText(RES.getString("QRView.correctionLabel.text")); // NOI18N

        moduleSize.setEnabled(false);

        modusLabel.setText(RES.getString("QRView.modusLabel.text")); // NOI18N

        moduleSizeLabel.setText(RES.getString("QRView.moduleSizeLabel.text")); // NOI18N

        actualSizeField.setEditable(false);
        actualSizeField.setColumns(4);
        actualSizeField.setText(RES.getString("QRView.actualSizeField.text")); // NOI18N

        actualSizeLabel.setText(RES.getString("QRView.actualSizeLabel.text")); // NOI18N

        sizeWarningLabel.setText(RES.getString("QRView.sizeWarningLabel.text")); // NOI18N
        sizeWarningLabel.setToolTipText(RES.getString("QRView.sizeWarningLabel.toolTipText")); // NOI18N

        encodingWarningLabel.setText(RES.getString("QRView.encodingWarningLabel.text")); // NOI18N
        encodingWarningLabel.setToolTipText(RES.getString("QRView.encodingWarningLabel.toolTipText")); // NOI18N

        javax.swing.GroupLayout codeOptionsPanelLayout = new javax.swing.GroupLayout(codeOptionsPanel);
        codeOptionsPanel.setLayout(codeOptionsPanelLayout);
        codeOptionsPanelLayout.setHorizontalGroup(
            codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(codeOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(codeOptionsPanelLayout.createSequentialGroup()
                        .addComponent(charEncodingLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(encodingWarningLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(charEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, codeOptionsPanelLayout.createSequentialGroup()
                        .addComponent(moduleSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(moduleSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, codeOptionsPanelLayout.createSequentialGroup()
                        .addComponent(modusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(modus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, codeOptionsPanelLayout.createSequentialGroup()
                        .addComponent(actualSizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sizeWarningLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(actualSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(codeOptionsPanelLayout.createSequentialGroup()
                        .addComponent(sizeLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(barcodeSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(codeOptionsPanelLayout.createSequentialGroup()
                        .addComponent(correctionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(errorCorrectionLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        codeOptionsPanelLayout.setVerticalGroup(
            codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(codeOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(modusLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizeLabel)
                    .addComponent(barcodeSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(actualSizeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(actualSizeLabel)
                    .addComponent(sizeWarningLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(moduleSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(moduleSizeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(correctionLabel)
                    .addComponent(errorCorrectionLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(charEncodingLabel)
                    .addComponent(charEncoding, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(encodingWarningLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tabbedPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("qrcodegen/QRView"); // NOI18N
        codeDetailsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), bundle.getString("QRView.codeDetailsPanel.border.title"))); // NOI18N
        codeDetailsPanel.setFocusable(false);

        charactersLabel.setText(RES.getString("QRView.charactersLabel.text")); // NOI18N

        modeField.setEditable(false);
        modeField.setColumns(10);
        modeField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        modeField.setText(bundle.getString("QRView.modeField.text")); // NOI18N
        modeField.setFocusable(false);

        charactersField.setEditable(false);
        charactersField.setColumns(4);
        charactersField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        charactersField.setText(RES.getString("QRView.charactersField.text")); // NOI18N
        charactersField.setFocusable(false);

        modeLabel.setText(bundle.getString("QRView.modeLabel.text")); // NOI18N

        asciiField.setEditable(false);
        asciiField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        asciiField.setText(RES.getString("QRView.asciiField.text")); // NOI18N

        versionField.setEditable(false);
        versionField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        versionField.setText(bundle.getString("QRView.versionField.text")); // NOI18N
        versionField.setFocusable(false);

        versionLabel.setText(bundle.getString("QRView.versionLabel.text")); // NOI18N

        asciiHintLabel.setText(RES.getString("QRView.asciiHintLabel.text")); // NOI18N
        asciiHintLabel.setToolTipText(RES.getString("QRView.asciiHintLabel.toolTipText")); // NOI18N

        javax.swing.GroupLayout codeDetails1PanelLayout = new javax.swing.GroupLayout(codeDetails1Panel);
        codeDetails1Panel.setLayout(codeDetails1PanelLayout);
        codeDetails1PanelLayout.setHorizontalGroup(
            codeDetails1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(codeDetails1PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(codeDetails1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(codeDetails1PanelLayout.createSequentialGroup()
                        .addGroup(codeDetails1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(codeDetails1PanelLayout.createSequentialGroup()
                                .addComponent(charactersLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(charactersField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(codeDetails1PanelLayout.createSequentialGroup()
                                .addGroup(codeDetails1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(codeDetails1PanelLayout.createSequentialGroup()
                                        .addComponent(modeLabel)
                                        .addGap(27, 27, 27))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, codeDetails1PanelLayout.createSequentialGroup()
                                        .addComponent(asciiHintLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(codeDetails1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(asciiField)
                                    .addComponent(modeField))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, codeDetails1PanelLayout.createSequentialGroup()
                        .addComponent(versionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(versionField, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        codeDetails1PanelLayout.setVerticalGroup(
            codeDetails1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(codeDetails1PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(codeDetails1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(charactersLabel)
                    .addComponent(charactersField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeDetails1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modeLabel)
                    .addComponent(modeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeDetails1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(asciiField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(asciiHintLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeDetails1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(versionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(versionLabel))
                .addContainerGap())
        );

        totalBytesField.setEditable(false);
        totalBytesField.setColumns(4);
        totalBytesField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        totalBytesField.setText(bundle.getString("QRView.totalBytesField.text")); // NOI18N
        totalBytesField.setFocusable(false);

        dataBytesField.setEditable(false);
        dataBytesField.setColumns(4);
        dataBytesField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        dataBytesField.setText(bundle.getString("QRView.dataBytesField.text")); // NOI18N
        dataBytesField.setFocusable(false);

        rsBlocksLabel.setText(bundle.getString("QRView.rsBlocksLabel.text")); // NOI18N

        ecBytesLabel.setText(bundle.getString("QRView.ecBytesLabel.text")); // NOI18N

        rsBlocksField.setEditable(false);
        rsBlocksField.setColumns(4);
        rsBlocksField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        rsBlocksField.setText(bundle.getString("QRView.rsBlocksField.text")); // NOI18N
        rsBlocksField.setFocusable(false);

        ecBytesField.setEditable(false);
        ecBytesField.setColumns(4);
        ecBytesField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ecBytesField.setText(bundle.getString("QRView.ecBytesField.text")); // NOI18N
        ecBytesField.setToolTipText(bundle.getString("QRView.ecBytesField.toolTipText")); // NOI18N
        ecBytesField.setFocusable(false);

        dataBytesLabel.setText(bundle.getString("QRView.dataBytesLabel.text")); // NOI18N

        totalBytesLabel.setText(bundle.getString("QRView.totalBytesLabel.text")); // NOI18N

        javax.swing.GroupLayout codeDetails2PanelLayout = new javax.swing.GroupLayout(codeDetails2Panel);
        codeDetails2Panel.setLayout(codeDetails2PanelLayout);
        codeDetails2PanelLayout.setHorizontalGroup(
            codeDetails2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(codeDetails2PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(codeDetails2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rsBlocksLabel)
                    .addComponent(totalBytesLabel)
                    .addGroup(codeDetails2PanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(codeDetails2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ecBytesLabel)
                            .addComponent(dataBytesLabel))))
                .addGap(18, 18, 18)
                .addGroup(codeDetails2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rsBlocksField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ecBytesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataBytesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalBytesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        codeDetails2PanelLayout.setVerticalGroup(
            codeDetails2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(codeDetails2PanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(codeDetails2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalBytesLabel)
                    .addComponent(totalBytesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeDetails2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dataBytesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dataBytesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeDetails2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ecBytesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ecBytesLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(codeDetails2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rsBlocksField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rsBlocksLabel))
                .addContainerGap())
        );

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout codeDetailsPanelLayout = new javax.swing.GroupLayout(codeDetailsPanel);
        codeDetailsPanel.setLayout(codeDetailsPanelLayout);
        codeDetailsPanelLayout.setHorizontalGroup(
            codeDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(codeDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(codeDetails1Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codeDetails2Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        codeDetailsPanelLayout.setVerticalGroup(
            codeDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(codeDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(codeDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addGroup(codeDetailsPanelLayout.createSequentialGroup()
                        .addGroup(codeDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(codeDetails2Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(codeDetails1Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        fileField.setEditable(false);
        fileField.setText(RES.getString("QRView.fileField.text")); // NOI18N
        fileField.setFocusable(false);

        fileLabel.setText(RES.getString("QRView.fileLabel.text")); // NOI18N

        resultField.setEditable(false);
        resultField.setCopyMenuItemEnabled(true);
        resultField.setExportFailedDialogMessage(RES.getString("CLIPBOARD")); // NOI18N
        resultField.setExportFailedDialogTitle(RES.getString("THE CLIPBOARD IS CURRENTLY NOT AVAILABLE. PLEASE TRY AGAIN LATER.")); // NOI18N
        resultField.setHandleExportFailedException(true);
        resultField.setMenuText(RES.getString("QRView.resultFieldPopupMenuItem.text")); // NOI18N

        fileMenu.setText(RES.getString("QRView.fileMenu.text")); // NOI18N

        saveMenuItem.setText(RES.getString("QRView.saveMenuItem.text")); // NOI18N
        saveMenuItem.setEnabled(false);
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setText(RES.getString("QRView.saveAsMenuItem.text")); // NOI18N
        saveAsMenuItem.setEnabled(false);
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(jSeparator1);

        pageSetupMenuItem.setText(RES.getString("QRView.pageSetupMenuItem.text")); // NOI18N
        fileMenu.add(pageSetupMenuItem);

        printMenuItem.setText(RES.getString("QRView.printMenuItem.text")); // NOI18N
        printMenuItem.setEnabled(false);
        fileMenu.add(printMenuItem);
        fileMenu.add(jSeparator2);

        exitMenuItem.setText(RES.getString("QRView.exitMenuItem.text")); // NOI18N
        fileMenu.add(exitMenuItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText(RES.getString("QRView.editMenu.text")); // NOI18N

        copyQRCodeMenuItem.setText(RES.getString("QRView.copyQRCodeMenuItem.text")); // NOI18N
        editMenu.add(copyQRCodeMenuItem);

        copyDataMenuItem.setText(RES.getString("QRView.copyDataMenuItem.text")); // NOI18N
        editMenu.add(copyDataMenuItem);

        jMenuBar1.add(editMenu);

        windowMenu.setText(RES.getString("QRView.windowMenu.text")); // NOI18N

        openQRCodeWindowMenuItem.setText(RES.getString("QRView.openQRCodeWindowMenuItem.text")); // NOI18N
        windowMenu.add(openQRCodeWindowMenuItem);

        jMenuBar1.add(windowMenu);

        helpMenu.setText(RES.getString("QRView.helpMenu.text")); // NOI18N

        aboutMenuItem.setText(RES.getString("QRView.aboutMenuItem.text")); // NOI18N
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(resultLabel)
                            .addComponent(fileLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fileField)
                            .addComponent(resultField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(codeOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(codeDetailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 15, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(codeOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(codeDetailsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultLabel)
                    .addComponent(resultField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/*
		 * Set the Nimbus look and feel
		 */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
		 */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//				if ("Nimbus".equals(info.getName())) {
//					javax.swing.UIManager.setLookAndFeel(info.getClassName());
//					break;
//				}
//			}

		} catch (ClassNotFoundException ex) {
			LOGGER.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			LOGGER.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			LOGGER.log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			LOGGER.log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/*
		 * Create and display the form
		 */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				QRView qrView = QRView.newInstance();
				qrView.setVisible(true);
				qrView.displayCodeView();
				qrView.requestFocus();
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JTextField actualSizeField;
    private javax.swing.JLabel actualSizeLabel;
    private javax.swing.JTextField asciiField;
    private javax.swing.JLabel asciiHintLabel;
    private final javax.swing.JComboBox barcodeSize = new javax.swing.JComboBox();
    private final javax.swing.JComboBox charEncoding = new javax.swing.JComboBox();
    private javax.swing.JLabel charEncodingLabel;
    private javax.swing.JTextField charactersField;
    private javax.swing.JLabel charactersLabel;
    private javax.swing.JPanel codeDetails1Panel;
    private javax.swing.JPanel codeDetails2Panel;
    private javax.swing.JPanel codeDetailsPanel;
    private javax.swing.JMenuItem copyDataMenuItem;
    private javax.swing.JMenuItem copyQRCodeMenuItem;
    private javax.swing.JMenuItem copyQRCodePopupMenuItem;
    private javax.swing.JLabel correctionLabel;
    private javax.swing.JTextField dataBytesField;
    private javax.swing.JLabel dataBytesLabel;
    private javax.swing.JTextField ecBytesField;
    private javax.swing.JLabel ecBytesLabel;
    private javax.swing.JMenu editMenu;
    private javax.swing.JLabel encodingWarningLabel;
    private final javax.swing.JComboBox errorCorrectionLevel = new javax.swing.JComboBox();
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JTextField fileField;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField modeField;
    private javax.swing.JLabel modeLabel;
    private javax.swing.JSpinner moduleSize;
    private javax.swing.JLabel moduleSizeLabel;
    private javax.swing.JComboBox modus;
    private javax.swing.JLabel modusLabel;
    private javax.swing.JMenuItem openQRCodeWindowMenuItem;
    private javax.swing.JMenuItem pageSetupMenuItem;
    private javax.swing.JPopupMenu picturePopup;
    private javax.swing.JMenuItem printMenuItem;
    private qrcodegen.tools.JCustomTextField resultField;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JTextField rsBlocksField;
    private javax.swing.JLabel rsBlocksLabel;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JLabel sizeWarningLabel;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField totalBytesField;
    private javax.swing.JLabel totalBytesLabel;
    private javax.swing.JTextField versionField;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JMenu windowMenu;
    // End of variables declaration//GEN-END:variables
}
