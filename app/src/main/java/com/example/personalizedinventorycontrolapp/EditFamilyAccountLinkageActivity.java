package com.example.personalizedinventorycontrolapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.personalizedinventorycontrolapp.adapter.listAdapter;
import com.example.personalizedinventorycontrolapp.dao.GroupDao;
import com.example.personalizedinventorycontrolapp.dao.UserDao;
import com.example.personalizedinventorycontrolapp.entity.Group;
import com.example.personalizedinventorycontrolapp.entity.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditFamilyAccountLinkageActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    listAdapter adapter;
    List<Group> familyAccountList = new ArrayList<>();
    FloatingActionButton addFamilyAccountButton;
    SharedPreferences sharedpreferences;
    private static final String TAG = "EditFamilyAccountLinkageActivity";
    String shUsername;
    String shEmail;
    int shUserid;
    int notificationTypeID = 0;
    GroupDao groupDao;
    Group gg;
    UserDao userDao;
    User user;
    private static final String LEGACY_SERVER_KEY = "AAAAPDbqf6Y:APA91bEeOynmp-07posWCNHobfsjKg5mYIv28qoWwBpAAYmxKZSYFXTEL6oBSpX5WNOQCw8VTQWNC2jW7za74J45LaGUwH4xfPD6TaR9oOn5zS0J_KW-8P_E1t93FAUJTSkH7Gz-UYO6";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_family_account_linkage);
        drawerLayout = findViewById(R.id.LinkagePageDrawer);
        addFamilyAccountButton = findViewById(R.id.addFamilyAccount);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        shUsername = sharedpreferences.getString(MainActivity.Username, "");
        shEmail = sharedpreferences.getString(MainActivity.Email, "");
        shUserid = sharedpreferences.getInt(MainActivity.Userid,0);
        getLinkedFamilyAccount();
        addFamilyAccountButton.setOnClickListener(view -> dialog());
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }


    private final Handler myHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.d(TAG, "run: Ui update successfully.");
                RecyclerView recyclerView = findViewById(R.id.recyclerviewFamilyAccount);
                LinearLayoutManager Manager = new LinearLayoutManager(EditFamilyAccountLinkageActivity.this);
                recyclerView.setLayoutManager(Manager);
                adapter = new listAdapter(EditFamilyAccountLinkageActivity.this, familyAccountList);
                adapter.setOnItemClickListener((view, position) -> Toast.makeText(EditFamilyAccountLinkageActivity.this, "click " + familyAccountList.get(position).getgEmail(), Toast.LENGTH_SHORT).show());
                adapter.setOnItemLongClickListener((view, position) -> {
                    removeFamilyAccount(familyAccountList.get(position));
                    Toast.makeText(EditFamilyAccountLinkageActivity.this, "long click " + familyAccountList.get(position).getUsers_id(), Toast.LENGTH_SHORT).show();
                });
                recyclerView.setAdapter(adapter);
            }
        }
    };

    private void getLinkedFamilyAccount() {
        MyThread myThread = new MyThread();
        Thread thread = new Thread(myThread);
        thread.start();
    }

    private class MyThread implements Runnable {
        @Override
        public void run() {
            groupDao = new GroupDao();
            gg = groupDao.findGroup(shEmail);
            if (gg != null) {
                int groupings_id = gg.getGid();
                familyAccountList = groupDao.list(groupings_id);
                Log.d(TAG, "run: Database read successfully.");
                Message msg = new Message();
                msg.what = 1;
                myHandler.sendMessage(msg);
                Log.d(TAG, "run: Message msg.what sent successfully.");
            }
        }
    }

    void removeFamilyAccount(Group userInGroup) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Remove Family Account");
        builder.setMessage("Are you sure you want to remove " + userInGroup.getgEmail() + "?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> new Thread(() -> {
            //Remove family account from current user's group
            groupDao = new GroupDao();
            gg = groupDao.findGroup(shEmail);
            int gid = gg.getGid();
            boolean flag = groupDao.deleteUserFromGroup(gid, userInGroup.getUsers_id());
            if (flag) {
                getLinkedFamilyAccount();
                userDao = new UserDao();
                user = userDao.findUserWithID(userInGroup.getUsers_id());

                //Remove current user from that family account's group
                Group familyAccountGroup = groupDao.findGroup(user.getEmail());
                int familyAccountGid = familyAccountGroup.getGid();
                boolean removeLinkage = groupDao.deleteUserFromGroup(familyAccountGid, shUserid);
                if(removeLinkage){
                    notificationTypeID = 2;
                    User currentUser = userDao.findUserWithID(shUserid);
                    sendNotification(user, currentUser, 2);
                    Looper.prepare();
                    Toast.makeText(EditFamilyAccountLinkageActivity.this, userInGroup.getgEmail() + " removed", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }).start());
        builder.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText emailInputEditText = new EditText(EditFamilyAccountLinkageActivity.this);
        emailInputEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(emailInputEditText);
        builder.setTitle("Please enter the email address");
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        AlertDialog alertDialogAndroid = builder.create();
        alertDialogAndroid.show();
        Button positiveButton = alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            boolean isError = false;
            if (!isEmail(emailInputEditText)) {
                isError = true;
                emailInputEditText.setError("Please enter valid email!");
            }
            if (emailInputEditText.getText().toString().equals("")) {
                isError = true;
                emailInputEditText.setError("Please enter email!");
            }
            if (!isError) {
                new Thread(() -> {
                    UserDao userDao = new UserDao();
                    User currentUser = userDao.findUserWithID(shUserid);
                    User user = userDao.findUserWithEmail(emailInputEditText.getText().toString());
                    if (user != null) {
                        groupDao = new GroupDao();
                        Group group = groupDao.findGroup(shEmail);
                        int gid = 0;
                        if(group == null){
                            boolean newGroupCreate = groupDao.createGroup(shEmail);
                            if(newGroupCreate){
                                Group newGroup = groupDao.findGroup(shEmail);
                                gid = newGroup.getGid();
                            }
                        }else{
                            gid = group.getGid();
                        }
                        if(gid != 0){
                            Group userInGroup = groupDao.findUserInGroup(gid, user.getId());
                            if(userInGroup != null){
                                Looper.prepare();
                                Toast.makeText(EditFamilyAccountLinkageActivity.this, emailInputEditText.getText().toString() + " already linked.", Toast.LENGTH_SHORT).show();
                            }else{
                                //send notification device to device for request
                                notificationTypeID = 1;
                                sendNotification(user,currentUser, notificationTypeID);
                                alertDialogAndroid.cancel();
                                Looper.prepare();
                                Toast.makeText(EditFamilyAccountLinkageActivity.this, "Linkage request sent to " + emailInputEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                            }
                            Looper.loop();
                        }

                    } else {
                        Looper.prepare();
                        Toast.makeText(EditFamilyAccountLinkageActivity.this, emailInputEditText.getText().toString() + " not registered user", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }).start();
            }
        });
        alertDialogAndroid.show();
    }

    private void sendNotification(User user,User currentUser, int notificationTypeID) {
        String regToken = user.getToken();
        String email = currentUser.getEmail();
        String username = currentUser.getUsername();
        if(notificationTypeID == 1){
            String msg = "Allowing " + username + "(" + email + ") to add your account to the family account?";
            String title = "Link Family Account";
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Log.d(TAG, "Reg token to send: " + regToken);
                        OkHttpClient client = new OkHttpClient();

                        JSONObject notification = new JSONObject();
                        notification.put("body", msg);
                        notification.put("title", title);

                        JSONObject data =  new JSONObject();
                        data.put("userid", shUserid);
                        data.put("email", shEmail);

                        JSONObject payload = new JSONObject();
                        payload.put("notification", notification);
                        payload.put("data", data);
                        payload.put("to", regToken);
                        RequestBody body = RequestBody.create(JSON, payload.toString());
                        Request request = new Request.Builder()
                                .header("Authorization", "key=" + LEGACY_SERVER_KEY)
                                .url("https://fcm.googleapis.com/fcm/send")
                                .post(body)
                                .build();

                        Response response = client.newCall(request).execute();
                        String finalResponse = Objects.requireNonNull(response.body()).string();
                        Log.d(TAG, "Response: " + finalResponse);
                    } catch (Exception e) {
                        Log.d(TAG, e + "");
                    }
                    return null;
                }
            }.execute();
        }else if(notificationTypeID == 2){
            String msg = username + "(" + email + ") unlinked your account.";
            String title = "Unlink Family Account";
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
                        String finalResponse = Objects.requireNonNull(response.body()).string();
                        Log.d(TAG, "Response: " + finalResponse);
                    } catch (Exception e) {
                        Log.d(TAG, e + "");
                    }
                    return null;
                }
            }.execute();
        }
    }

    public void clickBack(View view) {
        ViewInventoryActivity.redirectActivity(this, EditSettingActivity.class);
    }
}