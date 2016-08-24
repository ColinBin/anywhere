package com.colinlvbin.extreme.anywhere.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.RoutineOps;
import com.colinlvbin.extreme.anywhere.model.Post;
import com.colinlvbin.extreme.anywhere.model.User;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class PostDetailActivity extends AppCompatActivity implements View.OnClickListener{


    private TextView postTitleDisplay;
    private TextView postCreatorDisplay;
    private TextView postContentDisplay;
    private TextView postCreateTimeDisplay;
    private TextView postLikeNumberDisplay;
    private TextView postCondemnNumberDisplay;
    private ImageView postPictureImage;
    private ImageView hasLikedPostImage;
    private ImageView hasCondemnedPostImage;
    private Button checkCommentsButton;
    private Button cancelButton;

    private RequestQueue requestQueue;
    private Post post;
    private User user;

    private Bitmap postPictureBitmap;

    private int has_liked;
    private int has_condemned;
    private int like_number;
    private int condemn_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        requestQueue= NoHttp.newRequestQueue();
        postTitleDisplay=(TextView)findViewById(R.id.post_title_post_detail);
        postCreatorDisplay=(TextView)findViewById(R.id.post_creator_post_detail);
        postContentDisplay=(TextView)findViewById(R.id.post_content_post_detail);
        postPictureImage=(ImageView)findViewById(R.id.post_picture_post_detail);
        hasLikedPostImage=(ImageView)findViewById(R.id.has_liked_image_post_detail);
        hasCondemnedPostImage=(ImageView)findViewById(R.id.has_condemned_image_post_detail);
        postLikeNumberDisplay=(TextView)findViewById(R.id.like_number_post_detail);
        postCondemnNumberDisplay=(TextView)findViewById(R.id.condemn_number_post_detail);
        postCreateTimeDisplay=(TextView)findViewById(R.id.create_time_post_detail);
        checkCommentsButton=(Button)findViewById(R.id.check_comments_button_post_detail);
        cancelButton=(Button)findViewById(R.id.cancel_button_post_detail);
        checkCommentsButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        hasLikedPostImage.setOnClickListener(this);
        hasCondemnedPostImage.setOnClickListener(this);
        post=(Post)getIntent().getExtras().getSerializable(Config.BUNDLE_POST_INFO);
        user=(User)getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);
        has_liked=getIntent().getIntExtra("has_liked",0);
        has_condemned=getIntent().getIntExtra("has_condemned",0);
        like_number=post.getLike_number();
        condemn_number=post.getCondemn_number();

        InitializePostInfo(post);

    }

    private void InitializePostInfo(Post post){
        postTitleDisplay.setText(post.getTitle());
        if(post.getContent().isEmpty()&&post.getHas_picture()==0){
            postContentDisplay.setTextSize(20);
            postContentDisplay.setText(R.string.no_content);
        }else{
            postContentDisplay.setText(post.getContent());
        }

        postCreatorDisplay.setText(post.getUser_id());
        postCreateTimeDisplay.setText(post.getCreate_time());
        postLikeNumberDisplay.setText(String.valueOf(post.getLike_number()));
        postCondemnNumberDisplay.setText(String.valueOf(post.getCondemn_number()));
        if(post.getComment_number()>0){
            checkCommentsButton.setText(getString(R.string.comments)+"("+String.valueOf(post
                    .getComment_number
                    ())+")");
        }
        if(post.getHas_picture()==1){
            Request<Bitmap>getPostPictureRequest=NoHttp.createImageRequest(Config
                    .SERVER_IP+"/get_post_picture", RequestMethod.POST);
            getPostPictureRequest.add("post_id",post.getPost_id());
            requestQueue.add(Config.REQUEST_GET_POST_PICTURE,getPostPictureRequest,onBitmapResponseListener);
        }
        if(has_liked==1){
            hasLikedPostImage.setImageResource(R.drawable.like_post);
        }else{
            hasLikedPostImage.setImageResource(R.drawable.not_like_post);
        }
        if(has_condemned==1){
            hasCondemnedPostImage.setImageResource(R.drawable.condemn_post);
        }else{
            hasCondemnedPostImage.setImageResource(R.drawable.not_condemn_post);
        }
    }

    OnResponseListener<Bitmap>onBitmapResponseListener=new OnResponseListener<Bitmap>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<Bitmap> response) {
            switch (what){
                case Config.REQUEST_GET_POST_PICTURE:
                    if(response.getHeaders().getResponseCode()==200){
                        postPictureBitmap=response.get();
                        if(postPictureBitmap!=null){
                            postPictureImage.setImageBitmap(postPictureBitmap);
                        }else{
                            RoutineOps.MakeToast(PostDetailActivity.this,PostDetailActivity.this
                                    .getResources().getString(R.string.fetch_pic_failure));
                        }
                    }
                    break;
                default:
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int
                responseCode, long networkMillis) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

    OnResponseListener<JSONObject>onResponseListener=new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            switch(what){
                case Config.REQUEST_LIKE_OR_NOT:
                    if(response.getHeaders().getResponseCode()==200){
                        JSONObject like_result=response.get();
                        try {
                            int permission=like_result.getInt("permission");
                            if(permission==Config.LIKE_POST_SUCCESS){
                                //has_liked=1;

                                //hasLikedPostImage.setImageResource(R.drawable.like_post);
                            }else if(permission==Config.DE_LIKE_POST_SUCCESS){
                                //has_liked=0;
                                //like_number=like_number-1;
                                //postLikeNumberDisplay.setText(String.valueOf(like_number));
                                //hasLikedPostImage.setImageResource(R.drawable.not_like_post);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case Config.REQUEST_CONDEMN_OR_NOT:
                    if(response.getHeaders().getResponseCode()==200){
                        JSONObject condemn_result=response.get();
                        try {
                            int permission=condemn_result.getInt("permission");
                            if(permission==Config.CONDEMN_POST_SUCCESS){
                                //has_condemned=1;
                                //condemn_number=condemn_number+1;
                                //postCondemnNumberDisplay.setText(String.valueOf(condemn_number));
                                //hasCondemnedPostImage.setImageResource(R.drawable.condemn_post);
                            }else if(permission==Config.DE_CONDEMN_POST_SUCCESS){
                                //has_condemned=0;
                                //condemn_number=condemn_number-1;
                                //postCondemnNumberDisplay.setText(String.valueOf(condemn_number));
                                //hasCondemnedPostImage.setImageResource(R.drawable
                                        //.not_condemn_post);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                default:
            }
        }

        @Override
        public void onFailed(int what, String url, Object tag, Exception exception, int
                responseCode, long networkMillis) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.check_comments_button_post_detail:
                Intent toPostComments=new Intent(PostDetailActivity.this,PostCommentsActivity
                        .class);
                Bundle postCommentBundle=new Bundle();
                postCommentBundle.putSerializable(Config.BUNDLE_USER_INFO,user);
                postCommentBundle.putSerializable(Config.BUNDLE_POST_INFO,post);
                toPostComments.putExtras(postCommentBundle);
                startActivityForResult(toPostComments,Config
                        .POST_DETAIL_TO_POST_COMMENTS_REQUESTCODE);
                break;
            case R.id.cancel_button_post_detail:
                finish();
                break;
            case R.id.has_liked_image_post_detail:
                //先更新UI再网络通信
                if(has_liked==1){
                    has_liked=0;
                    like_number=like_number-1;
                    postLikeNumberDisplay.setText(String.valueOf(like_number));
                    hasLikedPostImage.setImageResource(R.drawable.not_like_post);
                }else{
                    has_liked=1;
                    like_number=like_number+1;
                    postLikeNumberDisplay.setText(String.valueOf(like_number));
                    hasLikedPostImage.setImageResource(R.drawable.like_post);
                    if(has_condemned==1){
                        has_condemned=0;
                        condemn_number=condemn_number-1;
                        postCondemnNumberDisplay.setText(String.valueOf(condemn_number));
                        hasCondemnedPostImage.setImageResource(R.drawable.not_condemn_post);

                    }
                }

                Request<JSONObject>likeOrNotRequest=NoHttp.createJsonObjectRequest(Config
                        .SERVER_IP+"/like_post_or_not",RequestMethod.POST);
                likeOrNotRequest.add("post_id",post.getPost_id());
                likeOrNotRequest.add("user_id",user.getUser_id());
                likeOrNotRequest.add("has_liked",has_liked);
                requestQueue.add(Config.REQUEST_LIKE_OR_NOT,likeOrNotRequest,onResponseListener);

                break;
            case R.id.has_condemned_image_post_detail:
                if(has_condemned==1){
                    has_condemned=0;
                    condemn_number=condemn_number-1;
                    postCondemnNumberDisplay.setText(String.valueOf(condemn_number));
                    hasCondemnedPostImage.setImageResource(R.drawable.not_condemn_post);
                }else{
                    has_condemned=1;
                    condemn_number=condemn_number+1;
                    postCondemnNumberDisplay.setText(String.valueOf(condemn_number));
                    hasCondemnedPostImage.setImageResource(R.drawable.condemn_post);
                    if(has_liked==1){
                        has_liked=0;
                        like_number=like_number-1;
                        postLikeNumberDisplay.setText(String.valueOf(like_number));
                        hasLikedPostImage.setImageResource(R.drawable.not_like_post);

                    }
                }
                Request<JSONObject>condemnOrNotRequest=NoHttp.createJsonObjectRequest(Config
                        .SERVER_IP+"/condemn_post_or_not",RequestMethod.POST);
                condemnOrNotRequest.add("post_id",post.getPost_id());
                condemnOrNotRequest.add("user_id",user.getUser_id());
                condemnOrNotRequest.add("has_condemned",has_condemned);
                requestQueue.add(Config.REQUEST_CONDEMN_OR_NOT,condemnOrNotRequest,
                        onResponseListener);
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case Config.POST_DETAIL_TO_POST_COMMENTS_REQUESTCODE:
                if(resultCode==RESULT_OK){
                    checkCommentsButton.setText(getString(R.string.comments)+"("+data.getIntExtra
                            ("comment_number",0) +")");
                }

                break;
            default:
        }
    }
}
