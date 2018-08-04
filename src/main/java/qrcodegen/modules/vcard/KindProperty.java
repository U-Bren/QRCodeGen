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
package qrcodegen.modules.vcard;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Stefan Ganzer
 */
public class KindProperty extends PropertyEntry {

	private final Kind kind;

	private enum Kind {

		INDIVIDUAL, GROUP, ORG, LOCATION;
		private static final Map<String, Kind> stringToEnum = new HashMap<String, Kind>(4);

		static {
			for (Kind k : values()) {
				stringToEnum.put(k.getValueAsString(), k);
			}
		}

		/**
		 * Returns the enum constant of this type with the specified name. The
		 * string must match exactly the value {@link #getValueAsString()}
		 * returns. Case matters.
		 *
		 * @param s the enum constant of this type with the specified name
		 *
		 * @return the enum constant of this type with the specified name
		 *
		 * @throws IllegalArgumentException if no enum constant of the specified
		 * name exists
		 * @throws NullPointerException if s is null
		 */
		public static Kind fromString(String s) {
			if (s == null) {
				throw new NullPointerException();
			}
			Kind k = stringToEnum.get(s);
			if (k == null) {
				throw new IllegalArgumentException(s);
			}
			return k;
		}

		public String getValueAsString() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}

	private KindProperty(Builder builder) {
		super(builder);
		this.kind = builder.kind;
		assert this.kind != null;
	}

	public static final class Builder extends Builder2 {

		private Kind kind = Kind.INDIVIDUAL;

		public Builder() {
			super(Property.KIND);
		}

		public Builder individual() {
			this.kind = Kind.INDIVIDUAL;
			return this;
		}

		public Builder group() {
			this.kind = Kind.GROUP;
			return this;
		}

		public Builder org() {
			this.kind = Kind.ORG;
			return this;
		}

		public Builder location() {
			this.kind = Kind.LOCATION;
			return this;
		}

		/**
		 * Sets the kind from the given name. The name must be one of
		 * individual, group, org, or location. The case is ignored.
		 *
		 * @param name
		 *
		 * @return this Builder instance
		 *
		 * @throws IllegalArgumentException if the name is unknown
		 * @throws NullPointerException if name is null
		 */
		public Builder fromName(String name) {
			this.kind = Kind.fromString(name.toLowerCase(Locale.ENGLISH));
			return this;
		}

		@Override
		public KindProperty build() {
			return new KindProperty(this);
		}
	}

	@Override
	VCardValue getValue() {
		assert kind != null;
		return new VCardText(kind.getValueAsString());
	}

	@Override
	String getValueAsString() {
		assert kind != null;
		return kind.getValueAsString();
	}

	/**
	 * Returns the value of this KindProperty.
	 *
	 * The returned value is one of
	 * {@code  individual}, {@code group}, {@code org}, or {@code location}.
	 *
	 * @return the value of this KindProperty
	 */
	public String getKind() {
		return kind.getValueAsString();
	}
}
