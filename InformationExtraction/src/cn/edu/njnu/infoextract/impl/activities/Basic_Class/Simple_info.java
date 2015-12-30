package cn.edu.njnu.infoextract.impl.activities.Basic_Class;

public class Simple_info {
    public Simple_info() {
        super();
        this.time = "";
        this.address = "";
        this.title = "";
        // TODO Auto-generated constructor stub
    }

    String title;
    String time;
    String address;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int hashCode() {
        return this.title.hashCode() * this.time.hashCode() * this.address.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Simple_info)) {
            return false;
        }

        Simple_info p = (Simple_info) obj;
        if (this.title.equals(p.title) && this.time.equals(p.time) && this.address.equals(p.address)) {
            return true;
        } else {
            return false;
        }
    }

}
