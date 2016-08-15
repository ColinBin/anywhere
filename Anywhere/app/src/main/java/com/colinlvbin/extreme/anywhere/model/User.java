package com.colinlvbin.extreme.anywhere.model;

import java.io.Serializable;

/**
 * Created by Colin on 2016/6/9.
 */
public class User implements Serializable{

    private String user_id;
    private String username;
    private String create_time;
    private String email;
    private int has_avatar;

    public User(String user_id, String username, String create_time, String email, int
            has_avatar) {
        this.user_id = user_id;
        this.create_time = create_time;
        this.email = email;
        this.has_avatar = has_avatar;
        this.username = username;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getHas_avatar() {
        return has_avatar;
    }

    public void setHas_avatar(int has_avatar) {
        this.has_avatar = has_avatar;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
