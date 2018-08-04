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

import java.util.Set;

/**
 * Implements the different legal ways types may printed.
 *
 * PARAMETER_LIST: TYPE=type1;TYPE=type2 <p>VALUE_LIST: TYPE=type1,type2
 *
 * @author Stefan Ganzer
 * @see https://www.ietf.org/mail-archive/web/vcarddav/current/msg02490.html
 */
enum ListType {

	PARAMETER_LIST {
		/*
		 * ;TYPE=type1;TYPE=type2
		 */
		@Override
		public String asString(Set<TypeParameter> parameter) {
			if (parameter == null) {
				throw new NullPointerException();
			}
			StringBuilder sb = new StringBuilder(parameter.size() * 8);
			for (TypeParameter p : parameter) {
				sb.append(PREFIX).append(p.getValueAsString());
			}
			return sb.toString();
		}
	}, VALUE_LIST {
		/*
		 * ;TYPE=type1,type2
		 */
		@Override
		public String asString(Set<TypeParameter> parameter) {
			if (parameter == null) {
				throw new NullPointerException();
			}
			String result;
			if (parameter.isEmpty()) {
				result = EMPTY_STRING;
			} else {
				StringBuilder sb = new StringBuilder(VCardTools.asDelimitedString(parameter, VALUE_DELIMITER));
				sb.insert(0, PREFIX);
				result = sb.toString();
			}
			return result;
		}
	};
	private static final String PREFIX = ";TYPE=";
	private static final String EMPTY_STRING = "";
	private static final String VALUE_DELIMITER = ",";

	/**
	 * Returns a {@literal Set<TypeParameter>} as a string suitable for v4
	 * vCards.
	 *
	 * The returned string includes the prefic ";TYPE=" if the given set is not
	 * empty.
	 *
	 * <p>PARAMETER_LIST: ;TYPE=type1;TYPE=type2 <p>VALUE_LIST:
	 * ;TYPE=type1,type2
	 *
	 * @param parameter a set of {@code TypeParameter}s
	 *
	 * @return the content of parameter as a string. If parameter is empty, an
	 * empty string will be returned.
	 */
	public abstract String asString(Set<TypeParameter> parameter);
}
