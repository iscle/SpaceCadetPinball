package com.fexed.spacecadetpinball;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import org.libsdl.app.SDLActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import com.fexed.spacecadetpinball.databinding.ActivityMainBinding;
import com.google.android.material.resources.TextAppearance;

public class MainActivity extends SDLActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding mBinding;
    private Handler plungerTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen();
        super.onCreate(savedInstanceState);
        File filesDir = getFilesDir();
        copyAssets(filesDir);
        initNative(filesDir.getAbsolutePath() + "/");

        mBinding = ActivityMainBinding.inflate(getLayoutInflater(), mLayout, false);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mLayout.addView(mBinding.getRoot(), layoutParams);

        mBinding.getRoot().bringToFront();

        mBinding.left.setOnTouchListener((v1, event) -> {
            v1.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_Z);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_Z);
            }
            return false;
        });


        mBinding.right.setOnTouchListener((v1, event) -> {
            v1.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_SLASH);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_SLASH);
            }
            return false;
        });


        mBinding.plunger.setOnTouchListener((v1, event) -> {
            v1.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_SPACE);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_SPACE);
            }
            return false;
        });

        mBinding.replay.setOnLongClickListener(view -> {
            SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_F2);
            SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_F2);
            getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).edit().putBoolean("cheatsused", false).apply();
            return true;
        });

        mBinding.replay.setOnClickListener(view -> {
            Toast.makeText(getContext(), R.string.restartprompt, Toast.LENGTH_SHORT).show();
        });


        mBinding.tiltLeft.setOnTouchListener((v1, event) -> {
            v1.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_X);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_X);
            }
            return false;
        });

        mBinding.tiltRight.setOnTouchListener((v1, event) -> {
            v1.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_PERIOD);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_PERIOD);
            }
            return false;
        });

        mBinding.tiltBottom.setOnTouchListener((v1, event) -> {
            v1.performClick();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                SDLActivity.onNativeKeyDown(KeyEvent.KEYCODE_DPAD_UP);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                SDLActivity.onNativeKeyUp(KeyEvent.KEYCODE_DPAD_UP);
            }
            return false;
        });

        mBinding.settingsbtn.setOnClickListener(view -> {
            Intent i = new Intent(this, Settings.class);
            startActivity(i);
        });
    }

    private void copyAssets(File filesDir) {
        if (!new File(filesDir, "PINBALL.DAT").exists()) {
            AssetManager assetManager = getAssets();
            try {
                for (String asset : assetManager.list("")) {
                    Log.d(TAG, "Copying " + asset);
                    try (InputStream is = assetManager.open(asset)){
                        try (OutputStream os = new FileOutputStream(new File(filesDir, asset))) {
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = is.read(buffer)) != -1) {
                                os.write(buffer, 0, len);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setFullscreen() {
        int ui_Options = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(ui_Options);
    }

    private StateHelper.IStateListener mStateListener = new StateHelper.IStateListener() {
        @Override
        public void onStateChanged(int state) {
            //runOnUiThread(() -> mBinding.replay.setVisibility(state == GameState.RUNNING ? View.GONE : View.VISIBLE));
            setVolume(getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getInt("volume", 100));
            putTranslations();
            putString(26, getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getString("username", "Player 1"));
        }

        @Override
        public void onBallInPlungerChanged(boolean isBallInPlunger) {
            runOnUiThread(() -> mBinding.plunger.setVisibility(isBallInPlunger ? View.VISIBLE : View.INVISIBLE));
            if (isBallInPlunger) {
                plungerTimer = new Handler(Looper.getMainLooper());
                plungerTimer.postDelayed(() -> runOnUiThread(() -> Toast.makeText(getContext(), R.string.plungerhint, Toast.LENGTH_LONG).show()), 3000);
            } else {
                plungerTimer.removeCallbacksAndMessages(null);
                plungerTimer = null;
            }
        }

        @Override
        public void onHighScorePresented(int score) {
            if (HighScoreHandler.postHighScore(getContext(), score)) {
                runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.newhighscore, score), Toast.LENGTH_LONG).show());
            }
        }

        @Override
        public int onHighScoreRequested() {
            return HighScoreHandler.getHighScore(getContext());
        }

        @Override
        public void onStringPresented(String str, int type) {
            final String fstr = str.replace("\n", " ");
            if (type == 1) runOnUiThread(() -> mBinding.missiontxt.setText(fstr));
            else runOnUiThread(() -> mBinding.infotxt.setText(fstr));
        }

        @Override
        public void onClearText(int type) {
            if (type == 1) runOnUiThread(() -> mBinding.missiontxt.setText(""));
            else runOnUiThread(() -> mBinding.infotxt.setText(""));
        }

        @Override
        public void onScorePosted(int score) {
            String str = "" + score;
            runOnUiThread(() -> mBinding.txtscore.setText(str));
        }

        @Override
        public void onBallCountUpdated(int count) {
            String str = getString(R.string.balls, count);
            runOnUiThread(() -> mBinding.ballstxt.setText(str));
        }

        @Override
        public void onCheatsUsed() {
            getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).edit().putBoolean("cheatsused", true).apply();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        StateHelper.INSTANCE.addListener(mStateListener);

        boolean tiltenabled = getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getBoolean("tiltbuttons", true);

        if (tiltenabled) {
            mBinding.tiltLeft.setVisibility(View.VISIBLE);
            mBinding.tiltRight.setVisibility(View.VISIBLE);
            mBinding.tiltBottom.setVisibility(View.VISIBLE);
        } else {
            mBinding.tiltLeft.setVisibility(View.GONE);
            mBinding.tiltRight.setVisibility(View.GONE);
            mBinding.tiltBottom.setVisibility(View.GONE);
        }

        boolean customfonts = getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getBoolean("customfonts", true);

        if (customfonts) {
            mBinding.ballstxt.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bauhaus93));
            mBinding.ballstxt.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
            mBinding.txtscore.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bauhaus93));
            mBinding.txtscore.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
            mBinding.infotxt.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bauhaus93));
            mBinding.infotxt.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
            mBinding.missiontxt.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bauhaus93));
            mBinding.missiontxt.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
            mBinding.plunger.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bauhaus93));
            // not editing the plunger because it's a button (and using its color as default color)
            mBinding.tiltLeft.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bauhaus93));
            mBinding.tiltLeft.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
            mBinding.tiltBottom.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bauhaus93));
            mBinding.tiltBottom.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
            mBinding.tiltRight.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bauhaus93));
            mBinding.tiltRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
            mBinding.left.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bauhaus93));
            mBinding.left.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
            mBinding.right.setTypeface(ResourcesCompat.getFont(getContext(), R.font.bauhaus93));
            mBinding.right.setTextColor(ResourcesCompat.getColor(getResources(), R.color.purple_200, getTheme()));
        } else {
            int defaultColor = mBinding.plunger.getCurrentTextColor();
            mBinding.ballstxt.setTypeface(Typeface.DEFAULT);
            mBinding.ballstxt.setTextColor(defaultColor);
            mBinding.txtscore.setTypeface(Typeface.DEFAULT);
            mBinding.txtscore.setTextColor(defaultColor);
            mBinding.infotxt.setTypeface(Typeface.DEFAULT);
            mBinding.infotxt.setTextColor(defaultColor);
            mBinding.missiontxt.setTypeface(Typeface.DEFAULT);
            mBinding.missiontxt.setTextColor(defaultColor);
            mBinding.plunger.setTypeface(Typeface.DEFAULT);
            mBinding.tiltLeft.setTypeface(Typeface.DEFAULT);
            mBinding.tiltLeft.setTextColor(defaultColor);
            mBinding.tiltBottom.setTypeface(Typeface.DEFAULT);
            mBinding.tiltBottom.setTextColor(defaultColor);
            mBinding.tiltRight.setTypeface(Typeface.DEFAULT);
            mBinding.tiltRight.setTextColor(defaultColor);
            mBinding.left.setTypeface(Typeface.DEFAULT);
            mBinding.left.setTextColor(defaultColor);
            mBinding.right.setTypeface(Typeface.DEFAULT);
            mBinding.right.setTextColor(defaultColor);
        }

        setVolume(getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getInt("volume", 100));
        getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).edit().putBoolean("cheatsused", checkCheatsUsed()).apply();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBinding.infotxt.setVisibility(View.GONE);
            mBinding.missiontxt.setVisibility(View.GONE);
            mBinding.txtscore.setVisibility(View.GONE);
            mBinding.ballstxt.setVisibility(View.GONE);
        } else {
            mBinding.infotxt.setVisibility(View.VISIBLE);
            mBinding.missiontxt.setVisibility(View.VISIBLE);
            mBinding.txtscore.setVisibility(View.VISIBLE);
            mBinding.ballstxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StateHelper.INSTANCE.removeListener(mStateListener);
    }

    @Override
    protected String getMainFunction() {
        return "main";
    }

    @Override
    protected String[] getLibraries() {
        return new String[] {
                "SDL2",
                "SpaceCadetPinball"
        };
    }

    private void putTranslations() {
        int[] ids = getResources().getIntArray(R.array.gametexts_idxs);
        String[] texts = getResources().getStringArray(R.array.gametexts_strings);
        for (int i = 0; i < ids.length; i++) {
            putString(ids[i], texts[i]);
        }
    }

    private native void initNative(String dataPath);

    private native void setVolume(int vol);

    private native void putString(int id, String str);

    private native boolean checkCheatsUsed();
}