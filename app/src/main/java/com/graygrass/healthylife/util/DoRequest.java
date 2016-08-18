package com.graygrass.healthylife.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.graygrass.healthylife.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by 橘沐 on 2015/12/17.
 */
public class DoRequest {
    public static void doRequest(Context context, boolean isGET, String url, Response.Listener listener, Response.ErrorListener errListener) {
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest request;
        if (isGET)
            request = new StringRequest(Request.Method.GET, url, listener, errListener);
        else
            request = new StringRequest(Request.Method.POST, url, listener, errListener);

        queue.add(request);
    }

    public static void doImageRequest2(String url, final ImageView img) {
        if (url != null && !url.equals("")) {
            final DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoader.getInstance().loadImage(url, options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    img.setImageBitmap(loadedImage);
                }
            });
        }
    }

    public static void doImageRequest(String imageUrl,ImageView mImageView) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_photo)
                .showImageOnFail(R.drawable.loadingfail)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(imageUrl, mImageView, options);
    }
}
