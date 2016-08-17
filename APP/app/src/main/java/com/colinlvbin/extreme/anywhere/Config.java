package com.colinlvbin.extreme.anywhere;

/**
 * Created by Colin on 2016/6/7.
 */
public class Config {

    public static final String SERVER_IP="http://108.61.247.246:5000";
    public static final String AVATAR_FILE_LOCATION="file:///sdcard/avatar.jpg";
    public static final String POST_PIC_FILE_LOCATION="file:///sdcard/post_pic.jpg";
    //活动请求编号
    public static final int LOGIN_TO_SIGN_UP_REQUESTCODE=1000;
    public static final int HOME_TO_SETTINGS_REQUESTCODE=1001;
    public static final int SETTINGS_TO_BASIC_REQUESTCODE=1002;
    public static final int BASIC_TO_TAKE_PIC_REQUESTCODE=1003;
    public static final int BASIC_TO_PICK_PIC_REQUESTCODE=1004;
    public static final int BASIC_TO_CROP_PIC_REQUESTCODE=1005;
    public static final int HOME_TO_CONFIGURE_POST_REQUESTCODE=1006;
    public static final int CONFIGURE_TO_CREATE_POST_REQUESTCODE=1007;
    public static final int CREATE_POST_TO_PICK_PIC_REQUESTCODE=1008;
    public static final int POST_LIST_TO_POST_DETAIL_REQUESTCODE=1009;
    public static final int POST_DETAIL_TO_POST_COMMENTS_REQUESTCODE=1010;
    public static final int CREATE_POST_TO_TAKE_PIC_REQUESTCODE=1011;
    //网络请求编号
    public static final int REQUEST_LOGIN=1;
    public static final int REQUEST_SIGN_UP=2;
    public static final int REQUEST_GET_AVATAR=3;
    public static final int REQUEST_ALTER_BASIC_INFO=4;
    public static final int REQUEST_ALTER_PASSWORD=5;
    public static final int REQUEST_GET_LOCATION_DESCRIPTION=6;
    public static final int REQUEST_CREATE_POST=7;
    public static final int REQUEST_GET_POST_LIST=8;
    public static final int REQUEST_GET_POST_DETAIL=9;
    public static final int REQUEST_GET_POST_PICTURE=10;
    public static final int REQUEST_GET_COMMENT_LIST=11;
    public static final int REQUEST_SUBMIT_COMMENT=12;
    public static final int REQUEST_LIKE_OR_NOT=13;
    public static final int REQUEST_CONDEMN_OR_NOT=14;

    //服务器返回状态
    public static final int LOGIN_SUCCESS=40;
    public static final int USER_NOT_FOUND=41;
    public static final int PASSWORD_WRONG=42;
    public static final int SIGN_UP_SUCCESS=50;
    public static final int USER_ALREADY_EXIST=51;

    public static final int ALTER_BASIC_INFO_SUCCESS=60;
    public static final int ALTER_PASSWORD_SUCCESS=70;

    public static final int GET_LOCATION_DESCRIPTION_SUCCESS=80;
    public static final int LOCATION_DESCRIPTION_SERVICE_UNAVAILABLE=81;

    public static final int CREATE_POST_SUCCESS=90;
    //至少有一个post时成功
    public static final int GET_POST_LIST_SUCCESS=100;
    //附近没有posts
    public static final int NO_POST_AROUND=101;

    public static final int GET_POST_DETAIL_SUCCESS=110;
    public static final int POST_CIPHER_WRONG=111;
    public static final int GET_COMMENT_LIST_SUCCESS=120;
    public static final int NO_COMMENT_AVAILABLE=121;

    public static final int ADD_COMMENT_SUCCESS=130;

    public static final int LIKE_POST_SUCCESS=140;
    public static final int DE_LIKE_POST_SUCCESS=142;
    public static final int CONDEMN_POST_SUCCESS=150;
    public static final int DE_CONDEMN_POST_SUCCESS=152;


    public static final String BUNDLE_USER_INFO="BUNDLE_USER_INFO";
    public static final String BUNDLE_POST_INFO="BUNDLE_POST_INFO";
}
