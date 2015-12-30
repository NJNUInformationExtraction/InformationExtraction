package cn.edu.njnu.infoextract.impl.activities.Tool;

public class Tool_Rule_Store {

    public String content_div_rule() {
        //div id="content"
        return "div[id=event_desc_page],div[id=link-report],div[id=content]";
    }

    public String content_p_rule() {
        return "p";
    }

    public String introduction_div_rule() {
        //ul class="search-events-list"
        return "div[class~=(media-body*)],div[class~=(event-info*)],div[class~=(post-titles entry-header*)]";
    }

    public String introduction_clear_rule() {
        return "div:has(h2),div:has(h1)";
    }

    public String introduction_em_rule() {
        return "em";
    }

    public String introduction_em_title_rule() {
        return "title";
    }

    public String introduction_span_rule() {
        return "span[class~=(p1*)]";
    }

    public String introduction_list_div_rule() {
        // TODO Auto-generated method stub
        return "ul[class~=(search-events-list*)]";
    }

    public String introduction_list_clear_rule() {
        // TODO Auto-generated method stub
        //li class="search-list-entry"
//		return "li[class~=(search-list-entry*)]";
//		div class="info"
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

}
