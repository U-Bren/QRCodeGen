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
import java.util.List;

/**
 * A VCardList that uses ',' as delimiter, so *-lists (text-list, date-list,...)
 * of the VCard v4 can be built.
 *
 * @author Stefan Ganzer
 * @param <E>
 */
class VCardValueList<E extends VCardValue> extends qrcodegen.tools.ForwardingList<E> implements VCardList<E> {

	private static final String DELIMITER = ",";

	VCardValueList(List<E> l) {
		super(l);
	}

	static <E extends VCardValue> VCardValueList<E> newArrayList() {
		return new VCardValueList<E>(new ArrayList<E>());
	}

	@Override
	public String delimiter() {
		return DELIMITER;
	}

	@Override
	public String getValueAsString() {
		return VCardTools.asDelimitedString(super.subList(0, super.size()), delimiter());
	}

	@Override
	public int elements() {
		return size();
	}
}
