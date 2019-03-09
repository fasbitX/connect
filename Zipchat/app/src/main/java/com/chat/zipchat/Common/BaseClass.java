package com.chat.zipchat.Common;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.zipchat.R;
import com.chat.zipchat.Service.ApiClient;
import com.chat.zipchat.Service.ApiInterFace;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.chat.zipchat.Common.SessionManager.KEY_ID;
import static com.chat.zipchat.Common.SessionManager.PREF_NAME;

public class BaseClass {

    public static final int RequestPermissionCode = 1;
    public static final int MY_REQUEST_CODE_IMAGE = 100;
    public static final int MY_REQUEST_CODE_DOCUMENT = 101;

    public static final int IMAGE_PICKER_SELECT = 102;
    public static final int DOCUMENT_PICKER_SELECT = 103;
    public static final String NO_INTERNET = "Check your Internet Connection !";
    public static final String ACCESS_DENIED = "You are logged in from another device!";
    public static final String PUBLISHABLE_KEY = "pk_test_uzFnOtl3tNwStqKIi5Vflq61";

    public static final String PhotoDirectoryPath = Environment.getExternalStorageDirectory() + "/WhatsApp Clone/Photos";
    public static final String VideoDirectoryPath = Environment.getExternalStorageDirectory() + "/WhatsApp Clone/Videos";
    public static final String DocumentDirectoryPath = Environment.getExternalStorageDirectory() + "/WhatsApp Clone/Documents";

    public static final String DOLLAR_SYMBOL = "$ ";

    public static Dialog mProgressDialog, mPopupDoneDialog;
    public static ApiInterFace apiInterface = ApiClient.getClient().create(ApiInterFace.class);
    public static ApiInterFace apiInterfaceConvertion = ApiClient.getClientConvertion().create(ApiInterFace.class);
    public static ApiInterFace apiInterfacePayment = ApiClient.getClientPayment().create(ApiInterFace.class);
    public static ApiInterFace apiInterfaceNotification = ApiClient.getClientNotification().create(ApiInterFace.class);

    public static void HideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static void myToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void myLog(String hint, String msg) {
        Boolean value = true;
        if (value) {
            Log.e("Arun", hint + " " + msg);
        }
    }

    public static SessionManager sessionManager(Context context) {
        SessionManager sessionManager = new SessionManager(context);
        return sessionManager;
    }

    public static String UserId(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String userId = sharedpreferences.getString(KEY_ID, null);
        return userId;
    }

    public static String DeviceToken() {
        String device_token = FirebaseInstanceId.getInstance().getToken();
        return device_token;
    }

    public static void logout_User(Context context) {

        App.getmInstance().daoMaster.dropAllTables(App.getmInstance().daoSession.getDatabase(), true);
        App.getmInstance().daoMaster.createAllTables(App.getmInstance().daoSession.getDatabase(), true);

        SessionManager sessionManager = new SessionManager(context.getApplicationContext());
        sessionManager.logoutUser();
    }

    public static boolean eMailValidation(String emailstring) {
        if (null == emailstring || emailstring.length() == 0) {
            return false;
        }
        Pattern emailPattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        Matcher emailMatcher = emailPattern.matcher(emailstring);
        return emailMatcher.matches();
    }

    public static void snackbar(Context context, View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View sbview = snackbar.getView();
        sbview.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
        TextView textView = (TextView) sbview.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(context.getResources().getColor(android.R.color.white));
        snackbar.show();
    }

    public static void showSimpleProgressDialog(Context context) {
        if (context != null) {


            mProgressDialog = new Dialog(context, R.style.DialogThemeforview_pop);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            mProgressDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setContentView(R.layout.animation_loading);
            mProgressDialog.show();
        }
    }

    public static void removeProgressDialog() {
        if (null != mProgressDialog)
            mProgressDialog.dismiss();
    }

    public static String ConvertedDateTime() {
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        return mdformat.format(CurrentDateTime());
    }

    public static Date CurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static void Invitefriend(Context context) {

        String value = "Hey,\n" +
                "\n" +
                "WhatsApp Messenger is a fast, simple and secure app that I use to message and call the people I care about.\n" +
                "\n" +
                "Get it for free at https://play.google.com/store/apps/details?id=com.chat.whatsappclone";

        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(android.content.Intent.EXTRA_SUBJECT, "WhatsApp Clone");
        share.putExtra(android.content.Intent.EXTRA_TEXT, value);
        context.startActivity(Intent.createChooser(share, "Invite via"));
    }

    public static String getRealPathFromURI(Context mContent, Uri contentURI) {
        String result;
        Cursor cursor = mContent.getContentResolver().query(contentURI, null,
                null, null, null);

        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static void PopupDone(Context context) {
        mPopupDoneDialog = new Dialog(context);
        mPopupDoneDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPopupDoneDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mPopupDoneDialog.setContentView(R.layout.popup_transaction);
//        mPopupDoneDialog.setCancelable(false);
        mPopupDoneDialog.show();
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        myLog("thumbnile", bitmap.toString());
        return bitmap;

           /*Bitmap thumbnile = null;
                try {
                    thumbnile = retriveVideoFrameFromVideo(chatPojoArrayList.get(position).getFileUrl());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }*/

    }

    public static String PaymentConvertedDateTime(String date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date testDate = null;
        try {
            testDate = format.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy & hh:mm a");
        formatter.setTimeZone(TimeZone.getDefault());
        String newFormat = formatter.format(testDate);

        return newFormat;
    }

    public static String PopUpConvertedDateTime(String date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date testDate = null;
        try {
            testDate = format.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy 'at' hh:mm a");
        formatter.setTimeZone(TimeZone.getDefault());
        String newFormat = formatter.format(testDate);

        return newFormat;
    }

    public static float DecimalConvertion(float value) {
        return Float.valueOf(new DecimalFormat("##.#######").format(value));
    }

    public static String mImageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imagByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imagByte, Base64.DEFAULT);
    }

}
