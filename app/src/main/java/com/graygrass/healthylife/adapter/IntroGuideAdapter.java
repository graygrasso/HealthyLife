package com.graygrass.healthylife.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/21.
 * 引导的adapter viewPager
 */
public class IntroGuideAdapter extends PagerAdapter{

    //界面列表
    private List<View> viewList;

    public IntroGuideAdapter(List<View> viewList) {
        this.viewList = viewList;
    }

    /**
     *销毁position位置的界面
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    /**
     * 初始化position位置的界面
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public int getCount() {
        if(viewList!=null)
            return viewList.size();
        return 0;
    }

    /**
     * 判断是否由对象生成界面
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==object);
    }
}
