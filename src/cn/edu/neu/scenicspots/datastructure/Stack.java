package cn.edu.neu.scenicspots.datastructure;

/**
 * 数据结构 栈 (基于顺序存储实现)
 * @author 李林根
 * @param <T>
 */
public class Stack<T> {
    private T[] data = null;
    private int maxSize=0;   //栈容量
    private int size=0;   //栈大小
    private int top =-1;  //栈顶指针
    private static final int DEFAULT_SIZE = 10; // 默认大小

    public int getSize() {
        return size;
    }

    /**
     * 构造函数：根据给定的size初始化栈
     */
    public Stack(){
        this(DEFAULT_SIZE);
    }

    public Stack(int initSize){
        if(initSize >=0){
            this.maxSize = initSize;
            data = (T[]) new Object[initSize];
            top = -1;
        }else{
            throw new RuntimeException("初始化大小不能小于0：" + initSize);
        }
    }

    //判空
    public boolean empty(){
        return size <= 0;
    }

    public boolean full() { return size >= maxSize; }

    //进栈,第一个元素top=0；
    public boolean push(T t){
        if(top == maxSize -1){
            throw new RuntimeException("栈已满，无法将元素入栈！");
        }else{
            data[++top]= t;
            size++;
            return true;
        }
    }

    /**
     * 查看栈顶元素
     * @return 内容
     */
    public T top(){
        if(top == -1){
            throw new RuntimeException("栈为空！");
        }else{
            return (T)data[top];
        }
    }

    /**
     * 弹出栈顶元素
     * @return
     */
    public T pop(){
        if(top == -1){
            throw new RuntimeException("栈为空！");
        }else{
            size--;
            return (T)data[top--];
        }
    }

    /**
     * 查找对象并返回位置 （第一个元素为1）
     * @param t
     * @return 位置
     */
    public int find(T t){
        int i=top;
        while(top != -1){
            if(!top().equals(t)){
                top --;
            }else{
                break;
            }
        }
        int result = top+1;
        top = i;
        return result;
    }
}