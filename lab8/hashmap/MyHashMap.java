package hashmap;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;

    private double loadFactor = 0.75;

    private int size = 0;
    /** Constructors */
    public MyHashMap() {
        buckets = createTable(16);
    }

    public MyHashMap(int initialSize) {
        buckets = createTable(initialSize);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        loadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] c = new Collection[tableSize];
        for (int i = 0; i < tableSize; i ++) {
            c[i] = createBucket();
        }
        return c;
    }


    /**
     * 哈希表扩容
     */
    private void expandTable() {
        Collection<Node>[] newTable = createTable(buckets.length * 2);
        for (Collection<Node> bucket : buckets) {
            for (Node n : bucket) {
                int index = Math.floorMod(n.key.hashCode(), newTable.length);
                newTable[index].add(n);
            }
        }
        buckets = newTable;
    }


    @Override
    public void clear() {
        for (Collection<Node> bucket : buckets) {
            bucket.clear();
        }
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node n : buckets[index]) {
            if (n.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node n : buckets[index]) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        for (Node n : buckets[index]) {
            if (n.key.equals(key)) {
                n.value = value;
                return;
            }
        }
        size += 1;
        if ((double)size / buckets.length > loadFactor) {
            expandTable();
            // 索引更新
            index = Math.floorMod(key.hashCode(), buckets.length);
        }
        buckets[index].add(createNode(key, value));
    }

    @Override
    public Set<K> keySet() {
        Set<K> res = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            res.addAll(bucket.stream().map(n -> n.key).collect(Collectors.toList()));
        }
        return res;
    }

    @Override
    public V remove(K key) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Node target = null;
        for (Node n : buckets[index]) {
            if (n.key.equals(key)) {
                target = n;
                break;
            }
        }
        if (target != null) {
            buckets[index].remove(target);
            size -= 1;
            return target.value;
        }
        return null;
    }

    @Override
    public V remove(K key, V value) {
        int index = Math.floorMod(key.hashCode(), buckets.length);
        Node target = null;
        for (Node n : buckets[index]) {
            if (n.key.equals(key)) {
                if (n.value.equals(value)){
                    target = n;
                }
                break;
            }
        }
        if (target != null) {
            buckets[index].remove(target);
            size -= 1;
            return target.value;
        }
        return null;
    }

    private class MyHashMapIterator implements Iterator<K> {

        private final List<K> nodes = new ArrayList<>();

        private int current = 0;

        public MyHashMapIterator() {
            nodes.addAll(keySet());
        }

        @Override
        public boolean hasNext() {
            return current < nodes.size();
        }

        @Override
        public K next() {
            return nodes.get(current ++);
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }
}
