package com.graygrass.healthylife.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.graygrass.healthylife.R;
import com.graygrass.healthylife.model.BookShowModel;
import com.graygrass.healthylife.model.CookShowModel;
import com.graygrass.healthylife.model.DrugShowModel;
import com.graygrass.healthylife.model.DrugStoreListModel;
import com.graygrass.healthylife.model.FoodShowModel;
import com.graygrass.healthylife.model.HospitalLocationModel;
import com.graygrass.healthylife.model.IllnessModel;
import com.graygrass.healthylife.model.KnowledgeModel;
import com.graygrass.healthylife.model.SearchAllModel;
import com.graygrass.healthylife.util.Common;
import com.graygrass.healthylife.util.DoRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 橘沐 on 2015/12/16.
 * 显示列表条目listView item的具体信息
 */
public class ItemActivity extends Activity implements View.OnClickListener {
    private TextView tv_titleDetail, tv_keywords, tv_countDetail, tv_contentDetail, tv_descriptionDetail;
    private ImageView img_itemDetail;
    private ImageButton img_back;
    private ListView lv_book_content;
    private Button btn_showMap;
    private String title, content, keywords, imgUrl, count, description;
    private String kind;//要显示具体信息的类型（knowledge、drugShow...）
    private double x, y;//用于显示地图所需的经纬度
    private String address;//药店或医院地址

    //显示疾病信息需折叠的部分
    private LinearLayout linear_illness;
    private final int num = 6;//展示疾病具体信息时的长展示项（可折叠）
    private Button[] btns;
    private TextView[] tvs;
    private boolean[] flags = new boolean[num];//各个项是否折叠
    int btn1_id, tv1_id;//第一个折叠的按钮和textView的id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemdetail);
        initView();
        initListener();

        //判断是显示哪个列表的具体信息
        kind = getIntent().getStringExtra("kind");
        switch (kind) {
            case "knowledge":
                initKnowledgeData();
                break;
            case "drugShow":
                initDrugShowData();
                break;
            case "bookShow":
                initBookShowData();
                break;
            case "illness":
                initIllnessData();
                break;
            case "searchAll":
                initSearchAllData();
                break;
            case "foodShow":
                initFoodShowData();
                break;
            case "cookShow":
                initCookShowData();
                break;
            case "drugStoreShow":
                initDrugStoreShow();
                break;
            case "hospitalShow":
                initHospitalShow();
                break;
        }

