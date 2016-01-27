package cn.edu.njnu.infoextract.impl.activities.Active_Web_Extract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.njnu.domain.ext.Activity;
import cn.edu.njnu.tools.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.njnu.infoextract.impl.activities.Tool.Tool_Process_Repetion;
import cn.edu.njnu.infoextract.impl.activities.Tool.Tool_Rule_Store;
import cn.edu.njnu.infoextract.impl.activities.Basic_Class.Simple_info;
import cn.edu.njnu.infoextract.impl.activities.Basic_Class.Simple_info_HashMap;

public class Active_HTML_Extract {

    public static StringBuffer result = new StringBuffer();

    public Activity Active_Html_Extract(Element doc) {
        Activity active_result = new Activity();
        find_active_introduction(doc, active_result);
        find_content(doc, active_result);

        return active_result;

    }

    public Activity extract_some_actives_from_one_page(Document doc) {
        Activity activity_result = new Activity();
        //查找标签为<ul class="search-events-list>
        ArrayList<String> list_tags = new ArrayList<>();
        list_tags.add("ul[class~=(search-events-list*)]");
        for (int i = 0; i < list_tags.size(); i++) {
            String list_tag = list_tags.get(i);
            Elements content_list = doc.select(list_tag);
            //查找标签 <div class="info">
            Elements content_detail = content_list.select("div[class~=(info*)]");
            //System.out.println(content_detail.text());
        }
        return activity_result;
    }

