package deque;

import java.util.Iterator;

/**
 * @description: deque implemented by linkedList
 * @author: 杨怀龙
 * @create: 2025-04-24 19:44
 **/
public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    private final LinkedListDequeNode<T> head;

    private int size;

    public LinkedListDeque() {
        head = new LinkedListDequeNode<>(null);
        head.setNext(head);
        head.setFront(head);
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        LinkedListDequeNode<T> node = new LinkedListDequeNode<>(item);
        LinkedListDequeNode<T> next = head.getNext();
        node.setNext(next);
        next.setFront(node);
        head.setNext(node);
        node.setFront(head);
        size += 1;
    }

    @Override
    public void addLast(T item) {
        LinkedListDequeNode<T> node = new LinkedListDequeNode<>(item);
        LinkedListDequeNode<T> front = head.getFront();
        front.setNext(node);
        node.setFront(front);
        node.setNext(head);
        head.setFront(node);
        size += 1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (LinkedListDequeNode<T> i = head.getNext(); i != head; i = i.getNext()) {
            System.out.print(i.getValue());
            System.out.print(" ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        LinkedListDequeNode<T> node = head.getNext();
        head.setNext(node.getNext());
        node.getNext().setFront(head);
        size -= 1;
        return node.getValue();
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        LinkedListDequeNode<T> node = head.getFront();
        head.setFront(node.getFront());
        node.getFront().setNext(head);
        size -= 1;
        return node.getValue();
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        LinkedListDequeNode<T> node = head.getNext();
        while (index > 0) {
            node = node.getNext();
            index -= 1;
        }
        return node.getValue();
    }

    public T getRecursive(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return dequeRecurse(head.getNext(), index);
    }

    public T dequeRecurse(LinkedListDequeNode<T> node, int index) {
        if (index == 0) {
            return node.getValue();
        }
        return dequeRecurse(node.getNext(), index - 1);
    }

    private class LinkedListDequeIterator implements Iterator<T> {

        private LinkedListDequeNode<T> current;

        public LinkedListDequeIterator(LinkedListDequeNode<T> head) {
            this.current = head;
        }

        @Override
        public boolean hasNext() {
            return current.getNext() != head;
        }

        @Override
        public T next() {
            T next = current.getNext().getValue();
            current = current.getNext();
            return next;
        }
    }


    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator(head);
    }
}
