/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package qrcodegen.modules.vcard.reader;

/**
 *
 * @author Stefan Ganzer
 */
abstract class AbstractVCardState implements VCardParsingState {

	private final VCardReader reader;

	AbstractVCardState(VCardReader reader) {
		if (reader == null) {
			throw new NullPointerException();
		}
		this.reader = reader;
	}

	@Override
	public void foundBeginProperty() {
		reader.setState(reader.getMalformedVCardState());
	}

	@Override
	public void foundVersionProperty() {
		reader.setState(reader.getMalformedVCardState());
	}

	@Override
	public void foundFNProperty() {
		reader.setState(reader.getMalformedVCardState());
	}

	@Override
	public void foundContentProperty() {
		reader.setState(reader.getMalformedVCardState());
	}

	@Override
	public void foundEndProperty() {
		reader.setState(reader.getMalformedVCardState());
	}
	
	@Override
	public void endOfInput(){
		reader.setState(reader.getMalformedVCardState());
	}

	@Override
	public void foundUnknownProperty() {
		reader.setState(reader.getMalformedVCardState());
	}

	@Override
	public void foundEmptyLine() {
		reader.setState(reader.getMalformedVCardState());
	}

	@Override
	public void foundMalformedLine() {
		reader.setState(reader.getMalformedVCardState());
	}
	
	void reset(){
		reader.setState(reader.getNewState());
	}

	/**
	 * Returns the VCardReader.
	 * @return the VCardReader
	 */
	VCardReader getReader() {
		return reader;
	}
}
