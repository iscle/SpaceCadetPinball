package com.fexed.spacecadetpinball;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public enum StateHelper {
    INSTANCE;

    private static final String TAG = StateHelper.class.getSimpleName();

    private int mState;
    private boolean mIsBallInPlunger = false;

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

    public interface IStateListener {

        void onStateChanged(int state);

        void onBallInPlungerChanged(boolean isBallInPlunger);
    }
}
