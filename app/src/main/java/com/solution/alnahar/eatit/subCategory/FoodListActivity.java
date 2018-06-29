package com.solution.alnahar.eatit.subCategory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.HomeActivity;
import com.solution.alnahar.eatit.Interface.ItemClickListner;
import com.solution.alnahar.eatit.Model.Category;
import com.solution.alnahar.eatit.Model.Food;
import com.solution.alnahar.eatit.Model.Order;
import com.solution.alnahar.eatit.R;
import com.solution.alnahar.eatit.SQLiteDatabaseHelper;
import com.solution.alnahar.eatit.SignInActivity;
import com.solution.alnahar.eatit.foodDetail.FoodDetailsActivity;
import com.solution.alnahar.eatit.roomDb.Favourites;
import com.solution.alnahar.eatit.viewHolder.FoodListViewHolder;
import com.solution.alnahar.eatit.viewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodListActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference foodList_db_ref;

    RecyclerView recyclerView_foodList;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Food, FoodListViewHolder> adapter;

    public static String categoryId = "";
    SpotsDialog dialog;

    // Search functionality///
    FirebaseRecyclerAdapter<Food, FoodListViewHolder> searchAdapter;
    List<String> suggestedList = new ArrayList<String>();
    MaterialSearchBar materialSearchBar;

    SQLiteDatabaseHelper  sqLiteDatabaseHelper;


    SwipeRefreshLayout swipeRefreshLayout;

    // aFacebbok share
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    // create target from picasso
    Target target=new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            // create photo from bitmap
            SharePhoto sharePhoto=new SharePhoto.Builder()
                                .setBitmap(bitmap)
                                .build();
            if (ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content=new SharePhotoContent.Builder()
                                    .addPhoto(sharePhoto)
                                    .build();
                shareDialog.show(content);


            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }



        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/LBRITE.TTF")
                .setFontAttrId(R.attr.fontPath)
                .build());

        setContentView(R.layout.activity_food_list);


        // init faceBook

        callbackManager=CallbackManager.Factory.create();
        shareDialog=new ShareDialog(this);




        swipeRefreshLayout=findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (getIntent() != null) {
                    categoryId = getIntent().getExtras().getString("categoryId").toString();
                    if (categoryId != null && !categoryId.isEmpty()) {


                        if (Common.isConnectedToInternet(getApplicationContext())) {
                            LoadListFood(categoryId);
                        } else {

                            Toast.makeText(FoodListActivity.this, "Please check your internet connection!!", Toast.LENGTH_SHORT).show();
                            return;

                        }


                    }

                }


            }
        });


        sqLiteDatabaseHelper=new SQLiteDatabaseHelper(this);

        // init firebase
        database = FirebaseDatabase.getInstance();
        foodList_db_ref = database.getReference("Foods");

        dialog = new SpotsDialog(FoodListActivity.this);
        dialog.setCancelable(false);

        recyclerView_foodList = findViewById(R.id.food_list);
        layoutManager = new LinearLayoutManager(this);
        recyclerView_foodList.setHasFixedSize(true);
        recyclerView_foodList.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            categoryId = getIntent().getExtras().getString("categoryId").toString();
            if (categoryId != null && !categoryId.isEmpty()) {


                if (Common.isConnectedToInternet(getApplicationContext())) {
                    LoadListFood(categoryId);
                } else {

                    Toast.makeText(FoodListActivity.this, "Please check your internet connection!!", Toast.LENGTH_SHORT).show();
                    return;

                }


            }

        }


        materialSearchBar = findViewById(R.id.material_searchBar);
        materialSearchBar.setHint("Enter Your Food.....");

        loadSuggested();

        materialSearchBar.setLastSuggestions(suggestedList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // when user type text then we will change  suggest list
                List<String> suggest = new ArrayList<String>();
                for (String search : suggestedList) {

                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())) {
                        suggest.add(search);
                    }

                    materialSearchBar.setLastSuggestions(suggest);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // when search bar is closed
                //  restore orginal suggest adapter
                if (!enabled) {
                    recyclerView_foodList.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                // when search finish
                // show reault of  search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });


    }

    private void startSearch(CharSequence text) {


        Query query = foodList_db_ref.orderByChild("name").equalTo(text.toString()); /// compare name
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query, Food.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodListViewHolder>(options) {

            @NonNull
            @Override
            public FoodListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_food_list, parent, false);

                return new FoodListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FoodListViewHolder holder, int position, @NonNull Food model) {

                holder.foodListName.setText(model.getName());


                Picasso.with(FoodListActivity.this).load(model.getImage()).into(holder.foodListImage);

                final Food object = model;


                holder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        Toast.makeText(FoodListActivity.this, "" + object.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodListActivity.this, FoodDetailsActivity.class);
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);

                        Toast.makeText(FoodListActivity.this, "menu ID----" + object.getMenuId(), Toast.LENGTH_SHORT).show();


                    }
                });

            }


        };

        searchAdapter.startListening();
        recyclerView_foodList.setAdapter(searchAdapter);

    }

    private void loadSuggested() {

        foodList_db_ref.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postDataSnapshot : dataSnapshot.getChildren()) {

                    Food item = postDataSnapshot.getValue(Food.class);
                    suggestedList.add(item.getName());// add  name of food to suggested list


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (categoryId != null && !categoryId.isEmpty()) {
            LoadListFood(categoryId);
        }

    }

    private void LoadListFood(final String categoryId) {

        // DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        Query query = foodList_db_ref.orderByChild("menuId").equalTo(categoryId);


        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(query, Food.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Food, FoodListViewHolder>(options) {
            @Override
            public FoodListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_food_list, parent, false);

                return new FoodListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FoodListViewHolder holder, final int position, @NonNull final Food model) {
                holder.foodListName.setText(model.getName());


                holder.foodListPrice.setText(model.getPrice());


                //add to cart
                holder.image_quick_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        String product_Id=adapter.getRef(position).getKey();

                        Order object = new Order();
                        object.setProduct_id(product_Id);
                        object.setProduct_name(model.getName());
                        object.setQty("1");
                        object.setPhone(Common.currentUser.getPhone());
                        object.setPrice(model.getPrice());
                        object.setImage(model.getImage());


                        String productId=HomeActivity.myAppDatabase.myDao().itemCartExitorNot(object.getProduct_id());
                        if (productId==null)
                        {
                            HomeActivity.myAppDatabase.myDao().addToCart(object);
                            Toast.makeText(FoodListActivity.this, "added to cart", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            HomeActivity.myAppDatabase.myDao().updateCart(object.getQty(),object.getProduct_id());
                            Toast.makeText(FoodListActivity.this, "updated to cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                final String itemId=adapter.getRef(position).getKey();
               final String phoneNo= String.valueOf(Common.currentUser.getPhone());
                // add favourites

               String idReturn= HomeActivity.myAppDatabase.myDao().isFavourite(itemId,phoneNo);
                if (idReturn==null) {
                    holder.favtFood.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
                else
                {
                    holder.favtFood.setImageResource(R.drawable.ic_favorite_red_24dp);
                }


                // share phot0
                holder.image_share_fb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(FoodListActivity.this, "please wait now...", Toast.LENGTH_SHORT).show();
                        Picasso.with(FoodListActivity.this).load(model.getImage()).into(target);
                    }
                });

                holder.favtFood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Food object = model;
                     //   Toast.makeText(FoodListActivity.this, "menu ID----"+"id.."+itemId+"  Name..." + object.getName()+"pos.."+position, Toast.LENGTH_SHORT).show();


                        String itemId=adapter.getRef(position).getKey();

                         Favourites favourites=new Favourites();
                        favourites.setProductId(itemId);
                        favourites.setPhoneNumber(phoneNo);



                        String idReturn= HomeActivity.myAppDatabase.myDao().isFavourite(itemId,phoneNo);
                        if (idReturn==null)
                        {
                            HomeActivity.myAppDatabase.myDao().addTofavourite(favourites);
                            holder.favtFood.setImageResource(R.drawable.ic_favorite_red_24dp);
                            Toast.makeText(FoodListActivity.this, model.getName()+" added to favourite", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            HomeActivity.myAppDatabase.myDao().removeFavouriteItemId(itemId,phoneNo);
                            holder.favtFood.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FoodListActivity.this, model.getName()+" removed from favourite", Toast.LENGTH_SHORT).show();
                        }

                    }
                });



                Picasso.with(FoodListActivity.this).load(model.getImage()).into(holder.foodListImage);

                final Food object = model;


                holder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                      //  Toast.makeText(FoodListActivity.this, "" + object.getName(), Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodListActivity.this, FoodDetailsActivity.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);

                       // Toast.makeText(FoodListActivity.this, "menu ID----" + object.getMenuId(), Toast.LENGTH_SHORT).show();


                    }
                });
            }

        };

        adapter.startListening();
        recyclerView_foodList.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        if (searchAdapter!=null)
        {
            searchAdapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Common.isConnectedToInternet(getApplicationContext())) {
            adapter.startListening();
        } else {

            Toast.makeText(FoodListActivity.this, "Please check your internet connection!!", Toast.LENGTH_SHORT).show();
            return;

        }


    }
}
