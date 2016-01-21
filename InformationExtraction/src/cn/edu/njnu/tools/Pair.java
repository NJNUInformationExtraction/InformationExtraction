package cn.edu.njnu.tools;

/**
 * Created by Zhi on 12/27/2015. 抽取的一个键值对单元数据
 */
public class Pair<K, V> {

    // 单元数据的键
    public K key;

    // 单元数据的值
    public V value;

    /**
     * 由字段与对应的值作为参数构造实例
     *
     * @param key   键
     * @param value 值
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return key.toString() + ":" + value.toString() + "\n";
    }

}
