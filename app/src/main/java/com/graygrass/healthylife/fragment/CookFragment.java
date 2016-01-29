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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.graygrass.healthylife.CookListActivity;
import com.graygrass.healthylife.MainActivity;
import com.graygrass.healthylife.MyApplication;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.model.CookClassifyModel;
import com.graygrass.healthylife.util.Common;
import com.graygrass.healthylife.util.DoRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 橘沐 on 2015/12/28.
 */
public class CookFragment extends Fragment {
    private Response.Listener<String> listener;
    private Response.ErrorListener errListener;
    private TextView tv_classify_title;
    private ListView lv_cook;
    private View view;
    private Gson gson;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cook, container, false);
        lv_cook = (ListView) view.findViewById(R.id.lv_cook);
        tv_classify_title = (TextView) view.findViewById(R.id.tv_classify_title);
        tv_classify_title.setText("健康菜谱");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gson = new Gson();
        initListener();

        //获取菜谱分类缓存
        String foodClassifyString = MyApplication.mCache.getAsString("cookClassify");
        if (TextUtils.isEmpty(foodClassifyString))
            DoRequest.doRequest(view.getContext(), true, Common.cookClassifyUrl, listener, errListener);
        else
            cookClassify(foodClassifyString);
    }

    /**
     * 当在其他的fragment时点击返回键退回HealthFragment(标记为“healthFragment”)
     */
    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.e("gif--", "fragment back key is clicked");
                    getActivity().getSupportFragmentManager().popBackStack("healthFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    MainActivity.layout4.setBackgroundColor(Color.parseColor("#00000000"));
                    MainActivity.layout1.setBackgroundColor(Color.parseColor("#5086C340"));
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 初始化请求监听器
     */
    private void initListener() {
        //请求成功
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                MyApplication.mCache.put("cookClassify", s);//将菜谱分类放入缓存
                MyApplication.mCache.remove("foodList");
                cookClassify(s);
            }
        };
        //请求失败
        errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("cook错误：" + volleyError.getMessage());
            }
        };
    }

    private void cookClassify(String s) {
        CookClassifyModel mmodel = gson.fromJson(s, CookClassifyModel.class);
        //model里得到的list_body
        final List<CookClassifyModel.Tngou> list_cook = mmodel.getTngou();
        List<String> list1 = new ArrayList<>();
        for (int i = 0; i < list_cook.size(); i++) {
            String title = list_cook.get(i).getName();
            list1.add(title);
        }
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (view.getContext(), R.layout.lv_cook_item, list1);
        lv_cook.setAdapter(adapter1);
        lv_cook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int cookClassifyId = list_cook.get(position).getId();
                String cookClassifyName=list_cook.get(position).getName();
                Intent intent = new Intent(view.getContext(), CookListActivity.class);
                intent.putExtra("id", cookClassifyId);
                intent.putExtra("name", cookClassifyName);
                startActivity(intent);
            }
        });
    }
}
