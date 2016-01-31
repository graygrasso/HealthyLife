package com.graygrass.healthylife.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.graygrass.healthylife.activity.ItemActivity;
import com.graygrass.healthylife.activity.MainActivity;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.model.SearchAllModel;
import com.graygrass.healthylife.util.Common;
import com.graygrass.healthylife.util.DoRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 橘沐 on 2015/12/24.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {
    private View view;
    private ImageButton imgbtn_search;
    private ImageView img_health;
    private EditText txt_search;
    private PullToRefreshListView lv_search;
    private TextView tv_classify_title;
    private Response.Listener<String> listener;
    private Response.ErrorListener errListener;
    private static ProgressDialog progressDialog;

    //分页
    private List<SearchAllModel.Tngou> searchList;
    private int page;
    private String keyword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        imgbtn_search = (ImageButton) view.findViewById(R.id.imgbtn_search);
        txt_search = (EditText) view.findViewById(R.id.txt_search);
        lv_search = (PullToRefreshListView) view.findViewById(R.id.lv_search);
        lv_search.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        img_health = (ImageView) view.findViewById(R.id.img_health);
        tv_classify_title = (TextView) view.findViewById(R.id.tv_classify_title);
        tv_classify_title.setText("查询");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
    }

    private void initListener() {
        imgbtn_search.setOnClickListener(this);

        //将软键盘回车改为发送
        txt_search.setImeOptions(EditorInfo.IME_ACTION_SEND);
        //EditText的键盘事件监听
        txt_search.setOnEditorActionListener(this);

        //网络请求失败
        errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("search出错了：" + volleyError.getMessage());
            }
        };
        //请求成功
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                searchListener(s);
            }
        };

        lv_search.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                page=++page;
                DoRequest.doRequest(view.getContext(), true, Common.searchUrl + "?keyword=" + URLEncoder.encode(keyword) + "&page="+page+"&rows=40", listener, errListener);
            }
        });
    }

    private void searchListener(String s) {
        Gson gson = new Gson();
        final SearchAllModel searchAll = gson.fromJson(s, SearchAllModel.class);
        if (searchAll.getTotal() == 0) {
            progressDialog.dismiss();
            Toast.makeText(view.getContext(), "没有找到相关信息，再试试其他的吧(｡◕‿‿◕｡)", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.dismiss();
            if (searchList == null || page == 1)
                searchList = searchAll.getTngou();
            else
                searchList.addAll(searchAll.getTngou());
            ArrayList<HashMap<String, String>> titles = new ArrayList<>();
            for (int i = 0; i < searchList.size(); i++) {
                HashMap<String, String> map = new HashMap<>();
                String title = searchList.get(i).getTitle();
                if (title.length() > 14)
                    title = title.substring(0, 14) + "...";
                map.put("title_main", title);
//                        s=replaceType(s);
                map.put("type", "分类：" + searchList.get(i).getType());
                titles.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(view.getContext(), titles, R.layout.list_searchitem,
                    new String[]{"title_main", "type"}, new int[]{R.id.tv_search_title, R.id.tv_search_type});
            lv_search.setAdapter(adapter);
            lv_search.onRefreshComplete();
            lv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(view.getContext(), ItemActivity.class);
                    Bundle bundle = new Bundle();
                    SearchAllModel.Tngou sa = searchAll.getTngou().get(position-1);
                    bundle.putSerializable("searchAll", sa);
                    intent.putExtras(bundle);
                    intent.putExtra("kind", "searchAll");
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        search();
    }

    /**
     * 将返回的英文类型替换为中文
     */
    private String replaceType(String s) {
        s = s.replace("info", "健康咨询").replace("lore", "健康知识").replace("ask", "健康问答").replace("book", "健康图书")
                .replace("cook", "健康菜谱").replace("food", "健康食物").replace("drug", "药品信息")
                .replace("disease", "疾病信息").replace("symptom", "病状信息").replace("check", "检查项目")
                .replace("operation", "手术项目").replace("factory", "药企信息").replace("hospital", "医院信息")
                .replace("store", "药店信息").replace("news", "农业新闻").replace("tech", "农业技术")
                .replace("top", "热点热词");
        return s;
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
                    //按返回键时退回到首页（即healthFragment）
                    getActivity().getSupportFragmentManager().popBackStack("healthFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    //左边侧滑颜色变化
                    MainActivity.layout2.setBackgroundColor(Color.parseColor("#00000000"));
                    MainActivity.layout1.setBackgroundColor(Color.parseColor("#5086C340"));
                    return true;
                }
                return false;
            }
        });
    }

    private void search() {
        keyword = txt_search.getText().toString().trim();
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.show();
        page=1;
        DoRequest.doRequest(view.getContext(), true, Common.searchUrl + "?keyword=" + URLEncoder.encode(keyword) + "&page="+page+"&rows=40", listener, errListener);
        //设置图片透明度
        img_health.getBackground().setAlpha(100);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            //处理事件
            search();
        }
        return false;
    }
}
