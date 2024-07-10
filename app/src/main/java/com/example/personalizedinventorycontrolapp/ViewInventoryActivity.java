package com.example.personalizedinventorycontrolapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalizedinventorycontrolapp.adapter.gridAdapter;
import com.example.personalizedinventorycontrolapp.dao.ItemDao;
import com.example.personalizedinventorycontrolapp.dao.ProductDao;
import com.example.personalizedinventorycontrolapp.entity.Item;
import com.example.personalizedinventorycontrolapp.entity.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ViewInventoryActivity extends AppCompatActivity{

    DrawerLayout drawerLayout;
    List<Item> itemList = new ArrayList<>();
    FloatingActionButton addbutton;
    TextView username;
    TextView email;
    SharedPreferences sharedpreferences;
    private static final String TAG = "ViewInventoryActivity";
    public static final String ProductId = "productIdKey";
    gridAdapter adapter;
    int shUserid = 0;
    ItemDao itemDao;
    ProductDao productDao;
    Product product;
    String itemName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);
        drawerLayout=findViewById(R.id.drawer);
        addbutton = findViewById(R.id.add);
        username = findViewById(R.id.login_username);
        email =findViewById(R.id.login_email);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        String shUsername = sharedpreferences.getString(MainActivity.Username, "");
        String shEmail = sharedpreferences.getString(MainActivity.Email, "");
        username.setText(shUsername);
        email.setText(shEmail);
        getItems();
    }

    private final Handler myHandler = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg){
            if (msg.what == 1) {
                Log.d(TAG, "run: Ui update successfully.");
                RecyclerView recyclerView = findViewById(R.id.recyclerview);
                GridLayoutManager Manager = new GridLayoutManager(ViewInventoryActivity.this,2);
                recyclerView.setLayoutManager(Manager);
                adapter = new gridAdapter(ViewInventoryActivity.this, itemList);
                adapter.setOnItemClickListener((view, position) -> {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putInt(ProductId,itemList.get(position).getProducts_id());
                    editor.apply();
                    startActivity(new Intent(ViewInventoryActivity.this, ViewItemDetailActivity.class));
                    Toast.makeText(ViewInventoryActivity.this, "click " + itemList.get(position).getItemName(), Toast.LENGTH_SHORT).show();
                });
                adapter.setOnItemLongClickListener((view, position) -> {
                    removeItem(itemList.get(position));
                    Toast.makeText(ViewInventoryActivity.this,"long click "+itemList.get(position).getItemName(),Toast.LENGTH_SHORT).show();

                });
                recyclerView.setAdapter(adapter);
            }else if(msg.what == 2){
                Toast.makeText(ViewInventoryActivity.this, itemName + " removed", Toast.LENGTH_SHORT).show();
            }
        }
    };
    void removeItem(Item item) {
        android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(this);
        builder.setTitle("Remove Item");
        builder.setMessage("Are you sure you want to remove " + item.getItemName()+ "?");
        builder.setPositiveButton("Yes", (dialogInterface, i) -> {
            new Thread(() -> {
                itemDao = new ItemDao();
                String itemBarcode = item.getItemBarcode();
                productDao = new ProductDao();
                product = productDao.findProduct(itemBarcode);
                int pid = product.getPid();
                shUserid = sharedpreferences.getInt(MainActivity.Userid,0);
                Message msg = new Message();
                boolean flag = itemDao.deleteItem(shUserid,pid);

                int directID = 0;
                if(flag){
                    directID = 1;
                    itemName = item.getItemName();
                    msg.what = 2;
//                    Looper.prepare();
//                    Toast.makeText(ViewInventoryActivity.this, item.getItemName() + " removed", Toast.LENGTH_SHORT).show();
//                    Looper.loop();
                }
                myHandler.sendMessage(msg);
                if(directID == 1){
                    redirectActivity(this, ViewInventoryActivity.class);
                }
            }).start();
        });
        builder.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.show();
    }

    public void initPopWindow(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) ViewInventoryActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.add_popup,null);
        Button addByScan = customView.findViewById(R.id.AddByScan);
        Button addManually = customView.findViewById(R.id.AddManuallyButton);

        final PopupWindow popWindow = new PopupWindow(customView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.anim_popup);
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor((v1, event) -> false);
        popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));


        popWindow.showAsDropDown(v, 0, -400);

        addByScan.setOnClickListener(v12 -> startActivity(new Intent(ViewInventoryActivity.this,ScanInventoriesActivity.class)));
        addManually.setOnClickListener(v13 -> {
            startActivity(new Intent(ViewInventoryActivity.this,AddInventoryActivity.class));
            popWindow.dismiss();
        });
    }

    private void getItems() {
        new Thread() {
            @Override
            public void run() {
                sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
                int user_id = sharedpreferences.getInt(MainActivity.Userid, 0);
                ItemDao itemdao = new ItemDao();
                itemList = itemdao.list(user_id);
                Log.d(TAG, "run: Database read successfully.");
                Message msg = new Message();
                msg.what = 1;
                myHandler.sendMessage(msg);
                Log.d(TAG, "run: Message msg.what sent successfully.");
            }
        }.start();
    }
    public  void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {

        drawerLayout.openDrawer(GravityCompat.START);

    }
    public void ClickLogo(View view){
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }

    }
    public void clickInventoryRecord(View view){
        recreate();
    }

    public void clickToolBarLogo(View view){
        recreate();
    }

    public void clickAddInventoryWithScan(View view){
        redirectActivity(this,ScanInventoriesActivity.class);
    }

    public void clickAddInventoryManually(View view){
        redirectActivity(this,AddInventoryActivity.class);
    }

    public void clickShoppingList(View view){
       redirectActivity(this,ViewShoppingListActivity.class);
    }

    public void clickSettings(View view){
        redirectActivity(this,EditSettingActivity.class);
    }

    public  void ClickLogout(View view){
        logout(this);
    }

    public static void logout(Activity activity) {
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        String yes = "Yes";
        String no = "No";
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finishAffinity();
                System.exit(0);
            }
        });
        builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    public static void redirectActivity(Activity activity,Class Class) {
        Intent intent = new Intent(activity,Class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}