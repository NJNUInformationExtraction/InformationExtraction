package cn.edu.njnu.infoextract.impl.incubators;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.njnu.domain.Extractable;
import cn.edu.njnu.domain.ext.Incubator;
import cn.edu.njnu.infoextract.InfoExtract;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

/**
 * Created by luotianyao on 15-12-29. 孵化器类型页面抽取实现类
 */
public class ExtractIncubators extends InfoExtract {

	@Override
	public List<Extractable> extractInformation(String html) {
		String safe = Jsoup.clean(html, Whitelist.relaxed().addAttributes(":all", "class"));
		Element root = Jsoup.parse(safe);
		Incubator info = new Incubator();
		List<Extractable> result = new ArrayList<>();
		while (true) {
			if (Extract(root, info) == false)
				break;
		}
		result.add(info);
		return result;
	}

	@Override
	public String getType() {
		return "新闻资讯";
	}

	public static boolean Extract(Element root, Incubator Info) {
		String s_value = "";
		String s_key = "";
		String s_tag = "";
		boolean esuc = false;// 标志是否抽取到信息
		boolean kflag = false;
		boolean vflag = false;
		/**
		 * 抽取过程分2步
		 *
		 * 1.只抽value 2.抽key，value对应的
		 *
		 */
		/**
		 * 遍历所有value模式 选class，若不存在则下一条模式 若存在，则选class下对应的标签，可能有多个标签，所以要遍历所有的标签
		 * 判断路径＋兄弟节点。若符合，则提取信息， 若不符合则下一个标签。
		 */
		for (ValueFeature i : PatternStore.valuepattern) {
			Elements MatchRootList = root.select("[" + i.getAttr() + "~=(.*)" + i.getClassname() + "(.*)]");
			if (MatchRootList.isEmpty() == false) {
				// System.out.println(i.toString());
				for (int index = 0; index < MatchRootList.size(); index++) {
					Element MatchRoot = MatchRootList.get(index);
					for (Element p : MatchRoot.select(i.getTag())) {
						if (p == null) {// 没有匹配对应到对应标签，可能不是这棵子树
							// System.out.println("p===null");
							break;
						} else {
							if (IspatternValue(p, i) == true) {
								// 判断标签和模式是否匹配
								vflag = true;
								s_tag = p.tagName();
								if (s_tag.equals("img")) {
									s_value = p.absUrl("src");
								} else
									s_value = p.text();
								// System.out.println("s_value alone:" +
								// s_value);
								p.remove();
								break;
							} else
								continue;
						}
					}
					if (vflag == true)// extract successfully,break the whole
										// loop;
						break;
				}
			} else
				continue;// if no matched tag, find next pattern.
			if (vflag == true)// extract successfully, break the loop
				break;
		}
		for (Feature i : PatternStore.allpattern) {
			if (vflag == true)
				break;// 如果已经抽取到了V，就不再做key－value的抽取
			else {
				kflag = false;
				Elements MatchedParentRoots = root
						.select(i.getKey_parent_tag() + "[class~=(.*)" + i.getKey_parent_class() + "(.*)]");
				if (MatchedParentRoots.isEmpty() == false) { // if get matched
																// parent roots'
																// list.
					// System.out.println("find parent root");
					for (int index_parent = 0; index_parent < MatchedParentRoots.size(); index_parent++) {
						// System.out.println(i.toString());
						Element MatchedParentRoot = MatchedParentRoots.get(index_parent);
						// find key root.
						Elements MatchedKeyRoots = MatchedParentRoot
								.select("[class~=(.*)" + i.getKey_class() + "(.*)]");
						if (MatchedKeyRoots.isEmpty() == false) {
							// System.out.println("find key root");
							for (int index_key = 0; index_key < MatchedKeyRoots.size(); index_key++) {
								Element MatchedKeyRoot = MatchedKeyRoots.get(index_key);
								// find key tag
								Elements tags = MatchedKeyRoot.select(i.getKey_tag());
								if (!tags.isEmpty()) {
									// System.out.println("find key tag");
									for (int index_tag = 0; index_tag < tags.size(); index_tag++) {
										Element tag = tags.get(index_tag);
										if (IspatternKey(tag, i)) {
											if (!tag.equals(MatchedKeyRoot)) {
												kflag = true;
												// find matched key
												// System.out.println("Key::" +
												// tag.text());
												s_key = tag.text();
												tag.remove();
												break;
											} else
												continue;
										} else
											continue;
									} // key-tag-loop
									if (kflag == true)
										break;
								} else// if it isnt find matched tag,look for
										// another key.
									continue;
								if (kflag == true)
									break;// key root loop
							}
						} else// if it isnt find key, look for next parent root.
							continue;
						if (kflag == true) {// if it has find key, then look for
											// matched value
							// System.out.println("look for matched value");
							Elements MatchedValueRoots = MatchedParentRoots
									.select("[class~=(.*)" + i.getValue_class() + "(.*)]");
							if (!MatchedValueRoots.isEmpty()) {
								// System.out.println("find value root");
								for (int index_value = 0; index_value < MatchedValueRoots.size(); index_value++) {
									Element MatchedValueRoot = MatchedValueRoots.get(index_value);
									Elements tags = MatchedValueRoot.select(i.getValue_tag());
									if (!tags.isEmpty()) {
										// System.out.println("find value tag");
										for (int index_tag = 0; index_tag < tags.size(); index_tag++) {
											Element tag = tags.get(index_tag);
											if (IsKey_Value(tag, i)) {
												if (!tag.equals(MatchedValueRoot)) {
													// System.out.println("find
													// value");
													// System.out.println("value::"
													// + tag.text());
													s_value = tag.text();
													tag.remove();
													vflag = true;
													break;
												} else
													continue;
											}
										} // value-tag-loop
									}
									if (vflag == true)
										break;
								} // value-root-loop
							} else
								continue;// parent-loop
						} else
							continue;// parent-loop
						if (vflag == true)
							break;
					} // parent-loop
				} else// else find next pattern
					continue;
				/**
				 * 1.找公有的节点 class 2.找key独有节点的class 3.找value独有节点的class
				 * 4.先找key对应的值,若没找到则下一个 5.若找到，找对应
				 */
			}
		} // pattern-loop
		if (vflag == true && kflag == true) {
			Info.put(s_key, s_value);
			esuc = true;
		} else if (vflag == true && kflag == false) {
			/**
			 * 提取XXX:XXXX的信息
			 */
			// System.out.println("v=true,k=false::"+s_value);
			if (s_tag.equals("img"))
				Info.put("图片", s_value);
			else {
				if (s_value.indexOf("\u00A0") > 0) {// replace &nbsp with " "
					s_value = s_value.replaceAll("\u00A0", " ");
				}
				String[] brsplit = s_value.split("\\s+|\\|");
				// String[] brsplit=new String[1];
				// brsplit[0]=s_value;
				for (int i = 0; i < brsplit.length; i++) {
					// System.out.println("index" + i + "::" + brsplit[i]);
					Pattern patternx = Pattern.compile("(.{0,8})(:|：)(.*)");
					Matcher mx = patternx.matcher(brsplit[i]);
					if (mx.matches()) {
						String[] split = mx.group().toString().split(":|：");
						if (split.length == 1) {
							Info.put(split[0], "");
						} else if (split.length == 0)
							esuc = false;
						else {
							Info.put(split[0], split[1]);
						}
					} else {
						if (s_tag.equals("em") || s_tag.equals("h3") || s_tag.equals("b") || s_tag.equals("h1"))
							Info.put("标题", s_value);
					}
				}
			}
			esuc = true;
		} else if (vflag == false && kflag == true)

		{
		}
		return esuc;

	}

