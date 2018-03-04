package com.mapbook.mapbook;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.*;

import static android.content.ContentValues.TAG;


public class BuyFragment extends ListFragment {
    List<String> books = new ArrayList<String>();
    List<String> status = new ArrayList<String>();
    private FirebaseAuth mAuth;
    HashMap<String, BookInfo> bookInfoHashMap = new HashMap<>();
   // private OnFragmentInteractionListener mListener;
    public Button changeBtn;
    public BuyFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static BuyFragment newInstance() {
        BuyFragment fragment = new BuyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        getUserInfoByUserID(mAuth.getCurrentUser().getUid());


    }

    public void onListItemClick(ListView parent, View v,
                                int position, long id)
    {
        String index = books.get(position);
        BookInfo bookInfo = bookInfoHashMap.get(index);
        Toast.makeText(getActivity(),
                "You have selected " + books.get(position) + "\n"+
                "        Title: " + bookInfo.title + "\n" +
                        "        Author: " + bookInfo.author + "\n" +
                        "        ISBN: " + bookInfo.isbn + "\n" +
                        "        Subject: " + bookInfo.subject + "\n" +
                        "        Price: " + bookInfo.price + "\n",
                Toast.LENGTH_LONG).show();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy, container, false);
        changeBtn = view.findViewById(R.id.button2);


        return view;
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
            if(value.status.equals("BUY")){
                books.add(value.title);
                status.add(value.price);
                bookInfoHashMap.put(value.title, value);
            }

            List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
            for(int i =0 ; i<books.size();i++) {
                Map<String, Object> listItem = new HashMap<String, Object>();
                listItem.put("books", books.get(i));
                listItem.put("status", status.get(i));
                list.add(listItem);
            }
            SimpleAdapter adapter = new SimpleAdapter(getActivity(),list,
                    R.layout.view_list_item_no_button,  new String[] { "books","status"},
                    new int[] { R.id.booktitle, R.id.bookstatus });
            setListAdapter(adapter);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            // Failed to read value
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

//                EditText tempEdit = findViewById(R.id.editZipcode);
//                tempEdit.setText(value.email);
                if(value.bookIDMap != null) {
                    for (final String key : value.bookIDMap.keySet()) {
                        getBookInfoByBookID(key);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }
}
