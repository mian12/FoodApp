package com.solution.alnahar.eatit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.Model.Request;
import com.solution.alnahar.eatit.R;
import com.solution.alnahar.eatit.orderStatus.OrderStatusActivity;

public class ListenOrderService extends Service implements ChildEventListener{


FirebaseDatabase database;
DatabaseReference requests_db_ref;


    public ListenOrderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    //ctrl+o
    @Override
    public void onCreate() {
        super.onCreate();
        database=FirebaseDatabase.getInstance();
       requests_db_ref= database.getReference("Requests");
    }

    //ctrl+o


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // now implement child event  listener to  listen all the changes in the request table
        requests_db_ref.addChildEventListener(this);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {


        // trigger here
        Request request=dataSnapshot.getValue(Request.class);
        showNotification(dataSnapshot.getKey(),request);
    }

    private void showNotification(String key, Request request) {
        Intent intent=new Intent(getBaseContext(), OrderStatusActivity.class);
        intent.putExtra("userPhone",request.getPhone());

        PendingIntent pendingIntent=PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder  builder=new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Al Nahar Solution")
                .setContentInfo("Your  order updated")
                .setContentText("Order #"+key+"was update status to "+ Common.convertCodeToStatus(request.getStatus()))
                .setContentIntent(pendingIntent)
                .setContentInfo("Info")
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp);

        NotificationManager notificationManager= (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
