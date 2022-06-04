package com.fexed.spacecadetpinball;

public class JNIEntryPoint {
    static void setState(int state) {
        StateHelper.INSTANCE.setState(state);
    }

    static void setBallInPlunger(boolean isInPlunger) {
        StateHelper.INSTANCE.setBallInPlunger(isInPlunger);
    }
}
