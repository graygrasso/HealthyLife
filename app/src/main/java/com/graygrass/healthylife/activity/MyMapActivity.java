package com.graygrass.healthylife.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.navi.AMapNavi;
import com.graygrass.healthylife.R;

/**
 * Created by 橘沐 on 2016/1/14.
 */
public class MyMapActivity extends Activity implements LocationSource{
    private double x, y;//地图的经纬度
    private String address;//要显示的地点地址
    //声明变量
    private MapView mapView;
    private AMap aMap;
    //初始化导航引擎
//    AMapNavi mapNavi = AMapNavi.getInstance(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //显示地图
        setContentView(R.layout.activity_map);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        aMap = mapView.getMap();


        x = getIntent().getDoubleExtra("x", 0);
        y = getIntent().getDoubleExtra("y", 0);
        address = getIntent().getStringExtra("address");
        Log.d("map","x+y"+ x + "  " + y);
        Log.d("map","address:"+address);
        //显示坐标位置地点地图
        LatLng latLng = new LatLng(y,x);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20000));
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

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

}
