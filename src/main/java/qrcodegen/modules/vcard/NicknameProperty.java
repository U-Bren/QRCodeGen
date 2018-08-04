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

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Stefan Ganzer
 */
public final class NicknameProperty extends PropertyEntry {

	// nicknames are in a text-list: VCardValueList
	private final VCardList<VCardText> nicknames;

	private NicknameProperty(Builder builder) {
		super(builder);
		VCardValueList<VCardText> list = VCardValueList.newArrayList();
		for (String s : builder.nicknames) {
			if (s == null) {
				throw new NullPointerException();
			}
			list.add(new VCardText(s));
		}
		nicknames = UnmodifiableVCardList.newInstance(list);

	}

	//<editor-fold defaultstate="collapsed" desc="Definition of NICKNAME">
	/*
	 * 6.2.3. NICKNAME
	 *
	 * Purpose: To specify the text corresponding to the nickname of the object
	 * the vCard represents.
	 *
	 * Value type: One or more text values separated by a COMMA character
	 * (U+002C).
	 *
	 * Cardinality: *
	 *
	 * Special note: The nickname is the descriptive name given instead of or in
	 * addition to the one belonging to the object the vCard represents. It can
	 * also be used to specify a familiar form of a proper name specified by the
	 * FN or N properties.
	 *
	 * ABNF:
	 *
	 * NICKNAME-param = "VALUE=text" / type-param / language-param / altid-param
	 * / pid-param / pref-param / any-param NICKNAME-value = text-list
	 *
	 * Examples:
	 *
	 * NICKNAME:Robbie
	 *
	 * NICKNAME:Jim,Jimmie
	 *
	 * NICKNAME;TYPE=work:Boss
	 *
	 */
	//</editor-fold>
	public static final class Builder extends PropertyEntry.Builder2 {

		private final Set<TypeParameter> type = EnumSet.noneOf(TypeParameter.class);
		//private final VCardArrayList<VCardValue> nicknames = VCardArrayList.newArrayList();
		private final List<String> nicknames = new ArrayList<String>();

		public Builder() {
			super(Property.NICKNAME);
		}

		public Builder nickname(String n) {
			nicknames.add(n);
			return this;
		}

		public Builder nicknames(String[] n) {
			for (String s : n) {
				nicknames.add(s.trim());
			}
			return this;
		}

		@Override
		public NicknameProperty build() {
			return new NicknameProperty(this);
		}
	}

	@Override
	public VCardValue getValue() {
		assert nicknames != null;
		assert nicknames instanceof UnmodifiableVCardList;
		return nicknames;
	}

	@Override
	public String getValueAsString() {
		return nicknames.getValueAsString();
	}

	/**
	 * Returns the nickname in its original form.
	 *
	 * @return the nickname in its original form. Never returns null.
	 */
	public String getNickname() {
		return VCardTools.originalValuesOfvCardListToString(nicknames);
	}
}
