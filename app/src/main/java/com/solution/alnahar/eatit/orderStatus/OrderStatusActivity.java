package com.solution.alnahar.eatit.orderStatus;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.Interface.ItemClickListner;
import com.solution.alnahar.eatit.Model.Category;
import com.solution.alnahar.eatit.Model.Food;
import com.solution.alnahar.eatit.Model.Request;
import com.solution.alnahar.eatit.R;
import com.solution.alnahar.eatit.viewHolder.FoodListViewHolder;
import com.solution.alnahar.eatit.viewHolder.OrderViewHolder;

public class OrderStatusActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference requests_db_ref;

    RecyclerView recyclerView_orderStatus;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);



        // init firebase
        database= FirebaseDatabase.getInstance();
        requests_db_ref= database.getReference("Requests");

        recyclerView_orderStatus=findViewById(R.id.orderStatus);
        layoutManager=new LinearLayoutManager(this);
        recyclerView_orderStatus.setHasFixedSize(true);
        recyclerView_orderStatus.setLayoutManager(layoutManager);


        //Note

        // if we  start orderStatus from Home Activity then
        // we will not put any extra ,so we just load order from Common

        if (getIntent().getExtras()==null) {
            loadOrders(Common.currentUser.getPhone());
       }
        else
        {
            loadOrders(getIntent().getStringExtra("userPhone"));
        }
    }

    private void loadOrders(String phone) {


        // DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        Query query = requests_db_ref.orderByChild("phone").equalTo(phone);


        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(query, Request.class)
                .build();


        adapter=new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_order_status, parent, false);

                return new OrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull Request model) {

                holder.order_id.setText(adapter.getRef(position).getKey());
                holder.order_phone.setText(model.getPhone());
                holder.order_address.setText(model.getAddress());
                holder.order_status.setText(Common.convertCodeToStatus(model.getStatus()));
                holder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {

                    }
                });


            }
        };

        recyclerView_orderStatus.setAdapter(adapter);
    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
