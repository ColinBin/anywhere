package com.colinlvbin.extreme.anywhere.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.model.User;

public class SettingsNavActivity extends AppCompatActivity implements View.OnClickListener{

    private Button basicSettingsButton;
    private Button passwordSettingsButton;
    private Button cancelSettingsButton;
    private Button configureSettingsButton;
    private User user;
    private Boolean has_altered_profile=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_nav);
        basicSettingsButton=(Button)findViewById(R.id.basic_settings);
        passwordSettingsButton=(Button)findViewById(R.id.password_settings);
        cancelSettingsButton=(Button)findViewById(R.id.return_nav_settings);
        configureSettingsButton=(Button)findViewById(R.id.configure_settings);
        basicSettingsButton.setOnClickListener(this);
        passwordSettingsButton.setOnClickListener(this);
        cancelSettingsButton.setOnClickListener(this);
        configureSettingsButton.setOnClickListener(this);
        user=(User)getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);

        //初始化为没有进行信息修改
        setResult(RESULT_CANCELED);
        has_altered_profile=false;

    }



    @Override
    public void onClick(View v) {
        Bundle userInfoBundle=new Bundle();
        switch(v.getId()){
            case R.id.basic_settings:
                Intent toBasicSettings=new Intent(SettingsNavActivity.this,SettingsBasicActivity
                        .class);
                userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO,user);
                toBasicSettings.putExtras(userInfoBundle);
                startActivityForResult(toBasicSettings,Config.SETTINGS_TO_BASIC_REQUESTCODE);
                break;
            case R.id.password_settings:
                Intent toPasswordSettings=new Intent(SettingsNavActivity.this,
                        SettingsPasswordActivity.class);
                userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO,user);
                toPasswordSettings.putExtras(userInfoBundle);
                startActivity(toPasswordSettings);
                break;
            case R.id.configure_settings:
                Intent toConfigureSettings=new Intent(SettingsNavActivity.this,
                        SettingsConfigureActivity.class);
                startActivity(toConfigureSettings);
                break;
            case R.id.return_nav_settings:
                if(has_altered_profile){
                    Intent returnToHomeActivity=new Intent();
                    Bundle returnBundle=new Bundle();
                    returnBundle.putSerializable(Config.BUNDLE_USER_INFO,user);
                    returnToHomeActivity.putExtras(returnBundle);
                    setResult(RESULT_OK,returnToHomeActivity);
                }else{
                    setResult(RESULT_CANCELED);
                }
                finish();
                break;


            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case Config.SETTINGS_TO_BASIC_REQUESTCODE:
                if(resultCode==RESULT_OK){
                    has_altered_profile=true;
                    //如果修改了Basic information部分更新user实例
                    user=(User)data.getExtras().getSerializable(Config.BUNDLE_USER_INFO);
                    //返回HomeActivity时进行数据修改
                    setResult(RESULT_OK);
                }
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        if(has_altered_profile){
            Intent returnToHomeActivity=new Intent();
            Bundle returnBundle=new Bundle();
            returnBundle.putSerializable(Config.BUNDLE_USER_INFO,user);
            returnToHomeActivity.putExtras(returnBundle);
            setResult(RESULT_OK,returnToHomeActivity);
        }else{
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
