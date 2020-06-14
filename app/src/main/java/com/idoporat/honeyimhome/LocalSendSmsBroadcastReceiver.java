package com.idoporat.honeyimhome;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class LocalSendSmsBroadcastReceiver extends BroadcastReceiver {
    public static final String SMS_PERMISSIONS_MESSAGE = "no SEND_SMS permissions";
    public static final String SMS_C_ID = "sms";
    public static final int SMS_NOTIFICATION_ID = 0;
    public static  String PHONE = "phone";
    public static  String CONTENT = "content";


    @Override
    public void onReceive(Context context, Intent intent) {
        boolean hasPermissions = ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        if(!hasPermissions){
            Log.i(SMS_PERMISSIONS_MESSAGE,"no SEND_SMS permissions");
            return;
        }
        String message = intent.getStringExtra(CONTENT);
        String number = intent.getStringExtra(PHONE);
        SmsManager smgr = SmsManager.getDefault();
        smgr.sendTextMessage(number, null, message, null, null);

        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SMS_C_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("Sending SMS") todo
                .setContentText("sending sms to " + number + ": " + message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(SMS_NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(SMS_C_ID, name, importance);
            channel.setDescription(description);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
