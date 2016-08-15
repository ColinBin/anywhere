package com.colinlvbin.extreme.anywhere.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.R;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{


    private EditText userIdInput;
    private EditText passwordInput;
    private EditText passwordRepeatInput;
    private EditText usernameInput;
    private EditText emailInput;
    private Button signUpButton;
    private Button cancelButton;

    private String user_id="";
    private String password="";
    private String password_repeat="";
    private String username="";
    private String email="";


    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userIdInput=(EditText)findViewById(R.id.user_id_sign_up);
        usernameInput=(EditText)findViewById(R.id.username_sign_up);
        passwordInput=(EditText)findViewById(R.id.password_sign_up);
        passwordRepeatInput=(EditText)findViewById(R.id.password_repeat_sign_up);
        emailInput=(EditText)findViewById(R.id.email_sign_up);

        signUpButton=(Button)findViewById(R.id.sign_up_button_sign_up);
        cancelButton=(Button)findViewById(R.id.cancel_button_sign_up);
        signUpButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        requestQueue= NoHttp.newRequestQueue();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_up_button_sign_up:
                user_id=userIdInput.getText().toString().trim();
                password=passwordInput.getText().toString().trim();
                password_repeat=passwordRepeatInput.getText().toString().trim();
                username=usernameInput.getText().toString().trim();
                email=emailInput.getText().toString().trim();

                if(TextUtils.isEmpty(user_id)){
                    userIdInput.setError("用户名不能为空");
                    userIdInput.requestFocus();
                }else if(!user_id.matches("^[\\da-zA-Z]*$")) {
                    userIdInput.setError("用户名由字母或数字组成");
                    userIdInput.requestFocus();
                }else if(password.length()<6||password.length()>20){
                    passwordInput.setError("密码长度[6,20]");
                    passwordInput.requestFocus();
                }else if(!password.equals(password_repeat)){
                    passwordRepeatInput.setError("两次输入密码不一样");
                    passwordRepeatInput.requestFocus();
                }else if(TextUtils.isEmpty(username)){
                    usernameInput.setError("昵称不能为空");
                    usernameInput.requestFocus();
                }else if(TextUtils.isEmpty(email)){
                    emailInput.setError("请填写邮箱");
                    emailInput.requestFocus();
                }else if(!email.contains("@")){
                    emailInput.setError("邮箱格式不正确");
                    emailInput.requestFocus();
                }else{
                    //验证用户注册信息
                    Request<JSONObject>signUpRequest=NoHttp.createJsonObjectRequest(Config
                            .SERVER_IP+"/sign_up",RequestMethod.POST);
                    signUpRequest.add("user_id",user_id);
                    signUpRequest.add("password",password);
                    signUpRequest.add("username",username);
                    signUpRequest.add("email",email);
                    requestQueue.add(Config.REQUEST_SIGN_UP,signUpRequest,onResponseListener);
                }

                break;
            case R.id.cancel_button_sign_up:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
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
                        case Config.REQUEST_SIGN_UP:

                            if(response.getHeaders().getResponseCode()==200){
                                //验证成功进入主界面
                                JSONObject result=response.get();
                                try {
                                    int permission=result.getInt("permission");
                                    switch (permission){
                                        case Config.SIGN_UP_SUCCESS:
                                            Toast.makeText(SignUpActivity.this, "注册成功", Toast
                                                    .LENGTH_SHORT).show();
                                            Intent toLoginActivity=new Intent();
                                            toLoginActivity.putExtra("user_id",user_id);
                                            toLoginActivity.putExtra("password",password);
                                            setResult(RESULT_OK,toLoginActivity);
                                            finish();
                                            break;
                                        case Config.USER_ALREADY_EXIST:
                                            userIdInput.setError("用户已存在");
                                            userIdInput.requestFocus();
                                            break;
                                        default:
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
