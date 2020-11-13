package com.skybeats.retrofit.model;

import java.io.Serializable;
import java.util.List;

public class GiftModel extends BaseModel implements Serializable {
    private String gift_id, image_name, points, price;

    private List<GiftModel> data;

    public List<GiftModel> getData() {
        return data;
    }

    public void setData(List<GiftModel> data) {
        this.data = data;
    }

    public String getGift_id() {
        return gift_id;
    }

    public void setGift_id(String gift_id) {
        this.gift_id = gift_id;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
