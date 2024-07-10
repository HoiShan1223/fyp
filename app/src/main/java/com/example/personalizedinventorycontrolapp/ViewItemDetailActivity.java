package com.example.personalizedinventorycontrolapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.personalizedinventorycontrolapp.dao.ItemDao;
import com.example.personalizedinventorycontrolapp.dao.ProductDao;
import com.example.personalizedinventorycontrolapp.entity.Item;
import com.example.personalizedinventorycontrolapp.entity.Product;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

public class ViewItemDetailActivity extends AppCompatActivity{
    DrawerLayout drawerLayout;
    ImageView itemImage;
    TextView productNameDetail;
    TextView productManufactureDetail;
    TextView productPriceDetail;
    TextView productBarcodeNumberDetail;
    EditText productQuantityDetail;
    EditText productUsageDetail;
    Button editItemButton;
    String usageType = null;
    TextView username;
    TextView email;
    Spinner spin;
    ImageButton imageButtonPlus, imageButtonMinus;
    ArrayAdapter<CharSequence> adapter;
    private boolean isEditing = false;
    SharedPreferences sharedpreferences;
    int shUserid = 0;
    int shProductId = 0;
    String imagePath;
    int currentQuantity = 0;
    ItemDao dao;
    ProductDao productDao;
    Item item;
    Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item_detail);
        drawerLayout = findViewById(R.id.drawer_detail);
        itemImage = (ImageView) findViewById(R.id.edit_item_image);
        productNameDetail = findViewById(R.id.productNameDetail);
        productManufactureDetail = findViewById(R.id.productManufactureDetail);
        productPriceDetail = findViewById(R.id.productPriceDetail);
        productBarcodeNumberDetail = findViewById(R.id.productBarcodeNumberDetail);
        productQuantityDetail = findViewById(R.id.productQuantityDetail);
        productUsageDetail = findViewById(R.id.productUsageDetail);
        productQuantityDetail.setFocusable(false);
        productQuantityDetail.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        productQuantityDetail.setClickable(false);
        productUsageDetail.setFocusable(false);
        productUsageDetail.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        productUsageDetail.setClickable(false);
        editItemButton = findViewById(R.id.editItemButton);
        editItemButton.setOnClickListener(v -> edit(v));
        imageButtonPlus = findViewById(R.id.imageButtonPlus);
        imageButtonMinus = findViewById(R.id.imageButtonMinus);
        imageButtonPlus.setClickable(false);
        imageButtonMinus.setClickable(false);
        spin = (Spinner) findViewById(R.id.spinnerusagetype);
        spin.setEnabled(false);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.UsageType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        username = findViewById(R.id.login_username);
        email = findViewById(R.id.login_email);
        setItemDetail();
        if (imagePath != null) {
            Glide.with(getApplicationContext()).load(imagePath).into(itemImage);
        }
    }

    public void setItemDetail() {
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        String shUsername = sharedpreferences.getString(MainActivity.Username, "");
        String shEmail = sharedpreferences.getString(MainActivity.Email, "");
        shUserid = sharedpreferences.getInt(MainActivity.Userid, 0);
        shProductId = sharedpreferences.getInt(ViewInventoryActivity.ProductId, 0);
        username.setText(shUsername);
        email.setText(shEmail);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                dao = new ItemDao();
                productDao = new ProductDao();
                item = dao.findItem(shUserid, shProductId);
                product = productDao.findProduct(item.getProducts_id());
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        productNameDetail.setText(product.getPname());
                        productManufactureDetail.setText(product.getManufacture());
                        productPriceDetail.setText(String.valueOf(product.getPrice()));
                        productBarcodeNumberDetail.setText(product.getBarcode());
                        productQuantityDetail.setText(String.valueOf(item.getQuantity()));
                        productUsageDetail.setText(String.valueOf(item.getUsage()));
                        int spinnerPosition = adapter.getPosition(item.getUsageType());
                        spin.setSelection(spinnerPosition);
                        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Log.d("ViewItemDetailActivity", "Usage type selected: " + parent.getSelectedItem().toString());
                                usageType = parent.getSelectedItem().toString();
                                Toast.makeText(getApplicationContext(), parent.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                Toast.makeText(getApplicationContext(), "Usage type not yet selected!", Toast.LENGTH_LONG).show();
                            }
                        });
                        imagePath = product.getImage_path();

                        Glide.with(ViewItemDetailActivity.this).load(product.getImage_path()).into(itemImage);

                    }
                });
                Looper.loop();
            }
        }.start();
    }

    public void edit(View v) {
        if (!isEditing) {
            spin.setEnabled(true);
            productQuantityDetail.setFocusable(true);
            productQuantityDetail.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
            productQuantityDetail.setClickable(true);
            productUsageDetail.setFocusable(true);
            productUsageDetail.setFocusableInTouchMode(true); // user touches widget on phone with touch screen
            productUsageDetail.setClickable(true);
            imageButtonPlus.setClickable(true);
            imageButtonPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentQuantity = Integer.parseInt(productQuantityDetail.getText().toString());
                    int newCurrentQuantity = currentQuantity + 1;
                    productQuantityDetail.setText(String.valueOf(newCurrentQuantity));
                }
            });
            imageButtonMinus.setClickable(true);
            imageButtonMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentQuantity = Integer.parseInt(productQuantityDetail.getText().toString());
                    int newCurrentQuantity = currentQuantity - 1;
                    productQuantityDetail.setText(String.valueOf(newCurrentQuantity));
                }
            });
            editItemButton.setText("Update");
            editItemButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.pink_fill_round_color));
            isEditing = true;
        } else {
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("ViewItemDetailActivity", "Usage type selected: " + parent.getSelectedItem().toString());
                    Toast.makeText(getApplicationContext(), parent.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Toast.makeText(getApplicationContext(), "Usage type not yet selected!", Toast.LENGTH_LONG).show();
                }
            });
            int cquantity = Integer.parseInt(productQuantityDetail.getText().toString());
            int cusage = Integer.parseInt(productUsageDetail.getText().toString());
            BigDecimal dailyusage;
            int usage_daily = 0;
            int usage_weekly = 0;
            int usage_biweekly = 0;
            int usage_monthly = 0;
            if (checkEnteredData()) {
                if (Objects.equals(usageType, "Daily")) {
                    dailyusage = BigDecimal.valueOf(cusage);
                    usage_daily = cusage;
                    Log.d("ViewItemDetailActivity", "Usage type selected: " + usageType);
                } else if (Objects.equals(usageType, "Weekly")) {
                    double days = 7;
                    double dailyUsage = cusage / days;
                    dailyusage = BigDecimal.valueOf(dailyUsage);
                    usage_weekly = cusage;
                    Log.d("ViewItemDetailActivity", "Usage type selected: " + usageType);
                } else if (Objects.equals(usageType, "Biweekly")) {
                    double days = 14;
                    double dailyUsage = cusage / days;
                    dailyusage = BigDecimal.valueOf(dailyUsage);
                    usage_biweekly = cusage;
                    Log.d("ViewItemDetailActivity", "Usage type selected: " + usageType);
                } else {
                    double days = 30;
                    double dailyUsage = cusage / days;
                    dailyusage = BigDecimal.valueOf(dailyUsage);
                    usage_monthly = cusage;
                    Log.d("ViewItemDetailActivity", "Usage type selected: " + usageType);
                }
                int finalUsage_daily = usage_daily;
                int finalUsage_weekly = usage_weekly;
                int finalUsage_biweekly = usage_biweekly;
                int finalUsage_monthly = usage_monthly;
                LocalDate currentDate = LocalDate.now();
                Log.d("ViewItemDetailActivity", "Current Date:" + currentDate);
                String currentDateString = currentDate.toString();
                Log.d("ViewItemDetailActivity", "Current Date To String:" + currentDateString);

                new Thread() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        if (shUserid != 0 && shProductId != 0) {
                            dao = new ItemDao();
                            boolean flag = dao.updateItem(shUserid, shProductId, cquantity, dailyusage, finalUsage_daily, finalUsage_weekly, finalUsage_biweekly, finalUsage_monthly, currentDateString, cquantity);
                            if (flag) {
                                msg.what = 1;
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {

                                        editItemButton.setText("Edit");
                                        spin.setEnabled(false);
                                        productQuantityDetail.setFocusable(false);
                                        productQuantityDetail.setFocusableInTouchMode(false);
                                        productQuantityDetail.setClickable(false);
                                        productUsageDetail.setFocusable(false);
                                        productUsageDetail.setFocusableInTouchMode(false);
                                        productUsageDetail.setClickable(false);
                                        imageButtonPlus.setClickable(false);
                                        imageButtonMinus.setClickable(false);
                                        isEditing = false;
                                    }
                                });
                            }
                            myHandler.sendMessage(msg);
                        }

                    }
                }.start();
            }

        }

    }

    final Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(getApplicationContext(), "Item updated.", Toast.LENGTH_LONG).show();
            }
        }
    };

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean checkEnteredData() {
        if (isEmpty(productQuantityDetail)) {
            Toast t = Toast.makeText(this, "Quantity is required!", Toast.LENGTH_SHORT);
            t.show();
            if (isEmpty(productUsageDetail)) {
                productUsageDetail.setError("Usage is required!");
            }
            return false;
        }
        if (isEmpty(productUsageDetail)) {
            productUsageDetail.setError("Usage is required!");
        }
        return true;
    }

    public void clickBack(View view) {
        ViewInventoryActivity.redirectActivity(this, ViewInventoryActivity.class);
    }

}