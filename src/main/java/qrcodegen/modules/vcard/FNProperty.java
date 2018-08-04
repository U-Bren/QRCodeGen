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
public final class FNProperty extends PropertyEntry {

	private final VCardText formattedName;

	private FNProperty(Builder builder) {
		super(builder);
		formattedName = new VCardText(builder.formattedName);
	}

	public static final class Builder extends PropertyEntry.Builder2 {

		private final String formattedName;

		public Builder(String formattedName) {
			super(Property.FN);
			this.formattedName = formattedName;
		}

		@Override
		public FNProperty build() {
			return new FNProperty(this);
		}
	}

	@Override
	public VCardValue getValue() {
		assert formattedName != null;
		return formattedName;
	}

	@Override
	public String getValueAsString() {
		return formattedName.getValueAsString();
	}

	/**
	 * Returns the formatted name in its original form.
	 *
	 * @return the formatted name in its original form. Never returns null.
	 */
	public String getFormattedName() {
		return formattedName.getOriginalValue();
	}
}
