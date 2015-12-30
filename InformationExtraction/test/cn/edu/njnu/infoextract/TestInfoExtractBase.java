package cn.edu.njnu.infoextract;

import cn.edu.njnu.infoextract.impl.ExtractNews;
import cn.edu.njnu.tidypage.TidyPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhangzhi on 15-12-21.
 * 测试InfoExtract抽象类下面的方法
 */
public class TestInfoExtractBase {

    private InfoExtract ie = new ExtractNews();

    @BeforeClass
    public static void initialize() {
    }

    @Test
    public void testCanBeTime() {
        //System.out.print(ie.canBeTime("199"));
        //Assert.assertEquals(ie.canBeTime("199"), true);
        Assert.assertEquals(false, ie.canBeTime("centoscddczxc"));
    }

    @Test
    public void testCanBePlace() throws IOException {
        Assert.assertEquals(true, ie.canBePlace("北京创客空间", "北京"));
    }

}
