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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.graygrass.healthylife.MyApplication;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.activity.CookListActivity;
import com.graygrass.healthylife.activity.MainActivity;
import com.graygrass.healthylife.layout.ImageCarousel;
import com.graygrass.healthylife.layout.KeywordsFlow;
import com.graygrass.healthylife.model.CookClassifyModel;
import com.graygrass.healthylife.util.Common;
import com.graygrass.healthylife.util.DoRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by 橘沐 on 2015/12/28.
 */
public class CookFragment extends Fragment implements View.OnClickListener {
    private Response.Listener<String> listener;
    private Response.ErrorListener errListener;
    private TextView tv_classify_title;
    private List<CookClassifyModel.Tngou> cookList;
    private View view;
    private Gson gson;
//    private ImageCarousel imageCarousel;

    private KeywordsFlow keywordsFlow;
    private ArrayList<String> keywords; //用于飞入飞出的文字
    Map<String,Integer> classifyMap;
    private Button btn_other;//换一批

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cook, container, false);
//        imageCarousel = (ImageCarousel) view.findViewById(R.id.imageCarousel);
        keywordsFlow = (KeywordsFlow) view.findViewById(R.id.keywordsFlow);
        btn_other = (Button) view.findViewById(R.id.btn_other);
        tv_classify_title = (TextView) view.findViewById(R.id.tv_classify_title);
        tv_classify_title.setText("健康菜谱");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        keywordsFlow.setDuration(800l);
        gson = new Gson();
        initListener();

        //获取菜谱分类缓存
        String foodClassifyString = MyApplication.mCache.getAsString("cookClassify");
        if (TextUtils.isEmpty(foodClassifyString))
            DoRequest.doRequest(view.getContext(), true, Common.cookClassifyUrl, listener, errListener);
        else
            cookClassify(foodClassifyString);
    }

    private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, ArrayList list) {
        Random random = new Random();
        for (int i = 0; i < KeywordsFlow.MAX; i++) {
            int ran = random.nextInt(list.size());
            String tmp = (String) list.get(ran);
            keywordsFlow.feedKeyword(tmp);
        }
    }


    /**
     * 当在其他的fragment时点击返回键退回HealthFragment(标记为“healthFragment”)
     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    Log.e("gif--", "fragment back key is clicked");
//                    getActivity().getSupportFragmentManager().popBackStack("healthFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                    MainActivity.layout4.setBackgroundColor(Color.parseColor("#00000000"));
//                    MainActivity.layout1.setBackgroundColor(Color.parseColor("#5086C340"));
//                    return true;
//                }
//                return false;
//            }
//        });
//    }

    /**
     * 初始化请求监听器
     */
    private void initListener() {
        btn_other.setOnClickListener(this);
        keywordsFlow.setOnItemClickListener(this);
        //请求成功
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                MyApplication.mCache.put("cookClassify", s);//将菜谱分类放入缓存
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
        cookList = mmodel.getTngou();
        classifyMap = new HashMap<>();
        for (int i = 0; i < cookList.size(); i++) {
            String title = cookList.get(i).getName();
            int id = cookList.get(i).getId();
            classifyMap.put(title, id);
        }
        keywords = new ArrayList<>(classifyMap.size());
        //遍历map得到key(title)并将其加入到keywords
        Iterator<String> it = classifyMap.keySet().iterator();
        while(it.hasNext()) {
            keywords.add(it.next());
        }
        // 添加
        feedKeywordsFlow(keywordsFlow, keywords);
        keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_other) {
            keywordsFlow.rubKeywords();
            // keywordsFlow.rubAllViews();
            feedKeywordsFlow(keywordsFlow, keywords);
            //随机飞入飞出动画
            Random random = new Random();
            int n = random.nextInt(2);
            if (n == 0)
                keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
            else
                keywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);
        }else if(v instanceof TextView) {
            String cookClassifyName=((TextView) v).getText().toString();
            int cookClassifyId = classifyMap.get(cookClassifyName);
            Intent intent = new Intent(view.getContext(), CookListActivity.class);
            intent.putExtra("id", cookClassifyId);
            intent.putExtra("name", cookClassifyName);
            startActivity(intent);
        }
    }
}
