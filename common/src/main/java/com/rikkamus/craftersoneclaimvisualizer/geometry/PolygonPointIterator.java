package com.rikkamus.craftersoneclaimvisualizer.geometry;

import java.util.Iterator;
import java.util.SequencedCollection;

class PolygonPointIterator<T> implements Iterator<T> {

    private final SequencedCollection<T> points;
    private final Iterator<T> iterator;
    private T current;
    private T previous;
    private T next;

    public PolygonPointIterator(SequencedCollection<T> points) {
        this.points = points;
        this.iterator = points.iterator();
    }

    @Override
    public boolean hasNext() {
        return this.next != null || this.iterator.hasNext();
    }

    @Override
    public T next() {
        this.previous = this.current;
        this.current = this.next != null ? this.next : this.iterator.next();
        this.next = null;

        return this.current;
    }

    public T peekNextNeighbor() {
        if (this.current == null) throw new IllegalArgumentException("Cannot peek before iterating.");

        if (this.next != null) return this.next;

        if (this.iterator.hasNext()) {
            this.next = this.iterator.next();
            return this.next;
        } else {
            return this.points.getFirst();
        }
    }

    public T peekPreviousNeighbor() {
        if (this.current == null) throw new IllegalArgumentException("Cannot peek before iterating.");
        return this.previous != null ? this.previous : this.points.getLast();
    }

}
