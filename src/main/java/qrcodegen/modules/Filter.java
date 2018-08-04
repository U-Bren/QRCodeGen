/*
 Copyright 2011, 2012 Stefan Ganzer

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
/*
 * $Revision$
 */
package qrcodegen.modules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Stefan Ganzer
 */
enum Filter {
	
	PHOTO ("Photo"){

		@Override
		String apply(CharSequence s) {
			return applyFilter(s, PHOTO_PATTERN);
		}

		@Override
		boolean apply(StringBuilder sb) {
			return applyFilter(sb, PHOTO_PATTERN);
		}
	}, X_MS_CARDPICTURE ("X-MS-Cardpicture"){

		@Override
		String apply(CharSequence s) {
			return applyFilter(s, MS_CARDPICTURE_PATTERN);
		}

		@Override
		boolean apply(StringBuilder sb) {
			return applyFilter(sb, MS_CARDPICTURE_PATTERN);
		}
	}, X_MS_OL_DESIGN ("X-MS-OL-Design"){

		@Override
		String apply(CharSequence s) {
			return applyFilter(s, MS_OL_DESIGN_PATTERN);
		}

		@Override
		boolean apply(StringBuilder sb) {
			return applyFilter(sb, MS_OL_DESIGN_PATTERN);
		}
	}, X_MS_OL_EXTENSIONS ("X-MS-OL-Extension"){

		@Override
		String apply(CharSequence s) {
			return applyFilter(s, MS_OL_EXTENSION_PATTERN);
		}

		@Override
		boolean apply(StringBuilder sb) {
			return applyFilter(sb, MS_OL_EXTENSION_PATTERN);
		}
	}, EMPTY_LINES ("Empty lines"){

		@Override
		String apply(CharSequence s) {
			return applyFilter(s, EMPTY_LINE_PATTERN);
		}

		@Override
		boolean apply(StringBuilder sb) {
			return applyFilter(sb, EMPTY_LINE_PATTERN);
		}
	};

	private Filter(String name){
		this.name = name;
	}
	
	private final String name;

	abstract String apply(CharSequence input);

	abstract boolean apply(StringBuilder sb);

	private static String applyFilter(CharSequence s, Pattern p) {
		if (s == null) {
			throw new NullPointerException();
		}
		// implicit null-check for p
		Matcher m = p.matcher(s);
		return m.replaceAll(EMPTY_STRING);
	}

	/**
	 * Inspired by 8.4.5.1
	 *
	 * @param sb
	 * @param p
	 */
	private static boolean applyFilter(StringBuilder sb, Pattern p) {
		if(sb == null){
			throw new NullPointerException();
		}
		// implicit null-check for p
		Matcher m = p.matcher(sb);
		int index = 0;
		boolean gotMatch = false;
		while (m.find(index)) {
			gotMatch = true;
			sb.delete(m.start(), m.end());
			index = m.start();
		}
		return gotMatch;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	private static final String EMPTY_STRING = "";
	/** Matches an inlined photo. Tested with BASE64 encoding only. */
	private static final Pattern PHOTO_PATTERN = Pattern.compile("^PHOTO;(ENCODING=[\\w]+)?[\\w;=]+(ENCODING=[\\w]+)?:[a-zA-Z0-9+/\\r\\n =]*(?=^[-\\w]*[:;])", Pattern.MULTILINE);
	/**
	 * Matches an inlined card pictures, i.e. a picture of the whole VCard
	 * itself, produced by MS Outlook. Tested with BASE64 encoding only.
	 */
	private static final Pattern MS_CARDPICTURE_PATTERN = Pattern.compile("^X-MS-CARDPICTURE;(ENCODING=[\\w]+)?[\\w;=]+(ENCODING=[\\w]+)?:[a-zA-Z0-9+/\\r\\n =]*(?=^[-\\w]*[:;])", Pattern.MULTILINE);
	/** Matches design information specific to MS Outlook. */
	private static final Pattern MS_OL_DESIGN_PATTERN = Pattern.compile("^X-MS-OL-DESIGN.*[\\r\\n]+", Pattern.MULTILINE);
	/** Matches all MS Outlook specific data. */
	private static final Pattern MS_OL_EXTENSION_PATTERN = Pattern.compile("^X-MS-OL-.*[\\r\\n]+", Pattern.MULTILINE);
	/** Matches empty lines. */
	private static final Pattern EMPTY_LINE_PATTERN = Pattern.compile("^\\s*[\\r\\n]+", Pattern.MULTILINE);
}
