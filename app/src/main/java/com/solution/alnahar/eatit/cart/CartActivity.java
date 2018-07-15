package com.solution.alnahar.eatit.cart;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.SnackBar;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.HomeActivity;
import com.solution.alnahar.eatit.Interface.RecyclerItemTouchHelperListener;
import com.solution.alnahar.eatit.MainActivity;
import com.solution.alnahar.eatit.Model.Order;
import com.solution.alnahar.eatit.Model.RecyclerItemTouchHelper;
import com.solution.alnahar.eatit.Model.Request;
import com.solution.alnahar.eatit.Model.Token;
import com.solution.alnahar.eatit.R;
import com.solution.alnahar.eatit.SQLiteDatabaseHelper;
import com.solution.alnahar.eatit.adapter.CartAdapter;
import com.solution.alnahar.eatit.fcmModel.MyResponse;
import com.solution.alnahar.eatit.fcmModel.Notification;
import com.solution.alnahar.eatit.fcmModel.Sender;
import com.solution.alnahar.eatit.remote.APIService;
import com.solution.alnahar.eatit.viewHolder.CartViewHolder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {


    FirebaseDatabase database;
    DatabaseReference request_db_ref;
    public TextView txtTotalPrice;
    Button btnPlaceOrder;
    RecyclerView recyclerView_cartList;
    RecyclerView.LayoutManager layoutManager;


    List<Order> cartArrayList;
    CartAdapter adapter;
    SQLiteDatabaseHelper databaseHelper;

    APIService mService;
    LinearLayout rootLayout;

    Place shippingAddess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCart);
        toolbar.setTitle("Cart Detail");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
//
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // init apiservice
        mService = Common.getFcmService();

        // init firebase
        database = FirebaseDatabase.getInstance();
        request_db_ref = database.getReference("Requests");

        databaseHelper = new SQLiteDatabaseHelper(this);

        recyclerView_cartList = findViewById(R.id.cart_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_cartList.setLayoutManager(layoutManager);

        rootLayout=findViewById(R.id.rootLayout);

// swipe  touch listener
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);

      new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView_cartList);





        // animation from right to left slide

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView_cartList.getContext(), R.anim.layout_slide_from_right);

        recyclerView_cartList.setLayoutAnimation(controller);

        txtTotalPrice = findViewById(R.id.total);
        btnPlaceOrder = findViewById(R.id.placeOrder);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cartArrayList.size() > 0) {
                    alertDialogPlaceOrder();
                } else {
                    Toast.makeText(CartActivity.this, "Sorry your cart is empty", Toast.LENGTH_SHORT).show();
                }


            }
        });


        loadListFood();

        //Log.e("test","onCreate");


    }


    private void loadListFood() {


        // fecthing data from sqlLite DB
        cartArrayList = HomeActivity.myAppDatabase.myDao().getCart();


        adapter = new CartAdapter(this, cartArrayList);
        adapter.notifyDataSetChanged();
        recyclerView_cartList.setAdapter(adapter);

        // calculate total price
        int total = 0;
        for (Order order : cartArrayList) {
            total += (Integer.parseInt(order.getPrice()) * Integer.parseInt(order.getQty()));

        }

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));

        // animation
        recyclerView_cartList.getAdapter().notifyDataSetChanged();
        recyclerView_cartList.scheduleLayoutAnimation();


    }


    // ctrl+0


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());

        }

        return true;
    }

    private void deleteCart(int position) {

        // first remove item form cartArray List
        cartArrayList.remove(position);
        // after then we will delete all data from sqllite
        HomeActivity.myAppDatabase.myDao().clearCart();
        //  databaseHelper.cleanCart();

        //now fill fresh data into sqlite from cartArrayList
        for (Order item : cartArrayList) {

            HomeActivity.myAppDatabase.myDao().addToCart(item);
        }

        //
        // now load new data from sqlite
        loadListFood();


    }

    @Override
    protected void onPause() {

        super.onPause();
        //  Log.e("tag","on pause");


    }

    @Override
    protected void onStop() {
        //   Log.i("tag","on stop");
        super.onStop();


    }

    private void alertDialogPlaceOrder() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("One  More Step");
        alertDialog.setMessage("Enter your address");


        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_order_adress, null);
