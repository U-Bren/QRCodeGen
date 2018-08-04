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
package qrcodegen.modules.vcardgenpanel.presentationmodel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumSet;
import java.util.Set;
import qrcodegen.modules.vcard.TypeParameter;
import qrcodegen.modules.vcardgenpanel.InputValidity;
import qrcodegen.modules.vcardgenpanel.model.VCardUrlModel;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardUrlPresentationModel extends AbstractPresentationModel {

	public static final String URL_ELEMENT = VCardUrlModel.URL_ELEMENT;
	public static final String TYPE_PARAMETER = VCardUrlModel.TYPE_PARAMETER;
	private static final String EMPTY_STRING = "";
	private final Set<TypeParameter> types = EnumSet.noneOf(TypeParameter.class);
	private final VCardUrlModel urlModel;
	private InputValidity validity;
	private String url = EMPTY_STRING;

	public VCardUrlPresentationModel(VCardUrlModel urlModel) {
		if (urlModel == null) {
			throw new NullPointerException();
		}
		if (urlModel.isUrlSet()) {
			validity = InputValidity.VALID;
		} else {
			validity = InputValidity.EMPTY;
		}
		this.urlModel = urlModel;
		this.urlModel.addPropertyChangeListener(new ModelListener());
	}

	public void setUrl(String url) {
		if (url == null) {
			throw new NullPointerException();
		}
		setUrlValue(url, InputValidity.UNDEFINED);
	}

	public String getUrl() {
		return url;
	}

	private void setUrlValue(String value, InputValidity iv) {
		assert value != null;
		assert iv != null;

		String oldUrl = this.url;
		this.url = value;
		firePropertyChange(URL_ELEMENT, oldUrl, value);
		setValidity(iv);
	}

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

	public void addTypeParameter(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		boolean changed = types.add(type);
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	public Set<TypeParameter> getTypeParameters() {
		return EnumSet.copyOf(types);
	}

	public void removeTypeParameter(TypeParameter type) {
		if (type == null) {
			throw new NullPointerException();
		}
		boolean changed = types.remove(type);
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	public void clearTypeParameters() {
		boolean changed = !types.isEmpty();
		types.clear();
		if (changed) {
			firePropertyChange(TYPE_PARAMETER, null, null);
		}
	}

	public void setTypeParameterOther() {
		removeTypeParameter(TypeParameter.HOME);
		removeTypeParameter(TypeParameter.WORK);
	}

	public void setTypeParameterHome() {
		addTypeParameter(TypeParameter.HOME);
		removeTypeParameter(TypeParameter.WORK);
	}

	public void setTypeParameterWork() {
		addTypeParameter(TypeParameter.WORK);
		removeTypeParameter(TypeParameter.HOME);
	}

	@Override
	public void update() {
		try {
			if (url.isEmpty()) {
				urlModel.clearUrl();
			} else {
				urlModel.setUrl(url);
			}
		} catch (IllegalArgumentException iae) {
			setValidity(InputValidity.INVALID);
			return;
		}
		urlModel.setTypeParameters(types);
	}

	@Override
	public void clear() {
		urlModel.clear();
	}

	@Override
	public InputValidity getValidity() {
		return validity;
	}

	private void setValidity(InputValidity iv) {
		assert iv != null;

		InputValidity oldValidity = this.validity;
		this.validity = iv;
		firePropertyChange(VALIDITY, oldValidity, iv);
	}

	private class ModelListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			final String propertyName = evt.getPropertyName();
			if (VCardUrlModel.URL_ELEMENT.equals(propertyName)) {
				if (urlModel.isUrlSet()) {
					setUrlValue(urlModel.getUrl(), InputValidity.VALID);
				} else {
					setUrlValue(EMPTY_STRING, InputValidity.EMPTY);
				}
			} else if (VCardUrlModel.TYPE_PARAMETER.equals(propertyName)) {
				setTypeParameters(urlModel.getTypeParameters());
			}
			firePropertyChange(CONTENT_CHANGED, evt.getOldValue(), evt.getNewValue());
		}
	}
}
