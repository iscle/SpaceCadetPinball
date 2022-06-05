package com.fexed.spacecadetpinball;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public enum StateHelper {
    INSTANCE;

    private static final String TAG = StateHelper.class.getSimpleName();

    private int mState;
    private boolean mIsBallInPlunger = false;
    private int score;

    private List<IStateListener> mStateListeners = new ArrayList<>();

    protected void addListener(IStateListener listener) {
        mStateListeners.add(listener);
    }

    protected void removeListener(IStateListener listener) {
        mStateListeners.remove(listener);
    }

    void setState(int state) {
        mState = state;

        for (IStateListener listener : mStateListeners) {
            if (listener != null) {
                listener.onStateChanged(state);
            }
        }
    }

    public void setBallInPlunger(boolean b) {
        mIsBallInPlunger = b;

        Log.d(TAG, "setBallInPlunger: " + b);

        for (IStateListener listener : mStateListeners) {
            if (listener != null) {
                listener.onBallInPlungerChanged(b);
            }
        }
    }

    public void addHighScore(int score) {
        for (IStateListener listener : mStateListeners) {
            if (listener != null) {
                listener.onHighScorePresented(score);
            }
        }
    }

    public int getHighScore() {
        for (IStateListener listener : mStateListeners) {
            if (listener != null) {
                return listener.onHighScoreRequested();
            }
        }
        return -1;
    }

    public void printString(String string) {
        for (IStateListener listener : mStateListeners) {
            if (listener != null) {
                listener.onStringPresented(string);
            }
        }
    }

    public void clearText() {
        for (IStateListener listener : mStateListeners) {
            if (listener != null) {
                listener.onClearText();
            }
        }
    }

    public void postScore(int score) {
        for (IStateListener listener : mStateListeners) {
            if (listener != null) {
                listener.onScorePosted(score);
            }
        }
    }

    public interface IStateListener {

        void onStateChanged(int state);

        void onBallInPlungerChanged(boolean isBallInPlunger);

        void onHighScorePresented(int score);

        int onHighScoreRequested();

        void onStringPresented(String string);

        void onClearText();

        void onScorePosted(int score);
    }
}
