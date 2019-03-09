package com.chat.zipchat.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chat.zipchat.Adapter.ChatListAdapter;
import com.chat.zipchat.Common.App;
import com.chat.zipchat.Model.ChatList.ChatListPojo;
import com.chat.zipchat.Model.Contact.ContactItemRequest;
import com.chat.zipchat.Model.Contact.ContactRequest;
import com.chat.zipchat.Model.Contact.ContactResponse;
import com.chat.zipchat.Model.Contact.ResultItem;
import com.chat.zipchat.Model.Contact.ResultItemDao;
import com.chat.zipchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.chat.zipchat.Common.BaseClass.RequestPermissionCode;
import static com.chat.zipchat.Common.BaseClass.UserId;
import static com.chat.zipchat.Common.BaseClass.apiInterface;
import static com.chat.zipchat.Common.BaseClass.isOnline;
import static com.chat.zipchat.Common.BaseClass.myLog;
import static com.chat.zipchat.Common.BaseClass.myToast;


@SuppressLint("ValidFragment")
public class ChatFragment extends Fragment {

    Context mContext;
    RecyclerView mRcyclerChat;
    DatabaseReference referenceContact, referenceMessage, referenceContactDetails;

    List<ChatListPojo> chatPojoList;
    ChatListAdapter chatListAdapter;
    TextView mTxtNoChat;

    ArrayList<ContactItemRequest> mListContact = new ArrayList<>();

    public ChatFragment(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mTxtNoChat = view.findViewById(R.id.mTxtNoChat);


        mRcyclerChat = view.findViewById(R.id.mRcyclerChat);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRcyclerChat.setLayoutManager(mLayoutManager);
        mRcyclerChat.setItemAnimator(new DefaultItemAnimator());
        mRcyclerChat.setHasFixedSize(true);

        chatPojoList = App.getmInstance().chatListPojoDao.queryBuilder().list();

        Collections.sort(chatPojoList, new Comparator<ChatListPojo>() {
            @Override
            public int compare(ChatListPojo o1, ChatListPojo o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });

        chatListAdapter = new ChatListAdapter(mContext, chatPojoList);
        mRcyclerChat.setAdapter(chatListAdapter);

        return view;
    }

    ValueEventListener valueEventListenerContact = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {
                mRcyclerChat.setVisibility(View.VISIBLE);
                mTxtNoChat.setVisibility(View.GONE);

                for (final DataSnapshot ds : dataSnapshot.getChildren()) {


//                    Hide by Arun on 07-01-2019
//                    chatPojoList.clear();

                    List<ResultItem> resultItems = App.getmInstance().resultItemDao.queryBuilder().where(
                            ResultItemDao.Properties.Id.eq(ds.getKey())).list();

                    if (resultItems.size() == 0) {
                        referenceContactDetails = FirebaseDatabase.getInstance().getReference("user-details").child(ds.getKey()).child("profile-details");
                        referenceContactDetails.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {

                                    ResultItem resultItem = new ResultItem();
                                    resultItem.setId(ds.getKey());
                                    resultItem.setIsFromContact("0");

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                        if (ds.getKey().equals("name")) {
                                            resultItem.setName(ds.getValue().toString());
                                        } else if (ds.getKey().equals("mobile-number")) {
                                            resultItem.setMobile_number(ds.getValue().toString());
                                        } else if (ds.getKey().equals("profile-url")) {
                                            resultItem.setProfile_picture(ds.getValue().toString());
                                        } else if (ds.getKey().equals("status")) {
                                            resultItem.setStatus(ds.getValue().toString());
                                        }
                                    }

                                    List<ContactResponse> contactResponseList = App.getmInstance().contactResponseDao.queryBuilder().list();

                                    if (contactResponseList.size() < 0) {
                                        resultItem.setContact_id(contactResponseList.get(0).contact_id);
                                    }
                                    App.getmInstance().resultItemDao.insertOrReplace(resultItem);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                    DatabaseReference ref = referenceContact.child(ds.getKey());
                    ref.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {

                                referenceMessage = FirebaseDatabase.getInstance().getReference("messages").child(child.getKey());
                                referenceMessage.addValueEventListener(valueEventListenerMessage);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Handle possible errors.
                        }
                    });
                }
            } else {
                App.getmInstance().chatListPojoDao.deleteAll();
                App.getmInstance().chatPojoDao.deleteAll();
                mTxtNoChat.setVisibility(View.VISIBLE);
                mRcyclerChat.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }
    };

    ValueEventListener valueEventListenerMessage = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.exists()) {

                ChatListPojo chatListPojo = new ChatListPojo();
                String text = "";
                Integer msgType = 0;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if (ds.getKey().equals("text")) {
                        text = ds.getValue().toString();
                    } else if (ds.getKey().equals("msgType")) {
                        msgType = Integer.parseInt(ds.getValue().toString());
                    } else if (ds.getKey().equals("toId") || ds.getKey().equals("fromId")) {

                        if (ds.getKey().equals("fromId") && !ds.getValue().equals(UserId(mContext))) {
                            chatListPojo.setToId(ds.getValue().toString());
                        } else if (ds.getKey().equals("toId") && !ds.getValue().equals(UserId(mContext))) {
                            chatListPojo.setToId(ds.getValue().toString());
                        }

                    } else if (ds.getKey().equals("timestamp")) {
                        chatListPojo.setTimestamp(ds.getValue().toString());
                    }
                }

                if (msgType == 1) {
                    chatListPojo.setText(text);
                } else if (msgType == 2) {
                    chatListPojo.setText("Photo");
                } else if (msgType == 3) {
                    chatListPojo.setText("Audio");
                } else if (msgType == 4) {
                    chatListPojo.setText("Video");
                } else if (msgType == 5) {
                    chatListPojo.setText("Document");
                } else if (msgType == 6) {
                    chatListPojo.setText("Payment");
                }

                App.getmInstance().chatListPojoDao.insertOrReplace(chatListPojo);

                chatPojoList = App.getmInstance().chatListPojoDao.queryBuilder().list();


