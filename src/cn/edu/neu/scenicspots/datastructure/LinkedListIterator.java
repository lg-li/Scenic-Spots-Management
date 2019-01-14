package cn.edu.neu.scenicspots.datastructure;

public class LinkedListIterator<T> {
    LinkedListNode<T> first;
    LinkedListNode<T> pointer;

    public LinkedListIterator(LinkedListNode<T> first) {
        this.first = first;
        pointer = null;
    }

    public boolean hasNext() {
        if (first == null) {
            return false;
        } else if (pointer == null) {
            return true;
        }
        return !(pointer.getNext() == null);
    }

    public T next() {
        if (pointer == null) {
            pointer = first;
        } else {
            pointer = pointer.getNext();
        }
        return pointer.getData();
    }
}
