package com.shashanksp.smartsonics;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.util.ArrayList;
import java.util.List;

public class ListenActivity extends AppCompatActivity {
    Button backtoscanBtn;
    ImageButton micBtn;
    TextView timerTV;
    public int seconds = 5;
    public int minutes =0;

    String artId,details;
    TextView resultTV,titleTV;
    TextToSpeech textToSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listen);
        backtoscanBtn = findViewById(R.id.backtoscanBtn);
        timerTV = findViewById(R.id.timerTV);
        resultTV = findViewById(R.id.res_text);
        titleTV = findViewById(R.id.title_text);
        micBtn = findViewById(R.id.mic_btn);

        artId = getIntent().getStringExtra("artId");
        details = getIntent().getStringExtra("details");
        titleTV.setText(artId);
        resultTV.setText(details);



        backtoscanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListenActivity.this,HomeScanActivity.class);
                startActivity(i);
                finish();
            }
        });







//        if (!Python.isStarted()) {
//            Python.start(new AndroidPlatform(this));
//        }
//
//        // Call the Python function
//        Python py = Python.getInstance();
//        PyObject pyObject = py.getModule("model");
//        PyObject result = pyObject.callAttr("extract_keywords", "Virupaksha Temple (ʋɪruːpaː'kʂɐ) is located in Hampi in the Vijayanagara district of Karnataka, India. It is part of the Group of Monuments at Hampi, designated as a UNESCO World Heritage Site. The temple is dedicated to Sri Virupaksha, a form of Shiva. The temple was built by Lakkan Dandesha, a nayaka (chieftain) under the ruler Deva Raya II also known as Prauda Deva Raya of the Vijayanagara Empire.[1]\n" +
//                "\n" +
//                "Hampi, capital of the Vijayanagara empire, sits on the banks of the Tungabhadra River (Pampa hole/Pampa river). Virupaksha Temple is the main center of pilgrimage (ತೀರ್ಥಯಾತ್ರೆ )at Hampi, and had been considered the most sacred sanctuary over the centuries. It is intact among the surrounding ruins and is still used in worship . The temple is dedicated to Lord Shiva, known here as Virupaksha/Pampa pathi, as the consort of the local goddess Pampadevi who is associated with the Tungabhadra River. There is also a Virupakshini Amma temple (mother goddess) in a village called Nalagamapalle, Chittoor district, Andhra Pradesh, approximately 100 km from Tirupati.", 5);
//
//        List<String> keywords = new ArrayList<>();
//        for (PyObject obj : result.asList()) {
//            keywords.add(obj.toJava(String.class));
//        }
//
//        Log.d("MainActivity", "Top Keywords: " + keywords);

//        Timer t = new Timer();
//        //Set the schedule function and rate
//        t.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(minutes<0){
//                            timerTV.setText("Time up");
//                            t.cancel();
//                        } else{
//                            timerTV.setText(String.valueOf(minutes)+":"+String.valueOf(seconds)+" mins left");
//                            seconds -= 1;
//
//                            if(seconds == 0) {
//                                timerTV.setText(String.valueOf(minutes)+":"+String.valueOf(seconds)+" mins left");
//                                seconds=60;
//                                minutes=minutes-1;
//                            }
//                        }
//
//                    }
//                });
//            }
//
//        }, 0, 1000);

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

}



