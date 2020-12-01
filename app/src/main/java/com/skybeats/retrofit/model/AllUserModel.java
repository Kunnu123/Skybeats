package com.skybeats.retrofit.model;

import java.io.Serializable;
import java.util.List;

public class AllUserModel extends BaseModel implements Serializable {
    private String user_id, user_name, gender, country, profile_image, dob, mobile_no, channel_name, create_date, secret_key, token, skybeat_id, user_diamonds;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSkybeat_id() {
        return skybeat_id;
    }

    public void setSkybeat_id(String skybeat_id) {
        this.skybeat_id = skybeat_id;
    }

    public String getUser_diamonds() {
        return user_diamonds;
    }

    public void setUser_diamonds(String user_diamonds) {
        this.user_diamonds = user_diamonds;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }

    private List<AllUserModel> data;

    public List<AllUserModel> getData() {
        return data;
    }

    public void setData(List<AllUserModel> data) {
        this.data = data;
    }

}
