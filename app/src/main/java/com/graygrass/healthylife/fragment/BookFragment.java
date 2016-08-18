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
import com.graygrass.healthylife.model.BookClassifyModel;
import com.graygrass.healthylife.model.BookListModel;
import com.graygrass.healthylife.util.Common;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 橘沐 on 2015/12/16.
 */
public class BookFragment extends Fragment {
    private View parentView;
    private PullToRefreshListView lv_book;
    private Spinner sp_book_classify;

    //用于分页
    private int page;
    private int bookListId;
    private List<BookListModel.T> bookList = null;
    private ModelAdapter modelAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_book, null, false);
        lv_book = (PullToRefreshListView) parentView.findViewById(R.id.lv_book);
        lv_book.setMode(PullToRefreshBase.Mode.BOTH);
        sp_book_classify = (Spinner) parentView.findViewById(R.id.sp_book_classify);
        return parentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();

        //读取图书类型缓存
        String bookClassifyString = MyApplication.mCache.getAsString("bookClassify");
        if (TextUtils.isEmpty(bookClassifyString))
            doBookClassify();
        else
            bookClassify(bookClassifyString);
        //读取“健康养生”列表缓存
        String bookListString = MyApplication.mCache.getAsString("bookList");
        if(!TextUtils.isEmpty(bookListString))
            bookList(bookListString);

    }

    private void initListener() {
        lv_book.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                doBookList(bookListId, 1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                doBookList(bookListId, ++page);
            }
        });
    }

    /**
     * 查询药品分类
     */
    public void doBookClassify() {
        RequestQueue queue = Volley.newRequestQueue(parentView.getContext());
        StringRequest request = new StringRequest(Request.Method.GET, Common.bookClassifyUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("doBookClassify()返回：》》》》》》" + s);
                MyApplication.mCache.put("bookClassify", s);//将图书分类放进缓存
                bookClassify(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("bookClassify错误：" + volleyError.getMessage());
            }
        });
        queue.add(request);
    }

    /**
     * 查询图书列表
     *
     * @param id 图书种类的id，作为请求参数
     */
    public void doBookList(final int id, final int page) {

        final RequestQueue queue = Volley.newRequestQueue(parentView.getContext());
        final StringRequest request = new StringRequest(Request.Method.GET, Common.bookListUrl + "?id=" + id + "&page=" + page + "&rows=20", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                System.out.println("doBookList()返回：》》》》" + s);
                if (id == 3 && page == 1)//如果id为3（健康养身），且为第一页的话就将其放进缓存
                    MyApplication.mCache.put("bookList", s);
                bookList(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("bookList错误：" + volleyError.getMessage());
                lv_book.onRefreshComplete();
            }
        });
        queue.add(request);
    }

    /**
     * 转到ItemActivity展示图书的具体信息
     *
     * @param id 图书的id
     */
    public void bookShow(long id) {
        Intent intent = new Intent(getActivity(), ItemActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("kind", "bookShow");//表明要显示的是图书详情
        startActivity(intent);
    }

    public void bookClassify(String s) {
        Gson gson = new Gson();
        final BookClassifyModel b = gson.fromJson(s, BookClassifyModel.class);
        List<BookClassifyModel.Tngou> list = b.getTngou();
        List<String> listTv = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String title = list.get(i).getName();
            listTv.add(title);
        }

        // ArrayAdapter 应用 自定义spinner内部布局（style_spinner.xml），只能存在一个TextView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (parentView.getContext(), R.layout.style_spinner, listTv);
        sp_book_classify.setAdapter(adapter);
        sp_book_classify.setSelection(2);//设置默认为健康养身(id 为3)
        sp_book_classify.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                page = 1;
                bookList = null;
                bookListId = b.getTngou().get(position).getId();
                doBookList(bookListId, page);//得到用于查询图书列表的id传入doBookList()
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void bookList(String s) {
        Gson gson = new Gson();
        final BookListModel bl = gson.fromJson(s, BookListModel.class);
        if (bookList == null || page == 1) {
            bookList = bl.getList();
            modelAdapter = new ModelAdapter(bookList, parentView.getContext(), "booklist");
            lv_book.setAdapter(modelAdapter);
        } else {
            bookList.addAll(bl.getList());
            modelAdapter.notifyDataSetChanged();
        }
        lv_book.onRefreshComplete();
        lv_book.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bookShow(bookList.get(position - 1).getId());//得到用于查询图书详情的id传入drugShow()
            }
        });
    }
}
