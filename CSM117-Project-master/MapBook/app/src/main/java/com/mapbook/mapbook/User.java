package com.mapbook.mapbook;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

/**
 * Created by g090617 on 11/3/17.
 */

@IgnoreExtraProperties
public class User {
    public String email;
    public String userID;
//    public List<BookInfo> bookList;
//    public String Location;
//    public List<BookInfo> bookArray;
    public HashMap<String, BookInfo> bookIDMap;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userID, String email) {
        this.email = email;
        this.userID = userID;
        this.bookIDMap = new HashMap<>();
    }

//    public void addBookToUser(String bookID, String status){
//        BookInfo b1 = new BookInfo(bookID,status);
//        this.bookIDMap.put(bookID, b1);
//    }
}
