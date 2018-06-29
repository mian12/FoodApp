package com.solution.alnahar.eatit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.solution.alnahar.eatit.Model.Order;
import com.solution.alnahar.eatit.Model.User;

import java.util.ArrayList;


/**
 * Created by Mian Shahbaz Idrees on 25-Jul-16.
 */
public class SQLiteDatabaseHelper extends SQLiteOpenHelper {
    //database
    private static final String DATABASE_NAME = "EatIt.db";
    //tables
    private static final String ORDER_DETAIL = "order_detail";
    private static final String FAVOURITES = "favourites";


    private static final String ID = "item_id";
    private static final String PRODUCT_ID = "product_id";
    private static final String PRODUCT_NAME = "product_name";
    private static final String QUANTITY = "qty";
    private static final String PRICE = "price";
    private static final String DISCOUNT = "discount";
    private static final String PHONE_NUMBER= "phoneNumber";


    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL("Create Table " + ORDER_DETAIL + "(" + ID + " Integer PRIMARY KEY AUTOINCREMENT, " + PRODUCT_ID + " text, " +
                PRODUCT_NAME + " text," + QUANTITY + " text, " + PRICE + " text," + DISCOUNT + " text)");

        db.execSQL("Create Table " + FAVOURITES + "(" + ID + " Integer PRIMARY KEY AUTOINCREMENT,"+ PHONE_NUMBER + " text)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ORDER_DETAIL);

        onCreate(db);
    }


    public void addToCart(Order order) {


        int  itemId=Integer.parseInt(order.getProduct_id());


        SQLiteDatabase db = getWritableDatabase();
        ContentValues S = new ContentValues();

        Cursor itemsCursor = db.rawQuery("SELECT  " + ID + " FROM " + ORDER_DETAIL + " WHERE " + PRODUCT_ID + "=" + itemId, null);

        if (itemsCursor.getCount()>0)
        {
            S.put(PRODUCT_ID, order.getQty());
            db.update(ORDER_DETAIL, S, PRODUCT_ID + "= ?", new String[]{String.valueOf(itemId)});
            itemsCursor.close();
            db.close();
           // return  false;
        }
        else
        {
            S.put(PRODUCT_ID, order.getProduct_id());
            S.put(PRODUCT_NAME, order.getProduct_name());
            S.put(QUANTITY, order.getQty());
            S.put(PRICE, order.getPrice());
            S.put(DISCOUNT, order.getDiscount());

            db.insert(ORDER_DETAIL, null, S);

            itemsCursor.close();
            db.close();
        }




    }


    public ArrayList<Order> getCart() {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<Order> cartList = new ArrayList<>();
        Order object = null;
        Cursor itemsCursor = db.rawQuery("SELECT  * FROM " + ORDER_DETAIL, null);

        while (itemsCursor.moveToNext()) {
            object = new Order();

            object.setProduct_id(itemsCursor.getString(itemsCursor.getColumnIndex(PRODUCT_ID)));
            object.setProduct_name(itemsCursor.getString(itemsCursor.getColumnIndex(PRODUCT_NAME)));
            object.setQty(itemsCursor.getString(itemsCursor.getColumnIndex(QUANTITY)));
            object.setPrice(itemsCursor.getString(itemsCursor.getColumnIndex(PRICE)));
            object.setDiscount(itemsCursor.getString(itemsCursor.getColumnIndex(DISCOUNT)));

            cartList.add(object);
        }
        itemsCursor.close();
        db.close();
        return cartList;
    }


