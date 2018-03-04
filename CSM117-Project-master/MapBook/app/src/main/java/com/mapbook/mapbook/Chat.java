package com.mapbook.mapbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;



public class Chat extends AppCompatActivity {

    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Toolbar toolbar;
    FirebaseAuth mAuth;
    DatabaseReference messageRef;
    DatabaseReference messageRefOpposite;
    User chatTo;
    ArrayList<String> chatHistory = new ArrayList<>();
    ArrayList<String> oppoChatHistory = new ArrayList<>();
    String myUID;
    String oppositeEmail;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        toolbar = findViewById(R.id.chat_with_toolbar);
        myUID = mAuth.getCurrentUser().getUid();


        Intent myIntent = getIntent();
        final String userID = myIntent.getStringExtra("userID");
        Log.d(TAG, "userID is " + userID);
        setTitleBarEmail(userID);

        messageRef = FirebaseDatabase.getInstance().getReference("Chat/" + myUID + "/");
        messageRefOpposite = FirebaseDatabase.getInstance().getReference("Chat/" + userID + "/");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference tempRef1 = FirebaseDatabase.getInstance().getReference("Chat/");
                tempRef1.child(myUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Log.d(TAG, "Send EXIST");
                            String messageText = "0" + messageArea.getText().toString();
                            String messageText2 = "1" + messageArea.getText().toString();
                            DatabaseReference tempRef = FirebaseDatabase.getInstance().
                                    getReference("Chat/" + myUID + "/" + userID);
                            DatabaseReference tempRefOpposite = FirebaseDatabase.getInstance().
                                    getReference("Chat/" + userID + "/" + myUID);
                            if (!messageText.equals("")) {

//                                ArrayList<String> tempList = chatHistory == null ? new ArrayList<String>() : chatHistory;
//                                ArrayList<String> tempList2 = oppoChatHistory  == null ? new ArrayList<String>() : oppoChatHistory;
//                                tempList.add(messageText);
//                                tempList2.add(messageText2);
                                chatHistory.add(messageText);
                                oppoChatHistory.add(messageText2);
                                tempRef.setValue(chatHistory);
                                tempRefOpposite.setValue(oppoChatHistory);
//                                addMessageBox("You:\n" + messageText.substring(1), 1);
//
                                messageArea.setText("");
//
                            }
                        }
                        else{
                            Log.d(TAG, "Send NOT EXIST");
                            String messageText = "0" + messageArea.getText().toString();
                            String messageText2 = "1" + messageArea.getText().toString();
                            if (!messageText.equals("")) {

//                                ArrayList<String> tempList = chatHistory == null ? new ArrayList<String>() : chatHistory;
//                                ArrayList<String> tempList2 = oppoChatHistory  == null ? new ArrayList<String>() : oppoChatHistory;
//                                tempList.add(messageText);
//                                tempList2.add(messageText2);
                                chatHistory.add(messageText);
                                oppoChatHistory.add(messageText2);
                                tempRef1.child(myUID).child(userID).setValue(chatHistory);
                                tempRef1.child(userID).child(myUID).setValue(oppoChatHistory);
//                                addMessageBox("You:\n" + messageText.substring(1), 1);

//
                                messageArea.setText("");
                                recreate();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        final DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("Chat/");
//        if(tempRef.getKey() == null){
//            Log.d(TAG, "tempRef key not exist");
//        }

        tempRef.child(myUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Log.d(TAG, "EXIST");
                    if (mAuth.getCurrentUser()!=null) {

                        messageRefOpposite.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Log.d(TAG, "Value is " + dataSnapshot.getValue());

                                Log.d(TAG, "snapshot key is " + dataSnapshot.getKey());
                                if(dataSnapshot.getKey().toString().equals(myUID)) {
                                    oppoChatHistory = (ArrayList<String>) dataSnapshot.getValue();
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Log.d(TAG, "Value is " + dataSnapshot.getValue());

                                Log.d(TAG, "snapshot key is " + dataSnapshot.getKey());
                                if(dataSnapshot.getKey().toString().equals(myUID)) {
                                    oppoChatHistory = (ArrayList<String>) dataSnapshot.getValue();

                                }
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        messageRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Log.d(TAG, "in messageRef on Child added Value is " + dataSnapshot.getValue());

//                                chatHistory = new ArrayList<>();
//                                oppoChatHistory = new ArrayList<>();
                                Log.d(TAG, "snapshot key is " + dataSnapshot.getKey());
                                if(dataSnapshot.getKey().toString().equals(userID)) {

                                    if(chatHistory.size() == 0) {
                                        chatHistory = (ArrayList<String>) dataSnapshot.getValue();
                                        Log.d(TAG, "Chat 0");
                                        for (int i = 0; i < chatHistory.size(); i++) {
                                            Log.d(TAG, "In loop");
                                            if (chatHistory.get(i) == null)
                                                continue;
                                            if (chatHistory.get(i).charAt(0) == '0')
                                                addMessageBox("You:\n" + chatHistory.get(i).substring(1), 1);
                                            else
                                                addMessageBox(oppositeEmail + ":\n" + chatHistory.get(i).substring(1), 2);
                                        }
                                    }
                                    else{
                                        Log.d(TAG, "Chat not 0");
//                                        if (chatHistory.get(chatHistory.size() - 1) == null)
//                                            continue;
                                        if (chatHistory.get(chatHistory.size() - 1).charAt(0) == '0')
                                            addMessageBox("You:\n" + chatHistory.get(chatHistory.size() - 1).substring(1), 1);
                                        else
                                            addMessageBox(oppositeEmail + ":\n" + chatHistory.get(chatHistory.size() - 1).substring(1), 2);
                                    }
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                Log.d(TAG, "in messageRef on Child changed Value is " + dataSnapshot.getValue());
//                                chatHistory = new ArrayList<>();
//                                oppoChatHistory = new ArrayList<>();
                                Log.d(TAG, "snapshot key is " + dataSnapshot.getKey());
                                if(dataSnapshot.getKey().toString().equals(userID)) {
                                    chatHistory = (ArrayList<String>) dataSnapshot.getValue();
                                    if(chatHistory.size() == 0) {
                                        Log.d(TAG, "Chat 0");
                                        for (int i = 0; i < chatHistory.size(); i++) {
                                            Log.d(TAG, "In loop");
                                            if (chatHistory.get(i) == null)
                                                continue;
                                            if (chatHistory.get(i).charAt(0) == '0')
                                                addMessageBox("You:\n" + chatHistory.get(i).substring(1), 1);
                                            else
                                                addMessageBox(oppositeEmail + ":\n" + chatHistory.get(i).substring(1), 2);
                                        }
                                    }
                                    else{
                                        Log.d(TAG, "on child changed Chat not 0");
                                        if (chatHistory.get(chatHistory.size() - 1).charAt(0) == '0')
                                            addMessageBox("You:\n" + chatHistory.get(chatHistory.size() - 1).substring(1), 1);
                                        else
                                            addMessageBox(oppositeEmail + ":\n" + chatHistory.get(chatHistory.size() - 1).substring(1), 2);
                                    }
                                    scrollView.fullScroll(View.FOCUS_DOWN);

                                }
                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        startActivity(new Intent(Chat.this, Chat.class));

                    }
                }
                else {
                    Log.d(TAG, "NOT EXIST");
                    chatHistory = new ArrayList<>();
                    oppoChatHistory = new ArrayList<>();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setTitleBarEmail(String userID){
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(
                "users/" + userID);
//        userRef.orderByChild("title")

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                toolbar.setTitle(value.email);
                oppositeEmail = value.email;
//                EditText tempEdit = findViewById(R.id.editZipcode);
//                tempEdit.setText(value.email);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.END;
            textView.setBackgroundResource(R.mipmap.bubble_in);
            textView.getBackground().setAlpha(150);
            textView.setTextSize(18);
        } else {
            lp2.gravity = Gravity.START;
            textView.setBackgroundResource(R.mipmap.bubble_out);
            textView.getBackground().setAlpha(150);
            textView.setTextSize(18);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
