package com.graygrass.healthylife;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.graygrass.healthylife.fragment.CookFragment;
import com.graygrass.healthylife.fragment.FoodFragment;
import com.graygrass.healthylife.fragment.HealthFragment;
import com.graygrass.healthylife.fragment.HospitalFragment;
import com.graygrass.healthylife.fragment.MineFragment;
import com.graygrass.healthylife.fragment.SearchFragment;

import cn.sharesdk.framework.ShareSDK;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private TextView tv_isNetworkAvailable;
    private ImageView img_showleft, title_img_search;
    public static RelativeLayout layout1, layout2, layout3, layout4, layout5,layout6, layout_isNetworkAvailable;
    private DrawerLayout drawerLayout;
    private LinearLayout layout_left;
    private Button btn_isNetworkAvailable;
    private static Fragment healthFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShareSDK.initSDK(this);//shareSDK的初始化
        //不行，估计是API 的问题
        /*// 创建状态栏的管理实例
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        // 激活导航栏设置
        tintManager.setNavigationBarTintEnabled(true);*/

        initView();
        initListener();
        initData();
        isNetworkAvailable();//判断当前网络是否可用

    }

    private void initView() {
        layout_isNetworkAvailable = (RelativeLayout) findViewById(R.id.layout_isNetworkAvailable);
        tv_isNetworkAvailable = (TextView) findViewById(R.id.tv_isNetworkAvailable);
        btn_isNetworkAvailable = (Button) findViewById(R.id.btn_isNetworkAvailable);
        img_showleft = (ImageView) findViewById(R.id.img_showleft);
        title_img_search = (ImageView) findViewById(R.id.title_img_search);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        layout_left = (LinearLayout) findViewById(R.id.layout_left);
        layout1 = (RelativeLayout) findViewById(R.id.layout1);
        layout2 = (RelativeLayout) findViewById(R.id.layout2);
        layout3 = (RelativeLayout) findViewById(R.id.layout3);
        layout4 = (RelativeLayout) findViewById(R.id.layout4);
        layout5 = (RelativeLayout) findViewById(R.id.layout5);
        layout6 = (RelativeLayout) findViewById(R.id.layout6);
    }

    private void initListener() {
        tv_isNetworkAvailable.setOnClickListener(this);
        btn_isNetworkAvailable.setOnClickListener(this);
        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        layout4.setOnClickListener(this);
        layout5.setOnClickListener(this);
        layout6.setOnClickListener(this);
        img_showleft.setOnClickListener(this);
        title_img_search.setOnClickListener(this);
    }

    private void initData() {
        healthFragment = new HealthFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, healthFragment).commit();

        //侧滑页面显示2/3
        //获取屏幕宽度
        WindowManager wm = this.getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        //设置侧滑页面宽度
        layout_left.getLayoutParams().width = screenWidth / 3 * 2;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_showleft:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.title_img_search:
                clearLayoutColor();
                layout2.setBackgroundColor(Color.parseColor("#5086C340"));
                replaceFragment(new SearchFragment());
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.layout1:
                clearLayoutColor();
                layout1.setBackgroundColor(Color.parseColor("#5086C340"));
                replaceFragment(healthFragment);
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.layout2:
                clearLayoutColor();
                replaceFragment(new SearchFragment());
                layout2.setBackgroundColor(Color.parseColor("#5086C340"));
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.layout3:
                clearLayoutColor();
                replaceFragment(new FoodFragment());
                layout3.setBackgroundColor(Color.parseColor("#5086C340"));
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.layout4:
                clearLayoutColor();
                replaceFragment(new CookFragment());
                layout4.setBackgroundColor(Color.parseColor("#5086C340"));
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.layout5:
                clearLayoutColor();
                replaceFragment(new HospitalFragment());
                layout5.setBackgroundColor(Color.parseColor("#5086C340"));
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.layout6:
                clearLayoutColor();
                replaceFragment(new MineFragment());
                layout6.setBackgroundColor(Color.parseColor("#5086C340"));
                drawerLayout.closeDrawer(Gravity.LEFT);
                break;
            case R.id.tv_isNetworkAvailable:
//                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
                startActivity(new Intent(Settings.ACTION_SETTINGS));//进入设置去配置网络
                break;
            case R.id.btn_isNetworkAvailable:
                layout_isNetworkAvailable.setVisibility(View.GONE);//隐藏设置网络提示
                break;
        }
    }

    /**
     * 替换Fragment ，同时如果在显示其他Fragment时按返回键返回到HealthFragment
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment, "healthFragment").addToBackStack("healthFragment").commit();
    }

    //设置侧滑菜单所有linearLayout背景透明
    public void clearLayoutColor() {
        layout1.setBackgroundColor(Color.parseColor("#00000000"));
        layout2.setBackgroundColor(Color.parseColor("#00000000"));
        layout3.setBackgroundColor(Color.parseColor("#00000000"));
        layout4.setBackgroundColor(Color.parseColor("#00000000"));
        layout5.setBackgroundColor(Color.parseColor("#00000000"));
        layout6.setBackgroundColor(Color.parseColor("#00000000"));
    }

    /**
     * 双击退出应用
     */
    /*long lastPress; //默认为0

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 2000) {
            Toast.makeText(getBaseContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show();
            lastPress = currentTime;
        } else {
            super.onBackPressed();
        }
    }*/

    /**
     * 判断网络是否可用
     *
     * @return
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            //当前无可用网络
            layout_isNetworkAvailable.setVisibility(View.VISIBLE);
            return false;
        } else {
            layout_isNetworkAvailable.setVisibility(View.GONE);
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isNetworkAvailable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareSDK.stopSDK(this);//停止ShearSDK
    }
}
