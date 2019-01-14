package cn.edu.neu.scenicspots.datastructure;

public class Queue<T> {
    // 链栈的节点
    private class QueueNode<T> {
        T data;
        QueueNode<T> next;

        QueueNode(T data, QueueNode<T> next) {
            this.data = data;
            this.next = next;
        }
    }

    private QueueNode<T> front; // 队列头，允许删除
    private QueueNode<T> rear; // 队列尾，允许插入
    private int size;

    public Queue() {
        front = null;
        rear = null;
    }

    public boolean empty(){
        return size==0;
    }

    /**
     * C插入元素到队列
     * @param t 数据内容
     * @return
     */
    public boolean add(T t){
        if(empty()){    //如果队列为空
            front = new QueueNode<>(t,null);//只有一个节点，front、rear都指向该节点
            rear = front;
        }else{
            QueueNode<T> newQueueNode = new QueueNode<T>(t, null);
            rear.next = newQueueNode; //让尾节点的next指向新增的节点
            rear = newQueueNode; //以新节点作为新的尾节点
        }
        size ++;
        return true;
    }

    /**
     * 返回队首元素，但不删除
     * @return 队首元素
     */
    public T peek(){
        if(empty()){
            throw new RuntimeException("队列为空，不可进行此操作。");
        }else{
            return front.data;
        }
    }

    /**
     * 队列滚动
     * @return 队首出队元素
     */
    public T poll(){
        if(empty()){
            throw new RuntimeException("队列为空，不可进行此操作。");
        }else{
            QueueNode<T> value = front; //得到队列头元素
            front = front.next;//让front引用指向原队列头元素的下一个元素
            value.next = null; //释放原队列头元素的next引用
            size --;
            return value.data;
        }
    }

    /**
     * 队列长度
     * @return 长度大小
     */
    public int length(){
        return size;
    }
}