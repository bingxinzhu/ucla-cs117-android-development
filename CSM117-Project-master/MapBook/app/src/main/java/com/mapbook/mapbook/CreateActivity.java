package com.mapbook.mapbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class CreateActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RequestAccess requestAccessDB;
    private static final String TAG = "CreateActivity";

    private LatLng location;
    private String zip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO:Get info about the selected place.

                location = place.getLatLng();
                String address = (String) place.getAddress();
                String[] addressArr = address.split(",");
                String state_zip = addressArr[addressArr.length-2];
                zip = state_zip.replaceAll("[^-?0-9]+", "");
                Log.i(TAG, "Place: " + String.valueOf( location.longitude) +"and " + String.valueOf(location.latitude));
                Log.i(TAG, "ZIP: " + zip);
                Log.i(TAG, "Address: " + address);
            }

            @Override
            public void onError(Status status) {
                // TODO:Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            final EditText titleField = (EditText)findViewById(R.id.editText2);

            String title = titleField.getText().toString();
            if(title.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please enter a book title", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                title = title.toUpperCase();

            }

            final EditText nameField = (EditText)findViewById(R.id.editText3);
            String name = nameField.getText().toString();
            if(name.isEmpty()){
                name = "N/A";
            }
            final EditText isbnField = (EditText)findViewById(R.id.editText5);
            String isbn  = isbnField.getText().toString();
            if (isbn.isEmpty()){
                isbn = "N/A";
            }
            final EditText pubField = (EditText)findViewById(R.id.editText6);
            String pub = pubField.getText().toString();
                if (pub.isEmpty()){
                    pub = "N/A";
                }
            final EditText priceField = (EditText)findViewById(R.id.editText8);
            String price = priceField.getText().toString();
                if(price.isEmpty()){
                    price = "N/A";
                }

                if(location == null){
                    Toast.makeText(getApplicationContext(), "Please select a location", Toast.LENGTH_SHORT).show();
                    return;
                }
            final Spinner subjectSpinner = (Spinner)findViewById(R.id.spinner);
            String subject = subjectSpinner.getSelectedItem().toString();
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            requestAccessDB = new RequestAccess();
            requestAccessDB.addBookToUser(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getUid(),
                                    title, name, isbn,
                                    pub, subject, price,"SELL", zip,
                       String.valueOf( location.longitude), String.valueOf(location.latitude));


                Snackbar.make(view, "Added to database", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
                BookInfo bookCreated = new BookInfo(null, title, name, isbn, pub, subject, price, "SELL", zip,
                    String.valueOf(location.longitude), String.valueOf(location.latitude));

            Intent intent = new Intent(getApplicationContext(), MainNavigation.class);
            intent.putExtra("bookCreated", bookCreated);

            startActivity(intent);
            }
        });
    }


}

