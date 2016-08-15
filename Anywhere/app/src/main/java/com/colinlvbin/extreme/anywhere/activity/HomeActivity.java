package com.colinlvbin.extreme.anywhere.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.colinlvbin.extreme.anywhere.Config;
import com.colinlvbin.extreme.anywhere.R;
import com.colinlvbin.extreme.anywhere.RoutineOps;
import com.colinlvbin.extreme.anywhere.model.User;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView avatarNav;
    private TextView usernameNav;
    private TextView emailNav;

    private User user;
    private String username;
    private String email;
    private int has_avatar;

    private Bitmap avatarBitmap;
    private RequestQueue requestQueue;

    //LBS相关
    private LocationManager locationManager;
    private Location location;
    private String provider;
    private double longitude;
    private double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        requestQueue= NoHttp.newRequestQueue();

        //LBS相关
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList=locationManager.getProviders(true);
        if(providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider=LocationManager.NETWORK_PROVIDER;
        }else if(providerList.contains(LocationManager.GPS_PROVIDER)){
            provider= LocationManager.GPS_PROVIDER;
        }else{
            provider=null;
            RoutineOps.MakeToast(HomeActivity.this,"请打开LBS服务后重试");
        }
        if(provider!=null){
            locationManager.requestLocationUpdates(provider,5000,1,locationListener);
        }

        avatarNav=(ImageView)findViewById(R.id.avatar_nav_home);
        usernameNav=(TextView)findViewById(R.id.username_nav_home);
        emailNav=(TextView)findViewById(R.id.email_nav_home);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        user=(User)getIntent().getExtras().getSerializable(Config.BUNDLE_USER_INFO);
        InitializeInformation(user);



    }

    private void InitializeInformation(User user){

        usernameNav.setText(user.getUsername());
        emailNav.setText(user.getEmail());

        has_avatar=user.getHas_avatar();
        if(has_avatar==1){
            Request<Bitmap>getAvatarRequest=NoHttp.createImageRequest(Config
                    .SERVER_IP+"/get_avatar",
                    RequestMethod.POST);
            getAvatarRequest.add("user_id",user.getUser_id());
            requestQueue.add(Config.REQUEST_GET_AVATAR,getAvatarRequest,onBitmapResponseListener);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Bundle userInfoBundle=new Bundle();
        int id = item.getItemId();
        switch(item.getItemId()){
            case R.id.nav_settings:

                break;
            default:
        }
        if (id == R.id.nav_create_post) {
            Intent toConfigruePost=new Intent(HomeActivity.this,ConfigurePostActivity.class);
            userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO,user);
            toConfigruePost.putExtras(userInfoBundle);
            toConfigruePost.putExtra("longitude",longitude);
            toConfigruePost.putExtra("latitude",latitude);

            startActivityForResult(toConfigruePost,Config.HOME_TO_CONFIGURE_POST_REQUESTCODE);

        } else if (id == R.id.nav_show_posts) {
            Intent toShowPosts=new Intent(HomeActivity.this,ShowPostsActivity.class);
            userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO,user);
            toShowPosts.putExtras(userInfoBundle);
            toShowPosts.putExtra("longitude",longitude);
            toShowPosts.putExtra("latitude",latitude);
            startActivity(toShowPosts);



        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_settings) {
            Intent toSettingsNavActivity=new Intent(HomeActivity.this,SettingsNavActivity.class);
            userInfoBundle.putSerializable(Config.BUNDLE_USER_INFO,user);
            toSettingsNavActivity.putExtras(userInfoBundle);
            startActivityForResult(toSettingsNavActivity,Config.HOME_TO_SETTINGS_REQUESTCODE);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(HomeActivity.this);
            alertDialog.setCancelable(true);
            alertDialog.setTitle("注销");
            alertDialog.setMessage("是否注销");
            alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent toLoginActivity=new Intent(HomeActivity.this,LoginActivity.class);
                    toLoginActivity.putExtra("from_home",true);
                    startActivity(toLoginActivity);
                    finish();
                }
            });
            alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

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
                                    avatarNav.setImageBitmap(avatarBitmap);
                                }else {
                                    RoutineOps.MakeToast(HomeActivity.this, "头像加载失败\n请在设置界面重新上传");
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
            case Config.HOME_TO_SETTINGS_REQUESTCODE:
                if(resultCode==RESULT_OK){
                    user=(User)data.getExtras().getSerializable(Config.BUNDLE_USER_INFO);
                    InitializeInformation(user);
                }
                break;
            case Config.HOME_TO_CONFIGURE_POST_REQUESTCODE:


                break;
            default:
        }
    }


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
