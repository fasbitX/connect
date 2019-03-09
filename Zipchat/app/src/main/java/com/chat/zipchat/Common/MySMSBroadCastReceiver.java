package com.chat.zipchat.Common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chat.zipchat.Model.Chat.ChatPojo;
import com.chat.zipchat.Model.Chat.ChatPojoDao;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.chat.zipchat.Common.BaseClass.ConvertedDateTime;
import static com.chat.zipchat.Common.BaseClass.UserId;
import static com.chat.zipchat.Common.BaseClass.isOnline;
import static com.chat.zipchat.Common.BaseClass.myLog;

public class MySMSBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        DatabaseReference userLastOnlineRef = null;

        if (UserId(context) != null) {
            userLastOnlineRef = FirebaseDatabase.getInstance().getReference("user-details").child(UserId(context)).child("profile-details");
        }

        try {
            if (isOnline(context)) {
                myLog("Network", "Online Connect Intenet");

                HashMap<String, Object> map = new HashMap<>();
                map.put("isOnline", "1");
                map.put("offline-time", ConvertedDateTime());

                if (userLastOnlineRef != null) {
                    userLastOnlineRef.updateChildren(map);
                }

                List<ChatPojo> chatPojoList = new ArrayList<>();
                chatPojoList = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.IsMessageSend.eq(0)).list();
                myLog("offlineMessageSize", String.valueOf(chatPojoList.size()));

                for (int i = 0; i < chatPojoList.size(); i++) {

                    ChatPojo chatPojo = new ChatPojo();
                    chatPojo.setFromId(chatPojoList.get(i).getFromId());
                    chatPojo.setText(chatPojoList.get(i).getText());
                    chatPojo.setTimestamp(chatPojoList.get(i).getTimestamp());
                    chatPojo.setToId(chatPojoList.get(i).getToId());
                    chatPojo.setMsgType(chatPojoList.get(i).getMsgType());
                    chatPojo.setIsRead(chatPojoList.get(i).getIsRead());
                    chatPojo.setFileUrl(chatPojoList.get(i).getFileUrl());

                    DatabaseReference referenceMessageInsert = FirebaseDatabase.getInstance().getReference("messages");
                    referenceMessageInsert.child(chatPojoList.get(i).getMessageId()).setValue(chatPojo);

                    DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("user-messages").child(UserId(context));
                    referenceUser.child(chatPojoList.get(i).getToId()).child(chatPojoList.get(i).getMessageId()).setValue("1");

                    DatabaseReference referenceUserInsert = FirebaseDatabase.getInstance().getReference("user-messages").child(chatPojoList.get(i).getToId()).child(UserId(context));
                    referenceUserInsert.child(chatPojoList.get(i).getMessageId()).setValue("1");

                    ChatPojo chatPojos = App.getmInstance().chatPojoDao.queryBuilder().where(ChatPojoDao.Properties.MessageId.eq(chatPojoList.get(i).getMessageId())).list().get(0);
                    chatPojos.setIsMessageSend(1);
                    App.getmInstance().chatPojoDao.update(chatPojos);


                }

            } else {

                myLog("Network", "Conectivity Failure !!!");

                HashMap<String, Object> map = new HashMap<>();
                map.put("isOnline", "0");
                map.put("offline-time", ConvertedDateTime());

                if (userLastOnlineRef != null) {
                    userLastOnlineRef.onDisconnect().updateChildren(map);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
           /* Bundle bundle = intent.getExtras();
            SmsMessage[] smsm = null;
            String sms_str = "";

            if (bundle != null) {
                // Get the SMS message
                Object[] pdus = (Object[]) bundle.get("pdus");
                smsm = new SmsMessage[pdus.length];
                for (int i = 0; i < smsm.length; i++) {
                    smsm[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    sms_str += "\r\nMessage: ";
                    sms_str += smsm[i].getMessageBody().toString();
                    sms_str += "\r\n";
                    String Sender = smsm[i].getOriginatingAddress();
                    //Check here sender is yours
                    //Sender.endsWith("SPRKOT");
                    Intent smsIntent = new Intent("otp");
                    smsIntent.putExtra("message", sms_str);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
                }
            }*/

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

}
