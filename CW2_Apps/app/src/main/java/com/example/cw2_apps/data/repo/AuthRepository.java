package com.example.cw2_apps.data.repo;

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.cw2_apps.data.remote.UserService;
import com.example.cw2_apps.domain.model.AuthSession;
import com.example.cw2_apps.domain.model.Role;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthRepository implements IAuthRepository {
    @Override
    public void login(Context ctx, String studentId, String username, String password, LoginCallback cb) {
        UserService api = new UserService(ctx);
        api.readAllUsers(studentId, resp -> {
            try {
                JSONArray users = extractUsers(resp);
                if (users == null) { cb.onError("Empty users list"); return; }

                JSONObject u = findUserByUsername(users, username);
                if (u == null || !isPasswordMatch(u, password)) {
                    cb.onError("Invalid username or password"); return;
                }
                Role role = toRole(u.optString("usertype", "guest"));
                cb.onSuccess(new AuthSession(u.optString("username"), role));
            } catch (Exception ex) {
                cb.onError("Parse error");
            }
        }, error -> cb.onError("Network error"));
    }
    @Override
    public void register(Context ctx, String studentId, String username, String password, String firstname, String lastname, String email, String contact, String usertype, RegisterCallback cb) {
        UserService api = new UserService(ctx);
        try {
            JSONObject body = new JSONObject();
            body.put("username", username);
            body.put("password", password);
            body.put("firstname", firstname);
            body.put("lastname", lastname);
            body.put("email", email);
            body.put("contact", contact);
            body.put("usertype", usertype); // guest

            api.createUser(studentId, body, resp -> cb.onSuccess(), err  -> cb.onError("Register failed"));
        } catch (JSONException e) {
            cb.onError("Invalid form data");
        }
    }


    private JSONArray extractUsers(JSONObject resp) {
        return resp != null ? resp.optJSONArray("users") : null;
    }

    private JSONObject findUserByUsername(JSONArray users, String username) {
        if (users == null || username == null) return null;
        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.optJSONObject(i);
            if (u != null && username.equalsIgnoreCase(u.optString("username"))) return u;
        }
        return null;
    }

    private boolean isPasswordMatch(JSONObject userObj, String rawPassword) {
        return userObj != null && rawPassword != null
                && rawPassword.equals(userObj.optString("password"));
    }

    private Role toRole(String usertype) {
        return "staff".equalsIgnoreCase(usertype) ? Role.STAFF : Role.GUEST;
    }

}

