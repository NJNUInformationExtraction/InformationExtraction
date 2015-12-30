package cn.edu.njnu.infoextract.impl.activities.Basic_Class;

import java.util.HashMap;

public class Simple_info_HashMap {
    private Simple_info_HashMap() {
    }

    //    private static Simple_info_HashMap Simple_info_HashMap_single=null;
    private static HashMap<Simple_info, String> simple_info_HashMap_single = null;

    //静态工厂方法
    public static HashMap<Simple_info, String> getInstance() {
        if (simple_info_HashMap_single == null) {
            simple_info_HashMap_single = new HashMap<Simple_info, String>();
        }
        return simple_info_HashMap_single;
    }
}
