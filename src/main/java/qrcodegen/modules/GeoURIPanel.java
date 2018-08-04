/*
 * Copyright (C) 2013 Stefan Ganzer
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
package qrcodegen.modules;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.print.Printable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipException;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import qrcodegen.ContentModule;
import qrcodegen.ReaderSaver;
import qrcodegen.kml.Coordinates;
import qrcodegen.kml.KMLFileReader;
import qrcodegen.kml.Kml;
import qrcodegen.kml.Placemark;
import qrcodegen.swing.ClipboardService;
import qrcodegen.swing.EmptyNumberFormatter;
import qrcodegen.swing.ExtendedJFileChooser;
import qrcodegen.swing.FileDropHandler;
import qrcodegen.swing.FileExtensionFilter;
import qrcodegen.tools.Shortener;
import qrcodegen.tools.TextShortener;
import qrcodegen.tools.TriState;

/**
 *
 * @author Stefan Ganzer
 */
public class GeoURIPanel extends javax.swing.JPanel implements ContentModule, ChangeListener {

	private static final int ONE_MEGABYTE = 1024 * 1024;
	private static final String EMPTY_STRING = "";
	private final ResourceBundle res;
	private final Logger logger;
	private final FileDropHandler dropHandler;
	private final GeoURIModel model;
	private final ClipboardService cbService;
	private final ReaderSaver rsService;
	private final ReaderSaver.Readable readable;
	private transient ChangeEvent changeEvent;

	/** Creates new form GeoURIPanel */
	public GeoURIPanel() {
		this(ResourceBundle.getBundle("qrcodegen/modules/GeoURIPanel"),
				Logger.getLogger(GeoURIPanel.class.getName()));
	}

	public GeoURIPanel(ResourceBundle bundle, Logger logger) {
		if (bundle == null) {
			throw new NullPointerException();
		}
		if (logger == null) {
			throw new NullPointerException();

		}
		this.res = bundle;
		this.logger = logger;
		setName(res.getString("panel.name"));
		initComponents();
		FileExtensionFilter kmzFilter = new FileExtensionFilter(res.getString("KML FILE"), "kml", "kmz");
		dropHandler = new FileDropHandler(kmzFilter, logger); //NOI18N
		dropHandler.addChangeListener(this);
		this.setTransferHandler(dropHandler);

		this.cbService = new ClipboardService(Toolkit.getDefaultToolkit().getSystemClipboard(), Logger.getLogger(GeoURIPanel.class.getName()));
		cbService.addPropertyChangeListener(ClipboardService.AVAILABLE_AS_STRING_FLAVOR_PROPERTY, new ClipboardListener());

		readable = new ConcreteReadable();
		ExtendedJFileChooser chooser = new ExtendedJFileChooser();
		chooser.addChoosableFileFilter(kmzFilter);
		chooser.setFileFilter(kmzFilter);
		rsService = new ReaderSaver(chooser, readable, bundle);

		this.model = new GeoURIModel();
		this.model.addPropertyChangeListener(new ModelListener());

		initTree();
		initZoomComboBox();

		PropertyChangeListener fl = new FieldListener();
		initDecimalLatitude(fl);
		initDecimalLongitude(fl);
		initLatitudeDegree(fl);
		initLongitudeDegree(fl);
		initAltitude(fl);
		initButtons();
		validate();
	}

