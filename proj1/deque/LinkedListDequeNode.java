package deque;

import java.util.Objects;

/**
 * @description: the node of the LinkedListDeque
 * @author: 杨怀龙
 * @create: 2025-04-24 20:20
 **/
public class LinkedListDequeNode<T> {

    private T value;

    private LinkedListDequeNode<T> next;

    private LinkedListDequeNode<T> front;


    public LinkedListDequeNode(T value) {
        this.value = value;
        this.next = null;
        this.front = null;
    }

    public LinkedListDequeNode(T value, LinkedListDequeNode<T> next, LinkedListDequeNode<T> front) {
        this.value = value;
        this.next = next;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public LinkedListDequeNode<T> getNext() {
        return next;
    }

    public void setNext(LinkedListDequeNode<T> next) {
        this.next = next;
    }

    public LinkedListDequeNode<T> getFront() {
        return front;
    }

    public void setFront(LinkedListDequeNode<T> front) {
        this.front = front;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkedListDequeNode<?> that = (LinkedListDequeNode<?>) o;
        return value.equals(that.value);
    }


}
