package cn.edu.njnu.domain.ext;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.tools.Pair;

import java.io.*;

/**
 * Created by zhangzhi on 15-12-29.
 * 抽取数据单元之活动类
 */
public class Activity extends Extractable {

    @Override
    public void persistData(String outputFile, String url, boolean hasPost) throws IOException {
        File file = new File(outputFile + "activities");
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file, true), "UTF-8"))) {

            bw.write("**************" + url + "********************是否成功提交数据库:" + hasPost);
            bw.newLine();
            for (Pair pair : data) {
                bw.write(pair.key + ":" + pair.value);
                bw.newLine();
            }
            bw.newLine();
            System.out.println("抽取了一条Activity信息.");
        }
    }

}
