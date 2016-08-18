package com.graygrass.healthylife.fragment;

import android.content.Intent;
import android.graphics.Color;
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

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * Created by 橘沐 on 2015/12/28.
 * 我的主页
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

        /*getView().setFocusableInTouchMode(true);
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
        });*/
    }
    //todo 分享什么！
    private void showShare() {
        ShareSDK.initSDK(view.getContext());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("健康生活");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://www.tngou.net/disease/show/25");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("这是我的分享~大家都来看看吧");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/mnt/sdcard/test.jpg");//确保SDcard下面存在此张图片
        /*Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"

                + r.getResourcePackageName(R.drawable.图片名称) + "/"

                + r.getResourceTypeName(R.drawable.图片名称) + "/"

                + r.getResourceEntryName(R.drawable.图片名称));
        */
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.tngou.net/disease/show/25");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.tngou.net/disease/show/25");
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
//                wxShare();
//                wbShare();
//                QQShare();
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

    public void wxShare() {
        //微信分享
        Wechat.ShareParams wxSp = new Wechat.ShareParams();
        wxSp.setTitle("title");
        wxSp.setText("text");
        wxSp.setImageUrl("http://d.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=eb3895228218367aaddc77db1e43a7ec/c9fcc3cec3fdfc03c92cbbb3d03f8794a4c22660.jpg");
        wxSp.setUrl("http://www.tngou.net/disease/show/25");
        wxSp.setShareType(Platform.SHARE_WEBPAGE);
        Platform wx = ShareSDK.getPlatform(Wechat.NAME);
//        Platform wxC = ShareSDK.getPlatform(WechatMoments.NAME);
        wx.share(wxSp);
    }

    public void wbShare() {
        SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
        sp.setText("文本");
        sp.setImagePath("/mnt/sdcard/test.jpg");
        Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
//        weibo.setPlatformActionListener(paListener); // 设置分享事件回调
// 执行图文分享
        weibo.share(sp);
    }

    public void QQShare() {
        QZone.ShareParams sp = new QZone.ShareParams();
        sp.setTitle("测试分享的标题");
        sp.setTitleUrl("http://sharesdk.cn"); // 标题的超链接
        sp.setText("测试分享的文本");
        sp.setImageUrl("http://www.someserver.com/测试图片网络地址.jpg");
        sp.setSite("发布分享的网站名称");
        sp.setSiteUrl("发布分享网站的地址");

        Platform qzone = ShareSDK.getPlatform (QZone.NAME);
//        qzone. setPlatformActionListener (paListener); // 设置分享事件回调
// 执行图文分享
        qzone.share(sp);
    }

}
