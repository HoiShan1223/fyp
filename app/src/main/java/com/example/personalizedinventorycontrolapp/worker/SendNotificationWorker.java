package com.example.personalizedinventorycontrolapp.worker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.personalizedinventorycontrolapp.dao.GroupDao;
import com.example.personalizedinventorycontrolapp.dao.NotificationDao;
import com.example.personalizedinventorycontrolapp.dao.UserDao;
import com.example.personalizedinventorycontrolapp.entity.Group;
import com.example.personalizedinventorycontrolapp.entity.Notification;
import com.example.personalizedinventorycontrolapp.entity.User;

import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendNotificationWorker extends Worker {
    private static final String TAG = "SendNotificationWorker";
    private static final String LEGACY_SERVER_KEY = "AAAAPDbqf6Y:APA91bEeOynmp-07posWCNHobfsjKg5mYIv28qoWwBpAAYmxKZSYFXTEL6oBSpX5WNOQCw8VTQWNC2jW7za74J45LaGUwH4xfPD6TaR9oOn5zS0J_KW-8P_E1t93FAUJTSkH7Gz-UYO6";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    List<User> userList = new ArrayList<>();
    public SendNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    @NonNull
    @Override
    public Result doWork() {
        //check notification day and time
        LocalDate currentDate = LocalDate.now();
        Log.d(TAG, "Date:"+ currentDate);
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        Log.d(TAG, "time:"+ currentTime);
        DayOfWeek dayOfWeek = DayOfWeek.from(currentDate);
        int value = dayOfWeek.getValue();
        Log.d(TAG, "DateOfWeek:"+ value);
        UserDao userDao = new UserDao();
        userList = userDao.list();
        Log.d(TAG, "userlist: "+ userList.size());
        userList.forEach(user ->{
            int userid = user.getId();
            Log.d(TAG, "userid: "+ userid);
            NotificationDao notificationDao = new NotificationDao();
            Notification notification = notificationDao.findNotification(userid);
            if(notification != null && notification.isNotification_condition()) {
                String notificationDay = notification.getNotification_day();
                Log.d(TAG, "NotificationDay: "+ notificationDay);
                String notificationTime = notification.getNotification_time();
                DateTimeFormatter formatter1 = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("HH:mm:ss")
                        .toFormatter();
                String notification_Time = LocalTime.parse(notificationTime, formatter1).format(DateTimeFormatter.ofPattern("HH:mm"));
                Log.d(TAG, "NotificationTime: "+ notificationTime);
                Log.d(TAG, "NotificationTime: "+ notification_Time);
                int dayOfWeekValue;
                if(!notificationDay.equals("") && !notification_Time.equals("")){
                    dayOfWeekValue = DayOfWeek.valueOf(notificationDay.toUpperCase()).getValue();
                    Log.d(TAG, "DateOfWeek: "+ dayOfWeekValue);
                    if(dayOfWeekValue != 0 && dayOfWeekValue == value && currentTime.equals(notification_Time)){
                        //Case 2 -- Notification on family account's shopping list
                        GroupDao groupDao = new GroupDao();
                        Group group = groupDao.findGroup(user.getEmail());
                        if(group != null){
                            int gid = group.getGid();
                            List<Group> groupList = groupDao.list(gid);
                            groupList.forEach(userInGroup -> {
                                User eachUser = userDao.findUserWithEmail(userInGroup.getgEmail());
                                Log.d(TAG, "Reg token for others: "+ eachUser.getToken());
                                sendNotification(eachUser.getToken(), eachUser.getId(), user.getUsername());
                            });
                        }
                        //Case 1 -- Notification on own shopping list
                        Log.d(TAG, "Reg token for own: "+ user.getToken());
                        sendNotification(user.getToken(), user.getId(), user.getUsername());
                    }
                }
            }
            });
        return Result.success();
    }



    private void sendNotification(String regToken, int user_id, String username) {
        String msg = username + "'s shopping list has generated";
        String title = "Shopping List";

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Log.d(TAG, "Reg token to send: " + regToken );
                        OkHttpClient client = new OkHttpClient();
                        JSONObject json = new JSONObject();
                        JSONObject dataJson = new JSONObject();
                        dataJson.put("body", msg);
                        dataJson.put("title", title);
                        dataJson.put("data", user_id);
                        json.put("notification", dataJson);
                        json.put("to", regToken);
                        RequestBody body = RequestBody.create(JSON, json.toString());
                        Request request = new Request.Builder()
                                .header("Authorization", "key=" + LEGACY_SERVER_KEY)
                                .url("https://fcm.googleapis.com/fcm/send")
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        String finalResponse = response.body().string();
                        Log.d(TAG, "Response: " + finalResponse );
                    } catch (Exception e) {
                        Log.d(TAG,e+"");
                    }
                    return null;
                }
            }.execute();

        }
    }

