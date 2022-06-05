package com.fexed.spacecadetpinball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        int score = getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getInt("highscore", 0);
        TextView highscoretxtv = findViewById(R.id.highscoretxtv);
        String txt = score + "";
        highscoretxtv.setText(txt);

        TextView verstxtv = findViewById(R.id.verstxtv);
        txt = "Version " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")";
        verstxtv.setText(txt);
    }
}