package com.graygrass.healthylife.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.graygrass.healthylife.MyApplication;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.activity.DialogLoginActivity;
import com.graygrass.healthylife.activity.MainActivity;
import com.graygrass.healthylife.activity.SettingActivity;
import com.graygrass.healthylife.util.DoRequest;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by 橘沐 on 2015/12/28.
 */
public class MineFragment extends Fragment implements View.OnClickListener {
    private View view;
    private Button btn_login;
    private RelativeLayout layout_share, layout_settings;
    private TextView tv_sign;//个性签名
    private ImageView img_user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //获取用户登录信息缓存
        isUserLogin();

        initListener();
    }

    private void initView() {
        btn_login = (Button) view.findViewById(R.id.btn_login);
        layout_share = (RelativeLayout) view.findViewById(R.id.layout_share);
        layout_settings = (RelativeLayout) view.findViewById(R.id.layout_settings);
        tv_sign = (TextView) view.findViewById(R.id.tv_sign);
        img_user = (ImageView) view.findViewById(R.id.img_user);
    }

    private void initListener() {
        btn_login.setOnClickListener(this);
        layout_share.setOnClickListener(this);
        layout_settings.setOnClickListener(this);
    }

    /**
     * 当在其他的fragment时点击返回键退回HealthFragment(标记为“healthFragment”)
     */
    @Override
    public void onResume() {
        super.onResume();

        //判断用户是否登录 如果已经退出登录，则恢复初始状态
        isUserLogin();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e("gif--", "fragment back key is clicked");
                    getActivity().getSupportFragmentManager().popBackStack("healthFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    MainActivity.layout6.setBackgroundColor(Color.parseColor("#00000000"));
                    MainActivity.layout1.setBackgroundColor(Color.parseColor("#5086C340"));
                    return true;
                }
                return false;
            }
        });
    }

    private void showShare() {
        ShareSDK.initSDK(view.getContext());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("测试");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("分享测试");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.baidu.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.baidu.com");
        // 启动分享GUI
        oks.show(view.getContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                startActivity(new Intent(view.getContext(), DialogLoginActivity.class));
                break;
            case R.id.layout_share:
                showShare();
                break;
            case R.id.layout_settings:
                Intent intent = new Intent(view.getContext(), SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 判断用户是否登录并显示与否用户登录信息
     */
    public void isUserLogin() {
        //获取缓存的用户信息
        String uName = MyApplication.mCache.getAsString("userName");
        String uImage = MyApplication.mCache.getAsString("userImage");
        String uDes = MyApplication.mCache.getAsString("userDescription");
        if (TextUtils.isEmpty(uName)) {
            btn_login.setText("点击登录");
            tv_sign.setText("个性签名");
            img_user.setImageResource(R.drawable.head);
        } else {
            btn_login.setText(uName);
            tv_sign.setText(uDes);
            DoRequest.doImageRequest(uImage, img_user);
        }
    }

}
