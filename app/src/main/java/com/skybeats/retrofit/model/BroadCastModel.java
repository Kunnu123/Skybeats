package com.skybeats.retrofit.model;

import java.io.Serializable;
import java.util.List;

public class BroadCastModel extends BaseModel implements Serializable {
    public String getBrodcast_id() {
        return brodcast_id;
    }

    public void setBrodcast_id(String brodcast_id) {
        this.brodcast_id = brodcast_id;
    }

    private String brodcast_id;

    private BroadCastModel data;

    public BroadCastModel getData() {
        return data;
    }

    public void setData(BroadCastModel data) {
        this.data = data;
    }

}
