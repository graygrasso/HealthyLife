package com.graygrass.healthylife;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
        mCache = ACache.get(getApplicationContext());
        imgOption();
        }

    /**
     * 配置ImageLoader
     */
    public void imgOption() {
        //创建默认的ImageLoad配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        //创建默认的ImageLoader配置参数
        /*ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
                .writeDebugLogs()
                .build();*/

        ImageLoader.getInstance().init(configuration);

//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
    }

}
