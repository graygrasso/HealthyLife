package com.graygrass.healthylife.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.graygrass.healthylife.ItemActivity;
import com.graygrass.healthylife.MainActivity;
import com.graygrass.healthylife.MyApplication;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.adapter.ModelAdapter;
import com.graygrass.healthylife.layout.RefreshableView;
import com.graygrass.healthylife.model.KnowledgeModel;
import com.graygrass.healthylife.util.Common;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import simplecache.ACache;

/**
 * Created by 橘沐 on 2015/12/16.
 * 健康知识
 */
public class KnowledgeFragment extends Fragment {
    private View parentView;
    private PullToRefreshListView lv_knowledge;
    private ProgressDialog progress;
//    private RefreshableView refreshableView;

    //分页
    private long rows;
    private List<KnowledgeModel.Tngou> knowledgeList = null;

    MainActivity main = new MainActivity();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_knowledge, null, false);
        lv_knowledge = (PullToRefreshListView) parentView.findViewById(R.id.lv_knowledge);
        lv_knowledge.setMode(PullToRefreshBase.Mode.BOTH);
//        refreshableView = (RefreshableView) parentView.findViewById(R.id.refreshable_view);
        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initListener();

        //判断是否有缓存内容，如果有则默认显示缓存内容
        progress = new ProgressDialog(parentView.getContext());
        rows = 20;
        String knowledgeListString = MyApplication.mCache.getAsString("knowledgeListString");
        if (TextUtils.isEmpty(knowledgeListString)) {
            progress.setMessage("正在加载...\n" + "请稍候");
            progress.show();
            doRequest(rows);
        } else {
            requestOperation(knowledgeListString);
        }

        /*//注册下拉刷新的监听
        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                doRequest();
                refreshableView.finishRefreshing();
            }
        }, 0);*/
    }

    private void initListener() {
        lv_knowledge.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                knowledgeList = null;
                doRequest(20);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                rows = rows + 20;
                doRequest(rows);
            }
        });
    }

    public void doRequest(final long rows) {
        RequestQueue queue = Volley.newRequestQueue(parentView.getContext());
        StringRequest request = new StringRequest(Request.Method.GET, Common.knowledgeUrl + "?classify=0&id=0&rows=" + rows, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (progress != null)
                    progress.dismiss();
                System.out.println("KnowledgeFragment->doRequest()返回结果》" + s);
                if (rows == 20) {
                    MyApplication.mCache.put("knowledgeListString", s);//将网络请求回来的数据放入缓存中(只将最新数据放入缓存)
                }
                requestOperation(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("knowledge错误：" + volleyError.getMessage());
                lv_knowledge.onRefreshComplete();
                if (progress != null)
                    progress.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("apikey", Common.knowledgeKey);
                return map;
            }
        };
        queue.add(request);
    }

    /**
     * 请求回来解析的操作
     *
     * @param s
     */
    private void requestOperation(String s) {
        Gson gson = new Gson();
        final KnowledgeModel k = gson.fromJson(s, KnowledgeModel.class);
        knowledgeList = k.getList();
        ModelAdapter adapter = new ModelAdapter(knowledgeList, parentView.getContext(), "knowledge");
        lv_knowledge.setAdapter(adapter);
        lv_knowledge.onRefreshComplete();
        lv_knowledge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position-1 是因为前面加了一个header_layout.xml,将position用了一个（具体原因：就是head占位）
                KnowledgeModel.Tngou tngou = k.getList().get(position - 1);
                //将详细信息传到ItemActivity,activity_itemdetail.xml显示
                Intent intent = new Intent(parentView.getContext(), ItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("knowledgeList", tngou);//Bundle传递对象，注意要使KnowledgeModel.Tngou实现Serializable
                intent.putExtras(bundle);
                intent.putExtra("kind", "knowledge");//提示ItemActivity解析knowledge的内容
                // activity not found：注意在manifest中注册此activity
                startActivity(intent);
            }
        });
    }
}
