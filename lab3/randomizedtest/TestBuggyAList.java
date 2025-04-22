package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> aList = new AListNoResizing<>();
        BuggyAList<Integer> buggyAList = new BuggyAList<>();
        for (int i = 1; i <= 3; i ++) {
            aList.addLast(i);
            buggyAList.addLast(i);
        }
        for (int i = 1; i <= 3; i ++) {
            assertEquals(aList.removeLast(), buggyAList.removeLast());
        }
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> bL = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                bL.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int bSize = bL.size();
                assertEquals(size, bSize);
            } else if (operationNumber == 2 && L.size() > 0) {
                int last = L.getLast();
                int bLast = bL.getLast();
                assertEquals(last, bLast);
            } else if (operationNumber == 3 && L.size() > 0) {
                int remove = L.removeLast();
                int bRemove = bL.removeLast();
                assertEquals(remove, bRemove);
            }
        }
    }

}
