package com.chat.zipchat.Common;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class MarshmallowPermission {

    public static final int PERMISSION_ALL = 1;
    public static final String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS
    };

    public static boolean hasPermissions(Context context) {
        if (context != null && PERMISSIONS != null) {
            for (String permission : PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}

