package com.fexed.spacecadetpinball;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fexed.spacecadetpinball.databinding.ActivityLeaderboardBinding;
import com.fexed.spacecadetpinball.databinding.ActivitySettingsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {
    public static boolean isCheatRanking = false;

    private ActivityLeaderboardBinding mBinding;
    private List<LeaderboardElement> cachedLeaderboard = null;
    private int currentpage = 0;
    private int maxpages = 0;
    private int playerpage = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isCheatRanking) setTitle(R.string.cheatleaderboard);
        else setTitle(R.string.leaderboard);
        mBinding = ActivityLeaderboardBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        HighScoreHandler.leaderboardActivity = this;

        mBinding.prevpagebtn.setEnabled(false);
        mBinding.currpagetxt.setText(currentpage + "/" + maxpages);

        mBinding.prevpagebtn.setOnClickListener(v -> {
            currentpage -= 1;
            mBinding.list.setAdapter(new LeaderboardAdapter(cachedLeaderboard.subList((LeaderboardAdapter.pagesize*currentpage), (LeaderboardAdapter.pagesize*(currentpage+1))), false, isCheatRanking, getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE), currentpage));

            if (currentpage == 0) mBinding.prevpagebtn.setEnabled(false);
            mBinding.nextpagebtn.setEnabled(true);

            mBinding.currpagetxt.setText(currentpage + "/" + maxpages);
        });

        mBinding.nextpagebtn.setOnClickListener(v -> {
            currentpage += 1;
            mBinding.list.setAdapter(new LeaderboardAdapter(cachedLeaderboard.subList((LeaderboardAdapter.pagesize*currentpage), Math.min((LeaderboardAdapter.pagesize*(currentpage+1)), cachedLeaderboard.size())), false, isCheatRanking, getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE), currentpage));

            if (currentpage == maxpages) mBinding.nextpagebtn.setEnabled(false);
            mBinding.prevpagebtn.setEnabled(true);

            mBinding.currpagetxt.setText(currentpage + "/" + maxpages);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        HighScoreHandler.getRanking(LeaderboardActivity.this);
        HighScoreHandler.getCurrentRank(LeaderboardActivity.this);

        if (cachedLeaderboard == null) {
            mBinding.nextpagebtn.setEnabled(false);
            mBinding.prevpagebtn.setEnabled(false);
            List<LeaderboardElement> placeholder = new ArrayList<>();
            placeholder.add(new LeaderboardElement("Loading", "", null, 0, 0));
            mBinding.list.setAdapter(new LeaderboardAdapter(placeholder, true, false, null, 0));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        HighScoreHandler.leaderboardActivity = null;
    }

    public void onLeaderboardReady(List<LeaderboardElement> leaderboard) {
        if (isCheatRanking) {
            Collections.sort(leaderboard, (t1, t2) -> -Integer.compare(t1.cheatScore, t2.cheatScore));
        } else {
            Collections.sort(leaderboard, (t1, t2) -> -Integer.compare(t1.normalScore, t2.normalScore));
        }
        this.cachedLeaderboard = leaderboard;
        this.currentpage = 0;
        this.maxpages = (int) Math.floor(leaderboard.size()/((float) LeaderboardAdapter.pagesize));
        Log.d("RANKS", "size: " + leaderboard.size() + ", pages: " + maxpages);
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
                if (isCheatRanking) setTitle(getString(R.string.cheatleaderboard_current, position + ""));
                else setTitle(getString(R.string.leaderboard_current, position + ""));
            }
            mBinding.currpagetxt.setText(currentpage + "/" + maxpages);
            if (maxpages > 0) mBinding.nextpagebtn.setEnabled(true);
            mBinding.prevpagebtn.setEnabled(false);
            mBinding.list.setAdapter(new LeaderboardAdapter(leaderboard.subList(0, Math.min(LeaderboardAdapter.pagesize, leaderboard.size())), false, isCheatRanking, prefs, 0));
        });
    }

    public void onCurrentRankReady(LeaderboardElement player) {
        Log.d("RANKS", player.toString());
    }

    public void onLeaderboardError(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.rankerrortitle);
        if (error == null) {
            error = getString(R.string.rankerrorretry);
        }
        builder.setMessage(getString(R.string.rankerrormsg, error));
        builder.create();
        builder.setOnDismissListener(dialogInterface -> LeaderboardActivity.this.finish());
        builder.show();
    }
}