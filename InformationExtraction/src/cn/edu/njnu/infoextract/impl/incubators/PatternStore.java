package cn.edu.njnu.infoextract.impl.incubators;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;

public class PatternStore {
    public static List<Feature> allpattern = new ArrayList<Feature>();

    static {
        allpattern.add(new Feature("/div/p/b", "b", "lxfs", "p", "show_main", "span", "", "/div/p/span", "span", "lxfs"));
        allpattern.add(new Feature("/div/ul/li", "li", "menu", "ul", "tab1", "", "", "/div/div/div", "div", "menudiv"));
        allpattern.add(new Feature("/div/div/h2", "h2", "mk-shortcode", "div", "wrapper", "", "", "/div/div/div", "div", "text"));
        allpattern.add(new Feature("/div/dl/dt", "dt", "title", "dl", "inc_main", "", "", "/div/dl/dd", "dd", "con"));
        allpattern.add(new Feature("/li/div/h1", "h1", "container", "div", "container", "", "", "/li/div/h2", "h2", "container"));
    }

    public static List<ValueFeature> valuepattern = new ArrayList<ValueFeature>();

    static {
        valuepattern.add(new ValueFeature("/div/h6/em", "em", "main7", "last", "span"));
        valuepattern.add(new ValueFeature("/div/p", "p", "pic2_r", "p", "p"));
        valuepattern.add(new ValueFeature("/div/p", "p", "pic2_r", "last", "p"));
        valuepattern.add(new ValueFeature("/div/p", "p", "pic2_r", "last", "first"));
        valuepattern.add(new ValueFeature("/div/p", "p", "pic2_r", "p", "first"));
        valuepattern.add(new ValueFeature("/div/img", "img", "show_img", "last", "first"));
        valuepattern.add(new ValueFeature("/div/div/div", "div", "f_bottom", "last", "div"));//创业谷
        valuepattern.add(new ValueFeature("/body/div/div", "div", "bottom", "last", "div"));//创客总部
        //valuepattern.add(new ValueFeature("/div/div/h5","h5","","")) 北京创客空间
        valuepattern.add(new ValueFeature("/html/body/div", "div", "*", "div", "div"));//普天德胜
    }

    public PatternStore() {
    }

    /**
     * @param root
     * @return
     */
    public static String CreatePath(Element root) {
        String path = "";
        Element parent = root.parent();
        while (parent != null) {
            //path="/"+root.tagName()+"["+root.elementSiblingIndex()+"]"+path;
            path = "/" + root.tagName() + path;
            root = parent;
            parent = root.parent();
        }
        return path;
    }
}
