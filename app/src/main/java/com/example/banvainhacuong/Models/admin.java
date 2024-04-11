package com.example.banvainhacuong.Models;

import java.io.Serializable;

public class admin implements Serializable {
    public admin(int admin_id, String ad_name, String username, String password) {
        this.admin_id = admin_id;
        this.ad_name = ad_name;
        this.username = username;
        this.password = password;
    }

    int admin_id;
    String ad_name;
    String username;
    String password;
    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public String getAd_name() {
        return ad_name;
    }

    public void setAd_name(String ad_name) {
        this.ad_name = ad_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public admin() {
    }


}
