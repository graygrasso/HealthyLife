package com.graygrass.healthylife.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.graygrass.healthylife.MyApplication;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.fragment.MineFragment;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

/**
 * Created by 橘沐 on 2016/1/18.
 */
public class DialogLoginActivity extends Activity implements PlatformActionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
    }

    public void qq_login(View v) {
        Platform qq = ShareSDK.getPlatform(QZone.NAME);
        qq.setPlatformActionListener(this);
        qq.showUser(null);
    }

    public void weibo_login(View v) {
        Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
        sina.setPlatformActionListener(this);
        sina.showUser(null);
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        String id = null, name = null, description = "", profile_image_url = null;
        if (platform == ShareSDK.getPlatform(SinaWeibo.NAME)) {
            //解析部分用户资料字段(weibo登录)
            id = hashMap.get("id").toString();//ID
            name = hashMap.get("name").toString();//用户名
            description = hashMap.get("description").toString();//描述
            profile_image_url = hashMap.get("profile_image_url").toString();//头像链接
        } else {//QQ空间登录
            if (i == Platform.ACTION_USER_INFOR) {
                PlatformDb platDB = platform.getDb();//获取数平台数据DB
                //通过DB获取各种数据
//                platDB.getToken();
//                platDB.getUserGender();
                profile_image_url = platDB.getUserIcon();
                id = platDB.getUserId();
                name = platDB.getUserName();
            }
        }
        MyApplication.mCache.put("userId", id); //将用户id放入缓存
        MyApplication.mCache.put("userName", name); //将用户name放入缓存
        MyApplication.mCache.put("userDescription", description); //将用户description放入缓存
        MyApplication.mCache.put("userImage", profile_image_url); //将用户image放入缓存
        finish();
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        Toast.makeText(this, "授权取消", Toast.LENGTH_SHORT).show();
        finish();
    }
}
