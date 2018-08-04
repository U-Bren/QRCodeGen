/*
Copyright 2011 Stefan Ganzer
 
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
package qrcodegen.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 * A ListModel implementation that supports Generics.
 * @author Stefan Ganzer
 */
public class ConcreteListModel<E> extends AbstractListModel implements Iterable<E>{

	private final List<E> list = new ArrayList<E>();

	public ConcreteListModel() {
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public E getElementAt(int index) {
		return get(index);
	}

	public E get(int index) {
		return list.get(index);
	}

	public boolean add(E element) {
		final int index = list.size();
		final boolean result = list.add(element);
		fireIntervalAdded(this, index, index);
		return result;
	}

	public void add(int index, E element) {
		list.add(index, element);
		fireIntervalAdded(this, index, index);
	}

	public boolean addAll(Collection<? extends E> collection) {
		final int oldLastIndex = list.size() - 1;
		final boolean result = list.addAll(collection);
		fireIntervalAdded(this, oldLastIndex, list.size() - 1);
		return result;
	}

	public boolean addAll(int index, Collection<? extends E> collection) {
		final boolean result = list.addAll(index, collection);
		fireIntervalAdded(this, index, index + collection.size() - 1);
		return result;
	}

	public boolean contains(E element) {
		return list.contains(element);
	}

	public int indexOf(E element) {
		return list.indexOf(element);
	}

	public int lastIndexOf(E element) {
		return list.lastIndexOf(element);
	}

	public E remove(int index) {
		final E e = list.remove(index);
		fireIntervalRemoved(this, index, index);
		return e;
	}

	public boolean remove(E element) {
		final int index = indexOf(element);
		final boolean result = list.remove(element);
		if (index >= 0) {
			fireIntervalRemoved(this, index, index);
		}
		return result;
	}

	public E set(int index, E element) {
		final E e = list.set(index, element);
		fireContentsChanged(this, index, index);
		return e;
	}

	public void clear() {
		final int oldLastIndex = list.size() - 1;
		list.clear();
		fireIntervalRemoved(this, 0, oldLastIndex);
	}

	public void addElement(E element) {
		list.add(element);
	}
	
	@Override
	public Iterator<E> iterator(){
		return list.iterator();
	}
}