package com.graygrass.healthylife.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.BusRouteOverlay;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.graygrass.healthylife.R;

/**
 * Created by 橘沐 on 2016/1/14.
 * 显示地图并可查询路线
 */
public class MyMapActivity extends Activity implements LocationSource, RouteSearch.OnRouteSearchListener, AMapLocationListener {
    private String address;//要显示的地点地址
    private String endName;//终点名称（医院、药店名称）
    //声明变量
    private MapView mapView;
    private AMap aMap;
    private Button btn_bus, btn_drive, btn_walk;

    private final static int BUS = 1;
    private final static int DRIVE = 2;
    private final static int WALK = 3;
    private int routeType = BUS;//选择的路径类型
    private RouteSearch routeSearch;

    private LocationSource.OnLocationChangedListener locationListener;
    private AMapLocationClient locationClient;  //定位对象

    private LatLonPoint startPoint; //起点经纬度
    private LatLonPoint endPoint; //终点经纬度
    private LatLng endMark;//终点标记

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //显示地图
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        initData();
        initView();
        initListener();
    }

    private void initData() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        double x = getIntent().getDoubleExtra("x", 0); //目标地经度
        double y = getIntent().getDoubleExtra("y", 0); //目标地纬度
        address = getIntent().getStringExtra("address");  //目标地址名称
        endName = getIntent().getStringExtra("name");
        endPoint = new LatLonPoint(y, x);//初始化终点坐标
        endMark = new LatLng(y, x); //标记终点坐标
        System.out.println("y 纬度："+y +"x 经度：  "+x);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(13));//缩放比例 13对应1km

        setUpMap();
        setMark();
        //显示比例尺
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setScaleControlsEnabled(true);
        routeSearch = new RouteSearch(this);
    }

    private void initView() {
        btn_bus = (Button) findViewById(R.id.btn_bus);
        btn_drive = (Button) findViewById(R.id.btn_drive);
        btn_walk = (Button) findViewById(R.id.btn_walk);
    }

    private void initListener() {
        routeSearch.setRouteSearchListener(this);
    }

    /**
     * AMap的一些属性
     */
    private void setUpMap() {
        //自定义小蓝点样式
        MyLocationStyle style = new MyLocationStyle();
        style.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));//设置小蓝点的图标
        style.strokeColor(Color.GREEN);//设置圆形边框的颜色
        style.strokeWidth(1.0f);//设置圆形边框的粗细
        style.radiusFillColor(Color.parseColor("#7076F505"));//设置圆形边框的填充颜色
        aMap.setMyLocationStyle(style);
        aMap.setLocationSource(this);//设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置定位按钮显示
        aMap.setMyLocationEnabled(true); //可触发定位
    }

    /**
     * 往地图上添加一个终点的marker
     */
    private void setMark() {
        com.amap.api.maps2d.model.Marker marker = aMap.addMarker(new MarkerOptions()
                .position(endMark)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(endName)
                .snippet(address)
                .draggable(true));
        marker.showInfoWindow();// 设置默认显示一个infowinfow
    }

    public void bus(View v) {
        routeType = BUS;
        btn_bus.setBackgroundColor(Color.WHITE);
        btn_walk.setBackgroundColor(Color.parseColor("#ECECEC"));
        btn_drive.setBackgroundColor(Color.parseColor("#ECECEC"));
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        // 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
        RouteSearch.BusRouteQuery busRoute = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BusDefault, "重庆", 0);
        routeSearch.calculateBusRouteAsyn(busRoute);
    }

    public void drive(View v) {
        routeType = DRIVE;
        btn_drive.setBackgroundColor(Color.WHITE);
        btn_walk.setBackgroundColor(Color.parseColor("#ECECEC"));
        btn_bus.setBackgroundColor(Color.parseColor("#ECECEC"));
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        RouteSearch.DriveRouteQuery driveRoute = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null, null, "");
        routeSearch.calculateDriveRouteAsyn(driveRoute);
    }

    public void walk(View v) {
        routeType = WALK;
        btn_walk.setBackgroundColor(Color.WHITE);
        btn_bus.setBackgroundColor(Color.parseColor("#ECECEC"));
        btn_drive.setBackgroundColor(Color.parseColor("#ECECEC"));
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
        RouteSearch.WalkRouteQuery walkRoute = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
        routeSearch.calculateWalkRouteAsyn(walkRoute);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    /**
     * 激活定位
     *
     * @param onLocationChangedListener
     */
    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        locationListener = onLocationChangedListener;
        if (locationClient == null) {
            locationClient = new AMapLocationClient(this);
            AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
            //设置定位监听
            locationClient.setLocationListener(this);
            //设置为高精度定位模式
            locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            locationClient.setLocationOption(locationClientOption);
            locationClient.startLocation();//开始定位
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        locationListener = null;
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        locationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (locationListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                locationListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
                double lat = aMapLocation.getLatitude();//获得所在地的纬度
                double lng = aMapLocation.getLongitude();//获得所在地的经度
                startPoint = new LatLonPoint(lat, lng);
                Log.d("startingPoint", "经度：" + lng + " 纬度：" + lat);
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 公交路线
     *
     * @param busRouteResult
     * @param rCode          错误码
     */
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int rCode) {
        if (rCode == 0) {
            if (busRouteResult != null && busRouteResult.getPaths() != null
                    && busRouteResult.getPaths().size() > 0) {
                BusPath busPath = busRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap,
                        busPath,
                        busRouteResult.getStartPos(),
                        busRouteResult.getTargetPos()
                );
                routeOverlay.removeFromMap();
                routeOverlay.addToMap();
                routeOverlay.zoomToSpan();
            } else {
                Toast.makeText(MyMapActivity.this, "没有查询到相关结果", Toast.LENGTH_SHORT).show();
            }
        } else if (rCode == 27) {
            Toast.makeText(MyMapActivity.this, "搜索失败！请检查网络连接", Toast.LENGTH_SHORT).show();
        } else if (rCode == 32) {
            Toast.makeText(MyMapActivity.this, "key验证无效", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyMapActivity.this, "未知错误，请稍后重试！错误码为：" + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 驾车路线
     *
     * @param driveRouteResult
     * @param rCode
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int rCode) {
        if (rCode == 0) {
            if (driveRouteResult != null && driveRouteResult.getPaths() != null
                    && driveRouteResult.getPaths().size() > 0) {
                DrivePath drivePath = driveRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                        this, aMap, drivePath, driveRouteResult.getStartPos(),
                        driveRouteResult.getTargetPos());
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            } else {
                Toast.makeText(MyMapActivity.this, "没有查询到相关结果", Toast.LENGTH_SHORT).show();
            }
        } else if (rCode == 27) {
            Toast.makeText(MyMapActivity.this, "搜索失败！请检查网络连接", Toast.LENGTH_SHORT).show();
        } else if (rCode == 32) {
            Toast.makeText(MyMapActivity.this, "key验证无效", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyMapActivity.this, "未知错误，请稍后重试！错误码为：" + rCode, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 步行路线
     *
     * @param walkRouteResult
     * @param rCode
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int rCode) {
        if (rCode == 0) {
            if (walkRouteResult != null && walkRouteResult.getPaths() != null
                    && walkRouteResult.getPaths().size() > 0) {
                WalkPath walkPath = walkRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
                WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
                        aMap, walkPath, walkRouteResult.getStartPos(),
                        walkRouteResult.getTargetPos());
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
            } else {
                Toast.makeText(MyMapActivity.this, "没有查询到相关结果", Toast.LENGTH_SHORT).show();
            }
        } else if (rCode == 27) {
            Toast.makeText(MyMapActivity.this, "搜索失败！请检查网络连接", Toast.LENGTH_SHORT).show();
        } else if (rCode == 32) {
            Toast.makeText(MyMapActivity.this, "key验证无效", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MyMapActivity.this, "未知错误，请稍后重试！错误码为：" + rCode, Toast.LENGTH_SHORT).show();
        }
    }
}
