package com.chat.zipchat.Common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.chat.zipchat.Activity.MainActivity;
import com.chat.zipchat.Activity.SigninActivity;

public class SessionManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    public static final String PREF_NAME = "Arun";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_ID = "id";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_PIC = "profile_pic";
    public static final String PHONE = "phone";
    public static final String PHONE_CODE = "phone_code";
    public static final String STATUS = "status";
    public static final String STELLAR_ADDRESS = "stellar_address";
    public static final String STELLAR_SEED = "stellar_seed";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String id, String username, String profile_pic,
                                   String access_token, String phone, String phone_code, String status,String stellar_address,String stellar_seed) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PROFILE_PIC, profile_pic);
        editor.putString(ACCESS_TOKEN, access_token);
        editor.putString(PHONE, phone);
        editor.putString(PHONE_CODE, phone_code);
        editor.putString(STATUS, status);
        editor.putString(STELLAR_ADDRESS, stellar_address);
        editor.putString(STELLAR_SEED, stellar_seed);
        editor.commit();
    }

    public void checkLogin() {

        if (this.isLoggedIn()) {
            Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, SigninActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }


}
