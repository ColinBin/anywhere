package com.colinlvbin.extreme.anywhere.model;

import java.io.Serializable;

/**
 * Created by Colin on 2016/6/10.
 */
public class Post implements Serializable{
    private int post_id;
    private String title;
    private String content;
    private String location_description;
    private double longitude;
    private double latitude;
    private int style;
    private int like_number;
    private int condemn_number;
    private int comment_number;
    private int has_cipher;
    private int has_picture;
    private String create_time;
    private String user_id;

    public Post(int post_id,String title,String content,String location_description,double longitude,double latitude,
                int style,int like_number,int condemn_number,int comment_number,int has_cipher,
                int has_picture,String create_time,String user_id) {
        this.has_cipher = has_cipher;
        this.comment_number = comment_number;
        this.condemn_number = condemn_number;
        this.content = content;
        this.create_time = create_time;
        this.has_picture = has_picture;
        this.latitude = latitude;
        this.like_number = like_number;
        this.location_description = location_description;
        this.longitude = longitude;
        this.post_id = post_id;
        this.style = style;
        this.title = title;
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getComment_number() {
        return comment_number;
    }

    public void setComment_number(int comment_number) {
        this.comment_number = comment_number;
    }

    public int getCondemn_number() {
        return condemn_number;
    }

    public void setCondemn_number(int condemn_number) {
        this.condemn_number = condemn_number;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getHas_cipher() {
        return has_cipher;
    }

    public void setHas_cipher(int has_cipher) {
        this.has_cipher = has_cipher;
    }

    public int getHas_picture() {
        return has_picture;
    }

    public void setHas_picture(int has_picture) {
        this.has_picture = has_picture;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getLike_number() {
        return like_number;
    }

    public void setLike_number(int like_number) {
        this.like_number = like_number;
    }

    public String getLocation_description() {
        return location_description;
    }

    public void setLocation_description(String location_description) {
        this.location_description = location_description;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}
