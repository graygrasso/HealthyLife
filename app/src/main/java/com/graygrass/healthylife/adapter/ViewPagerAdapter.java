package com.graygrass.healthylife.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by 橘沐 on 2015/12/16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private ArrayList<Fragment> list;
    private String[] tabTitles={"健康知识","药品大全","药店信息","健康图书","疾病信息"};
    public ViewPagerAdapter(FragmentManager fm, Context context, ArrayList<Fragment> list) {
        super(fm);
        this.context = context;
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
