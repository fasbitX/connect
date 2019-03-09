package com.chat.zipchat.Common;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.chat.zipchat.Model.Chat.ChatPojoDao;
import com.chat.zipchat.Model.Chat.DaoMaster;
import com.chat.zipchat.Model.Chat.DaoSession;
import com.chat.zipchat.Model.ChatList.ChatListPojoDao;
import com.chat.zipchat.Model.Contact.ContactResponseDao;
import com.chat.zipchat.Model.Contact.ResultItemDao;
import com.chat.zipchat.Model.Favourites.FavouritePojoDao;
import com.chat.zipchat.Model.LocalDataPojoDao;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;

import io.fabric.sdk.android.Fabric;


public class App extends Application {

    public static App mInstance;
    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase db;
    public DaoMaster daoMaster;
    public DaoSession daoSession;
    public ContactResponseDao contactResponseDao;
    public ResultItemDao resultItemDao;

    public ChatPojoDao chatPojoDao;
    public ChatListPojoDao chatListPojoDao;
    public FavouritePojoDao favouritePojoDao;
    public LocalDataPojoDao localDataPojoDao;

    @Override
    public void onCreate() {
        super.onCreate();


        mInstance = this;
        helper = new DaoMaster.DevOpenHelper(this, "gks-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        contactResponseDao = daoSession.getContactResponseDao();
        resultItemDao = daoSession.getResultItemDao();

        chatPojoDao = daoSession.getChatPojoDao();
        chatListPojoDao = daoSession.getChatListPojoDao();
        favouritePojoDao = daoSession.getFavouritePojoDao();
        localDataPojoDao = daoSession.getLocalDataPojoDao();

        FirebaseApp.initializeApp(this);
        Fabric.with(this, new Crashlytics());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static App getmInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