    /**
     * @return ture is only one activity ;false :some activity
     */
    public boolean Judge_Html_List(Element doc) {
        //查找标签为div class="media-body" id="event-info"

        Tool_Rule_Store rule_story_tool = new Tool_Rule_Store();
        String list_rule_div = rule_story_tool.introduction_list_div_rule();
        Elements introduction_list_div = doc.select(list_rule_div);
        if (!introduction_list_div.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @param doc 提取web中活动的内容
     */
    private static void find_active_introduction(Document doc) {
        //查找标签为div class="media-body" id="event-info"
        Tool_Rule_Store rule_story_tool = new Tool_Rule_Store();
        String rule_div = rule_story_tool.introduction_div_rule();
        Elements introduction_div = doc.select(rule_div);
//		System.out.println("div h2:gt(1) is  "+introduction_div.select("div:has(h2),div:has(h1)"));
        String rule_clear_tag = rule_story_tool.introduction_clear_rule();
        Elements introduction_clear_div = introduction_div.select(rule_clear_tag);
        for (Element one_introduction_div : introduction_clear_div) {
            //System.out.println("------process leaf node-------------");
            //System.out.println(one_introduction_div.val());
            process_leaf_node(one_introduction_div);
            //System.out.println("------process leaf node--  end!-----------");
        }
    }

    private static void find_active_introduction(Element doc, Activity activity_result) {
        //查找标签为div class="media-body" id="event-info"
        Tool_Rule_Store rule_story_tool = new Tool_Rule_Store();
        String rule_div = rule_story_tool.introduction_div_rule();
        Elements introduction_div = doc.select(rule_div);
//		System.out.println("div h2:gt(1) is  "+introduction_div.select("div:has(h2),div:has(h1)"));
        String rule_clear_tag = rule_story_tool.introduction_clear_rule();
        Elements introduction_clear_div = introduction_div.select(rule_clear_tag);
        boolean is_find_title=false;
        for (Element one_introduction_div : introduction_clear_div) {
            //System.out.println("------process leaf node-------------");
            //System.out.println(one_introduction_div.val());
            if(one_introduction_div.tagName()=="div"){
            process_leaf_node(one_introduction_div, activity_result);
            }
            else if(one_introduction_div.tagName()=="blockquote"){
            	if(!is_find_title)
            	{process_blockquote_title(doc,activity_result);
            		is_find_title=true;
            	}
            	process_blockquote_node(one_introduction_div,activity_result);
            }
            //System.out.println("------process leaf node--  end!-----------");
        }
    }

    private static void process_blockquote_title(Element doc,
			Activity activity_result) {
		// TODO Auto-generated method stub
		Elements titles=doc.select("div[class=title]");
		if(!titles.isEmpty()){
		String key="活动标题";
		String value=titles.get(0).text();
		Pair<String, String> new_atom = new Pair<>("", "");
		new_atom.key = key;
		new_atom.value =value;
		activity_result.put(new_atom);
		}
	}

	/**
     * @param doc 提取web中活动的内容
     */
    private static void find_content(Document doc) {
        //Elements content_div=doc.select("div[class~=(article*)],div[id=link-report]");
        //System.out.println("#####内容的div："+content_div.text());
        //id="event_desc_page"
        //查找标签为<div id=event_desc_page> <div class="mod" id="link-report">
        Tool_Rule_Store rule_store_tool = new Tool_Rule_Store();
        String div_rule = rule_store_tool.content_div_rule();
        Elements content_detail = doc.select(div_rule);
        //System.out.println("有P标签的文字"+p_tag_elements.text());

        String p_rule = rule_store_tool.content_p_rule();
        Elements p_tag_elements = content_detail.select(p_rule);
        result.append("活动内容：\n");
        if (!p_tag_elements.isEmpty()) {
            for (Element p_element : p_tag_elements) {
                if (!p_element.text().trim().equals("")) {
                    result.append(p_element.text() + "\n");
                }
            }
        } else {
            String[] content_splits = content_detail.text().split("  ");
            for (String one_line : content_splits)
                result.append(one_line + "\n");
        }
        result.append("\n\n");
    }

    private static void find_content(Element doc, Activity activity_result) {
        //查找标签为<div id=event_desc_page> <div class="mod" id="link-report">
        Pair<String, String> content_atom = new Pair<>("", "");
        Pair<String, String> pic_atom = new Pair<>("", "");
        
        Tool_Rule_Store rule_store_tool = new Tool_Rule_Store();
        String div_rule = rule_store_tool.content_div_rule();
        String activity_key;
        String activity_value = "";
        Elements content_detail = doc.select(div_rule);
        String p_rule = rule_store_tool.content_p_rule();
        String pic_rule = rule_store_tool.content_pic_rule();
        
        Elements p_tag_elements = content_detail.select(p_rule);
        Elements pic=content_detail.select(pic_rule);
        if(!pic.isEmpty()){
        	Element one_pic=pic.get(0);
        	activity_key="图片地址";
        	activity_value=one_pic.attr("src");
            pic_atom.key = activity_key;
            pic_atom.value = activity_value;
            //System.out.println(activity_key+activity_value);
            activity_result.put(pic_atom);
            activity_value="";
        }

        activity_key = "活动内容";
        result.append("活动内容：\n");
        //如果包含在p标签内的
        if (!p_tag_elements.isEmpty()) {
        	int count=1;
        	while(count<5&&count<p_tag_elements.size())
        	{
        		Element p_element=p_tag_elements.get(count);
        		if (!p_element.text().trim().equals("")) {
                    activity_value += p_element.text();
                    result.append(p_element.text() + "\n");
                }
        		count++;
        	}
//            for (Element p_element :p_tag_elements) {
//                if (!p_element.text().trim().equals("")) {
//                    activity_value += p_element.text();
//                    result.append(p_element.text() + "\n");
//                }
//            }
        }
        //没有P标签的处理
        else {
            String[] content_splits = content_detail.text().split(" ");
            int count=1;
            while(count<5&&count<content_splits.length)
            {
            	activity_value += content_splits[count];
            	count++;
            }
//            for (String one_line : content_splits) {
//                result.append(one_line + "\n");
//                int count=1;
//                if(count<5){
//                activity_value += one_line;
//                count++;
//                }else {
//                	break;
//                }
//            }
        }
        if(!activity_value.isEmpty()){
        content_atom.key = activity_key;
        content_atom.value = activity_value;
        
        activity_result.put(content_atom);
        result.append("\n\n");
        }
    }

    public static void traver(Element root, int depth) {
        if (root == null) {
            return;
        }
        depth++;
        Elements elements_in_body = root.children();

        for (Element element : elements_in_body) {
            String element_text = element.text();
            if (reg_match(".+年.*月.*日|.*时间:", element_text) && reg_match(".*北京海淀.*|.*地点|.*广州.*|.*西安.*|.*杭州.*|.*北京.*", element_text)) {
                //System.out.println(element.tagName());
                if (element.tagName().equals("p") | element.tagName().equals("div")) {
                    //System.out.println("element  is " + depth + element.text());
                    //System.out.println("  element father is " + element.parent().text());
                    Elements siblings = element.siblingElements();
                    if (siblings.size() == 1) {
                        //System.out.println("######### perfect #############");
                        process_leaf_node(element);
                    }
                    for (Element sibling_element : siblings) {
                        //System.out.println("	element sbling is " + sibling_element.text());
                    }
                }
            }
            traver(element, depth);
        }
    }

    /**
     * @param pattern ".*年.*月.*日"
     * @param matcher 2015年10月19日 13:00 ～ 2015年10月19日 17:00
     * @return 匹配是否成功
     */
    public static boolean reg_match(String pattern, String matcher) {
        Pattern p = Pattern.compile(pattern);//".*年.*月.*日"
        Matcher m = p.matcher(matcher);//"2015年10月19日 13:00 ～ 2015年10月19日 17:00"
        ArrayList<String> strs = new ArrayList<String>();
        return m.find();
    }

    //根据获取到的叶子，进行不同的处理
    public static void process_leaf_node(Element div_leaf) {
        HashMap<Simple_info, String> simple_info_hashmap_single = Simple_info_HashMap.getInstance();
        Simple_info new_simple_info = new Simple_info();
        Tool_Rule_Store rule_store_tool = new Tool_Rule_Store();
        Elements extract_element = div_leaf.children();
        //System.out.println("------找到所在的div之后，输出孩子的信息------");

        for (Element one_element : extract_element) {
            //System.out.println(one_element);
            String active_key = null;
            if (one_element.tagName().equals("h2") | one_element.tagName().equals("h1") | one_element.className().equals("event-title")) {
                active_key = "活动标题";
                new_simple_info.setTitle(one_element.text());
            } else {
                String em = rule_store_tool.introduction_em_rule();
                String em_title = rule_store_tool.introduction_em_title_rule();
                active_key = one_element.select(em).attr(em_title);
                if (active_key.equals("")) {
                    String span_rule = rule_store_tool.introduction_span_rule();
                    active_key = one_element.select(span_rule).text();
                    one_element.select(span_rule).remove();
                    //System.out.println("p1" + one_element.select("span").text());
                }
                if (active_key.trim().equals("")) {
                    active_key = "其他";
                }

            }
            String active_value = one_element.text();
            if (active_key.contains("时间")) {
                new_simple_info.setTime(active_value);
            } else if (active_key.contains("地点")) {
                new_simple_info.setAddress(active_value);
            }
            result.append(active_key + "  " + active_value + "\n");

        }
        simple_info_hashmap_single.put(new_simple_info, "single_activity");
        //System.out.println("输出孩子节点结束！");

    }

    public static void process_blockquote_node(Element blockquote_leaf, Activity activity_result) {
    	process_elements_in_a_div(blockquote_leaf,activity_result);
    }
    public static void process_leaf_node(Element div_leaf, Activity activity_result) {
        HashMap<Simple_info, String> simple_info_hashmap_single = Simple_info_HashMap.getInstance();
        Simple_info new_simple_info = new Simple_info();
        Tool_Rule_Store rule_store_tool = new Tool_Rule_Store();
        Elements extract_element = div_leaf.children();
        //System.out.println("------找到所在的div之后，输出孩子的信息------");
        for (Element one_element : extract_element) {
            Pair<String, String> new_atom = new Pair<>("", "");
//            System.out.println(one_element);
            String active_key = null;
//            System.out.println(one_element.attr("class"));
            if (one_element.tagName().equals("h2") 
            		| one_element.tagName().equals("h1") 
            		| one_element.className().equals("event-title")
            		| one_element.attr("class").equals("title")) { //judge <div class=title> title </div>
            	
                active_key = "活动标题";
                new_simple_info.setTitle(one_element.text());
            } else {
                String em = rule_store_tool.introduction_em_rule();
                String em_title = rule_store_tool.introduction_em_title_rule();
                active_key = one_element.select(em).attr(em_title);
                if (active_key.equals("")) {
                	
                    String span_rule = rule_store_tool.introduction_span_rule();
                    active_key = one_element.select(span_rule).text();
                    one_element.select(span_rule).remove();

                   // System.out.println("p1" + one_element.select("span").text());
                }
                if (active_key.trim().equals("")) {
                    active_key = "其他";
                }

            }
            
            if(!active_key.equals("其他"))
            {
            String active_value = one_element.text();

            if (active_key.contains("时间")) {
                new_simple_info.setTime(active_value);
            } else if (active_key.contains("地点")) {
                new_simple_info.setAddress(active_value);
            }
            //result.append(active_key + "  " + active_value + "\n");
            new_atom.key = active_key;
            new_atom.value = active_value;
            activity_result.put(new_atom);
        }
        simple_info_hashmap_single.put(new_simple_info, "single_activity");
        }
        //System.out.println("输出孩子节点结束！");

    }

    private static void process_elements_in_a_div(Element one_element,
			Activity activity_result) {
//      process 
//      <p><strong>活动时间:</strong>2013年05月21日 [周二] 14:00-17:00</p>
//      <p><strong>活动地点:</strong>北京-海淀区中关村鼎好大厦A座8层</p>
//      <p><strong>参与费用:</strong>0元</p>

		Elements child_elments=one_element.children();
		for(Element child:child_elments){
			String[] split_child=child.text().split(":");
			if(split_child.length==1){
				split_child=child.text().split("：");
			}
			if(split_child.length>1){
				String key=split_child[0];
				if(key.length()>5){
					continue;
				}
				String value="";
				if(split_child.length>2){
					for(int i=1;i<=split_child.length-1;i++){
						value=value+":"+split_child[i];
					}
					value=value+":"+split_child[split_child.length-1];
				}
				else {
					value=split_child[1];
				}
				Pair<String, String> new_atom = new Pair<>("", "");
				new_atom.key = key;
				new_atom.value =value;
				activity_result.put(new_atom);
			}
			
			
		}
		
    	
	}

	public ArrayList<Activity> Some_Active_Html_Extract(Element doc) {
        // TODO Auto-generated method stub

        //System.out.println("start processed some_activiety webpage");
        ArrayList<Activity> some_activity = new ArrayList<Activity>();
        //查找标签为div class="media-body" id="event-info"
        Tool_Rule_Store rule_story_tool = new Tool_Rule_Store();
        String list_rule_div = rule_story_tool.introduction_list_div_rule();
        Elements introduction_list_div = doc.select(list_rule_div);

        String rule_clear_tag = rule_story_tool.introduction_list_clear_rule();
        //li class="search-list-entry"
        Elements introduction_clear_div = introduction_list_div.select(rule_clear_tag);

        for (Element one_introduction_div : introduction_clear_div) {
            Activity one_activity = new Activity();
            //System.out.println("------process leaf node-------------");
            //System.out.println(one_introduction_div.val());
            process_some_activity_leaf_node(one_introduction_div, one_activity);
            //System.out.println("------process leaf node--  end!-----------");
            some_activity.add(one_activity);
        }
        return some_activity;
    }

    private void process_some_activity_leaf_node(Element div_leaf) {
        // TODO Auto-generated method stub
        Simple_info new_simple_info = new Simple_info();
        Tool_Process_Repetion process_repetion_tool = new Tool_Process_Repetion();
        HashMap<Simple_info, String> Simple_info_hashmap = Simple_info_HashMap.getInstance();
        Tool_Rule_Store rule_store_tool = new Tool_Rule_Store();
        Elements extract_element = div_leaf.children();
        //System.out.println("------找到所在的div之后，输出孩子的信息------");
        for (Element one_element : extract_element) {
            //System.out.println(one_element);
            String active_key = null;
            String active_value = null;
            if (one_element.tagName().equals("h2") | one_element.tagName().equals("h1") | one_element.className().equals("event-title")) {
                active_key = "活动标题";
                process_repetion_tool.save_new_simple_info(active_key, one_element.text(), new_simple_info);
                result.append(active_key + "  " + one_element.text() + "\n");
            } else {
                Elements li_list = one_element.select("li[class=search-list-item]");
                for (Element one_li : li_list) {
                    String one_li_value = one_li.text();

                    //String[] key_Array={"时间：","地点","分类"};
                    String[] key_Array = rule_store_tool.key_Array_list_rule();
                    if (one_li_value.contains("|")) {
                        String[] one_li_value_split = one_li_value.split("\\|");
                        for (int i = 0; i < one_li_value_split.length; i++) {
                            active_key = key_Array[i];
                            active_value = one_li_value_split[i];
                            process_repetion_tool.save_new_simple_info(active_key, active_value, new_simple_info);
                            result.append(active_key + "  " + active_value + "\n");
                        }
                        continue;
                    } else if (one_li.text().contains("：")) {
                        String[] one_li_split = one_li.text().split("：");

                        active_key = one_li_split[0];
                        active_value = one_li_split[1];
                    } else {
                        active_key = "其他";
                        active_value = one_li.text();
                    }
                    result.append(active_key + "  " + active_value + "\n");
                }
            }//else
            if (Simple_info_hashmap.containsKey(new_simple_info)) {
                //System.out.println("是重复元素");
            }
        }

    }

    private void process_some_activity_leaf_node(Element div_leaf, Activity activity_result) {
        // TODO Auto-generated method stub
        Simple_info new_simple_info = new Simple_info();
        Tool_Process_Repetion process_repetion_tool = new Tool_Process_Repetion();
        HashMap<Simple_info, String> Simple_info_hashmap = Simple_info_HashMap.getInstance();
        Tool_Rule_Store rule_store_tool = new Tool_Rule_Store();
        Elements extract_element = div_leaf.children();
        //System.out.println("------找到所在的div之后，输出孩子的信息------");
        for (Element one_element : extract_element) {
            //System.out.println(one_element);
            Pair<String, String> new_atom = new Pair<>("", "");
            String active_key = null;
            String active_value = null;
            if (one_element.tagName().equals("h2") | one_element.tagName().equals("h1") | one_element.className().equals("event-title")) {
                active_key = "活动标题";
                active_value = one_element.text();
                new_atom.key = active_key;
                new_atom.value = active_value;
                activity_result.put(new_atom);
                process_repetion_tool.save_new_simple_info(active_key, one_element.text(), new_simple_info);
                result.append(active_key + "  " + one_element.text() + "\n");
            } else {
                Elements li_list = one_element.select("li[class=search-list-item]");
                for (Element one_li : li_list) {
                    String one_li_value = one_li.text();

                    //String[] key_Array={"时间：","地点","分类"};
                    String[] key_Array = rule_store_tool.key_Array_list_rule();
                    if (one_li_value.contains("|")) {
                        String[] one_li_value_split = one_li_value.split("\\|");
                        for (int i = 0; i < one_li_value_split.length; i++) {
                            Pair<String, String> activity_atom = new Pair<>("", "");
                            active_key = key_Array[i];
                            active_value = one_li_value_split[i];
                            activity_atom.key = active_key;
                            activity_atom.value = active_value;
                            activity_result.put(activity_atom);
                            process_repetion_tool.save_new_simple_info(active_key, active_value, new_simple_info);
                            result.append(active_key + "  " + active_value + "\n");
                        }
                        continue;
                    } else if (one_li.text().contains("：")) {
                        String[] one_li_split = one_li.text().split("：");

                        active_key = one_li_split[0];
                        active_value = one_li_split[1];
                        Pair<String, String> activity_atom = new Pair<>("", "");
                        activity_atom.key = active_key;
                        activity_atom.value = active_value;
                        activity_result.put(activity_atom);
                    } else {
                        active_key = "其他";
                        active_value = one_li.text();
                        Pair<String, String> activity_atom = new Pair<>("", "");
                        activity_atom.key = active_key;
                        activity_atom.value = active_value;
                        activity_result.put(activity_atom);
                    }
                    result.append(active_key + "  " + active_value + "\n");
                }
            }//else
            if (Simple_info_hashmap.containsKey(new_simple_info)) {
                //System.out.println("是重复元素");
            }
        }

    }

}
