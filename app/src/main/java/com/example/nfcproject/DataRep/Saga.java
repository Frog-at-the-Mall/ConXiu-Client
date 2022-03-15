package com.example.nfcproject.DataRep;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class Saga implements SortedSet {
	TreeSet<Journey> journeys;
	String sagaName;
	int sagaID;

	public Saga(int sagaID, String sagaName, ArrayList<Journey> journeys){
		this.sagaID = sagaID;
		this.sagaName = sagaName;
		this.journeys = new TreeSet<>();
		this.journeys.addAll(journeys);
	}

	/**
	 * Compare two Journey objects
	 */
	private class journeyComparator implements Comparator{
		@Override
		public int compare(Object j1, Object j2) {
			return ((Journey)j1).compareTo(j2);
		}
	}

	@Override
	public Comparator comparator() {
		return new journeyComparator();
	}

	@Override
	public SortedSet subSet(Object o, Object e1) {
		return journeys.subSet((Journey) o, (Journey) e1);
	}

	@Override
	public SortedSet headSet(Object o) {
		return journeys.headSet((Journey) o);
	}

	@Override
	public SortedSet tailSet(Object o) {
		return journeys.tailSet((Journey) o);
	}

	@Override
	public Object first() {
		return journeys.first();
	}

	@Override
	public Object last() {
		return journeys.last();
	}

	@Override
	public int size() {
		return journeys.size();
	}

	@Override
	public boolean isEmpty() {
		return journeys.isEmpty();
	}

	@Override
	public boolean contains(@Nullable Object o) {
		return journeys.contains(o);

	}

	@NonNull
	@Override
	public Iterator iterator() {
		return journeys.iterator();
	}

	@NonNull
	@Override
	public Object[] toArray() {
		return journeys.toArray();
	}

	@NonNull
	@Override
	public Object[] toArray(@NonNull Object[] objects) {
		return journeys.toArray(objects);
	}

	@Override
	public boolean add(Object o) {
		return journeys.add((Journey) o);
	}

	@Override
	public boolean remove(@Nullable Object o) {
		return journeys.remove(o);
	}

	@Override
	public boolean addAll(@NonNull Collection collection) {
		return journeys.addAll(collection);
	}

	@Override
	public void clear() {
		journeys.clear();
	}

	@Override
	public boolean removeAll(@NonNull Collection collection) {
		return journeys.removeAll(collection);
	}

	@Override
	public boolean retainAll(@NonNull Collection collection) {
		return journeys.retainAll(collection);
	}

	@Override
	public boolean containsAll(@NonNull Collection collection) {
		return journeys.containsAll(collection);
	}
}
