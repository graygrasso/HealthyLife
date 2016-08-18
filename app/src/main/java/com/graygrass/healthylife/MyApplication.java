package com.graygrass.healthylife;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import simplecache.ACache;

/**
 * Created by 橘沐 on 2015/12/16.
 */
public class MyApplication extends Application {
    private Context context;
    public static ACache mCache;

    @Override
    public void onCreate() {
        super.onCreate();

        //缓存
        mCache = ACache.get(getApplicationContext());
        //极光推送
        jPushOtion();
        //ImageLoade网络图片请求设置
        imgOption();
    }

    /**
     * 配置ImageLoader
     */
    public void imgOption() {
        //创建默认的ImageLoad配置参数
//        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);

//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
//                .writeDebugLogs() //打印log信息
                .build();
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);
    }

    /**
     * 极光推送相关配置
     */
    public void jPushOtion() {
        JPushInterface.setDebugMode(true);//TODO 测试时设置为true，发布时改为false
        JPushInterface.init(this);

        //设置别名
        String userId=mCache.getAsString("userId");
        if(!TextUtils.isEmpty(userId)) {
            JPushInterface.setAlias(MyApplication.this, userId, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    Log.d("myjpush", "the alias result is: " + i +" and the userId is: "+s );
                }
            });

        }
    }
}
