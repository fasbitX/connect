package com.chat.zipchat.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.chat.zipchat.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

import static com.chat.zipchat.Common.BaseClass.removeProgressDialog;
import static com.chat.zipchat.Common.BaseClass.showSimpleProgressDialog;


public class ZoomActivity extends AppCompatActivity implements View.OnClickListener {

    PhotoView mPhotoView;
    ImageView mImageBack;
    String mPhoto;
    int value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        mPhotoView = findViewById(R.id.photo_view);
        mPhotoView.setMaximumScale(5.0f);
        mImageBack = findViewById(R.id.mImageBack);
        mImageBack.setOnClickListener(this);

        value = getIntent().getIntExtra("Value", 0);

        if (value == 1) {
            mPhoto = getIntent().getStringExtra("PATH");
            File file = new File(mPhoto);
            Uri imageUri = Uri.fromFile(file);
            Glide.with(this).load(imageUri).error(R.drawable.thumbnail_photo).into(mPhotoView);

        } else if (value == 2) {
            showSimpleProgressDialog(this);
            mPhoto = getIntent().getStringExtra("URL");
            Glide.with(this).load(mPhoto).error(R.drawable.thumbnail_photo).
                    listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            removeProgressDialog();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            removeProgressDialog();
                            return false;
                        }
                    }).into(mPhotoView);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImageBack:
                finish();
                break;
        }
    }

}
