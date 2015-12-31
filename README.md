# InformationExtraction使用说明

#### 注意点1

> 所有类型的抽取主方法都继承自*InfoExtract*类的如下方法：
**public abstract** Extractable extractInformation( String *html* );

#### 注意点2

> **public abstract** Extractable extractInformation( String *html* ):
方法的参数是html字符串(在方法内部会有可选择的清洗);
方法的返回值是Extractable类型(下面讲);

#### 注意点3

> Extractable类里面的唯一一个 *field* 是:
**protected** ArrayList<*Pair*<String, String>> data = new ArrayList<>();
向Extractable实例里面添加一个Pair对有如下两种方法:
1.**public void** put(String key, String value);
2.**public void** put(Pair<String, String> pair);