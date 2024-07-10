package com.example.personalizedinventorycontrolapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.personalizedinventorycontrolapp.dao.ItemDao;
import com.example.personalizedinventorycontrolapp.dao.ProductDao;
import com.example.personalizedinventorycontrolapp.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class AddNewProductActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    EditText productname = null;
    EditText manufacture = null;
    EditText price = null;
    EditText barcodeNumber = null;
    EditText imagePath = null;
    Button addnewProducttodbbutton;
    SharedPreferences sharedpreferences;
    public static final String ProductIDFromAddNewProduct = "ProductIdFromAddNewProductKey";
    int shUserid = 0;
    int shProductId = 0;
    String shProductName;
    ProductDao productDao;
    Product pp;
    ItemDao itemDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);
        drawerLayout=findViewById(R.id.addNewProductDrawer);
        productname = findViewById(R.id.productNameAdd);
        manufacture = findViewById(R.id.productManufactureAdd);
        price = findViewById(R.id.productPriceAdd);
        barcodeNumber = findViewById(R.id.productBarcodeNumberAdd);
        imagePath = findViewById(R.id.productImageURLAdd);
        addnewProducttodbbutton = findViewById(R.id.addnewproducttodbbutton);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        shProductId = sharedpreferences.getInt(AddInventoryActivity.ProductIDFromAddItemManually, 0);
        shProductName = sharedpreferences.getString(AddInventoryActivity.ProductName, "");
        if(shProductId != 0){
            getItemInfoFromProductID();
        }
        if(!Objects.equals(shProductName, "")){
            getItemInfo();
        }
        addnewProducttodbbutton.setOnClickListener(v -> addNewProduct(v));
    }

    public void getItemInfoFromProductID(){
        productDao = new ProductDao();
        pp = productDao.findProduct(shProductId);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(AddInventoryActivity.ProductIDFromAddItemManually,0);
        editor.apply();
        if(pp != null){
            productname.setText(pp.getPname());
            manufacture.setText(pp.getManufacture());
            price.setText(String.valueOf(pp.getPrice()));
            barcodeNumber.setText(pp.getBarcode());
            imagePath.setText(pp.getImage_path());
        }
    }

    public void getItemInfo(){
        String shManufacture = sharedpreferences.getString(AddInventoryActivity.Manufacture, "");
        String shPrice = sharedpreferences.getString(AddInventoryActivity.Price, "");
        String shBarcodeNumber = sharedpreferences.getString(AddInventoryActivity.BarcodeNumber, "");
        String shImagePath = sharedpreferences.getString(AddInventoryActivity.ImagePath, "");
        productname.setText(shProductName);
        manufacture.setText(shManufacture);
        price.setText(shPrice);
        barcodeNumber.setText(shBarcodeNumber);
        imagePath.setText(shImagePath);
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
        if (isEmpty(productname)) {
            Toast t = Toast.makeText(this, "Product name is required!!", Toast.LENGTH_SHORT);
            t.show();
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

        if (isEmpty(barcodeNumber)) {
            barcodeNumber.setError("Barcode number is required!");
            return false;
        }
        return true;
    }

    public void addNewProduct(View view) {
        String cproductname = productname.getText().toString();
        String cmanufacture = manufacture.getText().toString();
        float cprice = Float.parseFloat(price.getText().toString());
        String barcode = barcodeNumber.getText().toString();
        String image_path = imagePath.getText().toString();

        if (checkEnteredData()) {
            new Thread(() -> {
                productDao = new ProductDao();
                shUserid = sharedpreferences.getInt(MainActivity.Userid,0);
                pp = productDao.findProduct(barcode);
                int directid = 0;
                Product product = new Product();
                Message msg = new Message();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                if (pp == null) {
                    product.setPname(cproductname);
                    product.setManufacture(cmanufacture);
                    product.setPrice(cprice);
                    product.setImage_path(image_path);
                    product.setBarcode(barcode);
                    boolean ap = productDao.addProduct(product);
                    if (ap) {
                       int product_id = productDao.findProduct(barcode).getPid();
                        editor.putInt(ProductIDFromAddNewProduct, product_id);
                        editor.apply();
                        itemDao = new ItemDao();
                        int shItemQuantity = sharedpreferences.getInt(AddInventoryActivity.Quantity, 0);
                        int shItemUsage = sharedpreferences.getInt(AddInventoryActivity.Usage, 0);
                        String shItemUsageType = sharedpreferences.getString(AddInventoryActivity.UsageType, "");
                        BigDecimal dailyusage;
                        int usage_daily = 0;
                        int usage_weekly = 0;
                        int usage_biweekly = 0;
                        int usage_monthly = 0;
                        if (Objects.equals(shItemUsageType, "Daily")){
                            dailyusage = BigDecimal.valueOf(shItemUsage);
                            usage_daily = shItemUsage;
                        }else if(Objects.equals(shItemUsageType, "Weekly")){
                            double days = 7;
                            double dailyUsage = shItemUsage/days;
                            dailyusage = BigDecimal.valueOf(dailyUsage);
                            usage_weekly = shItemUsage;
                        }else if(Objects.equals(shItemUsageType, "Biweekly")){
                            double days = 14;
                            double dailyUsage = shItemUsage/days;
                            dailyusage = BigDecimal.valueOf(dailyUsage);
                            usage_biweekly = shItemUsage;
                        }else{
                            double days = 30;
                            double dailyUsage = shItemUsage/days;
                            dailyusage = BigDecimal.valueOf(dailyUsage);
                            usage_monthly = shItemUsage;
                        }

                        int finalUsage_daily = usage_daily;
                        int finalUsage_weekly = usage_weekly;
                        int finalUsage_biweekly = usage_biweekly;
                        int finalUsage_monthly = usage_monthly;
                        LocalDate currentDate = LocalDate.now();
                        Log.d("AddInventoryActivity", "Current Date:"+ currentDate);
                        String currentDateString = currentDate.toString();
                        Log.d("AddInventoryActivity", "Current Date To String:"+ currentDateString);
                        Log.d("AddInventoryActivity", "Data"+ shItemQuantity + " " + dailyusage);
                        boolean flag = itemDao.addItem(shUserid, product_id, shItemQuantity, dailyusage, finalUsage_daily, finalUsage_weekly, finalUsage_biweekly, finalUsage_monthly, currentDateString, shItemQuantity);
                        if(flag){
                            msg.what = 2;
                            directid = 2;
                        }

                    }
                }else{
                    msg.what = 1;
                    directid = 1;
                }
                myHandler.sendMessage(msg);
                switch (directid){
                    case 1:
                        ViewInventoryActivity.redirectActivity(AddNewProductActivity.this, AddInventoryWithScanActivity.class);
                        break;
                    case 2:
                        startActivity(new Intent(AddNewProductActivity.this, ViewInventoryActivity.class));
                        break;
                }
            }).start();
        }
    }

    final Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getApplicationContext(), "Product already exist. You can add inventory by scanning.", Toast.LENGTH_LONG).show();
                    break;

                case 2:
                    Toast.makeText(getApplicationContext(), "Add new Product successfully and item is also added to the inventory record.", Toast.LENGTH_LONG).show();
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