package com.colinlvbin.extreme.anywhere.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.MD5;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.RoutineOps;
import com.colinlvbin.extreme.anywhere.model.User;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText originalPasswordInput;
    private EditText newPasswordInput;
    private EditText newPasswordRepeatInput;
    private Button alterPasswordConfirmButton;
    private Button alterPasswordCancelButton;

    private String original_password;
    private String new_password;
    private String new_password_repeat;

    private User user;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_password);
        originalPasswordInput=(EditText)findViewById(R.id.original_password_settings);
        newPasswordInput=(EditText)findViewById(R.id.new_password_settings);
        newPasswordRepeatInput=(EditText)findViewById(R.id.new_password_repeat_settings);
        alterPasswordConfirmButton=(Button)findViewById(R.id.alter_password_button_settings);
        alterPasswordCancelButton=(Button)findViewById(R.id.cancel_password_button_settings);
        alterPasswordConfirmButton.setOnClickListener(this);
        alterPasswordCancelButton.setOnClickListener(this);

        user=(User)getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);

        requestQueue= NoHttp.newRequestQueue();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.alter_password_button_settings:
                original_password=originalPasswordInput.getText().toString().trim();
                new_password=newPasswordInput.getText().toString().trim();
                new_password_repeat=newPasswordRepeatInput.getText().toString().trim();
                //检查密码格式
                if(original_password.length()<6||original_password.length()>20){
                    originalPasswordInput.setError(this.getResources().getString(R.string.password_length));
                    originalPasswordInput.requestFocus();
                }else if(new_password.length()<6||new_password.length()>20){
                    newPasswordInput.setError(this.getResources().getString(R.string.password_length));
                    newPasswordInput.requestFocus();
                }else if(!new_password_repeat.equals(new_password)){
                    newPasswordRepeatInput.setError(this.getResources().getString(R.string.password_inconsistent));
                    newPasswordRepeatInput.requestFocus();
                }else{
                    //验证密码正确性
                    Request<JSONObject>alterPasswordRequest=NoHttp.createJsonObjectRequest(Config
                            .SERVER_IP+"/alter_password", RequestMethod.POST);
                    alterPasswordRequest.add("user_id",user.getUser_id());
                    alterPasswordRequest.add("original_password", MD5.GetMD5(original_password));
                    alterPasswordRequest.add("new_password",new_password);
                    requestQueue.add(Config.REQUEST_ALTER_PASSWORD,alterPasswordRequest,
                            onResponseListener);
                }
                break;
            case R.id.cancel_password_button_settings:
                finish();
                break;
        }
    }

    private OnResponseListener<JSONObject> onResponseListener = new
            OnResponseListener<JSONObject>() {

                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<JSONObject> response) {
                    switch (what){
                        case Config.REQUEST_ALTER_PASSWORD:
                            if(response.getHeaders().getResponseCode()==200){
                                JSONObject result=response.get();
                                try {
                                    int permission=result.getInt("permission");
                                    switch (permission){
                                        case Config.PASSWORD_WRONG:
                                            RoutineOps.MakeToast(SettingsPasswordActivity.this,
                                                    SettingsPasswordActivity.this.getResources()
                                                            .getString(R.string.password_wrong));
                                            break;
                                        case Config.ALTER_PASSWORD_SUCCESS:
                                            RoutineOps.MakeToast(SettingsPasswordActivity.this,
                                                    SettingsPasswordActivity.this.getResources()
                                                            .getString(R.string.alter_password_succeed));
                                            finish();
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
                public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {

                }

                @Override
                public void onFinish(int what) {

                }
            };
}
