package cn.edu.njnu.infoextract.impl.incubators;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;

public class PatternStore {
    public static List<Feature> allpattern = new ArrayList<>();

    static {
        allpattern.add(new Feature("/div/p/b", "b", "lxfs", "p", "show_main", "span", "", "/div/p/span", "span", "lxfs"));
        allpattern.add(new Feature("/div/ul/li", "li", "menu", "ul", "tab1", "", "", "/div/div/div", "div", "menudiv"));
        //allpattern.add(new Feature("/div/h6/em", "em", "main7", "h6", "main4_c", "", "", "/div/div/p"));
    }

    public PatternStore() {
    }

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
