package deque;

import org.junit.Test;

import java.util.Comparator;

/**
 * @description:
 * @author: 杨怀龙
 * @create: 2025-04-26 14:16
 **/
public class MaxArrayDequeTest {

    static class IntegerComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    @Test
    public void maxTest() {
        IntegerComparator integerComparator = new IntegerComparator();
        MaxArrayDeque<Integer> arrayDeque = new MaxArrayDeque<>(integerComparator);
        arrayDeque.addFirst(1);
        arrayDeque.addFirst(2);
        arrayDeque.addLast(3);
        System.out.println(arrayDeque.max());
    }
}
