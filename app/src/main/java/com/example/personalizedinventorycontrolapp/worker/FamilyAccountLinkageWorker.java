package com.example.personalizedinventorycontrolapp.worker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.personalizedinventorycontrolapp.dao.GroupDao;
import com.example.personalizedinventorycontrolapp.dao.UserDao;
import com.example.personalizedinventorycontrolapp.entity.Group;
import com.example.personalizedinventorycontrolapp.entity.User;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FamilyAccountLinkageWorker extends Worker {

    private static final String TAG = "FamilyAccountLinkageWorker";
    private static final String LEGACY_SERVER_KEY = "AAAAPDbqf6Y:APA91bEeOynmp-07posWCNHobfsjKg5mYIv28qoWwBpAAYmxKZSYFXTEL6oBSpX5WNOQCw8VTQWNC2jW7za74J45LaGUwH4xfPD6TaR9oOn5zS0J_KW-8P_E1t93FAUJTSkH7Gz-UYO6";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public FamilyAccountLinkageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Check
        int responseUserid = getInputData().getInt("ResponseUserid", 0);
        int senderUserid = getInputData().getInt("senderUserid", 0);
        String email = getInputData().getString("email");
        String senderEmail = getInputData().getString("senderEmail");
        UserDao userDao = new UserDao();
        GroupDao groupDao = new GroupDao();
        if(senderUserid != 0) {
            User user = userDao.findUserWithID(senderUserid);
            User currentUser = userDao.findUserWithID(responseUserid);
            Group gg = groupDao.findGroup(senderEmail);
            if (gg == null) { //Link own account to sender account with group not created
                boolean flag = groupDao.createGroup(senderEmail);
                if (flag) {
                    Group new_gg = groupDao.findGroup(senderEmail);
                    int new_gid = new_gg.getGid();
                    boolean ag = groupDao.addUsersToGroup(new_gid,responseUserid);
                    if (ag) {
                        Log.d(TAG, "AddUserToGroup: "+ true);
                        Group familyGroup = groupDao.findGroup(email); //link sender to own account group
                        if (familyGroup == null) { //link sender to own account group still not have group
                            boolean flag2 = groupDao.createGroup(email);
                            if (flag2) {
                                Group new_familyGroup = groupDao.findGroup(email);
                                int new_familyGid = new_familyGroup.getGid();
                                boolean addGroup = groupDao.addUsersToGroup(new_familyGid, senderUserid);
                                if(addGroup){
                                    Log.d(TAG, "AddUserToGroup: "+ true);
                                    //sendNotification to sender
                                    String regToken = user.getToken();
                                    sendNotification(regToken, currentUser.getUsername(), currentUser.getEmail(), true);
                                }
                            }
                        } else {//link sender to own account group already have group
                            Group new_familyGroup = groupDao.findGroup(email);
                            int new_familyGid = new_familyGroup.getGid();
                            boolean addGroup = groupDao.addUsersToGroup(new_familyGid,senderUserid);
                            if(addGroup){
                                Log.d(TAG, "AddUserToGroup: "+ true);
                                //sendNotification to sender
                                String regToken = user.getToken();
                                sendNotification(regToken, currentUser.getUsername(), currentUser.getEmail(), true);
                            }
                        }
                    }
                }
            }else{ //Link own account to sender account with group created
                int gid = gg.getGid();
                Group userInGroup = groupDao.findUserInGroup(gid, responseUserid);
                if (userInGroup == null) { // own account not in the sender account group yet
                    boolean ag = groupDao.addUsersToGroup(gid, responseUserid);
                    if (ag) {
                        //add the coding for add this account user also in the family account's group
                        Log.d(TAG, "AddUserToGroup: "+ ag);
                        Group familyGroup = groupDao.findGroup(email); //link sender account to our account group
                        if (familyGroup == null) { // family account that want to link still not have group
                            boolean flag2 = groupDao.createGroup(email);
                            if (flag2) {
                                Group new_familyGroup = groupDao.findGroup(email);
                                int new_familyGid = new_familyGroup.getGid();
                                boolean addGroup = groupDao.addUsersToGroup(new_familyGid,senderUserid);
                                if(addGroup){
                                    Log.d(TAG, "AddUserToGroup: "+ true);
                                    //sendNotification to sender
                                    String regToken = user.getToken();
                                    sendNotification(regToken, currentUser.getUsername(), currentUser.getEmail(), true);
                                }
                            }
                        } else {// family account that want to link already have group
                            Group new_familyGroup = groupDao.findGroup(email);
                            int new_familyGid = new_familyGroup.getGid();
                            boolean addGroup = groupDao.addUsersToGroup(new_familyGid,senderUserid);
                            if(addGroup){
                                Log.d(TAG, "AddUserToGroup: "+ true);
                                //sendNotification to sender
                                String regToken = user.getToken();
                                sendNotification(regToken, currentUser.getUsername(), currentUser.getEmail(), true);
                            }
                        }
                    }
                    }else{
                    String regToken = user.getToken();
                    sendNotification(regToken, currentUser.getUsername(), currentUser.getEmail(), false);
                }
                }
            }
        return Result.success();
    }

    private void sendNotification(String regToken, String username, String email, Boolean userInGroup) {
        String msg;
        if(userInGroup){
            msg =  username + "(" + email + ") allowed to add your account to their family account.";
        }else{
            msg =  username + "(" + email + ") already linked";

        }

        String title = "Response to Family Account Linkage";

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Log.d(TAG, "Reg token to send: " + regToken);
                    OkHttpClient client = new OkHttpClient();
                    JSONObject payload = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", msg);
                    notification.put("title", title);
//                    JSONObject data =  new JSONObject();
//                    data.put("userid", shUserid);
//                    data.put("email", shEmail);
                    payload.put("notification", notification);
//                    payload.put("data", data);
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
