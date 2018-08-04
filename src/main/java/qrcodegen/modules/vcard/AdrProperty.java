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
 *
 * @author Stefan Ganzer
 */
public final class AdrProperty extends PropertyEntry {

	private final VCardList<VCardComponent> poBox = VCardValueList.newArrayList();
	private final VCardList<VCardComponent> extAddress = VCardValueList.newArrayList();
	private final VCardList<VCardComponent> street = VCardValueList.newArrayList();
	private final VCardList<VCardComponent> locality = VCardValueList.newArrayList();
	private final VCardList<VCardComponent> region = VCardValueList.newArrayList();
	private final VCardList<VCardComponent> code = VCardValueList.newArrayList();
	private final VCardList<VCardComponent> country = VCardValueList.newArrayList();
	private final VCardList<VCardList<VCardComponent>> value;

	private AdrProperty(Builder builder) {
		super(builder);
		for (String s : builder.poBox) {
			if (s == null) {
				throw new NullPointerException();
			}
			poBox.add(new VCardComponent(s));
		}
		for (String s : builder.extAddress) {
			if (s == null) {
				throw new NullPointerException();
			}
			extAddress.add(new VCardComponent(s));
		}
		for (String s : builder.street) {
			if (s == null) {
				throw new NullPointerException();
			}
			street.add(new VCardComponent(s));
		}
		for (String s : builder.locality) {
			if (s == null) {
				throw new NullPointerException();
			}
			locality.add(new VCardComponent(s));
		}
		for (String s : builder.region) {
			if (s == null) {
				throw new NullPointerException();
			}
			region.add(new VCardComponent(s));
		}
		for (String s : builder.code) {
			if (s == null) {
				throw new NullPointerException();
			}
			code.add(new VCardComponent(s));
		}
		for (String s : builder.country) {
			if (s == null) {
				throw new NullPointerException();
			}
			country.add(new VCardComponent(s));
		}
		VCardList<VCardList<VCardComponent>> valueList = VCardComponentList.newArrayList();
		valueList.add(UnmodifiableVCardList.newInstance(poBox));
		valueList.add(UnmodifiableVCardList.newInstance(extAddress));
		valueList.add(UnmodifiableVCardList.newInstance(street));
		valueList.add(UnmodifiableVCardList.newInstance(locality));
		valueList.add(UnmodifiableVCardList.newInstance(region));
		valueList.add(UnmodifiableVCardList.newInstance(code));
		valueList.add(UnmodifiableVCardList.newInstance(country));
		value = UnmodifiableVCardList.newInstance(valueList);

	}
	//<editor-fold defaultstate="collapsed" desc="Definition of ADR">
	/*
	 * 6.3.1. ADR
	 *
	 * Purpose: To specify the components of the delivery address for the vCard
	 * object.
	 *
	 * Value type: A single structured text value, separated by the SEMICOLON
	 * character (U+003B).
	 *
	 * Cardinality: *
	 *
	 * Special notes: The structured type value consists of a sequence of
	 * address components. The component values MUST be specified in their
	 * corresponding position. The structured type value corresponds, in
	 * sequence, to the post office box; the extended address (e.g., apartment
	 * or suite number); the street address; the locality (e.g., city); the
	 * region (e.g., state or province); the postal code; the country name (full
	 * name in the language specified in Section 5.1).
	 *
	 * When a component value is missing, the associated component separator
	 * MUST still be specified.
	 *
	 * Experience with vCard 3 has shown that the first two components (post
	 * office box and extended address) are plagued with many interoperability
	 * issues. To ensure maximal interoperability, their values SHOULD be empty.
	 *
	 * The text components are separated by the SEMICOLON character (U+003B).
	 * Where it makes semantic sense, individual text components can include
	 * multiple text values (e.g., a "street" component with multiple lines)
	 * separated by the COMMA character (U+002C).
	 *
	 * The property can include the "PREF" parameter to indicate the preferred
	 * delivery address when more than one address is specified.
	 *
	 * The GEO and TZ parameters MAY be used with this property.
	 *
	 * The property can also include a "LABEL" parameter to present a delivery
	 * address label for the address. Its value is a plain-text string
	 * representing the formatted address. Newlines are encoded as \n, as they
	 * are for property values.
	 *
	 * ABNF:
	 *
	 * label-param = "LABEL=" param-value
	 *
	 * ADR-param = "VALUE=text" / label-param / language-param / geo-parameter /
	 * tz-parameter / altid-param / pid-param / pref-param / type-param /
	 * any-param
	 *
	 * ADR-value = ADR-component-pobox ";" ADR-component-ext ";"
	 * ADR-component-street ";" ADR-component-locality ";" ADR-component-region
	 * ";" ADR-component-code ";" ADR-component-country ADR-component-pobox =
	 * list-component ADR-component-ext = list-component ADR-component-street =
	 * list-component ADR-component-locality = list-component
	 * ADR-component-region = list-component ADR-component-code = list-component
	 * ADR-component-country = list-component
	 *
	 * Example: In this example, the post office box and the extended address
	 * are absent.
	 *
	 * ADR;GEO="geo:12.3457,78.910";LABEL="Mr. John Q. Public, Esq.\n Mail Drop:
	 * TNE QB\n123 Main Street\nAny Town, CA 91921-1234\n U.S.A.":;;123 Main
	 * Street;Any Town;CA;91921-1234;U.S.A.
	 *
	 */
	//</editor-fold>

