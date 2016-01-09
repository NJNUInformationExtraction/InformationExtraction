package cn.edu.njnu.tools;

import cn.edu.njnu.Main;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Zhi on 12/27/2015.
 * 用于封装congfig.xml配置文件中的参数并提供访问的接口
 */
public class ParameterHelper implements Iterable<Pair<String, String>> {

    //线程池的最大线程数量
    private int poolsize;

    //抽取信息的根目录
    private String rootFile;

    //抽取判重日志文件
    private String outputFile;

    //抽取地点pid文件
    private String places;

    private String postPlaceURL;

    private String postDataURL;

    //类别目录名及对应的解析类
    private List<Pair<String, String>> list = new ArrayList<>();

    /**
     * default constructor
     */
    public ParameterHelper() {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(new File(
                    Main.class.getResource("/config.xml").getPath()));
            Element root = doc.getRootElement();

            Element extractLog = root.element("output");
            this.outputFile = extractLog.getText().replaceAll("\n", "").trim();

            Element places = root.element("places");
            this.places = places.getText().replaceAll("\n", "").trim();

            Element poolsize = root.element("poolsize");
            this.poolsize = Integer.valueOf(poolsize.getText().replaceAll("\n", "").trim());

            Element rootFile = root.element("source");
            this.rootFile = rootFile.getText().replaceAll("\n", "").trim();

            Element categories = root.element("categories");
            List<Element> nodes = categories.elements();
            nodes.forEach(node -> list.add(new Pair<>(
                    node.attribute("name").getText().replaceAll("\n", "").trim(),
                    node.getText().replaceAll("\n", "").trim())));

            Element interfaces = root.element("interfaces");
            nodes = interfaces.elements();
            this.postPlaceURL = nodes.get(0).getText().replaceAll("\n", "").trim();
            this.postDataURL = nodes.get(1).getText().replaceAll("\n", "").trim();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得线程池的线程数设置
     *
     * @return 线程配置数量
     */
    public int getPoolsize() {
        return poolsize;
    }

    /**
     * 获得抽取信息的根目录
     *
     * @return 根目录的路径
     */
    public String getRootFile() {
        return rootFile;
    }

    /**
     * 抽取判重日志文件
     *
     * @return 抽取判重日志文件
     */
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * 抽取地点pid文件
     *
     * @return 抽取地点pid文件
     */
    public String getPlaces() {
        return places;
    }

    /**
     * @return 地点上传接口
     */
    public String getPostPlaceURL() {
        return postPlaceURL;
    }

    /**
     * @return 数据上传接口
     */
    public String getPostDataURL() {
        return postDataURL;
    }

    /**
     * 得到迭代器
     *
     * @return 内部迭代器
     */
    @Override
    public Iterator<Pair<String, String>> iterator() {
        return new InnerIterator();
    }

    /**
     * 迭代器的内部类实现
     */
    private class InnerIterator implements Iterator<Pair<String, String>> {

        private int cursor = 0;

        @Override
        public boolean hasNext() {
            return cursor < ParameterHelper.this.list.size();
        }

        @Override
        public Pair<String, String> next() {
            if (hasNext())
                return ParameterHelper.this.list.get(cursor++);
            return null;
        }

    }

}
