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
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Stefan Ganzer
 */
public final class NProperty extends PropertyEntry {

	private final VCardList<VCardComponent> familyNames = VCardValueList.newArrayList();
	private final VCardList<VCardComponent> givenNames = VCardValueList.newArrayList();
	private final VCardList<VCardComponent> additionalNames = VCardValueList.newArrayList();
	private final VCardList<VCardComponent> honorificPrefixes = VCardValueList.newArrayList();
	private final VCardList<VCardComponent> honorificSuffixes = VCardValueList.newArrayList();
	private final VCardList<VCardList<VCardComponent>> value;

	/**
	 * Constructs a new NProperty instance. Access via this class' Builder only.
	 *
	 * @param builder
	 */
	private NProperty(Builder builder) {
		super(builder);
		for (String s : builder.familyNames) {
			if (s == null) {
				throw new NullPointerException();
			}
			familyNames.add(new VCardComponent(s));
		}
		for (String s : builder.additionalNames) {
			if (s == null) {
				throw new NullPointerException();
			}
			additionalNames.add(new VCardComponent(s));
		}
		for (String s : builder.givenNames) {
			if (s == null) {
				throw new NullPointerException();
			}
			givenNames.add(new VCardComponent(s));
		}
		for (String s : builder.honorificPrefixes) {
			if (s == null) {
				throw new NullPointerException();
			}
			honorificPrefixes.add(new VCardComponent(s));
		}
		for (String s : builder.honorificSuffixes) {
			if (s == null) {
				throw new NullPointerException();
			}
			honorificSuffixes.add(new VCardComponent(s));
		}
		VCardList<VCardList<VCardComponent>> valueList = VCardComponentList.newArrayList();
		valueList.add(UnmodifiableVCardList.newInstance(familyNames));
		valueList.add(UnmodifiableVCardList.newInstance(givenNames));
		valueList.add(UnmodifiableVCardList.newInstance(additionalNames));
		valueList.add(UnmodifiableVCardList.newInstance(honorificPrefixes));
		valueList.add(UnmodifiableVCardList.newInstance(honorificSuffixes));
		this.value = UnmodifiableVCardList.newInstance(valueList);
	}

	@Override
	VCardList<VCardList<VCardComponent>> getValue() {
		return value;
	}

	@Override
	String getValueAsString() {
		return value.getValueAsString();
	}

	public static final class Builder extends PropertyEntry.Builder {

		private String altID;
		private final List<String> familyNames = new ArrayList<String>();
		private final List<String> givenNames = new ArrayList<String>();
		private final List<String> additionalNames = new ArrayList<String>();
		private final List<String> honorificPrefixes = new ArrayList<String>();
		private final List<String> honorificSuffixes = new ArrayList<String>();
//		private VCardArrayList<VCardComponent> familyNames = new VCardValueList<VCardComponent>(new ArrayList<VCardComponent>());
//		private VCardArrayList<VCardComponent> givenNames = new VCardValueList<VCardComponent>(new ArrayList<VCardComponent>());
//		private VCardArrayList<VCardComponent> additionalNames = new VCardValueList<VCardComponent>(new ArrayList<VCardComponent>());
//		private VCardArrayList<VCardComponent> honorificPrefixes = new VCardValueList<VCardComponent>(new ArrayList<VCardComponent>());
//		private VCardArrayList<VCardComponent> honorificSuffixes = new VCardValueList<VCardComponent>(new ArrayList<VCardComponent>());

		public static Builder newInstance() {
			return new Builder();
		}

		private Builder() {
			super(Property.N);
		}

		public Builder familyName(String s) {
			familyNames.add(s);
			return this;
		}

		public Builder givenName(String s) {
			givenNames.add(s);
			return this;
		}

		public Builder additionalName(String s) {
			additionalNames.add(s);
			return this;
		}

		public Builder additionalNames(String[] values) {
			for (String s : values) {
				additionalNames.add(s.trim());
			}
			return this;
		}

		public Builder honorificPrefix(String s) {
			honorificPrefixes.add(s);
			return this;
		}

		public Builder honorificPrefixes(String[] values) {
			for (String s : values) {
				honorificPrefixes.add(s.trim());
			}
			return this;
		}

		public Builder honorificSuffix(String s) {
			honorificSuffixes.add(s);
			return this;
		}

		public Builder honorificSuffixes(String[] values) {
			for (String s : values) {
				honorificSuffixes.add(s.trim());
			}
			return this;
		}

		public Builder altID(String value) {
			this.altID = value;
			return this;
		}

		@Override
		String getAltID() {
			return altID;
		}

		@Override
		public NProperty build() {
			return new NProperty(this);
		}
	}
	
	public String getLastName(){
		return VCardTools.originalValuesOfvCardListToString(familyNames);
	}
	
	public String getFirstName(){
		return VCardTools.originalValuesOfvCardListToString(givenNames);
	}
	
	public String getAdditionalNames(){
		return VCardTools.originalValuesOfvCardListToString(additionalNames);
	}
	
	public String getHonorificPrefixes(){
		return VCardTools.originalValuesOfvCardListToString(honorificPrefixes);
	}
	
	public String getHonorificSuffixes(){
		return VCardTools.originalValuesOfvCardListToString(honorificSuffixes);
	}
}