	public static final class Builder extends Builder2 {

		private final List<String> poBox = new ArrayList<String>();
		private final List<String> extAddress = new ArrayList<String>();
		private final List<String> street = new ArrayList<String>();
		private final List<String> locality = new ArrayList<String>();
		private final List<String> region = new ArrayList<String>();
		private final List<String> code = new ArrayList<String>();
		private final List<String> country = new ArrayList<String>();
		private String label;

		public static Builder newInstance() {
			return new Builder();
		}

		public Builder() {
			super(Property.ADR);
		}

		public Builder poBox(String poBox) {
			this.poBox.add(poBox);
			return this;
		}

		public Builder poBox(String[] poBox) {
			for (String s : poBox) {
				this.poBox.add(s.trim());
			}
			return this;
		}

		public Builder extAddress(String ext) {
			this.extAddress.add(ext);
			return this;
		}

		public Builder extAddress(String[] ext) {
			for (String s : ext) {
				this.extAddress.add(s.trim());
			}
			return this;
		}

		/**
		 * Sets the street of this builder.
		 *
		 * @param street
		 *
		 * @return
		 */
		public Builder street(String street) {
			this.street.add(street);
			return this;
		}

		/**
		 * Sets the street of this builder.
		 *
		 * @param street
		 *
		 * @return
		 */
		public Builder street(String[] street) {
			for (String s : street) {
				this.street.add(s.trim());
			}
			return this;
		}

		/**
		 * Sets the locality, e.g. the city, of this builder.
		 *
		 * @param locality
		 *
		 * @return
		 */
		public Builder locality(String locality) {
			this.locality.add(locality);
			return this;
		}

		/**
		 * Sets the locality, e.g. the city, of this builder.
		 *
		 * @param locality
		 *
		 * @return
		 */
		public Builder locality(String[] locality) {
			for (String s : locality) {
				this.locality.add(s.trim());
			}
			return this;
		}

		/**
		 * Sets the region, e.g. the state or province, of this builder.
		 *
		 * @param region
		 *
		 * @return
		 */
		public Builder region(String region) {
			this.region.add(region);
			return this;
		}

		/**
		 * Sets the region, e.g. the state or province, of this builder.
		 *
		 * @param region
		 *
		 * @return
		 */
		public Builder region(String[] region) {
			for (String s : region) {
				this.region.add(s.trim());
			}
			return this;
		}

		/**
		 * Sets the postal code.
		 *
		 * @param code
		 *
		 * @return
		 */
		public Builder code(String code) {
			this.code.add(code);
			return this;
		}

		/**
		 * Sets the postal code.
		 *
		 * @param code
		 *
		 * @return
		 */
		public Builder code(String[] code) {
			for (String s : code) {
				this.code.add(s.trim());
			}
			return this;
		}

		public Builder country(String country) {
			this.country.add(country);
			return this;
		}

		public Builder country(String[] country) {
			for (String s : country) {
				this.country.add(s.trim());
			}
			return this;
		}

		public Builder label(String label) {
			this.label = label;
			return this;
		}

		@Override
		String getLabel() {
			return label;
		}

		@Override
		public AdrProperty build() {
			return new AdrProperty(this);
		}
	}

	@Override
	VCardValue getValue() {
		return value;
	}

	@Override
	String getValueAsString() {
		return value.getValueAsString();
	}

	public String getPoBox() {
		return VCardTools.originalValuesOfvCardListToString(poBox);
	}

	public String getExtendedAddress() {
		return VCardTools.originalValuesOfvCardListToString(extAddress);
	}

	public String getStreet() {
		return VCardTools.originalValuesOfvCardListToString(street);
	}

	public String getLocality() {
		return VCardTools.originalValuesOfvCardListToString(locality);
	}

	public String getRegion() {
		return VCardTools.originalValuesOfvCardListToString(region);
	}

	public String getPostalCode() {
		return VCardTools.originalValuesOfvCardListToString(code);
	}

	public String getCountryName() {
		return VCardTools.originalValuesOfvCardListToString(country);
	}
}
