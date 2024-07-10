package com.example.personalizedinventorycontrolapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.personalizedinventorycontrolapp.worker.FamilyAccountLinkageWorker;
import com.example.personalizedinventorycontrolapp.worker.RejectLinkageWorker;

public class NotificationController extends BroadcastReceiver {
    public static final String ACTION_YES = "com.example.personalizedinventorycontrolapp.ACTION_YES";
    public static final String ACTION_NO = "com.example.personalizedinventorycontrolapp.ACTION_NO";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null && intent.getAction().equals(ACTION_YES)){
            boolean tappedYes = intent.getBooleanExtra("TAPPED_YES", false);
            int ResponseUserid = intent.getIntExtra("ResponseUserid", 0);
            int senderUserid = intent.getIntExtra("senderUserid", 0);
            String email = intent.getStringExtra("email");
            String senderEmail = intent.getStringExtra("senderEmail");
            if(tappedYes){
                scheduleWorkForTappedYes(context, ResponseUserid,senderUserid, email, senderEmail);
            }
        } else if (intent.getAction() != null && intent.getAction().equals(ACTION_NO)){
            boolean tappedNo = intent.getBooleanExtra("TAPPED_NO", false);
            int ResponseUserid = intent.getIntExtra("ResponseUserid", 0);
            int senderUserid = intent.getIntExtra("senderUserid", 0);
            if(tappedNo){
                scheduleWorkForTappedNo(context, ResponseUserid, senderUserid);
            }
        }
    }

    private void scheduleWorkForTappedYes(Context context, int ResponseUserid, int senderUserid, String email, String senderEmail){
        Data inputData = new Data.Builder().putInt("ResponseUserid", ResponseUserid).putInt("senderUserid", senderUserid).putString("email", email).putString("senderEmail", senderEmail).build();
        String FamilyAccountLinkageWorkRequest = "FamilyAccountLinkageWorkRequest";

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(FamilyAccountLinkageWorker.class)
                .setInputData(inputData).build();

        WorkManager.getInstance(context).enqueueUniqueWork(FamilyAccountLinkageWorkRequest, ExistingWorkPolicy.REPLACE, workRequest);
    }

    private void scheduleWorkForTappedNo(Context context, int ResponseUserid, int senderUserid){
        Data inputData = new Data.Builder().putInt("ResponseUserid", ResponseUserid).putInt("senderUserid", senderUserid).build();
        String FamilyAccountLinkageWorkRequest = "RejectLinkageWorkRequest";

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RejectLinkageWorker.class)
                .setInputData(inputData).build();

        WorkManager.getInstance(context).enqueueUniqueWork(FamilyAccountLinkageWorkRequest, ExistingWorkPolicy.REPLACE, workRequest);
    }
}
