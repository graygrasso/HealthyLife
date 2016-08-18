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
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.graygrass.healthylife.MyApplication;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.activity.ItemActivity;
import com.graygrass.healthylife.activity.MainActivity;
import com.graygrass.healthylife.adapter.ImageWallAdapter;
import com.graygrass.healthylife.model.FoodListModel;
import com.graygrass.healthylife.model.FoodShowModel;
import com.graygrass.healthylife.util.Common;
import com.graygrass.healthylife.util.DoRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/28.
 */
public class FoodFragment extends Fragment {
    private View view;
    private PullToRefreshGridView gridView_food;
    private Response.Listener<String> listener;
    private Response.ErrorListener errListener;
    private TextView tv_classify_title;
    private String type;
    private ProgressBar progressBar;

    //用于分页
    private static int page = 1;
    private static List<FoodListModel.Tngou> foodList;
    private Gson gson;
    private ImageWallAdapter imageWallAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_food, container, false);
        gridView_food = (PullToRefreshGridView) view.findViewById(R.id.gridView_food);
        gridView_food.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_food);
        tv_classify_title = (TextView) view.findViewById(R.id.tv_classify_title);
        tv_classify_title.setText("健康食物");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gson = new Gson();
        initListener();
        type = "foodList";
        //如果有缓存，则从缓存中读取数据
        String foodList = MyApplication.mCache.getAsString("foodList");
        if (TextUtils.isEmpty(foodList))
            DoRequest.doRequest(view.getContext(), true, Common.foodListUrl + "?page=1&rows=36", listener, errListener);
        else
            foodList(gson, foodList);
    }

    private void initListener() {
        errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("food错误：" + volleyError.getMessage());
            }
        };
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (type.equals("foodList"))
                    foodList(gson, s);
                else if (type.equals("foodShow"))
                    foodShow(gson, s);
            }
        };
        gridView_food.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                type = "foodList";
                page++;
                DoRequest.doRequest(view.getContext(), true, Common.foodListUrl + "?page=" + page + "&rows=36", listener, errListener);
            }
        });
    }

    /**
     * 请求回来将其显示到GridView 的监听
     *
     * @param gson
     * @param s    json数据
     */
    private void foodList(Gson gson, String s) {
        final FoodListModel food = gson.fromJson(s, FoodListModel.class);
        if (page == 1 || foodList == null) {
            foodList = food.getTngou();
            MyApplication.mCache.put("foodList", s);//将获得的Json字符串写进缓存
            imageWallAdapter = new ImageWallAdapter(foodList, view.getContext(), "food");
            gridView_food.setAdapter(imageWallAdapter);
        } else {
            foodList.addAll(food.getTngou());
            imageWallAdapter.notifyDataSetChanged();
        }
        gridView_food.onRefreshComplete();
        progressBar.setVisibility(View.GONE);//加载出来时进度条消失
        gridView_food.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long foodId = foodList.get(position).getId();
                type = "foodShow";
                DoRequest.doRequest(view.getContext(), true, Common.foodShowUrl + "?id=" + foodId, listener, errListener);
            }
        });
    }

    private void foodShow(Gson gson, String s) {
        FoodShowModel f = gson.fromJson(s, FoodShowModel.class);
        Intent intent = new Intent(view.getContext(), ItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("foodShow", f);
        intent.putExtras(bundle);
        intent.putExtra("kind", "foodShow");
        startActivity(intent);
    }

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
//                    MainActivity.layout3.setBackgroundColor(Color.parseColor("#00000000"));
//                    MainActivity.layout1.setBackgroundColor(Color.parseColor("#5086C340"));
//                    return true;
//                }
//                return false;
//            }
//        });
//    }
}
