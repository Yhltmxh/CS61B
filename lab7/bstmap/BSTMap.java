package bstmap;

import java.util.*;

/**
 * @description: 二叉搜索树映射集
 * @author: 杨怀龙
 * @create: 2025-06-21 17:26
 **/
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private BSTNode<K, V> root;

    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }

    /**
     * 遍历二叉搜索树，寻找给定key所在节点
     * @param node 当前根节点
     * @param key 给定key
     * @return 找到返回对应节点，否则返回null
     */
    private BSTNode<K, V> findNode(BSTNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        K cur = node.getKey();
        if (cur.equals(key)) {
            return node;
        } else if (key.compareTo(cur) < 0) {
            return findNode(node.getLeftChild(), key);
        } else {
            return findNode(node.getRightChild(), key);
        }
    }


    /**
     * 插入节点
     * @param node 当前根节点
     * @param key 给定key
     * @param value 给定value
     */
    private void insertNode(BSTNode<K, V> node, K key, V value) {
        K cur = node.getKey();
        if (key.equals(cur)) {
            node.setValue(value);
        } else if (key.compareTo(cur) < 0) {
            BSTNode<K, V> leftChild = node.getLeftChild();
            if (leftChild == null) {
                node.setLeftChild(new BSTNode<>(key, value));
                size += 1;
            } else {
                insertNode(leftChild, key, value);
            }
        } else {
            BSTNode<K, V> rightChild = node.getRightChild();
            if (rightChild == null) {
                node.setRightChild(new BSTNode<>(key, value));
                size += 1;
            } else {
                insertNode(rightChild, key, value);
            }
        }
    }


    /**
     * 删除节点
     * @param node 当前根节点
     * @param key 给定key
     * @return 删除操作完成后当前的根节点
     */
    private BSTNode<K, V> removeNode(BSTNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        K cur = node.getKey();
        if (key.equals(cur)) {
            if (node.getLeftChild() == null && node.getRightChild() == null) {
                return null;
            } else if (node.getLeftChild() == null){
                return node.getRightChild();
            } else if (node.getRightChild() == null) {
                return node.getLeftChild();
            } else {
                BSTNode<K, V> t = node.getLeftChild();
                while (t.getRightChild() != null) {
                    t = t.getRightChild();
                }
                node.setKey(t.getKey());
                node.setValue(t.getValue());
                node.setLeftChild(removeNode(node.getLeftChild(), t.getKey()));
            }
        } else if (key.compareTo(cur) < 0) {
            node.setLeftChild(removeNode(node.getLeftChild(), key));
        } else {
            node.setRightChild(removeNode(node.getRightChild(), key));
        }
        return node;
    }


    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return findNode(root, key) != null;
    }

    @Override
    public V get(K key) {
        BSTNode<K, V> node = findNode(root, key);
        if (node == null) {
            return null;
        }
        return node.getValue();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new BSTNode<>(key, value);
            size += 1;
        } else {
            insertNode(root, key, value);
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> res = new HashSet<>();
        Queue<BSTNode<K, V>> q = new ArrayDeque<>();
        q.offer(root);
        while (!q.isEmpty()) {
            BSTNode<K, V> node = q.poll();
            if (node != null) {
                res.add(node.getKey());
                BSTNode<K, V> leftNode = node.getLeftChild();
                BSTNode<K, V> rightNode = node.getRightChild();
                if (leftNode != null) {
                    q.offer(leftNode);
                }
                if (rightNode != null) {
                    q.offer(rightNode);
                }
            }
        }
        return res;
    }

    @Override
    public V remove(K key) {
        BSTNode<K, V> target = findNode(root, key);
        if (target == null) {
            return null;
        }
        root = removeNode(root, key);
        size -= 1;
        return target.getValue();
    }

    @Override
    public V remove(K key, V value) {
        BSTNode<K, V> target = findNode(root, key);
        if (target == null || !target.getValue().equals(value)) {
            return null;
        }
        root = removeNode(root, key);
        size -= 1;
        return target.getValue();
    }

    private class BSTMapIterator implements Iterator<K> {

        int current;

        private final List<K> keyList;

        public BSTMapIterator() {
            this.current = 0;
            this.keyList = new ArrayList<>(keySet());
        }

        @Override
        public boolean hasNext() {
            return current < keyList.size() - 1;
        }

        @Override
        public K next() {
            current += 1;
            return keyList.get(current);
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTMapIterator();
    }
}
