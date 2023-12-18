package com.shashanksp.smartsonics;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.io.IOException;


public class ListenActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    Button backtoscanBtn;
    ImageButton micBtn;
    TextView timerTV;
    public int seconds = 5;
    public int minutes =0;

    String artId,details;
    TextView resultTV,titleTV;
    TextToSpeech textToSpeech;
    YouTubePlayerView ytvideo;
    private static final String API_KEY = "AIzaSyCA-L8jfpKHynjIkqK2lA5s6prB0gb9A0w";
    private static final int LOADER_ID = 1;
    String videoId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        backtoscanBtn = findViewById(R.id.backtoscanBtn);
        timerTV = findViewById(R.id.timerTV);
        resultTV = findViewById(R.id.res_text);
        titleTV = findViewById(R.id.title_text);
        micBtn = findViewById(R.id.mic_btn);
        ytvideo = findViewById(R.id.ytvideo);


        artId = getIntent().getStringExtra("artId");
        details = getIntent().getStringExtra("details");
        titleTV.setText(artId);
        resultTV.setText(details);


        Log.d("YouTubeApiLoader", "loader init"+ videoId+" this");
        LoaderManager.getInstance(this).initLoader(LOADER_ID, null, ListenActivity.this);
        Log.d("YouTubeApiLoader", "loader init done"+ videoId +" this");

        backtoscanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListenActivity.this,HomeScanActivity.class);
                startActivity(i);
                finish();
            }
        });

        //TTS
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // TTS initialization successful
                    textToSpeech.setSpeechRate(0.8F);
                } else {
                    // TTS initialization failed
                    Toast.makeText(ListenActivity.this,"TTS Initialization Failed!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //TTS button
        micBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!textToSpeech.isSpeaking()) {
                    createNotificationChannel();
                    showTTSNotification();
                    convertTextToSpeech(resultTV.getText().toString());
                    micBtn.setImageDrawable(getResources().getDrawable(R.drawable.mic_on));
                } else {
                    textToSpeech.stop();
                    hideTTSNotification();
                    micBtn.setImageDrawable(getResources().getDrawable(R.drawable.mic_off));
                }
            }
        });


        getLifecycle().addObserver(ytvideo);
        ytvideo.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = "ZYXwVtbo8Zc";
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

    }

    private void convertTextToSpeech(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "TTS Channel";
            String description = "Channel for Text-to-Speech";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("tts_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showTTSNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "tts_channel";
            String channelName = "TTS Channel";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.mic_on)
                    .setContentTitle("TTS is on")
                    .setContentText("Text-to-Speech is active")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            notificationManagerCompat.notify(1, builder.build());
        }
    }
    private void hideTTSNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(1); // Cancels the notification with the specified ID (1 in this case)
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new YouTubeApiLoader(this, API_KEY, "baby");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        Log.d("YouTubeApiLoader", "onLoadFinished started...");
        // Your existing code here
        if (data != null) {
            Log.d("YouTubeApiLoader", "Video ID: " + data);
            Toast.makeText(ListenActivity.this, "Video ID: " + data, Toast.LENGTH_SHORT).show();

        } else {
            Log.e("YouTubeApiLoader", "Video ID: NullIIDD ");
            Toast.makeText(ListenActivity.this, "Error extracting video ID.", Toast.LENGTH_SHORT).show();
        }
        Log.d("YouTubeApiLoader", "onLoadFinished completed.");

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        // Handle loader reset if needed
        Log.d("YouTubeApiLoader", "onLoaderReset started...");
        // Handle loader reset if needed
        Log.d("videoid", "loaderreset");
        Log.d("YouTubeApiLoader", "onLoaderReset completed.");
    }

    public static class YouTubeApiLoader extends AsyncTaskLoader<String> {

        private final String apiKey;
        private final String topicId;

        public YouTubeApiLoader(Context context, String apiKey, String topicId) {
            super(context);
            this.apiKey = apiKey;
            this.topicId = topicId;
        }

        @Override
        public String loadInBackground() {
            try {
                Log.d("YouTubeApiLoader", "Load in background started...");
                // Your existing code here
                return YouTubeApiUtil.searchVideosByTopicId(apiKey, topicId);
            } catch (IOException e) {
                Log.e("YouTubeApiLoader", "Error in loadInBackground", e);
                e.printStackTrace();
                return null;
            } finally {
                Log.d("YouTubeApiLoader", "Load in background completed.");
            }
        }
    }
}





