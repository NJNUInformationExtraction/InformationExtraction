package cn.edu.njnu.infoextract.impl.incubators;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsoup.nodes.Element;

public class PatternStore {
    public static int page = 0;
    public static List<Feature> allpattern = new ArrayList<>();

    static {
        allpattern.add(new Feature("/div/p/b", "b", "lxfs", "div", "lxfs", "span", "", "/div/p/span", "span", "lxfs"));//创客秀
        allpattern.add(new Feature("/div/ul/li", "li", "menu", "div", "tab1", "", "", "/div/div/div", "div", "menudiv"));//创客秀
        allpattern.add(new Feature("/div/div/h2", "h2", "mk-shortcode", "div", "wrapper", "", "", "/div/div/div", "div", "text"));
        allpattern.add(new Feature("/div/dl/dt", "dt", "title", "dl", "inc_main", "", "", "/div/dl/dd", "dd", "con"));
        allpattern.add(new Feature("/li/div/h1", "h1", "container", "div", "container", "", "", "/li/div/h2", "h2", "container"));
        allpattern.add(new Feature("/div/div/span", "span", "z_main", "div", "z_main", "", "", "/div/div/p", "p", "z_main"));//cyzone
        allpattern.add(new Feature("/div/div/span", "span", "z_center", "div", "z_center", "", "", "/div/div/p", "p", "z_center"));//cyzone
        allpattern.add(new Feature("/div/ul/li", "li", "Menubox3", "div", "lpjs", "", "", "/div/div", "div", "Contentbox3"));//cyzone
        //allpattern.add(new Feature("/span","span","columntext","table","border01","","","/tbody/tr/td","td","ain_text"));//huilongsen
        allpattern.add(new Feature("/div/div/h2", "h2", "div_R", "div", "div_R", "", "", "/div/div/div", "div", "div_R"));//jingmenggaoke
        allpattern.add(new Feature("/div/div", "div", "n3_Title", "div", "*", "", "", "/div/div", "div", "n3_test"));// cn putian
    }

    public static List<ValueFeature> valuepattern = new ArrayList<>();

    static {
        valuepattern.add(new ValueFeature("/div/h6/em", "em", "main7", "last", "span", "class"));//cyzgc title
        valuepattern.add(new ValueFeature("/div/p", "p", "pic2_r", "p", "p", "class"));//cyzgc
        valuepattern.add(new ValueFeature("/div/p", "p", "pic2_r", "last", "p", "class"));//cyzgc
        valuepattern.add(new ValueFeature("/div/p", "p", "pic2_r", "last", "first", "class"));//cyzgc
        valuepattern.add(new ValueFeature("/div/p", "p", "pic2_r", "p", "first", "class"));//cyzgc
        valuepattern.add(new ValueFeature("/div/img", "img", "show_img", "last", "first", "class"));//创客秀
        valuepattern.add(new ValueFeature("/div/div/div", "div", "f_bottom", "last", "div", "class"));//创业谷
        valuepattern.add(new ValueFeature("/body/div/div", "div", "bottom", "last", "div", "class"));//创客总部
        //valuepattern.add(new ValueFeature("/div/div/h5","h5","","")) 北京创客空间
        //valuepattern.add(new ValueFeature("/html/body/div","div","*","div","div"));//普天德胜
        valuepattern.add(new ValueFeature("/ul/li/p", "p", "clearfix", "p", "p", "class"));//cyzone
        valuepattern.add(new ValueFeature("/ul/li/p", "p", "clearfix", "last", "first", "class"));//cyzone
        valuepattern.add(new ValueFeature("/ul/li/p", "p", "clearfix", "p", "first", "class"));//cyzone
        valuepattern.add(new ValueFeature("/ul/li/p", "p", "clearfix", "last", "p", "class"));//cyzone
        valuepattern.add(new ValueFeature("/div/div/h3", "h3", "contain_left", "ul", "first", "class"));//cyzone title
        valuepattern.add(new ValueFeature("/tbody/tr/td", "td", "footer_bottom", "last", "td", "class"));//bei hang tian hui incubators.
        valuepattern.add(new ValueFeature("/div/div", "div", "footer_mian", "last", "first", "id"));//beijing gaoke kejifuhua
        valuepattern.add(new ValueFeature("/td/div/b", "b", "s9", "br", "br", "class"));//beijing wangjing title
        valuepattern.add(new ValueFeature("/table/tbody/tr", "tr", "s14", "last", "tr", "class"));//beijing wangjing main content
        valuepattern.add(new ValueFeature("/div/div/h1", "h1", "clearfix", "p", "first", "class"));//beijing saiou title
        valuepattern.add(new ValueFeature("/div/div/p", "p", "sokjy", "p", "p", "class"));//beijing saiou
        valuepattern.add(new ValueFeature("/div/div", "div", "lianxi", "div", "div", "id"));//pu tian de sheng
        valuepattern.add(new ValueFeature("/div/div/p", "p", "display_nr_div", "p", "p", "class"));page = Math.abs(new Random(System.currentTimeMillis()).nextInt() % 400);
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
