package com.colinlvbin.extreme.anywhere.model;

/**
 * Created by Colin on 2016/6/12.
 */
public class Comment {
    private int comment_id;
    private String content;
    private int post_id;
    private String user_id;
    private String create_time;

    public Comment( int comment_id, String user_id,int post_id, String content, String create_time) {
        this.post_id = post_id;
        this.comment_id = comment_id;
        this.content = content;
        this.create_time = create_time;
        this.user_id = user_id;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


}
