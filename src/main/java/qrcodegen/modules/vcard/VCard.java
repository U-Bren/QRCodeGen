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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Stefan Ganzer
 */
public final class VCard {

	private static final String VCARD_NEWLINE = "\r\n";
	private static final String VCARD_BEGIN_PROPERTY = "BEGIN:VCARD";
	private static final String VCARD_VERSION_PROPERTY = "VERSION:4.0";
	private static final String VCARD_END_PROPERTY = "END:VCARD";
	private final List<AdrProperty> adrProperties;
	private final List<EMailProperty> emailProperties;
	private final List<BDayProperty> bdayProperties;
	private final List<FNProperty> fnProperties;
	private final List<NProperty> nProperties;
	private final List<NicknameProperty> nicknameProperties;
	private final List<OrgProperty> orgProperties;
	private final List<NoteProperty> noteProperties;
	private final List<TelProperty> telProperties;
	private final List<UrlProperty> urlProperties;
	private final List<KindProperty> kindProperties;

	/**
	 * Builder class for constructing new VCard-objects.
	 */
	public static final class Builder {

		private final List<PropertyEntry> entries = new ArrayList<PropertyEntry>();

		/**
		 * Returns a new VCard.Builder object.
		 */
		public Builder() {
		}

		/**
		 * Adds a PropertyEntry to this VCard.Builder.
		 *
		 * @param entry a PropertyEntry to add to this VCard.Builder
		 *
		 * @return this VCard.Builder
		 */
		public Builder propertyEntry(PropertyEntry entry) {
			entries.add(entry);
			return this;
		}

		/**
		 * Adds a collection of PropertyEntries to this VCard.Builder.
		 *
		 * @param collection a collection of PropertyEntries to add to this
		 * VCard.Builder
		 *
		 * @return this VCard.Builder
		 */
		public Builder propertyEntry(Collection<? extends PropertyEntry> collection) {
			entries.addAll(collection);
			return this;
		}

		public VCard build() {
			return new VCard(this);
		}
	}

	private VCard(Builder builder) {
		VCardProperty<AdrProperty> adrVCardProperty = VCardProperty.newInstance(Property.ADR);
		VCardProperty<BDayProperty> bdayVCardProperty = VCardProperty.newInstance(Property.BDAY);
		VCardProperty<EMailProperty> emailVCardProperty = VCardProperty.newInstance(Property.EMAIL);
		VCardProperty<FNProperty> fnVCardProperty = VCardProperty.newInstance(Property.FN);
		VCardProperty<NProperty> nVCardProperty = VCardProperty.newInstance(Property.N);
		VCardProperty<NicknameProperty> nicknameVCardProperty = VCardProperty.newInstance(Property.NICKNAME);
		VCardProperty<OrgProperty> orgVCardProperty = VCardProperty.newInstance(Property.ORG);
		VCardProperty<NoteProperty> noteVCardProperty = VCardProperty.newInstance(Property.NOTE);
		VCardProperty<TelProperty> telVCardProperty = VCardProperty.newInstance(Property.TEL);
		VCardProperty<UrlProperty> urlVCardProperty = VCardProperty.newInstance(Property.URL);
		VCardProperty<KindProperty> kindVCardProperty = VCardProperty.newInstance(Property.KIND);
		for (PropertyEntry pe : builder.entries) {
			if (pe == null) {
				throw new NullPointerException();
			}
			if (pe instanceof FNProperty) {
				fnVCardProperty.addEntry((FNProperty) pe);
			} else if (pe instanceof NProperty) {
				nVCardProperty.addEntry((NProperty) pe);
			} else if (pe instanceof BDayProperty) {
				bdayVCardProperty.addEntry((BDayProperty) pe);
			} else if (pe instanceof AdrProperty) {
				adrVCardProperty.addEntry((AdrProperty) pe);
			} else if (pe instanceof EMailProperty) {
				emailVCardProperty.addEntry((EMailProperty) pe);
			} else if (pe instanceof NicknameProperty) {
				nicknameVCardProperty.addEntry((NicknameProperty) pe);
			} else if (pe instanceof OrgProperty) {
				orgVCardProperty.addEntry((OrgProperty) pe);
			} else if (pe instanceof NoteProperty) {
				noteVCardProperty.addEntry((NoteProperty) pe);
			} else if (pe instanceof TelProperty) {
				telVCardProperty.addEntry((TelProperty) pe);
			} else if (pe instanceof UrlProperty) {
				urlVCardProperty.addEntry((UrlProperty) pe);
			} else if (pe instanceof KindProperty) {
				kindVCardProperty.addEntry((KindProperty) pe);
			} else {
				throw new IllegalArgumentException(pe.toString());
			}
		}
		adrProperties = inUnmodifiableList(adrVCardProperty);
		bdayProperties = inUnmodifiableList(bdayVCardProperty);
		fnProperties = inUnmodifiableList(fnVCardProperty);
		nProperties = inUnmodifiableList(nVCardProperty);
		nicknameProperties = inUnmodifiableList(nicknameVCardProperty);
		orgProperties = inUnmodifiableList(orgVCardProperty);
		noteProperties = inUnmodifiableList(noteVCardProperty);
		urlProperties = inUnmodifiableList(urlVCardProperty);
		emailProperties = inUnmodifiableList(emailVCardProperty);
		telProperties = inUnmodifiableList(telVCardProperty);
		kindProperties = inUnmodifiableList(kindVCardProperty);
	}

