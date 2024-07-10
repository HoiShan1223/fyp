package com.example.personalizedinventorycontrolapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.personalizedinventorycontrolapp.adapter.familyShoppingListAdapter;
import com.example.personalizedinventorycontrolapp.adapter.myAdapter;
import com.example.personalizedinventorycontrolapp.dao.GroupDao;
import com.example.personalizedinventorycontrolapp.dao.ItemDao;
import com.example.personalizedinventorycontrolapp.dao.ProductDao;
import com.example.personalizedinventorycontrolapp.dao.ShoppingListDao;
import com.example.personalizedinventorycontrolapp.entity.Group;
import com.example.personalizedinventorycontrolapp.entity.Item;
import com.example.personalizedinventorycontrolapp.entity.ShoppingList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class ViewShoppingListActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView username;
    TextView email;
    List<ShoppingList> ShoppingList = new ArrayList<>();
    List<ShoppingList> LatestShoppingList = new ArrayList<>();
    List<ShoppingList> familyAccountShoppingList = new ArrayList<>();
    List<Group> groupList = new ArrayList<>();
    Button updateInventoryButton;
    myAdapter adapter;
    com.example.personalizedinventorycontrolapp.adapter.familyShoppingListAdapter familyShoppingListAdapter;
    String shUsername;
    String shEmail;
    private static final String TAG = "ViewShoppingListActivity";
    SharedPreferences sharedpreferences;
    int shUserid = 0;
    ShoppingListDao shoppingListDao;
    ItemDao itemDao;
    Item thatItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_shopping_list);
        drawerLayout = findViewById(R.id.drawer);
        username = findViewById(R.id.login_username);
        email = findViewById(R.id.login_email);
        updateInventoryButton = findViewById(R.id.updateInventoryButton);
        updateInventoryButton.setVisibility(View.INVISIBLE);
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, MODE_PRIVATE);
        shUsername = sharedpreferences.getString(MainActivity.Username, "");
        shEmail = sharedpreferences.getString(MainActivity.Email, "");
        username.setText(shUsername);
        email.setText(shEmail);
        getShoppingListItems();

        updateInventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInventory(v);
            }
        });
    }

    public void getShoppingListItems() {
        new Thread(() -> {
            sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
            int user_id = sharedpreferences.getInt(MainActivity.Userid, 0);
            ShoppingListDao shoppingListDao = new ShoppingListDao();
            ShoppingList = shoppingListDao.getShoppingList(user_id);
            GroupDao groupDao = new GroupDao();
            Group group = groupDao.findGroup(shEmail);
            Message msg = new Message();
            if (group != null) {
                int gid = group.getGid();
                groupList = groupDao.list(gid);
            }
            ShoppingList.forEach(shoppingListItem -> {
                String itemName = shoppingListItem.getItemName();
                boolean restock = shoppingListItem.isRestock();
                Log.d(TAG, "Item: " + itemName + ", Restock condition: " + restock);
                if (!restock) {
                    LatestShoppingList.add(shoppingListItem);
                }
            });
            if (LatestShoppingList.size() != 0) {
                msg.what = 1;
                if (groupList.size() != 0) {
                    linkShoppingListToFamilyAccount(shoppingListDao, groupList);
                    ListIterator<Group> iterator = groupList.listIterator();
                    int number = 0;
                    while (iterator.hasNext()) {
                        List<ShoppingList> familyShoppingList = iterator.next().getShoppingList();
                        if (familyShoppingList != null) {
                            number++;
                        }
                    }
                    if (!iterator.hasNext() && number != 0) {
                        msg.what = 2;
                    }

                }
            } else {
                if (groupList.size() != 0) {
                    linkShoppingListToFamilyAccount(shoppingListDao, groupList);
                    ListIterator<Group> iterator = groupList.listIterator();
                    int number = 0;
                    while (iterator.hasNext()) {
                        List<ShoppingList> familyShoppingList = iterator.next().getShoppingList();
                        if (familyShoppingList.size() != 0) {
                            number++;
                        }
                    }
                    if (!iterator.hasNext() && number != 0) {
                        msg.what = 2;
                    }

                }
            }
            Log.d(TAG, "run: Database read successfully.");
            myHandler.sendMessage(msg);
            Log.d(TAG, "run: Message msg.what sent successfully.");

        }).start();
    }

    public final Handler myHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.d(TAG, "run: Ui update successfully.");
                RecyclerView recyclerView = findViewById(R.id.recyclerviewshoppinglist);
                if (LatestShoppingList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    updateInventoryButton.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    updateInventoryButton.setVisibility(View.VISIBLE);
                }
                LinearLayoutManager Manager = new LinearLayoutManager(ViewShoppingListActivity.this);
                recyclerView.setLayoutManager(Manager);
                adapter = new myAdapter(ViewShoppingListActivity.this, LatestShoppingList);
                recyclerView.setAdapter(adapter);
            } else if (msg.what == 2) {
                Log.d(TAG, "run: Ui update successfully.");
                RecyclerView recyclerView = findViewById(R.id.recyclerviewshoppinglist);
                if (LatestShoppingList.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    updateInventoryButton.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    updateInventoryButton.setVisibility(View.VISIBLE);
                }
                LinearLayoutManager Manager = new LinearLayoutManager(ViewShoppingListActivity.this);
                recyclerView.setLayoutManager(Manager);
                adapter = new myAdapter(ViewShoppingListActivity.this, LatestShoppingList);
                recyclerView.setAdapter(adapter);
                RecyclerView nestedRecyclerView = findViewById(R.id.recyclerviewshoppinglist2);
                nestedRecyclerView.setLayoutManager(new LinearLayoutManager(ViewShoppingListActivity.this));
                familyShoppingListAdapter = new familyShoppingListAdapter(ViewShoppingListActivity.this, groupList);
                nestedRecyclerView.setAdapter(familyShoppingListAdapter);
            }
        }
    };

    public void linkShoppingListToFamilyAccount(ShoppingListDao shoppingListDao, List<Group> groupList){
        groupList.forEach(familyAccount -> {
            int familyAccountUid = familyAccount.getUsers_id();

            familyAccountShoppingList = shoppingListDao.getShoppingList(familyAccountUid);
            familyAccount.setShoppingList(familyAccountShoppingList);
        });
    }

    //when pressed the update button after purchase
    private void updateInventory(View view) {
        new Thread() {
            @Override
            public void run() {
                ListIterator<ShoppingList> iterator = ShoppingList.listIterator();
                while (iterator.hasNext()) {
                    ShoppingList shoppingListItem = iterator.next();
                    shoppingListItem.setRestock(true);
                    int pid = shoppingListItem.getProducts_id();
                    shUserid = sharedpreferences.getInt(MainActivity.Userid, 0);
                    shoppingListDao = new ShoppingListDao();
                    itemDao = new ItemDao();
                    thatItem = itemDao.findItem(shUserid, pid); //the original data of that item in DB
                    if (thatItem != null) {
                        // Quantity purchased
                        int purchasedQuantity = shoppingListItem.getPurchaseQuantity();

                        // Quantity of the item in DB
                        int oldQuantity = thatItem.getQuantity();

                        //Daily Usage of that item in DB
                        BigDecimal dailyUsage = thatItem.getDailyusage();

                        //Calculation of Stock remaining until restock
                        LocalDate currentDate = LocalDate.now();
                        String currentDateString = currentDate.toString();
                        Log.d(TAG, "Restock time:  " + currentDateString);
                        String stock_time = thatItem.getStock_time();
                        LocalDate stockDay = LocalDate.parse(stock_time);
                        long days = ChronoUnit.DAYS.between(stockDay, currentDate);
                        BigDecimal usage = dailyUsage.multiply(BigDecimal.valueOf(days)).setScale(0, RoundingMode.DOWN);

                        //Calculate the stock remain
                        BigDecimal stockLeftToday = BigDecimal.valueOf(oldQuantity).subtract(usage);

                        int remainingStock = shoppingListItem.getRemainingStock();
                        Log.d(TAG, "stock left " + stockLeftToday);
                        int result = stockLeftToday.compareTo(BigDecimal.valueOf(remainingStock));
                        int stockLeftInNumber = stockLeftToday.intValue();
                        int restockQuantity = 0;
                        if (result > 0) { // Case 1 -- meaning early purchase
                            restockQuantity = stockLeftInNumber + purchasedQuantity;
                        } else if (result == 0) { //Case 2 -- purchase on the notification day
                            restockQuantity = remainingStock;
                        }
                        if (restockQuantity != 0) {
                            boolean updateToDB = updateToDB(shoppingListDao, pid, shoppingListItem, thatItem, itemDao, currentDateString, restockQuantity);
                            if (updateToDB) {
                                Log.d(TAG, "Item name :" + shoppingListItem.getItemName() + "restock and quantity updated");
                            }
                        }
                    }

                }
                if (!iterator.hasNext()) {
                    Looper.prepare();
                    Toast t = Toast.makeText(getApplicationContext(), "Stock updated", Toast.LENGTH_SHORT);
                    t.show();
                    startActivity(new Intent(ViewShoppingListActivity.this, ViewInventoryActivity.class));
                    Looper.loop();

                }
            }
        }.start();

    }

    private boolean updateToDB(ShoppingListDao shoppingListDao, int pid, ShoppingList shoppingListItem, Item thatItem, ItemDao itemDao, String currentDateString, int restockQuantity) {
        boolean updateShoppingList = shoppingListDao.updateItemOnShoppingList(shUserid, pid, shoppingListItem.getItems_id(), shoppingListItem.getItemPurchasePrice(), shoppingListItem.getPurchaseQuantity(), shoppingListItem.isRestock());
        if (updateShoppingList) {
            Log.d(TAG, "Stock " + shoppingListItem.getItemName() + " with Quantity: " + restockQuantity + " updated with restock: " + shoppingListItem.isRestock());
            thatItem.setStockCondition(false);
            boolean update = itemDao.updateItem(shUserid, pid, restockQuantity, restockQuantity, currentDateString, thatItem.isStockCondition()); //Also update remaining stock in itemDao
            if (update) {
                Log.d(TAG, "Stock " + shoppingListItem.getItemName() + " with Quantity: " + shoppingListItem.getPurchaseQuantity() + " updated");
                return true;
            }
        }
        return false;
    }

    public void ClickMenu(View view) {
        ViewInventoryActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        ViewInventoryActivity.closeDrawer(drawerLayout);
    }

    public void clickToolBarLogo(View view) {
        ViewInventoryActivity.redirectActivity(this, ViewInventoryActivity.class);
    }

    public void clickInventoryRecord(View view) {
        ViewInventoryActivity.redirectActivity(this, ViewInventoryActivity.class);
    }

    public void clickAddInventoryWithScan(View view) {
        ViewInventoryActivity.redirectActivity(this, AddInventoryWithScanActivity.class);
    }

    public void clickAddInventoryManually(View view) {
        ViewInventoryActivity.redirectActivity(this, AddInventoryActivity.class);
    }

    public void clickShoppingList(View view) {
        recreate();
    }

    public void clickSettings(View view) {
        ViewInventoryActivity.redirectActivity(this, EditSettingActivity.class);
    }

    public void ClickLogout(View view) {
        ViewInventoryActivity.logout(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        ViewInventoryActivity.closeDrawer(drawerLayout);
    }
}