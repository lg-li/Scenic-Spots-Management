package cn.edu.neu.scenicspots.datastructure;

public class LinkedList<T> {
    private LinkedListNode<T> head = null;

    public LinkedList() {
        head = null;
    }

    public LinkedList(T data) {
        head = null;
        insert(data);
    }

    public void insert(T toInsert) {
        if (head == null) {
            // 链表为空 插入第一个值
            head = new LinkedListNode<>(toInsert);
            return;
        }
        LinkedListNode<T> pointer = head;
        while (pointer.getNext() != null) {
            pointer = pointer.getNext();
        }
        // 插入值到链表尾部
        pointer.setNext(new LinkedListNode<T>(toInsert));
    }

    public LinkedListNode<T> getHeadNode() {
        return head;
    }

    public LinkedListIterator<T> iterator() {
        return new LinkedListIterator<T>(head);
    }
}
