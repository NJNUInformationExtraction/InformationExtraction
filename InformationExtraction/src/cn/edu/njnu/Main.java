package cn.edu.njnu;

import cn.edu.njnu.tools.Pair;
import cn.edu.njnu.tools.ParameterGetter;

import java.io.*;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        try {
            //从ParameterGetter中获取配置文件的配置参数
            ParameterGetter helper = new ParameterGetter();
            //调度线程池
            ExecutorService service = Executors.newFixedThreadPool(helper.getPoolsize());
            //向线程池提交任务
            for (Pair<String, String> pair : helper)
                service.submit(new ProcessUnit(pair, new File(helper.getRootFile())));
            service.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
