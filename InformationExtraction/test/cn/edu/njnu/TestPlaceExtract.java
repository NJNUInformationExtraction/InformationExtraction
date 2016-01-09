package cn.edu.njnu;

import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by zhangzhi on 16-1-7.
 * 测试地点上传接口
 */
public class TestPlaceExtract {

    @Test
    public void testPlaceExtract() {
        String title = "extractInformation";
        String desc = "北京创客空间";
        String abs = "测试数据上传接口";
        String url = "www.makerspace.com";
        String city = "北京";
        JSONObject other = new JSONObject();
        Assert.assertEquals(true, new PlacesExtract(null, null, null).
                postPlace(title, desc, abs, url, other, city));
    }

}
