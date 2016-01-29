package com.graygrass.healthylife.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.adapter.ModelAdapter;
import com.graygrass.healthylife.model.CityModel;
import com.graygrass.healthylife.model.DrugStoreListModel;
import com.graygrass.healthylife.model.ProvinceModel;
import com.graygrass.healthylife.util.Common;
import com.graygrass.healthylife.util.DoRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 橘沐 on 2015/12/16.
 * 药店信息
 */
public class DrugStoreFragment extends Fragment {
    private View parentView;
    private Spinner sp1, sp2;
    private Response.Listener<String> listener;
    private Response.ErrorListener errListener;
    private PullToRefreshListView lv_drugstore;
    private static String flag = "province";//标识符:查询的是哪个（主要关乎listener中方法的调用）

    //用于分页
    private int page;
    private int proId;
    private List<DrugStoreListModel.Tngou> drugStoreList = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_drugstore, null, false);
        sp1 = (Spinner) parentView.findViewById(R.id.sp1);
//        sp2 = (Spinner) parentView.findViewById(R.id.sp2);
        lv_drugstore = (PullToRefreshListView) parentView.findViewById(R.id.lv_drugstore);
        lv_drugstore.setMode(PullToRefreshBase.Mode.BOTH);
        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化请求所用监听器（Response）
        initListener();
        //初始化查询
        DoRequest.doRequest(parentView.getContext(), true, Common.provinceUrl, listener, errListener);
    }


    /**
     * 初始化请求监听器
     */
    private void initListener() {
        //请求成功
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                if (flag.equals("province")) {
                    //查询body的spinner
                    provinceSpinner(gson, s);
                }/*else if(flag.equals("city")) {
                    //查询department的spinner
                    citySpinner(gson, s);
                }*/ else if (flag.equals("drugStoreList")) {
                    //显示list
                    drugStoreList(gson, s);
                }
            }
        };
        //请求失败
        errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("drugStore错误："+volleyError.getMessage());
                lv_drugstore.onRefreshComplete();
            }
        };

        lv_drugstore.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                DoRequest.doRequest(parentView.getContext(), true, Common.drugStoreListUrl + "?id=" + proId + "&page=1&rows=20", listener, errListener);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = ++page;
                DoRequest.doRequest(parentView.getContext(), true, Common.drugStoreListUrl + "?id=" + proId + "&page=" + page + "&rows=20", listener, errListener);
            }
        });
    }

    private void provinceSpinner(Gson gson, String s) {
        ProvinceModel province = gson.fromJson(s, ProvinceModel.class);
        //model里得到的list_province
        final List<ProvinceModel.Tngou> list_province = province.getTngou();
        //获取省份名称并放入list1（放进spinner里的）
        List<String> list1 = new ArrayList<>();
        for (int i = 0; i < list_province.size(); i++) {
            String title = list_province.get(i).getProvince();
            list1.add(title);
        }
        //第一个spinner的适配器
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (parentView.getContext(), R.layout.style_spinner, list1);
        sp1.setAdapter(adapter1);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //根据省份id查询城市
//                flag = "city";
                flag = "drugStoreList";
                //根据省份id查询药店
                page = 1;
                drugStoreList = null;
                proId = list_province.get(position).getId();
                DoRequest.doRequest(parentView.getContext(), true, Common.drugStoreListUrl + "?id=" + proId + "&page=" + page + "&rows=20", listener, errListener);
//                DoRequest.doRequest(parentView.getContext(), true, Common.cityUrl + "?id=" + proId, listener, errListener);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void citySpinner(Gson gson, String s) {
        CityModel city = gson.fromJson(s, CityModel.class);
        //model里得到的list_province
        final List<CityModel.Tngou> list_city = city.getTngou();
        //获取城市名称并放入list2（放进spinner里的）
        List<String> list2 = new ArrayList<>();
        for (int i = 0; i < list_city.size(); i++) {
            String title = list_city.get(i).getCity();
            list2.add(title);
        }
        //第二个spinner的适配器
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (parentView.getContext(), R.layout.style_spinner, list2);
        sp2.setAdapter(adapter1);
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //根据城市id查询药店信息
                flag = "drugStoreList";
                int cityId = list_city.get(position).getId();
                System.out.println("========cityId:" + cityId);
                DoRequest.doRequest(parentView.getContext(), true, Common.drugStoreListUrl + "?id=" + cityId + "&page=1&rows=20", listener, errListener);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void drugStoreList(Gson gson, String s) {
        DrugStoreListModel ds = gson.fromJson(s, DrugStoreListModel.class);
        if (drugStoreList == null || page == 1)
            drugStoreList = ds.getTngou();
        else
            drugStoreList.addAll(ds.getTngou());
        ModelAdapter adapter = new ModelAdapter(drugStoreList, parentView.getContext(), "drugStore");
        lv_drugstore.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        lv_drugstore.onRefreshComplete();
    }
}
