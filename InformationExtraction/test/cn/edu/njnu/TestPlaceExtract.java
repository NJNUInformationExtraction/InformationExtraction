package cn.edu.njnu;

import cn.edu.njnu.tools.ParameterHelper;
import cn.edu.njnu.tools.PostDataHelper;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

/**
 * Created by zhangzhi on 16-1-7.
 * 测试地点上传接口
 */
public class TestPlaceExtract {

    @Test
    public void testPlaceExtract() {
        File folder = new File("/home/zhangzhi/Documents/samples");
        PlacesExtract pe = new PlacesExtract("ss", new ParameterHelper().getOutputFile(),
                new HashMap<>());
        File[] list = folder.listFiles();
        if (list != null) {
            for (File file : list) {
                File[] sublist = file.listFiles();
                if (sublist != null)
                    pe.process(sublist, file.getName(), "北京");
            }
        }
    }

    @Test
    public void testActivityExtract() {
        File folder = new File("/home/zhangzhi/Documents/samples");
        ActivityExtract pe = new ActivityExtract("ss", new ParameterHelper().getOutputFile(),
                new HashMap<>(), new PostDataHelper(new HashMap<>()));
        File[] list = folder.listFiles();
        if (list != null) {
            for (File file : list) {
                File[] sublist = file.listFiles();
                if (sublist != null)
                    pe.process(sublist, file.getName(), "北京");
            }
        }
    }

}
