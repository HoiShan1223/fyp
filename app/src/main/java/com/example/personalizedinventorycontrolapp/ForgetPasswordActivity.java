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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.personalizedinventorycontrolapp.dao.UserDao;
import com.example.personalizedinventorycontrolapp.entity.User;

public class ForgetPasswordActivity extends AppCompatActivity {
    EditText username;
    EditText email;
    EditText password;
    EditText confirmpassword;
    Button buttonConfirm;
    RelativeLayout relativeLayoutConfirmPassword;
    RelativeLayout relativeLayoutPassword;
    SharedPreferences sharedpreferences;
    User user;
    UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        password = findViewById(R.id.passwordResetPassword);
        confirmpassword = findViewById(R.id.passwordResetPassword2);
        username = findViewById(R.id.usernameResetPassword);
        email = findViewById(R.id.emailResetPassword);
        relativeLayoutPassword = findViewById(R.id.relativeLayoutPassword);
        relativeLayoutConfirmPassword = findViewById(R.id.relativeLayoutConfirmPassword);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);

    }
    // check if the text is empty
    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    //check if the email is valid pattern
    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEqual(EditText text, EditText text2 ) {
        CharSequence str = text.getText().toString();
        CharSequence str2 = text2.getText().toString();
        return TextUtils.equals(str,str2);
    }

    boolean checkEnteredPassword() {
        if (isEmpty(password) && isEmpty(confirmpassword)) {
            password.setError("Password is required!");
            confirmpassword.setError("Confirm Password is required!");
            return false;
        }else if(isEmpty(password) && !isEmpty(confirmpassword)){
            password.setError("Password is required!");
            return false;
        }else if(isEmpty(confirmpassword) && !isEmpty(password)){
            confirmpassword.setError("Confirm Password is required!");
            return false;
        }else if (!isEmpty(password) && !isEmpty(confirmpassword) && !isEqual(password, confirmpassword)) {
            confirmpassword.setError("Confirm Password is not the same as the password!");
            return false;
        }
        return true;
    }

    boolean checkEnteredData() {
        if (isEmpty(username)) {
            Toast t = Toast.makeText(this, "You must enter username to reset password!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        if(!isEmail(email)){
            email.setError("Please enter valid email!");
            return false;
        }
        return true;
    }

    public void confirm(View view){
        String cusername = username.getText().toString();
        String cemail = email.getText().toString();
        if(relativeLayoutPassword.getVisibility() == View.GONE && relativeLayoutConfirmPassword.getVisibility() == View.GONE){
          if(checkEnteredData()){
              user = new User();

              user.setUsername(cusername);
              user.setEmail(cemail);

              new Thread(() -> {
                  Message msg = new Message();
                  userDao = new UserDao();
                  User u = userDao.findUser(user.getUsername(), user.getEmail());
                  if(u != null ){
                      //show password
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              relativeLayoutPassword.setVisibility(View.VISIBLE);
                              relativeLayoutConfirmPassword.setVisibility(View.VISIBLE);
                              msg.what = 1;
                          }
                      });
                  }else{
                      msg.what = 2;
                  }
                  hand.sendMessage(msg);
              }).start();
          }
        }else if(relativeLayoutPassword.getVisibility() == View.VISIBLE && relativeLayoutConfirmPassword.getVisibility() == View.VISIBLE){
            String cpassword = password.getText().toString();
            if(checkEnteredData() && checkEnteredPassword()){
                user = new User();

                user.setUsername(cusername);
                user.setEmail(cemail);

                new Thread(() -> {
                    Message msg = new Message();
                    userDao = new UserDao();
                    int directid = 0;
                    User u = userDao.findUser(user.getUsername(), user.getEmail());
                    if(u != null ){
                        boolean updatePassword = userDao.updatePassword(u.getId(), cpassword);
                        if(updatePassword){
                            msg.what = 3;
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
    }

    final Handler hand = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(getApplicationContext(),"Valid account. Now you can reset your password.",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(),"Invalid account. Please try again.",Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(),"Password reset Successfully.",Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    public void clickBack(View view) {
        ViewInventoryActivity.redirectActivity(this, MainActivity.class);
    }
}