package com.chat.zipchat.Activity;

import android.Manifest;
import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chat.zipchat.Activity.Payments.SendMoneyActivity;
import com.chat.zipchat.Adapter.ChatAdapter;
import com.chat.zipchat.Common.App;
import com.chat.zipchat.Common.BaseClass;
import com.chat.zipchat.Common.ImageFilePath;
import com.chat.zipchat.Model.Chat.ChatPojo;
import com.chat.zipchat.Model.Chat.ChatPojoDao;
import com.chat.zipchat.Model.Contact.ResultItem;
import com.chat.zipchat.Model.Contact.ResultItemDao;
import com.chat.zipchat.Model.LocalDataPojo;
import com.chat.zipchat.Model.LocalDataPojoDao;
import com.chat.zipchat.Model.ProfileImageUpdate.ProfileImageResponse;
import com.chat.zipchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.greendao.query.DeleteQuery;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.Common.BaseClass.CurrentDateTime;
import static com.chat.zipchat.Common.BaseClass.DOCUMENT_PICKER_SELECT;
import static com.chat.zipchat.Common.BaseClass.HideKeyboard;
import static com.chat.zipchat.Common.BaseClass.IMAGE_PICKER_SELECT;
import static com.chat.zipchat.Common.BaseClass.MY_REQUEST_CODE_DOCUMENT;
import static com.chat.zipchat.Common.BaseClass.MY_REQUEST_CODE_IMAGE;
import static com.chat.zipchat.Common.BaseClass.UserId;
import static com.chat.zipchat.Common.BaseClass.apiInterface;
import static com.chat.zipchat.Common.BaseClass.getRealPathFromURI;
import static com.chat.zipchat.Common.BaseClass.isOnline;
import static com.chat.zipchat.Common.BaseClass.myLog;
import static com.chat.zipchat.Common.BaseClass.myToast;
import static com.chat.zipchat.Common.BaseClass.snackbar;
import static okhttp3.MediaType.parse;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbarChat;
    ImageView mImgBackChat, mImgEmoji, mImgSend, mImgAdd;
    CircleImageView mImgContact;
    TextView mTxtContactName, mTxtStatus;
    RecyclerView mRvChat;
    EditText mTxtMessage;
    LinearLayout Ll_text;
    RelativeLayout mRlToolbarChat, mPaymentsRl, mPhotosRl, mDocumentRl;
    List<ChatPojo> chatPojoList;
    DatabaseReference referenceUser, referenceMessage, referenceOnline;
    DatabaseReference referenceMessageInsert, referenceUserInsert;
    String toId;
    BottomSheetBehavior behavior;
    View mViewBg, bottomSheet;
    ChatAdapter chatAdapter;

    int value = -1;
    SearchView mSearchView;

    public static ArrayList<Integer> mListSearch;
    List<ResultItem> resultItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mToolbarChat = findViewById(R.id.mToolbarChat);
        setSupportActionBar(mToolbarChat);

        mTxtContactName = findViewById(R.id.mTxtContactName);
        mTxtStatus = findViewById(R.id.mTxtStatus);
        mImgBackChat = findViewById(R.id.mImgBackChat);
        mImgEmoji = findViewById(R.id.mImgEmoji);
        mImgSend = findViewById(R.id.mImgSend);
        mImgContact = findViewById(R.id.mImgContact);
        mRvChat = findViewById(R.id.mRvChat);
        mTxtMessage = findViewById(R.id.mTxtMessage);
        Ll_text = findViewById(R.id.Ll_text);
        mRlToolbarChat = findViewById(R.id.mRlToolbarChat);
        mImgAdd = findViewById(R.id.mImgAdd);

        mViewBg = findViewById(R.id.mViewBg);
        mPaymentsRl = findViewById(R.id.mPaymentsRl);
        mPhotosRl = findViewById(R.id.mPhotosRl);
        mDocumentRl = findViewById(R.id.mDocumentRl);

        mImgBackChat.setOnClickListener(this);
        mImgEmoji.setOnClickListener(this);
        mImgSend.setOnClickListener(this);
        mRlToolbarChat.setOnClickListener(this);
        mTxtMessage.setOnClickListener(this);
        mImgAdd.setOnClickListener(this);
        mPaymentsRl.setOnClickListener(this);
        mPhotosRl.setOnClickListener(this);
        mDocumentRl.setOnClickListener(this);

        mListSearch = new ArrayList<>();

        toId = getIntent().getStringExtra("toId");

        chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).list();


        Collections.sort(chatPojoList, new Comparator<ChatPojo>() {
            @Override
            public int compare(ChatPojo o1, ChatPojo o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }

        });

        resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                ResultItemDao.Properties.Id.eq(toId)).list();

        if (resultItems.size() > 0) {

            if (resultItems.get(0).getIsFromContact().equalsIgnoreCase("1")) {
                mTxtContactName.setText(resultItems.get(0).getName());
            } else {
                mTxtContactName.setText(resultItems.get(0).getMobile_number());
            }
            Glide.with(this).load(resultItems.get(0).getProfile_picture()).error(R.drawable.defult_user).into(mImgContact);
        } else {
            mTxtContactName.setText("Unknown");
        }


        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setStackFromEnd(true);
        mRvChat.setLayoutManager(mLayoutManager);
        mRvChat.setItemAnimator(new DefaultItemAnimator());
        mRvChat.setHasFixedSize(true);

        chatAdapter = new ChatAdapter(ChatActivity.this, chatPojoList);
        mRvChat.setAdapter(chatAdapter);

        referenceUser = FirebaseDatabase.getInstance().getReference("user-messages").child(UserId(this));
        referenceUser.addValueEventListener(valueEventListenerUserChat);

        referenceOnline = FirebaseDatabase.getInstance().getReference("user-details").child(toId).child("profile-details");
        referenceOnline.addValueEventListener(valueEventListenerOnline);

        mTxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mTxtMessage.getText().toString().length() == 0) {
                    mImgSend.setVisibility(View.GONE);
                } else {
                    mImgSend.setVisibility(View.VISIBLE);
                }
            }
        });

        bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    mViewBg.setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                mViewBg.setVisibility(View.VISIBLE);
                mViewBg.setAlpha(slideOffset);
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_chat, menu);

        ViewGroup.LayoutParams navButtonsParams = new ViewGroup.LayoutParams(mToolbarChat.getHeight() * 2 / 3, mToolbarChat.getHeight() * 2 / 3);
        Button btnNext = new Button(this);
        Button btnPrev = new Button(this);
        btnNext.setBackground(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down));
        btnPrev.setBackground(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white));

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        ((LinearLayout) mSearchView.getChildAt(0)).addView(btnPrev, navButtonsParams);
        ((LinearLayout) mSearchView.getChildAt(0)).addView(btnNext, navButtonsParams);
        ((LinearLayout) mSearchView.getChildAt(0)).setGravity(Gravity.BOTTOM);

        ImageView searchViewIcon = (ImageView) mSearchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        ViewGroup linearLayoutSearchView = (ViewGroup) searchViewIcon.getParent();
        linearLayoutSearchView.removeView(searchViewIcon);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        mSearchView.setIconifiedByDefault(false);
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                mListSearch.clear();
                if (chatPojoList.size() > 0) {
                    chatAdapter.getSearchValues(newText);
                    for (int i = 0; i < chatPojoList.size(); i++) {

                        if (!TextUtils.isEmpty(newText)) {
                            if (chatPojoList.get(i).getText().contains(newText)) {
                                myLog("Value", String.valueOf(i));
                                mListSearch.add(i);
                            }
                        } else {
                            mListSearch.clear();
                        }
                    }

                    mRvChat.setAdapter(chatAdapter);
                }
                return true;
            }

            public boolean onQueryTextSubmit(String query) {

                myLog("mListSearch", String.valueOf(mListSearch));
                return true;
            }

        };

        mSearchView.setOnQueryTextListener(queryTextListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collections.sort(mListSearch, Collections.reverseOrder());

                for (int i = 0; i < mListSearch.size(); i++) {

                    if (value == -1) {
                        value = mListSearch.get(mListSearch.size() - 1);
                        mRvChat.scrollToPosition(value);
                        break;
                    } else if (mListSearch.get(i) > value) {
                        value = mListSearch.get(i);
                        mRvChat.scrollToPosition(mListSearch.get(i));
                        break;
                    } else if (mListSearch.get(i) == mListSearch.get(mListSearch.size() - 1)) {
                        myToast(ChatActivity.this, getResources().getString(R.string.not_found));
                        break;
                    }
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Collections.sort(mListSearch);

                for (int i = 0; i < mListSearch.size(); i++) {

                    if (value == -1) {
                        value = mListSearch.get(mListSearch.size() - 1);
                        mRvChat.scrollToPosition(value);
                        break;
                    } else if (mListSearch.get(i) < value) {
                        value = mListSearch.get(i);
                        mRvChat.scrollToPosition(mListSearch.get(i));
                        break;
                    } else if (mListSearch.get(i) == mListSearch.get(0)) {
                        myToast(ChatActivity.this, getResources().getString(R.string.not_found));
                        break;
                    }
                }
            }
        });


        // Detect SearchView icon clicks
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLog("Enabled", String.valueOf(mSearchView.isEnabled()));
//                Ll_text.setVisibility(View.GONE);
            }
        });

        // Detect SearchView close
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
//                value = -1;
                myLog("Disabled", String.valueOf(mSearchView.isEnabled()));
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_media:
                Intent mediaIntent = new Intent(this, MediaActivity.class);
                mediaIntent.putExtra("toId", toId);
                mediaIntent.putExtra("Name", mTxtContactName.getText().toString());
                startActivity(mediaIntent);
                break;
            case R.id.menu_search:
                break;
            case R.id.menu_clrchat:
                clearChat();
                break;
            case R.id.menu_block:
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mImgBackChat:
                onBackPressed();
                break;
            case R.id.mImgEmoji:
                close_dialog();
                break;
            case R.id.mTxtMessage:
                close_dialog();
                break;
            case R.id.mPaymentsRl:
                close_dialog();

                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("id", toId);
                hashMap.put("number", resultItems.get(0).getMobile_number());
                hashMap.put("type", "chat");

                Intent mSentMoney = new Intent(this, SendMoneyActivity.class);
                mSentMoney.putExtra("userDetails", hashMap);
                startActivity(mSentMoney);
                break;
            case R.id.mPhotosRl:
                imagePicker();
                break;
            case R.id.mDocumentRl:
                documentPicker();
                break;
            case R.id.mImgAdd:
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            case R.id.mImgSend:
                if (TextUtils.isEmpty(mTxtMessage.getText().toString())) {
                    myToast(this, getResources().getString(R.string.enter_message));
                } else {
                    sendMsg();
                }
                break;
            case R.id.mRlToolbarChat:
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("toId", toId);
                startActivity(intent);
        }
    }

    ValueEventListener valueEventListenerUserChat = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.getKey().equals(toId)) {


                        chatPojoList.clear();

                        for (DataSnapshot dss : ds.getChildren()) {
                            referenceMessage = FirebaseDatabase.getInstance().getReference("messages").child(dss.getKey());
                            referenceMessage.addValueEventListener(valueEventListenerMessageChat);
                        }

                        if (chatPojoList.size() < ds.getChildrenCount()) {

                           /* final DeleteQuery<ChatPojo> tableDeleteQuery = App.getmInstance().chatPojoDao.queryBuilder()
                                    .where(ChatPojoDao.Properties.FriendId.eq(toId))
                                    .buildDelete();
                            tableDeleteQuery.executeDeleteWithoutDetachingEntities();*/

                        }
                    }
                }
            } else {
                final DeleteQuery<ChatPojo> tableDeleteQuery = App.getmInstance().chatPojoDao.queryBuilder()
                        .where(ChatPojoDao.Properties.FriendId.eq(toId))
                        .buildDelete();
                tableDeleteQuery.executeDeleteWithoutDetachingEntities();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }
    };

    ValueEventListener valueEventListenerMessageChat = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                ChatPojo chatPojo = new ChatPojo();
                chatPojo.setMessageId(dataSnapshot.getKey().toString());
                chatPojo.setFriendId(toId);
                chatPojo.setIsMessageSend(1);

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.getKey().equals("fromId")) {
                        chatPojo.setFromId(ds.getValue().toString());
                    } else if (ds.getKey().equals("text")) {
                        chatPojo.setText(ds.getValue().toString());
                    } else if (ds.getKey().equals("timestamp")) {
                        chatPojo.setTimestamp(ds.getValue().toString());
                    } else if (ds.getKey().equals("toId")) {
                        chatPojo.setToId(ds.getValue().toString());
                    } else if (ds.getKey().equals("msgType")) {
                        chatPojo.setMsgType(ds.getValue().toString());
                    } else if (ds.getKey().equals("fileUrl")) {
                        chatPojo.setFileUrl(ds.getValue().toString());
                    }
                }

                App.getmInstance().chatPojoDao.insertOrReplace(chatPojo);


                chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).list();

                Collections.sort(chatPojoList, new Comparator<ChatPojo>() {
                    @Override
                    public int compare(ChatPojo o1, ChatPojo o2) {
                        return o1.getTimestamp().compareTo(o2.getTimestamp());
                    }

                });

                if (chatPojoList.size() > 0) {

                    chatAdapter = new ChatAdapter(ChatActivity.this, chatPojoList);
                    mRvChat.setAdapter(chatAdapter);


//                    Hide by Arun on 09-01-2019
                    /*chatAdapter.updateChatList(chatPojoList);
                    mRvChat.scrollToPosition(chatPojoList.size() - 1);*/

                }

                if (Integer.parseInt(chatPojo.getMsgType()) != 1) {

                    List<LocalDataPojo> localDataPojos = App.getmInstance().localDataPojoDao.queryBuilder().where(LocalDataPojoDao.Properties.MessageId.eq(chatPojo.getMessageId())).list();

                    if (localDataPojos.size() == 0) {
                        download(chatPojo.getFileUrl(), Integer.parseInt(chatPojo.getMsgType()), chatPojo.getMessageId());
                    }
                }

            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }

    };

    ValueEventListener valueEventListenerOnline = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                String isOnline = "0";
                String offline_time = "";

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.getKey().equals("isOnline")) {
                        isOnline = ds.getValue().toString();
                    } else if (ds.getKey().equals("offline-time")) {
                        offline_time = ds.getValue().toString();
                    }
                }

                if (isOnline.equalsIgnoreCase("1")) {
                    mTxtStatus.setText("Online");
                } else {
                    mTxtStatus.setText(returnDate(offline_time));
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }
    };

    private void sendMsg() {

        HideKeyboard(this);

        ChatPojo chatPojo = new ChatPojo();
        chatPojo.setFromId(UserId(this));
        chatPojo.setText(mTxtMessage.getText().toString());
        chatPojo.setTimestamp(ConvertedDateTime());
        chatPojo.setToId(toId);
        chatPojo.setMsgType("1");
        chatPojo.setIsRead("0");
        chatPojo.setFileUrl("null");

        mTxtMessage.setText("");

        referenceMessageInsert = FirebaseDatabase.getInstance().getReference("messages");

        if (isOnline(this)) {
            String mGroupId = referenceMessageInsert.push().getKey();
            referenceMessageInsert.child(mGroupId).setValue(chatPojo);

            referenceUser.child(toId).child(mGroupId).setValue("1");
            referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(toId).child(UserId(this));
            referenceUserInsert.child(mGroupId).setValue("1");
        } else {
            String mGroupId = referenceMessageInsert.push().getKey();
            chatPojo.setFriendId(toId);
            chatPojo.setMessageId(mGroupId);
            chatPojo.setIsMessageSend(0);
            App.getmInstance().chatPojoDao.insertOrReplace(chatPojo);
            chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).list();

            Collections.sort(chatPojoList, new Comparator<ChatPojo>() {
                @Override
                public int compare(ChatPojo o1, ChatPojo o2) {
                    return o1.getTimestamp().compareTo(o2.getTimestamp());
                }

            });

            if (chatPojoList.size() > 0 && chatAdapter != null) {

                chatAdapter = new ChatAdapter(ChatActivity.this, chatPojoList);
                mRvChat.setAdapter(chatAdapter);


//                    Hide by Arun on 07-01-2019
               /* chatAdapter.updateChatList(chatPojoList);
                mRvChat.scrollToPosition(chatPojoList.size() - 1);*/

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void clearChat() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to clear?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {

                        chatPojoList.clear();
                        chatAdapter.updateChatList(chatPojoList);
                        App.getmInstance().chatPojoDao.deleteAll();
                        referenceUser.child(toId).removeValue();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void imagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_IMAGE);
            } else {
                getImageFromGallery();
            }
        } else {
            getImageFromGallery();
        }
    }

    private void getImageFromGallery() {
        close_dialog();
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/* video/*");
        startActivityForResult(pickIntent, IMAGE_PICKER_SELECT);
    }

    private void documentPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_DOCUMENT);
            } else {
                getDocument();
            }
        } else {
            getDocument();
        }
    }

    private void getDocument() {
        close_dialog();
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, DOCUMENT_PICKER_SELECT);
    }

    private void close_dialog() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Uri resultUri = data.getData();

            if (requestCode == IMAGE_PICKER_SELECT) {
                try {

                    if (resultUri.toString().contains("image")) {
                        String filePath = getRealPathFromURI(this, resultUri);
                        UpdateImageDocument(filePath, "2");
                    } else if (resultUri.toString().contains("video")) {
                        String filePath = getRealPathFromURI(this, resultUri);
                        UpdateImageDocument(filePath, "4");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == DOCUMENT_PICKER_SELECT) {
                try {

                    if (resultUri.toString().contains("image")) {
                        String filePath = getRealPathFromURI(this, resultUri);
                        UpdateImageDocument(filePath, "2");
                    } else if (resultUri.toString().contains("video")) {
                        String filePath = getRealPathFromURI(this, resultUri);
                        UpdateImageDocument(filePath, "4");
                    } else {
                        String filePath = ImageFilePath.getPath(this, resultUri);
                        UpdateImageDocument(filePath, "5");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case MY_REQUEST_CODE_IMAGE:
                getImageFromGallery();
                break;

            case MY_REQUEST_CODE_DOCUMENT:
                getDocument();
                break;
        }
    }

    private void UpdateImageDocument(String filePath, String type) {

        myLog("UpdateImageDocument", filePath);
        myLog("type", type);

        ChatPojo chatPojo = new ChatPojo();
        chatPojo.setFromId(UserId(this));
        chatPojo.setText("");
        chatPojo.setTimestamp(ConvertedDateTime());
        chatPojo.setToId(toId);
        chatPojo.setMsgType(type);
        chatPojo.setIsRead("0");
        chatPojo.setFileUrl("null");

        App.getmInstance().chatPojoDao.insertOrReplace(chatPojo);
        chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.FriendId.eq(toId)).list();

        Collections.sort(chatPojoList, new Comparator<ChatPojo>() {
            @Override
            public int compare(ChatPojo o1, ChatPojo o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }

        });

        if (chatPojoList.size() > 0 && chatAdapter != null) {

            chatAdapter = new ChatAdapter(ChatActivity.this, chatPojoList);
            mRvChat.setAdapter(chatAdapter);


//                    Hide by Arun on 07-01-2019
            /*chatAdapter.updateChatList(chatPojoList);
            mRvChat.scrollToPosition(chatPojoList.size() - 1);*/

        }


        if (isOnline(this)) {
            File file = new File(filePath);
            RequestBody requestBody = RequestBody.create(parse("*/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("media", file.getName(), requestBody);

            final Call<ProfileImageResponse> registerResponseCall = apiInterface.updateProfileImage(fileToUpload);
            registerResponseCall.enqueue(new Callback<ProfileImageResponse>() {
                @Override
                public void onResponse(Call<ProfileImageResponse> call, Response<ProfileImageResponse> response) {

                    if (response.isSuccessful()) {
                        String responce = response.body().getResult().getUrl();
                        String response_type = response.body().getResult().getType();


                        HashMap<String, String> map = new HashMap<>();
                        map.put("fromId", UserId(ChatActivity.this));
                        map.put("text", "");
                        map.put("timestamp", ConvertedDateTime());
                        map.put("toId", toId);
                        map.put("msgType", response_type);
                        map.put("isRead", "0");
                        map.put("fileUrl", responce);

                        mTxtMessage.setText("");


                        referenceMessageInsert = FirebaseDatabase.getInstance().getReference("messages");

                        if (isOnline(ChatActivity.this)) {
                            String mGroupId = referenceMessageInsert.push().getKey();
                            referenceMessageInsert.child(mGroupId).setValue(map);

                            referenceUser.child(toId).child(mGroupId).setValue("1");
                            referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(toId).child(UserId(ChatActivity.this));
                            referenceUserInsert.child(mGroupId).setValue("1");
                        }


                    } else if (response.code() == 104) {
                    }
                }

                @Override
                public void onFailure(Call<ProfileImageResponse> call, Throwable t) {
                    myLog("OnFailure", t.toString());
                }
            });
        } else {
            snackbar(this, mRvChat, BaseClass.NO_INTERNET);
        }
    }

    private String returnDate(String Date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        Date testDate = null;
        try {
            testDate = sdf.parse(Date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yy");
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        String strCurrentTime = formatDate.format(CurrentDateTime());
        String strCurrentDate = formatDate.format(testDate);

        String date = "";
        String time = "";

        if (strCurrentTime.equalsIgnoreCase(strCurrentDate)) {
            date = "today";
            time = formatter.format(testDate);
        } else {
            date = strCurrentDate;
            time = formatter.format(testDate);
        }


        String ruternDate = "last seen " + date + " at " + time;

        return ruternDate;
    }

    private void download(String download_url, int i, String messageId) {

        LocalDataPojo localDataPojo = new LocalDataPojo();
        localDataPojo.setMessageId(messageId);
        localDataPojo.setUserId(toId);


        if (!TextUtils.isEmpty(download_url) && !download_url.equalsIgnoreCase("null")) {
            String mPath = "";
            String mTextName = "";

            if (i == 2) {
                mTextName = messageId;
                mPath = "WhatsApp Clone/Photos";
            } else if (i == 4) {
                mTextName = messageId;
                mPath = "WhatsApp Clone/Videos";
            } else if (i == 5) {
                mTextName = messageId;
                mPath = "WhatsApp Clone/Documents";
            }

            try {

                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(download_url));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                request.setDestinationInExternalPublicDir(mPath, mTextName);
                manager.enqueue(request);
                localDataPojo.setIsDownloaded(1);
                localDataPojo.setStoragePath(Environment.getExternalStorageDirectory() + "/" + mPath + "/" + mTextName);

            } catch (Exception e) {

                localDataPojo.setIsDownloaded(1);
                localDataPojo.setStoragePath(Environment.getExternalStorageDirectory() + "/" + mPath + "/" + mTextName);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(download_url)));
            } finally {
                App.getmInstance().localDataPojoDao.insertOrReplace(localDataPojo);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
            this.overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out);
        }
    }

}
