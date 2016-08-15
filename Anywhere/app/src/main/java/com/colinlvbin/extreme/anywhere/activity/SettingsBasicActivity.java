package com.colinlvbin.extreme.anywhere.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsBasicActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView avatarBasicSettings;
    private EditText usernameSettingsInput;
    private EditText emailSettingsInput;
    private Button basicSettingsConfirmButton;
    private Button basicSettingsCancelButton;

    private boolean has_altered_info=false;
    private boolean has_altered_avatar=false;

    private RequestQueue requestQueue;
    private Bitmap avatarBitmap;

    private User user;

    private String avatarPath="";
    private String username="";
    private String email="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_basic);

        requestQueue= NoHttp.newRequestQueue();

        avatarBasicSettings=(ImageView)findViewById(R.id.avatar_settings);
        usernameSettingsInput=(EditText)findViewById(R.id.username_settings);
        emailSettingsInput=(EditText)findViewById(R.id.email_settings);

        basicSettingsConfirmButton=(Button)findViewById(R.id.change_basic_button_settings);
        basicSettingsCancelButton=(Button)findViewById(R.id.cancel_basic_button_settings);

        basicSettingsConfirmButton.setOnClickListener(this);
        basicSettingsCancelButton.setOnClickListener(this);
        avatarBasicSettings.setOnClickListener(this);
        //增加监听器，如果修改则改变has_altered_info的值
        usernameSettingsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                has_altered_info=true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailSettingsInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                has_altered_info=true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        user=(User)getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);
        InitializeInformation(user);

        //初始化为没有修改信息
        setResult(RESULT_CANCELED);
        has_altered_info=false;
        has_altered_avatar=false;
    }

    private void InitializeInformation(User user){
        usernameSettingsInput.setText(user.getUsername());
        emailSettingsInput.setText(user.getEmail());
        if(user.getHas_avatar()==1){
            Request<Bitmap>getAvatarRequest=NoHttp.createImageRequest(Config
                    .SERVER_IP+"/get_avatar",RequestMethod.POST);
            getAvatarRequest.add("user_id",user.getUser_id());
            requestQueue.add(Config.REQUEST_GET_AVATAR,getAvatarRequest,onBitmapResponseListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.avatar_settings:
                //从相册中选择照片并进行裁剪
                Intent toAlbumSelection=new Intent(Intent.ACTION_PICK,null);
                toAlbumSelection.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(toAlbumSelection, Config.BASIC_TO_PICK_PIC_REQUESTCODE);
                break;

            case R.id.change_basic_button_settings:
                if(has_altered_info){
                    //如果修改了信息
                    username=usernameSettingsInput.getText().toString().trim();
                    email=emailSettingsInput.getText().toString().trim();
                    //用户昵称和邮箱格式检查
                    if(TextUtils.isEmpty(username)){
                        usernameSettingsInput.setError("昵称不能为空");
                        usernameSettingsInput.requestFocus();
                        return;
                    }else if(!email.contains("@")){
                        emailSettingsInput.setError("邮箱格式不正确");
                        emailSettingsInput.requestFocus();
                        return;
                    }

                    Request<JSONObject> alterBasicInfoRequest=NoHttp.createJsonObjectRequest
                            (Config.SERVER_IP+"/alter_basic_information", RequestMethod.POST);
                    alterBasicInfoRequest.add("user_id",user.getUser_id());
                    alterBasicInfoRequest.add("username",username);
                    alterBasicInfoRequest.add("email",email);
                    //如果修改了头像则将头像上传
                    if(has_altered_avatar){
                        System.out.println("Here");
                        alterBasicInfoRequest.add("has_altered_avatar","1");
                        alterBasicInfoRequest.add("avatar",new FileBinary(new File(avatarPath)));
                    }else{
                        alterBasicInfoRequest.add("has_altered_avatar","0");
                    }
                    requestQueue.add(Config.REQUEST_ALTER_BASIC_INFO,alterBasicInfoRequest,
                            onResponseListener);
                }else{
                    RoutineOps.MakeToast(this,"没有修改信息");
                }
                break;
            case R.id.cancel_basic_button_settings:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Config.BASIC_TO_PICK_PIC_REQUESTCODE:
                if(data!=null){
                    startPhotoZoom(data.getData(),150);
                }
                break;

            case Config.BASIC_TO_CROP_PIC_REQUESTCODE:
                if(data!=null){
                    Bundle avatarBundle=data.getExtras();
                    if(avatarBundle!=null){
                        //更新ImageView
                        avatarBitmap=avatarBundle.getParcelable("data");
                        avatarBasicSettings.setImageBitmap(avatarBitmap);
                        //已经修改了头像
                        has_altered_avatar=true;
                        has_altered_info=true;
                        //暂时保存为文件
                        File avatarFile=new File(this.getCacheDir(),user.getUser_id()+".png");
                        try{
                            avatarFile.createNewFile();
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            avatarBitmap.compress(Bitmap.CompressFormat.PNG, 0 ,
                                    bos);
                            byte[] bitmapdata = bos.toByteArray();
                            FileOutputStream fos = new FileOutputStream(avatarFile);
                            fos.write(bitmapdata);
                            fos.flush();
                            fos.close();
                            //得到头像路径
                            avatarPath = avatarFile.getAbsolutePath();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:

        }
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, Config.BASIC_TO_CROP_PIC_REQUESTCODE);
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
                        case Config.REQUEST_ALTER_BASIC_INFO:
                            if(response.getHeaders().getResponseCode()==200){
                                JSONObject result=response.get();
                                try {
                                    int permission = result.getInt("permission");
                                    if(permission==Config.ALTER_BASIC_INFO_SUCCESS){
                                        RoutineOps.MakeToast(SettingsBasicActivity.this,"信息修改成功");
                                        user.setEmail(email);
                                        user.setUsername(username);
                                        if(has_altered_avatar){
                                            user.setHas_avatar(1);
                                        }
                                        Intent returnToSettingsNav=new Intent();
                                        Bundle userInfoBundle=new Bundle();
                                        userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO,
                                                user);
                                        returnToSettingsNav.putExtras(userInfoBundle);
                                        setResult(RESULT_OK,returnToSettingsNav);
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

    //加载用户头像
    private OnResponseListener<Bitmap> onBitmapResponseListener = new
            OnResponseListener<Bitmap>(){
                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int
                        responseCode, long networkMillis) {

                }

                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<Bitmap> response) {
                    switch(what){
                        case Config.REQUEST_GET_AVATAR:
                            if(response.getHeaders().getResponseCode()==200){
                                avatarBitmap=response.get();
                                if(avatarBitmap!=null){
                                    avatarBasicSettings.setImageBitmap(avatarBitmap);
                                }else {
                                    RoutineOps.MakeToast(SettingsBasicActivity.this, "头像加载失败");
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