	public static boolean IspatternKey(Element root, Feature feature) {
		String pre_tag = "";
		String next_tag = "";
		String path = PatternStore.CreatePath(root);
		int len = feature.getKey_path().length();
		if (path.substring(path.length() - len, path.length()).equals(feature.getKey_path())) {
			return true;
		} else
			return false;
	}

	public static boolean IspatternValue(Element root, ValueFeature feature) {
		String path = PatternStore.CreatePath(root);
		int len = feature.getPath().length();
		String pre_tag = "";
		String next_tag = "";
		if (path.substring(path.length() - len, path.length()).equals(feature.getPath())) {
			if (root.previousElementSibling() == null)
				pre_tag = "first";
			else
				pre_tag = root.previousElementSibling().tagName();
			if (root.nextElementSibling() == null)
				next_tag = "last";
			else
				next_tag = root.nextElementSibling().tagName();
			// System.out.println("pre_tag:" + pre_tag);
			// System.out.println("next_tag:" + next_tag);
			if (pre_tag.equals(feature.getLeft_tag()) && next_tag.equals(feature.getRight_tag())) {
				return true;
			} else
				return false;
		} else
			return false;
	}

	public static boolean IsKey_Value(Element root, Feature feature) {
		String pre_tag = "";
		String next_tag = "";
		String path = PatternStore.CreatePath(root);
		int len = feature.getValue_path().length();
		if (path.substring(path.length() - len, path.length()).equals(feature.getValue_path())) {
			return true;
		} else
			return false;
	}
}