        putToText();
    }

    private void initView() {
        img_itemDetail = (ImageView) findViewById(R.id.img_itemDetail);
        img_back = (ImageButton) findViewById(R.id.img_back);
        tv_titleDetail = (TextView) findViewById(R.id.tv_titleDetail);
        tv_countDetail = (TextView) findViewById(R.id.tv_countDetail);
        tv_contentDetail = (TextView) findViewById(R.id.tv_contentDetail);
        tv_keywords = (TextView) findViewById(R.id.tv_keywords);
        tv_descriptionDetail = (TextView) findViewById(R.id.tv_descriptionDetail);
        lv_book_content = (ListView) findViewById(R.id.lv_book_content);
        btn_showMap = (Button) findViewById(R.id.btn_showMap);

        //疾病需折叠部分
        linear_illness = (LinearLayout) findViewById(R.id.linear_illness);
        btns = new Button[num];
        tvs = new TextView[num];
        btn1_id = R.id.btn1;
        tv1_id = R.id.tv1;
        for (int i = 0; i < num; i++) {
            btns[i] = (Button) findViewById(btn1_id + i * 2);
            tvs[i] = (TextView) findViewById(tv1_id + i * 2);
            flags[i] = false;//设初值，默认为TextView不显示
        }

    }

    private void initListener() {
        //返回
        img_back.setOnClickListener(this);
        //折叠按钮
        for (int i = 0; i < num; i++) {
            btns[i].setOnClickListener(this);
        }
        //显示地图
        btn_showMap.setOnClickListener(this);
    }

    /**
     * 将值放入控件中
     */
    public void putToText() {
        tv_titleDetail.setText(title);
        tv_descriptionDetail.setText(description);
        tv_keywords.setText(keywords);
        tv_countDetail.setText(count);
        tv_contentDetail.setText(content);
        imageRequest(imgUrl);

    }

    /**
     * 显示图片
     *
     * @param url
     */
    public void imageRequest(String url) {
        if (url != null && !url.equals("")) {
            //有有效图片
            if (!url.contains("http://tnfs.tngou.net/img"))
                //原链接完整则不添加,反之添加
                url = "http://tnfs.tngou.net/image" + url;
            DoRequest.doImageRequest(url, img_itemDetail);
        }
    }

    /**
     * 获取从KnowledgeFragment传过来的数据，并将其放到itemdetail.xml中
     */
    private void initKnowledgeData() {
        Bundle bundle = getIntent().getExtras();
        KnowledgeModel.Tngou k = (KnowledgeModel.Tngou) bundle.getSerializable("knowledgeList");
        if(k!=null) {
            title = k.getTitle();
            description = "描述： " + k.getDescription();
            count = "浏览量： " + k.getCount() + "";
            content = Html.fromHtml(k.getMessage()).toString();
            keywords = "关键词： " + k.getKeywords();
            imgUrl = k.getImg();
            if (imgUrl.equals("/lore/default.jpg"))
                //图片为默认图片，证明无有效图片
                imgUrl = "";
        }
    }

    /**
     * 药品详情
     */
    public void initDrugShowData() {
        //获得药物id
        long id = (long) getIntent().getSerializableExtra("id");
        //请求药物详细信息
        RequestQueue queue = Volley.newRequestQueue(ItemActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, Common.drugShowUrl + "?id=" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("initDrugShow()返回结果：》》》》》》" + s);
                        Gson gson = new Gson();
                        DrugShowModel ds = gson.fromJson(s, DrugShowModel.class);
                        title = "药品详情";
                        keywords = "关键词： " + ds.getKeywords();
                        count = "浏览量： " + ds.getCount();
                        description = "描述： " + ds.getDescription();
                        imgUrl = ds.getImg();
                        content = Html.fromHtml(ds.getMessage()).toString();
                        putToText();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("错误：》》》》》》》" + volleyError.getMessage());
            }
        });
        queue.add(request);
    }

    /**
     * 健康图书详情
     */
    public void initBookShowData() {
        //获得药物id
        long id = (long) getIntent().getSerializableExtra("id");
        //请求药物详细信息
        RequestQueue queue = Volley.newRequestQueue(ItemActivity.this);
        StringRequest request = new StringRequest(Request.Method.GET, Common.bookShowUrl + "?id=" + id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        System.out.println("initBookShowData->Show()返回结果：》》》》》》" + s);
                        Gson gson = new Gson();
                        BookShowModel bs = gson.fromJson(s, BookShowModel.class);
                        title = "图书详情";
                        keywords = "作者: " + bs.getAuthor();
                        count = "浏览量： " + bs.getCount();
                        description = "";
                        imgUrl = bs.getImg();
                        content = "";
                        List<BookShowModel.T> list = bs.getList();
                        List<Map<String, String>> simlist = new ArrayList<>();
                        Map<String, String> map = new HashMap<>();
                        for (int i = 0; i < list.size(); i++) {
                            map.put("title_main", list.get(i).getTitle());
                            map.put("message", Html.fromHtml(list.get(i).getMessage()).toString());
                            simlist.add(map);
                        }
                        SimpleAdapter adapter = new SimpleAdapter
                                (ItemActivity.this, simlist, R.layout.bookshow_content,
                                        new String[]{"title_main", "message"},
                                        new int[]{R.id.tv_bookDetail_title, R.id.tv_bookDetail_content});
                        lv_book_content.setAdapter(adapter);
                        putToText();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("错误：》》》》》》》" + volleyError.getMessage());
            }
        });
        queue.add(request);
    }


    private void initIllnessData() {
        Bundle bundle = getIntent().getExtras();
        IllnessModel.Illness ill = (IllnessModel.Illness) bundle.getSerializable("illness");
        if (ill != null) {
            title = ill.getName();
            description = "描述： " + ill.getDescription();
            count = "浏览量： " + ill.getCount();
            content = "";
            keywords = "关键词： " + ill.getKeywords();
            imgUrl = ill.getImg();
       /* if (imgUrl.equals("/lore/default.jpg"))
            //图片为默认图片，证明无有效图片
            imgUrl = "";*/

            //设置linear_illness可见
            linear_illness.setVisibility(View.VISIBLE);
            btns[0].setText("病症描述 ▲");
            tvs[0].setText(Html.fromHtml(ill.getSymptomtext()).toString());
            btns[1].setText("用药说明 ▲");
            tvs[1].setText(Html.fromHtml(ill.getDrugtext()).toString());
            btns[2].setText("病因 ▲");
            tvs[2].setText(Html.fromHtml(ill.getCausetext()).toString());
            btns[3].setText("预防护理 ▲");
            tvs[3].setText(Html.fromHtml(ill.getCaretext()).toString());
            btns[4].setText("检测说明 ▲");
            tvs[4].setText(Html.fromHtml(ill.getChecktext()).toString());
            btns[5].setText("健康保健 ▲");
            tvs[5].setText(Html.fromHtml(ill.getFoodtext()).toString());
        }
    }


    private void initSearchAllData() {
        Bundle bundle = getIntent().getExtras();
        SearchAllModel.Tngou searchAll = (SearchAllModel.Tngou) bundle.getSerializable("searchAll");
        if (searchAll != null) {
            title = searchAll.getTitle();
            description = "描述： " + searchAll.getDescription();
            count = "";
            content = Html.fromHtml(searchAll.getMessage()).toString();
            keywords = "关键词： " + searchAll.getKeywords();
            imgUrl = searchAll.getImg();//查询返回的地址是完整的
        }
    }

    private void initFoodShowData() {
        Bundle bundle = getIntent().getExtras();
        FoodShowModel f = (FoodShowModel) bundle.getSerializable("foodShow");
        if (f != null) {
            title = f.getName();
            description = "描述：" + f.getDescription();
            count = "浏览量：" + f.getCount();
            if (TextUtils.isEmpty(f.getMessage())) {
                content = "";
            } else {
                content = Html.fromHtml(f.getMessage()).toString();
            }
            keywords = "关键词：" + f.getKeywords();
            imgUrl = f.getImg();
        }
    }

    private void initCookShowData() {
        Bundle bundle = getIntent().getExtras();
        CookShowModel f = (CookShowModel) bundle.getSerializable("cookShow");
        if (f != null) {
            title = f.getName();
            description = "描述：" + f.getDescription();
            count = "浏览量：" + f.getCount();
            if (TextUtils.isEmpty(f.getMessage()))
                content = "";
            else
                content = Html.fromHtml(f.getMessage()).toString();
            keywords = "关键词：" + f.getKeywords();
            imgUrl = f.getImg();
        }
    }

    private void initDrugStoreShow() {
        Bundle bundle = getIntent().getExtras();
        DrugStoreListModel.Tngou d = (DrugStoreListModel.Tngou) bundle.getSerializable("drugStore");
        if (d != null) {
            title = d.getName();
            keywords = "类型： " + d.getType();
            description = "地址： " + d.getAddress();
            count = "浏览量：" + d.getCount();
            content = "经营范围： " + d.getBusiness();
            btn_showMap.setVisibility(View.VISIBLE);
            x = d.getX();
            y = d.getY();
            address = d.getAddress();
            imgUrl = d.getImg();
            if (imgUrl.equals("/store/default.jpg"))
                imgUrl = "";
        }
    }

    private void initHospitalShow() {
        Bundle bundle = getIntent().getExtras();
        HospitalLocationModel.Tngou d = (HospitalLocationModel.Tngou) bundle.getSerializable("hospital");
        if (d != null) {
            title = d.getName();
            keywords = "等级： " + d.getLevel();
            description = "地址： " + d.getAddress();
            count = "浏览量：" + d.getCount();
            content = "联系电话： " + d.getTel();
            btn_showMap.setVisibility(View.VISIBLE);
            x = d.getX(); //获取经度
            y = d.getY(); //获取纬度
            address = d.getAddress();
            imgUrl = d.getImg();
            if (imgUrl.equals("/hospital/default.jpg"))
                imgUrl = "";
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_back) {
            finish();
        } else if (v.getId() == R.id.btn_showMap) {
            //点击按钮跳转显示地图
            Intent mapIntent = new Intent(ItemActivity.this, MyMapActivity.class);
            mapIntent.putExtra("x", x);
            mapIntent.putExtra("y", y);
            mapIntent.putExtra("address", address);
            mapIntent.putExtra("name", title);//医院、药店名称
            startActivity(mapIntent);
        } else {
            //点击某一按钮时改变
            for (int i = 0; i < num; i++) {
                //被点击的按钮对应textView显示
                if (v.getId() == (btn1_id + i * 2)) {
                    if (flags[i]) {
                        //原某一TextView为true，所以把所有都变为false,并使当前这个不显示
                        for (int j = 0; j < num; j++) {
                            flags[j] = false;
                            tvs[i].setVisibility(View.GONE);
                            btns[i].setText(btns[i].getText().toString().replace("▼", "▲"));
                        }
                    } else {
                        System.out.println("改变成true");
                        tvs[i].setVisibility(View.VISIBLE);
                        btns[i].setText(btns[i].getText().toString().replace("▲", "▼"));
                        flags[i] = true;
                    }
                }
            }
        }
    }
}

