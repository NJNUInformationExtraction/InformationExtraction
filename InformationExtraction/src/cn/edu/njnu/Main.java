package cn.edu.njnu;

import cn.edu.njnu.tools.Pair;
import cn.edu.njnu.tools.ParameterHelper;

import java.io.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        try {
            //从ParameterGetter中获取配置文件的配置参数
            ParameterHelper helper = new ParameterHelper();

            //加载地点与pid的映射文件
            ConcurrentHashMap<String, String> placesToPid = Main.loadPlaceToId(helper);

            //加载地点信息
            ExecutorService handlePlace = Executors.newFixedThreadPool(1);
            handlePlace.submit(new PlacesExtract
                    (helper.getRootFile(), helper.getOutputFile(), placesToPid));
            handlePlace.shutdown();

            //调度线程池
            ExecutorService handlePage = Executors.newFixedThreadPool(helper.getPoolsize());
            //向线程池提交任务
            for (Pair<String, String> pair : helper)
                handlePage.submit(new ProcessUnit
                        (pair, new File(helper.getRootFile()), helper.getOutputFile(), placesToPid));
            handlePage.shutdown();

            //写入地点与pid映射文件
            persisitPlaceToId(helper, placesToPid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载地点与pid的映射文件
     *
     * @param helper 参数获得类
     * @return 地点到pid的映射map
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static ConcurrentHashMap<String, String> loadPlaceToId(ParameterHelper helper)
            throws IOException, ClassNotFoundException {
        File placesFile = new File(helper.getPlaces());
        if (placesFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(placesFile));
            return (ConcurrentHashMap<String, String>) ois.readObject();
        } else
            return new ConcurrentHashMap<>();
    }

    /**
     * 写入地点与pid映射文件
     *
     * @param helper      参数获得类
     * @param placesToPid 地点到pid的映射map
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static void persisitPlaceToId
    (ParameterHelper helper, ConcurrentHashMap<String, String> placesToPid)
            throws IOException, ClassNotFoundException {
        File placesFile = new File(helper.getPlaces());
        if (!placesFile.exists())
            placesFile.createNewFile();
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(placesFile));
        oos.writeObject(placesToPid);
    }

}
