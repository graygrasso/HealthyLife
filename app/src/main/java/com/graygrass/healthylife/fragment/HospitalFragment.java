package com.graygrass.healthylife.fragment;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.activity.ItemActivity;
import com.graygrass.healthylife.activity.MainActivity;
import com.graygrass.healthylife.adapter.ModelAdapter;
import com.graygrass.healthylife.layout.ProgressDialogCustom;
import com.graygrass.healthylife.model.DrugStoreListModel;
import com.graygrass.healthylife.model.HospitalLocationModel;
import com.graygrass.healthylife.util.Common;
import com.graygrass.healthylife.util.DoRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.List;

/**
 * Created by 橘沐 on 2016/1/12.
 */
public class HospitalFragment extends Fragment implements View.OnClickListener {
    private View view;
    private TextView tv_classify_title, tv_myLocation;
    private Button btn_hospital, btn_drugStore;
    private Response.Listener<String> listener;
    private Response.ErrorListener errListener;
    private PullToRefreshListView lv_hospital;
    private ProgressDialogCustom progressDialog;

    //用于分页
    private int page = 1;
    private List<HospitalLocationModel.Tngou> hospitalList = null;
    private List<DrugStoreListModel.Tngou> drugStoreList = null;
    private static String flag = "hospital";//标识符:查询的是哪个（主要关乎listener中方法的调用）
    private ModelAdapter modelAdapter;

    private Gson gson;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = null;
    //声明mLocationOption对象(配置定位参数)
    public AMapLocationClientOption mLocationOption = null;

