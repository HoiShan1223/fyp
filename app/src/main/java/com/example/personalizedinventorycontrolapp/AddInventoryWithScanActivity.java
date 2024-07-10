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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class AddInventoryWithScanActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DrawerLayout drawerLayout;
    ImageView itemImage;
    TextView itemName = null;
    TextView manufacture = null;
    TextView price = null;
    EditText quantity = null;
    EditText usage = null;
    Button additembuttontodb;
    TextView barcodeView;
    TextView username;
    TextView email;
    String usagetype = null;
    SharedPreferences sharedpreferences;
    int shUserid = 0;
    String shBarcodeNumber = null;
    int shProductId = 0;
    String imagePath;
    ProductDao productDao;
    ItemDao itemDao;
    Product pp;
    Item ii;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inventory_with_scan);
        drawerLayout=findViewById(R.id.addItemWithScanDrawer);
        itemImage = findViewById(R.id.productImage);
        itemName = findViewById(R.id.productNameByScan);
        manufacture = findViewById(R.id.productManufactureByScan);
        price = findViewById(R.id.productPriceByScan);
        quantity = findViewById(R.id.productQuantityByScan);
        usage = findViewById(R.id.productUsageByScan);
        barcodeView = findViewById(R.id.productBarcodeNumberByScan);
        additembuttontodb = findViewById(R.id.addItemButton);
        Spinner spin = findViewById(R.id.spinnerusagetypeByScan);
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
        getItemInfoFromScan();
        additembuttontodb.setOnClickListener(v -> addItem(v));
        if(!Objects.equals(imagePath, "")){
            Glide.with(AddInventoryWithScanActivity.this).load(imagePath).into(itemImage);
        }
    }

    public void getItemInfoFromScan(){
        new Thread(() -> {
            shBarcodeNumber = sharedpreferences.getString(ScanInventoriesActivity.BarcodeNumber, "");
            shProductId = sharedpreferences.getInt(AddNewProductActivity.ProductIDFromAddNewProduct, 0);
            pp = new Product();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            int directID = 0;
            if (shBarcodeNumber != null) {
                productDao = new ProductDao();
                pp = productDao.findProduct(shBarcodeNumber);
                editor.putString(ScanInventoriesActivity.BarcodeNumber, "");
                editor.apply();
            } else if (shProductId != 0) {
                productDao = new ProductDao();
                pp = productDao.findProduct(shProductId);
                editor.putInt(AddNewProductActivity.ProductIDFromAddNewProduct, 0);
                editor.apply();
                if(pp == null){
                    directID = 1;
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "Cannot recognize the item. Please add item manually.", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
            if (pp != null) {
                String citemname = pp.getPname();
                itemName.setText(citemname);
                String cmanufacture = pp.getManufacture();
                manufacture.setText(cmanufacture);
                Float cprice = pp.getPrice();
                price.setText(String.valueOf(cprice));
                String barcode = pp.getBarcode();
                barcodeView.setText(barcode);
                imagePath = pp.getImage_path();
                runOnUiThread(() -> Glide.with(AddInventoryWithScanActivity.this).load(imagePath).into(itemImage));
            }
            if(directID == 1){
                ViewInventoryActivity.redirectActivity(this, AddInventoryActivity.class);
            }
        }).start();
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

    private boolean checkEnteredData() {

        if (isEmpty(quantity)) {
            quantity.setError("Quantity is required!");
            return false;
        }

        if (isEmpty(usage)) {
            usage.setError("Usage is required!");
            return false;
        }
        return true;
    }

    public void addItem(View view) {
        shUserid = sharedpreferences.getInt(MainActivity.Userid,0);
        int cquantity = Integer.parseInt(quantity.getText().toString());
        int cusage = Integer.parseInt(usage.getText().toString());
        BigDecimal dailyusage;
        int usage_daily = 0;
        int usage_weekly = 0;
        int usage_biweekly = 0;
        int usage_monthly = 0;
        if (checkEnteredData()) {
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
            Log.d("AddInventoryWithScanActivity", "Current Date:"+ currentDate);
            String currentDateString = currentDate.toString();
            Log.d("AddInventoryWithScanActivity", "Current Date To String:"+ currentDateString);

            new Thread(() -> {
                productDao = new ProductDao();
                pp = productDao.findProduct(shBarcodeNumber);

                Message msg = new Message();
                int directid = 0;
                if (pp != null) {
                    int product_id = pp.getPid();
                    itemDao = new ItemDao();
                    ii = itemDao.findItem(shUserid, product_id);
                    if(ii != null){
                        if(!Objects.equals(ii.getDailyusage(), dailyusage)){
                            AlertDialog.Builder builder=new AlertDialog.Builder(AddInventoryWithScanActivity.this);
                            builder.setTitle("Usage Error");
                            builder.setMessage("Item already recorded in database. However, usage just entered is different from the recorded one. " +
                                    "Would you like to update the usage to the one just entered or keep the old usage" + ii.getUsage() + " " + ii.getUsageType());
                            builder.setPositiveButton("Update", (dialogInterface, i) -> {
                                boolean flag = itemDao.updateItem(shUserid, product_id, cquantity, dailyusage, finalUsage_daily, finalUsage_weekly, finalUsage_biweekly, finalUsage_monthly,currentDateString, cquantity);
                                if(flag){
                                    Toast.makeText(getApplicationContext(),"Updated quantity.",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(AddInventoryWithScanActivity.this, ViewInventoryActivity.class));
                                }
                            });
                            builder.setNegativeButton("No change", (dialogInterface, i) -> {
                                boolean flag = itemDao.updateItem(shUserid, product_id, cquantity, ii.getDailyusage(), ii.getUsage_daily(), ii.getUsage_weekly(), ii.getUsage_biweekly(), ii.getUsage_monthly(), ii.getStock_time(), cquantity);
                                if(flag){
                                    Toast.makeText(getApplicationContext(),"Updated quantity and usage.",Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(AddInventoryWithScanActivity.this, ViewInventoryActivity.class));
                                }
                            });
                            builder.show();
                        }
                    }else{
                        boolean flag = itemDao.addItem(shUserid, product_id, cquantity, dailyusage, finalUsage_daily, finalUsage_weekly, finalUsage_biweekly, finalUsage_monthly,currentDateString, cquantity);
                        if(flag){
                            msg.what = 2;
                            directid = 2;
                        }
                    }
                }else {
                    msg.what = 1;
                    directid = 1;
                }
                myHandler.sendMessage(msg);

                switch (directid){
                    case 1:
                        startActivity(new Intent(AddInventoryWithScanActivity.this, AddInventoryActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(AddInventoryWithScanActivity.this, ViewInventoryActivity.class));
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
                    Toast.makeText(getApplicationContext(), "Cannot recognize the item. Please add item manually.", Toast.LENGTH_LONG).show();
                    break;
                case 2 :
                    Toast.makeText(getApplicationContext(), "Item successfully added to the Inventory!", Toast.LENGTH_LONG).show();
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
        recreate();
    }

    public  void clickAddInventoryManually(View view){
        ViewInventoryActivity.redirectActivity(this,AddInventoryActivity.class);
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