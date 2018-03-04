package com.mapbook.mapbook;

import android.app.usage.UsageEvents;
import android.support.v7.app.AppCompatActivity;
import android.util.EventLog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static android.content.ContentValues.TAG;

/**
 * Created by g090617 on 11/6/17.
 */

public class RequestAccess extends  CreateActivity{
    private DatabaseReference mDatabase;
    public RequestAccess(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    final ArrayList<BookInfo> bookInfoArrayList = new ArrayList<>(10);
    static BookInfo someBook;
    static User someUser;

    public void writeNewUser(String userId, String email) {
        User user = new User(userId, email);

        mDatabase.child("users").child(userId).setValue(user);

    }

    public void addBookToUser(final String userId, String email, String title, String author, String isbn,
                              String publisher,
                              String subject, String price, String status, String zipCode,
                              String longtitude, String latitude){
        User user = new User(userId, email);
        final DatabaseReference newRef = mDatabase.child("users").child(userId).child("bookIDMap").push();//create a new book id

        BookInfo book = new BookInfo(newRef.getKey().toString(), status);

        newRef.setValue(book);

        addBookToBookDatabase(newRef.getKey().toString(), title, author, isbn, publisher, subject,
                price, status, zipCode, longtitude, latitude);

        addBookToLocation(newRef.getKey().toString(), zipCode);

        addBookUserRef(newRef.getKey().toString(), userId);

//        changeStatus(newRef.getKey().toString(), "RESERVED");
    }

    public void addBookToBookDatabase(String bookID, String title, String author, String isbn,
                                      String publisher,
                                      String subject, String price, String status, String zipCode,
                                      String longtitude, String latitude){
        BookInfo book = new BookInfo(bookID, title, author, isbn, publisher,
                subject, price, status, zipCode, longtitude, latitude);
        mDatabase.child("BookDB").child(bookID).setValue(book);
    }

    public void changeStatus(String bookID, String newStatus){
        mDatabase.child("BookDB").child(bookID).child("status").setValue(newStatus);
    }

    public void addBookToLocation(String bookID, String zipCode){
//        BookInfo book = new BookInfo(bookID, "Buy");
        mDatabase.child("ZipCodeBook").child(zipCode).child(bookID).setValue(bookID);
    }

    public void getUser(String userID){
        Log.w(TAG, "In get user");
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "users/" + userID);


        final TaskCompletionSource<Object> tcs = new TaskCompletionSource<>();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User value = dataSnapshot.getValue(User.class);
                Log.d(TAG, "Value is: " + value.email + value.userID + value.bookIDMap.keySet().toString());

                tcs.setResult(value);
//                for(final String key : value.bookIDMap.keySet()){
//
//                    getterBookInFoByBookID(key);
//                }
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        Task<Object> t = tcs.getTask();
        try{
            Tasks.await(t);
        }catch (ExecutionException | InterruptedException d){

        }

        User tempUser;
        if(t.isSuccessful()){
            Object result = t.getResult();
            tempUser = (User)result;
            Log.w(TAG, "Successful  ===============" + tempUser.email);
            this.someUser = tempUser;

        }

    }

    public void getBookInfoByBookID(String bookID){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "BookDB/" + bookID);

        final TaskCompletionSource<Object> tcs = new TaskCompletionSource<>();


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                BookInfo value = dataSnapshot.getValue(BookInfo.class);
                Log.d(TAG, "Book is: " + value.title + value.author + value.publisher + value.zipCode);
//                titleField.setText("adadasdasd");
                tcs.setResult(value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        Task<Object> t = tcs.getTask();
        try{
            Tasks.await(t);
        }catch (ExecutionException | InterruptedException d){

        }

        BookInfo tempBook;
        if(t.isSuccessful()){
            Object result = t.getResult();
            tempBook = (BookInfo)result;
            Log.w(TAG, "Successful ===============" + tempBook.title);
            someBook = tempBook;
        }

    }

    public BookInfo getterBookInFoByBookID(final String bookID){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                getBookInfoByBookID(bookID);
            }
        });
        Log.w(TAG, "Successful somebook ===============" + this.someBook.title);
        return this.someBook;
    }

    public void testingFunc(String userID){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "users/" + userID);
//        userRef.orderByChild("title")

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                Log.d(TAG, "Testing Value is: " + value.email + value.userID +
                        value.bookIDMap.keySet().toString());

                User tempUser;
                tempUser = value;
                someUser = tempUser;
            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public  void getterUserByUserID(final String userID){
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "running new thread");
                getUser(userID);

            }
        });
        EditText tempEdit = findViewById(R.id.editText2);
        tempEdit.setText("tempUser.email");
    }

    public void addBookUserRef(String bookID, String userID){
//        BookInfo book = new BookInfo(bookID, "Buy");
        mDatabase.child("BookToUser").child(bookID).setValue(userID);
    }

}