	private void initZoomComboBox() {
		zoomLabel.setLabelFor(zoomComboBox);
		String[] values = {"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
		DefaultComboBoxModel cbModel = new DefaultComboBoxModel(values);
		zoomComboBox.setModel(cbModel);
		zoomComboBox.addActionListener(new ZoomComboBoxListener());
	}

	private void initTree() {
		kmlTree.setModel(null);
		kmlTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		kmlTree.setRootVisible(false);
		kmlTree.setShowsRootHandles(true);
		kmlTree.addTreeSelectionListener(new KmlTreeSelectionListener());
	}

	private void initButtons() {

		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearPanel();
			}
		});

		importFromClipboardButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearPanel();
				importKMLFromClipboard();
			}
		});

		importFromFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearPanel();
				rsService.readFrom();
			}
		});
	}

	private void clearPanel() {
		clearModel();
		kmlTree.setModel(null);
	}

	private void importKMLFromClipboard() {
		cbService.acquireContentsAsString();
		String contents = cbService.getContentsAsString();
		if (contents != null) {
			try {
				Reader reader = new StringReader(contents);
				Kml kml = JAXB.unmarshal(reader, Kml.class);
				importKML(kml);
			} catch (DataBindingException dbe) {
				logger.throwing("GeoURIPanel.importFromClipboardButton.ActionListener", "actionPerformed", dbe);
				String title = res.getString("DBE TITLE");
				String message = MessageFormat.format(res.getString("DBE MESSAGE"), "\n");
				int type = JOptionPane.ERROR_MESSAGE;
				JOptionPane.showMessageDialog(null, message, title, type);
			}
		}
		cbService.clearClipboard();
	}

	private static NumberFormatter getFormatter() {
		NumberFormat nf = NumberFormat.getNumberInstance();
		if (nf instanceof DecimalFormat) {
			DecimalFormat df = (DecimalFormat) nf;
			df.setDecimalSeparatorAlwaysShown(false);
			df.setMaximumFractionDigits(20);
		}
		NumberFormatter nfr = new NumberFormatter(nf);
		return nfr;
	}

	private static EmptyNumberFormatter getEmptyNumberFormatter() {
		NumberFormat nf = NumberFormat.getNumberInstance();
		if (nf instanceof DecimalFormat) {
			DecimalFormat df = (DecimalFormat) nf;
			df.setDecimalSeparatorAlwaysShown(false);
			df.setMaximumFractionDigits(20);
		}
		EmptyNumberFormatter nfr = new EmptyNumberFormatter(nf);
		return nfr;
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		for (File f : dropHandler.getFiles()) {
			clearPanel();
			readable.readFrom(f.toURI());
		}
	}

	private void initLongitudeDegree(PropertyChangeListener fl) {
		NumberFormatter longDegreeFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		longDegreeFormatter.setMinimum(-180);
		longDegreeFormatter.setMaximum(+180);

		NumberFormatter longMinuteFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		longMinuteFormatter.setMinimum(0);
		longMinuteFormatter.setMaximum(59);

		NumberFormatter longSecondFormatter = getFormatter();
		longSecondFormatter.setMinimum(0.0);
		longSecondFormatter.setMaximum(59.99999999999999999999);

		longitudeDegree.setFormatterFactory(new DefaultFormatterFactory(longDegreeFormatter));
		longitudeMinute.setFormatterFactory(new DefaultFormatterFactory(longMinuteFormatter));
		longitudeSecond.setFormatterFactory(new DefaultFormatterFactory(longSecondFormatter));

		longitudeDegree.addPropertyChangeListener("value", fl);
		longitudeMinute.addPropertyChangeListener("value", fl);
		longitudeSecond.addPropertyChangeListener("value", fl);

		longitudeDegree.getDocument().addDocumentListener(null);
	}

	private void initLatitudeDegree(PropertyChangeListener fl) {
		NumberFormatter latDegreeFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		latDegreeFormatter.setMinimum(-90);
		latDegreeFormatter.setMaximum(+90);

		NumberFormatter latMinuteFormatter = new NumberFormatter(NumberFormat.getIntegerInstance());
		latMinuteFormatter.setMinimum(0);
		latMinuteFormatter.setMaximum(59);

		NumberFormatter latSecondFormatter = getFormatter();
		latSecondFormatter.setMinimum(0.0);
		latSecondFormatter.setMaximum(59.99999999999999999999);

		latitudeDegree.setFormatterFactory(new DefaultFormatterFactory(latDegreeFormatter));
		latitudeMinute.setFormatterFactory(new DefaultFormatterFactory(latMinuteFormatter));
		latitudeSecond.setFormatterFactory(new DefaultFormatterFactory(latSecondFormatter));

		latitudeDegree.addPropertyChangeListener("value", fl);
		latitudeMinute.addPropertyChangeListener("value", fl);
		latitudeSecond.addPropertyChangeListener("value", fl);
	}

	private void initDecimalLatitude(PropertyChangeListener fl) {
		NumberFormatter latitudeFormatter = getFormatter();
		latitudeFormatter.setMaximum(90.0);
		latitudeFormatter.setMinimum(-90.0);
		decimalLatitude.setFormatterFactory(new DefaultFormatterFactory(latitudeFormatter));
		decimalLatitude.addPropertyChangeListener("value", fl);
	}

	private void initDecimalLongitude(PropertyChangeListener fl) {
		NumberFormatter longitudeFormatter = getFormatter();
		longitudeFormatter.setMaximum(180.0);
		longitudeFormatter.setMinimum(-180.0);
		decimalLongitude.setFormatterFactory(new DefaultFormatterFactory(longitudeFormatter));
		decimalLongitude.addPropertyChangeListener("value", fl);
	}

	private void initAltitude(PropertyChangeListener fl) {
		EmptyNumberFormatter altitudeFormatter = getEmptyNumberFormatter();
		decimalAltitude.setFormatterFactory(new DefaultFormatterFactory(altitudeFormatter));
		decimalAltitude.addPropertyChangeListener("value", fl);
	}

	private void importKML(Kml kml) {
		GeoTreeMaker treeMaker = new GeoTreeMaker(kml);
		treeMaker.generateTree(false);
		kmlTree.setModel(treeMaker.getTreeModel());
		kmlTree.setSelectionRow(0);
	}

	private void updateModel(Placemark pm) {
		assert model.isAddressSet() == false;
		assert model.isNameSet() == false;
		assert model.isZoomSet() == false;
		assert model.isLatitudeSet() == false;
		assert model.isLongitudeSet() == false;
		assert model.isAltitudeDefined() == false;
		
		if (pm == null) {
			return;
		}

		if (pm.getPoint() != null) {
			if (pm.getPoint().getCoordinates() != null) {
				try {
					Coordinates coord = new Coordinates(pm.getPoint().getCoordinates());
					model.setCoordinates(coord);
				} catch (IllegalArgumentException iae) {
					logger.throwing("GeoURIPanel", "updatePlacemarkDisplay", iae);
					throw new DataBindingException("Unknown format of <coordinates>", iae);
				}
			}
		}

		String name = pm.getName();
		if (name != null) {
			model.setName(name);
		}

		String address = pm.getAddress();
		if (address != null) {
			model.setAddress(address);
		}
	}

	private void clearModel() {
		clearModelCoordinates();
		model.clearName();
		model.clearAddress();
		model.clearZoom();
	}

	private void clearModelCoordinates() {
		model.clearLatitude();
		model.clearLongitude();
		model.clearAltitude();
	}

	private class ModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (GeoURIModel.ALTITUDE_PROPERTY.equals(propertyName)) {
				if (model.isAltitudeDefined()) {
					decimalAltitude.setValue(model.getAltitude());
				} else {
					if (decimalAltitude.getValue() != null) {
						decimalAltitude.setValue(null);
					}
				}
			} else if (GeoURIModel.DECIMAL_LATITUDE_PROPERTY.equals(propertyName)) {
				Double value = model.isDecimalLatitudeSet() ? model.getDecimalLatitude() : null;
				decimalLatitude.setValue(value);
			} else if (GeoURIModel.DECIMAL_LONGITUDE_PROPERTY.equals(propertyName)) {
				Double value = model.isDecimalLongitudeSet() ? model.getDecimalLongitude() : null;
				decimalLongitude.setValue(value);
			} else if (GeoURIModel.LATITUDE_DEGREE_PROPERTY.equals(propertyName)) {
				Integer value = model.isLatitudeSet() ? model.getLatitudeDegree() : null;
				latitudeDegree.setValue(value);
			} else if (GeoURIModel.LATITUDE_MINUTE_PROPERTY.equals(propertyName)) {
				Integer value = model.isLatitudeSet() ? model.getLatitudeMinute() : null;
				latitudeMinute.setValue(value);
			} else if (GeoURIModel.LATITUDE_DECIMAL_SECOND_PROPERTY.equals(propertyName)) {
				Double value = model.isLatitudeSet() ? model.getLatitudeDecimalSecond() : null;
				latitudeSecond.setValue(value);
			} else if (GeoURIModel.LONGITUDE_DEGREE_PROPERTY.equals(propertyName)) {
				Integer value = model.isLongitudeSet() ? model.getLongitudeDegree() : null;
				longitudeDegree.setValue(value);
			} else if (GeoURIModel.LONGITUDE_MINUTE_PROPERTY.equals(propertyName)) {
				Integer value = model.isLongitudeSet() ? model.getLongitudeMinute() : null;
				longitudeMinute.setValue(value);
			} else if (GeoURIModel.LONGITUDE_DECIMAL_SECOND_PROPERTY.equals(propertyName)) {
				Double value = model.isLongitudeSet() ? model.getLongitudeDecimalSecond() : null;
				longitudeSecond.setValue(value);
			} else if (GeoURIModel.ADDRESS_PROPERTY.equals(propertyName)) {
				if (model.isAddressSet()) {
					if (!model.getAddress().equals(placemarkAddress.getText())) {
						placemarkAddress.setText(model.getAddress());
					}
				} else {
					placemarkAddress.setText(null);
				}
			} else if (GeoURIModel.ZOOM_PROPERTY.equals(propertyName)) {
				int index = model.isZoomSet() ? model.getZoom() : 0;
				zoomComboBox.setSelectedIndex(index);
			}
			fireStateChanged();
		}
	}

	private class FieldListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == decimalLongitude) {
				Number o = (Number) decimalLongitude.getValue();
				if (o == null) {
					model.clearLongitude();
				} else {
					model.setDecimalLongitude(o.doubleValue());
				}
			} else if (evt.getSource() == decimalLatitude) {
				Number o = (Number) decimalLatitude.getValue();
				if (o == null) {
					model.clearLatitude();
				} else {
					model.setDecimalLatitude(o.doubleValue());
				}
			} else if (evt.getSource() == decimalAltitude) {
				Number value = (Number) evt.getNewValue();
				if (evt.getNewValue() == null) {
					model.clearAltitude();
				} else {
					model.setAltitude(value.doubleValue());
				}
			} else if (evt.getSource() == latitudeDegree) {
				Number o = (Number) latitudeDegree.getValue();
				if (o != null) {
					model.setLatitudeDegree(o.intValue());
				}
			} else if (evt.getSource() == latitudeMinute) {
				Number o = (Number) latitudeMinute.getValue();
				if (o != null) {
					model.setLatitudeMinute(o.intValue());
				}
			} else if (evt.getSource() == latitudeSecond) {
				Number o = (Number) latitudeSecond.getValue();
				if (o != null) {
					model.setLatitudeDecimalSecond(o.doubleValue());
				}
			} else if (evt.getSource() == longitudeDegree) {
				Number o = (Number) longitudeDegree.getValue();
				if (o != null) {
					model.setLongitudeDegree(o.intValue());
				}
			} else if (evt.getSource() == longitudeMinute) {
				Number o = (Number) longitudeMinute.getValue();
				if (o != null) {
					model.setLongitudeMinute(o.intValue());
				}
			} else if (evt.getSource() == longitudeSecond) {
				Number o = (Number) longitudeSecond.getValue();
				if (o != null) {
					model.setLongitudeDecimalSecond(o.doubleValue());
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

        clearButton = new JButton();
        jPanel1 = new JPanel();
        jScrollPane1 = new JScrollPane();
        placemarkAddress = new JTextArea();
        placemarkAddressLabel = new JLabel();
        jScrollPane2 = new JScrollPane();
        kmlTree = new JTree();
        importFromClipboardButton = new JButton();
        importFromFileButton = new JButton();
        jLabel15 = new JLabel();
        jPanel2 = new JPanel();
        jLabel8 = new JLabel();
        latitudeMinute = new JFormattedTextField();
        jLabel10 = new JLabel();
        jLabel3 = new JLabel();
        jLabel6 = new JLabel();
        jLabel12 = new JLabel();
        latitudeSecond = new JFormattedTextField();
        latitudeDegree = new JFormattedTextField();
        jLabel1 = new JLabel();
        decimalAltitude = new JFormattedTextField();
        jLabel2 = new JLabel();
        jLabel9 = new JLabel();
        jLabel7 = new JLabel();
        decimalLatitude = new JFormattedTextField();
        jLabel11 = new JLabel();
        jLabel5 = new JLabel();
        longitudeDegree = new JFormattedTextField();
        decimalLongitude = new JFormattedTextField();
        jLabel4 = new JLabel();
        jLabel14 = new JLabel();
        jLabel13 = new JLabel();
        longitudeMinute = new JFormattedTextField();
        longitudeSecond = new JFormattedTextField();
        zoomLabel = new JLabel();
        zoomComboBox = new JComboBox();
        jLabel16 = new JLabel();
        jLabel17 = new JLabel();

        setToolTipText(res.getString("GeoURIPanel.toolTipText")); // NOI18N

        clearButton.setText(res.getString("GeoURIPanel.clearButton.text")); // NOI18N

        jPanel1.setBorder(BorderFactory.createTitledBorder(res.getString("GeoURIPanel.jPanel1.border.title"))); // NOI18N

        placemarkAddress.setEditable(false);
        placemarkAddress.setColumns(20);
        placemarkAddress.setLineWrap(true);
        placemarkAddress.setRows(5);
        placemarkAddress.setWrapStyleWord(true);
        placemarkAddress.setFocusable(false);
        jScrollPane1.setViewportView(placemarkAddress);

        placemarkAddressLabel.setText(res.getString("GeoURIPanel.placemarkAddressLabel.text")); // NOI18N

        kmlTree.setModel(null);
        jScrollPane2.setViewportView(kmlTree);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(placemarkAddressLabel)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
                    .addComponent(jScrollPane2)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(placemarkAddressLabel)
                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
        );

        importFromClipboardButton.setText(res.getString("GeoURIPanel.importFromClipboardButton.text")); // NOI18N
        importFromClipboardButton.setToolTipText(res.getString("GeoURIPanel.importFromClipboardButton.toolTipText")); // NOI18N

        importFromFileButton.setText(res.getString("GeoURIPanel.importFromFileButton.text")); // NOI18N

        jLabel15.setText(res.getString("GeoURIPanel.jLabel15.text")); // NOI18N

        jLabel8.setText(res.getString("GeoURIPanel.jLabel8.text")); // NOI18N

        latitudeMinute.setColumns(3);
        latitudeMinute.setHorizontalAlignment(JTextField.RIGHT);
        latitudeMinute.setText(res.getString("GeoURIPanel.latitudeMinute.text")); // NOI18N

        jLabel10.setText(res.getString("GeoURIPanel.jLabel10.text")); // NOI18N

        jLabel3.setText(res.getString("GeoURIPanel.jLabel3.text")); // NOI18N

        jLabel6.setText(res.getString("GeoURIPanel.jLabel6.text")); // NOI18N

        jLabel12.setText(res.getString("GeoURIPanel.jLabel12.text")); // NOI18N

        latitudeSecond.setColumns(5);
        latitudeSecond.setHorizontalAlignment(JTextField.RIGHT);
        latitudeSecond.setText(res.getString("GeoURIPanel.latitudeSecond.text")); // NOI18N

        latitudeDegree.setColumns(4);
        latitudeDegree.setHorizontalAlignment(JTextField.RIGHT);
        latitudeDegree.setText(res.getString("GeoURIPanel.latitudeDegree.text")); // NOI18N

        jLabel1.setText(res.getString("GeoURIPanel.jLabel1.text")); // NOI18N

        decimalAltitude.setColumns(5);
        decimalAltitude.setHorizontalAlignment(JTextField.RIGHT);
        decimalAltitude.setText(res.getString("GeoURIPanel.decimalAltitude.text")); // NOI18N

        jLabel2.setText(res.getString("GeoURIPanel.jLabel2.text")); // NOI18N

        jLabel9.setText(res.getString("GeoURIPanel.jLabel9.text")); // NOI18N

        jLabel7.setText(res.getString("GeoURIPanel.jLabel7.text")); // NOI18N

        decimalLatitude.setColumns(8);
        decimalLatitude.setHorizontalAlignment(JTextField.RIGHT);
        decimalLatitude.setText(res.getString("GeoURIPanel.decimalLatitude.text")); // NOI18N

        jLabel11.setText(res.getString("GeoURIPanel.jLabel11.text")); // NOI18N

        jLabel5.setText(res.getString("GeoURIPanel.jLabel5.text")); // NOI18N

        longitudeDegree.setColumns(4);
        longitudeDegree.setHorizontalAlignment(JTextField.TRAILING);
        longitudeDegree.setText(res.getString("GeoURIPanel.longitudeDegree.text")); // NOI18N

        decimalLongitude.setColumns(8);
        decimalLongitude.setHorizontalAlignment(JTextField.RIGHT);
        decimalLongitude.setText(res.getString("GeoURIPanel.decimalLongitude.text")); // NOI18N

        jLabel4.setText(res.getString("GeoURIPanel.jLabel4.text")); // NOI18N

        jLabel14.setText(res.getString("GeoURIPanel.jLabel14.text")); // NOI18N

        jLabel13.setText(res.getString("GeoURIPanel.jLabel13.text")); // NOI18N

        longitudeMinute.setColumns(3);
        longitudeMinute.setHorizontalAlignment(JTextField.RIGHT);
        longitudeMinute.setText(res.getString("GeoURIPanel.longitudeMinute.text")); // NOI18N

        longitudeSecond.setColumns(5);
        longitudeSecond.setHorizontalAlignment(JTextField.RIGHT);
        longitudeSecond.setText(res.getString("GeoURIPanel.longitudeSecond.text")); // NOI18N

        zoomLabel.setText(res.getString("GeoURIPanel.zoomLabel.text")); // NOI18N

        zoomComboBox.setModel(new DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));

        jLabel16.setText(res.getString("GeoURIPanel.jLabel16.text")); // NOI18N

        jLabel17.setText(res.getString("GeoURIPanel.jLabel17.text")); // NOI18N

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
                            .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING, false)
                            .addComponent(decimalLatitude)
                            .addComponent(decimalLongitude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(decimalAltitude, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(zoomLabel)
                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(zoomComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(jLabel13)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(latitudeDegree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(jLabel14)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(longitudeDegree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(longitudeMinute, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jLabel10)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(longitudeSecond, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jLabel11))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(latitudeMinute, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addComponent(latitudeSecond, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(jLabel7))))
                    .addComponent(jLabel12)
                    .addComponent(jLabel16))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(decimalLatitude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(latitudeDegree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(latitudeMinute, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(latitudeSecond, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel13))
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(decimalLongitude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(longitudeDegree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(longitudeMinute, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(longitudeSecond, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel14))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(decimalAltitude, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(zoomLabel)
                    .addComponent(zoomComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(importFromClipboardButton)
                    .addComponent(importFromFileButton)
                    .addComponent(jLabel15)
                    .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(clearButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(ComponentPlacement.UNRELATED)
                        .addComponent(clearButton)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(importFromFileButton)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(importFromClipboardButton)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JButton clearButton;
    private JFormattedTextField decimalAltitude;
    private JFormattedTextField decimalLatitude;
    private JFormattedTextField decimalLongitude;
    private JButton importFromClipboardButton;
    private JButton importFromFileButton;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JLabel jLabel13;
    private JLabel jLabel14;
    private JLabel jLabel15;
    private JLabel jLabel16;
    private JLabel jLabel17;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTree kmlTree;
    private JFormattedTextField latitudeDegree;
    private JFormattedTextField latitudeMinute;
    private JFormattedTextField latitudeSecond;
    private JFormattedTextField longitudeDegree;
    private JFormattedTextField longitudeMinute;
    private JFormattedTextField longitudeSecond;
    private JTextArea placemarkAddress;
    private JLabel placemarkAddressLabel;
    private JComboBox zoomComboBox;
    private JLabel zoomLabel;
    // End of variables declaration//GEN-END:variables

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public int getMnemonic() {
		return KeyEvent.VK_G;
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

	@Override
	public String getContent() {
		if (model.getIsValidLatitude() == TriState.TRUE && model.getIsValidLongitude() == TriState.TRUE) {
			return model.getExtendedGeoURIAsString();
		} else {
			return null;
		}
	}

	private class ClipboardListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();
			if (ClipboardService.AVAILABLE_AS_STRING_FLAVOR_PROPERTY.equals(propertyName)) {
				TriState state = (TriState) evt.getNewValue();
				importFromClipboardButton.setEnabled(state == TriState.TRUE || state == TriState.NOT_APPLICABLE);
			}
		}
	}

	private class ConcreteReadable implements ReaderSaver.Readable {

		private Shortener<String> shortener = new TextShortener(70);

		@Override
		public void readFrom(URI uri) {
			File f = new File(uri);
			KMLFileReader reader = new KMLFileReader(f, ONE_MEGABYTE, logger);
			try {
				reader.readFile();
				Kml kml = reader.getKml();
				importKML(kml);
			} catch (ZipException ex) {
				logger.throwing("GeoURIPanel.ConcreteReadable", "readFrom(URI)", ex);
				String title = res.getString("NOT A KMZ FILE TITLE");
				String message = MessageFormat.format(res.getString("NOT A KMZ FILE MESSAGE"), "\n", shortener.shorten(f.getAbsolutePath()));
				int type = JOptionPane.ERROR_MESSAGE;
				JOptionPane.showMessageDialog(null, message, title, type);
			} catch (FileNotFoundException ex) {
				logger.throwing("GeoURIPanel.ConcreteReadable", "readFrom(URI)", ex);
				String title = res.getString("FILE NOT FOUND TITLE");
				String message = MessageFormat.format(res.getString("FILE NOT FOUND MESSAGE"), "\n", shortener.shorten(f.getAbsolutePath()));
				int type = JOptionPane.ERROR_MESSAGE;
				JOptionPane.showMessageDialog(null, message, title, type);
			} catch (IOException ex) {
				logger.throwing("GeoURIPanel.ConcreteReadable", "readFrom(URI)", ex);
				String title = res.getString("IO TITLE");
				String message = MessageFormat.format(res.getString("IO MESSAGE"), "\n", shortener.shorten(f.getAbsolutePath()));
				int type = JOptionPane.ERROR_MESSAGE;
				JOptionPane.showMessageDialog(null, message, title, type);
			}
		}
	}

	private class KmlTreeSelectionListener implements TreeSelectionListener {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) kmlTree.getLastSelectedPathComponent();

			clearModel();

			if (node == null) {
				return;
			}

			Object nodeInfo = node.getUserObject();
			if (node.isLeaf()) {
				if (nodeInfo instanceof Placemark) {
					updateModel((Placemark) nodeInfo);
				}
			}
		}
	}

	private class ZoomComboBoxListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JComboBox b = (JComboBox) e.getSource();
			int index = b.getSelectedIndex();
			if (index == 0) {
				model.clearZoom();
			} else {
				model.setZoom(index);
			}
		}
	}
}
