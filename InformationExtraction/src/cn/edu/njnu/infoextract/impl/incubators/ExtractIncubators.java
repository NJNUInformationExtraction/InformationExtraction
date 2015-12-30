package cn.edu.njnu.infoextract.impl.incubators;

import java.util.*;

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
        info = Traverse(root, info);
        result.add(info);
        return result;
    }

    public static Incubator Traverse(Element root, Incubator info) {
        for (Element i : root.children()) {
            info = Extract(i, info);
            info = Traverse(i, info);
        }
        return info;
    }

    public static Incubator Extract(Element root, Incubator Info) {
        Element p = root;
        Element kp = root;
        Element vp = root;
        for (Feature i : PatternStore.allpattern) {
            /*
             * 抽取算法
			 * 1.先提取大divclass，在分别提取小div,
			 * 1.判断节点路径是否匹配
			 * 2.把key value都提出来
			 * 3.
			 *
			 */
            if (!root.select("*." + i.getKey_parent_class()).isEmpty())
                p = root.select("*." + i.getKey_parent_class()).first();
            else
                continue;
            if (!p.select("*." + i.getKey_class()).isEmpty())
                kp = p.select("*." + i.getKey_class()).first();
            else
                continue;
            if (!p.select("*." + i.getValue_class()).isEmpty())
                vp = p.select("*." + i.getValue_class()).first();
            else continue;
            String key = "";
            String value = "";
            boolean kflag = false;
            boolean vflag = false;
            for (Element atom : kp.select(i.getKey_tag())) {
                String path = PatternStore.CreatePath(atom);
                int len = i.getKey_path().length();
                if (path.substring(path.length() - len, path.length()).equals(i.getKey_path())) {
                    kflag = true;
                    key = atom.text();
                    atom.remove();
                    break;
                }
            }
            for (Element vatom : vp.select(i.getValue_tag())) {
                String path = PatternStore.CreatePath(vatom);
                int len = i.getValue_path().length();
                if (path.substring(path.length() - len, path.length()).equals(i.getValue_path())) {
                    vflag = true;
                    value = vatom.text();
                    vatom.remove();
                    break;
                }
            }
            if (kflag && vflag) {
                Info.put(key, value);
                return Info;
            }
        }
        return Info;
    }
}
