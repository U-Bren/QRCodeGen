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
package qrcodegen.modules.vcard;

/**
 *
 * @author Stefan Ganzer
 */
public final class NoteProperty extends PropertyEntry {

	private final VCardText note;

	private NoteProperty(Builder builder) {
		super(builder);
		String s = builder.note;
		if (s == null) {
			throw new NullPointerException();
		}
		note = new VCardText(s);
	}

	public static final class Builder extends PropertyEntry.Builder2 {

		private final String note;

		public Builder(String note) {
			super(Property.NOTE);
			this.note = note;
		}

		@Override
		public NoteProperty build() {
			return new NoteProperty(this);
		}
	}

	/**
	 * Returns the original text of this NoteProperties' note.
	 *
	 * @return the original text of this NoteProperties' note
	 */
	public String getNote() {
		return note.getOriginalValue();
	}

	@Override
	public VCardValue getValue() {
		assert note != null;
		return note;
	}

	@Override
	public String getValueAsString() {
		return note.getValueAsString();
	}
}
