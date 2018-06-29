package com.solution.alnahar.eatit;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.Model.User;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {


    MaterialEditText  edtPhone,edtPassword,edtName,editSecureCode;
    FButton signUp;


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

        setContentView(R.layout.activity_sign_up);

        edtPhone=findViewById(R.id.editPhone);
        edtPassword=findViewById(R.id.editPassword);
        edtName=findViewById(R.id.editName);
        editSecureCode=findViewById(R.id.editSecureCode);

        signUp=findViewById(R.id.btnSignUp);



        final SpotsDialog dialog=new SpotsDialog(SignUpActivity.this);
        dialog.setCancelable(false);

        final FirebaseDatabase database= FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Common.isConnectedToInternet(getApplicationContext())) {

                    dialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                dialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Phone number already exits!!", Toast.LENGTH_SHORT).show();
                            } else

                            {
                                dialog.dismiss();

                                User user = new User();
                                user.setName(edtName.getText().toString());
                                user.setPassword(edtPassword.getText().toString());
                                user.setIsStaff("false");
                                user.setSecureCode(editSecureCode.getText().toString());

                                //  user.setPhone(edtPhone.getText().toString());

                                table_user.child(edtPhone.getText().toString()).setValue(user);

                                Toast.makeText(SignUpActivity.this, "Sign up successfully!!", Toast.LENGTH_SHORT).show();
                                finish();


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {

                    Toast.makeText(SignUpActivity.this, "Please check your internet connection!!", Toast.LENGTH_SHORT).show();
                    return;

                }
            }
        });






    }
}
