package com.graygrass.healthylife;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.graygrass.healthylife.adapter.ImageWallAdapter;
import com.graygrass.healthylife.model.BookListModel;
import com.graygrass.healthylife.model.CookListModel;
import com.graygrass.healthylife.model.CookShowModel;
import com.graygrass.healthylife.model.FoodListModel;
import com.graygrass.healthylife.model.FoodShowModel;
import com.graygrass.healthylife.util.Common;
import com.graygrass.healthylife.util.DoRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/29.
 */
public class CookListActivity extends Activity {
    private Response.Listener<String> listener;
    private Response.ErrorListener errListener;
    private TextView tv_classify_title;
    private PullToRefreshGridView photo_wall;
    private String type;
    private ImageButton img_back;
    private ProgressDialog progressDialog;

    //用于分页
    private int page;
    private int cookListId;
    private List<CookListModel.Tngou> cookList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooklist);

        img_back = (ImageButton) findViewById(R.id.img_back);
        photo_wall = (PullToRefreshGridView) findViewById(R.id.photo_wall);
        tv_classify_title = (TextView) findViewById(R.id.tv_classify_title);

        photo_wall.setMode(PullToRefreshBase.Mode.BOTH);
        tv_classify_title.setText("健康菜谱——" + getIntent().getStringExtra("name"));

        initListener();

        progressDialog = new ProgressDialog(CookListActivity.this);
        progressDialog.show();
        type = "cookList";
        cookListId = getIntent().getIntExtra("id", 0);//要显示列表的id
        DoRequest.doRequest(this, true, Common.cookListUrl + "?id=" + cookListId + "&page=1&rows=42", listener, errListener);

    }

    private void initListener() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //网络请求
        errListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("cookList错误：" + volleyError.getMessage());
                photo_wall.onRefreshComplete();
            }
        };
        listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Gson gson = new Gson();
                if (type.equals("cookList"))
                    cookList(gson, s);
                else if (type.equals("cookShow"))
                    cookShow(gson, s);
            }
        };
        //上下拉
        photo_wall.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                page = 1;
                cookList = null;
                DoRequest.doRequest(CookListActivity.this, true, Common.cookListUrl + "?id=" + cookListId + "&page=" + page + "&rows=42", listener, errListener);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                page = ++page;
                DoRequest.doRequest(CookListActivity.this, true, Common.cookListUrl + "?id=" + cookListId + "&page=" + page + "&rows=42", listener, errListener);
            }
        });
    }

    /**
     * 请求回来将其显示到GridView 的监听
     *
     * @param gson
     * @param s    json数据
     */
    private void cookList(Gson gson, String s) {
        System.out.println("cookList 返回：  %%%%%%%%%%%%%%%%%%%"+s);
        final CookListModel food = gson.fromJson(s, CookListModel.class);
        if (cookList == null || page == 1)
            cookList = food.getTngou();
        else
            cookList.addAll(food.getTngou());
               /* List<String> imgList = new ArrayList<>();
                for(int i=0;i<list.size();i++) {
                    imgList.add(list.get(i).getImg());
                }*/
//                PhotoWallAdapter adapter = new PhotoWallAdapter(view.getContext(), 0, photo_wall, imgList);
        ImageWallAdapter adapter = new ImageWallAdapter(cookList, this, "cook");
        photo_wall.setAdapter(adapter);
        progressDialog.dismiss();
        photo_wall.onRefreshComplete();
        photo_wall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long cookId = food.getTngou().get(position).getId();
                type = "cookShow";
                DoRequest.doRequest(view.getContext(), true, Common.cookShowUrl + "?id=" + cookId, listener, errListener);
            }
        });
    }

    private void cookShow(Gson gson, String s) {
        CookShowModel f = gson.fromJson(s, CookShowModel.class);
        Intent intent = new Intent(this, ItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cookShow", f);
        intent.putExtras(bundle);
        intent.putExtra("kind", "cookShow");
        startActivity(intent);
    }

}
