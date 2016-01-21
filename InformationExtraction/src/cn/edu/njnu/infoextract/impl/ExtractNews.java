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

    static boolean flag = false;    //判断是否已经找到标题
    static boolean flag_t = false;  //判断是否找到时间
    static boolean flag_t1 = false; //判断时间的另一个标准
    static boolean flag_t2 = false;  //是否是新闻
    static boolean brief = false;  //下面是否是摘要
    static String content = "";   //保存网页的内容
    static String content1 = "";  //保存一个div节点中的内容
    static String time = "";     //保存新闻发表的时间
    static String s_match = "";
    static boolean isdiv = true;

    //判断是否是新闻类型的网页
    //判断是否是新闻类型的网页
    public static boolean isNews(Document doc) {
        Elements es = doc.select("a");
        String text = "";
        for (Element e : es) {
            text = e.text();
            if (text.contains("新闻") | text.contains("资讯") | text.contains("动态")) {

                return true;
            }
        }
        es = doc.select("div");
        for (Element e : es) {
            text = e.text();
            if (isSource(text)) {
                return true;
            }
        }
        es = doc.select("p");
        for (Element e : es) {
            text = e.text();
            //System.out.println();
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
    public String getType() {
        return "新闻资讯";
    }

    @Override
    public List<Extractable> extractInformation(String html) {
        Document doc = parseHTML(html);
        doc = web_Clean(doc); //网页清洗
        if (!isNews(doc))
            return null;
        doc = clean_Again(doc);    //再次清洗
        List<Extractable> result = new ArrayList<>();
        News news = new News();

        //String fileName="F:\\news\\http___www.zhaoshang800.com_news_n-15967.html.html";
        Elements div_main1 = doc.select("div[class~=(main*)|content*|(container*)|(article*)|(detail*)]");  //把最大的div块拿出来
        Elements div_main2 = doc.select("div[id~=(main*)|(content*)|(container*)|(article*)|(detail*)]");
        Elements div_main3 = doc.select("table");
        div_main1.addAll(div_main3);
        div_main1.addAll(div_main2);
        Elements es = doc.select("div[class=news-item-excerpts]");
        for (Element e : es) {
            news.put("摘要", e.text().trim());
        }
        doc.select("div[class=news-item-excerpts]").remove();    //保证下次不再取此摘要
        int i = 1;
        for (Element element : div_main1) {
            element.select("a").remove();
            element.select("br").remove();
            //element.select("div[class=inner]").remove();
            element.select("div[class=pic]").remove();
            element.select("div[class=tit]").remove();
            //element.select("div[class=txt]").remove();
            element.select("div[class=page_right right]").remove();
            element.select("button").remove();
            element.select("i").remove();
            element.select("span[class~=nav*]").remove();
            element.select("span[class=text-primary fs12]").remove();

            //System.out.println("\r\n"+(i++)+"\r\n");
            flag = false;
            flag_t = false;
            flag_t1 = false;
            news.put("标题", "");
            //深度优先遍历
            traverse_my(element, news);

            if (news.get("标题").trim().isEmpty() | content.trim().isEmpty()) {
                //System.out.println("$$$$$$$$$");
                isdiv = false;    //不是我们要找的div块
            }
            if (isdiv) {
                news.put("内容", content);
                news.put("时间", time);
                result.add(news);
                return result;
            }
            content = "";
            time = "";
            isdiv = true;
            news.put("标题", "");
        }

        return new ArrayList<>();
    }

    //对被查询的元素执行一次深度优先遍历（针对新闻类型的网页）
    public static void traverse_my(Element root, News news) {  //root为最大的div节点
        //System.out.println("@@@@@"+"  "+root.tagName()+" "+root.children().size()+"  "+root.text());
        Elements nodes_in = root.children();
        int i = nodes_in.size();  //获取子节点的个数
        if (i == 0) {    //若其中无子节点，则将其中的文本取出
            //System.out.println("judge！");
            String s = root.text().trim();
            //System.out.println("########"+s);
            //寻找摘要（比较局限）
            //System.out.println("########"+s);
            if (s.contains("文章简介") | s.contains("导读")) {
                brief = true;
            } else {
                if (root.className().equals("abstract")) {
                    news.put("摘要", root.text().trim());
                } else {
                    if (!isTitle(root, news)) {
                        //System.out.println(s);
                        if (!s.trim().isEmpty()) {
                            if (flag_t == false) {    //还没有找到时间
                                if (isTimeAll(s)) {
                                    System.out.println("@@@@@@@" + s);
                                    time += s_match;
                                    time += " ";
                                    s_match = "";
                                    flag_t1 = true;  //找到了时间的开始标志
                                    //System.out.println("@@@@@"+time);
                                    if (root.nextElementSibling() != null) {
                                        String time = root.nextElementSibling().text();
                                        if (!time.trim().isEmpty()) {
                                            if (isTime(time)) {
                                                //下一个兄弟节点含有具体的时间
                                                time += s_match;
                                                time += " ";
                                                s_match = "";
                                            }
                                        }
                                    }
                                    flag_t = true;
                                } else {
                                    if (isTime(s)) {
                                        if (flag_t1) {
                                            time += s_match;
                                            flag_t = true;
                                            s_match = "";
                                        } else {
                                            content = content + root.text();
                                        }
                                    } else {
                                        if ((!isTag(s)) && (!isSpecial(s)) && (!isView(s)) && (!isSymbol(s))) {    //若不是无用标签，则为网页内容content
                                            if (isNum(s) | isKanji(s)) {
                                                content = content + root.text();
                                            } else {
                                                if (flag_t2) {
                                                    flag_t2 = false;
                                                } else {
                                                    content = content + "\r\n" + root.text();
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                String s1 = root.text().trim();
                                if (!(s1.isEmpty() || s1 == "")) {
                                    if ((!isSundry(s1)) && (!isTag(s1)) && (!isSpecial(s1)) && (!isView(s1)) && (!isSymbol(s1))) {
                                        if (isNum(s1) | isKanji(s1)) {
                                            content = content + s1;
                                        } else {
                                            if (flag_t2) {
                                                flag_t2 = false;
                                            } else {
                                                content = content + "\r\n" + s1;    //在上一个content1的基础上加
                                                //System.out.println("%%%%%%%%%"+root.tagName()+"   "+root.id()+"  "+s1);
                                            }
                                        }
                                    }
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
            String s2 = root.text().trim();   //获得剩余的文本
            if (!s2.isEmpty()) {
                if (flag_t == false) {
                    if (isTimeAll(s2)) {
                        time += s_match;
                        time += " ";
                        s_match = "";
                        flag_t1 = true;  //找到了时间的开始标志
                        //System.out.println("@@@@@"+time);
                        if (root.nextElementSibling() != null) {
                            String time = root.nextElementSibling().text();
                            if (!time.trim().isEmpty()) {
                                if (isTime(time)) {
                                    //下一个兄弟节点含有具体的时间
                                    time += s_match;
                                    time += " ";
                                    s_match = "";
                                }
                            }
                        }
                        flag_t = true;
                    } else {
                        if (isTime(s2)) {
                            //System.out.println("@@@@@@@@@@"+s2);
                            time += s_match;
                            flag_t = true;
                            s_match = "";
                        } else {
                            if ((!isSundry(s2)) && (!isTag(s2)) && (!isSpecial(s2)) && (!isView(s2)) && (!isSymbol(s2))) {
                                if (brief) {   //如果是简介
                                    news.put("摘要", s2);
                                    brief = false;
                                } else {
                                    if (!s2.equals("无")) {
                                        content = content + "\r\n" + s2;
                                        //System.out.println("%%%%%%%%%"+root.tagName()+"   "+root.id()+"  "+s2);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (!(s2.isEmpty() || s2 == "")) {
                        if ((!isSundry(s2)) && (!isTag(s2)) && (!isSpecial(s2)) && (!isView(s2)) && (!isSymbol(s2))) {
                            //System.out.println("%%%%%%%%%%%$$$$$$$$^^^^^^^^^^^^^^^^^^^^^^^^");
                            if (brief) {   //如果是简介
                                news.put("摘要", s2);
                                brief = false;
                            } else {
                                if (!s2.equals("无")) {
                                    content = content + "\r\n" + s2;
                                    //System.out.println("%%%%%%%%%"+root.tagName()+"   "+root.id()+"  "+s2);
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
        if (tagName.equals("h1") | tagName.equals("h2") | tagName.equals("h3") | tagName.equals("h4") | root.tagName().equals("font")) {
            flag = true;    //找到标题
            //System.out.println("标题："+root.text());
            news.put("标题", root.text());
            return flag;
        }
        if (root.className().equals("title") | root.className().contains("head")) {
            flag = true;    //找到标题
            news.put("标题", root.text());
            return flag;
        }
        if (root.tagName().equals("strong")) {
            String s = root.text().trim();
            if (s.getBytes().length > 20) {
                flag = true;
                news.put("标题", s);
                return flag;
            }
        }
        return false;
    }

    public static boolean isSource(String s) { //判断是否是新闻：依据：含有“来源：”字样
        Pattern p = Pattern.compile("来源：");
        Matcher matcher = p.matcher(s);
        if (matcher.find()) {
            return true;
        }
        p = Pattern.compile("新闻");
        matcher = p.matcher(s);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    public static boolean isView(String s) {  //网友看法
        Pattern p = Pattern.compile("网友看法");
        Matcher matcher = p.matcher(s);
        if (matcher.matches()) {
            return true;
        }
        if (s.contains("评论")) {
            if (!s.contains("评论，") && (!s.contains("评论”"))) {
                return true;
            }

        }
        return false;
    }

    public static boolean isNum(String s) {  //单数字
        Pattern p = Pattern.compile("\\d{1,4}");
        Matcher matcher = p.matcher(s);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean isKanji(String s) {  //单个汉字
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = p.matcher(s);
        if (matcher.matches()) {
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

    public static boolean isSpecial(String s) {   //判断字符串中是否含有">>"
        if (s.contains("»")) {
            return true;
        }
        return false;
    }

    public static boolean isSymbol(String s) {   //判断字符串中是否含有">"
        if (s.contains(">")) {
            return true;
        }
        return false;
    }


    public static Document parseHTML(String html) {
        Document doc = Jsoup.parse(html, "UTF-8"); //把一个文档对象给解析出来
        String str = doc.html().replaceAll("&nbsp;", "");
        doc = Jsoup.parse(str);
        return doc;
    }

    public static Document web_Clean(Document doc) {   //网页清洗，去掉无用标签
        doc.select("div[class~=active*]").remove();
        //doc.select("div[class~=box*]").remove();
        doc.select("div[class=tit clr]").remove();
        doc.select("div[class~=comment*]").remove();
        doc.select("div[id~=comment*]").remove();
        doc.select("div[class=des]").remove();
        doc.select("div[class~=share*]").remove();
        doc.select("div[id=yuanweninfo]").remove();    //把网页中的URL去掉
        doc.select("div[id=contentMsg_baidu]").remove();    //把网页中重复的内容去掉
        doc.select("div[id=articlePreTitle]").remove();   //将“？”去掉
        doc.select("div[id=articleSubtitle]").remove();   //将“？”去掉
        doc.select("div[id=displaypagenum]").remove();
        doc.select("div[id=contentLiability]").remove();
        doc.select("div[id=articleEdit]").remove();
        doc.select("div[class=contentColumnLeft]").remove();
        doc.select("div[class=contentRight]").remove();
        doc.select("div[class=black12]").remove();
        doc.select("div[id=mpagecount]").remove();
        doc.select("div[id=mcurrentpage]").remove();
        doc.select("span[class=top_comment_wp]").remove();
        doc.select("div[class=qrcode wxbj]").remove();
        doc.select("div[class=nfwwx]").remove();
        doc.select("div[class=cl article_ft]").remove();
        doc.select("div[class=fn_area cl]").remove();
        doc.select("div[class=column related]").remove();
        doc.select("div[class=column comment]").remove();
        doc.select("div[class=column gallery_rec]").remove();
        doc.select("div[class=headline_rec]").remove();
        doc.select("div[class=column special_rec]").remove();
        doc.select("div[class=wpb_row row-fluid next-prev]").remove();
        doc.select("div[class=author-box-wrap]").remove();
        doc.select("div[class=ds-thread]").remove();
        doc.select("div[class=span4 column_container]").remove();
        doc.select("div[class=relative-news feed-wrap]").remove();
        doc.select("div[class=focus-news left-module notInPad]").remove();
        doc.select("div[class=sina15-top-bar-wrap]").remove();
        doc.select("div[id=ent_search]").remove();
        doc.select("div[class=aside]").remove();
        doc.select("div[class=single-tag]").remove();
        doc.select("div[id=respond]").remove();
        doc.select("div[class=aside]").remove();
        doc.select("div[class=box cl]").remove();
        doc.select("div[class=art-img-text-down similar-articles]").remove();
        doc.select("div[class=mainmiddle-right-01]").remove();
        doc.select("table[id=news2]").remove();
        doc.select("table[id=news]").remove();
        doc.select("table[class=left_border]").remove();
        doc.select("td[class=hjpl]").remove();
        doc.select("div[class=right_12 fr]").remove();
        doc.select("div[class=right fl]").remove();
        doc.select("div[class=proCard-cnt]").remove();
        doc.select("div[class=rank]").remove();
        doc.select("div[class=lph-right]").remove();
        //doc.select("ul").remove();
        //doc.select("ol").remove();
        doc.select("span[class=close-button]").remove();   //关闭雷锋广告
        return doc;
    }

    public static Document clean_Again(Document doc) {
        Elements uls = doc.select("ul");
        for (Element ul : uls) {
            if (!ul.parent().className().equals("text")) {
                ul.remove();
            }
        }
        doc.select("ol").remove();
        doc.select("div[class=mainmiddle-left]").remove();
        return doc;
    }

}
