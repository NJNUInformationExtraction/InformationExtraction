package cn.edu.njnu;

import cn.edu.njnu.tools.Pair;
import cn.edu.njnu.tools.PostDataHelper;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

/**
 * Created by zhangzhi on 16-1-12.
 * 测试页面第一行的标记是否能够被匹配
 */
public class TestPageFlag {

    @Test
    public void testPageFlag() {
        new ProcessUnit(
                new Pair<>("news", "cn.edu.njnu.infoextract.impl.ExtractNews"),
                null, null, null, new PostDataHelper(new HashMap<>()), null)
                .process(new File("/home/zhangzhi/Documents/data"), "www.makerspace.com");
    }

}
