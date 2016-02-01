package com.graygrass.healthylife.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.graygrass.healthylife.MyApplication;
import com.graygrass.healthylife.R;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;

/**
 * Created by 橘沐 on 2016/1/30.
 */
public class SettingActivity extends Activity implements View.OnClickListener {
    private TextView tv_title;
    private ImageButton img_back;
    private RelativeLayout layout_clear, layout_exitLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initView();
        initListener();
    }

    private void initView() {
        layout_clear = (RelativeLayout) findViewById(R.id.layout_clear);
        layout_exitLogin = (RelativeLayout) findViewById(R.id.layout_exitLogin);
        img_back = (ImageButton) findViewById(R.id.img_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("设置");
    }

    private void initListener() {
        img_back.setOnClickListener(this);
        layout_clear.setOnClickListener(this);
        layout_exitLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.layout_clear:
                //清除缓存
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingActivity.this);
                dialog.setMessage("您确定要清除缓存吗？");
                setPositiveButton(dialog,"clearCache");
                setNegeativeButton(dialog).create().show();
                break;
            case R.id.layout_exitLogin:
                //退出登录（取消授权）
                AlertDialog.Builder dialog2 = new AlertDialog.Builder(SettingActivity.this);
                dialog2.setMessage("您确定要退出登录吗？");
                setPositiveButton(dialog2,"exitLogin");
                setNegeativeButton(dialog2).create().show();
                break;
        }
    }

    //清除缓存的弹框
    private AlertDialog.Builder setPositiveButton(AlertDialog.Builder builder, final String tag) {
        return builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(tag.equals("clearCache")) {
                    MyApplication.mCache.clear();
                    Toast.makeText(SettingActivity.this, "缓存已清除", Toast.LENGTH_SHORT).show();
                }else if(tag.equals("exitLogin")) {
                    exitLogin();
                    Toast.makeText(SettingActivity.this, "您已成功退出登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private AlertDialog.Builder setNegeativeButton(AlertDialog.Builder builder) {
        return builder.setNegativeButton("取消", null);
    }

    /**
     * 退出登录（取消授权）
     */
    private void exitLogin() {
        Platform qzone = ShareSDK.getPlatform(this, QZone.NAME);
        Platform weibo = ShareSDK.getPlatform(this, SinaWeibo.NAME);
        if (qzone.isValid())
            qzone.removeAccount();
        if(weibo.isValid())
            weibo.removeAccount();
        //将缓存也删除
        MyApplication.mCache.remove("userName");
        MyApplication.mCache.remove("userImage");
        MyApplication.mCache.remove("userDescription");
    }
}
