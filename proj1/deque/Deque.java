package deque;

/**
 * @description: deque interface
 * @author: 杨怀龙
 * @create: 2025-04-24 19:48
 **/
public interface Deque<T> {

     /**
      * Adds an item of type T to the front of the deque.
      * @param item the new element
      */
     void addFirst(T item);


     /**
      * Adds an item of type T to the back of the deque.
      * @param item the new element
      */
     void addLast(T item);


     /**
      * Judge the queue is empty or not.
      * @return true for empty and false for not empty
      */
     default boolean isEmpty(){
          return size() == 0;
     }


     /**
      * Get the size of the deque.
      * @return size
      */
     int size();



     /**
      * Print the content of the deque.
      */
     void printDeque();


     /**
      * Remove the first item.
      * @return the removed item of type T
      */
     T removeFirst();


     /**
      * Remove the last item.
      * @return the removed item of type T
      */
     T removeLast();


     /**
      * Gets the item at the given index.
      * @param index the index of the deque
      * @return the item
      */
     T get(int index);
}