//        final MaterialEditText editTextAdress = view.findViewById(R.id.edtAddress);
        PlaceAutocompleteFragment editAddres= (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        editAddres.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);

        ((EditText)editAddres.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter your address");
        ((EditText)editAddres.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(15);


        // get address from place  auto complete

        editAddres.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                shippingAddess=place;
            }

            @Override
            public void onError(Status status) {

            }
        });



        final MaterialEditText editTextComment = view.findViewById(R.id.edtComment);


        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_cart);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // create new request

                Request object = new Request();
                object.setName(Common.currentUser.getName());
                object.setPhone(Common.currentUser.getPhone());
                object.setAddress(shippingAddess.getAddress().toString());
                object.setLatlng(String.format("%s,%s",shippingAddess.getLatLng().latitude,shippingAddess.getLatLng().longitude));
                object.setTotal(txtTotalPrice.getText().toString());
                object.setOrderList(cartArrayList);
                //object.setStatus("0");
                object.setComment(editTextComment.getText().toString());

                String order_number = String.valueOf(System.currentTimeMillis());
                // submit to firebase we will use current mili seconds  for request key
                request_db_ref.child(order_number).setValue(object);
                // delete cart
                HomeActivity.myAppDatabase.myDao().clearCart();

                 sendNotificationOrder(order_number);
//                Toast.makeText(CartActivity.this, "Thank you for order place", Toast.LENGTH_SHORT).show();
//                finish();


            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                // remove frgment
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();

            }
        });

        alertDialog.show();


    }



    private void sendNotificationOrder(final String order_number) {

        DatabaseReference tokens_db_ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens_db_ref.orderByChild("serverToken").equalTo(true); // get all node with server token is true;
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postDtaSnapShort : dataSnapshot.getChildren()) {
                    Token serverToken = postDtaSnapShort.getValue(Token.class);
                    Notification notification = new Notification("Food Order", "You have new order " + order_number);
                    Sender content = new Sender(serverToken.getToken(), notification);
                    mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                            if (response.body().success == 1) {
                                Toast.makeText(CartActivity.this, "Thank you for order place", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CartActivity.this, "Failed!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                            Log.e("error", t.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder) {

            String name = ((CartAdapter) recyclerView_cartList.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProduct_name();

            final Order deleteItem = ((CartAdapter) recyclerView_cartList.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);

            HomeActivity.myAppDatabase.myDao().removeFromCart(deleteItem.getProduct_id(),Common.currentUser.getPhone());
            //HomeActivity.myAppDatabase.myDao().removeFromCart(deleteItem.getProduct_id());



            List<Order> orderList = HomeActivity.myAppDatabase.myDao().getCart();

            // calculate total price
            int total = 0;
            for (Order item : orderList) {
                total += (Integer.parseInt(item.getPrice()) * Integer.parseInt(item.getQty()));

            }
            // sv_SE

            Locale locale = new Locale("en", "US");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
            txtTotalPrice.setText(fmt.format(total));

            // makes snakbar
            Snackbar snackBar=Snackbar.make(rootLayout,name+"  removed from  cart!!", Snackbar.LENGTH_LONG);

            snackBar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    adapter.restoreItem(deleteItem,deleteIndex);

                    HomeActivity.myAppDatabase.myDao().addToCart(deleteItem);



                    List<Order> orderList = HomeActivity.myAppDatabase.myDao().getCart();

                    // calculate total price
                    int total = 0;
                    for (Order item : orderList) {
                        total += (Integer.parseInt(item.getPrice()) * Integer.parseInt(item.getQty()));

                    }
                    // sv_SE

                    Locale locale = new Locale("en", "US");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
                    txtTotalPrice.setText(fmt.format(total));



                }
            });
            snackBar.setActionTextColor(Color.YELLOW);
            snackBar.show();




        }
    }
}
