/*
 * Copyright (C) 2012 Stefan Ganzer
 *
 * This file is part of QRCodeGen.
 *
 * QRCodeGen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QRCodeGen is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qrcodegen.modules.vcardgenpanel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import qrcodegen.modules.vcard.FNProperty;
import qrcodegen.modules.vcard.NProperty;
import qrcodegen.modules.vcard.NicknameProperty;
import qrcodegen.modules.vcard.OrgProperty;
import qrcodegen.modules.vcard.PropertyEntry;
import qrcodegen.modules.vcard.VCardTools;
import qrcodegen.modules.vcardgenpanel.VCardPropertyProvider;
import qrcodegen.tools.CollectionTools;
import qrcodegen.tools.StaticTools;

/**
 *
 * @author Stefan Ganzer
 */
public class VCardNameModel extends AbstractModel implements VCardPropertyProvider {

	public static final String FORMATTED_NAME_ELEMENT = "FormattedName"; //NOI18N
	public static final String LAST_NAME_ELEMENT = "LastName"; //NOI18N
	public static final String FIRST_NAME_ELEMENT = "FirstName"; //NOI18N
	public static final String ADDITIONAL_NAMES_ELEMENT = "AdditionalNames"; //NOI18N
	public static final String HONORIFIC_PREFIXES_ELEMENT = "HonorificPrefixes"; //NOI18N
	public static final String HONORIFIC_SUFFIXES_ELEMENT = "HonorificSuffixes"; //NOI18N
	public static final String NICKNAME_ELEMENT = "Nickname"; //NOI18N
	public static final String ORG_ELEMENT = "Org";
	public static final String UNIT_NAMES_ELEMENT = "UnitNames";
	private static final Pattern VALUE_SPLITTER = Pattern.compile(",");
	private static final Pattern COMPONENT_SPLITTER = Pattern.compile(";");
//	private static final Pattern VALUE_SPLITTER = Pattern.compile("(?<!\\\\),");
//	private static final Pattern VALUE_DEESCAPER = Pattern.compile("\\\\(,)");
	private String formattedName = null;
	private String lastName = null;
	private String firstName = null;
	private String additionalNames = null;
	private String honorificPrefixes = null;
	private String honorificSuffixes = null;
	private String nickname = null;
	private String org = null;
	private String unitNames = null;

	public VCardNameModel() {
	}

	/**
	 * Returns the formatted name.
	 *
	 * @return the formatted name. Never returns null.
	 */
	public String getFormattedName() {
		if (!isFormattedNameSet()) {
			throw new IllegalStateException();
		}
		return formattedName;
	}

	/**
	 * Sets the formatted name.
	 *
	 * @param value the new formatted name. Mustn't be null.
	 *
	 * @throws NullPointerException if the value is null
	 */
	public void setFormattedName(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setFormattedNameValue(value);
	}

	public boolean isFormattedNameSet() {
		return formattedName != null;
	}

	public void clearFormattedName() {
		setFormattedNameValue(null);
	}

	private void setFormattedNameValue(String value) {
		String oldValue = this.formattedName;
		this.formattedName = value;
		firePropertyChange(FORMATTED_NAME_ELEMENT, oldValue, value);
	}

	/**
	 * Returns the last name.
	 *
	 * @return the last name. Never returns null.
	 */
	public String getLastName() {
		if (!isLastNameSet()) {
			throw new IllegalStateException();
		}
		return lastName;
	}

	/**
	 * Sets the last name.
	 *
	 * @param value the new last name. Mustn't be null.
	 *
	 * @throws NullPointerException if the value is null
	 */
	public void setLastName(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setLastNameValue(value);
	}

	public boolean isLastNameSet() {
		return lastName != null;
	}

	public void clearLastName() {
		setLastNameValue(null);
	}

	private void setLastNameValue(String value) {
		String oldValue = this.lastName;
		this.lastName = value;
		firePropertyChange(LAST_NAME_ELEMENT, oldValue, value);
	}

