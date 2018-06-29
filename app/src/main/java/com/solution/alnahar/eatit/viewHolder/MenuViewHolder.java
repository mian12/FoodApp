package com.solution.alnahar.eatit.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.solution.alnahar.eatit.Interface.ItemClickListner;
import com.solution.alnahar.eatit.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView menuName;
    public ImageView menuImage;
    public ItemClickListner itemClickListner;

    public MenuViewHolder(View itemView) {
        super(itemView);

        menuName = itemView.findViewById(R.id.menu_name);
        menuImage = itemView.findViewById(R.id.menu_imge);


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
