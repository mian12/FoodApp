package com.solution.alnahar.eatit;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.facebook.accountkit.AccountKit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.Interface.ItemClickListner;
import com.solution.alnahar.eatit.Model.Banner;
import com.solution.alnahar.eatit.Model.Category;
import com.solution.alnahar.eatit.Model.Request;
import com.solution.alnahar.eatit.Model.Token;
import com.solution.alnahar.eatit.cart.CartActivity;
import com.solution.alnahar.eatit.foodDetail.FoodDetailsActivity;
import com.solution.alnahar.eatit.orderStatus.OrderStatusActivity;
import com.solution.alnahar.eatit.roomDb.MyAppDatabase;
import com.solution.alnahar.eatit.subCategory.FoodListActivity;
import com.solution.alnahar.eatit.viewHolder.MenuViewHolder;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static MyAppDatabase myAppDatabase;

    FirebaseDatabase database;
    DatabaseReference category;
    TextView txtFullUserName;
    RecyclerView recyclerView_menu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter adapter;
    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 400;


    SwipeRefreshLayout swipeRefreshLayout;

    Query query;

    CounterFab fab;


    // slider
    HashMap<String,String> imageList;
    SliderLayout sliderLayout;

    NavigationView navigationView;
    View view;
    DrawerLayout drawer;




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

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);


        //init room database
        myAppDatabase = Room.databaseBuilder(getApplicationContext(), MyAppDatabase.class, "FoodApp").allowMainThreadQueries().build();


        // init firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");





        // Note

