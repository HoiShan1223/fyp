package com.example.personalizedinventorycontrolapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.personalizedinventorycontrolapp.dao.UserDao;
import com.example.personalizedinventorycontrolapp.entity.User;
import com.example.personalizedinventorycontrolapp.worker.CheckStockConditionWorker;
import com.example.personalizedinventorycontrolapp.worker.SendNotificationWorker;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "1234";
    EditText account = null;
    EditText password = null;
    Button login = null;
    String ipaddress;
    UserDao userDao;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String IPADDRESS = "ipaddressKey";
    public static final String Username = "usernameKey";
    public static final String Userid = "useridKey";
    public static final String Email = "EmailKey";
    public static final String Firebase_Cloud_Messaging_Token = "FCMTokenKey";
    private static final String TAG = "PushNotification";
    private static final String TAG2 = "Updatetoken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        account = findViewById(R.id.username_input);
        password = findViewById(R.id.password_input);
        login = findViewById(R.id.LoginButton2);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        final WorkManager mWorkManager = WorkManager.getInstance(getApplicationContext());
        PeriodicWorkRequest CheckStockConditionWorkRequest = new PeriodicWorkRequest.Builder(CheckStockConditionWorker.class,1, TimeUnit.DAYS).build();
        mWorkManager.enqueueUniquePeriodicWork("CheckStockConditionWork",ExistingPeriodicWorkPolicy.KEEP,CheckStockConditionWorkRequest);
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(CheckStockConditionWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        createNotificationChannel();
        getToken();
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(SendNotificationWorker.class)
                .build();
        WorkManager.getInstance().beginWith(notificationWork).enqueue();
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }
    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean checkEnteredData() {
        if (isEmpty(account)) {
            Toast t = Toast.makeText(this, "You must enter username or email address to register!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        if (isEmpty(password)) {
            password.setError("Password is required!");
            return false;
        }
        return true;
    }

    public void reg(View view) {

        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));

    }

    public void forgetPassword(View view) {

        startActivity(new Intent(getApplicationContext(), ForgetPasswordActivity.class));

    }

    public void login(View view) {
        if(checkEnteredData()){
             new Thread(() -> {
                 Message msg = new Message();
                 SharedPreferences.Editor editor = sharedpreferences.edit();
                 userDao = new UserDao();
                 int directid = 0;
                 if(isEmail(account)){

                     boolean aa = userDao.emailLogin(account.getText().toString(), password.getText().toString());
                     if (aa) {
                         User currentUser = userDao.findUserWithEmail(account.getText().toString());
                         editor.putString(Email, account.getText().toString());
                         editor.putString(Username,currentUser.getUsername());
                         editor.putInt(Userid,currentUser.getId());
                         editor.putString(IPADDRESS,ipaddress);
                         editor.apply();
                         String shFCMToken = sharedpreferences.getString(Firebase_Cloud_Messaging_Token, "");
                         Log.d(TAG2, "shared preferences token for adding to db: " + shFCMToken);
                         User someTokenUser = userDao.findUserWithToken(shFCMToken, currentUser.getId());
                         if(someTokenUser == null){
                             boolean updateToken = userDao.updateToken(currentUser.getId(), shFCMToken);
                             Log.d(TAG2, "Update Token: " + updateToken);
                         }else{
                             String emptyTokenString = "";
                             boolean updateDuplicateToken = userDao.updateToken(someTokenUser.getId(), emptyTokenString);
                             Log.d(TAG2, "Update Duplicate Token: " + updateDuplicateToken);
                             if(updateDuplicateToken){
                                 boolean updateToken = userDao.updateToken(currentUser.getId(), shFCMToken);
                                 Log.d(TAG2, "Update Token: " + updateToken);
                             }
                         }
                         msg.what = 1;
                         directid = 1;
                     }
                 }else {
                     boolean aa = userDao.usernameLogin(account.getText().toString(), password.getText().toString());
                     if (aa) {
                         User currentUser = userDao.findUserWithUsername(account.getText().toString());
                         editor.putString(Username, account.getText().toString());
                         editor.putString(Email, currentUser.getEmail());
                         editor.putInt(Userid, currentUser.getId());
                         editor.apply();
                         String shFCMToken = sharedpreferences.getString(Firebase_Cloud_Messaging_Token, "");
                         Log.d(TAG2, "shared preferences token for adding to db: " + shFCMToken);
                         User someTokenUser = userDao.findUserWithToken(shFCMToken, currentUser.getId());
                         if(someTokenUser == null){
                             boolean updateToken = userDao.updateToken(currentUser.getId(), shFCMToken);
                             Log.d(TAG2, "Update Token: " + updateToken);
                         }else{
                             String emptyTokenString = "";
                             boolean updateDuplicateToken = userDao.updateToken(someTokenUser.getId(), emptyTokenString);
                             Log.d(TAG2, "Update Duplicate Token: " + updateDuplicateToken);
                             if(updateDuplicateToken){
                                 boolean updateToken = userDao.updateToken(currentUser.getId(), shFCMToken);
                                 Log.d(TAG2, "Update Token: " + updateToken);
                             }
                         }
                         msg.what = 1;
                         directid = 1;
                     }else{
                         msg.what  = 2;
                     }
                 }
                 hand1.sendMessage(msg);
                 if(directid == 1){
                     Intent intent = new Intent(MainActivity.this, ViewInventoryActivity.class);
                     startActivity(intent);
                 }
             }).start();
        }

    }
    final Handler hand1 = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(getApplicationContext(), "Login succeeded!", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "Login failed. Invalid Username, Email or Password.", Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            // if task is failed
            if(!task.isSuccessful()){
                Log.d(TAG, "onComplete: Fail to get the Token");
            }
            //Token
            String token = task.getResult();
            Log.d(TAG, "onComplete: " + token);
            String shFCMToken = sharedpreferences.getString(Firebase_Cloud_Messaging_Token, "");
            Log.d(TAG, "shared preferences token: " + shFCMToken);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Firebase_Cloud_Messaging_Token, token);
            editor.apply();
            String shNewFCMToken = sharedpreferences.getString(Firebase_Cloud_Messaging_Token, "");
            Log.d(TAG, "shared preferences new token: " + shNewFCMToken);

        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FirebaseNotificationChannel";
            String description = "Receive Firebase Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
