package com.colinlvbin.extreme.anywhere.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.MyApplication;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.model.User;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigurePostActivity extends AppCompatActivity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{

    private EditText postTitleInput;
    private CheckBox hasCipherCheckBox;
    private RadioGroup postStyleGroup;
    private EditText postCipherInput;
    private Button confirmConfigurePostButton;
    private Button cancelConfigurePostButton;
    private TextView locationDescriptionDisplay;

    private User user;
    private double longitude;
    private double latitude;
    private String location_description="";

    RequestQueue requestQueue;

    private int has_cipher=0;
    private int post_style;
    private String post_cipher;
    private String post_title;

    //语言相关
    String language;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_post);
        requestQueue= NoHttp.newRequestQueue();
        user=(User)getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);

        latitude=((MyApplication)getApplication()).GetLatitude();
        longitude=((MyApplication)getApplication()).GetLongitude();
        postTitleInput=(EditText)findViewById(R.id.post_title_configure_post);
        postStyleGroup=(RadioGroup)findViewById(R.id.post_style_group_configure_post);
        postCipherInput=(EditText)findViewById(R.id.post_cipher_configure_post);
        hasCipherCheckBox=(CheckBox)findViewById(R.id.has_cipher_configure_post);
        locationDescriptionDisplay=(TextView)findViewById(R.id
                .location_description_configure_post);
        confirmConfigurePostButton=(Button)findViewById(R.id.confirm_button_configure_post);
        cancelConfigurePostButton=(Button)findViewById(R.id.cancel_button_configure_post);
        confirmConfigurePostButton.setOnClickListener(this);
        cancelConfigurePostButton.setOnClickListener(this);
        //初始化radio group和post style
        postStyleGroup.setOnCheckedChangeListener(this);
        post_style=1;
        hasCipherCheckBox.setChecked(false);
        postCipherInput.setEnabled(false);
        hasCipherCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                    postCipherInput.setText("");
                    postCipherInput.setEnabled(false);
                }else{
                    postCipherInput.setEnabled(true);
                }
            }
        });
        language=((MyApplication)getApplication()).GetLanguage();

        Request<JSONObject>getLocationDescriptionRequest=NoHttp.createJsonObjectRequest(Config
                .SERVER_IP+"/get_location_description",
                RequestMethod.POST);
        getLocationDescriptionRequest.add("longitude",longitude);
        getLocationDescriptionRequest.add("latitude",latitude);
        if(language.endsWith("zh")){
            getLocationDescriptionRequest.add("language","zh");
        }else{
            getLocationDescriptionRequest.add("language","en");
        }

        requestQueue.add(Config.REQUEST_GET_LOCATION_DESCRIPTION,getLocationDescriptionRequest,
                onResponseListener);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.confirm_button_configure_post:
                post_title=postTitleInput.getText().toString().trim();
                if(hasCipherCheckBox.isChecked()){
                    has_cipher=1;
                    post_cipher=postCipherInput.getText().toString().trim();
                }else{
                    has_cipher=0;
                    post_cipher="";
                }
                if(TextUtils.isEmpty(post_title)){
                    postTitleInput.setError(this.getResources().getString(R.string
                            .title_cannot_be_empty));
                    postTitleInput.requestFocus();
                }else if(has_cipher==1&&(post_cipher.length()<6||post_cipher.length()>20)){
                    postCipherInput.setError(this.getResources().getString(R.string
                            .password_length));
                    postCipherInput.requestFocus();
                }else{
                    Intent toCreatePost=new Intent(ConfigurePostActivity.this,CreatePostActivity.class);
                    toCreatePost.putExtra("post_title",post_title);
                    toCreatePost.putExtra("post_style",post_style);
                    toCreatePost.putExtra("has_cipher",has_cipher);
                    toCreatePost.putExtra("post_cipher",post_cipher);
                    toCreatePost.putExtra("location_description",location_description);
                    toCreatePost.putExtra("longitude",longitude);
                    toCreatePost.putExtra("latitude",latitude);
                    Bundle userInfoBundle=new Bundle();
                    userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO,user);
                    toCreatePost.putExtras(userInfoBundle);
                    startActivityForResult(toCreatePost,Config.CONFIGURE_TO_CREATE_POST_REQUESTCODE);
                }


                break;
            case R.id.cancel_button_configure_post:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
        }
    }

    OnResponseListener<JSONObject> onResponseListener=new OnResponseListener<JSONObject>(){

        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<JSONObject> response) {
            switch(what){
                case Config.REQUEST_GET_LOCATION_DESCRIPTION:
                    if(response.getHeaders().getResponseCode()==200){

                        try {
                            JSONObject result=response.get();
                            int permission=result.getInt("permission");
                            switch(permission){
                                case Config.GET_LOCATION_DESCRIPTION_SUCCESS:
                                    location_description=result.getString("location_description");
                                    locationDescriptionDisplay.setText(location_description);
                                    break;
                                case Config.LOCATION_DESCRIPTION_SERVICE_UNAVAILABLE:
                                    locationDescriptionDisplay.setText(getString(R
                                            .string.fail_to_get_location_description));
                                    break;
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
        public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Config.CONFIGURE_TO_CREATE_POST_REQUESTCODE:
                if(resultCode==RESULT_OK){
                    setResult(RESULT_OK);
                    finish();
                }
                break;
            default:
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(group==postStyleGroup){
            switch (checkedId){
                case R.id.post_style1_configure_post:
                    post_style=1;
                    break;
                case R.id.post_style2_configure_post:
                    post_style=2;
                    break;
                case R.id.post_style3_configure_post:
                    post_style=3;
                    break;
                default:
            }
        }
    }
}