	/**
	 * Returns the first name.
	 *
	 * @return the first name. Never returns null.
	 */
	public String getFirstName() {
		if (!isFirstNameSet()) {
			throw new IllegalStateException();
		}
		return firstName;
	}

	/**
	 * Sets the first name.
	 *
	 * @param value the new first name. Mustn't be null.
	 *
	 * @throws NullPointerException if the value is null
	 */
	public void setFirstName(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setFirstNameValue(value);
	}

	public boolean isFirstNameSet() {
		return firstName != null;
	}

	public void clearFirstName() {
		setFirstNameValue(null);
	}

	private void setFirstNameValue(String value) {
		String oldValue = this.firstName;
		this.firstName = value;
		firePropertyChange(FIRST_NAME_ELEMENT, oldValue, value);
	}

	/**
	 * Returns the formatted names.
	 *
	 * @return the additional names. Never returns null.
	 */
	public String getAdditionalNames() {
		if (!isAdditionalNamesSet()) {
			throw new IllegalStateException();
		}
		return additionalNames;
	}

	/**
	 * Sets the additional names.
	 *
	 * @param value the new additional names. Mustn't be null.
	 *
	 * @throws NullPointerException if the value is null
	 */
	public void setAdditionalNames(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setAdditionalNamesValue(value);
	}

	public boolean isAdditionalNamesSet() {
		return additionalNames != null;
	}

	public void clearAdditionalNames() {
		setAdditionalNamesValue(null);
	}

	private void setAdditionalNamesValue(String value) {
		String oldValue = this.additionalNames;
		this.additionalNames = value;
		firePropertyChange(ADDITIONAL_NAMES_ELEMENT, oldValue, value);
	}

	/**
	 * Returns the honorific prefixes.
	 *
	 * @return the honorific prefixes. Never returns null.
	 */
	public String getHonorificPrefixes() {
		if (!isHonorificPrefixesSet()) {
			throw new IllegalStateException();
		}
		return honorificPrefixes;
	}

	/**
	 * Sets the honorific prefixes.
	 *
	 * @param value the new honorific prefixes. Mustn't be null.
	 *
	 * @throws NullPointerException if the value is null
	 */
	public void setHonorificPrefixes(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setHonorificPrefixesValue(value);
	}

	public boolean isHonorificPrefixesSet() {
		return honorificPrefixes != null;
	}

	public void clearHonorificPrefixes() {
		setHonorificPrefixesValue(null);
	}

	private void setHonorificPrefixesValue(String value) {
		String oldValue = this.honorificPrefixes;
		this.honorificPrefixes = value;
		firePropertyChange(HONORIFIC_PREFIXES_ELEMENT, oldValue, value);
	}

	/**
	 * Returns the honorific suffixes.
	 *
	 * @return the honorific suffixes. Never returns null.
	 */
	public String getHonorificSuffixes() {
		if (!isHonorificSuffixesSet()) {
			throw new IllegalStateException();
		}
		return honorificSuffixes;
	}

	/**
	 * Sets the honorific suffixes.
	 *
	 * @param value the new honorific suffixes. Mustn't be null.
	 *
	 * @throws NullPointerException if the value is null
	 */
	public void setHonorificSuffixes(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setHonorificSuffixesValue(value);
	}

	public boolean isHonorificSuffixesSet() {
		return honorificSuffixes != null;
	}

	public void clearHonorificSuffixes() {
		setHonorificSuffixesValue(null);
	}

	private void setHonorificSuffixesValue(String value) {
		String oldValue = this.honorificSuffixes;
		this.honorificSuffixes = value;
		firePropertyChange(HONORIFIC_SUFFIXES_ELEMENT, oldValue, value);
	}

