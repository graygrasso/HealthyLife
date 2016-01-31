package com.graygrass.healthylife.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.graygrass.healthylife.activity.ItemActivity;
import com.graygrass.healthylife.MyApplication;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.adapter.ModelAdapter;
import com.graygrass.healthylife.model.BodyModel;
import com.graygrass.healthylife.model.DepartmentModel;
import com.graygrass.healthylife.model.IllnessModel;
import com.graygrass.healthylife.util.Common;
import com.graygrass.healthylife.util.DoRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 橘沐 on 2015/12/16.
 * 疾病信息
 */
public class IllnessFragment extends Fragment {
    private View parentView;
    private Spinner sp_type, sp1, sp2;
    private Response.Listener<String> listener;
    private ErrorListener errListener;
    private PullToRefreshListView lv_illness;
    private static String flag = "body1";//标识符:查询的是哪个（主要关乎listener中方法的调用）
    private List<String> typeList;

    private int page;
    private int illnessListId;
    private List<IllnessModel.Illness> illnessList = null;

    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_illness, null, false);
        sp_type = (Spinner) parentView.findViewById(R.id.sp_type);
        sp1 = (Spinner) parentView.findViewById(R.id.sp1);
        sp2 = (Spinner) parentView.findViewById(R.id.sp2);
        lv_illness = (PullToRefreshListView) parentView.findViewById(R.id.lv_illness);
        lv_illness.setMode(PullToRefreshBase.Mode.BOTH);
        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化请求所用监听器（Response）
        initListener();
        initType();//初始化查询类型
    }

    /**
     * 初始化请求监听器
     */
    private void initListener() {
        //请求成功
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (flag.equals("body1")) {
                    MyApplication.mCache.put("bodyClassify", s);//将身体部位放进缓存
                    //查询body的spinner
                    bodySpinner(s);
                } else if (flag.equals("body2") || flag.equals("department2")) {
                    if (page == 1 && illnessListId == 2) //当页面为第一页，且id为2（鼻）时缓存
                        MyApplication.mCache.put("illnessList", s);//将疾病列表放入缓存
                    //显示list
                    bodyList(s);
                } else if (flag.equals("department1")) {
                    //查询department的spinner
                    departmentSpinner(s);
                }
            }
        };
        //请求失败
        errListener = new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("illness错误： " + volleyError.getMessage());
                lv_illness.onRefreshComplete();
            }
        };

        lv_illness.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                DoRequest.doRequest(parentView.getContext(), true, Common.illnessPlaceUrl + "?id=" + illnessListId + "&page=1&rows=20", listener, errListener);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = ++page;
                DoRequest.doRequest(parentView.getContext(), true, Common.illnessPlaceUrl + "?id=" + illnessListId + "&page=" + page + "&rows=20", listener, errListener);
            }
        });
    }

    /**
     * 初始化第一个spinner（查询类型），默认按身体部位查询
     */
    private void initType() {
        typeList = new ArrayList<>();
        typeList.add("按身体部位查询");
        typeList.add("按科室查询");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(parentView.getContext(), R.layout.style_spinner, typeList);
        sp_type.setAdapter(adapter);
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //初始化页面时即请求身体部位
                    flag = "body1";
                    //读取身体部位缓存
                    String bodyClassifyString = MyApplication.mCache.getAsString("bodyClassify");
                    if (TextUtils.isEmpty(bodyClassifyString))
                        DoRequest.doRequest(parentView.getContext(), true, Common.bodyUrl, listener, errListener);
                    else {
                        bodySpinner(bodyClassifyString);
                    }
                } else if (position == 1) {
                    //请求科室部门
                    flag = "department1";
                    DoRequest.doRequest(parentView.getContext(), true, Common.departmentUrl, listener, errListener);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * 当选中为按身体部位查询时，后两个spinner的数据放置和列表的请求
     *
     * @param s
     */
    private void bodySpinner(String s) {
        BodyModel body = gson.fromJson(s, BodyModel.class);
        //model里得到的list_body
        final List<BodyModel.Tngou> list_body = body.getTngou();
        //获取身体大部位（如头、上、下肢）的名称并放入list1（放进spinner里的）
        List<String> list1 = new ArrayList<>();
        for (int i = 0; i < list_body.size(); i++) {
            String title = list_body.get(i).getName();
            list1.add(title);
        }
        //第二个spinner的适配器
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (parentView.getContext(), R.layout.style_spinner, list1);
        sp1.setAdapter(adapter1);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取身体具体部位（如头下的眼、耳、口）的list_detail
                final List<BodyModel.Tngou.Place> list_detail = list_body.get(position).getPlaces();
                //获取某一大类下的具体部位名称list2
                List<String> list2 = new ArrayList<>();
                for (int j = 0; j < list_detail.size(); j++) {
                    String detailName = list_detail.get(j).getName();
                    list2.add(detailName);
                }
                //第三个spinner的适配器
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                        (parentView.getContext(), R.layout.style_spinner, list2);
                sp2.setAdapter(adapter2);
                sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        illnessListId = list_detail.get(position).getId();
                        flag = "body2";
                        page = 1;
                        illnessList = null;
                        //读取疾病列表缓存
                        String illnessListString = MyApplication.mCache.getAsString("illnessList");
                        if (TextUtils.isEmpty(illnessListString))
                            DoRequest.doRequest(parentView.getContext(), true, Common.illnessPlaceUrl + "?id=" + illnessListId + "&page=" + page + "&rows=20", listener, errListener);
                        else
                            bodyList(illnessListString);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    /**
     * 列表数据的显示和列表项点击传值
     *
     * @param s
     */
    private void bodyList(String s) {
        final IllnessModel illness = gson.fromJson(s, IllnessModel.class);
        if (illnessList == null || page == 1)
            illnessList = illness.getList();
        else
            illnessList.addAll(illness.getList());
        ModelAdapter adapter = new ModelAdapter(illnessList, parentView.getContext(), "illness");
        lv_illness.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        lv_illness.onRefreshComplete();
        lv_illness.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //得到点击的条目信息
                IllnessModel.Illness ill = illnessList.get(position - 1);
                Intent intent = new Intent(parentView.getContext(), ItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("illness", ill);
                intent.putExtras(bundle);
                intent.putExtra("kind", "illness");
                startActivity(intent);
            }
        });
    }

    /**
     * 当选中为按科室部门查询时，后两个spinner的数据放置和列表的请求
     *
     * @param s
     */
    private void departmentSpinner(String s) {
        DepartmentModel depart = gson.fromJson(s, DepartmentModel.class);
        //model里得到的list_depart
        final List<DepartmentModel.Tngou> list_depart = depart.getTngou();
        //获取大部门（如内科、外科）的名称并放入list1（放进spinner里的）
        List<String> list1 = new ArrayList<>();
        for (int i = 0; i < list_depart.size(); i++) {
            String title = list_depart.get(i).getName();
            list1.add(title);
        }
        //第一个spinner的适配器
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>
                (parentView.getContext(), R.layout.style_spinner, list1);
        sp1.setAdapter(adapter1);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获取具体部门（如内科下的呼吸内科、消化内科）的list_detail
                final List<DepartmentModel.Tngou.Departments> list_detail = list_depart.get(position).getDepartments();
                //获取某一大类下的具体部门名称list2
                List<String> list2 = new ArrayList<>();
                for (int j = 0; j < list_detail.size(); j++) {
                    String detailName = list_detail.get(j).getName();
                    list2.add(detailName);
                }
                //第二个spinner的适配器
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                        (parentView.getContext(), R.layout.style_spinner, list2);
                sp2.setAdapter(adapter2);
                sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        int parts_id = list_detail.get(position).getId();
                        flag = "department2";
                        DoRequest.doRequest(parentView.getContext(), true, Common.illnessDepartUrl + "?id=" + parts_id + "&page=1&rows=20", listener, errListener);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
