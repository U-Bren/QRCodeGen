/*
 * Copyright (C) 2013 Stefan Ganzer
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
package qrcodegen.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Stefan Ganzer
 */
public class CollectionTools {

	private CollectionTools() {
		throw new IllegalStateException("Don't instantiate CollectionTools");
	}

	/**
	 * Returns a deep copy of the given map. The iteration order is guaranteed
	 * to be the same as that of the original map. The same is true for the sets
	 * it contains as values.
	 *
	 * @param <K>
	 * @param <V>
	 * @param m
	 *
	 * @return a deep copy of the given map, or an immutable empty map if the
	 * given map is empty
	 *
	 * @throws NullPointerException if the given map is null
	 */
	public static <K, V> Map<K, Set<V>> deepCopyMapOfSet(Map<K, Set<V>> m) {
		if (m == null) {
			throw new NullPointerException();
		}
		if (m.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<K, Set<V>> result = new LinkedHashMap<K, Set<V>>(m.size());
		for (Map.Entry<K, Set<V>> entry : m.entrySet()) {
			result.put(entry.getKey(), new LinkedHashSet<V>(entry.getValue()));
		}
		return result;
	}

	/**
	 *
	 * @param list the value of list
	 * @param t the value of t
	 */
	public static <T> void addIfNonNull(Collection<T> list, T t) {
		if (list == null) {
			throw new NullPointerException();
		}
		if (t != null) {
			list.add(t);
		}
	}

	/**
	 * Returns a copy of the given list, or an Collections.emptyList() if the
	 * given list is empty.
	 *
	 * @param <T>
	 * @param list
	 *
	 * @return
	 */
	public static <T> List<T> copyList(List<? extends T> list) {
		List<T> result;
		if (list.isEmpty()) {
			result = Collections.emptyList();
		} else {
			result = new ArrayList<T>(list);
		}
		return result;
	}

	public static <K, V> void putIfNonNull(Map<K, V> map, K key, V value) {
		if (map == null) {
			throw new NullPointerException();
		}
		if (key == null) {
			throw new NullPointerException();
		}
		if (value != null) {
			map.put(key, value);
		}
	}
}
