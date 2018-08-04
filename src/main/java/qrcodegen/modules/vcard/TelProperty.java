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
package qrcodegen.modules.vcard;

/**
 *
 * @author Stefan Ganzer
 */
public final class TelProperty extends PropertyEntry {

	private final VCardText tel;

	private TelProperty(Builder builder) {
		super(builder);
		String s = builder.tel;
		if (s == null) {
			throw new NullPointerException();
		}
		this.tel = new VCardText(s);
	}

	public static final class Builder extends PropertyEntry.Builder2 {

		private final String tel;
		/** For now only phone numbers in text form are supported. */
		private final ValueType valueParameter;

		public Builder(String tel) {
			this(ValueType.TEXT, tel);
		}

		private Builder(ValueType value, String tel) {
			super(Property.TEL);
			this.valueParameter = value;
			this.tel = tel;
		}

		@Override
		ValueType getValueParameter() {
			return valueParameter;
		}

		@Override
		public TelProperty build() {
			return new TelProperty(this);
		}
	}

	@Override
	public VCardValue getValue() {
		return tel;
	}

	@Override
	public String getValueAsString() {
		return tel.getValueAsString();
	}

	/**
	 * Returns true if the phone number is in text form, false otherwise.
	 *
	 * @return true if the phone number is in text form, false otherwise.
	 */
	public boolean hasTextValue() {
		return true;
	}

	/**
	 * Returns the original text phone number.
	 *
	 * @return the original text phone number. Never returns null.
	 *
	 * @see #hasTextValue()
	 * @throws IllegalStateException if there is no phone number in text form
	 */
	public String getText() {
		if (!hasTextValue()) {
			throw new IllegalStateException();
		}
		String result = tel.getOriginalValue();
		assert result != null;
		return result;
	}
}
