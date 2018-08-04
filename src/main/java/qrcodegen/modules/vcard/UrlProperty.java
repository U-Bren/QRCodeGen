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
public class UrlProperty extends PropertyEntry {

	private final VCardURI uri;

	private UrlProperty(Builder builder) throws URISyntaxException {
		super(builder);
		if ((builder.uri == null && builder.url == null)
				|| (builder.uri != null && builder.url != null)) {
			throw new IllegalStateException("Only one of uri and url may be non-null");
		}
		if (builder.uri == null) {
			this.uri = new VCardURI(builder.url);
		} else {
			this.uri = new VCardURI(builder.uri);
		}

	}

	public static final class Builder extends PropertyEntry.Builder2 {

		private final String url;
		private final URI uri;

		/**
		 * Builds a UrlProperty from a string. The string must denote a valid
		 * URL, that is an absolute URI.
		 *
		 * @param url
		 */
		public Builder(String url) {
			this(url, null);
		}

		/**
		 * Builds a UrlProperty from a valid URL, that is an absolute URI.
		 *
		 * @param url
		 */
		public Builder(URI url) {
			this(null, url);
		}

		private Builder(String url, URI uri) {
			super(Property.URL);
			this.uri = uri;
			this.url = url;
		}

		@Override
		public UrlProperty build() {
			try {
				UrlProperty instance = new UrlProperty(this);
				String scheme = instance.uri.getScheme();
				if (scheme == null || scheme.isEmpty()) {
					throw new IllegalArgumentException("Not a valid URL because it has no scheme.");
				}
				return instance;
			} catch (URISyntaxException ex) {
				throw new IllegalArgumentException("Not a valid URL", ex);
			}
		}
	}

	@Override
	VCardURI getValue() {
		return uri;
	}

	@Override
	String getValueAsString() {
		return uri.getValueAsString();
	}
	
	public String getUrlAsString(){
		return uri.getUriAsString();
	}
	
	public URI getUrl(){
		return uri.getUri();
	}
}
