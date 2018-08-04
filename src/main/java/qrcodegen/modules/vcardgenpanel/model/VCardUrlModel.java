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
package qrcodegen.modules.vcardgenpanel.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import qrcodegen.modules.vcard.AdrProperty;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcard.UrlProperty;
import qrcodegen.modules.vcardgenpanel.IllegalInputException;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.modules.vcardgenpanel.VCardPropertyProvider;
import qrcodegen.tools.CollectionTools;
import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardUrlModel extends AbstractModel implements VCardPropertyProvider {

	public static final String URL_ELEMENT = "Url";
	public static final String TYPE_PARAMETER = "TypeParameter";
	private final Set<TypeParameter> types = EnumSet.noneOf(TypeParameter.class);
	private URI url;

	public VCardUrlModel() {
	}

	public void setUrl(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		URI newUrl;
		try {
			newUrl = new URI(value);
		} catch (URISyntaxException use) {
			throw new IllegalArgumentException(use);
		}
		if (!newUrl.isAbsolute()) {
			throw new IllegalArgumentException();
		}
		setUrlValue(newUrl);
	}

	public String getUrl() {
		if (!isUrlSet()) {
			throw new IllegalStateException();
		}
		return url.toString();
	}

	public void clearUrl() {
		setUrlValue(null);
	}

	public boolean isUrlSet() {
		return url != null;
	}

	private void setUrlValue(URI value) {
		URI oldUrl = this.url;
		this.url = value;
		firePropertyChange(URL_ELEMENT, oldUrl == null ? null : oldUrl.toString(), value == null ? null : value.toString());
	}

	/**
	 * Sets the type parameters to those contained in the given set.
	 *
	 * This method removes all type parameters that are not in the given set,
	 * and sets those of the given set instead.
	 *
	 * @param s a set of type parameters
	 *
	 * @throws NullPointerException if the given set is null
	 */
	public void setTypeParameters(Set<TypeParameter> s) {
		if (s == null) {
			throw new NullPointerException();
		}
		EnumSet<TypeParameter> newTypeParameters = EnumSet.copyOf(s);
		if (types.equals(s)) {
			//nothing to do
		} else {
			types.clear();
			types.addAll(newTypeParameters);
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	/**
	 * Adds the given type parameter to this address. This method only fires an
	 * event if the given type has not been a part of this address prior to
	 * invoking this method.
	 *
	 * @param type
	 *
	 * @throws NullPointerException if type is null
	 */
	public void addType(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		boolean changed = types.add(type);
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	/**
	 * Removes the given type parameter from this address, if present. This
	 * method only fires an event if the given type has been a part of this
	 * address.
	 *
	 * @param type
	 *
	 * @throws NullPointerException if type is null
	 */
	public void removeType(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		boolean changed = types.remove(type);
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	/**
	 * Returns the type parameters of this address as set.
	 *
	 * @return a {@literal Set<TypeParameters>}. Returns an empty set of this
	 * address has no type parameters. The caller is free to modify the returned
	 * set without changing this address.
	 */
	public Set<TypeParameter> getTypeParameters() {
		return EnumSet.copyOf(types);
	}

	/**
	 * Clears all type parameters, if present. This doesn't change any other
	 * fields, though.
	 *
	 * @see #clear()
	 * @see #clearFields()
	 */
	public void clearTypes() {
		boolean changed = !types.isEmpty();
		types.clear();
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	public boolean isTypesEmpty() {
		return types.isEmpty();
	}

	@Override
	public void clear() {
		clearUrl();
		clearTypes();
	}

	@Override
	public List<PropertyEntry> getPropertyEntries() {
		List<PropertyEntry> result = new ArrayList<PropertyEntry>(1);
		CollectionTools.addIfNonNull(result, getURLProperty());
		return result;
	}

	private UrlProperty getURLProperty() {
		UrlProperty result;
		if (isUrlSet()) {
			UrlProperty.Builder builder = new UrlProperty.Builder(url);
			builder.types(getTypeParameters());
			result = builder.build();
		} else {
			result = null;
		}
		return result;
	}

	/**
	 * Sets all to the values provided by the given UrlProperty.
	 *
	 * @param properties an UrlProperty. This mustn't be null.
	 *
	 * @throws NullPointerException if property is null
	 */
	public void setFromPropertyEntries(List<UrlProperty> properties) {
		if (properties == null) {
			throw new NullPointerException();
		}

		if (!properties.isEmpty()) {
			UrlProperty u = properties.get(0);
			setUrlValue(u.getUrl());
			setTypeParameters(u.getType());
		}
	}
}
