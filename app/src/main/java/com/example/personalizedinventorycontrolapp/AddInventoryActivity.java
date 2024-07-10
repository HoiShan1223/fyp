package com.example.personalizedinventorycontrolapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.personalizedinventorycontrolapp.dao.ItemDao;
import com.example.personalizedinventorycontrolapp.dao.ProductDao;
import com.example.personalizedinventorycontrolapp.entity.Item;
import com.example.personalizedinventorycontrolapp.entity.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class AddInventoryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DrawerLayout drawerLayout;
    EditText itemName = null;
    EditText manufacture = null;
    EditText price = null;
    EditText quantity = null;
    EditText usage = null;
    Button additemtodbbutton;
    EditText barcodeNumber = null;
    TextView username;
    TextView email;
    FloatingActionButton addImageButton;
    ImageView productImageAdd;
    String usagetype = null;
    SharedPreferences sharedpreferences;
    public static final String ProductIDFromAddItemManually = "ProductIdFromAddNewProductManuallyKey";
    public static final String ProductName = "ProductNameKey";
    public static final String Manufacture = "ManufactureKey";
    public static final String Price = "PriceKey";
    public static final String BarcodeNumber = "BarcodeNumberKey";
    public static final String ImagePath = "ImagePathKey";
    public static final String Quantity = "ItemQuantityKey";
    public static final String Usage = "ItemUsageKey";
    public static final String UsageType = "ItemUsageTypeKey";
    int shUserid = 0;
    ProductDao productDao;
    ItemDao itemDao;
    Product pp;
    Item ii;
    String imageURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory);
        drawerLayout = findViewById(R.id.addItemManuallyDrawer);
        itemName = findViewById(R.id.productNameManually);
        manufacture = findViewById(R.id.productManufactureManually);
        price = findViewById(R.id.productPriceManually);
        quantity = findViewById(R.id.productQuantityManually);
        usage = findViewById(R.id.productUsageManually);
        barcodeNumber = findViewById(R.id.productBarcodeNumberManually);
        additemtodbbutton = findViewById(R.id.addItemManuallyButton);
        addImageButton = findViewById(R.id.addImage);
        productImageAdd = findViewById(R.id.productImageAdd);
        addImageButton.setOnClickListener(view -> dialog());
        Spinner spin = findViewById(R.id.spinnerusagetypeManually);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.UsageType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        username = findViewById(R.id.login_username);
        email = findViewById(R.id.login_email);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        String shUsername = sharedpreferences.getString(MainActivity.Username, "");
        String shEmail = sharedpreferences.getString(MainActivity.Email, "");
        username.setText(shUsername);
        email.setText(shEmail);
        additemtodbbutton.setOnClickListener(v -> addItem(v));
    }

    public void dialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        final EditText imageUrlInputEditText = new EditText(AddInventoryActivity.this);
        builder.setView(imageUrlInputEditText);
        builder.setTitle("Please enter the image url");
        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        builder.setCancelable(false);
        final androidx.appcompat.app.AlertDialog alertDialogAndroid = builder.create();
        alertDialogAndroid.show();
        Button positiveButton = alertDialogAndroid.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            boolean isError = false;

            if (imageUrlInputEditText.getText().toString().equals("")) {
                isError = true;
                imageUrlInputEditText.setError("Please enter url!");
            }
            if (!isError) {
                new Thread(() -> {
                    imageURL = imageUrlInputEditText.getText().toString();
                    alertDialogAndroid.dismiss();
                    runOnUiThread(() -> Glide.with(AddInventoryActivity.this).load(imageUrlInputEditText.getText().toString()).into(productImageAdd));
                }).start();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        usagetype = parent.getSelectedItem().toString();
        Toast.makeText(getApplicationContext(),parent.getSelectedItem().toString() , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(), "Usage type not yet selected!", Toast.LENGTH_LONG).show();
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean isEmptyNumber(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    private boolean checkEnteredData() {
        if (isEmpty(itemName)) {
            Toast t = Toast.makeText(this, "Item name is required!!", Toast.LENGTH_SHORT);
            t.show();
            return false;
        }
        if(isEmpty(manufacture) && isEmptyNumber(price) && isEmpty(barcodeNumber) && isEmpty(quantity) && isEmpty(usage)){
            manufacture.setError("Manufacture is required!");
            price.setError("Price is required!");
            barcodeNumber.setError("Barcode number is required!");
            quantity.setError("Quantity is required!");
            usage.setError("Usage is required!");
            return false;
        }else if(isEmptyNumber(price) && isEmpty(barcodeNumber) && isEmpty(quantity) && isEmpty(usage)){
            price.setError("Price is required!");
            barcodeNumber.setError("Barcode number is required!");
            quantity.setError("Quantity is required!");
            usage.setError("Usage is required!");
            return false;
        }else if(isEmpty(barcodeNumber) && isEmpty(quantity) && isEmpty(usage)){
            barcodeNumber.setError("Barcode number is required!");
            quantity.setError("Quantity is required!");
            usage.setError("Usage is required!");
            return false;
        }else if (isEmpty(quantity) && isEmpty(usage)){
            quantity.setError("Quantity is required!");
            usage.setError("Usage is required!");
            return false;
        }

        if (isEmpty(manufacture)) {
            manufacture.setError("Manufacture is required!");
            return false;
        }

        if (isEmptyNumber(price)) {
            price.setError("Price is required!");
            return false;
        }

        if (isEmpty(quantity)) {
            quantity.setError("Quantity is required!");
            return false;
        }

        if (isEmpty(usage)) {
            usage.setError("Usage is required!");
            return false;
        }

        if (isEmpty(barcodeNumber)) {
            barcodeNumber.setError("Barcode number is required!");
            return false;
        }

        return true;
    }

    public void addItem(View view) {
        if(checkEnteredData()){
        String citemname = itemName.getText().toString();
        String cmanufacture = manufacture.getText().toString();
        String cprice = price.getText().toString();
        int cquantity = Integer.parseInt(quantity.getText().toString());
        int cusage = Integer.parseInt(usage.getText().toString());
        //get item barcode
        String barcode = barcodeNumber.getText().toString();
        BigDecimal dailyusage;
        int usage_daily = 0;
        int usage_weekly = 0;
        int usage_biweekly = 0;
        int usage_monthly = 0;
            shUserid = sharedpreferences.getInt(MainActivity.Userid,0);

            if (Objects.equals(usagetype, "Daily")){
                dailyusage = BigDecimal.valueOf(cusage);
                usage_daily = cusage;
            }else if(Objects.equals(usagetype, "Weekly")){
                double days = 7;
                double dailyUsage = cusage/days;
                dailyusage = BigDecimal.valueOf(dailyUsage);
                usage_weekly = cusage;
            }else if(Objects.equals(usagetype, "Biweekly")){
                double days = 14;
                double dailyUsage = cusage/days;
                dailyusage = BigDecimal.valueOf(dailyUsage);
                usage_biweekly = cusage;
            }else{
                double days = 30;
                double dailyUsage = cusage/days;
                dailyusage = BigDecimal.valueOf(dailyUsage);
                usage_monthly = cusage;
            }

            int finalUsage_daily = usage_daily;
            int finalUsage_weekly = usage_weekly;
            int finalUsage_biweekly = usage_biweekly;
            int finalUsage_monthly = usage_monthly;
            LocalDate currentDate = LocalDate.now();
            Log.d("AddInventoryActivity", "Current Date:"+ currentDate);
            String currentDateString = currentDate.toString();
            Log.d("AddInventoryActivity", "Current Date To String:"+ currentDateString);

            new Thread(() -> {
                productDao = new ProductDao();
                pp = productDao.findProduct(barcode);

                Message msg = new Message();
                int directid = 0;
                if(imageURL.equals("")){
                    msg.what = 3;
                }
                if(pp != null){
                    int product_id = pp.getPid();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt(ProductIDFromAddItemManually,0);
                    editor.apply();
                    itemDao = new ItemDao();
                    ii = itemDao.findItem(shUserid, product_id);
                    if(ii != null){
                        if(!Objects.equals(ii.getDailyusage(), dailyusage)){
                            AlertDialog.Builder builder=new AlertDialog.Builder(AddInventoryActivity.this);
                            builder.setTitle("Usage Error");
                            builder.setMessage("Item already recorded in database. However, usage just entered is different from the recorded one. " +
                                    "Would you like to update the usage to the one just entered or keep the old usage" + ii.getUsage() + " " + ii.getUsageType());
                            builder.setPositiveButton("Update", (dialogInterface, i) -> {
                                boolean flag = itemDao.updateItem(shUserid, product_id, cquantity, dailyusage, finalUsage_daily, finalUsage_weekly, finalUsage_biweekly, finalUsage_monthly,currentDateString, cquantity);
                                if(flag){
                                    Toast.makeText(getApplicationContext(),"Updated quantity.",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(AddInventoryActivity.this, ViewInventoryActivity.class));
                                }
                            });
                            builder.setNegativeButton("No change", (dialogInterface, i) -> {
                                boolean flag = itemDao.updateItem(shUserid, product_id, cquantity, ii.getDailyusage(), ii.getUsage_daily(), ii.getUsage_weekly(), ii.getUsage_biweekly(), ii.getUsage_monthly(), ii.getStock_time(), cquantity);
                                if(flag){
                                    Toast.makeText(getApplicationContext(),"Updated quantity and usage.",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(AddInventoryActivity.this, ViewInventoryActivity.class));
                                }
                            });
                            builder.show();
                        }
                    }else{
                        boolean flag = itemDao.addItem(shUserid, product_id, cquantity, dailyusage, finalUsage_daily, finalUsage_weekly, finalUsage_biweekly, finalUsage_monthly, currentDateString, cquantity);
                        if(flag){
                            msg.what = 2;
                            directid = 2;
                        }
                    }
                }else{
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(ProductName, citemname);
                    editor.putString(Manufacture, cmanufacture);
                    editor.putString(Price, cprice);
                    editor.putString(BarcodeNumber, barcode);
                    editor.putString(ImagePath, imageURL);
                    editor.putInt(Quantity, cquantity);
                    editor.putInt(Usage, cusage);
                    editor.putString(UsageType, usagetype);
                    editor.apply();
                    msg.what = 1;
                    directid = 1;
                }
                myHandler.sendMessage(msg);

                switch (directid){
                    case 1:
                        Intent intent2 = new Intent(AddInventoryActivity.this, AddNewProductActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        startActivity(new Intent(AddInventoryActivity.this, ViewInventoryActivity.class));
                        break;
                }
            }).start();
        }


    }

    final Handler myHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Toast.makeText(getApplicationContext(),"Item not in database! Please add new product",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(),"Item added to inventory!",Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(),"Item image url is missing!",Toast.LENGTH_LONG).show();
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
        ViewInventoryActivity.redirectActivity(this,ScanInventoriesActivity.class);
    }

    public  void clickAddInventoryManually(View view){
        recreate();
    }

    public void clickShoppingList(View view){
        ViewInventoryActivity.redirectActivity(this,ViewShoppingListActivity.class);
    }

    public void clickSettings(View view){
        ViewInventoryActivity.redirectActivity(this,EditSettingActivity.class);
    }

    public  void ClickLogout(View view){
        ViewInventoryActivity.logout(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        ViewInventoryActivity.closeDrawer(drawerLayout);
    }
}