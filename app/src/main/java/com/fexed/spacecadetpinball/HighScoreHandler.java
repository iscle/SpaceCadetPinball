package com.fexed.spacecadetpinball;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Policy;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class HighScoreHandler {
    static String PUBLIC_KEY;
    static String URL;
    static LeaderboardActivity leaderboardActivity;

    static boolean postHighScore(Context context, int score) {
        SharedPreferences prefs = context.getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE);

        if (!prefs.getBoolean("cheatsused", true)) {
            postScore(context, true, true);
            int oldscore = prefs.getInt("highscore", 0);
            if (score > oldscore) {
                prefs.edit().putInt("highscore", score).apply();
                return true;
            }
        } else {
            postScore(context, true, false);
            int oldscore = prefs.getInt("cheathighscore", 0);
            if (score > oldscore) {
                prefs.edit().putInt("cheathighscore", score).apply();
                return false;
            }
        }

        return false;
    }

    static int getHighScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE);

        return prefs.getInt("highscore", 0);
    }

    static RSAPublicKey getPublicKey(Context context) {
        return publicKey;
    }

    static String encode(byte[] toEncode, RSAPublicKey publicKey) {
        return encoded;
    }

    static List<LeaderboardElement> getRanking(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE);
        List<LeaderboardElement> corpus = new ArrayList<>();
        Response.Listener<String> listener = response -> {
            try {
                JSONArray rankingJSON = new JSONArray(response);
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                for (int i = 0; i < rankingJSON.length(); i++) {
                    JSONObject elem = rankingJSON.getJSONObject(i);
                    Date lastupdate = fmt.parse(elem.getString("updatedAt"));
                    LeaderboardElement player = new LeaderboardElement(elem.getString("username"), elem.getString("_id"), lastupdate, elem.getJSONObject("rank").getInt("score"), elem.getJSONObject("cheatRank").getInt("score"));
                    corpus.add(player);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
            if (leaderboardActivity != null) leaderboardActivity.onLeaderboardReady(corpus);
        };
        Response.ErrorListener errorListener = error -> {
            Log.e("RANKS", "error: " + error + " " + Arrays.toString(error.getStackTrace()));
            if (leaderboardActivity != null) leaderboardActivity.onLeaderboardError(error.getMessage());
        };
        StringRequest GETRankingRequest = new StringRequest(Request.Method.GET, URL, listener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(GETRankingRequest);
        return corpus;
    }

    static void postScore(Context context, boolean verbose, boolean cheatUsed) {
        SharedPreferences prefs = context.getSharedPreferences("com.fexed.spacecadetpinball", Context.MODE_PRIVATE);
        if (!prefs.getString("username", "").equals("")) {
            try {
                JSONObject objectToEncode = new JSONObject();

                RSAPublicKey key = getPublicKey(context);
                String encryptedBody = encode(objectToEncode.toString(), key);

                JSONObject objectToSend = new JSONObject();
                Log.d("RANKS", "toSend: " + objectToSend);

                Response.Listener<String> listener = response -> {};
                Response.ErrorListener errorListener = error -> Log.e("RANKS", "error: " + error + " " + Arrays.toString(error.getStackTrace()));
                StringRequest POSTRankingRequest = new StringRequest(Request.Method.POST, URL, listener, errorListener) {
                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseJSON = "";
                        if (response != null) {
                            try {
                                JSONObject received = new JSONObject(new String(response.data));
                                if (prefs.getString("userid", "0").equals("0")) prefs.edit().putString("userid", uid).apply();
                                Log.d("RANKS", "response: " + received.toString());

                                Looper.prepare();
                                if (verbose) {
                                    if (response.statusCode == 200) {
                                        Toast.makeText(context, context.getString(R.string.scoreupload_ok, nickname), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.scoreupload_ok, "" + response.statusCode), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return Response.success(responseJSON, HttpHeaderParser.parseCacheHeaders(response));
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        return objectToSend.toString().getBytes(StandardCharsets.UTF_8);
                    }
                };
                POSTRankingRequest.setRetryPolicy(new DefaultRetryPolicy(60000, 10, 2.0f));
                RequestQueue queue = Volley.newRequestQueue(context);
                queue.add(POSTRankingRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
