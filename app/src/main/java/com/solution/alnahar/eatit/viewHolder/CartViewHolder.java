package com.solution.alnahar.eatit.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

   public  TextView cart_item_name, cart_item_price;
    public ElegantNumberButton btn_cart_counter;
    public ImageView cart_image;

    public  RelativeLayout view_background;
    public LinearLayout  view_foreground;



    public CartViewHolder(View itemView) {
        super(itemView);
        cart_item_name = itemView.findViewById(R.id.cart_item_name);
        cart_item_price = itemView.findViewById(R.id.cart_item_price);
        btn_cart_counter = itemView.findViewById(R.id._btn_cart_numberCounter);
        cart_image = itemView.findViewById(R.id.cart_image);

        view_background=itemView.findViewById(R.id.view_background);
        view_foreground=itemView.findViewById(R.id.view_forground);



        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {


        contextMenu.setHeaderTitle("Select action");
        contextMenu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}