/// i use it adpter here after geting the refernce variable of databse
        //if isue it on  onloAD  method then its work due to animation rules thats why i put here to see the effect of fall down animation :P
        // animation only  work with  static adapter (one time) but in dymanic we cant use animation

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category, Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_menu_item, parent, false);

                return new MenuViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
                holder.menuName.setText(model.getName());


                Picasso.with(HomeActivity.this)
                        .load(model.getImage())
                        .resize(MAX_WIDTH, MAX_HEIGHT)
                        .centerCrop()
                        .into(holder.menuImage);

                final Category object = model;


                holder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, Boolean isLongClick) {
                        Toast.makeText(HomeActivity.this, "" + object.getName(), Toast.LENGTH_SHORT).show();
                        String catId = adapter.getRef(position).getKey();
                        Intent intent = new Intent(HomeActivity.this, FoodListActivity.class);
                        intent.putExtra("categoryId", catId);
                        startActivity(intent);


                    }
                });
            }

        };


        //paper init
        Paper.init(this);

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_red_dark
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (Common.isConnectedToInternet(getApplicationContext())) {
                    // load menu
                    LoadMenu();
//                    sliderLayout.removeAllSliders();
//                    setupSlider();
                } else {

                    Toast.makeText(HomeActivity.this, "Please check your internet connection!!", Toast.LENGTH_SHORT).show();
                    return;

                }


            }
        });


        recyclerView_menu = findViewById(R.id.recyclerView_Item);
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView_menu.setLayoutManager(layoutManager);

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView_menu.getContext(), R.anim.layout_fall_down);

        recyclerView_menu.setLayoutAnimation(controller);


         fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        // fab setting cart value
        int itemCounter=myAppDatabase.myDao().getCountCartItem();
        fab.setCount(itemCounter);





       drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView= (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        try {
            // set name  for user
            view = navigationView.getHeaderView(0);
            txtFullUserName = view.findViewById(R.id.userName);
            txtFullUserName.setText(Common.currentUser.getName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        if (Common.isConnectedToInternet(getApplicationContext())) {
            // load menu
            LoadMenu();
        } else {

            Toast.makeText(HomeActivity.this, "Please check your internet connection!!", Toast.LENGTH_SHORT).show();
            return;

        }


// register token of each device

        updateToken(FirebaseInstanceId.getInstance().getToken());


        // setup slider
        // call this after  init  firebase database
        
        setupSlider();


    }

    private void setupSlider() {

        // slider init
        sliderLayout=findViewById(R.id.slider);
        imageList=new HashMap<>();

      final DatabaseReference banner_db_ref= database.getReference("Banner");
      banner_db_ref.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {

              for (DataSnapshot postSnapShort:dataSnapshot.getChildren())
              {
                  Banner banner=postSnapShort.getValue(Banner.class);
                  imageList.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());

              }
              imageList.size();

              for (String key:imageList.keySet())
              {
                String[] keySplit=  key.split("@@@");
                String nameOfFood=keySplit[0];
                String idOfFood=keySplit[1];


                // create slider
                   final TextSliderView  textSliderView=new TextSliderView(getBaseContext());

                  textSliderView.description(nameOfFood)
                                .image(imageList.get(key))
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {

                                        Intent intent=new Intent(HomeActivity.this,FoodDetailsActivity.class);
                                        // we will send food id to food detail class
                                        intent.putExtras(textSliderView.getBundle());
                                        startActivity(intent);

                                    }
                                });

                  // add extra bundle
                  textSliderView.bundle(new Bundle());
                  textSliderView.getBundle().putString("FoodId",idOfFood);

                  sliderLayout.addSlider(textSliderView);

                  // remove event after finish
                  banner_db_ref.removeEventListener(this);




              }

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });

      sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
      sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
      sliderLayout.setCustomAnimation(new DescriptionAnimation());
      sliderLayout.setDuration(4000);

    }

    private void updateToken(String token) {

        try {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference tokens_db_ref = database.getReference("Tokens");
            Token data = new Token(token, false); // because this token is send from client side thats why is a false
            tokens_db_ref.child(Common.currentUser.getPhone()).setValue(data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        int itemCounter=myAppDatabase.myDao().getCountCartItem();
        fab.setCount(itemCounter);



    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Common.isConnectedToInternet(getApplicationContext())) {
            adapter.startListening();
        } else {

            Toast.makeText(HomeActivity.this, "Please check your internet connection!!", Toast.LENGTH_SHORT).show();
            return;

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        sliderLayout.stopAutoCycle();

    }

    public void LoadMenu() {


        // use it adapter in oncreate method because Animation requrite adapter after firebse databse instance//
        // if isue it here then animation not effect thats why  i use this commented code in ocrate method after initalization the real time databse
//
//        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
//                .setQuery(category, Category.class)
//                .build();
//        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
//            @Override
//            public MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.row_menu_item, parent, false);
//
//                return new MenuViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull Category model) {
//                holder.menuName.setText(model.getName());
//
//
//                Picasso.get()
//                        .load(model.getImage())
//                        .resize(MAX_WIDTH, MAX_HEIGHT)
//                        .centerCrop()
//                        .into(holder.menuImage);
//
//                final Category object = model;
//
//
//                holder.setItemClickListner(new ItemClickListner() {
//                    @Override
//                    public void onClick(View view, int position, Boolean isLongClick) {
//                        Toast.makeText(HomeActivity.this, "" + object.getName(), Toast.LENGTH_SHORT).show();
//                        String catId = adapter.getRef(position).getKey();
//                        Intent intent = new Intent(HomeActivity.this, FoodListActivity.class);
//                        intent.putExtra("categoryId", catId);
//                        startActivity(intent);
//
//
//                    }
//                });
//            }
//
//        };

        adapter.startListening();
        recyclerView_menu.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);


        // animation
        recyclerView_menu.getAdapter().notifyDataSetChanged();
        recyclerView_menu.scheduleLayoutAnimation();


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {

            Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(cartIntent);

        }

        else if (id == R.id.nav_homeAdress) {

            showHomeAddressDialog();

        }
        else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(HomeActivity.this, OrderStatusActivity.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_changeName) {
//            Intent orderIntent = new Intent(HomeActivity.this, OrderStatusActivity.class);
//            startActivity(orderIntent);

            //changePasswordDialog();
            changeUserNameDialog();

        } else if (id == R.id.nav_logout) {

            //  delete remember me user name and password
           // Paper.book().destroy();

           if (AccountKit.getCurrentAccessToken()!=null);
                AccountKit.logOut();


            Intent logOut = new Intent(HomeActivity.this, MainActivity.class);
            logOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logOut);
            finish();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHomeAddressDialog() {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Change Home Address");
        alertDialog.setMessage("Please fill all information");


        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_home_address, null);

        final MaterialEditText editTextHomeAddress = view.findViewById(R.id.editHomeAddress);
        alertDialog.setView(view);
//        alertDialog.setIcon(R.drawable.ic_home_black_24dp);



        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {


                    final android.app.AlertDialog waitingDialog = new SpotsDialog(HomeActivity.this);

                    waitingDialog.show();

                Common.currentUser.setHomeAddress(editTextHomeAddress.getText().toString());

                FirebaseDatabase.getInstance().getReference("User")
                        .child(Common.currentUser.getPhone())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                waitingDialog.dismiss();

                                Toast.makeText(HomeActivity.this, "Update Address Successfull", Toast.LENGTH_SHORT).show();
                            }
                        });




            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();


            }
        });

        alertDialog.show();

    }

    private void changePasswordDialog() {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Change Password");
        alertDialog.setMessage("Please fill all information");


        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.dialog_update_name, null);

        final MaterialEditText editTextOldPassword = view.findViewById(R.id.editOldPssword);

        final MaterialEditText editTextNewPassword = view.findViewById(R.id.editNewPassword);
        final MaterialEditText editTextRepeatPassword = view.findViewById(R.id.editRepeatPassword);


        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_security);
        alertDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                final android.app.AlertDialog waitingDialog = new SpotsDialog(HomeActivity.this);
                waitingDialog.show();


                // check old password

                if (editTextOldPassword.getText().toString().equals(Common.currentUser.getPassword().toString())) {
                    // check new password with repeat password
                    if (editTextNewPassword.getText().toString().equals(editTextRepeatPassword.getText().toString())) {

                        HashMap<String, Object> updatePasssword = new HashMap<>();
                        updatePasssword.put("Password", editTextNewPassword.getText().toString());

                        // make update

                        DatabaseReference user_db_ref = FirebaseDatabase.getInstance().getReference("User");
                        user_db_ref.child(Common.currentUser.getPhone()).updateChildren(updatePasssword)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        waitingDialog.dismiss();

                                        Common.currentUser.setPassword(editTextNewPassword.getText().toString());

                                        Toast.makeText(HomeActivity.this, "Password updated!!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        waitingDialog.dismiss();
                                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });


                    } else {

                        waitingDialog.dismiss();
                        Toast.makeText(HomeActivity.this, "New password doesn't match", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "Wrong old  password!!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();

    }

    private void changeUserNameDialog() {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Update Name");
        alertDialog.setMessage("Please fill all information");


        LayoutInflater inflater = LayoutInflater.from(this);

        final View view = inflater.inflate(R.layout.dialog_update_name, null);

        final MaterialEditText editTextName= view.findViewById(R.id.editName);

        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_security);
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {


                // update name here

                Map<String,Object> update_name=new HashMap<>();
                update_name.put("name",editTextName.getText().toString());

                FirebaseDatabase.getInstance().getReference("User")
                                                .child(Common.currentUser.getPhone())
                                                .updateChildren(update_name)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        dialog.dismiss();

                                                        if (task.isSuccessful()) {

                                                            // set name  for user
                                                            // update the global user
                                                            Common.currentUser.setName(editTextName.getText().toString());
                                                            txtFullUserName.setText(Common.currentUser.getName());



                                                            Toast.makeText(HomeActivity.this, "Name updated!!!", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });





            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();

    }
}
