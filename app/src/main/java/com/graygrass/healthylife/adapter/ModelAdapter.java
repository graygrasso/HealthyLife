package com.graygrass.healthylife.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.graygrass.healthylife.R;
import com.graygrass.healthylife.model.BookListModel;
import com.graygrass.healthylife.model.DrugListModel;
import com.graygrass.healthylife.model.DrugStoreListModel;
import com.graygrass.healthylife.model.FoodListModel;
import com.graygrass.healthylife.model.HospitalLocationModel;
import com.graygrass.healthylife.model.IllnessModel;
import com.graygrass.healthylife.model.KnowledgeModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by 橘沐 on 2015/12/16.
 * 展示listView中的listitem的modelAdapter
 */
public class ModelAdapter extends BaseAdapter {
    List list;
    Context context;
    String kind;//用于标识是哪个Model 使用此model

    public ModelAdapter(List list, Context context, String kind) {
        this.list = list;
        this.context = context;
        this.kind = kind;
    }

    @Override
    public int getCount() {
        if (list != null)
            return list.size();
        else
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
        String title = null, description = null, count = null, url = null;
//        System.out.println(" position>>>>>>>>>>>>>> "+position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item, null);
            viewHolder.img_item = (ImageView) convertView.findViewById(R.id.img_item);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            viewHolder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(viewHolder);
        }
        if (kind.equals("knowledge")) {
            //健康知识页面
            KnowledgeModel.Tngou k = (KnowledgeModel.Tngou) list.get(position);
            title = k.getTitle();
            description = k.getDescription();
            count = k.getCount() + "";
            url = k.getImg();//获取图片地址
        } else if (kind.equals("druglist")) {
            //药物大全 的列表
            DrugListModel.Tngou dl = (DrugListModel.Tngou) list.get(position);
            title = dl.getName();
            description = dl.getDescription();
            count = dl.getCount() + "";
            url = dl.getImg();//获取图片地址
        } else if (kind.equals("booklist")) {
            //健康图书 的列表
            BookListModel.T bl = (BookListModel.T) list.get(position);
            title = bl.getName();
            description = "作者： " + bl.getAuthor();
            count = bl.getCount() + "";
            url = bl.getImg();//获取图片地址
        } else if (kind.equals("illness")) {
            //疾病信息 的列表
            IllnessModel.Illness ill = (IllnessModel.Illness) list.get(position);
            title = ill.getName();
            description = "症状：" + ill.getSymptom();
            count = ill.getCount() + "";
            url = ill.getImg();
        } else if (kind.equals("drugStore")) {
            //药店信息 的列表
            DrugStoreListModel.Tngou ds = (DrugStoreListModel.Tngou) list.get(position);
            title = ds.getName();//店名
            description = "地址：" + ds.getAddress();
            count = ds.getCount() + "";
            url = ds.getImg();
            if (url!=null&&url.equals("/store/default.jpg"))
                url = "";
        } else if(kind.equals("hospital")) {
            HospitalLocationModel.Tngou hospital = (HospitalLocationModel.Tngou) list.get(position);
            title=hospital.getName();
            description = "地址：" +hospital.getAddress();
            count = hospital.getCount()+"";
            url = hospital.getImg();
            if(url.equals("/hospital/default.jpg"))
                url = "";
        }

        viewHolder = (ViewHolder) convertView.getTag();
        viewHolder.tv_title.setText(title);
        //如果描述超过70个字符，则将后面的省略，保证布局不错乱
        if (description.length() > 60)
            description = description.substring(0, 60);
        viewHolder.tv_content.setText(Html.fromHtml(description));
        viewHolder.tv_count.setText("浏览量：" + count);
        //加载图片
        doImageRequest(url, viewHolder, position);
        return convertView;
    }

    class ViewHolder {
        TextView tv_title, tv_content, tv_count;
        ImageView img_item;
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
                    finalViewHolder.img_item.setImageBitmap(loadedImage);
                }
            });
        }
    }
}
