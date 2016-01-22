package cn.edu.njnu.infoextract;

import cn.edu.njnu.ExtractModule;
import cn.edu.njnu.PlacesExtract;
import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.impl.ExtractNews;
import cn.edu.njnu.infoextract.impl.ExtractProjects;
import cn.edu.njnu.infoextract.impl.activities.main_process.ExtractActivities;
import cn.edu.njnu.infoextract.impl.incubators.ExtractIncubators;
import cn.edu.njnu.tools.ParameterHelper;
import cn.edu.njnu.tools.PostDataHelper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhangzhi on 15-12-30.
 * 测试InfoExtract的四个实现类
 */
public class TestInfoExtract {

    @Test
    public void testExtractNews() {
        /*try {
            File folder = new File("/home/zhangzhi/Documents/folder");
            File[] list = folder.listFiles();
            if (list != null) {
                for (File file : list) {
                    String html = Main.getHtml(file);
                    InfoExtract ie = new ExtractNews();
                    List<Extractable> result = ie.extractInformation(html);
                    if (result != null) {
                        for (Extractable e : result)
                            e.persistData("/home/zhangzhi/Documents/folder2", "", true);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void testExtractActivities() {
        try {
            File file = new File("/home/zhangzhi/Documents/test.htm");
            String html = "";//ExtractModule.getHtml(file);
            InfoExtract ie = new ExtractActivities();
            List<Extractable> result = ie.extractInformation(html);
            if (result != null) {
                for (Extractable e : result)
                    e.persistData("/home/zhangzhi/Documents/", "", true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            File folder = new File("/home/zhangzhi/Documents/folder");
            File[] list = folder.listFiles();
            if (list != null) {
                for (File file : list) {
                    String html = ExtractModule.getHtml(file);
                    InfoExtract ie = new ExtractActivities();
                    List<Extractable> result = ie.extractInformation(html);
                    if (result != null) {
                        for (Extractable e : result)
                            e.persistData("/home/zhangzhi/Documents/", "", true);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void testExtractIncubators() {
        try {
            File folder = new File("/home/zhangzhi/Documents/folder");
            File[] list = folder.listFiles();

            ParameterHelper helper = new ParameterHelper();
            ConcurrentHashMap<String, String> IncubatorsToPid =
                    ExtractModule.loadPlaceToPId(helper.getIncubatorsPlaces());
            PlacesExtract pe = new PlacesExtract(helper.getRootFile(), helper.getOutputFile(),
                    IncubatorsToPid);

            if (list != null) {
                for (File file : list) {
                    //pe.process(file.listFiles(), "www.makerspace.com", "北京");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExtractProjects() {
        /*try {
            File folder = new File("/home/zhangzhi/Documents/folder");
            File[] list = folder.listFiles();
            if (list != null) {
                for (File file : list) {
                    String html = Main.getHtml(file);
                    InfoExtract ie = new ExtractProjects();
                    List<Extractable> result = ie.extractInformation(html);
                    if (result != null) {
                        for (Extractable e : result)
                            e.persistData("/home/zhangzhi/Documents/", "", true);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*try {
            File file = new File("/home/zhangzhi/Documents/test.htm");
            String html = Main.getHtml(file);
            InfoExtract ie = new ExtractProjects();
            List<Extractable> result = ie.extractInformation(html);
            if (result != null) {
                for (Extractable e : result)
                    e.persistData("/home/zhangzhi/Documents/", "", true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
