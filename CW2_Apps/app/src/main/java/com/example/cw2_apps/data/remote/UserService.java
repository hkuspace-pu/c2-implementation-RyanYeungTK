package com.example.cw2_apps.data.remote;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.cw2_apps.core.network.MyRequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserService {

    private static final String BASE = "http://10.240.72.69/comp2000/coursework/";
    private final Context appCtx;

    public UserService(Context context) {
        this.appCtx = context.getApplicationContext();
    }

    public void createStudentDb(String studentId, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        String url = BASE + "create_student/" + studentId;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, null, onSuccess, onError);
        MyRequestQueue.getInstance(appCtx).add(req);
    }

    public void createUser(String studentId, JSONObject body, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        String url = BASE + "create_user/" + studentId;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, body, onSuccess, onError);
        MyRequestQueue.getInstance(appCtx).add(req);
    }

    public void readAllUsers(String studentId, Response.Listener<JSONObject> onSuccess, Response.ErrorListener onError) {
        String url = BASE + "read_all_users/" + studentId;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, onSuccess, onError);
        MyRequestQueue.getInstance(appCtx).add(req);
    }


}
