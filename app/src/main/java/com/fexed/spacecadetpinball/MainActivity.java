package com.fexed.spacecadetpinball;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.libsdl.app.SDLActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fexed.spacecadetpinball.databinding.ActivityMainBinding;

public class MainActivity extends SDLActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        ImageButton settings = findViewById(R.id.settingsbtn);
        settings.setOnClickListener(view -> {
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

    private StateHelper.IStateListener mStateListener = new StateHelper.IStateListener() {
        @Override
        public void onStateChanged(int state) {
            //runOnUiThread(() -> mBinding.replay.setVisibility(state == GameState.RUNNING ? View.GONE : View.VISIBLE));
        }

        @Override
        public void onBallInPlungerChanged(boolean isBallInPlunger) {
            runOnUiThread(() -> mBinding.plunger.setVisibility(isBallInPlunger ? View.VISIBLE : View.INVISIBLE));
        }

        @Override
        public void onHighScorePresented(int score) {
            int oldscore = getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getInt("highscore", 0);
            if (score > oldscore) {
                getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).edit().putInt("highscore", score).apply();
                runOnUiThread(() -> Toast.makeText(getContext(), getString(R.string.newhighscore, score), Toast.LENGTH_LONG).show());
            }
        }

        @Override
        public int onHighScoreRequested() {
            return getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE).getInt("highscore", 0);
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

    private native void initNative(String dataPath);
}