	/**
	 * Returns the nickname.
	 *
	 * @return the nickname. Never returns null.
	 */
	public String getNickname() {
		if (!isNicknameSet()) {
			throw new IllegalStateException();
		}
		return nickname;
	}

	/**
	 * Sets the nickname.
	 *
	 * @param value the new nickname. Mustn't be null.
	 *
	 * @throws NullPointerException if the value is null
	 */
	public void setNickname(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setNicknameValue(value);
	}

	public boolean isNicknameSet() {
		return nickname != null;
	}

	public void clearNickname() {
		setNicknameValue(null);
	}

	private void setNicknameValue(String value) {
		String oldValue = this.nickname;
		this.nickname = value;
		firePropertyChange(NICKNAME_ELEMENT, oldValue, value);
	}

	public void setOrg(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setOrgValue(value);
	}

	private void setOrgValue(String value) {
		String oldValue = this.org;
		this.org = value;
		firePropertyChange(ORG_ELEMENT, oldValue, value);
	}

	public boolean isOrgSet() {
		return this.org != null;
	}

	public String getOrg() {
		if (!isOrgSet()) {
			throw new IllegalStateException();
		}
		return org;
	}

	public void clearOrg() {
		setOrgValue(null);
	}

	public void setUnitNames(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		setUnitNamesValue(value);
	}

	private void setUnitNamesValue(String value) {
		String oldValue = this.unitNames;
		this.unitNames = value;
		firePropertyChange(UNIT_NAMES_ELEMENT, oldValue, value);
	}

	public boolean isUnitNamesSet() {
		return this.unitNames != null;
	}

	public String getUnitNames() {
		if (!isUnitNamesSet()) {
			throw new IllegalStateException();
		}
		return unitNames;
	}

	public void clearUnitNames() {
		setUnitNamesValue(null);
	}

	public boolean isOrgPropertyEmptyOrValid() {
		/*
		 * org + unitnames empty : ok
		 * org + unitnames set : ok
		 * org set + unitnames empty : ok
		 * org empty + unitnames set : invalid
		 */
		return isOrgSet() || !isUnitNamesSet();
	}

	@Override
	public void clear() {
		clearFormattedName();
		clearLastName();
		clearFirstName();
		clearAdditionalNames();
		clearHonorificPrefixes();
		clearHonorificSuffixes();
		clearNickname();
		clearOrg();
		clearUnitNames();
	}

	/**
	 * Returns true if any one of the fields is set, false otherwise.
	 *
	 * @return true if any one of the fields is set, false otherwise
	 */
	public boolean isSet() {
		return isFormattedNameSet()
				|| isNameFieldsSet()
				|| isNicknameSet()
				|| isOrgSet()
				|| isUnitNamesSet();
	}

	/**
	 * Returns true if any one of last name, first name, additional names,
	 * honorific prefixes or honorific suffixes is set, false otherwise.
	 *
	 * @return true if any one of last name, first name, additional names,
	 * honorific prefixes or honorific suffixes is set, false otherwise
	 */
	public boolean isNameFieldsSet() {
		return isLastNameSet()
				|| isFirstNameSet()
				|| isAdditionalNamesSet()
				|| isHonorificPrefixesSet()
				|| isHonorificSuffixesSet();
	}

	@Override
	public List<PropertyEntry> getPropertyEntries() {
		FNProperty fnProperty = getFNProperty();
		NProperty nProperty = getNProperty();
		NicknameProperty nicknameProperty = getNicknameProperty();
		OrgProperty orgProperty = getOrgProperty();

		List<PropertyEntry> result = new ArrayList<PropertyEntry>(3);
		CollectionTools.addIfNonNull(result, fnProperty);
		CollectionTools.addIfNonNull(result, nProperty);
		CollectionTools.addIfNonNull(result, nicknameProperty);
		CollectionTools.addIfNonNull(result, orgProperty);

		return result;
	}

