package cn.edu.njnu;

import cn.edu.njnu.tools.ParameterHelper;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangzhi on 16-1-9.
 * 测试地点与pid映射map的序列化与反序列化
 */
public class TestPlaceToPid {

    @Test
    public void testSerializePlaceToPid() {
        //try {
            //ParameterHelper helper = new ParameterHelper();
            //ConcurrentHashMap<String, String> placesToPid = Main.loadPlaceToPId(helper);
            //placesToPid.put("www.google.com", "123acb42bdff3");
            //placesToPid.put("www.youtube.com", "12a4d56bce90a");
            //placesToPid.put("www.flickr.com", "12379aac8dfb0");
            //Main.persisitPlaceToId(helper, placesToPid);
       // } catch (ClassNotFoundException | IOException e) {
        //    e.printStackTrace();
        //}
    }

    @Test
    public void testUnserializePlaceToPid() {
        //try {
         //   ParameterHelper helper = new ParameterHelper();
         //   ConcurrentHashMap<String, String> placesToPid = Main.loadPlaceToPId(helper);
         //   placesToPid.put("www.facebook.com", "123acb42bdff3");
         //   placesToPid.put("www.wikipedia.com", "12a4d56bce90a");
         //   placesToPid.put("www.github.com", "12379aac8dfb0");
            //Main.persisitPlaceToId(helper, placesToPid);
        //} catch (ClassNotFoundException | IOException e) {
        //    e.printStackTrace();
        //}
    }

}