    public void cleanCart() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ORDER_DETAIL, null, null);
        db.close();
    }

    public void addToFavt(String itemId,String phoneNumber) {


        SQLiteDatabase db = getWritableDatabase();
        ContentValues S = new ContentValues();
        S.put(ID, itemId);
        S.put(PHONE_NUMBER, phoneNumber);
        db.insert(FAVOURITES, null, S);
        db.close();

    }

    public void removeFromFavt(String itemId,String phoneNumber) {

        String[] args={itemId,phoneNumber};
        SQLiteDatabase db = getWritableDatabase();



        db.rawQuery("delete FROM FAVOURITES WHERE ID=? and PHONE_NUMBER=?",args);
        db.close();


      //  db.delete(FAVOURITES, ID + "= ?", new String[]{String.valueOf(itemId)} +"and"+PHONE_NUMBER+"="+phoneNumber);
        db.close();


    }

    public Boolean isFavourite(int itemId,String phoneNumber) {

        SQLiteDatabase db = getWritableDatabase();

        String sql="SELECT  * FROM " + FAVOURITES + " WHERE " + ID + "=" +itemId+ " "+"AND"+" " +PHONE_NUMBER+"="+phoneNumber;

        Cursor itemsCursor = db.rawQuery(sql, null);
        if (itemsCursor.getCount() <= 0) {

            itemsCursor.close();
            db.close();

            return false;
        }
        itemsCursor.close();
        db.close();

        return true;


    }


//    public void insertCategories(ArrayList<CategoriesModel> categoriesModelArrayList) {
//        SQLiteDatabase db = getWritableDatabase();
//
//        db.delete(CATEGORIES, null, null);
//
//        ContentValues S = new ContentValues();
//        CategoriesModel categoriesModel;
//
//        for (int i = 0; i < categoriesModelArrayList.size(); i++) {
//            categoriesModel = categoriesModelArrayList.get(i);
//            S.clear();
//            S.put(CATID, categoriesModel.getCatid());
//            S.put(NAME, categoriesModel.getName());
//            S.put(DESCRIPTION, categoriesModel.getDescription());
//            S.put(UID, categoriesModel.getUid());
//            S.put(URL_PATH, categoriesModel.getImgUrl());
//            db.insert(CATEGORIES, null, S);
//        }
//        db.close();
//    }

//
//    public void insertCartCounter(long productID, double counter) {
//        SQLiteDatabase db = getWritableDatabase();
//        ContentValues S = new ContentValues();
//
//        Cursor itemsCursor = db.rawQuery("SELECT  " + PRODUCT_ID + " FROM " + CART + " WHERE " + PRODUCT_ID + "=" + productID, null);
//
//        if (counter > 0) {
//            // if already have then update other wise insert
//            if (itemsCursor.getCount() > 0) {
//                S.put(PRODUCT__CART_QTY, counter);
//                db.update(CART, S, PRODUCT_ID + "= ?", new String[]{String.valueOf(productID)});
//            } else {
//                S.put(PRODUCT_ID, productID);
//                S.put(PRODUCT__CART_QTY, counter);
//                db.insert(CART, null, S);
//            }
//            S.clear();
//            S.put(IS_ADDED_TO_CART, 1);
//            db.update(PRODUCTS, S, PRODUCT_ID + "= ?", new String[]{String.valueOf(productID)});
//        } else {
//            //delete
//            db.delete(CART, PRODUCT_ID + "= ?", new String[]{String.valueOf(productID)});
//            S.clear();
//            S.put(IS_ADDED_TO_CART, 0);
//            db.update(PRODUCTS, S, PRODUCT_ID + "= ?", new String[]{String.valueOf(productID)});
//        }
//    }

//    public int getTotalCartCounter() {
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor itemsCursor = db.rawQuery("SELECT  " + PRODUCT_ID + " FROM " + CART, null);
//        int count = itemsCursor.getCount();
//        itemsCursor.close();
//        db.close();
//        return count;
//    }
//
//    public int getTotalProducts() {
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor itemsCursor = db.rawQuery("SELECT  " + PRODUCT_ID + " FROM " + PRODUCTS, null);
//        int count = itemsCursor.getCount();
//        itemsCursor.close();
//        db.close();
//        return count;
//    }


//
//    public int getTotalCategories() {
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor itemsCursor = db.rawQuery("SELECT  " + CATID + " FROM " + CATEGORIES, null);
//        int count = itemsCursor.getCount();
//        itemsCursor.close();
//        db.close();
//        return count;
//    }
//
//
//
//    public boolean isAddedToCart(long productID) {
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor productCursor = db.rawQuery("SELECT  " + PRODUCT_ID + " FROM " + CART + " WHERE " + PRODUCT_ID + "=" + productID, null);
//
//        if (productCursor.getCount() > 0) {
//            // if already have then update other wise insert
//            db.close();
//            return true;
//        } else {
//            db.close();
//            return false;
//        }
//    }


}
