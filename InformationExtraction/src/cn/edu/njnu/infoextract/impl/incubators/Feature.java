package cn.edu.njnu.infoextract.impl.incubators;

public class Feature {
    private String key_path;
    private String key_tag;
    private String key_class;
    private String key_parent_tag;
    private String key_parent_class;
    private String key_right_tag;
    private String key_left_tag;
    private String value_path;
    private String value_tag;
    private String value_class;

    public Feature() {
    }

    public Feature(String key_path, String key_tag, String key_class, String key_parent_tag, String key_parent_class,
                   String key_right_tag, String key_left_tag, String value_path, String value_tag, String value_class) {
        super();
        this.key_path = key_path;
        this.key_tag = key_tag;
        this.key_class = key_class;
        this.key_parent_tag = key_parent_tag;
        this.key_parent_class = key_parent_class;
        this.key_right_tag = key_right_tag;
        this.key_left_tag = key_left_tag;
        this.value_path = value_path;
        this.value_tag = value_tag;
        this.value_class = value_class;
    }

    public String getKey_path() {
        return key_path;
    }

    public void setKey_path(String key_path) {
        this.key_path = key_path;
    }

    public String getKey_tag() {
        return key_tag;
    }

    public void setKey_tag(String key_tag) {
        this.key_tag = key_tag;
    }

    public String getKey_class() {
        return key_class;
    }

    public void setKey_class(String key_class) {
        this.key_class = key_class;
    }

    public String getKey_parent_tag() {
        return key_parent_tag;
    }

    public void setKey_parent_tag(String key_parent_tag) {
        this.key_parent_tag = key_parent_tag;
    }

    public String getKey_parent_class() {
        return key_parent_class;
    }

    public void setKey_parent_class(String key_parent_class) {
        this.key_parent_class = key_parent_class;
    }

    public String getKey_right_tag() {
        return key_right_tag;
    }

    public void setKey_right_tag(String key_right_tag) {
        this.key_right_tag = key_right_tag;
    }

    public String getKey_left_tag() {
        return key_left_tag;
    }

    public void setKey_left_tag(String key_left_tag) {
        this.key_left_tag = key_left_tag;
    }

    public String getValue_path() {
        return value_path;
    }

    public void setValue_path(String value_path) {
        this.value_path = value_path;
    }

    public String getValue_tag() {
        return value_tag;
    }

    public void setValue_tag(String value_tag) {
        this.value_tag = value_tag;
    }

    public String getValue_class() {
        return value_class;
    }

    public void setValue_class(String value_class) {
        this.value_class = value_class;
    }
}
