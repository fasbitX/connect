package com.chat.zipchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.chat.zipchat.Activity.ChatActivity;
import com.chat.zipchat.Common.App;
import com.chat.zipchat.Model.Contact.ResultItem;
import com.chat.zipchat.Model.Contact.ResultItemDao;
import com.chat.zipchat.Model.Favourites.FavouritePojo;
import com.chat.zipchat.R;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> {


    Context mContext;
    List<FavouritePojo> arrayList;

    public FavouriteAdapter(Context mContext, List<FavouritePojo> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_favourites, viewGroup, false);
        return new FavouriteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {

        final List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                ResultItemDao.Properties.Id.eq(arrayList.get(position).getFavouriteId())).list();

        if (resultItems.size() > 0) {
            Glide.with(mContext).load(resultItems.get(0).getProfile_picture()).error(R.drawable.defult_user).into(viewHolder.mImgFavourite);
        }


        viewHolder.mFavouriteRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("toId", arrayList.get(position).getFavouriteId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImgFavourite;
        View mViewFavourite;
        RelativeLayout mFavouriteRl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImgFavourite = (itemView).findViewById(R.id.mImgFavourite);
            mViewFavourite = (itemView).findViewById(R.id.mViewFavourite);
            mFavouriteRl = (itemView).findViewById(R.id.mFavouriteRl);
        }
    }
}
