package com.fexed.spacecadetpinball;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.fexed.spacecadetpinball.databinding.ActivityLeaderboardBinding;
import com.fexed.spacecadetpinball.databinding.ActivitySettingsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    private ActivityLeaderboardBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.leaderboard);
        mBinding = ActivityLeaderboardBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        HighScoreHandler.leaderboardActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        HighScoreHandler.getRanking(LeaderboardActivity.this);

        List<LeaderboardElement> placeholder = new ArrayList<>();
        placeholder.add(new LeaderboardElement("Loading", "", null, 0, 0));
        mBinding.list.setAdapter(new LeaderboardAdapter(placeholder, true, false, null));
    }

    @Override
    protected void onPause() {
        super.onPause();
        HighScoreHandler.leaderboardActivity = null;
    }

    public void onLeaderboardReady(List<LeaderboardElement> leaderboard) {
        if (false) {
            Collections.sort(leaderboard, (t1, t2) -> -Integer.compare(t1.cheatScore, t2.cheatScore));
        } else {
            Collections.sort(leaderboard, (t1, t2) -> -Integer.compare(t1.normalScore, t2.normalScore));
        }
        runOnUiThread(() -> {
            SharedPreferences prefs = getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE);
            int position = -1;

            for (int i = 0; i < leaderboard.size(); i++) {
                if (leaderboard.get(i).uid.equals(prefs.getString("userid", "0"))){
                    position = i+1;
                    break;
                }
            }

            if (position != -1) {
                setTitle(getString(R.string.leaderboard_current, position + ""));
            }
            mBinding.list.setAdapter(new LeaderboardAdapter(leaderboard, false, false, prefs));
        });
    }
}