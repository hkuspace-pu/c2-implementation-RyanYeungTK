package com.example.cw2_apps.core.network;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyRequestQueue {

    private static MyRequestQueue instance;
    private static Context appCtx;
    private RequestQueue requestQueue;

    private MyRequestQueue(Context context) {
        appCtx = context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(appCtx);
    }

    public static synchronized MyRequestQueue getInstance(Context context) {
        if (instance == null) instance = new MyRequestQueue(context);
        return instance;
    }

    public <T> void add(Request<T> req) {

        if (req.getRetryPolicy() == null) {
            req.setRetryPolicy(new DefaultRetryPolicy(
                    8000, // timeoutMs
                    1,    // maxRetries
                    1.0f  // backoffMultiplier
            ));
        }
        requestQueue.add(req);

    }

}
