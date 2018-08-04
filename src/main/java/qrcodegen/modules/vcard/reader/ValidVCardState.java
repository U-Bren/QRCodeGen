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
package qrcodegen.modules.vcard.reader;

/**
 *
 * @author Stefan Ganzer
 */
class ValidVCardState extends AbstractVCardState {

	ValidVCardState(VCardReader reader) {
		super(reader);
	}

	@Override
	public void foundBeginProperty() {
		throw new IllegalStateException();
	}

	@Override
	public void foundVersionProperty() {
		throw new IllegalStateException();
	}

	@Override
	public void foundFNProperty() {
		throw new IllegalStateException();
	}

	@Override
	public void foundContentProperty() {
		throw new IllegalStateException();
	}

	@Override
	public void foundEndProperty() {
		throw new IllegalStateException();
	}

	@Override
	public void endOfInput() {
		throw new IllegalStateException();
	}

	@Override
	public void foundUnknownProperty() {
		throw new IllegalStateException();
	}

	@Override
	public void foundEmptyLine() {
		throw new IllegalStateException();
	}

	@Override
	public void foundMalformedLine() {
		throw new IllegalStateException();
	}
}
