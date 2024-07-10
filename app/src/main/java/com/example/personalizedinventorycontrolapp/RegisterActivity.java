package com.example.personalizedinventorycontrolapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.personalizedinventorycontrolapp.dao.UserDao;
import com.example.personalizedinventorycontrolapp.entity.User;

public class RegisterActivity extends AppCompatActivity {

    EditText username = null;
    EditText password = null;
    EditText confirmpassword = null;
    EditText email = null;
    Button register;
    UserDao userDao;
    User user;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.password2);
        email = findViewById(R.id.email);
        register = findViewById(R.id.button3);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);

    }
    // check if the text is empty
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEqual(EditText text, EditText text2 ) {
        CharSequence str = text.getText().toString();
        CharSequence str2 = text2.getText().toString();
        return TextUtils.equals(str,str2);
    }

    //check if the email is valid pattern
    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    //Error message for different data
    boolean checkEnteredData() {
        if (isEmpty(username)) {
            Toast t = Toast.makeText(this, "You must enter username to register!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        if (isEmpty(password) && isEmpty(confirmpassword) && !isEmail(email)) {
            password.setError("Password is required!");
            confirmpassword.setError("Confirm Password is required!");
            email.setError("Please enter valid email!");
            return false;
        }else if(isEmpty(confirmpassword)&&!isEmail(email)){
            confirmpassword.setError("Confirm Password is required!");
            email.setError("Please enter valid email!");
            return false;
        }else if (!isEqual(password, confirmpassword)&&!isEmail(email)) {
            confirmpassword.setError("Confirm Password is not the same as the password!");
            email.setError("Please enter valid email!");
            return false;
        }else if(isEmpty(password)&&!isEmail(email)){
            password.setError("Password is required!");
            email.setError("Please enter valid email!");
            return false;
        } else if (isEmpty(password) && isEmpty(confirmpassword)) {
            password.setError("Password is required!");
            confirmpassword.setError("Confirm Password is required!");
            return false;
        }

        if (!isEmail(email)) {
            email.setError("Please enter valid email!");
            return false;
        }
        return true;
    }

    public void login(View view){

        startActivity(new Intent(getApplicationContext(),MainActivity.class));

    }

    public void register(View view){

        String cusername = username.getText().toString();
        String cpassword = password.getText().toString();
        String cemail = email.getText().toString();
        if(checkEnteredData()){
            user = new User();

            user.setUsername(cusername);
            user.setPassword(cpassword);
            user.setEmail(cemail);

            new Thread(() -> {
                Message msg = new Message();
                userDao = new UserDao();
                int directid = 0;
                User u = userDao.findUser(user.getUsername(), user.getEmail());
                if(u != null ){
                    msg.what = 1;
                }else {
                    boolean flag = userDao.register(user);
                    if(flag){
                        msg.what = 2;
                        directid = 1;
                    }
                }
                hand.sendMessage(msg);
                if(directid == 1){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }).start();
        }
    }
    final Handler hand = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(getApplicationContext(),"Account already exist, please try to login with the account or register another account.",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(),"Successfully Registered.",Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

}