//                    Hide by Arun on 07-01-2019
              /*  chatListAdapter = new ChatListAdapter(mContext, chatPojoList);
                mRcyclerChat.setAdapter(chatListAdapter);*/
//                chatListAdapter.notifyDataSetChanged();

                Collections.sort(chatPojoList, new Comparator<ChatListPojo>() {
                    @Override
                    public int compare(ChatListPojo o1, ChatListPojo o2) {
                        return o2.getTimestamp().compareTo(o1.getTimestamp());
                    }
                });

                chatListAdapter.updateFragChatList(chatPojoList);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            myLog("onCancelled: ", databaseError.getMessage());
        }

    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            if (isOnline(mContext) && null != getActivity()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    EnableRuntimePermission();
                } else {
                    GetContactsIntoArrayList();
                }

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        referenceContact = FirebaseDatabase.getInstance().getReference("user-messages").child(UserId(mContext));
        referenceContact.addValueEventListener(valueEventListenerContact);
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
            GetContactsIntoArrayList();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, RequestPermissionCode);
            GetContactsIntoArrayList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    GetContactsIntoArrayList();
                } else {
                    myToast(mContext, getResources().getString(R.string.permission_canceled));
                }
                break;
        }
    }

    public void GetContactsIntoArrayList() {

        myLog("GetContactsIntoArrayList", "");

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                //plus any other properties you wish to query
        };

        Cursor cursor = null;
        try {
            cursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        } catch (SecurityException e) {
            //SecurityException can be thrown if we don't have the right permissions
        }


        if (cursor != null) {
            try {
                HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                int indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                int indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                mListContact.clear();
                while (cursor.moveToNext()) {

                    String normalizedNumber = cursor.getString(indexOfNormalizedNumber);

                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {

                        String displayName = cursor.getString(indexOfDisplayName);
                        String displayNumber = cursor.getString(indexOfDisplayNumber);
                        String newDisplayNumber = displayNumber.replaceAll("[^0-9]", "");

                        myLog("display Name & Number : ", displayName + "\t" + displayNumber);
                        myLog("newDisplayNumber: ", newDisplayNumber);

                        ContactItemRequest contactItem = new ContactItemRequest();
                        contactItem.setName(displayName);
                        contactItem.setMobileNumber(newDisplayNumber);
                        mListContact.add(contactItem);

                        //haven't seen this number yet: do something with this contact!
                    } else {
                        //don't do anything with this contact because we've already found this number
                    }
                }
                SyncContact();

            } finally {
                cursor.close();
            }
        }
    }

    private void SyncContact() {

        if (isOnline(mContext)) {

            ContactRequest contactRequest = new ContactRequest();
            contactRequest.setContact(mListContact);
            contactRequest.setDeviceType("ANDROID");

            Call<ContactResponse> contactResponseCall = apiInterface.contactDetails(contactRequest);
            contactResponseCall.enqueue(new Callback<ContactResponse>() {
                @Override
                public void onResponse(Call<ContactResponse> call, final Response<ContactResponse> response) {

                    if (response.isSuccessful()) {

                        App.getmInstance().contactResponseDao.deleteAll();
                        App.getmInstance().resultItemDao.deleteAll();

                        if (response.body() != null) {
                            ContactResponse contactResponse = response.body();
                            contactResponse.contact_id = 1L;
                            App.getmInstance().contactResponseDao.insertOrReplace(contactResponse);

                            if (response.body().getResult().size() > 0) {

                                for (ResultItem con : contactResponse.getResult()) {
                                    if (!con.getId().equalsIgnoreCase(UserId(mContext))) {
                                        con.setContact_id(contactResponse.contact_id);
                                        con.setIsFromContact("1");
                                        App.getmInstance().resultItemDao.insertOrReplace(con);
                                    }

                                }

                            }

                        }
                    }
                }

                @Override
                public void onFailure(Call<ContactResponse> call, Throwable t) {
                    myLog("onFailure: ", t.toString());
                }
            });

        }

    }

}