	private static <T extends PropertyEntry> List<T> inUnmodifiableList(VCardProperty<T> vcp) {
		if (vcp.isValid()) {
			return Collections.unmodifiableList(vcp.getEntries());
		} else {
			throw new IllegalArgumentException(vcp.asString());
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(1000);
		sb.append(VCARD_BEGIN_PROPERTY).append(VCARD_NEWLINE);
		sb.append(VCARD_VERSION_PROPERTY).append(VCARD_NEWLINE);
		
		append(sb, kindProperties);
		append(sb, fnProperties);
		append(sb, nProperties);
		append(sb, nicknameProperties);
		append(sb, bdayProperties);
		append(sb, adrProperties);
		append(sb, telProperties);
		append(sb, emailProperties);
		append(sb, orgProperties);
		append(sb, noteProperties);
		append(sb, urlProperties);

		sb.append(VCARD_END_PROPERTY).append(VCARD_NEWLINE);
		return sb.toString();
	}

	private static <T extends PropertyEntry> void append(StringBuilder sb, Collection<T> collection) {
		for (T t : collection) {
			sb.append(t.asString());
		}
	}

	/**
	 * Returns this VCard's AdrProperties.
	 *
	 * The caller is free to modify the returned list without affection this
	 * VCard instance.
	 *
	 * @return this VCard's AdrProperties
	 */
	public List<AdrProperty> getAdrProperties() {
		return new ArrayList<AdrProperty>(adrProperties);
	}

	public List<FNProperty> getFNProperties() {
		return new ArrayList<FNProperty>(fnProperties);
	}

	public List<BDayProperty> getBDayProperties() {
		return new ArrayList<BDayProperty>(bdayProperties);
	}

	public List<NProperty> getNProperties() {
		return new ArrayList<NProperty>(nProperties);
	}

	public List<NicknameProperty> getNicknameProperties() {
		return new ArrayList<NicknameProperty>(nicknameProperties);
	}

	public List<TelProperty> getTelProperties() {
		return new ArrayList<TelProperty>(telProperties);
	}

	public List<OrgProperty> getOrgProperties() {
		return new ArrayList<OrgProperty>(orgProperties);
	}

	/**
	 * Returns this VCard's NoteProperties.
	 *
	 * The caller is free to modify the returned list without affection this
	 * VCard instance.
	 *
	 * @return this VCard's NoteProperties
	 */
	public List<NoteProperty> getNoteProperties() {
		return new ArrayList<NoteProperty>(noteProperties);
	}

	/**
	 * Returns this VCard's UrlProperties.
	 *
	 * The caller is free to modify the returned list without affection this
	 * VCard instance.
	 *
	 * @return this VCard's UrlProperties
	 */
	public List<UrlProperty> getUrlProperties() {
		return new ArrayList<UrlProperty>(urlProperties);
	}

	public boolean hasEMailProperties(){
		return !emailProperties.isEmpty();
	}
	
	public List<EMailProperty> getEMailProperties() {
		return new ArrayList<EMailProperty>(emailProperties);
	}

	public boolean hasKindProperties(){
		return !kindProperties.isEmpty();
	}
	
	public List<KindProperty> getKindProperties() {
		return new ArrayList<KindProperty>(kindProperties);
	}
}
