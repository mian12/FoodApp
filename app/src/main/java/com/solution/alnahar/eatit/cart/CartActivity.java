package com.solution.alnahar.eatit.cart;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
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
import com.solution.alnahar.eatit.SignInActivity;
import com.solution.alnahar.eatit.adapter.CartAdapter;
import com.solution.alnahar.eatit.fcmModel.MyResponse;
import com.solution.alnahar.eatit.fcmModel.Notification;
import com.solution.alnahar.eatit.fcmModel.Sender;
import com.solution.alnahar.eatit.mapsPlacesModel.Geometry;
import com.solution.alnahar.eatit.mapsPlacesModel.MyLocation;
import com.solution.alnahar.eatit.mapsPlacesModel.MyPlacesResponse;
import com.solution.alnahar.eatit.mapsPlacesModel.MyResults;
import com.solution.alnahar.eatit.remote.APIService;
import com.solution.alnahar.eatit.remote.IGoogleService;
import com.solution.alnahar.eatit.viewHolder.CartViewHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    FirebaseDatabase database;
    DatabaseReference request_db_ref;
    public TextView txtTotalPrice;
    Button btnPlaceOrder;
    RecyclerView recyclerView_cartList;
    RecyclerView.LayoutManager layoutManager;


    List<Order> cartArrayList;
    CartAdapter adapter;
    SQLiteDatabaseHelper databaseHelper;



    LinearLayout rootLayout;

    Place shippingAddess;
 double shippingAddess_Lat=0.0;
 double shippingAddess_Lng=0.0;

    //// current Location of device
    private LocationRequest mLocationRequest;
    // public LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    public static final int UPDATE_INTERVAL = 5000;
    public static final int FASTEST_INTERVAL = 3000;
    public static final int DISPLACEMENT = 10;


    public static final int REQUEST_LOCATION_CODE = 999;

