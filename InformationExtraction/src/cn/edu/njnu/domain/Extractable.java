package cn.edu.njnu.domain;

import cn.edu.njnu.tools.Pair;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Zhi on 12/27/2015.
 * 抽取的数据单元的抽象类
 */
public abstract class Extractable {

    //所抽取的数据的集合
    protected ArrayList<Pair<String, String>> data = new ArrayList<>();

    /**
     * 向data里面放入数据对(给出字段和对应值)
     *
     * @param key   字段
     * @param value 字段对应的值
     */
    public void put(String key, String value) {
        this.data.add(new Pair<>(key, value));
    }

    /**
     * 向data里面放入数据对(给出pair结构)
     *
     * @param pair 键值对
     */
    public void put(Pair<String, String> pair) {
        this.data.add(pair);
    }

    /**
     * 用于持久化抽取的数据
     */
    public abstract void persistData(String location) throws IOException;

}
