package cn.edu.njnu.infoextract.impl.activities.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Tool_File {

    public static void writeFile(String fileName, String content) {
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.write("\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readFolder(String File_Direction) {
        ArrayList<String> files = new ArrayList<>();
        File file = new File(File_Direction);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            int j = i + 1;
            if (!tempList[i].isDirectory()) {
                files.add(tempList[i].getPath());
            }
        }
        return files;
    }

    public static String assemble_rule(String path) {
        File file = new File(path);
        InputStreamReader read = null;
        try {
            read = new InputStreamReader(new FileInputStream(file), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }// 考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String result = "";
        String lineTxt = null;
        try {
            while ((lineTxt = bufferedReader.readLine()) != null) {//按行读取
                result += lineTxt;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            read.close();//关闭InputStreamReader
            bufferedReader.close();//关闭BufferedReader
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
