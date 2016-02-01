package com.graygrass.healthylife.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.graygrass.healthylife.R;
import com.graygrass.healthylife.adapter.IntroGuideAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 此项目为引导页的demo
 * 当滑动到第三张图片时，2s后进入MainActivity
 */
public class IntroGuideActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager viewPager;
    private IntroGuideAdapter adapter;
    private List<View> viewList;
    private ImageView img;
    private Button btn_enter;

    //引导的图片资源
    private static final int[] pics = {R.drawable.view0, R.drawable.view1, R.drawable.view2, R.drawable.view3};
    //底部小圆点图片
    private ImageView[] dots;
    //记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        btn_enter = (Button) findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(this);
        viewList = new ArrayList<>();
        //初始化图片列表
        initPics();

        //初始化底部小圆点
        initDots();


    }

    private void initPics() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < pics.length; i++) {
            img = new ImageView(this);
            img.setLayoutParams(layoutParams);
            img.setImageResource(pics[i]);
            viewList.add(img);

        }
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        //初始化Adapter
        adapter = new IntroGuideAdapter(viewList);
        viewPager.setAdapter(adapter);
        //绑定回调
        viewPager.addOnPageChangeListener(this);
    }

    private void initDots() {
        LinearLayout lin = (LinearLayout) findViewById(R.id.lin);
        dots = new ImageView[pics.length];
        //循环取得小圆点
        for (int i = 0; i < pics.length; i++) {
            dots[i] = (ImageView) lin.getChildAt(i);
            dots[i].setEnabled(true);
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);//设置位置tag,方便取出与当前位置对应
        }
        currentIndex = 0;
        dots[currentIndex].setEnabled(false);
    }

    /**
     * 设置当前的引导页
     *
     * @param position
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前选中小圆点
     *
     * @param position
     */
    private void setCurDot(int position) {
        if (position < 0 || position > pics.length)
            return;
        dots[position].setEnabled(false);
        dots[currentIndex].setEnabled(true);//将之前的页面设为可选状态
        currentIndex = position;//再将现在此页面设为当前页面
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * 当当前页面被选中时调用
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        //设置底部小圆点的选中状态
        setCurDot(position);
        //如果滑动到了第三张图片，则两秒后进入MainActivity
        if (position == pics.length-1) {
            /*//1.创建Timer对象
            Timer timer = new Timer();
            //3.创建TimerTask对象
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(IntroGuideActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            };
            timer.schedule(timerTask, 2000);*/
            btn_enter.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_enter) {
            Intent intent = new Intent(IntroGuideActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            int position = (int) v.getTag();
            setCurView(position);
            setCurDot(position);
        }
    }
}
