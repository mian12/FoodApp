package com.solution.alnahar.eatit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.HomeActivity;
import com.solution.alnahar.eatit.MainActivity;
import com.solution.alnahar.eatit.Model.Order;
import com.solution.alnahar.eatit.R;
import com.solution.alnahar.eatit.SQLiteDatabaseHelper;
import com.solution.alnahar.eatit.cart.CartActivity;
import com.solution.alnahar.eatit.viewHolder.CartViewHolder;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Mian Shahbaz Idrees on 12-Mar-18.
 */

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {


    private LayoutInflater inflater;
    private CartActivity cartActivity;

    List<Order> dataList;

    public CartAdapter(CartActivity cartActivity, List<Order> dataList) {
        this.cartActivity = cartActivity;
        this.dataList = dataList;
        inflater = LayoutInflater.from(cartActivity);

    }


    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.row_cart, parent, false);
        CartViewHolder holder = new CartViewHolder(view);
        return holder;


    }


    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int position) {


        //  TextDrawable drawable = TextDrawable.builder().buildRound(dataList.get(position).getQty() + "", Color.RED);


        // holder.img_cart_item_count.setImageDrawable(drawable);

        Order order = dataList.get(position);
        String productQty = HomeActivity.myAppDatabase.myDao().getItemQuantityForSpecificeItem(order.getProduct_id());
        holder.btn_cart_counter.setNumber(productQty);


        holder.btn_cart_counter.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {


                Order order = dataList.get(position);
                // updated the  item  quantity of current item
                HomeActivity.myAppDatabase.myDao().updateCart(String.valueOf(newValue), order.getProduct_id());
                //  Toast.makeText(cartActivity, "updated", Toast.LENGTH_SHORT).show();

                List<Order> orderList = HomeActivity.myAppDatabase.myDao().getCart();

                // calculate total price
                int total = 0;
                for (Order item : orderList) {
                    total += (Integer.parseInt(item.getPrice()) * Integer.parseInt(item.getQty()));

                }
                // sv_SE

                Locale locale = new Locale("en", "US");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                cartActivity.txtTotalPrice.setText(fmt.format(total));


                int price = (Integer.parseInt(orderList.get(position).getPrice()) * Integer.parseInt(orderList.get(position).getQty()));

                holder.cart_item_price.setText(fmt.format(price));


            }
        });

        Locale locale = new Locale("en", "US");

        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(dataList.get(position).getPrice()) * Integer.parseInt(dataList.get(position).getQty()));

        holder.cart_item_price.setText(fmt.format(price));

        holder.cart_item_name.setText(dataList.get(position).getProduct_name());


       //holder.cart_image.
                Picasso.with(cartActivity).load(dataList.get(position).getImage())
                             .resize(70,70)
                              .centerCrop()
                             .into(holder.cart_image);




    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    public  Order getItem(int position)
    {
        return  dataList.get(position);

    }

    public  void removeItem(int position)
    {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    public  void restoreItem(Order item,int position)
    {
        dataList.add(position,item);
        notifyItemInserted(position);
    }



}
