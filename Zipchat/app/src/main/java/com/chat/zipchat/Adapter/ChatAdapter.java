package com.chat.zipchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chat.zipchat.Activity.Payments.TransHistoryActivity;
import com.chat.zipchat.Activity.VideoActivity;
import com.chat.zipchat.Activity.WebActivity;
import com.chat.zipchat.Activity.ZoomActivity;
import com.chat.zipchat.Model.Chat.ChatPojo;
import com.chat.zipchat.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.chat.zipchat.Common.BaseClass.PhotoDirectoryPath;
import static com.chat.zipchat.Common.BaseClass.UserId;
import static com.chat.zipchat.Common.BaseClass.VideoDirectoryPath;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int EMPTY = 0;
    private static final int CHAT = 1;
    private static final int IMAGE = 2;
    private static final int AUDIO = 3;
    private static final int VIDEO = 4;
    private static final int DOCUMENT = 5;
    private static final int PAYMENT = 6;

    private Context mContext;
    private List<ChatPojo> chatPojoArrayList;

    String mSearch;

    public ChatAdapter(Context mContext, List<ChatPojo> chatPojoArrayList) {
        this.mContext = mContext;
        this.chatPojoArrayList = chatPojoArrayList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case CHAT:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case EMPTY:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case IMAGE:
                View viewImage = inflater.inflate(R.layout.list_image, parent, false);
                viewHolder = new ImageItem(viewImage);
                break;
            case VIDEO:
                View viewVideo = inflater.inflate(R.layout.list_video, parent, false);
                viewHolder = new VideoItem(viewVideo);
                break;
            case DOCUMENT:
                View viewDocument = inflater.inflate(R.layout.list_document, parent, false);
                viewHolder = new DocumentItem(viewDocument);
                break;
            case PAYMENT:
                View viewPayment = inflater.inflate(R.layout.list_payment, parent, false);
                viewHolder = new PaymentItem(viewPayment);
                break;
        }

        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.list_item_chat_message, parent, false);
        viewHolder = new ChatItem(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        switch (getItemViewType(position)) {

            case EMPTY:
                final ChatItem emptyItem = (ChatItem) viewHolder;

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    emptyItem.mOutMessageRl.setVisibility(View.VISIBLE);
                    emptyItem.txtOutMessage.setText(chatPojoArrayList.get(position).getText());
                    emptyItem.mOutMessageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                } else {
                    emptyItem.mInMessageRl.setVisibility(View.VISIBLE);
                    emptyItem.txtInMessage.setText(chatPojoArrayList.get(position).getText());
                }

                break;

            case CHAT:
                final ChatItem chatItem = (ChatItem) viewHolder;

                String mText = chatPojoArrayList.get(position).getText();

                //search
                Spannable spanText = null;
                if (!TextUtils.isEmpty(mSearch)) {
                    if (mText.contains(mSearch)) {
                        int startPos = mText.indexOf(mSearch);
                        int endPos = startPos + mSearch.length();

                        // StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
                        spanText = Spannable.Factory.getInstance().newSpannable(chatPojoArrayList.get(position).getText());
                        spanText.setSpan(new ForegroundColorSpan(Color.BLACK), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        spanText.setSpan(new BackgroundColorSpan(Color.YELLOW), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    chatItem.mOutMessageRl.setVisibility(View.VISIBLE);
                    chatItem.txtOutMessage.setText(chatPojoArrayList.get(position).getText());
                    chatItem.mOutMessageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    if (spanText != null) {
                        chatItem.txtOutMessage.setText(spanText, TextView.BufferType.SPANNABLE);
                    }


                } else {
                    chatItem.mInMessageRl.setVisibility(View.VISIBLE);
                    chatItem.txtInMessage.setText(chatPojoArrayList.get(position).getText());

                    /*layout params for time*/
                    LinearLayout.LayoutParams layoutParamsTime = (LinearLayout.LayoutParams) chatItem.mInMessageTime.getLayoutParams();
                    layoutParamsTime.gravity = Gravity.END;
                    chatItem.mInMessageTime.setLayoutParams(layoutParamsTime);
                    chatItem.mInMessageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                    if (spanText != null) {
                        chatItem.txtInMessage.setText(spanText, TextView.BufferType.SPANNABLE);
                    }

                }
                break;

            case IMAGE:
                final ImageItem imageItem = (ImageItem) viewHolder;
                final String getDirectoryPath = PhotoDirectoryPath + "/" + chatPojoArrayList.get(position).getMessageId();
                imageItem.mImageLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if ("null" != chatPojoArrayList.get(position).getFileUrl()) {
                            Intent mImageIntent = new Intent(mContext, ZoomActivity.class);
                            mImageIntent.putExtra("Value", 1);
                            mImageIntent.putExtra("PATH", getDirectoryPath);
                            mContext.startActivity(mImageIntent);
                        }
                    }
                });

                if ("null" == chatPojoArrayList.get(position).getFileUrl()) {

                    imageItem.mProgressSendImage.setVisibility(View.VISIBLE);
                    imageItem.mProgressReceiveImage.setVisibility(View.VISIBLE);

                } else {

                    imageItem.mProgressSendImage.setVisibility(View.GONE);
                    imageItem.mProgressReceiveImage.setVisibility(View.GONE);
                }


                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {

                    imageItem.mSendImageRl.setVisibility(View.VISIBLE);
                    imageItem.mReceiveImageRl.setVisibility(View.GONE);

                    imageItem.mSendImageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                    Glide.with(mContext).load(chatPojoArrayList.get(position).getFileUrl())
                            .error(R.drawable.thumbnail_photo).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageItem.mSendImage);
                } else {

                    imageItem.mReceiveImageRl.setVisibility(View.VISIBLE);
                    imageItem.mSendImageRl.setVisibility(View.GONE);

                    imageItem.mReceiveImageTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                    Glide.with(mContext).load(chatPojoArrayList.get(position).getFileUrl())
                            .error(R.drawable.thumbnail_photo).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageItem.mReceiveImage);
                }

                break;

            case VIDEO:
                final VideoItem videoView = (VideoItem) viewHolder;


              /*  MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(chatPojoArrayList.get(position).getFileUrl(), new HashMap<String, String>());
                Bitmap thumbnile = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                thumbnile.compress(Bitmap.CompressFormat.PNG, 100, stream);*/

                final String getDirectoryPathVideo = VideoDirectoryPath + "/" + chatPojoArrayList.get(position).getMessageId();

                videoView.mVideoLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("null" != chatPojoArrayList.get(position).getFileUrl()) {
                            Intent mVideoIntent = new Intent(mContext, VideoActivity.class);
//                            mVideoIntent.putExtra("URL", chatPojoArrayList.get(position).getFileUrl());
                            mVideoIntent.putExtra("URL", getDirectoryPathVideo);
                            mContext.startActivity(mVideoIntent);
                        }
                    }
                });


                if ("null" == chatPojoArrayList.get(position).getFileUrl()) {
                    videoView.mProgressSendVideo.setVisibility(View.VISIBLE);
                    videoView.mProgressReceiveVideo.setVisibility(View.VISIBLE);
                } else {
                    videoView.mProgressSendVideo.setVisibility(View.GONE);
                    videoView.mProgressReceiveVideo.setVisibility(View.GONE);
                }

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    videoView.mSendVideoRl.setVisibility(View.VISIBLE);
                    videoView.mReceiveVideoRl.setVisibility(View.GONE);
                    videoView.mSendVideoTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
