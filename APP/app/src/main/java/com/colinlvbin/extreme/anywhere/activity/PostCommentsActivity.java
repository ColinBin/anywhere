package com.colinlvbin.extreme.anywhere.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.ParseInformation;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.RoutineOps;
import com.colinlvbin.extreme.anywhere.adapter.CommentAdapter;
import com.colinlvbin.extreme.anywhere.model.Comment;
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

import java.util.ArrayList;
import java.util.List;

public class PostCommentsActivity extends AppCompatActivity implements View.OnClickListener{

    private List<Comment> commentList=new ArrayList<>();
    private CommentAdapter commentAdapter=null;

    private ListView commentListView;
    private EditText commentInput;
    private Button submitCommentButton;

    private RequestQueue requestQueue;
    private User user;
    private Post post;

    private int comment_number;

    private String comment_content;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);
        requestQueue= NoHttp.newRequestQueue();


        commentListView=(ListView)findViewById(R.id.comment_list_post_comments);
        commentInput=(EditText)findViewById(R.id.input_comment_post_comments);
        submitCommentButton=(Button)findViewById(R.id.submit_comment_button_post_comments);
        submitCommentButton.setOnClickListener(this);

        user=(User)getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);
        post=(Post)getIntent().getExtras().getSerializable(Config.BUNDLE_POST_INFO);
        comment_number=post.getComment_number();
        progressDialog=RoutineOps.ShowProgressDialog(PostCommentsActivity.this,this.getResources
                ().getString(R.string
                .fetching));
        Request<JSONObject>getCommentsRequest=NoHttp.createJsonObjectRequest(Config
                .SERVER_IP+"/get_comments", RequestMethod.POST);
        getCommentsRequest.add("post_id",post.getPost_id());
        requestQueue.add(Config.REQUEST_GET_COMMENT_LIST,getCommentsRequest,onResponseListener);



    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.submit_comment_button_post_comments:
                comment_content=commentInput.getText().toString().trim();
                if(TextUtils.isEmpty(comment_content)){
                    RoutineOps.MakeToast(PostCommentsActivity.this,this.getResources().getString(
                            R.string.comment_cannot_be_empty));
                    return;
                }
                Request<JSONObject>submitCommentRequest=NoHttp.createJsonObjectRequest(Config
                        .SERVER_IP+"/add_comment",RequestMethod.POST);
                submitCommentRequest.add("user_id",user.getUser_id());
                submitCommentRequest.add("post_id",post.getPost_id());
                submitCommentRequest.add("comment_content",comment_content);
                requestQueue.add(Config.REQUEST_SUBMIT_COMMENT,submitCommentRequest,onResponseListener);

                break;
            default:
        }
    }

    OnResponseListener<JSONObject>onResponseListener=new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            switch(what){
                case Config.REQUEST_GET_COMMENT_LIST:
                    if(response.getHeaders().getResponseCode()==200){
                        progressDialog.dismiss();
                        JSONObject result=response.get();
                        try {
                            int permission=result.getInt("permission");
                            if(permission==Config.GET_COMMENT_LIST_SUCCESS){
                                commentList= ParseInformation.ParseComments(result
                                        .getJSONArray("comments"));
                                //初始化commentadapter
                                commentAdapter=new CommentAdapter
                                        (PostCommentsActivity.this,R.layout.comment_item,
                                                commentList);
                                commentListView.setAdapter(commentAdapter);
                            }else if(permission==Config.NO_COMMENT_AVAILABLE){
                                RoutineOps.MakeToast(PostCommentsActivity.this,
                                        PostCommentsActivity.this.getResources().getString(R
                                                .string.no_comments));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    break;
                case Config.REQUEST_SUBMIT_COMMENT:
                    if(response.getHeaders().getResponseCode()==200){
                        JSONObject submit_comment_result=response.get();
                        try {
                            int permission=submit_comment_result.getInt("permission");
                            if(permission==Config.ADD_COMMENT_SUCCESS){
                                JSONObject commentJsonObject=submit_comment_result.getJSONObject
                                        ("comment");
                                Comment newComment=new Comment(commentJsonObject.getInt
                                        ("comment_id"),commentJsonObject.getString("user_id"),
                                        commentJsonObject.getInt("post_id"),commentJsonObject
                                        .getString("content"),commentJsonObject.getString
                                        ("create_time"));
                                //没有就初始化
                                if(commentAdapter==null){
                                    commentList.add(newComment);
                                    commentAdapter=new CommentAdapter
                                            (PostCommentsActivity.this,R.layout.comment_item,
                                                    commentList);
                                    commentListView.setAdapter(commentAdapter);
                                }else{
                                    commentList.add(newComment);
                                    commentAdapter.notifyDataSetChanged();
                                }
                                comment_number++;
                                commentInput.setText("");
                                RoutineOps.MakeToast(PostCommentsActivity.this,
                                        PostCommentsActivity.this.getResources().getString(R
                                                .string.create_comment_succeed));
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
    public void onBackPressed() {
        Intent returnToPostDetail=new Intent();
        returnToPostDetail.putExtra("comment_number",comment_number);
        setResult(RESULT_OK,returnToPostDetail);
        finish();
    }
}
