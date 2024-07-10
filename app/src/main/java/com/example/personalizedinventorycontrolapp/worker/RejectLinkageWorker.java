package com.example.personalizedinventorycontrolapp.worker;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.personalizedinventorycontrolapp.dao.UserDao;
import com.example.personalizedinventorycontrolapp.entity.User;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RejectLinkageWorker extends Worker {

    private static final String TAG = "RejectLinkageWorker";
    private static final String LEGACY_SERVER_KEY = "AAAAPDbqf6Y:APA91bEeOynmp-07posWCNHobfsjKg5mYIv28qoWwBpAAYmxKZSYFXTEL6oBSpX5WNOQCw8VTQWNC2jW7za74J45LaGUwH4xfPD6TaR9oOn5zS0J_KW-8P_E1t93FAUJTSkH7Gz-UYO6";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public RejectLinkageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int responseUserid = getInputData().getInt("ResponseUserid", 0);
        int senderUserid = getInputData().getInt("senderUserid", 0);
        UserDao userDao = new UserDao();
        User responseUser = userDao.findUserWithID(senderUserid);
        User user = userDao.findUserWithID(responseUserid);
        if(user != null){
            sendNotification(user, responseUser);
        }
        return Result.success();
    }

    private void sendNotification(User user, User responseUser) {
        String username = user.getUsername();
        String email = user.getEmail();
        String regToken = responseUser.getToken();

        String title = "Rejection on Family Account Linkage";
        String msg =  username + "(" + email + ") reject the linkage request.";

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Log.d(TAG, "Reg token to send: " + regToken);
                    OkHttpClient client = new OkHttpClient();
                    JSONObject notification = new JSONObject();
                    notification.put("body", msg);
                    notification.put("title", title);

                    JSONObject payload = new JSONObject();
                    payload.put("notification", notification);
                    payload.put("to", regToken);

                    RequestBody body = RequestBody.create(JSON, payload.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + LEGACY_SERVER_KEY)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();

                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.d(TAG, "Response: " + finalResponse);
                } catch (Exception e) {
                    Log.d(TAG, e + "");
                }
                return null;
            }
        }.execute();
    }
}
