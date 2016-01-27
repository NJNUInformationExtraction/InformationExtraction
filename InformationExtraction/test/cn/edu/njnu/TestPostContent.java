package cn.edu.njnu;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.domain.ext.News;
import cn.edu.njnu.tools.Pair;
import cn.edu.njnu.tools.PostDataHelper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhangzhi on 16-1-9.
 * 测试上传内容
 */
public class TestPostContent {

    @Test
    public void testPostContent() {
        String pid = "568f37291411513bc4c4f7ce";
        List<Extractable> info = new ArrayList<>();
        Extractable news1 = new News();
        news1.put("标题", "测试新闻上传1");
        news1.put("时间", "2016.1.1");
        news1.put("内容", "测试新闻上传接口1");
        news1.put("重要性", "中等");
        news1.put("引用", "暂无");
        info.add(news1);
        Extractable news2 = new News();
        news2.put("标题", "测试新闻上传2");
        news2.put("时间", "2016.1.1");
        news2.put("内容", "测试新闻上传接口2");
        news2.put("重要性", "较低");
        info.add(news2);
        PostDataHelper postDataHelper = new PostDataHelper(new HashMap<>());
        new ProcessUnit(
                new Pair<>("news", "cn.edu.njnu.infoextract.impl.ExtractNews"),
                null, null, null, postDataHelper, null).postData(info, "", "");
        //postDataHelper.post();
    }
}
