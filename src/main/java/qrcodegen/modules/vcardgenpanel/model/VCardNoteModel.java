/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcardgenpanel.model;

import java.util.ArrayList;
import java.util.List;
import qrcodegen.modules.vcard.NoteProperty;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcardgenpanel.VCardPropertyProvider;
import qrcodegen.tools.CollectionTools;
import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardNoteModel extends AbstractModel implements VCardPropertyProvider {

	public static final String NOTE_ELEMENT = "Note";
	private String note = null;

	public VCardNoteModel() {
	}

	/**
	 * Returns the note.
	 *
	 * This method never returns null.
	 *
	 * @return the note
	 */
	public String getNote() {
		if (!isNoteSet()) {
			throw new IllegalStateException();
		}
		return note;
	}

	/**
	 * Sets the note.
	 *
	 * @param newNote the new note. Mustn't be null.
	 *
	 * @throws NullPointerException if newNote is null
	 */
	public void setNote(String newNote) {
		if (newNote == null) {
			throw new NullPointerException();
		}
		setNoteValue(newNote);
	}

	/**
	 * Returns true if this note is set, false otherwise.
	 *
	 * @return true if this note is set, false otherwise. A set but empty note
	 * will return true.
	 */
	public boolean isNoteSet() {
		return note != null;
	}

	@Override
	public void clear() {
		setNoteValue(null);
	}

	private void setNoteValue(String value) {
		String oldNote = this.note;
		this.note = value;
		firePropertyChange(NOTE_ELEMENT, oldNote, value);
	}

	@Override
	public List<PropertyEntry> getPropertyEntries() {
		List<PropertyEntry> list = new ArrayList<PropertyEntry>(1);
		CollectionTools.addIfNonNull(list, getNoteProperty());
		return list;
	}

	private NoteProperty getNoteProperty() {
		if (!isNoteSet()) {
			return null;
		}
		return new NoteProperty.Builder(note).build();
	}

	public void setFromPropertyEntries(List<NoteProperty> properties) {
		if (properties == null) {
			throw new NullPointerException();
		}
		if (!properties.isEmpty()) {
			setNote(properties.get(0).getNote());
		}
	}
}
