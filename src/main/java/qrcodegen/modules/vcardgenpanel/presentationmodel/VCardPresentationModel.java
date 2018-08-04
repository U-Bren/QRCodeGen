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
package qrcodegen.modules.vcardgenpanel.presentationmodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import qrcodegen.Encodable;
import qrcodegen.modules.vcard.IllegalCharacterException;
import qrcodegen.modules.vcard.VCard;
import qrcodegen.modules.vcard.VCardTools;
import qrcodegen.modules.vcard.reader.VCardReader;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.modules.vcardgenpanel.model.VCardModel;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardPresentationModel extends AbstractPresentationModel implements Encodable {

	private static final ResourceBundle RES = ResourceBundle.getBundle("qrcodegen/modules/vcardgenpanel/presentationmodel/VCardPresentationModel");
	public static final String GENERATION_ENABLED_STATE = "GenerationEnabledState"; //NOI18N
	public static final String CLEARING_ENABLED_STATE = "ClearingEnabledState"; //NOI18N
	public static final String EXPORT_ENABLED_STATE = "ExportEnabledState"; //NOI18N
	public static final String TYPE_PARAMETER = "TypeParameter"; //NOI18N
	public static final String IMPORT_VCARD = "ImportVCard"; //NOI18N
	public static final String EXPORT_VCARD = "ExportVCard"; //NOI18N
	public static final String IGNORED_CONTENT = "IgnoredProperties"; //NOI18N
	public static final String PARSING_FAILED = "MalformedLine"; //NOI18N
	public static final String FILE_NOT_FOUND_EXCEPTION = "FileNotFoundException"; //NOI18N
	public static final String READING_IO_EXCEPTION = "ReadintIOException"; //NOI18N
	public static final String READING_ILLEGAL_CHARACTERS_EXCEPTION = "ReadintIllegalCharactersException"; //NOI18N
	public static final String WRITING_IO_EXCEPTION = "WritingIOException"; //NOI18N
	private static final Logger LOGGER = Logger.getLogger(VCardPresentationModel.class.getPackage().getName());
	private static final String NEWLINE = "\n";
	private static final Charset UTF_8 = Charset.forName("UTF-8"); //NOI18N
	private static final String EMPTY_STRING = "";
	private final EventListenerList listenerList = new EventListenerList();
	private final VCardModel vCardModel;
	private final List<PresentationModel> presentationModels;
	/** Listens to the registered {@code PresentationModel}s */
	private final PropertyChangeListener presentationModelListener = new PresentationModelListener();
	private InputValidity validity;
	private Map<PresentationModel, InputValidity> iv;
	private boolean isGenerationEnabled = false;
	private boolean isClearingEnabled = false;
	private boolean isExportEnabled = false;
	private boolean aPresentationModelChanged = false;
	private String ignoredContentMessage = null;
	private String parsingFailedMessage = null;
	private transient ChangeEvent changeEvent;

	public VCardPresentationModel(VCardModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		this.vCardModel = model;
		presentationModels = new ArrayList<PresentationModel>(10);
		iv = new ConcurrentHashMap<PresentationModel, InputValidity>(presentationModels.size());
	}

	@Override
	public void clear() {
		for (PresentationModel m : presentationModels) {
			m.clear();
		}
	}

	@Override
	public void update() {
		for (PresentationModel m : presentationModels) {
			m.update();
		}
	}

	@Override
	public InputValidity getValidity() {
		throw new UnsupportedOperationException("Not supported yet."); //NOI18N
	}

	public void addPresentationModel(PresentationModel model) {
		if (model == null) {
			throw new NullPointerException();
		}
		presentationModels.add(model);
		aPresentationModelChanged = true;
		model.addPropertyChangeListener(presentationModelListener);
	}

	public void importVCard() {
		clear();
		firePropertyChange(IMPORT_VCARD, null, null);
	}

	/**
	 * Opens a VCard and sets this model from its content.
	 *
	 * @param f the file to read a VCard from
	 *
	 * @throws NullPointerException if f is null
	 */
	public void importVCard(File f) {
		if (f == null) {
			throw new NullPointerException();
		}
		ignoredContentMessage = null;
		parsingFailedMessage = null;
		// Allow the view to display a modal message dialog while importing,
		// thus preventing user input
//		firePropertyChange("ImportingVcard", false, true);
		(new VCardImporter(f, vCardModel)).execute();
	}

	public void exportVCard() {
		generate();
		if (isValidState()) {
			firePropertyChange(EXPORT_VCARD, null, null);
		}
	}

	/**
	 * Saves a created VCard.
	 *
	 * @param f the file to save a VCard to
	 *
	 * @throws NullPointerException if f is null
	 */
	public void exportVCard(File f) {
		if (f == null) {
			throw new NullPointerException();
		}
		try {
			VCardTools.writeCard(f, getContent());
		} catch (FileNotFoundException ex) {
			firePropertyChange(WRITING_IO_EXCEPTION, null, f);
		} catch (IOException ex) {
			firePropertyChange(WRITING_IO_EXCEPTION, null, f);
		}

	}

	/**
	 * Creates a VCard from this models' content.
	 */
	public void generate() {
		update();
		if (determineGenerationEnabledState()) {
			vCardModel.createVCard();
			aPresentationModelChanged = false;
			setGenerationEnabled(false);
			fireContentChanged();
		}
	}

	public boolean hasVCard() {
		return vCardModel.getVCard() != null;
	}

	public VCard getVCard() {
		return vCardModel.getVCard();
	}

	@Override
	public String getContent() {
		if (aPresentationModelChanged) {
			return EMPTY_STRING; //NOI18N
		}
		VCard vCard = vCardModel.getVCard();
		String result;
		if (vCard == null) {
			assert false;
			result = EMPTY_STRING; //NOI18N
		} else {
			result = vCard.toString();
		}
		return result;

	}

	/**
	 * Returns true if there is content that can be cleared.
	 *
	 * @return true if there is content that can be cleared, false otherwise.
	 */
	public boolean isClearingEnabled() {
		return isClearingEnabled;
	}

	/**
	 * Returns true if there is content a VCard could possibly be generated
	 * from.
	 *
	 * @return true if there is content a VCard could possibl be generated from,
	 * false otherwise
	 */
	public boolean isGenenerationEnabled() {
		return isGenerationEnabled;
	}

	/**
	 * Returns true if there is content a VCard could possibly be written from.
	 *
	 * @return true if there is content a VCard could possibl be written from,
	 * false otherwise
	 */
	public boolean isExportEnabled() {
		return isExportEnabled;
	}

	public boolean isImportEnabled() {
		return true;
	}

	public String getIgnoredContentMessage() {
		return ignoredContentMessage;
	}

	public String getParsingFailedMessage() {
		return parsingFailedMessage;
	}

	public synchronized void addChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to register listener twice: " + listener); //NOI18N
		}
		listenerList.add(ChangeListener.class, listener);
	}

	public synchronized void removeChangeListener(ChangeListener listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		if (!Arrays.asList(listenerList.getListeners(ChangeListener.class)).contains(listener)) {
			throw new IllegalStateException("Trying to remove unregistered listener: " + listener); //NOI18N
		}
		listenerList.remove(ChangeListener.class, listener);
	}

	private void fireContentChanged() {
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

	private Set<InputValidity> getInputValidityValues() {
		Set<InputValidity> s;
		if (iv.values().isEmpty()) {
			s = EnumSet.noneOf(InputValidity.class);
		} else {
			s = EnumSet.copyOf(iv.values());
		}
		return s;
	}

	/**
	 * Listens to the registered {@code PresentationModel}s.
	 */
	private class PresentationModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (VALIDITY.equals(propertyName)) {
				Object source = evt.getSource();
				if (source instanceof PresentationModel) {
					Object newValue = evt.getNewValue();
					if (newValue instanceof InputValidity) {
						iv.put((PresentationModel) evt.getSource(), (InputValidity) newValue);
						setContentChanged();
//						boolean value = determineGenerationEnabledState();
//						setGenerationEnabled(value);
//						setExportEnabled(value);
					} else {
						LOGGER.log(Level.FINER, "newValue not of type InputValidity: {0}", newValue.getClass());
					}
				} else {
					LOGGER.log(Level.FINER, "source not of type PresentationModel: {0}", source.getClass());
				}
			} else if (TYPE_PARAMETER.equals(propertyName)) {
				setContentChanged();
//			} else if (TelephonePresentationModel.ENTRY_ADDED.equals(propertyName)) {
//				setContentChanged();
//			} else if (TelephonePresentationModel.ENTRY_REMOVED.equals(propertyName)) {
//				setContentChanged();
			} else if (CONTENT_CHANGED.equals(propertyName)) {
				setContentChanged();
			}
		}
	}

	private void setContentChanged() {
//		if (aPresentationModelChanged) {
//			return;
//		}

		aPresentationModelChanged = true;
		boolean value = determineGenerationEnabledState();
		setGenerationEnabled(value);
		setExportEnabled(value);

		fireContentChanged();
	}

	private void setClearingEnabled(boolean value) {
		boolean oldValue = isClearingEnabled;
		isClearingEnabled = value;
		firePropertyChange(CLEARING_ENABLED_STATE, oldValue, value);
	}

	private void setGenerationEnabled(boolean value) {
		boolean oldValue = isGenerationEnabled;
		isGenerationEnabled = value;
		firePropertyChange(GENERATION_ENABLED_STATE, oldValue, value);
	}

	private void setExportEnabled(boolean value) {
		boolean oldValue = isExportEnabled;
		isExportEnabled = value;
		firePropertyChange(EXPORT_ENABLED_STATE, oldValue, value);
	}

	private boolean determineGenerationEnabledState() {
		Set<InputValidity> s = getInputValidityValues();
		return !s.contains(InputValidity.INVALID) && (s.contains(InputValidity.VALID) || s.contains(InputValidity.UNDEFINED));
	}

	private boolean isValidState() {
		Set<InputValidity> s = getInputValidityValues();
		return !s.contains(InputValidity.INVALID) && !s.contains(InputValidity.UNDEFINED) && s.contains(InputValidity.VALID);
	}

	private class VCardImporter extends SwingWorker<VCardReader, Void> {

		private final File file;
		private final VCardModel model;

		VCardImporter(File f, VCardModel model) {
			if (f == null) {
				throw new NullPointerException();
			}
			if (model == null) {
				throw new NullPointerException();
			}
			this.file = f;
			this.model = model;
		}

		@Override
		protected VCardReader doInBackground() throws Exception {
			String input;

			input = VCardTools.readCard(file, UTF_8);

			VCardReader reader = new VCardReader(input);
			reader.parseInput();
			return reader;
		}

		@Override
		protected void done() {
			try {
				VCardReader reader = get();
				if (reader.foundValidVCard()) {
					model.setFromVCard(reader.getVCard());
					LOGGER.log(Level.FINEST, "--- New VCard ---\n{0}--- End of new VCard ---", reader.getVCard().toString());

					StringBuilder sb = new StringBuilder(500);
					if (reader.hasUnknownTypeParameters()) {
						Map<String, Set<String>> unknownTypeParameters = reader.getUnknownTypeParameters();
						sb.append(RES.getString("UNKNOWN TYPE PARAMETERS"))
								.append(NEWLINE);

						for (Map.Entry<String, Set<String>> entry : unknownTypeParameters.entrySet()) {
							sb.append(VCardTools.shorten(entry.getKey(), 80))
									.append(" --> "); //NOI18N
							for (String s : entry.getValue()) {
								sb.append(VCardTools.shorten(s, 20));
							}
							sb.append(NEWLINE); //NOI18N
						}
					}
					if (reader.hasUnknownProperties()) {
						List<String> unknownProperties = reader.getUnknownProperties();
						sb.append(RES.getString("UNKNOWN PROPERTIES"))
								.append(NEWLINE); //NOI18N

						for (String s : unknownProperties) {
							sb.append(VCardTools.shorten(s, 80))
									.append(NEWLINE); //NOI18N
						}

					}
					String message = sb.toString();
					if (!message.isEmpty()) {
						ignoredContentMessage = message;
						VCardPresentationModel.this.firePropertyChange(IGNORED_CONTENT, null, null);
						LOGGER.log(Level.FINEST, "ignoredContentMessage={0}", message);
					}
					generate();
				} else {
					String shortenedInput = VCardTools.shorten(reader.getInput(), 80);
					LOGGER.log(Level.INFO, "No valid VCard found in:\n{0}", shortenedInput);
					String malformedLine = reader.getMalformedLine();
					if (malformedLine == null) {
						parsingFailedMessage = MessageFormat.format(RES.getString("PARSING FAILED"), "\n", shortenedInput);
					} else {
						String shortenedMalformedLine = VCardTools.shorten(malformedLine, 80);
						parsingFailedMessage = MessageFormat.format(RES.getString("PARSING ABORTED"), "\n", shortenedMalformedLine);
						LOGGER.log(Level.INFO, "Parsing aborted:\n{0}", shortenedMalformedLine);
					}
					VCardPresentationModel.this.firePropertyChange(PARSING_FAILED, null, null);
				}
			} catch (ExecutionException e) {
				Throwable t = e.getCause();
				if (t instanceof FileNotFoundException) {
					VCardPresentationModel.this.firePropertyChange(FILE_NOT_FOUND_EXCEPTION, null, file);
				} else if (t instanceof IllegalCharacterException) {
					VCardPresentationModel.this.firePropertyChange(READING_ILLEGAL_CHARACTERS_EXCEPTION, null, file);
				} else if (t instanceof IOException) {
					VCardPresentationModel.this.firePropertyChange(READING_IO_EXCEPTION, null, file);
				} else {
					LOGGER.log(Level.SEVERE, "Unhandled exception", e);
					throw new RuntimeException(e);
				}
				LOGGER.throwing("VCardPresentationModel.VCardImporter", "done", e);
			} catch (InterruptedException ie) {
				LOGGER.log(Level.INFO, "InterruptedException", ie);
			} finally {
				// Allow the view to remove a modal message dialog
				// that prevents user input
//				VCardPresentationModel.this.firePropertyChange("ImportingVCard", true, false);
			}
		}
	}
}
