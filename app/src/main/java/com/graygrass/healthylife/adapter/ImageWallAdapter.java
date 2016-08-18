package com.graygrass.healthylife.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.graygrass.healthylife.R;
import com.graygrass.healthylife.model.CookListModel;
import com.graygrass.healthylife.model.FoodListModel;
import com.graygrass.healthylife.util.DoRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/28.
 */
public class ImageWallAdapter extends BaseAdapter {
    private List list;
    private Context context;
    private String type;

    public ImageWallAdapter(List list, Context context, String type) {
        this.list = list;
        this.context = context;
        this.type = type;
    }

    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item, null);
            viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo);
            viewHolder.photo_title = (TextView) convertView.findViewById(R.id.photo_title);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        String imgUrl=null,name=null;
        if(type.equals("food")) {
            FoodListModel.Tngou food = (FoodListModel.Tngou) list.get(position);
            imgUrl = food.getImg();
            name=food.getName();
        }else if(type.equals("cook")) {
            CookListModel.Tngou food = (CookListModel.Tngou) list.get(position);
            imgUrl = food.getImg();
            name=food.getName();
        }
        doImageRequest(imgUrl, viewHolder);
        viewHolder.photo_title.setText(name);
        return convertView;
    }

    /**
     * 根据图片的URL地址加载图片
     *
     * @param url
     * @param viewHolder
     * @param position
     */
    private void doImageRequest(String url, ViewHolder viewHolder, int position) {
        if (url != null && !url.equals("")) {
            //有图片
            url = "http://tnfs.tngou.net/image" + url;  //图片完整地址
            if (url.equals("http://tnfs.tngou.net/image/lore/default.jpg"))
                //图片为默认图片，证明无有效图片
                url = "";
            else
                url = url + "_250x200";  //改变图片大小
            final DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            final ViewHolder finalViewHolder = viewHolder;
            ImageLoader.getInstance().loadImage(url, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    finalViewHolder.photo.setImageBitmap(loadedImage);
                }
            });
        }
    }

    private void doImageRequest(String url,ViewHolder viewHolder) {
        if (url != null && !url.equals("")) {
            //有图片
            url = "http://tnfs.tngou.net/image" + url;  //图片完整地址
            if (url.equals("http://tnfs.tngou.net/image/lore/default.jpg"))
                //图片为默认图片，证明无有效图片
                url = "";
            else
                url = url + "_250x200";  //改变图片大小
            DoRequest.doImageRequest(url,viewHolder.photo);
        }
    }

    class ViewHolder {
        ImageView photo;
        TextView photo_title;
    }
}
