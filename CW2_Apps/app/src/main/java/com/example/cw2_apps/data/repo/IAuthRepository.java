package com.example.cw2_apps.data.repo;

import android.content.Context;
import com.example.cw2_apps.domain.model.AuthSession;

public interface IAuthRepository {
    interface LoginCallback {
        void onSuccess(AuthSession session);
        void onError(String message);
    }
    void login(Context ctx, String studentId, String username, String password, LoginCallback cb);
}

