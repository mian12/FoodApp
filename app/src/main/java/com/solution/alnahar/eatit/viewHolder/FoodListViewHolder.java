package com.solution.alnahar.eatit.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.solution.alnahar.eatit.Interface.ItemClickListner;
import com.solution.alnahar.eatit.R;

public class FoodListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView foodListName,foodListPrice;
    public ImageView foodListImage,favtFood,image_share_fb,image_quick_cart;
    ItemClickListner itemClickListner;



    public FoodListViewHolder(View itemView) {
        super(itemView);

        foodListName=itemView.findViewById(R.id.foodList_name);
        foodListImage=itemView.findViewById(R.id.foodList_imge);
        favtFood=itemView.findViewById(R.id.favt);
        image_share_fb=itemView.findViewById(R.id.btnSahre);

        foodListPrice=itemView.findViewById(R.id.foodList_price);

        image_quick_cart=itemView.findViewById(R.id.btn_quick_cart);



        itemView.setOnClickListener(this);
    }


    public  void setItemClickListner(ItemClickListner itemClickListner)
    {

        this.itemClickListner=itemClickListner;
    }

    @Override
    public void onClick(View v) {

        itemClickListner.onClick(itemView,getAdapterPosition(),false);


    }
}
