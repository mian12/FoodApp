package com.solution.alnahar.eatit.foodDetail;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.HomeActivity;
import com.solution.alnahar.eatit.Model.Food;
import com.solution.alnahar.eatit.Model.Order;
import com.solution.alnahar.eatit.Model.Rating;
import com.solution.alnahar.eatit.R;
import com.solution.alnahar.eatit.SQLiteDatabaseHelper;
import com.solution.alnahar.eatit.subCategory.FoodListActivity;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetailsActivity extends AppCompatActivity  implements RatingDialogListener{

    TextView food_name, food_price, food_description;
    ImageView food_Image;

    CollapsingToolbarLayout collapsingToolbarLayout;
    CounterFab btnCartDetail;
    ElegantNumberButton elegantNumberButton;
    String foodId = "";

    FirebaseDatabase database;
    DatabaseReference food_db_ref;
    DatabaseReference rating_db_ref;

    SQLiteDatabaseHelper sqLiteDatabaseHelper;

    Food currentFood;


    FloatingActionButton btnRating;
    RatingBar ratingBar;


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

        setContentView(R.layout.activity_food_details);


        // init firebase
        database = FirebaseDatabase.getInstance();
        food_db_ref = database.getReference("Foods");
        rating_db_ref= database.getReference("Rating");


        if (getIntent() != null) {
            foodId = getIntent().getExtras().getString("FoodId").toString();
            if (foodId != null && !foodId.isEmpty()) {

                if (Common.isConnectedToInternet(getApplicationContext())) {
                    getDetailFood(foodId);
                    getRatingFood(foodId);
                } else {

                    Toast.makeText(FoodDetailsActivity.this, "Please check your internet connection!!", Toast.LENGTH_SHORT).show();
                    return;

                }


            }

        }



        sqLiteDatabaseHelper = new SQLiteDatabaseHelper(this);

        food_name = findViewById(R.id.foodName);
        food_price = findViewById(R.id.food_price);
        food_description = findViewById(R.id.food_description);

        food_Image = findViewById(R.id.img_food);

        btnRating=findViewById(R.id.btnCart_detail);
        ratingBar=findViewById(R.id.ratingBar);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });




        elegantNumberButton = findViewById(R.id.numberCounter);


        try {
            String productQty=HomeActivity.myAppDatabase.myDao().getItemQuantityForSpecificeItem(foodId);
          //  Log.e("qty",productQty);

            elegantNumberButton.setNumber(productQty);
        }
        catch (Exception e)
        {
            e.getMessage();
        }


        collapsingToolbarLayout = findViewById(R.id.collapsing);
        //collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAdapter);

        btnCartDetail = findViewById(R.id.btnCart_detail);

        btnCartDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Order object = new Order();

                String qty=elegantNumberButton.getNumber();

                object.setProduct_id(foodId);
                object.setProduct_name(currentFood.getName());
                object.setQty(qty);
                object.setPhone(Common.currentUser.getPhone());
                object.setPrice(currentFood.getPrice());
                object.setImage(currentFood.getImage());

                String productId=HomeActivity.myAppDatabase.myDao().itemCartExitorNot(object.getProduct_id());
                if (productId==null)
                {
                    HomeActivity.myAppDatabase.myDao().addToCart(object);
                    // setting cart number dynimcally
                    btnCartDetail.setCount(HomeActivity.myAppDatabase.myDao().getCountCartItem());

                    Toast.makeText(FoodDetailsActivity.this, "added to cart", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    HomeActivity.myAppDatabase.myDao().updateCart(object.getQty(),object.getProduct_id());
                    Toast.makeText(FoodDetailsActivity.this, "updated to cart", Toast.LENGTH_SHORT).show();
                }


            }
        });

        // setting cart number dynimcally

        btnCartDetail.setCount(HomeActivity.myAppDatabase.myDao().getCountCartItem());





    }

    @Override
    protected void onResume() {
        super.onResume();

        // setting cart number dynimcally

     //   btnCartDetail.setCount(HomeActivity.myAppDatabase.myDao().getCountCartItem());
    }

    private void getRatingFood(String foodId) {

        Query foodRating=rating_db_ref.orderByChild("foodId").equalTo(foodId);
        foodRating.addValueEventListener(new ValueEventListener() {
            int count=0, sum=0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    Rating  item=dataSnapshot1.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRatingValue());
                    count++;


                }
                if (count!=0)
                {
                    float avg=sum/count;
                    ratingBar.setRating(avg);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void showRatingDialog() {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quick Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnimation)
                .create(FoodDetailsActivity.this)
                .show();
    }

    private void getDetailFood(String foodId) {

        food_db_ref.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.with(FoodDetailsActivity.this).load(currentFood.getImage()).into(food_Image);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, String comment) {
        // get rating and upload to firebase
        final Rating  rating=new Rating(Common.currentUser.getPhone(),foodId,String.valueOf(value),comment);

        rating_db_ref.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(Common.currentUser.getPhone()).exists())
                {
                    // remove old rating vale
                    rating_db_ref.child(Common.currentUser.getPhone()).removeValue();
                    // update new vale
                    rating_db_ref.child(Common.currentUser.getPhone()).setValue(rating);


                }
                else {

                    // insert new vale
                    rating_db_ref.child(Common.currentUser.getPhone()).setValue(rating);
                }
                Toast.makeText(FoodDetailsActivity.this, "Thank your for submit  rating !!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }
}
