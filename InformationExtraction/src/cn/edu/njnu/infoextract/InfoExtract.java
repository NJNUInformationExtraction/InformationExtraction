package cn.edu.njnu.infoextract;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.tidypage.PreProcess;
import cn.edu.njnu.tidypage.TidyPage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangzhi on 15-12-19.
 * 信息抽取抽象类
 */
public abstract class InfoExtract {

    /**
     * 所处理的DOM根节点
     */
    protected Element root;
    /**
     * 从<title></title>标签抽取出来的该页面的题目(主题)
     */
    protected String theme;

    /**
     * 规范化数据结构,一个三元组,描述了一个子树的结构(包括其父节点的hashcode)
     */
    protected class InnerTuple {

        //标签名
        public String tag;

        //父亲结点的hashcode
        public int parent;

        //标签对应的内容
        public String content;

        //构造器
        public InnerTuple(String tag, int parent, String content) {
            this.tag = tag;
            this.parent = parent;
            this.content = content;
        }

    }

    /**
     * <K,V>: K:标签名;     V:标签里的内容
     */
    protected List<HashSet<InnerTuple>> dataList = new ArrayList<>();

    /**
     * default constructor
     */
    public InfoExtract() {
    }

    /**
     * 由html页面获取DOM树并赋值给this.root(带有寻找目标div标签的预处理逻辑)
     *
     * @param html 页面流
     * @param pre  寻找目标div标签的预处理逻辑
     */
    protected void getDOM(String html, PreProcess pre) {
        TidyPage tp = new TidyPage(html, pre);
        this.root = tp.tidyPage();
    }

    /**
     * 由html页面获取DOM树并赋值给this.root
     *
     * @param html 页面流
     */
    protected void getDOM(String html) {
        TidyPage tp = new TidyPage(html);
        this.root = tp.tidyPage();
    }

    /**
     * 将子树的结构信息与内容打包至HashSet<InnerTuple>中
     *
     * @param set  盛放数据的容器
     * @param node 待拓展的结点
     */
    protected void packDataToTuple(HashSet<InnerTuple> set, Element node) {
        if (!node.children().isEmpty()) {
            for (Element child : node.children()) {
                set.add(new InnerTuple(child.tagName(), node.hashCode(), child.ownText()));
                packDataToTuple(set, child);
            }
        }
    }

    /**
     * 从DOM树里提取可能的信息组放入dataList容器里,同时抽取<title></title>中的主题
     */
    protected void parseHtmlDOM() {
        Element title = root.select("title").first();
        if (title != null)
            theme = title.ownText();

        //优先根据<h*></h*>大标题来定位信息
        for (int i = 1; i < 7; i++) {
            for (Element head : root.select("h" + i)) {
                if (!dataList.get(dataList.size() - 1).contains(new InnerTuple
                        (head.tagName(), head.parent().hashCode(), head.ownText()))) {
                    HashSet<InnerTuple> set = new HashSet<>();
                    for (Element sibling : head.parent().children()) {
                        set.add(new InnerTuple
                                (head.tagName(), head.parent().hashCode(), head.ownText()));
                        packDataToTuple(set, sibling);
                    }
                    dataList.add(set);
                }
            }
        }

        //如果没有<h*></h*>标签,则根据<p>标签来定位信息
        if (dataList.isEmpty() && !root.select("p").isEmpty()) {
            for (Element p : root.select("p")) {
                if (!dataList.get(dataList.size() - 1).contains(new InnerTuple
                        (p.tagName(), p.parent().hashCode(), p.ownText()))) {
                    HashSet<InnerTuple> set = new HashSet<>();
                    for (Element sibling : p.parent().children()) {
                        set.add(new InnerTuple
                                (p.tagName(), p.parent().hashCode(), p.ownText()));
                        packDataToTuple(set, sibling);
                    }
                    dataList.add(set);
                }
            }
        }

        //如果没有<p>标签，则根据<div>标签来定位
        if (dataList.isEmpty() && !root.select("div").isEmpty()) {
            for (Element div : root.select("div")) {
                if (!dataList.get(dataList.size() - 1).contains(new InnerTuple
                        (div.tagName(), div.parent().hashCode(), div.ownText()))) {
                    HashSet<InnerTuple> set = new HashSet<>();
                    for (Element sibling : div.parent().children()) {
                        set.add(new InnerTuple
                                (div.tagName(), div.parent().hashCode(), div.ownText()));
                        packDataToTuple(set, sibling);
                    }
                    dataList.add(set);
                }
            }
            root.select("div").stream().
                    filter(element -> !dataList.get(dataList.size() - 1).contains(new InnerTuple
                            (element.tagName(), element.parent().hashCode(), element.ownText())));
        }

        //删除没有content内容的标签
        for (HashSet<InnerTuple> set : dataList) {
            set.stream().filter((innerTuple) -> {
                return innerTuple.content.equals("");
            }).forEach(set::remove);
        }
    }

