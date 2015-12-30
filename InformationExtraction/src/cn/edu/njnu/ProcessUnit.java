package cn.edu.njnu;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.tools.Pair;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Zhi on 12/28/2015.
 * 线程池的调度单元
 */
public class ProcessUnit implements Runnable {

    //页面存放的根目录
    protected File baseFile;

    //特定类型网页所在的文件夹名
    protected String folderName;

    //所需要使用的信息抽取实例
    protected InfoExtract ie;

    /**
     * 构造器
     *
     * @param config 用于获得目标文件夹与信息抽取实例
     */
    public ProcessUnit(Pair<String, String> config, File file) {
        try {
            this.baseFile = file;
            this.folderName = config.key;
            this.ie = (InfoExtract) Class.forName(config.value).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从指定文件获得内容
     *
     * @param file 指定的文件
     * @return 文件的内容
     */
    protected String getHtml(File file) {
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

    /**
     * 便于stream api使用方法引用特性,将处理过程封装
     *
     * @param f 待分析的页面文件
     */
    protected void process(File f) {
        String html = getHtml(f);
        List<Extractable> info = ie.extractInformation(html);
        if (info != null) {
            info.forEach(extraction -> {
                try {
                    extraction.persistData(f.getName().replaceAll(".html|.htm", ""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 递归搜索每个文件夹,找到目标文件夹作处理过程
     *
     * @param current 当前的文件夹
     */
    protected void searchForTarget(File current) {
        File[] list = current.listFiles();
        if (list != null && current.getName().equals(folderName))
            Arrays.stream(list).parallel().forEach(this::process);
        else if (list != null && list[0].isFile())
            return;
        else if (list != null)
            Arrays.stream(list).forEach(this::searchForTarget);
    }

    @Override
    public void run() {
        searchForTarget(baseFile);
    }

}
