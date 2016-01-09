package cn.edu.njnu.infoextract.impl.activities.Tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Tool_File {

    public static ArrayList<String> readFolder(String File_Direction) {
        ArrayList<String> files = new ArrayList<String>();
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
