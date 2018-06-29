package com.solution.alnahar.eatit.roomDb;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.solution.alnahar.eatit.Model.Order;

import java.util.List;

@Dao
public interface MyDao {

    @Insert
    public long addToCart(Order order);

    @Query("UPDATE order_detail SET order_qty=:qty WHERE order_productId = :productId")
    public  void updateCart(String qty,String productId);


    @Query(" select * from order_detail")
    public List<Order> getCart();

    @Query("delete  from  order_detail")
    public  void  clearCart();

    @Query("select order_productId from order_detail where order_productId=:productId")
    public  String itemCartExitorNot(String productId);

    @Insert
    public  void addTofavourite(Favourites favourites);

    @Query("select favt_productId from favourite where favt_productId=:productId and favt_phoneNumber=:phoneNumber" )
    public  String isFavourite(String productId, String phoneNumber);


    @Query("delete  from  favourite where  favt_productId=:productId and favt_phoneNumber=:phoneNumber")
    public  void  removeFavouriteItemId( String productId, String phoneNumber);



    @Query("delete  from  order_detail where  order_productId=:productId and order_phone=:phoneNumber ")
    public  void  removeFromCart( String productId, String phoneNumber);


//    @Query("delete  from  order_detail where  order_productId=:productId")
//    public  void  removeFromCart( String productId);


    @Query(" select  count(*) from order_detail")
    public  int getCountCartItem();

    @Query("select order_qty from order_detail where order_productId=:productId")
    public  String getItemQuantityForSpecificeItem(String productId);


}
