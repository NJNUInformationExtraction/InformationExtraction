package cn.edu.njnu.tools;

import org.junit.Test;

import java.io.File;

/**
 * Created by zhangzhi on 15-12-29.
 * 测试配置文件解析工具
 */
public class TestParameterGetter {

    @Test
    public void testParameterGetter() {
        ParameterGetter helper = new ParameterGetter();
        System.out.println(helper.getPoolsize());
        System.out.println(helper.getRootFile());
        for (Pair<String, String> pair : helper) {
            System.out.println(pair.key + " : " + pair.value);
        }
    }

}
