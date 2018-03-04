package com.mapbook.mapbook;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.DownloadManager;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.Intent;
import static android.content.ContentValues.TAG;
import android.widget.EditText;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DetailedBookInfoActivity extends AppCompatActivity {
    private BookInfo bookCreated;
    private String zipCode;
    private String status;
    private String booktitle;
    private String bookprice;
    private String sellerMail;
    private String userID;
    private String bookID;
    private String markerID;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private RequestAccess requestAccessDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_detailed_book_info);
        TextView lblTitle=(TextView)findViewById(R.id.lblTitle);
        TextView bigbooktitle=(TextView)findViewById(R.id.bigbooktitle);

        Intent intent = getIntent();
        booktitle=intent.getStringExtra("title");
        bookprice = intent.getStringExtra("price");
        zipCode = intent.getStringExtra("zipCode");
        markerID = intent.getStringExtra("markerId");
        bookID = intent.getStringExtra("bookID");
        // TO-DO
        sellerMail = "2@qq.com";
        
        String display =
                "        Author: " + intent.getStringExtra("author") + "\n" +
                "        ISBN: " + intent.getStringExtra("isbn")  + "\n" +
                "        Subject: " + intent.getStringExtra("subject")  + "\n" +
                "        Price: " + intent.getStringExtra("price")  ;

        lblTitle.setText(display);
        bigbooktitle.setText(booktitle);
        lblTitle.setTextSize(20);
        getUserIDByBookID(bookID);

    }

    public void onCancelButton(View v) {
        Intent intent = new Intent(this, MainNavigation.class);
        intent.putExtra("zip", zipCode);
        startActivity(intent);
    }

    public void onBuyButton(View v) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Intent intent = new Intent(this, MainNavigation.class);
        //intent.putExtra("bookStatusChanges", markerID);
        //intent.putExtra("zip", zipCode);
        //Log.w(TAG, "send book status change signal");
        //startActivity(intent);
        String display = "";
        Log.w(TAG, "sellerID of the book is" + userID);
        Log.w(TAG, "buyerID of the book is" + mAuth.getCurrentUser().getUid());
        if(new String(userID).equals(mAuth.getCurrentUser().getUid())){
            display = "You can't buy books owned by yourself";
        } else {
            Log.w(TAG, "bookID of the book is" + bookID);
            if(changeStatus(bookID, "RESERVED")) {
                display = "The status of the book has been changed";
                requestAccessDB = new RequestAccess();
                requestAccessDB.addBookToUser(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getUid(),
                        booktitle, "a", "isbn", "pub","sub",bookprice,"BUY","zip",
                        "long", "lat");

            } else {
                display = "The status of the book has not been changed";
            }
        }
        TextView lblTitle=(TextView)findViewById(R.id.lblTitle);

        lblTitle.setText(display);
    }

    public boolean changeStatus(String bookID, String newStatus){
        if(mDatabase != null && mDatabase.child("BookDB") != null && mDatabase.child("BookDB").child(bookID) != null){
            mDatabase.child("BookDB").child(bookID).child("status").setValue(newStatus);
            return true;
        }  else return false;
    }

    public void onContactSellerButton(View v) {
        //getUserInfoByUserID(mAuth.getCurrentUser().getUid());

        Log.d(TAG, "on contact seller function: " + userID);

        //getUserInfoByUserID(userID);
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("userID", userID);
        Log.d(TAG, "Book id: " + bookID + "\n" +
                "userID: " + userID + "\n" +
                "sellerMail: " +  sellerMail);
        startActivity(intent);
    }

    public void getBookInfoByBookID(String bookID){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "BookDB/" + bookID);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                BookInfo value = dataSnapshot.getValue(BookInfo.class);
                //Log.d(TAG, "Book title: " + value.title + "\n" +
                //        "Author: " + value.author + "\n" +
                //        "Publisher: " + value.publisher + "\n" +
                 //       "Zip code : " + value.zipCode);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void getUserByBookID(String bookID){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "users/");
        userRef.orderByChild("bookIDMap").equalTo(bookID);

    }
    public void getUserInfoByUserID(final String userID){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "users/" + userID);
//        userRef.orderByChild("title")

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                Log.d(TAG, "getUserInfoByUserID testing Value user id is: " + userID);
                Log.d(TAG, "getUserInfoByUserID testing Value user class is: " + value);
                Log.d(TAG, "getUserInfoByUserID testing Value seller email is: " + value.email);
                sellerMail = value.email;
                //Log.d(TAG, "Testing Value is: " + value.email + value.userID +
                //        value.bookIDMap.keySet().toString());
                //EditText tempEdit = findViewById(R.id.editZipcode);
                //tempEdit.setText(value.email);
                //for(final String key : value.bookIDMap.keySet()){
                //    getBookInfoByBookID(key);
                //}
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void getUserIDByBookID(final String bookID){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "BookToUser/" + bookID);
//        userRef.orderByChild("title")

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userID = dataSnapshot.getValue(String.class);
                //String userID = dataSnapshot.getValue(String.class);
                Log.d(TAG, "getUserIDByBookID book id is: " + bookID);
                Log.d(TAG, "getUserIDByBookID user id is: " + userID);
                //EditText tempEdit = findViewById(R.id.editZipcode);
                //tempEdit.setText(userID);
                getUserInfoByUserID(userID);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void getBookByZipCodeAndTitle(final String zipCode, String title){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "BookDB");
        userRef.orderByChild("title").
                equalTo(title).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BookInfo tempBook = dataSnapshot.getValue(BookInfo.class);
                if(tempBook.zipCode.equals(zipCode)) {
                    Log.d(TAG, "Book title: " + tempBook.title + "\n" +
                            "Author: " + tempBook.author + "\n" +
                            "Publisher: " + tempBook.publisher + "\n" +
                            "Zip code : " + tempBook.zipCode);
                }
//                Query query = userRef.orderByChild("zipCode").equalTo(zipCode);
//                query.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        BookInfo tempBook = dataSnapshot.getValue(BookInfo.class);
//                        Log.d(TAG, "Book title: " + tempBook.title + "\n" +
//                                "Author: " + tempBook.author + "\n" +
//                                "Publisher: " + tempBook.publisher + "\n" +
//                                "Zip code : " + tempBook.zipCode);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                BookInfo tempBook = dataSnapshot.getValue(BookInfo.class);
                Log.d(TAG, "Book title: " + tempBook.title + "\n" +
                        "Author: " + tempBook.author + "\n" +
                        "Publisher: " + tempBook.publisher + "\n" +
                        "Zip code : " + tempBook.zipCode);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                BookInfo tempBook = dataSnapshot.getValue(BookInfo.class);
                Log.d(TAG, "Book title: " + tempBook.title + "\n" +
                        "Author: " + tempBook.author + "\n" +
                        "Publisher: " + tempBook.publisher + "\n" +
                        "Zip code : " + tempBook.zipCode);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                BookInfo tempBook = dataSnapshot.getValue(BookInfo.class);
                Log.d(TAG, "Book title: " + tempBook.title + "\n" +
                        "Author: " + tempBook.author + "\n" +
                        "Publisher: " + tempBook.publisher + "\n" +
                        "Zip code : " + tempBook.zipCode);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }




}
