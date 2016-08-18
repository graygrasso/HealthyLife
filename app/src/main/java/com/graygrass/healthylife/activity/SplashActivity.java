package com.graygrass.healthylife.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.graygrass.healthylife.R;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by 橘沐 on 2015/12/24.
 * 欢迎页面
 */
public class SplashActivity extends InstrumentedActivity {
    SharedPreferences preferences;
    Intent intent;
    Handler mHandler;
    View rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_splash, null);
        setContentView(rootView);
        initAnim();
    }

    private void initAnim() {
        mHandler = new Handler();

        //初始化渐变动画
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        //设置动画监听器
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //当监听到动画结束时，开始跳转
                isFirstEnter();
            }
        });
        //开始播放动画
        rootView.startAnimation(animation);
    }


    /**
     * 判断是否为第一次进入应用
     * 如果是则进入引导页
     * 如果不是则进入应用
     */
    public void isFirstEnter() {
        //1.创建Timer对象
        Timer timer = new Timer();
        //3.创建TimerTask对象
        TimerTask timerTask1 = new TimerTask() {
            @Override
            public void run() {
                intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        TimerTask timerTask2 = new TimerTask() {
            @Override
            public void run() {
                intent = new Intent(SplashActivity.this, IntroGuideActivity.class);
                startActivity(intent);
                finish();
            }
        };
        //读取SharedPreferences中需要的数据
        preferences = getSharedPreferences("mySplash", MODE_PRIVATE);
        boolean flag = preferences.getBoolean("flag", true);
        System.out.println("flag:" + flag);
        //判断程序是否为第一次运行，如果是第一次运行则跳转到引导页面
        if (flag) {
            timer.schedule(timerTask2, 0);
        } else {
            timer.schedule(timerTask1, 0);
        }
        SharedPreferences.Editor editor = preferences.edit();
        //存入数据
        editor.putBoolean("flag", false);
        //提交修改
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(SplashActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(SplashActivity.this);
    }
}
