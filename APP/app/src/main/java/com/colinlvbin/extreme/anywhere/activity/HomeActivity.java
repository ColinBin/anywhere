package com.colinlvbin.extreme.anywhere.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.MyApplication;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.RoutineOps;
import com.colinlvbin.extreme.anywhere.model.User;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ImageView avatarNav;
    private TextView usernameNav;
    private TextView emailNav;

    private User user;
    private int has_avatar;

    private Bitmap avatarBitmap;
    private RequestQueue requestQueue;

    //LBS相关
    private String provider;

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        provider=((MyApplication)getApplication()).GetProvider();
        if(provider==null){
            RoutineOps.MakeToast(this,getString(R.string.please_enable_LBS));
        }

        requestQueue = NoHttp.newRequestQueue();

        avatarNav = (ImageView) findViewById(R.id.avatar_nav_home);
        usernameNav = (TextView) findViewById(R.id.username_nav_home);
        emailNav = (TextView) findViewById(R.id.email_nav_home);
        avatarNav.setOnClickListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user = (User) getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);
        InitializeInformation(user);


    }

    private void InitializeInformation(User user) {

        usernameNav.setText(user.getUsername());
        emailNav.setText(user.getEmail());

        has_avatar = user.getHas_avatar();
        if (has_avatar == 1) {
            Request<Bitmap> getAvatarRequest = NoHttp.createImageRequest(Config
                            .SERVER_IP + "/get_avatar",
                    RequestMethod.POST);
            getAvatarRequest.add("user_id", user.getUser_id());
            requestQueue.add(Config.REQUEST_GET_AVATAR, getAvatarRequest, onBitmapResponseListener);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Bundle userInfoBundle = new Bundle();
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.nav_create_post:
                if(provider==null){
                    RoutineOps.MakeToast(HomeActivity.this,getString(R.string.please_enable_LBS));
                    break;
                }
                Intent toConfigruePost = new Intent(HomeActivity.this, ConfigurePostActivity.class);
                userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO, user);
                toConfigruePost.putExtras(userInfoBundle);

                startActivityForResult(toConfigruePost, Config.HOME_TO_CONFIGURE_POST_REQUESTCODE);
                break;
            case R.id.nav_show_posts:
                if(provider==null){
                    RoutineOps.MakeToast(this,getString(R.string.please_enable_LBS));
                }
                Intent toShowPosts = new Intent(HomeActivity.this, ShowPostsActivity.class);
                userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO, user);
                toShowPosts.putExtras(userInfoBundle);
                startActivity(toShowPosts);
                break;
            case R.id.nav_slideshow:

                break;
            case R.id.nav_settings:
                Intent toSettingsNavActivity = new Intent(HomeActivity.this, SettingsNavActivity.class);
                userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO, user);
                toSettingsNavActivity.putExtras(userInfoBundle);
                startActivityForResult(toSettingsNavActivity, Config.HOME_TO_SETTINGS_REQUESTCODE);
                break;
            case R.id.nav_share:

                break;
            case R.id.nav_logout:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                alertDialog.setCancelable(true);
                alertDialog.setTitle(R.string.log_out);
                alertDialog.setMessage(R.string.log_out_info);
                alertDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent toLoginActivity = new Intent(HomeActivity.this, LoginActivity.class);
                        toLoginActivity.putExtra("from_home", true);
                        startActivity(toLoginActivity);
                        finish();
                    }
                });
                alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
                break;

            default:
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private OnResponseListener<Bitmap> onBitmapResponseListener = new
            OnResponseListener<Bitmap>() {
                @Override
                public void onFailed(int what, String url, Object tag, Exception exception, int
                        responseCode, long networkMillis) {

                }

                @Override
                public void onStart(int what) {

                }

                @Override
                public void onSucceed(int what, Response<Bitmap> response) {
                    switch (what) {
                        case Config.REQUEST_GET_AVATAR:
                            if (response.getHeaders().getResponseCode() == 200) {
                                avatarBitmap = response.get();
                                if (avatarBitmap != null) {
                                    avatarNav.setImageBitmap(avatarBitmap);
                                } else {
                                    RoutineOps.MakeToast(HomeActivity.this, HomeActivity.this
                                            .getResources().getString(R.string.avatar_not_available));
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
        switch (requestCode) {
            case Config.HOME_TO_SETTINGS_REQUESTCODE:
                if (resultCode == RESULT_OK) {
                    user = (User) data.getExtras().getSerializable(Config.BUNDLE_USER_INFO);
                    InitializeInformation(user);
                }
                break;
            case Config.HOME_TO_CONFIGURE_POST_REQUESTCODE:


                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        Bundle userInfoBundle = new Bundle();
        switch(v.getId()){
            case R.id.avatar_nav_home:
                Intent toSettingsNavActivity = new Intent(HomeActivity.this, SettingsNavActivity.class);
                userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO, user);
                toSettingsNavActivity.putExtras(userInfoBundle);
                startActivityForResult(toSettingsNavActivity, Config.HOME_TO_SETTINGS_REQUESTCODE);
                break;
            default:
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.press_again_to_exit),
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
