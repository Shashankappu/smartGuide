package com.shashanksp.smartsonics;

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


    public static String searchVideosByTopicId(String apiKey, String topicId) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL)).newBuilder();
        urlBuilder.addQueryParameter("topicId", topicId);
        urlBuilder.addQueryParameter("key", apiKey);

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
            throw new IOException("Error: " + response.code() + " " + response.message());
        }
    }

    private static String extractVideoId(String responseData) {
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
            JsonArray itemsArray = jsonObject.getAsJsonArray("items");

            if (itemsArray != null && itemsArray.size() > 0) {
                JsonObject firstItem = itemsArray.get(0).getAsJsonObject();
                JsonObject idObject = firstItem.getAsJsonObject("id");

                if (idObject != null && idObject.has("videoId")) {
                    return idObject.get("videoId").getAsString();
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

