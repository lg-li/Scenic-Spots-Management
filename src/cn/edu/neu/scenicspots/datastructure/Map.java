package cn.edu.neu.scenicspots.datastructure;

/**
 * 哈希键值表 基于顺序存储+链表冲突处理 实现
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class Map<K, V> {
    private final static int DEFAULT_CONTAINER_SIZE = 100;
    private int containerSize;
    private Entry[] container;// 哈希表数组容器
    private Entry<K, Integer> keySetHead;
    private Entry<K, Integer> keySetTail;

    public Map() {
        this(DEFAULT_CONTAINER_SIZE);
    }

    public Map(int containerSize) {
        container = new Entry[containerSize];
        this.containerSize = containerSize;
        keySetHead = keySetTail = null;
    }

    private int hash(K key) {
        // 哈希函数
        int index = key.hashCode() % containerSize;
        return index < 0 ? -index : index;
    }

    public void put(K key, V value) {
        if (key == null) {
            return;
        }
        int index = hash(key);
        // 维护键表
        if (keySetHead == keySetTail && keySetHead == null) {
            //首次插入
            keySetHead = new Entry<>(key, index);
            keySetTail = keySetHead;
        } else {
            keySetTail.next = new Entry<>(key, index);
            keySetTail = keySetTail.next;
        }
        if (container[index] == null) { // 没有冲突
            container[index] = new Entry<>(key, value);
        } else { // 存在冲突，处理冲突，加在链表之后
            Entry pointer = container[index];
            while (pointer.next != null) {
                if (pointer.key.equals(key)) {
                    // 重名替换上次的元素
                    pointer.value = value;
                    return;
                }
                pointer = pointer.next;
            }
            pointer.next = new Entry<>(key, value);
        }
    }

    public V get(K key) {
        int index = hash(key);
        if (container[index] == null) {
            // 未找到结果
            return null;
        } else {
            // 找到结果，遍历可能存在的冲突
            Entry<K, V> pointer = container[index];
            if (pointer.key.equals(key)) {
                return pointer.value;
            }
            do {
                pointer = pointer.next;
                if (pointer.key.equals(key)) {
                    return pointer.value;
                }
            } while (pointer.next != null);
            // 未找到
            return null;
        }
    }

    protected class Entry<K, V> {
        protected Entry<K, V> next = null;
        private K key;

        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
