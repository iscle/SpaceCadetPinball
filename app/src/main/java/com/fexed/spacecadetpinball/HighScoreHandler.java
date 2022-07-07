package com.fexed.spacecadetpinball;

import android.content.Context;
import android.content.SharedPreferences;

public class HighScoreHandler {
    static boolean postHighScore(Context context, int score) {
        SharedPreferences prefs = context.getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE);

        if (!prefs.getBoolean("cheatsused", true)) {
            int oldscore = prefs.getInt("highscore", 0);
            if (score > oldscore) {
                prefs.edit().putInt("highscore", score).apply();
                return true;
            }
        }

        return false;
    }

    static int getHighScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE);

        return prefs.getInt("highscore", 0);
    }
}
