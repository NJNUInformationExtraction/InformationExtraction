package cn.edu.njnu.tools;

import org.junit.Test;

/**
 * Created by zhangzhi on 15-12-29.
 * 测试配置文件解析工具
 */
public class TestParameterHelper {

    @Test
    public void testParameterHelper() {
        ParameterHelper helper = new ParameterHelper();
        System.out.println(helper.getPoolsize());
        System.out.println(helper.getRootFile());
        for (Pair<String, String> pair : helper) {
            System.out.println(pair.key + " : " + pair.value);
        }
    }

}
