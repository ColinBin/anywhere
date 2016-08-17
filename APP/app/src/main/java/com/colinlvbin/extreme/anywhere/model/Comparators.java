package com.colinlvbin.extreme.anywhere.model;

import java.util.Comparator;

/**
 * Created by Colin on 2016/8/17.
 */
public class Comparators {
    public static Comparator<Post>comparatorTime=new Comparator<Post>() {
        @Override
        public int compare(Post post1, Post post2) {
            return post1.getCreate_time().compareTo(post2.getCreate_time());
        }
    };
    public static Comparator<Post>comparatorTitle=new Comparator<Post>() {
        @Override
        public int compare(Post post1, Post post2) {
            return post1.getTitle().compareTo(post2.getTitle());
        }
    };
    public static Comparator<Post>comparatorCommentNumber=new Comparator<Post>() {
        @Override
        public int compare(Post post1, Post post2) {
            return Integer.valueOf(post2.getComment_number()).compareTo(post1
                    .getComment_number
                    ());
        }
    };
    public static  Comparator<Post>comparatorLikeNumber=new Comparator<Post>() {
        @Override
        public int compare(Post post1, Post post2) {
            return Integer.valueOf(post2.getLike_number()).compareTo(post1.getLike_number
                    ());
        }
    };
    public static Comparator<Post>comparatorAuthorID=new Comparator<Post>() {
        @Override
        public int compare(Post post1, Post post2) {
            return post1.getUser_id().compareTo(post2.getUser_id());
        }
    };
}
