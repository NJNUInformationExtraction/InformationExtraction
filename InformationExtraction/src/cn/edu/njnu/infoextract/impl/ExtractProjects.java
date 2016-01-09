package cn.edu.njnu.infoextract.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.domain.ext.Project;
import cn.edu.njnu.infoextract.InfoExtract;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by yangyang && nannan on 15-12-29.
 * 项目类型页面抽取实现类
 */
public class ExtractProjects extends InfoExtract {

    static boolean flag = false;
    static boolean flag_t = false;
    static String content = "";
    static String time = "";
    static String s_match = "";
    static boolean isdiv = true;
    static String img_url = "";

    @Override
    public String getType() {
        return "项目";
    }

    @Override
    public List<Extractable> extractInformation(String html) {

        List<Extractable> result = new ArrayList<>();
        Project news = new Project();

        Document doc = Jsoup.parse(html);
        String str = doc.html().replaceAll("&nbsp;", "");
        doc = Jsoup.parse(str);
        doc.select("div[class~=active*]").remove();
        //doc.select("div[class~=box*]").remove();
        doc.select("div[class=tit clr]").remove();
        doc.select("div[class~=comment*]").remove();
        doc.select("div[class=des]").remove();
        doc.select("div[class~=logic*]").remove();
        Elements div_main1 = doc.select("div[class~=(main*)|(content*)|(container*)|(article*)|(detail*)|(project*)]");
        int i = 1;
        for (Element element : div_main1) {
            //element.select("a").remove();
            //element.select("div[class=inner]").remove();
            element.select("div[class=pic]").remove();
            element.select("div[class=tit]").remove();
            element.select("div[class=txt]").remove();
            element.select("button").remove();
            //element.select("i").remove();
            element.select("span[class~=nav*]").remove();
            element.select("span[class=text-primary fs12]").remove();
            img_url = img_URL(element);
            //element.select("ul").remove();
            //element.select("ol").remove();
            //System.out.println("\r\n"+(i++)+"\r\n");
            flag = false;
            flag_t = false;
            traverse_my(element, news);

            if (time.trim().isEmpty() | content.trim().isEmpty()) {
                isdiv = false;
            }
            if (isdiv) {
                news.put("内容", content);
                news.put("时间", time);
                news.put("图片", img_url);
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

    public static void traverse_my(Element root, Project news) { //root为最大的div节点
        Elements nodes_in = root.children();
        int i = nodes_in.size(); //获取子节点的个数
        if (i == 0) { //若其中无子节点，则将其中的文本取出
            String s = root.text();
            //System.out.println("@@@@@@@@"+root.tagName()+"  "+root.text());
            if (!isTitle(root, news)) {
                if (!s.trim().isEmpty()) {

                    if (flag_t == false) { //还没有找到时间
                        //System.out.println("########"+s);
                        if (isTimeAll(s)) {

                            //System.out.println(root.tagName()+"  "+root.text());
                            if (s.contains(":")) {
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
                                /*if(root.text().contains("\\b")){
                                    System.out.println("###########"+root.text());
								}*/
                                //root.text().replaceAll("\0x3F", "");
                                root.text().trim();

                                content = content + "\r\n" + root.text();
                            }
                        }
                    } else {
                        String s1 = root.text().trim();
                        if (!(s1.isEmpty() || s1 == "")) {
                            if (!isSundry(s1)) {
                                content = content + "\r\n" + s1;
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
            String s2 = root.text().trim();
            //判断是否是作者
            //System.out.println("############"+root.tagName()+"   "+root.className()+"   "+s2);
            if ((root.tagName().equals("a")) && (root.className().equals("user-hd"))) {
                //System.out.println("what is @@@@@@@@@@@@@");
                news.put("作者", s2);
            } else {
                //System.out.println("*********"+s2);
                if (!s2.isEmpty()) {
                    if (isTimeAll(s2)) {
                        //System.out.println("@@@@@@@@@@"+s2);
                        if (s2.contains(":")) {

                            flag_t = true;
                        }
                        time += s_match;
                        time += " ";
                        s_match = "";
                    } else {
                        if (isTime(s2)) {
                            time += s_match;
                            flag_t = true;
                            s_match = "";
                        } else {
                            if (!isSundry(s2)) {
                                content = content + "\r\n" + s2;
                            }
                        }
                    }
                }
            }
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
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public static boolean isTitle(Element root, Project news) {
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

    public static String img_URL(Element doc) {
        int width = 0;
        int height = 0;
        int area = 0;
        String url = "";
        Elements image = doc.select("img");
        int count = image.size();
        if (count == 0) {
            return "";
        }
        for (Element element : image) {
            String widths = element.attr("width");
            String heights = element.attr("height");
            if ((!widths.trim().isEmpty()) && (!heights.trim().isEmpty())) {
                width = Integer.parseInt(element.attr("width"));
                height = Integer.parseInt(element.attr("height"));
                if (width * height > area) {
                    area = width * height;
                    url = element.absUrl("src");
                }
            }
        }
        return url;
    }

    public static void write_Img(String url, String filename) throws IOException {
        if (!url.trim().isEmpty()) {
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
    }

}
