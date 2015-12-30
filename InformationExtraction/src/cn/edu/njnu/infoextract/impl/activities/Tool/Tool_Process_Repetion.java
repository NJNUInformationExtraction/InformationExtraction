package cn.edu.njnu.infoextract.impl.activities.Tool;

import cn.edu.njnu.infoextract.impl.activities.Basic_Class.Simple_info;

public class Tool_Process_Repetion {
    public void save_new_simple_info(String key, String value, Simple_info new_simple_info) {
        //Simple_info new_simple_info=new Simple_info();
        if (key.contains("标题")) {
            new_simple_info.setTitle(value);
        } else if (key.contains("时间")) {
            new_simple_info.setTime(value);

        } else if (key.contains("地址")) {
            new_simple_info.setAddress(value);

        }
        //return new_simple_info;
    }
}
