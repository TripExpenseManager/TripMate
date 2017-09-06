package android.app.models;

/**
 * Created by Sai Krishna on 6/29/2017.
 */

public class HotelsTravelModel {

    String name,url_display,img_url1,img_url2,app_url = "",description,services_offered;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApp_url() {
        return app_url;
    }

    public void setApp_url(String app_url) {
        this.app_url = app_url;
    }

    public String getImg_url1() {
        return img_url1;
    }

    public String getImg_url2() {
        return img_url2;
    }

    public void setImg_url1(String img_url1) {
        this.img_url1 = img_url1;
    }

    public void setImg_url2(String img_url2) {
        this.img_url2 = img_url2;
    }

    public String getUrl_display() {
        return url_display;
    }

    public void setUrl_display(String url_display) {
        this.url_display = url_display;
    }

    public String getServices_offered() {
        return services_offered;
    }

    public void setServices_offered(String services_offered) {
        this.services_offered = services_offered;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


}
