package com.mapbook.mapbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView githublink = (TextView)findViewById(R.id.textViewgithub);
        githublink.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
