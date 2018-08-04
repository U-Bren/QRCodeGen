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
 * List of components, i.e. components separated by a ";" semicolon. Be aware
 * that this is not a list-component, which is components separated by a ","
 * comma.
 *
 * @author Stefan Ganzer
 */
class VCardComponentList<E extends VCardValue> extends qrcodegen.tools.ForwardingList<E> implements VCardList<E> {

	private static final String DELIMITER = ";";

	VCardComponentList(List<E> l) {
		super(l);
	}

	static <E extends VCardValue> VCardComponentList<E> newArrayList() {
		return new VCardComponentList<E>(new ArrayList<E>());
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
