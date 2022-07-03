package com.fexed.spacecadetpinball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fexed.spacecadetpinball.databinding.ActivityMainBinding;
import com.fexed.spacecadetpinball.databinding.ActivitySettingsBinding;

import org.libsdl.app.SDLActivity;
import org.w3c.dom.Text;

public class Settings extends AppCompatActivity {

    private ActivitySettingsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);

        int score = getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getInt("highscore", 0);
        String txt = score + "";
        mBinding.highscoretxtv.setText(txt);

        txt = "Version " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")";
        mBinding.verstxtv.setText(txt);

        boolean tiltenabled = getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getBoolean("tiltbuttons", true);
        mBinding.tiltbtns.setChecked(tiltenabled);
        mBinding.tiltbtns.setOnCheckedChangeListener((compoundButton, b) -> {
            getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).edit().putBoolean("tiltbuttons", b).apply();
        });

        boolean customfonts = getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getBoolean("customfonts", true);
        mBinding.cstmfnts.setChecked(customfonts);
        mBinding.cstmfnts.setOnCheckedChangeListener((compoundButton, b) -> {
            getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).edit().putBoolean("customfonts", b).apply();
        });

        mBinding.inpttxtusername.setText(getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getString("username", "Player 1"));
        mBinding.inpttxtusername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).edit().putString("username", charSequence.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.gplaytxtv.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=6687966458279653723"));
            startActivity(browserIntent);
        });
        mBinding.githubtxtv.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fexed/Pinball-on-Android/releases"));
            startActivity(browserIntent);
        });

        mBinding.volumebar.setProgress(getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getInt("volume", 100));
        mBinding.volumebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int percentage, boolean b) {
                getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).edit().putInt("volume", percentage).apply();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mBinding.gmaxbtn.setOnClickListener(v -> {
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_G);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_G);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_X);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_X);
        });

        mBinding.rmaxbtn.setOnClickListener(v -> {
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_R);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_R);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_X);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_X);
        });

        mBinding.onemaxbtn.setOnClickListener(v -> {
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_1);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_1);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_X);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_X);
        });

        mBinding.bmaxbtn.setOnClickListener(v -> {
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_B);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_B);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_X);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_X);
        });

        mBinding.omaxbtn.setOnClickListener(v -> {
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_O);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_O);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_X);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_X);
        });

        mBinding.lmaxbtn.setOnClickListener(v -> {
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_L);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_L);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_M);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_A);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_X);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_X);
        });

        mBinding.hdntestbtn.setOnClickListener(v -> {
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_H);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_H);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_I);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_I);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_D);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_D);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_D);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_D);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_E);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_E);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_N);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_N);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_SPACE);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_SPACE);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_T);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_T);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_E);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_E);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_S);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_S);
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_T);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_T);
        });
    }
}