//                    videoView.mSendVideo.setImageBitmap(thumbnile);

                } else {
                    videoView.mReceiveVideoRl.setVisibility(View.VISIBLE);
                    videoView.mSendVideoRl.setVisibility(View.GONE);
                    videoView.mReceiveVideoTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
//                    videoView.mReceiveVideo.setImageBitmap(thumbnile);
                }
                break;

            case DOCUMENT:
                final DocumentItem documentItem = (DocumentItem) viewHolder;

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {

                    documentItem.mSendDocumentCl.setVisibility(View.VISIBLE);
                    documentItem.mSendDocumentTime.setVisibility(View.VISIBLE);
                    documentItem.mSendDocumentTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));

                } else {

                    documentItem.mReceiveDocumentCl.setVisibility(View.VISIBLE);
                    documentItem.mReceiveDocumentTime.setVisibility(View.VISIBLE);
                    documentItem.mReceiveDocumentTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                }

                documentItem.mDocumentLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("null" != chatPojoArrayList.get(position).getFileUrl()) {
                            Intent mDocumentIntent = new Intent(mContext, WebActivity.class);
                            mDocumentIntent.putExtra("URL", chatPojoArrayList.get(position).getFileUrl());
                            mContext.startActivity(mDocumentIntent);
                        }
                    }
                });

                break;

            case PAYMENT:
                final PaymentItem paymentItem = (PaymentItem) viewHolder;

                double amount = Double.parseDouble(chatPojoArrayList.get(position).getText());
                double amountNew = Double.parseDouble(new DecimalFormat("##.#####").format(amount));

                if (chatPojoArrayList.get(position).getFromId().equalsIgnoreCase(UserId(mContext))) {
                    paymentItem.mSendPaymentRl.setVisibility(View.VISIBLE);
                    paymentItem.mSendPaymentAmount.setText(String.valueOf(amountNew));
                    paymentItem.mSendPaymentTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                } else {
                    paymentItem.mReceivePaymentRl.setVisibility(View.VISIBLE);
                    paymentItem.mReceivePaymentAmount.setText(String.valueOf(amountNew));
                    paymentItem.mReceivePaymentTime.setText(ConvertDate(chatPojoArrayList.get(position).getTimestamp()));
                }

                paymentItem.mPaymentLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent mTransHistory = new Intent(mContext, TransHistoryActivity.class);
                        mContext.startActivity(mTransHistory);
                    }
                });

                break;
        }
    }

    public void getSearchValues(String search) {
        mSearch = search;
    }

    @Override
    public int getItemCount() {
        return chatPojoArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {

        int viewtype = Integer.parseInt(chatPojoArrayList.get(position).getMsgType());
        return viewtype;
    }

    public class ChatItem extends RecyclerView.ViewHolder {
        TextView txtOutMessage, txtInMessage, mOutMessageTime, mInMessageTime;
        RelativeLayout mOutMessageRl;
        LinearLayout mInMessageRl;

        public ChatItem(View itemView) {
            super(itemView);
            mInMessageTime = itemView.findViewById(R.id.mInMessageTime);
            txtOutMessage = itemView.findViewById(R.id.txtOutMessage);
            txtInMessage = itemView.findViewById(R.id.txtInMessage);
            mOutMessageTime = itemView.findViewById(R.id.mOutMessageTime);
            mOutMessageRl = itemView.findViewById(R.id.mOutMessageRl);
            mInMessageRl = itemView.findViewById(R.id.mInMessageRl);
        }
    }

    public class ImageItem extends RecyclerView.ViewHolder {

        ImageView mSendImage, mReceiveImage;
        TextView mSendImageTime, mReceiveImageTime;
        LinearLayout mImageLl;
        RelativeLayout mSendImageRl, mReceiveImageRl;
        ProgressBar mProgressSendImage, mProgressReceiveImage;

        public ImageItem(View itemView) {
            super(itemView);

            mSendImage = itemView.findViewById(R.id.mSendImage);
            mReceiveImage = itemView.findViewById(R.id.mReceiveImage);
            mSendImageTime = itemView.findViewById(R.id.mSendImageTime);
            mReceiveImageTime = itemView.findViewById(R.id.mReceiveImageTime);
            mImageLl = itemView.findViewById(R.id.mImageLl);

            mSendImageRl = itemView.findViewById(R.id.mSendImageRl);
            mReceiveImageRl = itemView.findViewById(R.id.mReceiveImageRl);

            mProgressSendImage = itemView.findViewById(R.id.mProgressSendImage);
            mProgressReceiveImage = itemView.findViewById(R.id.mProgressReceiveImage);

        }
    }

    public class VideoItem extends RecyclerView.ViewHolder {

        CardView mCardSendVideo, mCardReceiveVideo;
        ImageView mSendVideo, mReceiveVideo;
        TextView mSendVideoTime, mReceiveVideoTime;
        LinearLayout mVideoLl;
        RelativeLayout mSendVideoRl, mReceiveVideoRl;
        ProgressBar mProgressSendVideo, mProgressReceiveVideo;

        public VideoItem(View itemView) {
            super(itemView);

            mCardSendVideo = itemView.findViewById(R.id.mCardSendVideo);
            mCardReceiveVideo = itemView.findViewById(R.id.mCardReceiveVideo);
            mSendVideo = itemView.findViewById(R.id.mSendVideo);
            mReceiveVideo = itemView.findViewById(R.id.mReceiveVideo);
            mSendVideoTime = itemView.findViewById(R.id.mSendVideoTime);
            mReceiveVideoTime = itemView.findViewById(R.id.mReceiveVideoTime);
            mVideoLl = itemView.findViewById(R.id.mVideoLl);

            mSendVideoRl = itemView.findViewById(R.id.mSendVideoRl);
            mReceiveVideoRl = itemView.findViewById(R.id.mReceiveVideoRl);

            mProgressSendVideo = itemView.findViewById(R.id.mProgressSendVideo);
            mProgressReceiveVideo = itemView.findViewById(R.id.mProgressReceiveVideo);
        }
    }

    public class DocumentItem extends RecyclerView.ViewHolder {

        RelativeLayout mDocumentLl;
        CardView mSendDocumentCl, mReceiveDocumentCl;
        TextView mSendDocumentTime, mReceiveDocumentTime;

        public DocumentItem(View itemView) {
            super(itemView);

            mDocumentLl = itemView.findViewById(R.id.mDocumentLl);
            mSendDocumentCl = itemView.findViewById(R.id.mSendDocumentCl);
            mReceiveDocumentCl = itemView.findViewById(R.id.mReceiveDocumentCl);
            mSendDocumentTime = itemView.findViewById(R.id.mSendDocumentTime);
            mReceiveDocumentTime = itemView.findViewById(R.id.mReceiveDocumentTime);

        }
    }

    public class PaymentItem extends RecyclerView.ViewHolder {

        LinearLayout mPaymentLl;
        RelativeLayout mSendPaymentRl, mReceivePaymentRl;
        TextView mSendPaymentAmount, mReceivePaymentAmount;
        TextView mSendPaymentTime, mReceivePaymentTime;

        public PaymentItem(View itemView) {
            super(itemView);

            mPaymentLl = itemView.findViewById(R.id.mPaymentLl);
            mSendPaymentRl = itemView.findViewById(R.id.mSendPaymentRl);
            mReceivePaymentRl = itemView.findViewById(R.id.mReceivePaymentRl);
            mSendPaymentAmount = itemView.findViewById(R.id.mSendPaymentAmount);
            mReceivePaymentAmount = itemView.findViewById(R.id.mReceivePaymentAmount);
            mSendPaymentTime = itemView.findViewById(R.id.mSendPaymentTime);
            mReceivePaymentTime = itemView.findViewById(R.id.mReceivePaymentTime);
        }
    }

    private String ConvertDate(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        String newFormat = formatter.format(testDate);

        return newFormat;
    }

    public void updateChatList(List<ChatPojo> newlist) {
        this.chatPojoArrayList = newlist;
        notifyDataSetChanged();
    }

}
