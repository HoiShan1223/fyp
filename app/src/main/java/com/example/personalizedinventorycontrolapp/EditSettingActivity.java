package com.example.personalizedinventorycontrolapp;


import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalizedinventorycontrolapp.dao.NotificationDao;
import com.example.personalizedinventorycontrolapp.entity.Notification;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Objects;

public class EditSettingActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;
    SharedPreferences msharedpreferences;
    TextView username;
    TextView email;
    String shNotificationDay;
    int shUserid;
    int shNotificationTime;
    String notificationTime;
    boolean shNotificationCondition;
    NotificationDao notificationDao;
    Notification nn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_setting);
        drawerLayout=findViewById(R.id.drawer);
        username = findViewById(R.id.login_username);
        email =findViewById(R.id.login_email);
        msharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        String shUsername = msharedpreferences.getString(MainActivity.Username, "");
        String shEmail = msharedpreferences.getString(MainActivity.Email, "");
        username.setText(shUsername);
        email.setText(shEmail);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.listNotification, new SettingsFragment())
                .commit();
        SharedPreferences fragmentSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        fragmentSharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        shUserid = msharedpreferences.getInt(MainActivity.Userid,0);
        shNotificationDay = fragmentSharedPreferences.getString("NotificationDay", "");
        shNotificationTime = fragmentSharedPreferences.getInt("NotificationTime", 0);
        shNotificationCondition = fragmentSharedPreferences.getBoolean("notificationSwitch", false);
        if(!shNotificationDay.equals("") && shNotificationTime != 0 && shNotificationCondition){
            int hours = shNotificationTime / 60;
            int minutes = shNotificationTime % 60;
            if(hours< 10){
                String hourString = "0" + hours;
                String mintuesString;
                if(minutes == 0){
                    mintuesString = "0" + minutes;
                    notificationTime = hourString + ":" + mintuesString;
                }else{
                    notificationTime = hourString + ":" + minutes;
                }
            }else{
                String mintuesString;
                if(minutes == 0){
                    mintuesString = "0" + minutes;
                    notificationTime = hours + ":" + mintuesString;
                }else{
                    notificationTime = hours + ":" + minutes;
                }
            }
        }

    }

    private final SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("NotificationDay")) {
                String value = sharedPreferences.getString(key, "");
                addNotifications(value, notificationTime,shNotificationCondition);
                Log.d("Change notification Day", "key: " + value + ", value: " + value);
            }
            if (key.equals("NotificationTime")) {
                int value = sharedPreferences.getInt(key, 0);
                int hours = value / 60;
                int minutes = value % 60;
                String newNotificationTime;
                if(hours< 10){
                    String hourString = "0" + hours;
                    String mintuesString;
                    if(minutes == 0){
                        mintuesString = "0" + minutes;
                        newNotificationTime = hourString + ":" + mintuesString;
                    }else{
                        newNotificationTime = hourString + ":" + minutes;
                    }
                }else{
                    String mintuesString;
                    if(minutes == 0){
                        mintuesString = "0" + minutes;
                        newNotificationTime = hours + ":" + mintuesString;
                    }else{
                        newNotificationTime = hours + ":" + minutes;
                    }
                }

                addNotifications(shNotificationDay, newNotificationTime,shNotificationCondition);
                Log.d("Change notification Time", "key: " + value + ", value: " + value);
            }
            if(key.equals("notificationSwitch")){
                boolean value = sharedPreferences.getBoolean(key, false);
                updateNotification(value);
            }


        }
    };

    void updateNotification(boolean notification_condition){
        new Thread(() -> {
            notificationDao = new NotificationDao();
            shUserid = msharedpreferences.getInt(MainActivity.Userid,0);
            boolean updateNotification = notificationDao.updateNotification(shUserid, notification_condition);
            if(updateNotification){
                if(notification_condition){
                    Looper.prepare();
                    Toast t = Toast.makeText(getApplicationContext(), "Notification turned on",Toast.LENGTH_LONG);
                    t.show();
                    Looper.loop();
                }else{
                    Looper.prepare();
                    Toast t = Toast.makeText(getApplicationContext(), "Notification turned off",Toast.LENGTH_LONG);
                    t.show();
                    Looper.loop();
                }
            }
        }).start();
    }

    void addNotifications(String notification_Day, String notification_Time, boolean notification_Condition){
        new Thread(() -> {
            notificationDao = new NotificationDao();
            shUserid = msharedpreferences.getInt(MainActivity.Userid,0);
            Message msg = new Message();
            nn = notificationDao.findNotification(shUserid);
            if(nn != null){
                String notificationTimeFromDB = nn.getNotification_time();
                DateTimeFormatter formatter1 = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .appendPattern("HH:mm")
                        .toFormatter();
                String notificationTimeString = LocalTime.parse(notification_Time, formatter1).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String notificationDayFromDB = nn.getNotification_day();
                if(!Objects.equals(notificationTimeFromDB, notificationTimeString) || !Objects.equals(notificationDayFromDB, notification_Day)){
                    boolean un = notificationDao.updateNotification(shUserid, notification_Day, notification_Time);
                    if(un){
                        msg.what = 2;
                    }
                }else{
                    finish();
                }
            }else{
                boolean an = notificationDao.addNotification(shUserid, notification_Day, notification_Time, notification_Condition);
                if (an) {
                    msg.what = 1;
                }
            }
            myHandler.sendMessage(msg);
        }).start();
    }

    final Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "Notification set successfully.", Toast.LENGTH_LONG).show();
                    break;

                case 2:
                    Toast.makeText(getApplicationContext(), "Notification setting updated.", Toast.LENGTH_LONG).show();
                    break;

            }

        }
    };

    public  void ClickMenu(View view){
        ViewInventoryActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view){
        ViewInventoryActivity.closeDrawer(drawerLayout);
    }

    public void clickToolBarLogo(View view){
        ViewInventoryActivity.redirectActivity(this, ViewInventoryActivity.class);
    }

    public void clickInventoryRecord(View view){
        ViewInventoryActivity.redirectActivity(this, ViewInventoryActivity.class);
    }

    public  void clickAddInventoryWithScan(View view){
        ViewInventoryActivity.redirectActivity(this,AddInventoryWithScanActivity.class);
    }

    public  void clickAddInventoryManually(View view){
        ViewInventoryActivity.redirectActivity(this,AddInventoryActivity.class);
    }

    public void clickShoppingList(View view){
        ViewInventoryActivity.redirectActivity(this,ViewShoppingListActivity.class);
    }

    public void clickSettings(View view){
        recreate();
    }

    public  void ClickLogout(View view){
        ViewInventoryActivity.logout(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        msharedpreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
        ViewInventoryActivity.closeDrawer(drawerLayout);
    }


}