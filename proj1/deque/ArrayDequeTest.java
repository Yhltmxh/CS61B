package deque;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @description: test for ArrayDeque
 * @author: 杨怀龙
 * @create: 2025-04-25 20:56
 **/
public class ArrayDequeTest {

    @Test
    public void buildTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst(1);
        arrayDeque.addFirst(2);
        arrayDeque.addLast(3);
        arrayDeque.printDeque();
    }

    @Test
    public void addFirstTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 12; i++) {
            arrayDeque.addFirst(i);
        }
        arrayDeque.printDeque();
    }

    @Test
    public void addLastTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 12; i++) {
            arrayDeque.addLast(i);
        }
        arrayDeque.printDeque();
    }

    @Test
    public void addMixTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                arrayDeque.addLast(i);
            } else {
                arrayDeque.addFirst(i);
            }
        }
        arrayDeque.printDeque();
    }

    @Test
    public void removeFirstTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 18; i++) {
            arrayDeque.addFirst(i);
        }
        arrayDeque.printDeque();
        for (int i = 0; i < 11; i++) {
            arrayDeque.removeFirst();
        }
        arrayDeque.printDeque();
    }


    @Test
    public void removeLastTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst(1);
        arrayDeque.addFirst(2);
        arrayDeque.addLast(3);
        arrayDeque.printDeque();
        for (int i = 0; i < 4; i++) {
            arrayDeque.removeLast();
        }
        arrayDeque.printDeque();
    }


    @Test
    public void getTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        arrayDeque.addFirst(1);
        arrayDeque.addFirst(2);
        arrayDeque.addLast(3);
        arrayDeque.printDeque();
        for (int i = 0; i < 4; i++) {
            System.out.println(arrayDeque.get(i));
        }
    }

    @Test
    public void sizeTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        System.out.println(arrayDeque.size());
        for (int i = 0; i < 8; i++) {
            arrayDeque.addLast(i);
        }
        System.out.println(arrayDeque.size());
    }


    @Test
    public void iteratorTest() {
        ArrayDeque<Integer> arrayDeque = new ArrayDeque<>();
        for (int i = 0; i < 18; i++) {
            if (i % 2 == 0) {
                arrayDeque.addLast(i);
            } else {
                arrayDeque.addFirst(i);
            }
        }
        for (Integer i : arrayDeque) {
            System.out.print(i);
            System.out.print(" ");
        }
        System.out.println();
    }

    @Test
    public void equalsTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(1);
        a.addFirst(2);
        a.addLast(3);

        ArrayDeque<Integer> b = new ArrayDeque<>();
        b.addFirst(1);
        b.addFirst(2);
        b.addLast(3);

        assertEquals(a, b);

        ArrayDeque<Integer> c = new ArrayDeque<>();
        c.addFirst(4);
        c.addFirst(2);
        c.addLast(3);

        assertNotEquals(a, c);
    }
}
