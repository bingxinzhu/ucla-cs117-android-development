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
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

import java.util.*;


public class SellFragment extends Fragment {
   List<String> books = new ArrayList<String>();
   List<String> status = new ArrayList<String> ();
   List<String> id = new ArrayList<String>();
   ListView SellList;

    private FirebaseAuth mAuth;
    HashMap<String, BookInfo> bookInfoHashMap = new HashMap<>();
    // private OnFragmentInteractionListener mListener;
    public Button changeBtn;

    public SellFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SellFragment newInstance() {
        SellFragment fragment = new SellFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        getUserInfoByUserID(mAuth.getCurrentUser().getUid());
//        Log.d(TAG, "BOOK 0" + books.get(0));


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
        View view = inflater.inflate(R.layout.fragment_sell, container, false);
        SellList= view.findViewById(R.id.sellList);
        changeBtn = (Button)view.findViewById(R.id.button2);

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
                    "Zip code : " + value.zipCode+"\n" +
                    "Status : " + value.status);
           if(!value.status.equals("BUY")) {

               books.add(value.title);
               status.add(value.status);
               id.add(value.bookID);
               bookInfoHashMap.put(value.title, value);
           }
            // place reserved book in list with button
//            List<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
//            Log.d(TAG, "res books SIZE " + books.size()+"\n");
//            for(int i =0 ; i<books.size();i++){
//                Map<String,Object> listItem = new HashMap<String, Object>();
//                listItem.put("books", books.get(i));
//                listItem.put("status",status.get(i));
//                list.add(listItem);
//            }
//            SimpleAdapter res_adapter = new SimpleAdapter(getActivity(),list,
//                    R.layout.view_list_item,  new String[] { "books","status"},
//                    new int[] { R.id.resbooktitle, R.id.resbookstatus });
 //           setListAdapter(res_adapter);

            ListAdapter adapter = new BookListAdapter(books,status,id,getActivity());
            SellList.setAdapter(adapter);

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

