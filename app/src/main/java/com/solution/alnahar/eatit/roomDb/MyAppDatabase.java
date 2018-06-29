package com.solution.alnahar.eatit.roomDb;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.solution.alnahar.eatit.Model.Order;

@Database(entities = {Order.class,Favourites.class},version =1)
public abstract class MyAppDatabase extends RoomDatabase {
    
    public  abstract MyDao myDao();
}
