package com.shashanksp.smartsonics.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashanksp.smartsonics.R;

public class EnterContentActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private String guideId,artId;
    EditText ArtName,details;
    Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_content);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        ArtName = findViewById(R.id.Artname);
        details = findViewById(R.id.detailsEdt);
        submitBtn = findViewById(R.id.submitBtn);

        guideId = getIntent().getStringExtra("guideId");
        artId = getIntent().getStringExtra("artId");
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (guideId.isEmpty() || artId.isEmpty() || details.getText().toString().isEmpty()) {
                    Toast.makeText(EnterContentActivity.this,"Enter the details"+guideId+artId,Toast.LENGTH_LONG).show();
                }else{
                    DatabaseReference artReference = databaseReference.child(artId).child(guideId);
                    artReference.child("Details").setValue(details.getText().toString());
                    Toast.makeText(EnterContentActivity.this,"Details Entered Successfully",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(EnterContentActivity.this,HomeScanActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}