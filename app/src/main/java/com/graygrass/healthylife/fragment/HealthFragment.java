package com.graygrass.healthylife.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.graygrass.healthylife.R;
import com.graygrass.healthylife.adapter.ViewPagerAdapter;
import com.graygrass.healthylife.layout.SlidingTabLayout;

import java.util.ArrayList;

/**
 * Created by 橘沐 on 2015/12/27.
 */
public class HealthFragment extends Fragment {
    private View view;
    private ViewPager viewPager;
    private ArrayList<Fragment> list;
    private SlidingTabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_health, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        tabLayout = (SlidingTabLayout) view.findViewById(R.id.tabLayout);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = new ArrayList<>();
        list.add(new KnowledgeFragment());
        list.add(new DrugFragment());
        list.add(new DrugStoreFragment());
        list.add(new BookFragment());
        list.add(new IllnessFragment());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), view.getContext(), list);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);

        //定义SlidingTabLayout
        tabLayout.setCustomTabView(R.layout.tab_text, 0);//注意这句一定要放在setViewPager前面
        tabLayout.setViewPager(viewPager);
        tabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
//        tabLayout.setSelectedIndicatorColors(Color.WHITE);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.title_green));
    }

    /**
     * 当显示的是首页的时候双击返回键退出程序
     */
    long lastPress;
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastPress > 2000) {
                        Toast.makeText(view.getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                        lastPress = currentTime;
                    } else
                        System.exit(0);//退出应用
                    return true;
                }
                return false;
            }
        });
    }
}
