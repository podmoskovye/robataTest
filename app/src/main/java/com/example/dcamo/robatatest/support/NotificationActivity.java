package com.example.dcamo.robatatest.support;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.dcamo.robatatest.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = new Intent(NotificationActivity.this, MainActivity.class);
//        startActivity(intent);
//        this.finish();
        setContentView(R.layout.activity_notification);
    }
}
