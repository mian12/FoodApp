package com.solution.alnahar.eatit.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.solution.alnahar.eatit.Interface.ItemClickListner;
import com.solution.alnahar.eatit.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView order_id,order_status,order_phone,order_address;
    public ImageView menuImage;
    public ItemClickListner itemClickListner;

    public OrderViewHolder(View itemView) {
        super(itemView);

        order_id = itemView.findViewById(R.id.order_id);
        order_status = itemView.findViewById(R.id.order_status);
        order_phone = itemView.findViewById(R.id.order_phone);
        order_address = itemView.findViewById(R.id.order_address);


        itemView.setOnClickListener(this);
    }


    public void setItemClickListner(ItemClickListner itemClickListner) {

        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {

        itemClickListner.onClick(itemView, getAdapterPosition(), false);


    }
}
