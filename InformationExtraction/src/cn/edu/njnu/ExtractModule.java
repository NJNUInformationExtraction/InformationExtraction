package cn.edu.njnu;

import cn.edu.njnu.tools.Pair;
import cn.edu.njnu.tools.ParameterHelper;
import cn.edu.njnu.tools.PostDataHelper;

import java.io.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExtractModule {

    public static void main(String[] args) {
        try {
            //从ParameterGetter中获取配置文件的配置参数
            ParameterHelper helper = new ParameterHelper();

            //加载地点与pid的映射文件
            ConcurrentHashMap<String, String> IncubatorsToPid =
                    loadPlaceToPId(helper.getIncubatorsPlaces());
            ConcurrentHashMap<String, String> ActivitiesToPid =
                    loadPlaceToPId(helper.getActivitiesPlaces());
            wipeLastLog(helper.getOutputFile());

            //上传数据工具类
            PostDataHelper postDataHelper = new PostDataHelper(IncubatorsToPid);

            //加载新地点信息
            new PlacesExtract(helper.getRootFile(), helper.getOutputFile(),
                    IncubatorsToPid).run();
            new ActivityExtract(helper.getRootFile(), helper.getOutputFile(),
                    ActivitiesToPid, postDataHelper).run();

            //向线程池提交任务
            ExecutorService handlePage = Executors.newFixedThreadPool(helper.getPoolsize());
            CountDownLatch latch = new CountDownLatch(helper.getPoolsize());

            Iterator<Pair<String, String>> iterator = helper.iterator();
            for (int i = 0; i < helper.getPoolsize() - 1; i++) {
                handlePage.submit(new ProcessUnit
                        (iterator.next(), new File(helper.getRootFile()), helper.getOutputFile(),
                                IncubatorsToPid, postDataHelper, latch));
            }
            handlePage.submit(new ProcessUnit
                    (iterator.next(), new File(helper.getRootFile()), helper.getOutputFile(),
                            ActivitiesToPid, postDataHelper, latch));

            handlePage.shutdown();
            latch.await();

            //写入地点与pid映射文件
            persisitPlaceToId(helper.getIncubatorsPlaces(), IncubatorsToPid);
            persisitPlaceToId(helper.getActivitiesPlaces(), ActivitiesToPid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载地点与pid的映射文件
     *
     * @param place 参数获得类
     * @return 地点到pid的映射map
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static ConcurrentHashMap<String, String> loadPlaceToPId(String place)
            throws IOException, ClassNotFoundException {
        File placesFile = new File(place);
        if (placesFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(placesFile));
            return (ConcurrentHashMap<String, String>) ois.readObject();
        } else
            return new ConcurrentHashMap<>();
    }

    /**
     * 写入地点与pid映射文件
     *
     * @param place       参数获得类
     * @param placesToPid 地点到pid的映射map
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static void persisitPlaceToId
    (String place, ConcurrentHashMap<String, String> placesToPid)
            throws IOException, ClassNotFoundException {
        File placesFile = new File(place);
        if (!placesFile.exists())
            placesFile.createNewFile();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(placesFile));
        oos.writeObject(placesToPid);
    }

    /**
     * 清理上次的日志
     *
     * @param outputFile 输出根目录
     */
    public static void wipeLastLog(String outputFile) {
        File root = new File(outputFile);
        if (root.exists()) {
            File[] files = root.listFiles();
            if (files != null) {
                for (File file : files) {
                    try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(file, false), "UTF-8"))) {
                        bw.write("");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

/*
    public static String getHtml(File file) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), "UTF-8"))) {
            StringBuilder sb = new StringBuilder();
            String buffer;
            while ((buffer = br.readLine()) != null)
                sb.append(buffer);
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
*/
}
