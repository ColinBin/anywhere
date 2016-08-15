package com.colinlvbin.extreme.anywhere.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.RoutineOps;
import com.colinlvbin.extreme.anywhere.model.User;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;

public class CreatePostActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView postTitleDisplay;
    private TextView postCreatorDisplay;
    private EditText postContentInput;
    private Button addPictureButton;
    private Button confirmCreatePostButton;
    private Button cancelCreatePostButton;
    private ImageView postPictureImage;

    private User user;
    private int has_picture;
    private RequestQueue requestQueue;
    private String postPicturePath;
    private Bitmap postPictureBitmap;

    private JSONObject location_description;
    private String post_title;
    private String post_content;
    private int post_style;
    private int has_cipher;
    private String post_cipher;
    private double longitude;
    private double latitude;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        requestQueue= NoHttp.newRequestQueue();

        postTitleDisplay=(TextView)findViewById(R.id.post_title_create_post);
        postCreatorDisplay=(TextView)findViewById(R.id.post_creator_create_post);
        postContentInput=(EditText)findViewById(R.id.post_content_create_post);
        postPictureImage=(ImageView)findViewById(R.id.post_picture_create_post);
        addPictureButton=(Button)findViewById(R.id.add_pic_button_create_post);
        confirmCreatePostButton=(Button)findViewById(R.id.confirm_button_create_post);
        cancelCreatePostButton=(Button)findViewById(R.id.cancel_button_create_post);
        addPictureButton.setOnClickListener(this);
        confirmCreatePostButton.setOnClickListener(this);
        cancelCreatePostButton.setOnClickListener(this);

        user=(User)getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);
        post_title=getIntent().getStringExtra("post_title");
        post_style=getIntent().getIntExtra("post_style",-1);
        has_cipher=getIntent().getIntExtra("has_cipher",-1);
        post_cipher=getIntent().getStringExtra("post_cipher");
        longitude=getIntent().getDoubleExtra("longitude",0);
        latitude=getIntent().getDoubleExtra("latitude",0);
        try {
            location_description=new JSONObject(getIntent().getStringExtra("location_description"));
        } catch (JSONException e) {
            e.printStackTrace();
            RoutineOps.MakeToast(CreatePostActivity.this,"Oops>>>>>>>");
        }

        //填补信息
        postTitleDisplay.setText(post_title);
        postCreatorDisplay.setText(user.getUser_id());


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_pic_button_create_post:
                //从相册中选择照片并进行裁剪
                Intent toAlbumSelection=new Intent(Intent.ACTION_PICK,null);
                toAlbumSelection.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(toAlbumSelection, Config.CREAT_POST_TO_ADD_PIC_REQUESTCODE);
                break;
            case R.id.confirm_button_create_post:
                post_content=postContentInput.getText().toString().trim();
                Request<JSONObject>createPostRequest=NoHttp.createJsonObjectRequest(Config
                        .SERVER_IP+"/create_post", RequestMethod.POST);
                createPostRequest.add("post_title",post_title);
                createPostRequest.add("post_content",post_content);
                createPostRequest.add("post_style",post_style);
                createPostRequest.add("has_cipher",has_cipher);
                createPostRequest.add("post_cipher",post_cipher);
                createPostRequest.add("user_id",user.getUser_id());
                createPostRequest.add("longitude",longitude);
                createPostRequest.add("latitude",latitude);
                Iterator<String>location_description_keys=location_description.keys();
                //将位置描述信息加入
                try{
                    while(location_description_keys.hasNext()){
                        String key=location_description_keys.next();
                        createPostRequest.add(key,
                                location_description.getString(key));
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
                if(has_picture==1){
                    createPostRequest.add("has_picture",1);
                    createPostRequest.add("post_picture",new FileBinary(new File(postPicturePath)));
                }else{
                    createPostRequest.add("has_picture",0);
                }
                requestQueue.add(Config.REQUEST_CREATE_POST,createPostRequest,onResponseListener);

                break;
            case R.id.cancel_button_create_post:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.CREAT_POST_TO_ADD_PIC_REQUESTCODE:
                if (data != null) {
                    Uri originalUri = data.getData();
                    ContentResolver resolver = null;
                    try {
                        resolver = getContentResolver();
                        postPictureBitmap = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        postPictureImage.setImageBitmap(postPictureBitmap);
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        postPicturePath = cursor.getString(column_index);
                        has_picture=1;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }
    private OnResponseListener<JSONObject> onResponseListener = new
            OnResponseListener<JSONObject>(){
                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int
                        responseCode, long networkMillis) {

                }

                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<JSONObject> response) {
                    switch(what){
                        case Config.REQUEST_CREATE_POST:
                            if(response.getHeaders().getResponseCode()==200){
                                JSONObject result=response.get();
                                try {
                                    int permission=result.getInt("permission");
                                    if(permission==Config.CREATE_POST_SUCCESS){
                                        RoutineOps.MakeToast(CreatePostActivity.this,"创建成功");
                                        setResult(RESULT_OK);
                                        finish();
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
                public void onFinish(int what) {

                }
            };
}
