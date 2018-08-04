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
public final class EMailProperty extends PropertyEntry {

	private final VCardText email;

	private EMailProperty(Builder builder) {
		super(builder);
		email = new VCardText(builder.email);
	}

	@Override
	public VCardValue getValue() {
		assert email != null;
		return email;
	}

	@Override
	public String getValueAsString() {
		return email.getValueAsString();
	}

	public static final class Builder extends PropertyEntry.Builder2 {

		private final String email;
		/** For now only email addresses in text form are supported. */
		private ValueType valueParameter;

		public Builder(String mail) {
			this(ValueType.TEXT, mail);
		}

		// For now only string type email addresses are supported,
		// but we are prepared to support uri type addresses in a later version.
		private Builder(ValueType value, String mail) {
			super(Property.EMAIL);
			this.valueParameter = value;
			this.email = mail;
		}

		@Override
		ValueType getValueParameter() {
			return valueParameter;
		}

		@Override
		public EMailProperty build() {
			return new EMailProperty(this);
		}
	}

	/**
	 * Returns the original EMail address.
	 *
	 * @return the original EMail address
	 */
	public String getEMail() {
		return email.getOriginalValue();
	}
}
