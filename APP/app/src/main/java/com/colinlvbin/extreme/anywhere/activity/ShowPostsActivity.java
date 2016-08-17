package com.colinlvbin.extreme.anywhere.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.MyApplication;
import com.colinlvbin.extreme.anywhere.ParseInformation;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.RoutineOps;
import com.colinlvbin.extreme.anywhere.adapter.PostAdapter;
import com.colinlvbin.extreme.anywhere.model.Comparators;
import com.colinlvbin.extreme.anywhere.model.Post;
import com.colinlvbin.extreme.anywhere.model.User;

import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowPostsActivity extends AppCompatActivity implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,RadioGroup.OnCheckedChangeListener{

    private TextView locationDescriptionDisplay;
    private ListView postsListView;
    private Button refreshButton;

    //排序相关
    private RadioGroup sortingCriteriaGroup;

    private User user;
    private RequestQueue requestQueue;
    private double longitude;
    private double latitude;
    private String location_description;

    //缓冲对话框
    ProgressDialog progressDialog;

    //LBS相关
    private String provider;

    //语言相关
    private String language;

    private SwipeRefreshLayout swipeRefreshLayout;

    PostAdapter postAdapter;
    private List<Post> postList=new ArrayList<Post>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_posts);
        requestQueue= NoHttp.newRequestQueue();
        locationDescriptionDisplay=(TextView)findViewById(R.id.location_description_show_posts);
        postsListView=(ListView)findViewById(R.id.post_list_show_post);

        refreshButton=(Button)findViewById(R.id.refresh_show_posts);
        refreshButton.setOnClickListener(this);

        sortingCriteriaGroup=(RadioGroup)findViewById(R.id.sorting_criteria_group_show_posts);
        sortingCriteriaGroup.setOnCheckedChangeListener(this);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_purple,android.R.color
                .holo_blue_bright,android.R.color.holo_red_light);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        //swipeRefreshLayout.setProgressBackgroundColorSchemeColor(R.color
              //  .bright_foreground_disabled_material_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        user=(User)getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);
        longitude=((MyApplication)getApplication()).GetLongitude();
        latitude=((MyApplication)getApplication()).GetLatitude();

        language=((MyApplication)getApplication()).GetLanguage();

        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Post selectedPost=postList.get(position);
                int has_cipher=selectedPost.getHas_cipher();
                if(has_cipher==1){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ShowPostsActivity.this);
                    final EditText editText = new EditText(ShowPostsActivity.this);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT|InputType
                            .TYPE_TEXT_VARIATION_PASSWORD);
                    dialog.setTitle(R.string.input_password);
                    dialog.setMessage(R.string.password);
                    dialog.setCancelable(true);
                    dialog.setView(editText);
                    dialog.setPositiveButton(R.string.confirm,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String inputCipher=editText.getText().toString().trim();
                                    if(inputCipher.length()<6||inputCipher.length()>20){
                                        RoutineOps.MakeToast(ShowPostsActivity.this,
                                                ShowPostsActivity.this.getResources().getString(R
                                                        .string.password_length));
                                    }else{
                                        progressDialog=RoutineOps.ShowProgressDialog
                                                (ShowPostsActivity.this,
                                                ShowPostsActivity.this.getResources().getString(R
                                                        .string.fetching));
                                        Request<JSONObject>getPostDetailRequest=NoHttp
                                                .createJsonObjectRequest(Config
                                                        .SERVER_IP+"/get_post_detail",
                                                        RequestMethod.POST);
                                        getPostDetailRequest.add("has_cipher",1);
                                        getPostDetailRequest.add("cipher",inputCipher);
                                        try{
                                            getPostDetailRequest.add("user_id",user.getUser_id());
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                        getPostDetailRequest.add("post_id",selectedPost.getPost_id());
                                        requestQueue.add(Config.REQUEST_GET_POST_DETAIL,
                                                getPostDetailRequest,onResponseListener);
                                    }

                                }
                            });
                    dialog.setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    dialog.show();
                }else{
                    progressDialog=RoutineOps.ShowProgressDialog(ShowPostsActivity.this,
                            ShowPostsActivity.this.getResources().getString(R.string.fetching));
                    Request<JSONObject>getPostDetail=NoHttp.createJsonObjectRequest(Config
                            .SERVER_IP+"/get_post_detail",RequestMethod.POST);
                    getPostDetail.add("has_cipher",0);
                    getPostDetail.add("post_id",selectedPost.getPost_id());
                    getPostDetail.add("user_id",user.getUser_id());
                    requestQueue.add(Config.REQUEST_GET_POST_DETAIL,getPostDetail,onResponseListener);
                }
            }
        });
        GetPostList();

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.refresh_show_posts:

                GetPostList();

                break;
            default:
        }
    }

    private void GetPostList(){
        progressDialog=RoutineOps.ShowProgressDialog(ShowPostsActivity.this,this.getResources()
                .getString(R.string.fetching));
        Request<JSONObject> getPostsRequest=NoHttp.createJsonObjectRequest(Config
                .SERVER_IP+"/get_posts", RequestMethod.POST);
        getPostsRequest.add("longitude",longitude);
        getPostsRequest.add("latitude",latitude);
        if(language.endsWith("zh")) {
            getPostsRequest.add("language", "zh");
        }else{
            getPostsRequest.add("language", "en");
        }
        requestQueue.add(Config.REQUEST_GET_POST_LIST,getPostsRequest,onResponseListener);
    }
    OnResponseListener<JSONObject> onResponseListener=new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            switch (what){
                case Config.REQUEST_GET_POST_LIST:
                    if(response.getHeaders().getResponseCode()==200){
                        progressDialog.dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        JSONObject post_list_result=response.get();
                        try {
                            int permission=post_list_result.getInt("permission");
                            if(permission==Config.GET_POST_LIST_SUCCESS){
                                location_description=post_list_result.getString("location_description");
                                if(location_description.equals("")){
                                    locationDescriptionDisplay.setText(getResources().getString(R
                                            .string.fail_to_get_location_description));
                                }else {
                                    locationDescriptionDisplay.setText(location_description);
                                }
                                JSONArray postJsonArray=post_list_result.getJSONArray("posts");
                                postList= ParseInformation.ParsePosts(postJsonArray);
                                postAdapter=new PostAdapter(ShowPostsActivity.this,R.layout
                                        .post_item, postList);
                                postsListView.setAdapter(postAdapter);
                                SortPostList(sortingCriteriaGroup.getCheckedRadioButtonId());

                            }else if(permission==Config.NO_POST_AROUND){
                                RoutineOps.MakeToast(ShowPostsActivity.this,ShowPostsActivity
                                        .this.getResources().getString(R.string.no_posts_around));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Config.REQUEST_GET_POST_DETAIL:
                    progressDialog.dismiss();
                    if(response.getHeaders().getResponseCode()==200){
                        JSONObject post_detail_result=response.get();
                        try {
                            int permission=post_detail_result.getInt("permission");
                            if(permission==Config.GET_POST_DETAIL_SUCCESS){

                                Intent toPostDetail=new Intent(ShowPostsActivity.this,
                                        PostDetailActivity.class);

                                Post post=ParseInformation.ParsePost(post_detail_result
                                        .getJSONObject("post"));
                                toPostDetail.putExtra("has_liked",post_detail_result.getInt
                                        ("has_liked"));
                                toPostDetail.putExtra("has_condemned",post_detail_result.getInt
                                        ("has_condemned"));
                                Bundle postDetailBundle=new Bundle();
                                postDetailBundle.putSerializable(Config.BUNDLE_POST_INFO,post);
                                postDetailBundle.putSerializable(Config.BUNDLE_USER_INFO,user);
                                toPostDetail.putExtras(postDetailBundle);
                                startActivityForResult(toPostDetail,Config.POST_LIST_TO_POST_DETAIL_REQUESTCODE);
                            }else if(permission==Config.POST_CIPHER_WRONG){
                                RoutineOps.MakeToast(ShowPostsActivity.this,ShowPostsActivity
                                        .this.getResources().getString(R.string.password_wrong));
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
    public void onRefresh() {
        GetPostList();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedID) {
        if(group==sortingCriteriaGroup){
            SortPostList(checkedID);
        }
    }

    public void SortPostList(int checkedID){
        switch(checkedID) {
            case R.id.sort_by_time_show_posts:
                Collections.sort(postList, Comparators.comparatorTime);
                postAdapter.notifyDataSetChanged();
                break;
            case R.id.sort_by_comment_number_show_posts:
                Collections.sort(postList, Comparators.comparatorCommentNumber);
                postAdapter.notifyDataSetChanged();
                break;
            case R.id.sort_by_like_number_show_posts:
                Collections.sort(postList, Comparators.comparatorLikeNumber);
                postAdapter.notifyDataSetChanged();
                break;
            case R.id.sort_by_title_show_posts:
                Collections.sort(postList, Comparators.comparatorTitle);
                postAdapter.notifyDataSetChanged();
                break;
        }
    }
}
