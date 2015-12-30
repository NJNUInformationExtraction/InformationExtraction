package cn.edu.njnu.tidypage;

import org.jsoup.nodes.Element;

/**
 * Created by zhangzhi on 15-12-25.
 * 用于在压缩清洗页面前作一些定制的预处理,如搜索目标<div></div>
 */
public interface PreProcess {

    /**
     * 预处理方法(留给各模块决定是否要实现)
     *
     * @param root DOM树的根节点
     * @return 搜索到的最大<div></div>
     */
    Element preProcess(Element root);

}
