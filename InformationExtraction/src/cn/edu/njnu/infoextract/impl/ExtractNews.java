package cn.edu.njnu.infoextract.impl;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.domain.ext.News;
import cn.edu.njnu.infoextract.InfoExtract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

import java.util.*;

/**
 * Created by yangyang && nannan on 15-12-21.
 * 新闻类型页面抽取实现类
 */
public class ExtractNews extends InfoExtract {

    static boolean flag = false;  //判断是否已经找到标题
    static boolean flag_t = false; //是否找到时间
    static boolean flag_n = false; //是否是新闻
    static boolean brief = false; //下面是否是摘要
    static String content = ""; //保存网页的内容
    static String content1 = ""; //保存一个div节点中的内容
    static String time = "";   //保存新闻发表的时间
    static String s_match = "";
    static boolean isdiv = true;

    //判断是否是新闻类型的网页
    public static boolean isNews(Document doc) {
        Elements es = doc.select("a");
        String text = "";
        for (Element e : es) {
            text = e.text();
            if (text.contains("新闻") | text.contains("资讯")) {
                return true;
            }
        }
        es = doc.select("div");
        for (Element e : es) {
            if (e.children().size() == 0) {
                text = e.text();
                if (isSource(text)) {
                    return true;
                }
            }
        }
        es = doc.select("p");
        for (Element e : es) {
            text = e.text();
            if (isSource(text)) {
                return true;
            }
        }
        es = doc.select("span");
        for (Element e : es) {
            text = e.text();
            if (isSource(text)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Extractable> extractInformation(String html) {
        Document doc = parseHTML(html);
        doc = web_Clean(doc); //网页清洗
        if (!isNews(doc))
            return null;

        List<Extractable> result = new ArrayList<>();
        News news = new News();

        //String fileName="F:\\news\\http___www.zhaoshang800.com_news_n-15967.html.html";
        Elements div_main1 = doc.select("div[class~=(main*)|(content*)|(container*)|(article*)|(detail*)]");  //把最大的div块拿出来
        Elements es = doc.select("div[class=news-item-excerpts]");
        for (Element e : es) {
            news.put("摘要", e.text().trim());
            //System.out.println("%%%%%%%%"+news.hm.get("摘要"));
        }
        doc.select("div[class=news-item-excerpts]").remove();    //保证下次不再取此摘要
        int i = 1;
        for (Element element : div_main1) {
            element.select("a").remove();
            //element.select("div[class=inner]").remove();
            element.select("div[class=pic]").remove();
            element.select("div[class=tit]").remove();
            element.select("div[class=txt]").remove();
            element.select("div[class=page_right right]").remove();
            element.select("button").remove();
            element.select("i").remove();
            element.select("span[class~=nav*]").remove();
            element.select("span[class=text-primary fs12]").remove();

            //System.out.println("\r\n"+(i++)+"\r\n");
            flag = false;
            flag_t = false;
            //深度优先遍历
            traverse_my(element, news);

            if (time.trim().isEmpty() | content.trim().isEmpty()) {
                isdiv = false;  //不是我们要找的div块
            }
            if (isdiv) {
                news.put("内容", content);
                news.put("时间", time);
                content = "";
                time = "";
                isdiv = true;
                break;
            }
            content = "";
            time = "";
            isdiv = true;
        }

        result.add(news);
        return result;
    }

    //对被查询的元素执行一次深度优先遍历（针对新闻类型的网页）
    public static void traverse_my(Element root, News news) {  //root为最大的div节点
        Elements nodes_in = root.children();
        int i = nodes_in.size(); //获取子节点的个数
        if (i == 0) { //若其中无子节点，则将其中的文本取出
            //System.out.println("judge！");
            String s = root.text();
            //寻找摘要（比较局限）
            if (s.contains("文章简介") | s.contains("创客秀导读")) {
                brief = true;
            } else {

                if (!isTitle(root, news)) {
                    //System.out.println(s);
                    if (!s.trim().isEmpty()) {
                        if (flag_t == false) { //还没有找到时间
                            if (isTimeAll(s)) {
                                if (s.contains(":")) {
                                    //System.out.println("%%%%%%%%%%%%%%%%%%");
                                    flag_t = true;
                                }
                                time += s_match;
                                time += " ";
                                s_match = "";
                            } else {
                                if (isTime(s)) {
                                    time += s_match;
                                    flag_t = true;
                                    s_match = "";
                                } else {
                                    if (!isTag(s)) {  //若不是无用标签，则为网页内容content
                                        content = content + "\r\n" + root.text();
                                    }
                                }
                            }
                        } else {
                            String s1 = root.text().trim();
                            if (!(s1.isEmpty() || s1 == "")) {
                                if ((!isSundry(s1)) && (!isTag(s1))) {
                                    content = content + "\r\n" + s1; //在上一个content1的基础上加
                                }
                            }

                        }
                    }
                }
            }


        } else {
            for (Element node : nodes_in) {
                traverse_my(node, news);
            }
            root.children().remove();
            String s2 = root.text().trim(); //获得剩余的文本
            if (!s2.isEmpty()) {
                if (isTimeAll(s2)) {
                    if (s2.contains(":")) {
                        flag_t = true;
                    }
                    time += s_match;
                    time += " ";
                    s_match = "";
                } else {
                    if (isTime(s2)) {
                        //System.out.println("@@@@@@@@@@"+s2);
                        time += s_match;
                        flag_t = true;
                        s_match = "";
                    } else {
                        if ((!isSundry(s2)) && (!isTag(s2))) {
                            if (brief) { //如果是简介
                                news.put("摘要", s2);
                                brief = false;
                            } else {
                                if (!s2.equals("无")) {
                                    content = content + "\r\n" + s2;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //下载图片
    public static void img_URL(Element doc, String filename) throws IOException { //输入参数是最大div节点

        Element image = doc.select("img").first();
        String url = image.absUrl("src");
        //下载图片
        URL url1 = new URL(url);
        URLConnection uc = url1.openConnection();
        InputStream is = uc.getInputStream();
        File file = new File(filename + ".jpg");
        FileOutputStream out = new FileOutputStream(file);
        int i = 0;
        while ((i = is.read()) != -1) {
            out.write(i);
        }
    }

    public static boolean isTimeAll(String time) {
        Pattern p = Pattern.compile("(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)");
        Matcher matcher = p.matcher(time);
        if (matcher.find()) {
            s_match = matcher.group();
            return true;
        }
        return false;
    }

    public static boolean isDate(String Date) {
        Pattern p = Pattern.compile("\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号]).*");
        Matcher matcher = p.matcher(Date);
        if (matcher.find()) {
            s_match = matcher.group();
            return true;
        }
        return false;
    }

    public static boolean isTime(String Time) {
        Pattern p = Pattern.compile("\\d{1,2}[点|时|:]\\d{1,2}(分)?(:)?(\\d{1,2}(秒)?)?");
        Matcher matcher = p.matcher(Time);
        if (matcher.find()) {
            s_match = matcher.group();
            return true;
        }
        return false;
    }

    public static boolean isTitle(Element root, News news) {
        if (flag) {
            return false;   //已经有标题，则不是标题
        }
        String tagName = root.tagName();
        if (tagName.equals("h1") | tagName.equals("h2") | tagName.equals("h3") | tagName.equals("h4")) {
            flag = true;  //找到标题
            //System.out.println("标题："+root.text());
            news.put("标题", root.text());
            return flag;
        }
        return false;
    }

    public static boolean isSource(String s) { //判断是否是新闻：依据：含有“来源：”字样
        Pattern p = Pattern.compile("来源：");
        Matcher matcher = p.matcher(s);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    public static boolean isSundry(String s) {   //把上一篇下一篇去掉
        Pattern p = Pattern.compile("上一篇：.*");
        Matcher matcher = p.matcher(s);
        boolean pre = matcher.matches();
        p = Pattern.compile("下一篇：.*");
        matcher = p.matcher(s);
        boolean next = matcher.matches();
        if (pre | next) {
            return true;
        }
        return false;
    }

    public static boolean isTag(String s) {  //将新闻网页底下的无用标签去掉
        if (s.contains("标签：") | s.contains("相关文章：") | s.contains("标签:")) {
            return true;
        }
        return false;
    }

    public static Document parseHTML(String html) {
        Document doc = Jsoup.parse(html); //把一个文档对象给解析出来
        String str = doc.html().replaceAll("&nbsp;", "");
        doc = Jsoup.parse(str);
        return doc;
    }

    public static Document web_Clean(Document doc) {  //网页清洗，去掉无用标签
        doc.select("div[class~=active*]").remove();
        doc.select("div[class~=box*]").remove();
        doc.select("div[class=tit clr]").remove();
        doc.select("div[class~=comment*]").remove();
        doc.select("div[class=des]").remove();
        doc.select("div[class~=share*]").remove();
        doc.select("ul").remove();
        doc.select("ol").remove();
        doc.select("span[class=close-button]").remove();   //关闭雷锋广告
        return doc;
    }

}