// for Notification
    APIService mService;
    // Google Map Api Client
    IGoogleService mGoogleMapService;
    String adress = "";


    View view=null;
    PlaceAutocompleteFragment editAddres;


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

        // it uses Scalar Convertor
        mGoogleMapService = Common.getGoogleMapApi();


        // it uses GSOn Convertor
        mService = Common.getFcmService();

        // Runtime permissions


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Check permission
            ActivityCompat.requestPermissions(this, new String[]
                            {android.Manifest.permission.ACCESS_FINE_LOCATION}
                    , REQUEST_LOCATION_CODE);
        } else {

            if (checkPlayServices()) {
                buildingGoogleApiClient();
                createLocationRequest();
            }
        }




        // init firebase
        database = FirebaseDatabase.getInstance();
        request_db_ref = database.getReference("Requests");

        databaseHelper = new SQLiteDatabaseHelper(this);

        recyclerView_cartList = findViewById(R.id.cart_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_cartList.setLayoutManager(layoutManager);

        rootLayout = findViewById(R.id.rootLayout);

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //  we have to remove  fragment so it can prevent from crash,why  because in xml i use placeholder fragment
      //  getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();
    }

    private synchronized void buildingGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();


    }

    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();

        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);


    }


    //ctrlO

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildingGoogleApiClient();
                        createLocationRequest();
                    }

                }
            }

        }
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(this, status, 2404).show();
                Toast.makeText(this, "This device  is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
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



    private void alertDialogPlaceOrder() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        alertDialog.setTitle("One  More Step");
        alertDialog.setMessage("Enter your address");

        if (view!=null )
        {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                 parent.removeView(view);

            }
        }


        LayoutInflater inflater = LayoutInflater.from(this);

        if (view==null) {
            view= inflater.inflate(R.layout.dialog_order_adress, null);

       }


        editAddres = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        editAddres.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);

        ((EditText) editAddres.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter your address");
        ((EditText) editAddres.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(25);





        // get address from place  auto complete

        editAddres.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {


                shippingAddess = place;
                Log.e("position",shippingAddess.getLatLng().latitude+"  "+shippingAddess.getLatLng().longitude);
                Toast.makeText(CartActivity.this, shippingAddess.getLatLng().latitude+" long"+shippingAddess.getLatLng().longitude, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Status status) {

            }
        });


        final MaterialEditText editTextComment = view.findViewById(R.id.edtComment);

        //Radio buttons

        final RadioButton rdShipToAdress = view.findViewById(R.id.rdShipToAdress);
        final RadioButton rdHomeAdress = view.findViewById(R.id.rdHomeToAdress);


        rdShipToAdress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final SpotsDialog dialog = new SpotsDialog(CartActivity.this);
               // dialog.setCancelable(true);


                if (isChecked) {
                    dialog.show();
//                    mGoogleMapService.getAdressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false",
//                            mLastLocation.getLatitude(),
//                            mLastLocation.getLongitude()))
//                            .enqueue(new Callback<String>() {
//                                @Override
//                                public void onResponse(Call<String> call, Response<String> response) {
//
//                                    try {
//                                        JSONObject jsonObject = new JSONObject(response.body().toString());
//                                        JSONArray jsonArray = jsonObject.getJSONArray("results");
//                                        JSONObject firstObjct = jsonArray.getJSONObject(0);
//                                        adress = firstObjct.getString("formatted_address");
//                                        // set this adress into edittext
//                                        ((EditText) editAddres.getView().findViewById(R.id.place_autocomplete_search_input)).setText(adress);
//
//                                        dialog.dismiss();
//
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<String> call, Throwable t) {
//
//                                    dialog.dismiss();
//                                    Toast.makeText(CartActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });

                    mGoogleMapService.getAdressName(String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f&sensor=false",
                            mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()))
                            .enqueue(new Callback<MyPlacesResponse>() {
                                @Override
                                public void onResponse(Call<MyPlacesResponse> call, Response<MyPlacesResponse> response) {
                                    if (response!=null)
                                    {
                                        List<MyResults> myResults=response.body().getResults();

                                       String myAddressName= myResults.get(0).getFormatted_address();

                                        String lat=myResults.get(0).getGeometry().getLocation().getLat();
                                        String lng=myResults.get(0).getGeometry().getLocation().getLng();

                                        shippingAddess_Lat=Double.parseDouble(lat);
                                        shippingAddess_Lng=Double.parseDouble(lng);

                                        adress =myAddressName;
                                        // set this adress into edittext
                                        ((EditText) editAddres.getView().findViewById(R.id.place_autocomplete_search_input)).setText(adress);

                                        dialog.dismiss();


//                                       MyPlacesResponse myPlacesResponse= response.body();
//                                       Log.e("places",myPlacesResponse+"");
                                    }

                                }

                                @Override
                                public void onFailure(Call<MyPlacesResponse> call, Throwable t) {

                                }
                            });

                }
            }
        });


        /// home address

        rdHomeAdress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    if (Common.currentUser.getHomeAddress() != null) {
                        adress = Common.currentUser.getHomeAddress();

                        ((EditText) editAddres.getView().findViewById(R.id.place_autocomplete_search_input)).setText(adress);


                    } else {

                        Toast.makeText(CartActivity.this, "Please Update your Home Address", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });


        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_cart);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                /// its tooo much important to decalare view null beacuse without this it creates null pointer exception :)
                view=null;

                dialog.dismiss();


                // create new request


                if (!rdShipToAdress.isChecked() && !rdHomeAdress.isChecked()) {

                    // if radio button is not activated
                    if (shippingAddess != null) {
                        adress = shippingAddess.getAddress().toString();
                    } else {
                        Toast.makeText(CartActivity.this, "Please enter address or select any option ", Toast.LENGTH_SHORT).show();

                        // fix crash fragment
                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();

                        return;
                    }

                }

                if (TextUtils.isEmpty(adress)) {
                    Toast.makeText(CartActivity.this, "Please enter address or select any option ", Toast.LENGTH_SHORT).show();
                    // fix crash fragment
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();

                    return;
                }
              //  if (shippingAddess != null) {
                    Request object = new Request();
                    object.setName(Common.currentUser.getName());
                    object.setPhone(Common.currentUser.getPhone());
                    object.setAddress(adress);

                   if (rdShipToAdress.isChecked()){
                        try {
//
                            object.setLatlng(String.format("%s,%s", shippingAddess_Lat, shippingAddess_Lng));

                            shippingAddess_Lat = 0.0;
                            shippingAddess_Lng = 0.0;

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }


                    }
                    else
                   {
                       try {

                           //object.setLatlng(String.format("%s,%s", shippingAddess.getLatLng().latitude, shippingAddess.getLatLng().longitude));
                       }
                       catch (Exception e)
                       {
                           e.printStackTrace();
                       }
                   }


                    object.setTotal(txtTotalPrice.getText().toString());
                    object.setOrderList(cartArrayList);
                    //object.setStatus("0");
                    object.setComment(editTextComment.getText().toString());

                    String order_number = String.valueOf(System.currentTimeMillis());
                    // submit to firebase we will use current mili seconds  for request key
                    request_db_ref.child(order_number).setValue(object);
                    // delete cart
                    HomeActivity.myAppDatabase.myDao().clearCart();

                    // Send Notification

                   sendNotificationOrder(order_number);
              //  }
//                Toast.makeText(CartActivity.this, "Thank you for order place", Toast.LENGTH_SHORT).show();
//               finish();



            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                /// its tooo much important to decalare view null beacuse without this it creates null pointer exception :)
                view=null;
                //  we have to remove  fragment so it can prevent from crash,why  because in xml i use placeholder fragment
                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();

                dialog.dismiss();

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
                                //  we have to remove  fragment so it can prevent from crash,why  because in xml i use placeholder fragment
                                getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment)).commit();

                                finish();
                            } else {
                                Toast.makeText(CartActivity.this, "Failed!!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                            Toast.makeText(CartActivity.this, t.getMessage()+"", Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();

        if (view!=null)
        {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }


    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartViewHolder) {

            String name = ((CartAdapter) recyclerView_cartList.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProduct_name();

            final Order deleteItem = ((CartAdapter) recyclerView_cartList.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);

            HomeActivity.myAppDatabase.myDao().removeFromCart(deleteItem.getProduct_id(), Common.currentUser.getPhone());
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
            Snackbar snackBar = Snackbar.make(rootLayout, name + "  removed from  cart!!", Snackbar.LENGTH_LONG);

            snackBar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    adapter.restoreItem(deleteItem, deleteIndex);

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


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        displayLocation();
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            Log.e("Loction", "Your Location" + mLastLocation.getLatitude() + ", " + mLastLocation.getLongitude());

        } else {
            Log.e("Loction", "Coudn't get your location!!");
        }

    }


    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();

    }
}
