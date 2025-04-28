package tester;

import static org.junit.Assert.*;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import student.StudentArrayDeque;

/**
 * @description: the test for StudentArrayDeque
 * @author: 杨怀龙
 * @create: 2025-04-28 21:15
 **/
public class TestArrayDequeEC {

    @Test
    public void randomizedTest() {
        StudentArrayDeque<Integer> sd = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> ad = new ArrayDequeSolution<>();
        int N = 100000;
        StringBuilder opString = new StringBuilder();
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 5);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                ad.addLast(randVal);
                sd.addLast(randVal);
                opString.append(String.format("addLast(%d)\n", randVal));
            } else if (operationNumber == 1) {
                int randVal = StdRandom.uniform(0, 100);
                ad.addFirst(randVal);
                sd.addFirst(randVal);
                opString.append(String.format("addFirst(%d)\n", randVal));
            } else if (operationNumber == 2) {
                Integer remove = null;
                if (ad.size() > 0) {
                    remove = ad.removeFirst();
                }
                Integer sRemove = sd.removeFirst();
                opString.append("removeFirst()\n");
                assertEquals(opString.toString(), remove, sRemove);
            } else if (operationNumber == 3) {
                Integer remove = null;
                if (ad.size() > 0) {
                    remove = ad.removeLast();
                }
                Integer sRemove = sd.removeLast();
                opString.append("removeLast()\n");
                assertEquals(opString.toString(), remove, sRemove);
            } else if (operationNumber == 4) {
                int size = ad.size();
                int sSize = sd.size();
                opString.append("size()\n");
                assertEquals(size, sSize);
            }
        }
    }
}
