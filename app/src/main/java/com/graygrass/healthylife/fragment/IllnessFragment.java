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
import com.graygrass.healthylife.MyApplication;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.activity.ItemActivity;
import com.graygrass.healthylife.adapter.ModelAdapter;
import com.graygrass.healthylife.layout.ProgressDialogCustom;
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
    private Spinner sp_type, sp_classify, sp_detail;
    private Response.Listener<String> listener;
    private ErrorListener errListener;
    private PullToRefreshListView lv_illness;
    /**
     * flag的类型有bodyClassify,departClassify
     * showBodyIllness,showDepartIllness（显示疾病列表）
     * （主要关乎listener中方法的调用）
     */
    private static String flag = "bodyClassify";

    //主要关乎spinner的adapter内容显示
    private static final int ADAPTER_TYPE = 1;
    private static final int ADAPTER_BODY_DETAIL = 2;
    private static final int ADAPTER_BODY_ILLNESS = 3;
    private static final int ADAPTER_DEPART_DETAIL = 4;
    private static final int ADAPTER_DEPART_ILLNESS = 5;

    private int page;
    private int illnessListId;
    private List<IllnessModel.Illness> illnessList = null;
    private ModelAdapter modelAdapter;

    private ProgressDialogCustom progressDialog;
    private Gson gson = new Gson();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_illness, container, false);
        sp_type = (Spinner) parentView.findViewById(R.id.sp_type);
        sp_classify = (Spinner) parentView.findViewById(R.id.sp_classify);
        sp_detail = (Spinner) parentView.findViewById(R.id.sp_detail);
        lv_illness = (PullToRefreshListView) parentView.findViewById(R.id.lv_illness);
        lv_illness.setMode(PullToRefreshBase.Mode.BOTH);
        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //初始化请求所用监听器（Response）
        initListener();
        //初始化查询类型（默认按身体部位查询）
        List<String> typeList = new ArrayList<>();
        typeList.add("按身体部位查询");
        typeList.add("按科室查询");
        setSpinnerAdapter(typeList, sp_type, null, ADAPTER_TYPE);
    }

    /**
     * 初始化请求监听器
     */
    private void initListener() {
        //请求成功
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                switch (flag) {
                    case "bodyClassify":
                        MyApplication.mCache.put("bodyClassify", s);//将身体部位放进缓存
                        bodyClassifySpinner(s);
                        break;
                    case "departClassify":
                        departClassifySpinner(s);
                        break;
                    case "showBodyIllness":
                    case "showDepartIllness":
                        if (page == 1 && illnessListId == 2 && flag.equals("showBodyIllness")) //当页面为第一页，且id为2（鼻）时缓存
                            MyApplication.mCache.put("illnessList", s);//将疾病列表放入缓存
                        //显示list
                        showIllnessList(s);
                        break;
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

        //列表的上下拉刷新加载
        lv_illness.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                if (flag.equals("showBodyIllness"))
                    DoRequest.doRequest(parentView.getContext(), true, Common.illnessPlaceUrl + "?id=" + illnessListId + "&page=" + page + "&rows=20", listener, errListener);
                else if (flag.equals("showDepartIllness"))
                    DoRequest.doRequest(parentView.getContext(), true, Common.illnessDepartUrl + "?id=" + illnessListId + "&page=" + page + "&rows=20", listener, errListener);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = ++page;
                if (flag.equals("showBodyIllness"))
                    DoRequest.doRequest(parentView.getContext(), true, Common.illnessPlaceUrl + "?id=" + illnessListId + "&page=" + page + "&rows=20", listener, errListener);
                else if (flag.equals("showDepartIllness"))
                    DoRequest.doRequest(parentView.getContext(), true, Common.illnessDepartUrl + "?id=" + illnessListId + "&page=" + page + "&rows=20", listener, errListener);
            }
        });
    }

    /**
     * Spinner 的适配器
     *
     * @param list        写进spinner的内容列表
     * @param sp          需写内容的spinner
     * @param list_detail 当选中某项后，该项的具体内容list
     */
    private void setSpinnerAdapter(List<String> list, Spinner sp, final List list_detail, final int type) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (parentView.getContext(), R.layout.style_spinner, list);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (type) {
                    case ADAPTER_TYPE:
                        typeSpinner(position);
                        break;
                    case ADAPTER_BODY_DETAIL:
                        bodyDetailSpinner(list_detail, position);
                        break;
                    case ADAPTER_BODY_ILLNESS:
                        startProgressDialog();
                        bodyIllness(list_detail, position);
                        break;
                    case ADAPTER_DEPART_DETAIL:
                        departDetailSpinner(list_detail, position);
                        break;
                    case ADAPTER_DEPART_ILLNESS:
                        startProgressDialog();
                        departIllness(list_detail, position);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * 第一个spinner（按科室还是按身体部位）的相应点击后果
     *
     * @param position spinner选中项
     */
    private void typeSpinner(int position) {
        if (position == 0) {
            //初始化页面时即请求身体部位
            flag = "bodyClassify";
            //读取身体部位缓存
            String bodyClassifyString = MyApplication.mCache.getAsString("bodyClassify");
            if (TextUtils.isEmpty(bodyClassifyString))
                DoRequest.doRequest(parentView.getContext(), true, Common.bodyUrl, listener, errListener);
            else {
                bodyClassifySpinner(bodyClassifyString);
            }
        } else if (position == 1) {
            //请求科室部门
            flag = "departClassify";
            DoRequest.doRequest(parentView.getContext(), true, Common.departmentUrl, listener, errListener);
        }
    }

    /**
     * 当选中为按身体部位查询时，
     *
     * @param s Json数据
     */
    private void bodyClassifySpinner(String s) {
        //获取身体大部位（如头、上、下肢）的名称并放入list1（放进spinner里的）
        List<String> list1 = new ArrayList<>();
        BodyModel body = gson.fromJson(s, BodyModel.class);
        //model里得到的list_body
        final List<BodyModel.Tngou> list_body = body.getTngou();
        if (list_body != null && list_body.size() > 0)
            for (int i = 0; i < list_body.size(); i++) {
                String title = list_body.get(i).getName();
                list1.add(title);
            }
        setSpinnerAdapter(list1, sp_classify, list_body, ADAPTER_BODY_DETAIL);
    }

    /**
     * 得到身体部位分类下的具体部位
     *
     * @param list     spinner_classify选中内容具体list
     * @param position spinner_classify选中项
     */
    private void bodyDetailSpinner(List list, int position) {
        List<BodyModel.Tngou> list_body = list;
        //获取身体具体部位（如头下的眼、耳、口）的list_detail
        final List<BodyModel.Tngou.Place> list_detail = list_body.get(position).getPlaces();
        //获取某一大类下的具体部位名称list2
        List<String> list2 = new ArrayList<>();
        if (list_detail != null && list_detail.size() > 0) {
            for (int j = 0; j < list_detail.size(); j++) {
                String detailName = list_detail.get(j).getName();
                list2.add(detailName);
            }
        }
        setSpinnerAdapter(list2, sp_detail, list_detail, ADAPTER_BODY_ILLNESS);
    }

    /**
     * 得到按身体部位疾病列表
     *
     * @param list
     * @param position
     */
    private void bodyIllness(List list, int position) {
        List<BodyModel.Tngou.Place> list_detail = list;
        illnessListId = list_detail.get(position).getId();
        flag = "showBodyIllness";
        page = 1;
        illnessList = null;
        //读取疾病列表缓存
        String illnessListString = MyApplication.mCache.getAsString("illnessList");
        //只有当显示第一页且为按身体部位查询和illnessListID=2（鼻）时且不为空时才显示缓存
        if (page == 1 && illnessListId == 2 && (!TextUtils.isEmpty(illnessListString)) && flag.equals("showBodyIllness"))
            showIllnessList(illnessListString);
        else
            DoRequest.doRequest(parentView.getContext(), true, Common.illnessPlaceUrl + "?id=" + illnessListId + "&page=" + page + "&rows=20", listener, errListener);
    }

    /**
     * 具体疾病列表数据的显示和列表项点击传值
     *
     * @param s
     */
    private void showIllnessList(String s) {
        final IllnessModel illness = gson.fromJson(s, IllnessModel.class);
        if (illnessList == null || page == 1) {
            illnessList = illness.getList();
            modelAdapter = new ModelAdapter(illnessList, parentView.getContext(), "illness");
            lv_illness.setAdapter(modelAdapter);
        } else {
            illnessList.addAll(illness.getList());
            modelAdapter.notifyDataSetChanged();
        }
        lv_illness.onRefreshComplete();
       stopProgerssDialog();
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
     * 当选中为按科室部门查询时，得到科室分类
     *
     * @param s
     */
    private void departClassifySpinner(String s) {
        DepartmentModel depart = gson.fromJson(s, DepartmentModel.class);
        //model里得到的list_depart
        final List<DepartmentModel.Tngou> list_depart = depart.getTngou();
        //获取大部门（如内科、外科）的名称并放入list1（放进spinner里的）
        List<String> list1 = new ArrayList<>();
        for (int i = 0; i < list_depart.size(); i++) {
            String title = list_depart.get(i).getName();
            list1.add(title);
        }
        setSpinnerAdapter(list1, sp_classify, list_depart, ADAPTER_DEPART_DETAIL);
    }

    /**
     * 得到科室分类下的具体分类
     *
     * @param list
     * @param position
     */
    private void departDetailSpinner(List list, int position) {
        List<DepartmentModel.Tngou> list_depart = list;
        //获取具体部门（如内科下的呼吸内科、消化内科）的list_detail
        final List<DepartmentModel.Tngou.Departments> list_detail = list_depart.get(position).getDepartments();
        //获取某一大类下的具体部门名称list2
        List<String> list2 = new ArrayList<>();
        for (int j = 0; j < list_detail.size(); j++) {
            String detailName = list_detail.get(j).getName();
            list2.add(detailName);
        }
        setSpinnerAdapter(list2, sp_detail, list_detail, ADAPTER_DEPART_ILLNESS);
    }


    /**
     * 得到按科室疾病列表
     *
     * @param list
     * @param position
     */
    private void departIllness(List list, int position) {
        List<DepartmentModel.Tngou.Departments> list_detail = list;
        illnessListId = list_detail.get(position).getId();
        flag = "showDepartIllness";
        page = 1;
        illnessList = null;
        DoRequest.doRequest(parentView.getContext(), true, Common.illnessDepartUrl + "?id=" + illnessListId + "&page=1&rows=20", listener, errListener);
    }

    private void startProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialogCustom.createDialog(parentView.getContext());
            progressDialog.setMessage("正在加载...");
        }
        progressDialog.show();
    }

    private void stopProgerssDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
