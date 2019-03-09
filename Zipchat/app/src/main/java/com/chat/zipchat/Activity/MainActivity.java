package com.chat.zipchat.Activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.Fragment.ContactsFragment;
import com.chat.zipchat.Fragment.HomeFragment;
import com.chat.zipchat.Fragment.PaymentFragment;
import com.chat.zipchat.Fragment.ProfileFragment;
import com.chat.zipchat.Fragment.SettingsFragment;
import com.chat.zipchat.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static com.chat.zipchat.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.Common.BaseClass.Invitefriend;
import static com.chat.zipchat.Common.BaseClass.UserId;
import static com.chat.zipchat.Common.BaseClass.isOnline;
import static com.chat.zipchat.Common.SessionManager.KEY_PROFILE_PIC;
import static com.chat.zipchat.Common.SessionManager.KEY_USERNAME;
import static com.chat.zipchat.Common.SessionManager.PREF_NAME;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    FrameLayout frame;
    FragmentManager fragmentManager;
    public static ImageView mImgUser;
    TextView mTxtUserName, mToolbarTitle;

    public static Toolbar toolbar;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mToolbarTitle = findViewById(R.id.mToolbarTitle);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        fragmentManager = getSupportFragmentManager();
        frame = (FrameLayout) findViewById(R.id.frame);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.menu_icon, getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        View hView = navigationView.getHeaderView(0);
        mImgUser = hView.findViewById(R.id.mImgUser);
        mTxtUserName = hView.findViewById(R.id.mTxtUserName);

        mImgUser.setOnClickListener(this);

        SharedPreferences sharedpreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        Glide.with(this).load(sharedpreferences.getString(KEY_PROFILE_PIC, null)).error(R.drawable.defult_user).into(mImgUser);
        mTxtUserName.setText(sharedpreferences.getString(KEY_USERNAME, null));

        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.frame, homeFragment).commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            setResult(Activity.RESULT_CANCELED);
            finishAffinity();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        item.setChecked(true);

        if (id == R.id.nav_chats) {
            HomeFragment homeFragment = new HomeFragment();
            mToolbarTitle.setText(getResources().getText(R.string.chats));
            fragmentManager.beginTransaction().replace(R.id.frame, homeFragment).commit();
        } else if (id == R.id.nav_contacts) {
            mToolbarTitle.setText(getResources().getText(R.string.contacts));
            ContactsFragment contactsFragment = new ContactsFragment(this);
            fragmentManager.beginTransaction().replace(R.id.frame, contactsFragment).commit();
        } else if (id == R.id.nav_profile) {
            mToolbarTitle.setText(getResources().getText(R.string.profile));
            ProfileFragment profileFragment = new ProfileFragment(this);
            fragmentManager.beginTransaction().replace(R.id.frame, profileFragment).commit();
        } else if (id == R.id.nav_settings) {
            mToolbarTitle.setText(getResources().getText(R.string.settings));
            SettingsFragment settingsFragment = new SettingsFragment();
            fragmentManager.beginTransaction().replace(R.id.frame, settingsFragment).commit();
        } else if (id == R.id.nav_payments) {
            mToolbarTitle.setText(getResources().getText(R.string.payments));
            PaymentFragment paymentFragment = new PaymentFragment(this);
            fragmentManager.beginTransaction().replace(R.id.frame, paymentFragment).commit();
        } else if (id == R.id.nav_invite_friend) {
            Invitefriend(this);
        } else if (id == R.id.nav_help) {

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseReference userLastOnlineRef = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(this)).child("profile-details");

        if (isOnline(this)) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("isOnline", "1");
            map.put("offline-time", ConvertedDateTime());
            userLastOnlineRef.updateChildren(map);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DatabaseReference userLastOnlineRef = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(this)).child("profile-details");

        HashMap<String, Object> map = new HashMap<>();
        map.put("isOnline", "0");
        map.put("offline-time", ConvertedDateTime());
        userLastOnlineRef.onDisconnect().updateChildren(map);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImgUser:
                mToolbarTitle.setText(getResources().getText(R.string.profile));
                ProfileFragment profileFragment = new ProfileFragment(this);
                fragmentManager.beginTransaction().replace(R.id.frame, profileFragment).commit();
                drawer.closeDrawer(GravityCompat.START);
                break;
        }
    }

}