	public void setFromFNPropertyEntries(List<FNProperty> entries) {
		if (entries == null) {
			throw new NullPointerException();
		}
		if (!entries.isEmpty()) {
			setFormattedName(entries.get(0).getFormattedName());
		}
	}

	public void setFromNPropertyEntries(List<NProperty> entries) {
		if (entries == null) {
			throw new NullPointerException();
		}
		if (!entries.isEmpty()) {
			NProperty np = entries.get(0);
			setLastName(np.getLastName());
			setFirstName(np.getFirstName());
			setAdditionalNames(np.getAdditionalNames());
			setHonorificPrefixes(np.getHonorificPrefixes());
			setHonorificSuffixes(np.getHonorificSuffixes());
		}
	}

	public void setFromNicknamePropertyEntries(List<NicknameProperty> entries) {
		if (entries == null) {
			throw new NullPointerException();
		}
		if (!entries.isEmpty()) {
			setNickname(entries.get(0).getNickname());
		}
	}

	public void setFromOrgPropertyEntries(List<OrgProperty> entries) {
		if (entries == null) {
			throw new NullPointerException();
		}

		if (!entries.isEmpty()) {
			OrgProperty p = entries.get(0);
			setOrg(p.getOrganizationName());
			if (p.hasUnitNames()) {
				String result = VCardTools.collectionAsDelimitedString(p.getUnitNames(), ";");
				setUnitNames(result);
			}
		}
	}

	public void setFromPropertyEntries(List<FNProperty> fnProperties, List<NProperty> nProperties, List<NicknameProperty> nicknameProperties, List<OrgProperty> orgProperties) {
		setFromFNPropertyEntries(fnProperties);
		setFromNPropertyEntries(nProperties);
		setFromNicknamePropertyEntries(nicknameProperties);
		setFromOrgPropertyEntries(orgProperties);
	}

	private FNProperty getFNProperty() {
		if (!isFormattedNameSet()) {
			return null;
		}

		return new FNProperty.Builder(getFormattedName()).build();
	}

	private NProperty getNProperty() {
		if (!isNameFieldsSet()) {
			return null;
		}
		NProperty.Builder builder = NProperty.Builder.newInstance();
		if (isLastNameSet()) {
			builder.familyName(getLastName());
		}
		if (isFirstNameSet()) {
			builder.givenName(getFirstName());
		}
		if (isAdditionalNamesSet()) {
//			builder.additionalNames(split(getAdditionalNames()));
			builder.additionalNames(VALUE_SPLITTER.split(getAdditionalNames()));
		}
		if (isHonorificPrefixesSet()) {
//			builder.honorificPrefixes(split(getHonorificPrefixes()));
			builder.honorificPrefixes(VALUE_SPLITTER.split(getHonorificPrefixes()));
		}
		if (isHonorificSuffixesSet()) {
//			builder.honorificSuffixes(split(getHonorificSuffixes()));
			builder.honorificSuffixes(VALUE_SPLITTER.split(getHonorificSuffixes()));
		}
		return builder.build();
	}

	private NicknameProperty getNicknameProperty() {
		if (!isNicknameSet()) {
			return null;
		}

		return new NicknameProperty.Builder().nicknames(VALUE_SPLITTER.split(getNickname())).build();
	}

	private OrgProperty getOrgProperty() {
		if (!isOrgSet()) {
			return null;
		}
		OrgProperty.Builder builder = new OrgProperty.Builder(getOrg());
		if (isUnitNamesSet()) {
//			builder.unitNames(split(getUnitNames()));
			builder.unitNames(COMPONENT_SPLITTER.split(getUnitNames()));
		}
		return builder.build();
	}

//	private String[] split(String input) {
//		String[] s = VALUE_SPLITTER.split(input);
//		for (int i = 0; i < s.length; i++) {
//			s[i] = VALUE_DEESCAPER.matcher(s[i]).replaceAll("$1");
//		}
//		return s;
//	}
}