    /**
     * 判断一个字段项是否是时间
     *
     * @param item 待判断的字段
     * @return 是否是时间
     */
    protected boolean canBeTime(String item) {
        Pattern p = Pattern.compile("(.*\\d{1,4}[-|\\/|年|\\.]\\d{1,2}" +
                "[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])" +
                "?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?).*");
        Matcher matcher = p.matcher(item);
        if (matcher.find())
            return true;
        p = Pattern.compile("\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号]).*");
        matcher = p.matcher(item);
        if (matcher.find())
            return true;
        p = Pattern.compile("(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}" +
                "(秒)?)?)?)?(\\s)*(PM|AM)?");
        matcher = p.matcher(item);
        return matcher.find();
    }

    /**
     * 从给定的request地址发出请求并返回response字符串
     *
     * @param request 请求的地址
     * @return 返回response字符串
     */
    protected String getResponseFromRequest(String request) throws IOException {
        HttpGet get = new HttpGet(request);
        try {
            //使用apache的开源网络工具箱请求语料库数据
            HttpClient http = new DefaultHttpClient();
            HttpResponse response = http.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                InputStream in = entity.getContent();
                byte[] buffer = new byte[4096];
                int hasread;
                String data;
                if ((hasread = in.read(buffer)) > 0) {
                    data = new String(buffer, 0, hasread);
                    return data;
                } else
                    return null;
            }
            return null;
        } finally {
            get.releaseConnection();
        }
    }

    /**
     * 从语料库返回的json字符串中提取可能的地址或地名
     *
     * @param json 包含可能地址或地名的json字符串
     * @param type 地址或地名,地址为true,地名为false
     * @return 抽取的可能的地址或地名的字符串
     */
    protected String[] getJsonInfo(String json, boolean type) {
        JSONObject jsonObj = JSONObject.fromObject(json);
        JSONArray array = jsonObj.getJSONArray("results");
        String[] list = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            if (type)
                list[i] = item.getString("address");
            else
                list[i] = item.getString("name");
        }
        return list;
    }

    /**
     * 判断待查询的字符串是否与语料库返回的结果相一致
     *
     * @param item   待查询的字符串
     * @param result 语料库返回的结果
     * @return 是否可能是地址
     */
    protected boolean isSimilar(String item, String result) {
        int[][] dp = new int[item.length()][result.length()];
        dp[0][0] = item.charAt(0) == result.charAt(0) ? 1 : 0;
        int i;
        for (i = 1; i < item.length(); ++i) {
            dp[i][0] = Math.max(dp[i - 1][0], item.charAt(i) == result.charAt(0) ? 1 : 0);
        }

        for (i = 1; i < result.length(); ++i) {
            dp[0][i] = Math.max(dp[0][i - 1], item.charAt(0) == result.charAt(i) ? 1 : 0);
        }

        for (i = 1; i < item.length(); ++i) {
            for (int j = 1; j < result.length(); ++j) {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                if (item.charAt(i) == result.charAt(j)) {
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - 1] + 1);
                }
            }
        }
        return dp[item.length() - 1][result.length() - 1] > 4;
    }

    /**
     * 判断一个字段项是否是地点
     *
     * @param item 待判断的字段
     * @param city 查找的城市(必须给出此字段,否则该方法达不到预期效果)
     * @return 是否是一个地点
     */
    protected boolean canBePlace(String item, String city) throws IOException {
        final String appkey = "dnHbgky1GB0HMRt7GReO0Sxp";
        String request = "http://api.map.baidu.com/place/v2/search?" +
                "q=" + item + "&region=" + city + "&output=json&ak=" + appkey;
        String response = getResponseFromRequest(request);
        String[] places;
        String[] addresses;
        if (response != null) {
            addresses = getJsonInfo(response, true);
            places = getJsonInfo(response, false);
            for (int i = 0; i < places.length; i++) {
                if (isSimilar(item, places[i]))
                    return true;
                if (isSimilar(item, addresses[i]))
                    return true;
            }
        }
        return false;
    }

    /**
     * 从dataList中提取出结构化信息
     */
    public abstract List<Extractable> extractInformation(String html);

}
