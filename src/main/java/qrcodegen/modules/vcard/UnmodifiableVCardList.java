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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 *
 * @author Stefan Ganzer
 */
class UnmodifiableVCardList<T extends VCardValue,E extends VCardList<T>> implements VCardList<T> {

	private final E l;
	
	static <T extends VCardValue,E extends VCardList<T>> UnmodifiableVCardList<T,E> newInstance(E l){
		return new UnmodifiableVCardList<T,E>(l);
	}
	
	private UnmodifiableVCardList(E l) {
		if(l == null){
			throw new NullPointerException();
		}
		this.l = l;
	}

		@Override
	public int size() {
		return l.size();
	}

	@Override
	public boolean isEmpty() {
		return l.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return l.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return l.iterator();
	}

	@Override
	public Object[] toArray() {
		return l.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return l.toArray(a);
	}

	@Override
	public boolean add(T t) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		return l.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return l.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return l.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return l.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return l.retainAll(c);
	}

	@Override
	public void clear() {
		l.clear();
	}

	@Override
	public T get(int index) {
		return l.get(index);
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		return l.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return l.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return l.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return l.listIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return l.subList(fromIndex, toIndex);
	}

	@Override
	public String getValueAsString() {
		return l.getValueAsString();
	}

	@Override
	public int elements() {
		return l.elements();
	}

	@Override
	public String delimiter() {
		return l.delimiter();
	}

}
