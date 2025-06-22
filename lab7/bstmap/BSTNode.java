package bstmap;

/**
 * @description: 二叉搜索树节点
 * @author: 杨怀龙
 * @create: 2025-06-21 17:46
 **/
public class BSTNode<K extends Comparable<K>, V> {

    private K key;

    private V value;

    private BSTNode<K, V> leftChild;

    private BSTNode<K, V> rightChild;

    public BSTNode(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public BSTNode<K, V> getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(BSTNode<K, V> leftChild) {
        this.leftChild = leftChild;
    }

    public BSTNode<K, V> getRightChild() {
        return rightChild;
    }

    public void setRightChild(BSTNode<K, V> rightChild) {
        this.rightChild = rightChild;
    }
}
