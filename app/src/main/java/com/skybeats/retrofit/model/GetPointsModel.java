package com.skybeats.retrofit.model;

import java.io.Serializable;
import java.util.List;

public class GetPointsModel extends BaseModel implements Serializable {
    private String count_id, image_name, points, price;

    private List<GetPointsModel> data;

    public List<GetPointsModel> getData() {
        return data;
    }

    public void setData(List<GetPointsModel> data) {
        this.data = data;
    }


    public String getCount_id() {
        return count_id;
    }

    public void setCount_id(String count_id) {
        this.count_id = count_id;
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
