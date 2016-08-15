package com.colinlvbin.extreme.anywhere.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.ParseInformation;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.RoutineOps;
import com.colinlvbin.extreme.anywhere.adapter.PostAdapter;
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
import java.util.List;

public class ShowPostsActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView locationDescriptionDisplay;
    private ListView postsListView;
    private Button refreshButton;

    private User user;
    private RequestQueue requestQueue;
    private double longitude;
    private double latitude;
    private JSONObject location_description;

    //缓冲对话框
    ProgressDialog progressDialog;

    //LBS相关
    private LocationManager locationManager;
    private String provider;

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

        //LBS相关
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList=locationManager.getProviders(true);
        if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider=LocationManager.NETWORK_PROVIDER;
        }else if(providerList.contains(LocationManager.GPS_PROVIDER)){
            provider= LocationManager.GPS_PROVIDER;
        }else{
            provider=null;
            RoutineOps.MakeToast(ShowPostsActivity.this,"请打开LBS服务后重试");
        }
        if(provider!=null){
            locationManager.requestLocationUpdates(provider,5000,1,locationListener);
        }

        user=(User)getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);
        longitude=getIntent().getDoubleExtra("longitude",0);
        latitude=getIntent().getDoubleExtra("latitude",0);

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
                    dialog.setTitle("请输入密码");
                    dialog.setMessage("密码");
                    dialog.setCancelable(true);
                    dialog.setView(editText);
                    dialog.setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String inputCipher=editText.getText().toString().trim();
                                    if(inputCipher.length()<6||inputCipher.length()>20){
                                        RoutineOps.MakeToast(ShowPostsActivity.this,"密码长度[6,20]");
                                    }else{
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
                    dialog.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    dialog.show();
                }else{
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
        progressDialog=RoutineOps.ShowProgressDialog(ShowPostsActivity.this,"正在获取列表");
        Request<JSONObject> getPostsRequest=NoHttp.createJsonObjectRequest(Config
                .SERVER_IP+"/get_posts", RequestMethod.POST);
        getPostsRequest.add("longitude",longitude);
        getPostsRequest.add("latitude",latitude);
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
                        JSONObject post_list_result=response.get();
                        try {
                            int permission=post_list_result.getInt("permission");
                            if(permission==Config.GET_POST_LIST_SUCCESS){
                                location_description=post_list_result.getJSONObject("location_description");
                                locationDescriptionDisplay.setText(location_description.getString
                                        ("formatted_address"));
                                JSONArray postJsonArray=post_list_result.getJSONArray("posts");
                                postList= ParseInformation.ParsePosts(postJsonArray);
                                PostAdapter postAdapter=new PostAdapter(ShowPostsActivity.this,R.layout
                                        .post_item, postList);
                                postsListView.setAdapter(postAdapter);

                            }else if(permission==Config.NO_POST_AROUND){
                                RoutineOps.MakeToast(ShowPostsActivity.this,"附近没有POST");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Config.REQUEST_GET_POST_DETAIL:
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
                                RoutineOps.MakeToast(ShowPostsActivity.this,"密码错误");
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
    //LBS相关
    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude=location.getLatitude();
            longitude=location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

}
