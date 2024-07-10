package com.example.personalizedinventorycontrolapp.worker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.personalizedinventorycontrolapp.dao.ItemDao;
import com.example.personalizedinventorycontrolapp.dao.NotificationDao;
import com.example.personalizedinventorycontrolapp.dao.ShoppingListDao;
import com.example.personalizedinventorycontrolapp.dao.UserDao;
import com.example.personalizedinventorycontrolapp.entity.Item;
import com.example.personalizedinventorycontrolapp.entity.Notification;
import com.example.personalizedinventorycontrolapp.entity.ShoppingList;
import com.example.personalizedinventorycontrolapp.entity.User;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CheckStockConditionWorker extends Worker {
    private static final String TAG = "CheckStockConditionWorker";
    List<User> userList = new ArrayList<>();
    String productName;
    private static final String LEGACY_SERVER_KEY = "AAAAPDbqf6Y:APA91bEeOynmp-07posWCNHobfsjKg5mYIv28qoWwBpAAYmxKZSYFXTEL6oBSpX5WNOQCw8VTQWNC2jW7za74J45LaGUwH4xfPD6TaR9oOn5zS0J_KW-8P_E1t93FAUJTSkH7Gz-UYO6";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public CheckStockConditionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        LocalDate currentDate = LocalDate.now();
        Log.d(TAG, "Date:"+ currentDate);
        DayOfWeek dayOfWeek = DayOfWeek.from(currentDate);
        int value = dayOfWeek.getValue();
        Log.d(TAG, "DateOfWeek:"+ value);
        UserDao userDao = new UserDao();
        userList = userDao.list();
        userList.forEach(user -> {
            int userid = user.getId();
            NotificationDao notificationDao = new NotificationDao();
            Notification notification = notificationDao.findNotification(userid);
            if(notification != null) {
                String notificationDay = notification.getNotification_day();
                Log.d(TAG, "NotificationDay: "+ notificationDay);
                int dayOfWeekValue;
                if(!notificationDay.equals("")){
                    dayOfWeekValue = DayOfWeek.valueOf(notificationDay.toUpperCase()).getValue();
                    Log.d(TAG, "DateOfWeek: "+ dayOfWeekValue);
                    if(dayOfWeekValue != 0 && dayOfWeekValue-1 == value){
                        checkStockCondition(user, userid);
                    }
                }
            }

        });
        return Result.success();
    }

    public void checkStockCondition(User thatUser, int user_id){
        Log.d(TAG, "Userid: "+ user_id);
        ItemDao itemDao = new ItemDao();
        ShoppingListDao shoppingListDao = new ShoppingListDao();
        List<ShoppingList> ShoppingList = shoppingListDao.getShoppingList(user_id);
        if(ShoppingList.size() > 0){
            ShoppingList.forEach(shoppingListItem -> {
                if(shoppingListItem.isRestock()){
                    boolean delete = shoppingListDao.deleteItemFromShoppingList(user_id, shoppingListItem.getProducts_id(), shoppingListItem.getItems_id());
                    if(delete){
                        Log.d(TAG, "Old Shopping List of Item: " + shoppingListItem.getItemName() + ", is deleted.");
                    }
                }
            });
        }
        //check the table of item of that user
        List<Item> itemList = itemDao.list(user_id);
        itemList.forEach(item -> {
            Log.d(TAG, "Item Name : "+ item.getItemName());
            BigDecimal Dailyusage = item.getDailyusage();
            String stock_time = item.getStock_time();
            //Cuz checking is done one day before notification time
            LocalDate currentDate = LocalDate.now().plusDays(1);
            Log.d(TAG, "Item: " +  item.getItemName() + ", Current Date:"+ currentDate);
            String currentDateString = currentDate.toString();
            Log.d(TAG, "Item: " +  item.getItemName() + ", Current Date To String:"+ currentDateString);
            LocalDate stockDay = LocalDate.parse(stock_time);
            long days = ChronoUnit.DAYS.between(stockDay, currentDate);
            Log.d(TAG, "Item: " +  item.getItemName() + ", Day difference:" + currentDate + " Compare to " + stockDay + "Value " + days);
            LocalDate nextWeekDate = currentDate.plusWeeks(1);
            long tillNextWeekDays = ChronoUnit.DAYS.between(stockDay, nextWeekDate);
            Log.d(TAG, "Item: " +  item.getItemName() + ", Day difference:" + nextWeekDate + "Compare to " + stockDay + "Value " + tillNextWeekDays);
            BigDecimal usageTillNextWeek;
            usageTillNextWeek = Dailyusage.multiply(BigDecimal.valueOf(tillNextWeekDays));
            Log.d(TAG, "Item: " +  item.getItemName() + ", usageTillNextWeek: " + usageTillNextWeek);
            BigDecimal usage = Dailyusage.multiply(BigDecimal.valueOf(days));
            Log.d(TAG,  "Item: " +  item.getItemName() + ", usage: " + usage);
            int quantity = item.getQuantity();
            BigDecimal roundedUpUsage = usage.setScale(5, RoundingMode.HALF_UP);
            int result = BigDecimal.valueOf(quantity).compareTo(roundedUpUsage);
            Log.d(TAG,  "Item: " +  item.getItemName() + ", usage rounded: " + roundedUpUsage);
            BigDecimal resultInBigDecimal = BigDecimal.valueOf(quantity).subtract(roundedUpUsage.setScale(0, RoundingMode.DOWN));
            int resultInNumber = resultInBigDecimal.intValue();
            Log.d(TAG,  "Item: " +  item.getItemName() + ", Remaining stock: " + resultInNumber);
            BigDecimal roundedUpUsageTillNextWeek = usageTillNextWeek.setScale(5, RoundingMode.HALF_UP);
            int resultTillNextWeek = BigDecimal.valueOf(quantity).compareTo(roundedUpUsageTillNextWeek);
            Log.d(TAG,  "Item: " +  item.getItemName() + ", usage till next week rounded: " + roundedUpUsageTillNextWeek);
            BigDecimal resultTillNextWeekInBigDecimal = BigDecimal.valueOf(quantity).subtract(roundedUpUsageTillNextWeek.setScale(0, RoundingMode.DOWN));
            int resultTillNextWeekInNumber = resultTillNextWeekInBigDecimal.intValue();
            Log.d(TAG,  "Item: " +  item.getItemName() + ", Remaining stock till next week: " + resultTillNextWeekInNumber);
            int purchaseQuantity;
            if(result < 0 || result == 0 ){
                purchaseQuantity = checkPurchaseQuantity(item);
                if(purchaseQuantity != 0){
                    float price = item.getItemPrice();
                    float totalPrice = price * purchaseQuantity;
                    int finalRemainingStock;
                    if (resultInNumber < 0){
                        finalRemainingStock = 0;
                    }else{
                        finalRemainingStock = resultInNumber;
                    }
                    boolean updateRemainingStockInItem = itemDao.updateRemainingStockInItem(user_id, item.getProducts_id(), finalRemainingStock);
                    if(updateRemainingStockInItem){
                        //Check already in shoppingList DB or not
                        ShoppingList shoppingListItem = shoppingListDao.findItemInShoppingListDB(user_id, item.getProducts_id(), item.getIid());
                        if(shoppingListItem == null){
                            boolean addToShoppingListDB = addItemToShoppingListDBAndUpdateStockCondition(shoppingListDao, user_id, item, totalPrice, purchaseQuantity, itemDao);
                            if(addToShoppingListDB){
                                Log.d(TAG, "Item: " + item.getItemName() + " added to shoppingList and stock condition in item DB updated");
                                if (finalRemainingStock == 0) { // Send Notification to user now
                                    String regToken = thatUser.getToken();
                                    String username = thatUser.getUsername();
                                    productName = item.getItemName();
                                    sendNotification(regToken, username, productName);
                                }
                            }
                        }
                    }
                }

            }else if (resultTillNextWeek < 0 || resultTillNextWeek == 0){
                purchaseQuantity = checkPurchaseQuantity(item);
                if(purchaseQuantity != 0){
                    float price = item.getItemPrice();
                    float totalPrice = price * purchaseQuantity;
                    int finalRemainingStock;
                    if (resultTillNextWeekInNumber <= 0){
                        finalRemainingStock = resultInNumber;
                    }else{
                        finalRemainingStock = resultTillNextWeekInNumber;
                    }
                    boolean updateRemainingStockInItem = itemDao.updateRemainingStockInItem(user_id, item.getProducts_id(), finalRemainingStock);
                    if(updateRemainingStockInItem){
                        Log.d(TAG, "Item: " + item.getItemName() + ", Remaining Stock: " + itemDao.findItem(user_id, item.getProducts_id()).getRemainingStock());
                        //Check already in shoppingList DB or not
                        ShoppingList shoppingListItem = shoppingListDao.findItemInShoppingListDB(user_id, item.getProducts_id(), item.getIid());
                        if(shoppingListItem == null){
                            boolean addToShoppingListDB = addItemToShoppingListDBAndUpdateStockCondition(shoppingListDao, user_id, item, totalPrice, purchaseQuantity, itemDao);
                            if(addToShoppingListDB){
                                Log.d(TAG, "Item: " + item.getItemName() + " added to shoppingList and stock condition in item DB updated");
                            }
                        }
                    }
                }
            }
        });
    }

    public int checkPurchaseQuantity(Item item){
        int purchaseQuantity = 0;
        switch(item.getUsageType()){
            case "Daily":
                purchaseQuantity = item.getUsage_daily()*7;
                break;

            case "Weekly":
                purchaseQuantity = item.getUsage_weekly();
                break;

            case "Biweekly":
                purchaseQuantity = item.getUsage_biweekly();
                break;

            case "Monthly":
                purchaseQuantity = item.getUsage_monthly();
                break;
        }
        return purchaseQuantity;
    }

    public boolean addItemToShoppingListDBAndUpdateStockCondition(ShoppingListDao shoppingListDao, int user_id, Item item, float totalPrice, int purchaseQuantity, ItemDao itemDao){
        boolean addToShoppingList = shoppingListDao.addItemToShoppingListDB(user_id, item.getProducts_id(), item.getIid(), totalPrice, purchaseQuantity, item.isStockCondition());
        if(addToShoppingList) {
            Log.d(TAG, "Add item to Shopping list in DB: " + user_id + " -- " + item.getItemName() + "with total price: " + totalPrice + ", and quantity: " + purchaseQuantity);
            item.setStockCondition(true);
            boolean updateStockCondition = itemDao.updateStockCondition(user_id, item.getProducts_id(), item.isStockCondition());
            if (updateStockCondition) {
                Log.d(TAG, "onStockConditionCheckedTillNextWeek: " + item.getItemName() + "with stock condition " + item.isStockCondition());
                return true;
            }
        }
        return false;
    }

    private void sendNotification(String regToken, String username, String productName) {
        String msg = "Hi, " + username + "! " + productName + " will be out of stock before next shopping.";
        String title = "Alert on Early Purchasing";

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Log.d(TAG, "Reg token to send: " + regToken);
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", msg);
                    dataJson.put("title", title);
                    json.put("notification", dataJson);
                    json.put("to", regToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + LEGACY_SERVER_KEY)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.d(TAG, "Response: " + finalResponse);
                } catch (Exception e) {
                    Log.d(TAG, e + "");
                }
                return null;
            }
        }.execute();

    }
}