    public double x, y;//经纬度

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hospital, container, false);
        initView();
        tv_classify_title.setText("附近的医院、药店");
        lv_hospital.setMode(PullToRefreshBase.Mode.BOTH);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gson = new Gson();
        startProgressDialog();
        location();
        locationOption();
        initListener();
    }

    private void initView() {
        tv_classify_title = (TextView) view.findViewById(R.id.tv_classify_title);
        tv_myLocation = (TextView) view.findViewById(R.id.tv_myLocation);
        btn_drugStore = (Button) view.findViewById(R.id.btn_showDrugStore);
        btn_hospital = (Button) view.findViewById(R.id.btn_showHospital);
        lv_hospital = (PullToRefreshListView) view.findViewById(R.id.lv_hospital);
    }

    private void initListener() {
        btn_hospital.setOnClickListener(this);
        btn_drugStore.setOnClickListener(this);
        //请求成功
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (flag.equals("hospital")) {
                    hospital(s);
                } else if (flag.equals("drugStore")) {
                    drugStore(s);
                }
            }
        };
        //请求失败
        errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("错误：" + volleyError.getMessage());
                lv_hospital.onRefreshComplete();
            }
        };

        lv_hospital.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                if (flag.equals("drugStore"))
                    DoRequest.doRequest(view.getContext(), true, Common.drugStoreLocation + "?page" + page + "&rows=20&x=" + x + "&y=" + y, listener, errListener);
                else if (flag.equals("hospital"))
                    DoRequest.doRequest(view.getContext(), true, Common.hospitalLocation + "?page" + page + "&rows=20&x=" + x + "&y=" + y, listener, errListener);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = ++page;
                if (flag.equals("drugStore"))
                    DoRequest.doRequest(view.getContext(), true, Common.drugStoreLocation + "?page=" + page + "&rows=20&x=" + x + "&y=" + y, listener, errListener);
                else if (flag.equals("hospital"))
                    DoRequest.doRequest(view.getContext(), true, Common.hospitalLocation + "?page=" + page + "&rows=20&x=" + x + "&y=" + y, listener, errListener);
            }
        });
    }

    /**
     * 定位(初始化定位客户端，设置监听)
     */
    public void location() {
        mLocationClient = new AMapLocationClient(view.getContext());
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
                        System.out.println("定位到了:" + aMapLocation.toString());
                        Log.d("tag", aMapLocation.getLatitude() + "纬度：");
                        Log.d("tag", aMapLocation.getLongitude() + "经度：");
                        stopProgerssDialog();
                        tv_myLocation.setText(aMapLocation.getAddress());
//                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(aMapLocation, 20));
                        //定位成功回调信息，设置相关消息
//                        aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        y = aMapLocation.getLatitude();//获取纬度
                        x = aMapLocation.getLongitude();//获取经度
                        //定位成功后请求网络(默认显示附近医院)
                        DoRequest.doRequest(view.getContext(), true, Common.hospitalLocation + "?page=" + page + "&rows=20&x=" + x + "&y=" + y, listener, errListener);
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
            }
        };
        mLocationClient.setLocationListener(mLocationListener);
    }

    /**
     * 配置定位参数，启动定位
     */
    public void locationOption() {
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void drugStore(String s) {
        final DrugStoreListModel ds = gson.fromJson(s, DrugStoreListModel.class);
        if (drugStoreList == null || page == 1) {
            drugStoreList = ds.getTngou();
            modelAdapter = new ModelAdapter(drugStoreList, view.getContext(), "drugStore");
            lv_hospital.setAdapter(modelAdapter);
        } else {
            drugStoreList.addAll(ds.getTngou());
            modelAdapter.notifyDataSetChanged();
        }
        lv_hospital.onRefreshComplete();
        lv_hospital.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (flag.equals("drugStore")) {
                    Intent intent = new Intent(view.getContext(), ItemActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("drugStore", drugStoreList.get(position - 1));
                    intent.putExtras(bundle);
                    intent.putExtra("kind", "drugStoreShow");
                    startActivity(intent);
                }
            }
        });
    }

    private void hospital(String s) {
        final HospitalLocationModel ds = gson.fromJson(s, HospitalLocationModel.class);
        if (hospitalList == null || page == 1) {
            hospitalList = ds.getTngou();
            modelAdapter = new ModelAdapter(hospitalList, view.getContext(), "hospital");
            lv_hospital.setAdapter(modelAdapter);
        } else {
            hospitalList.addAll(ds.getTngou());
            modelAdapter.notifyDataSetChanged();
        }
        lv_hospital.onRefreshComplete();
        lv_hospital.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (flag.equals("hospital")) {
                    Intent intent = new Intent(view.getContext(), ItemActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("hospital", hospitalList.get(position - 1));
                    intent.putExtras(bundle);
                    intent.putExtra("kind", "hospitalShow");
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * 当在其他的fragment时点击返回键退回HealthFragment(标记为“healthFragment”)
     */
   /* @Override
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
                    MainActivity.layout5.setBackgroundColor(Color.parseColor("#00000000"));
                    MainActivity.layout1.setBackgroundColor(Color.parseColor("#5086C340"));
                    return true;
                }
                return false;
            }
        });
    }
*/
    @Override
    public void onClick(View v) {
        Log.d("tag", y + "y纬度：");
        Log.d("tag", x + "x经度：");
        switch (v.getId()) {
            case R.id.btn_showHospital:
                btn_hospital.setBackgroundColor(getResources().getColor(R.color.title_green));
                btn_drugStore.setBackgroundColor(getResources().getColor(R.color.white));
                flag = "hospital";
                DoRequest.doRequest(view.getContext(), true, Common.hospitalLocation + "?page=" + page + "&rows=20&x=" + x + "&y=" + y, listener, errListener);
                break;
            case R.id.btn_showDrugStore:
                btn_drugStore.setBackgroundColor(getResources().getColor(R.color.title_green));
                btn_hospital.setBackgroundColor(getResources().getColor(R.color.white));
                flag = "drugStore";
                DoRequest.doRequest(view.getContext(), true, Common.drugStoreLocation + "?page=" + page + "&rows=20&x=" + x + "&y=" + y, listener, errListener);
                break;
        }
    }

    private void startProgressDialog() {
        if (progressDialog == null) {
            progressDialog = ProgressDialogCustom.createDialog(view.getContext());
            progressDialog.setMessage("正在定位...");
        }
        progressDialog.show();
    }

    private void stopProgerssDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog=null;
        }
    }
}
