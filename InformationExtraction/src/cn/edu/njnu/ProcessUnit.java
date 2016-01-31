package cn.edu.njnu;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.infoextract.InfoExtract;
import cn.edu.njnu.tools.Pair;
import cn.edu.njnu.tools.PostDataHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

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

    //抽取数据本地输出目录路径
    protected String outputFile;

    //地点与pid的映射
    protected Map<String, String> placeToPid;

    //批量上传的帮助类
    protected PostDataHelper postDataHelper;

    //用于线程同步的锁存器
    protected CountDownLatch latch;

    /**
     * 构造器
     *
     * @param config 用于获得目标文件夹与信息抽取实例
     */
    public ProcessUnit(Pair<String, String> config, File file, String outputFile,
                       Map<String, String> placeToPid,
                       PostDataHelper postDataHelper, CountDownLatch latch) {
        try {
            this.outputFile = outputFile;
            this.baseFile = file;
            this.folderName = config.key;
            this.placeToPid = placeToPid;
            this.postDataHelper = postDataHelper;
            this.latch = latch;
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
     * 将标记过的内容重新写入文件
     *
     * @param file 待写入的文件
     * @param html 标记过的内容
     */
    protected void writeHtml(File file, String html) {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"))) {
            bw.write(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传数据接口
     *
     * @param info 待上传的数据
     */
    protected void postData(List<Extractable> info, String url, String place) {
        JSONArray array = new JSONArray();
        for (Extractable extractable : info) {
            String title = "";
            String time = "2015.12.21";
            String content = "暂无";
            String pic = "";
            JSONObject other = new JSONObject();
            for (Pair<String, String> pair : extractable) {
                if (pair.key.contains("标题"))
                    title = pair.value;
                else if (pair.key.contains("时间"))
                    time = pair.value;
                else if (pair.key.contains("内容"))
                    content = pair.value;
                else if (pair.key.contains("图片"))
                    pic = pair.value;
                else
                    other.put(pair.key, pair.value);
            }
            try {
                if (title.equals("")) {
                    extractable.persistData(outputFile, url, false);
                } else {
                    String pid = placeToPid.get(place);
                    if (pid == null)
                        return;
                    else if (pid.startsWith("{")) {
                        pid = postDataHelper.postIncubator(pid);
                        if (pid == null)
                            return;
                        else
                            placeToPid.put(place, pid);
                    }
                    JSONObject item = new JSONObject();
                    item.put("title", title);
                    item.put("type", ie.getType());
                    item.put("time", time);
                    item.put("content", content);
                    item.put("pid", pid);
                    item.put("pic", pic);
                    item.put("other", other);
                    array.put(item);
                    JSONObject json = new JSONObject();
                    json.put("acs", array);
                    boolean hasPosted = postDataHelper.postContent(json);
                    extractable.persistData(outputFile, url, hasPosted);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 便于stream api使用方法引用特性,将处理过程封装
     *
     * @param f 待分析的页面文件
     */
    protected void process(File f, String place) {
        String html = getHtml(f);

        // 检测html开头是否有标记flag,若没有则说明该页面没有被访问过;
        // 若有则说明已访问过,跳过之;
        int index = 0;
        String url;
        while (html.charAt(index) != '<')
            index++;
        String head = html.substring(0, index);
        if (head.startsWith("flag"))
            return;
        else {
            url = html.substring(0, index);
            html = "flag " + html;
        }
        // 将标记flag的html重新写入页面文件中
        writeHtml(f, html);

        List<Extractable> info = ie.extractInformation(html);
        if (info != null && info.size() > 0) {
            postData(info, url, place);
        }
    }

    /**
     * 递归搜索每个文件夹,找到目标文件夹作处理过程
     *
     * @param current 当前的文件夹
     */
    protected void searchForTarget(File current) {
        File[] list = current.listFiles();
        if (list != null) {
            if (current.getName().equals(folderName)) {
                String place = current.getParentFile().getName();
                for (File file : list)
                    process(file, place);
            } else {
                if (list.length == 0)
                    return;
                else if (list[0].isFile())
                    return;
                Arrays.stream(list).forEach(this::searchForTarget);
            }
        }
    }

    @Override
    public void run() {
        searchForTarget(baseFile);
        latch.countDown();
    }

}
