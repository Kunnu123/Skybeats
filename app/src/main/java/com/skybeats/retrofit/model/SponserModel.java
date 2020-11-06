package com.skybeats.retrofit.model;

import java.io.Serializable;
import java.util.List;

public class SponserModel extends BaseModel implements Serializable {
    private String image_id, image_name, title, url = "";

    private List<SponserModel> data;

    public List<SponserModel> getData() {
        return data;
    }

    public void setData(List<SponserModel> data) {
        this.data = data;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
