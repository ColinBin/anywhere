package com.colinlvbin.extreme.anywhere.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.ParseInformation;
import com.colinlvbin.extreme.anywhere.R;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText userIdInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button signUpButton;
    private CheckBox rememberInfoCheckBox;
    private CheckBox autoLoginCheckBox;

    private String user_id="";
    private String password="";

    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences=getSharedPreferences("user_login_info", MODE_PRIVATE);
        userIdInput=(EditText)findViewById(R.id.user_id_login);
        passwordInput=(EditText)findViewById(R.id.password_login);
        loginButton=(Button)findViewById(R.id.login_button_login);
        signUpButton=(Button)findViewById(R.id.sign_up_button_login);
        loginButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        rememberInfoCheckBox=(CheckBox)findViewById(R.id.remember_info_login);
        autoLoginCheckBox=(CheckBox)findViewById(R.id.auto_login_login);
        //如果自动登录被勾选，则也勾选记住登录信息
        autoLoginCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    rememberInfoCheckBox.setChecked(true);
                }
            }
        });

        requestQueue=NoHttp.newRequestQueue();
        //如果自动登录则取出信息并调用登录函数，记住信息则填充信息
        if(sharedPreferences.getBoolean("AUTO_LOGIN",false)){
            rememberInfoCheckBox.setChecked(true);
            autoLoginCheckBox.setChecked(true);
            userIdInput.setText(sharedPreferences.getString("user_id",""));
            passwordInput.setText(sharedPreferences.getString("password",""));
            if(!getIntent().getBooleanExtra("from_home",false)){
                Login(sharedPreferences.getString("user_id",""),sharedPreferences.getString
                        ("password",""));
            }


        }else if(sharedPreferences.getBoolean("REMEMBER_INFO",false)){
            userIdInput.setText(sharedPreferences.getString("user_id",""));
            passwordInput.setText(sharedPreferences.getString("password",""));
            rememberInfoCheckBox.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login_button_login:
                user_id=userIdInput.getText().toString().trim();
                password=passwordInput.getText().toString().trim();

                if(TextUtils.isEmpty(user_id)){
                    userIdInput.setError("用户名不能为空");
                    userIdInput.requestFocus();
                }else if(TextUtils.isEmpty(password)){
                    passwordInput.setError("密码不能为空");
                    passwordInput.requestFocus();
                }else if(password.length()<6||password.length()>20){
                    passwordInput.requestFocus();
                    passwordInput.setError("密码长度[6,20]");
                }else{
                    //登录信息验证
                    Login(user_id,password);
                }

                break;
            case R.id.sign_up_button_login:
                //进入注册界面
                Intent toSignUpActivity=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivityForResult(toSignUpActivity,Config.LOGIN_TO_SIGN_UP_REQUESTCODE);

                break;
            default:
        }
    }

    private void Login(String user_id,String password){
        Request<JSONObject> loginRequest= NoHttp.createJsonObjectRequest(Config
                .SERVER_IP+"/login", RequestMethod.CONNECT.POST);
        loginRequest.add("user_id",user_id);
        loginRequest.add("password",password);
        requestQueue.add(Config.REQUEST_LOGIN,loginRequest,onResponseListener);
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
                        case Config.REQUEST_LOGIN:
                            if(response.getHeaders().getResponseCode()==200){
                                //验证成功进入主界面
                                JSONObject result=response.get();
                                try {
                                    int permission=result.getInt("permission");
                                    switch (permission){
                                        case Config.LOGIN_SUCCESS:
                                            JSONObject userInfo=response.get();
                                            Toast.makeText(LoginActivity.this,"登录成功",Toast
                                                    .LENGTH_SHORT).show();
                                            SharedPreferences.Editor editor=sharedPreferences
                                                    .edit();
                                            editor.putString("user_id",user_id);
                                            editor.putString("password",password);
                                            if(autoLoginCheckBox.isChecked()){
                                                editor.putBoolean("AUTO_LOGIN",true);
                                                editor.putBoolean("REMEMBER_INFO",true);
                                            }else if(rememberInfoCheckBox.isChecked()){
                                                editor.putBoolean("REMEMBER_INFO",true);
                                                editor.putBoolean("AUTO_LOGIN",false);
                                            }else{
                                                editor.putBoolean("REMEMBER_INFO",false);
                                                editor.putBoolean("AUTO_LOGIN",false);
                                            }
                                            editor.apply();
                                            Intent toHomeActivity=new Intent(LoginActivity.this,HomeActivity.class);

                                            Bundle userInfoBundle=new Bundle();
                                            userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO,
                                                    ParseInformation
                                                    .ParseUser(userInfo));
                                            toHomeActivity.putExtras(userInfoBundle);

                                            startActivity(toHomeActivity);
                                            finish();
                                            break;
                                        case Config.USER_NOT_FOUND:
                                            userIdInput.setError("用户不存在");
                                            userIdInput.requestFocus();
                                            break;
                                        case Config.PASSWORD_WRONG:
                                            passwordInput.setError("密码错误");
                                            passwordInput.requestFocus();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case Config.LOGIN_TO_SIGN_UP_REQUESTCODE:
                if(resultCode==RESULT_OK){
                    user_id=data.getStringExtra("user_id");
                    password=data.getStringExtra("password");
                    userIdInput.setText(user_id);
                    passwordInput.setText(password);
                }
                break;
            default:
        }

    }
}
