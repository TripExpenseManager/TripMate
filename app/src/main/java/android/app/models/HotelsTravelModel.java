package android.app.models;

/**
 * Created by Sai Krishna on 6/29/2017.
 */

public class HotelsTravelModel {

    String name,url,desc,iconUrl1,iconUrl2,referenseUrl;

    public String getReferenseUrl() {
        return referenseUrl;
    }

    public void setReferenseUrl(String referenseUrl) {
        this.referenseUrl = referenseUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconUrl1() {
        return iconUrl1;
    }

    public String getIconUrl2() {
        return iconUrl2;
    }

    public void setIconUrl1(String iconUrl1) {
        this.iconUrl1 = iconUrl1;
    }

    public void setIconUrl2(String iconUrl2) {
        this.iconUrl2 = iconUrl2;
    }
}
