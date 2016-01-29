package com.graygrass.healthylife.util;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
}
