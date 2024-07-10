package com.example.personalizedinventorycontrolapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.example.personalizedinventorycontrolapp.dao.ProductDao;
import com.example.personalizedinventorycontrolapp.entity.Product;

public class ScanInventoriesActivity extends AppCompatActivity {
    private static final String TAG = "ScanInventoriesActivity";
    private CodeScanner mCodeScanner;
    SharedPreferences sharedpreferences;
    Product pp;
    ProductDao productDao;
    String barcodeNumber;
    public static final String BarcodeNumber = "barcodeNumberKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_inventory);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);

        getPermissionCamera();
        mCodeScanner.setDecodeCallback(result -> {
            new Thread() {
                @Override
                public void run() {
                    Message msg = new Message();
                    sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(BarcodeNumber, result.getText());
                    editor.apply();
                    productDao = new ProductDao();
                    pp = productDao.findProduct(result.getText());
                    barcodeNumber = result.getText();
                    int directID = 0;
                    if (pp != null) {
                        msg.what = 1;
                        directID = 1;
                        Log.d(TAG, "onComplete: " +  directID);

                    } else {
                        msg.what = 2;
                        directID = 2;
                    }
                    myHandler.sendMessage(msg);
                    switch (directID){
                        case 1:
                            Intent intent2 = new Intent(ScanInventoriesActivity.this, AddInventoryWithScanActivity.class);
                            startActivity(intent2);
                            break;
                        case 2:
                            startActivity(new Intent(ScanInventoriesActivity.this, AddInventoryActivity.class));
                            break;
                    }
                }
                }.start();
        });
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    final Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(ScanInventoriesActivity.this, barcodeNumber, Toast.LENGTH_SHORT).show();
                    break;
                case 2 :
                    Toast.makeText(getApplicationContext(), "Cannot recognize the item. Please add item manually.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public void getPermissionCamera(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(ScanInventoriesActivity.this,
                Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Camera Permission")
                    .setMessage("InventoryH needs access to your camera for barcode scanning.")
                    .setPositiveButton("OK", (dialogInterface, i) -> ActivityCompat.requestPermissions(ScanInventoriesActivity.this,new String[]{Manifest.permission.CAMERA},1))
                    .show();

        }else{
            ActivityCompat.requestPermissions(ScanInventoriesActivity.this,new String[]{Manifest.permission.CAMERA},1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}
