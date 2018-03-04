package com.mapbook.mapbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class BookListAdapter extends BaseAdapter implements ListAdapter{

    private DatabaseReference mDatabase;


    private Context context;
    private List<String> titlelist;
    private List<String> statlist;
    private List<String> idlist;



    public void changeStatus(String bookID, String newStatus){
        mDatabase.child("BookDB").child(bookID).child("status").setValue(newStatus);
    }


    public BookListAdapter(List<String> titlelist,List<String> statlist, List<String> idlist,Context context){
        this.titlelist=titlelist;
        this.statlist=statlist;
        this.idlist=idlist;
        this.context = context;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();

    }
    @Override
    public int getCount() {
        return titlelist.size();
    }

    @Override
    public Object getItem(int pos) {
        return titlelist.get(pos);
    }

    public Object getStat(int pos){
        return statlist.get(pos);
    }

    public Object getid(int pos){
        return idlist.get(pos);
    }
    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.view_list_item, null);
        }

        //Handle TextView and display string from your list
        TextView listtitle = (TextView)view.findViewById(R.id.resbooktitle);
        listtitle.setText(titlelist.get(position));
        TextView liststat = (TextView)view.findViewById(R.id.resbookstatus);
        liststat.setText(statlist.get(position));
        //Handle buttons and add onClickListeners
        Button sellBtn = (Button)view.findViewById(R.id.buttonSell);
        Button soldBtn = (Button)view.findViewById(R.id.buttonSold);


        sellBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(getStat(position).equals("RESERVED")){


                    statlist.remove(position);
                    titlelist.remove(position);
                    notifyDataSetChanged();
                    changeStatus((String)getid(position), "SELL");


                }
                else{
                    Toast.makeText(context, "Cannot change status for this book. ", Toast.LENGTH_LONG ).show();
                }

            }
        });

        soldBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(getStat(position).equals("RESERVED")){

                    statlist.remove(position);
                    titlelist.remove(position);
                    notifyDataSetChanged();
                    changeStatus((String)getid(position), "SOLD");


                }
                else{
                    Toast.makeText(context, "Cannot change status for this book. ", Toast.LENGTH_LONG ).show();
                }

            }
        });



        return view;
    }
}
