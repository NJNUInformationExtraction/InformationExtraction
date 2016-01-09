package cn.edu.njnu.infoextract.impl.incubators;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.domain.ext.Incubator;
import cn.edu.njnu.infoextract.InfoExtract;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

/**
 * Created by luotianyao on 15-12-29.
 * 孵化器类型页面抽取实现类
 */
public class ExtractIncubators extends InfoExtract {

    @Override
    public List<Extractable> extractInformation(String html) {
        String safe = Jsoup.clean(html, Whitelist.relaxed().addAttributes(":all", "class"));
        Element root = Jsoup.parse(safe);
        Incubator info = new Incubator();
        List<Extractable> result = new ArrayList<>();
        while (true) {
            if (Extract(root, info) == false)
                break;
        }
        result.add(info);
        return result;
    }

    @Override
    public String getType() {
        return "";
    }

    public static boolean Extract(Element root, Incubator Info) {
        String s_value = "";
        Element e_value = null;
        Element e_key = null;
        String s_key = "";
        String s_tag = "";
        boolean esuc = false;//标志是否抽取到信息
        boolean kflag = false;
        boolean vflag = false;
        /**
         * 抽取过程分2步
         *
         * 1.只抽value
         * 2.抽key，value对应的
         *
         */
        /**
         * 遍历所有value模式
         * 选class，若不存在则下一条模式
         * 若存在，则选class下对应的标签，可能有多个标签，所以要遍历所有的标签
         * 判断路径＋兄弟节点。若符合，则提取信息，
         * 若不符合则下一个标签。
         */
        for (ValueFeature i : PatternStore.valuepattern) {
            if (root.select("[class~=(.*)" + i.getClassname() + "(.*)]").isEmpty() == false) {
                e_value = root.select("[class~=(.*)" + i.getClassname() + "(.*)]").first();
                System.out.println(e_value.tagName() + e_value.className());
            } else
                continue;
            for (Element p : e_value.select(i.getTag())) {
                if (p == null)
                    continue;
                else {
                    if (IspatternValue(p, i) == true) {
                        vflag = true;
                        s_tag = p.tagName();
                        if (s_tag.equals("img")) {
                            s_value = p.absUrl("src");
                        } else
                            s_value = p.text();
                        System.out.println("s_value alone:" + s_value);
                        p.remove();
                        break;
                    } else
                        continue;
                }
            }
            if (vflag == true)//抽取到了，跳出外层循环
                break;
        }
        for (Feature i : PatternStore.allpattern) {
            if (vflag == true)
                break;//如果已经抽取到了V，就不再做key－value的抽取
            else {
                Element p = root;
                Element kp = root;
                Element vp = root;
                /**
                 * 1.找公有的节点 class
                 * 2.找key独有节点的class
                 * 3.找value独有节点的class
                 * 4.先找key对应的值,若没找到则下一个
                 * 5.若找到，找对应
                 */
                if (root.select("[class~=(.*)" + i.getKey_parent_class() + "(.*)]").isEmpty() == false)
                    p = root.select("[class~=(.*)" + i.getKey_parent_class() + "(.*)]").first();//
                else
                    continue;
                if (p.select("[class~=(.*)" + i.getKey_class() + "(.*)]").isEmpty() == false)
                    kp = p.select("[class~=(.*)" + i.getKey_class() + "(.*)]").first();//
                else
                    continue;
                if (p.select("[class~=(.*)" + i.getValue_class() + "(.*)]").isEmpty() == false)
                    vp = p.select("[class~=(.*)" + i.getValue_class() + "(.*)]").first();//
                else continue;
                String key = "";
                String value = "";
                kflag = false;
                vflag = false;
                for (Element e : kp.select(i.getKey_tag())) {
                    if (e == null)
                        continue;
                    else {
                        if (IspatternKey(e, i) == true) {
                            System.out.println("Key:" + e.text());
                            s_key = e.text();
                            e.remove();
                            kflag = true;
                            break;
                        } else
                            continue;
                    }
                }
                if (kflag == true)//如果抽取到key，则抽取对应的value
                    for (Element e : vp.select(i.getValue_tag())) {
                        if (e == null) {
                            continue;
                        } else {
                            if (IsKey_Value(e, i) == true) {
                                System.out.println("Value:" + e.text());
                                s_value = e.text();
                                e.remove();
                                vflag = true;
                                break;
                            } else
                                continue;
                        }
                    }
                if (vflag == true)
                    break;
            }
        }
        if (vflag == true && kflag == true) {
            Info.put(s_key, s_value);
            esuc = true;
        } else if (vflag == true && kflag == false) {
            /**
             * 提取XXX:XXXX的信息
             */
            if (s_tag.equals("img"))
                Info.put("图片", s_value);
            else {
                String[] brsplit = s_value.split(" ");
                for (int i = 0; i < brsplit.length; i++) {
                    System.out.println(brsplit[i]);
                    Pattern patternx = Pattern.compile("(.{0,4})(:|：)(.*)");
                    Matcher mx = patternx.matcher(brsplit[i]);
                    if (mx.matches()) {
                        String[] split = mx.group().toString().split(":|：");
                        if (split.length == 1) {
                            Info.put(split[0], "");
                        } else {
                            Info.put(split[0], split[1]);
                        }
                    } else {
                        if (s_tag.equals("em"))
                            Info.put("标题", s_value);
                    }
                }
            }
            esuc = true;
        } else if (vflag == false && kflag == true) {
        }
        return esuc;
    }

    public static boolean IspatternKey(Element root, Feature feature) {
        String pre_tag = "";
        String next_tag = "";
        String path = PatternStore.CreatePath(root);
        int len = feature.getKey_path().length();
        if (path.substring(path.length() - len, path.length()).equals(feature.getKey_path())) {
            return true;
        } else
            return false;
    }

    public static boolean IspatternValue(Element root, ValueFeature feature) {
        String path = PatternStore.CreatePath(root);
        int len = feature.getPath().length();
        String pre_tag = "";
        String next_tag = "";
        if (path.substring(path.length() - len, path.length()).equals(feature.getPath())) {
            if (root.previousElementSibling() == null)
                pre_tag = "first";
            else
                pre_tag = root.previousElementSibling().tagName();
            if (root.nextElementSibling() == null)
                next_tag = "last";
            else
                next_tag = root.nextElementSibling().tagName();
            System.out.println("pre_tag:" + pre_tag);
            System.out.println("next_tag:" + next_tag);
            if (pre_tag.equals(feature.getLeft_tag()) && next_tag.equals(feature.getRight_tag())) {
                return true;
            } else
                return false;
        } else
            return false;
    }

    public static boolean IsKey_Value(Element root, Feature feature) {
        String pre_tag = "";
        String next_tag = "";
        String path = PatternStore.CreatePath(root);
        int len = feature.getValue_path().length();
        if (path.substring(path.length() - len, path.length()).equals(feature.getValue_path())) {
            return true;
        } else
            return false;
    }
}
