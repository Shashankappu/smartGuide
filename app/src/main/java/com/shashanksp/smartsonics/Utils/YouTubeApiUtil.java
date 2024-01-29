package com.shashanksp.smartsonics.Utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class YouTubeApiUtil {

    private static final String TAG = YouTubeApiUtil.class.getSimpleName();
    private static final String BASE_URL = "https://youtube.googleapis.com/youtube/v3/search";


    public static String searchVideosByQuery(String apiKey, String query) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL)).newBuilder();
        urlBuilder.addQueryParameter("q", query); // Change from 'topicId' to 'q'
        urlBuilder.addQueryParameter("key", apiKey);

        Log.d("YouTubeApiLoader", "Load in background started... and is in searchVideosByQuery ");
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            assert response.body() != null;
            String responseData = response.body().string();
            return extractVideoId(responseData);
        } else {
            Log.d("YouTubeApiLoader", "error in extracting ");
            throw new IOException("Error: " + response.code() + " " + response.message());
        }
    }


    private static String extractVideoId(String responseData) {
        Log.d("YouTubeApiLoader", "extracting videoId");
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
            JsonArray itemsArray = jsonObject.getAsJsonArray("items");

            if (itemsArray != null && itemsArray.size() > 0) {
                for (int i = 0; i < itemsArray.size(); i++) {
                    JsonObject currentItem = itemsArray.get(i).getAsJsonObject();
                    JsonObject idObject = currentItem.getAsJsonObject("id");

                    if (idObject != null && idObject.has("videoId")) {
                        return idObject.get("videoId").getAsString();
                    }
                }
            }

            Log.e(TAG, "Error extracting videoId: Video ID not found in the JSON response");
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error extracting videoId", e);
            return null;
        }
    }
}

