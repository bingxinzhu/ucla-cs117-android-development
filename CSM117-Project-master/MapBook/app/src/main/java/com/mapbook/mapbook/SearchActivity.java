package com.mapbook.mapbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

public class SearchActivity extends AppCompatActivity {
//    public BookInfo someBook;
//    public ZipCodeBook someZipCodeBook;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mAuth = FirebaseAuth.getInstance();
//        testingFunc(mAuth.getCurrentUser().getUid());
//        getBookByZipCodeAndTitle(((EditText) findViewById(R.id.editZipcode)).getText().toString(),"Kjnkjnkjn");
    //    getUserInfoByUserID(mAuth.getCurrentUser().getUid());
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
                Log.d(TAG, "Book title: " + value.title + "\n" +
                        "Author: " + value.author + "\n" +
                        "Publisher: " + value.publisher + "\n" +
                        "Zip code : " + value.zipCode);
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

    public void getBookInfoByZipCode(final String zipCode){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "ZipCodeBook/" + zipCode);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ZipCodeBook value = new ZipCodeBook();
                value.zipBookMap = (HashMap)dataSnapshot.getValue();
                Log.d(TAG, "Book ID in zip code " + zipCode + " are " + value.toString());
                for(final String key : value.zipBookMap.keySet()){
                    getBookInfoByBookID(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void getUserInfoByUserID(String userID){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "users/" + userID);
//        userRef.orderByChild("title")

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                Log.d(TAG, "Testing Value is: " + value.email + value.userID +
                        value.bookIDMap.keySet().toString());
                EditText tempEdit = findViewById(R.id.editZipcode);
                tempEdit.setText(value.email);
                for(final String key : value.bookIDMap.keySet()){
                    getBookInfoByBookID(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void getUserIDByBookID(String bookID){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "BookToUser/" + bookID);
//        userRef.orderByChild("title")

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "getUserIDByBookID Value is: " + value);
                EditText tempEdit = findViewById(R.id.editZipcode);
                tempEdit.setText(value);

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

    public void onCancelClick(View v) {
        Intent intent = new Intent(this, MainNavigation.class);
        startActivity(intent);
    }

    public void onConfirmClick(View v) {
        //getUserInfoByUserID(mAuth.getCurrentUser().getUid());

        Intent intent = new Intent(this, MainNavigation.class);
        EditText zip = (EditText) findViewById(R.id.editZipcode);
        String zipcode = zip.getText().toString();
        EditText title = (EditText) findViewById(R.id.editTitle);
        String bookTitle = title.getText().toString();
        intent.putExtra("zip", zipcode);
        intent.putExtra("title", bookTitle);
       // intent.putExtra("userEmail", bookTitle);
        Log.w(TAG, "on confirm print zip here");
        if(zip != null)
            Log.w(TAG, zipcode.toString());
        else
            Log.w(TAG, "print title here");
        Log.w(TAG, bookTitle);
        startActivity(intent);

//        getBookInfoByZipCode(((EditText) findViewById(R.id.editZipcode)).getText().toString());
//        mAuth = FirebaseAuth.getInstance();
//        testingFunc(mAuth.getCurrentUser().getUid());
//        getBookByZipCodeAndTitle(((EditText) findViewById(R.id.editZipcode)).getText().toString(),"Kjnkjnkjn");

    }
}
