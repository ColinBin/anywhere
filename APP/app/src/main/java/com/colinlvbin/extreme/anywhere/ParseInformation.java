package com.colinlvbin.extreme.anywhere;

import com.colinlvbin.extreme.anywhere.model.Comment;
import com.colinlvbin.extreme.anywhere.model.Post;
import com.colinlvbin.extreme.anywhere.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Colin on 2016/6/9.
 */
public class ParseInformation {

    public static User ParseUser(JSONObject userInformation){
        User user=null;
        try{
            user=new User(userInformation.getString("user_id"),userInformation.getString
                    ("username"),userInformation.getString("create_time"),userInformation.getString
                    ("email"),userInformation.getInt("has_avatar"));
        }catch(Exception e){
            e.printStackTrace();
        }
        return user;
    }
    public static List<Post> ParsePosts(JSONArray postsInformation){
        List<Post> postList=new ArrayList<Post>();
        try{
            for(int i=0;i<postsInformation.length();i++){
                JSONObject currentPostJsonObject=postsInformation.getJSONObject(i);
                Post currentPost=new Post(currentPostJsonObject.getInt("post_id"),
                        currentPostJsonObject.getString("title"),"",currentPostJsonObject.getString("location_description"), currentPostJsonObject
                        .getDouble("longitude"),currentPostJsonObject.getDouble("latitude"),
                        currentPostJsonObject.getInt("style"),currentPostJsonObject.getInt
                        ("like_number"),currentPostJsonObject.getInt("condemn_number"),
                        currentPostJsonObject.getInt("comment_number"),currentPostJsonObject
                        .getInt("has_cipher"),currentPostJsonObject.getInt("has_picture"),
                        currentPostJsonObject.getString("create_time"),currentPostJsonObject
                        .getString("user_id"));
                postList.add(currentPost);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }


        return postList;


    }
    public static List<Comment> ParseComments(JSONArray postsInformation){
        List<Comment> commentList=new ArrayList<Comment>();
        try{
            for(int i=0;i<postsInformation.length();i++){
                JSONObject currentCommentJsonObject=postsInformation.getJSONObject(i);
                Comment currentComment=new Comment(currentCommentJsonObject.getInt("comment_id"),
                        currentCommentJsonObject.getString("user_id"),currentCommentJsonObject
                        .getInt("post_id"),currentCommentJsonObject.getString("content"),
                        currentCommentJsonObject.getString("create_time"));
                commentList.add(currentComment);
            }
        }catch(JSONException e) {
            e.printStackTrace();
        }
        return commentList;
    }
    public static Post ParsePost(JSONObject postInformation){
        Post post=null;
        try{
            post=new Post(postInformation.getInt("post_id"),
                    postInformation.getString("title"),postInformation.getString
                    ("content"),postInformation.getString("location_description"), postInformation
                    .getDouble("longitude"),postInformation.getDouble("latitude"),
                    postInformation.getInt("style"),postInformation.getInt
                    ("like_number"),postInformation.getInt("condemn_number"),
                    postInformation.getInt("comment_number"),postInformation
                    .getInt("has_cipher"),postInformation.getInt("has_picture"),
                    postInformation.getString("create_time"),postInformation
                    .getString("user_id"));
        }catch(JSONException e) {
            e.printStackTrace();
        }
        return post;


    }
}
