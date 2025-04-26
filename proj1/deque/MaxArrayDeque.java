package deque;

import java.util.Comparator;

/**
 * @description: the MaxArrayDeque can get the max element
 * @author: 杨怀龙
 * @create: 2025-04-26 14:06
 **/
public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private final Comparator<T> maxComparator;


    public MaxArrayDeque(Comparator<T> c) {
        this.maxComparator = c;
    }

    public T max() {
        T res = this.get(0);
        for (T item : this) {
            if (maxComparator.compare(res, item) < 0) {
                res = item;
            }
        }
        return res;
    }

    public T max(Comparator<T> c) {
        T res = this.get(0);
        for (T item : this) {
            if (c.compare(res, item) < 0) {
                res = item;
            }
        }
        return res;
    }

}
