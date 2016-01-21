package cn.edu.njnu.infoextract.impl.activities.Tool;

import cn.edu.njnu.tools.ParameterHelper;

import java.io.IOException;

public class Tool_Rule_Store {

    String files_direction = new ParameterHelper().getPatternFile();

    public String content_div_rule() {
        //div id="content"
        return Tool_File.assemble_rule(files_direction + "content_div_rule");
    }

    public String content_p_rule() {
        return "p";
    }

    public String introduction_div_rule() {
        //ul class="search-events-list"
        return Tool_File.assemble_rule(files_direction + "introduction_div_rule");
    }

    public String introduction_clear_rule() {
        return Tool_File.assemble_rule(files_direction + "introduction_clear_rule");

    }

    public String introduction_em_rule() {
        return "em";
    }

    public String introduction_em_title_rule() {
        return "title";
    }

    public String introduction_span_rule() {
        return Tool_File.assemble_rule(files_direction + "introduction_span_rule");

    }

    public String introduction_list_div_rule() {
        // TODO Auto-generated method stub
        return Tool_File.assemble_rule(files_direction + "introduction_list_div_rule");
    }

    public String introduction_list_clear_rule() {
        // TODO Auto-generated method stub

        return "div[class~=(info*)]";
    }

    public String introduction_li_rule() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] key_Array_list_rule() {
        // TODO Auto-generated method stub
        String[] key_Array_list = {"时间", "地点", "分类"};
        return key_Array_list;
    }

    public String content_pic_rule() {
        // TODO Auto-generated method stub
        return "img[title~=屏幕快照]";
    }

}
