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

import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardURI implements VCardValue {

	private final URI uri;

	VCardURI(String value) throws URISyntaxException {
		if(value == null){
			throw new NullPointerException();
		}
		this.uri = new URI(value);
	}
	
	VCardURI(URI uri){
		if(uri == null){
			throw new NullPointerException();
		}
		// java.net.URI is final and immutable
		this.uri = uri;
	}

	@Override
	public String getValueAsString() {
		return uri.toString();
	}

	@Override
	public int elements() {
		return 1;
	}
	
	/**
	 * @see java.net.URI#getScheme()
	 */
	public String getScheme(){
		return uri.getScheme();
	}
	
	/**
	 * @see java.net.URI#getSchemeSpecificPart() 
	 */
	public String getSchemeSpecificPart(){
		return uri.getSchemeSpecificPart();
	}
	
	public String getUriAsString(){
		return uri.toString();
	}
	
	public URI getUri(){
		assert uri != null;
		return uri;
	}
}
