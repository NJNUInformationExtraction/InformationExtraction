package cn.edu.njnu.tidypage;

import org.junit.Test;

/**
 * Created by zhangzhi on 16-1-17.
 */
public class TestTidyURL {

    @Test
    public void testURLTidy() {
        System.out.print(new TidyPage("").tidyURL("www.baidu.com"));
    }

}
