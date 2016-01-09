package cn.edu.njnu.infoextract.impl.activities.main_process;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.infoextract.impl.activities.Active_Web_Extract.Active_HTML_Extract;

/**
 * Created by songzhenxing on 15-12-21.
 * 活动类型页面抽取实现类
 */
public class ExtractActivities extends InfoExtract {

    @Override
    public String getType() {
        return "众创活动";
    }

    @Override
    public List<Extractable> extractInformation(String html) {
        //getDOM(html);
        root = Jsoup.parse(html);
        List<Extractable> result = new ArrayList<>();

        Active_HTML_Extract active_Extract = new Active_HTML_Extract();
        //判断页面是否只有一个活动
        if (active_Extract.Judge_Html_List(root)) {
            //假如页面内只有一个活动
            result.add(active_Extract.Active_Html_Extract(root));
        } else {
            //假如页面内有多条活动记录
            result.addAll(active_Extract.Some_Active_Html_Extract(root));
        }

        return result;
    }
}
