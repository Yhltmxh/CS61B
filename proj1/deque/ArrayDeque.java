package deque;

import java.util.Iterator;

/**
 * @description: deque implemented by array
 * @author: 杨怀龙
 * @create: 2025-04-25 11:35
 **/
public class ArrayDeque<T> implements Deque<T>, Iterable<T> {

    private T[] items;

    private int first;

    private int end;

    private int size;


    public ArrayDeque() {
        this.items = (T[]) new Object[8];
        first = 0;
        end = 0;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        items[first] = item;
        first = (first + items.length - 1) % items.length;
        size += 1;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(items.length * 2);
        }
        end = (end + items.length + 1) % items.length;
        items[end] = item;
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[(first + i + 1) % items.length]);
            System.out.print(" ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) return null;
        first = (first + 1) % items.length;
        T item = items[first];
        items[first] = null;
        size -= 1;
        if (items.length > 16 && size * 4 < items.length) {
            resize(items.length / 2);
        }
        return item;
    }

    @Override
    public T removeLast() {
        if (size == 0) return null;
        T item = items[end];
        items[end] = null;
        end = (end + items.length - 1) % items.length;
        size -= 1;
        if (items.length > 16 && size * 4 < items.length) {
            resize(items.length / 2);
        }
        return item;
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return items[(first + 1 + index) % items.length];
    }

    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            newItems[i] = items[(first + i + 1) % items.length];
        }
        first = newItems.length - 1;
        end = size - 1;
        items = newItems;
    }

    private class ArrayDequeIterator implements Iterator<T> {

        private int current;

        ArrayDequeIterator() {
            this.current = first;
        }

        @Override
        public boolean hasNext() {
            return current != end;
        }

        @Override
        public T next() {
            current = (current + 1) % items.length;
            return items[current];
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<?> that = (Deque<?>) o;
        if (that.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(that.get(i))) {
                return false;
            }
        }
        return true;
    }
}
