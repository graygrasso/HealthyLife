package com.graygrass.healthylife.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.graygrass.healthylife.activity.ItemActivity;
import com.graygrass.healthylife.MyApplication;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.adapter.ModelAdapter;
import com.graygrass.healthylife.model.DrugClassifyModel;
import com.graygrass.healthylife.model.DrugListModel;
import com.graygrass.healthylife.util.Common;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 橘沐 on 2015/12/16.
 * 药物大全页面显示
 * 包括药物种类、药物列表、还有点击列表项后出现的药物详情
 */
public class DrugFragment extends Fragment {
    private View parentView;
    private PullToRefreshListView lv_drug;
    private Spinner sp_drug_classify;

    //分页
    private int page;
    private int drugListId;
    private List<DrugListModel.Tngou> druglist = null;
    private ModelAdapter modelAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_drug, null, false);
        lv_drug = (PullToRefreshListView) parentView.findViewById(R.id.lv_drug);
        lv_drug.setMode(PullToRefreshBase.Mode.BOTH);
        sp_drug_classify = (Spinner) parentView.findViewById(R.id.sp_drug_classify);
        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();

        //读取药品类型缓存
        String drugClassifyString = MyApplication.mCache.getAsString("drugClassify");
        if (TextUtils.isEmpty(drugClassifyString)) {
            doDrugClassify();
        } else {
            drugClassify(drugClassifyString);
        }
        //读取“家庭常备”药品列表缓存
        String drugListString = MyApplication.mCache.getAsString("drugList");
        if(!TextUtils.isEmpty(drugListString))
            drugList(drugListString);

    }

    public void initListener() {
        lv_drug.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doDrugList(drugListId, 1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doDrugList(drugListId, ++page);
            }
        });

    }

    /**
     * 查询药品分类
     */
    public void doDrugClassify() {
        RequestQueue queue = Volley.newRequestQueue(parentView.getContext());
        StringRequest request = new StringRequest(Request.Method.GET, Common.drugClassifyUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("doDrugClassify()返回：》》》》》》" + s);
                MyApplication.mCache.put("drugClassify", s);//将网络请求回的数据放入缓存
                drugClassify(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("drugClassify错误：" + volleyError.getMessage());
            }
        });
        queue.add(request);
    }

    /**
     * 查询药品列表
     *
     * @param id 药物种类的id，作为请求参数
     */
    public void doDrugList(final int id, final int page) {
        RequestQueue queue = Volley.newRequestQueue(parentView.getContext());
        StringRequest request = new StringRequest(Request.Method.POST, Common.drugListUrl + "?id=" + id + "&page=" + page + "&rows=20", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("doDrugList()返回：》》》》" + s);
                if (id == 17 && page == 1)// 因为默认显示id为17的列表，故缓存
                    MyApplication.mCache.put("drugList", s);
                drugList(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("drugList错误：" + volleyError.getMessage());
                lv_drug.onRefreshComplete();//刷新完成
            }
        });
        queue.add(request);
    }

    /**
     * 转到ItemActivity展示药物的具体信息
     *
     * @param id 药物的id
     */
    public void drugShow(long id) {
        Intent intent = new Intent(getActivity(), ItemActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("kind", "drugShow");//表明要显示的是药品详情
        startActivity(intent);
    }

    public void drugClassify(String s) {
        Gson gson = new Gson();
        final DrugClassifyModel d = gson.fromJson(s, DrugClassifyModel.class);
        List<DrugClassifyModel.Tngou> list = d.getTngou();
        List<String> listTv = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String title = list.get(i).getTitle();
            listTv.add(title);
        }

        // ArrayAdapter 应用 自定义spinner内部布局（style_spinner.xml），只能存在一个TextView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (parentView.getContext(), R.layout.style_spinner, listTv);
        sp_drug_classify.setAdapter(adapter);
        sp_drug_classify.setSelection(16);//设置默认为家庭常用(其实id是17，估计这里是从0开始)
        sp_drug_classify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                drugListId = d.getTngou().get(position).getId();
                druglist = null;
                page = 1;
                doDrugList(drugListId, page);//得到用于查询药品列表的id传入doDrugList()
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void drugList(String s) {
        Gson gson = new Gson();
        final DrugListModel dl = gson.fromJson(s, DrugListModel.class);
        if (druglist == null || page == 1) {
            druglist = dl.getTngou();
            modelAdapter = new ModelAdapter(druglist, parentView.getContext(), "druglist");
            lv_drug.setAdapter(modelAdapter);
        } else {
            druglist.addAll(dl.getTngou());
            modelAdapter.notifyDataSetChanged();
        }
        lv_drug.onRefreshComplete();//刷新完成
        lv_drug.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drugShow(druglist.get(position - 1).getId());//得到用于查询药品详情的id传入drugShow()
            }
        });
    }
}
