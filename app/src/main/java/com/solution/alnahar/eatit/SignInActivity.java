package com.solution.alnahar.eatit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.SingleLineTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.Model.Request;
import com.solution.alnahar.eatit.Model.User;
import com.solution.alnahar.eatit.cart.CartActivity;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SignInActivity extends AppCompatActivity {


    // Button btnSignIn;
    EditText edtPhone, edtPassword;
    CheckBox checkBoxRemember;
    TextView txtForgotPasword;

    SpotsDialog dialog;
    DatabaseReference table_user;
    FButton btnSignIn;


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


        setContentView(R.layout.activity_sign_in);

        edtPhone = (MaterialEditText) findViewById(R.id.editPhone);
        edtPassword = (MaterialEditText) findViewById(R.id.editPassword);

        btnSignIn = findViewById(R.id.btnSignIn);
        checkBoxRemember = findViewById(R.id.chkBox_rememberMe);
        txtForgotPasword = findViewById(R.id.txtForgotPasword);

        txtForgotPasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailogForgotPassord();
            }
        });


        // init paper
        Paper.init(this);


        dialog = new SpotsDialog(SignInActivity.this);
        dialog.setCancelable(false);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getApplicationContext())) {

                    // save user and passord for remember

                    if (checkBoxRemember.isChecked()) {

                        Paper.book().write(Common.USER_KEY, edtPhone.getText().toString());
                        Paper.book().write(Common.PASSWORD_KEY, edtPassword.getText().toString());

                    }
                    dialog.show();


                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {

                                dialog.dismiss();


                                User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);

                                //for using in Request table,so that if shiper does not find path the he cal call the customer
                                user.setPhone(edtPhone.getText().toString());

                                if (user.getPassword().equalsIgnoreCase(edtPassword.getText().toString())) {
                                    // Toast.makeText(SignInActivity.this, "Sign in Successfully!!", Toast.LENGTH_SHORT).show();
                                    Common.currentUser = user;


                                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();


                                    table_user.removeEventListener(this);

                                } else {
                                    Toast.makeText(SignInActivity.this, "Wrong password :(", Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                dialog.dismiss();
                                Toast.makeText(SignInActivity.this, "User does not exit", Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            dialog.dismiss();

                        }
                    });
                } else {

                    Toast.makeText(SignInActivity.this, "Please check your internet connection!!", Toast.LENGTH_SHORT).show();
                    return;

                }
            }
        });


    }

    private void dailogForgotPassord() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignInActivity.this);

        alertDialog.setTitle("Forgot Password");
        alertDialog.setMessage("Enter your secure code");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_forgot_password, null);
        final MaterialEditText editTextPhoneNumber = view.findViewById(R.id.editPhoneNumber);
        final MaterialEditText editTextSecureCode = view.findViewById(R.id.editSecureCode);


        alertDialog.setView(view);
        alertDialog.setIcon(R.drawable.ic_security_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {


                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (dataSnapshot.child(editTextPhoneNumber.getText().toString()).exists()) {

                            dialog.dismiss();


                            User user = dataSnapshot.child(editTextPhoneNumber.getText().toString()).getValue(User.class);

                            if (user.getSecureCode().equalsIgnoreCase(editTextSecureCode.getText().toString())) {
                                Toast.makeText(SignInActivity.this, user.getPassword().toString(), Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(SignInActivity.this, "Wrong secure code :(", Toast.LENGTH_SHORT).show();

                            }

                        } else {
                            Toast.makeText(SignInActivity.this, "User does not exit", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        dialog.dismiss();

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
