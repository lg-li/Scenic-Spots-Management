package cn.edu.neu.scenicspots.datastructure;

public abstract class Heap<T extends Comparable> {
    public int left(int i) {
        return (i + 1) * 2 - 1;
    }
    public int right(int i) {
        return (i + 1) * 2;
    }
    public int parent(int i) {
        // i为根结点
        if (i == 0) {
            return -1;
        }
        return (i - 1) / 2;
    }
    /**
     * @param a          保存堆的数组
     * @param i          堆中需要下降的元素
     * @param heapLength 堆元素个数
     */
    public abstract void heapify(T[] a, int i, int heapLength);
    /**
     * 建堆
     *
     * @param a          数组
     * @param heapLength 堆元素个数
     */

    public void buildHeap(T[] a, int heapLength) {
        // 从后往前看，lengthParent处的元素是第一个有孩子节点的节点
        int lengthParent = parent(heapLength - 1);
        for (int i = lengthParent; i >= 0; i--) {
            heapify(a, i, heapLength);
        }
    }
}
