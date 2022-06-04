package com.fexed.spacecadetpinball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        int score = getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getInt("highscore", 0);
        TextView highscoretxtv = findViewById(R.id.highscoretxtv);
        highscoretxtv.setText("" + score);
    }
}