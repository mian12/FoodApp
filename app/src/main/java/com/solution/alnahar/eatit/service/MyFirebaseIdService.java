package com.solution.alnahar.eatit.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.solution.alnahar.eatit.Common.Common;
import com.solution.alnahar.eatit.Model.Token;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String tokenRefreshed= FirebaseInstanceId.getInstance().getToken();

        if (Common.currentUser!=null)
        updateTokenToFirebase(tokenRefreshed);
    }

    private void updateTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase database=FirebaseDatabase.getInstance();
       DatabaseReference tokens_db_ref= database.getReference("Tokens");
        Token token=new Token(tokenRefreshed,false); // because this token is send from client side thats why is a false

        tokens_db_ref.child(Common.currentUser.getPhone()).setValue(token);
    }
}
