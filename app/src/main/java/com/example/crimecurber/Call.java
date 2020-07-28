package com.example.crimecurber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Call extends AppCompatActivity {
    private static final int REQUEST_CALL=1;// Constant required to call
    private  Button btnCall,btnCall102; //button declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        btnCall=(Button)findViewById(R.id.btnCall);
        btnCall102 = (Button) findViewById(R.id.btnCall102);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
        btnCall102.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                makePhoneCall102();

            }
        });
    }

     // Method For Calling 102
    private void makePhoneCall102() {
        if(ContextCompat.checkSelfPermission(Call.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Call.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);

        }else {
            String dial = "tel:102";
            startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(dial)));
        }

    }
      //Method For Calling 100
    private void makePhoneCall() {
        if(ContextCompat.checkSelfPermission(Call.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Call.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);

        }else {
            String dial = "tel:100";
            startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(dial)));
        }
    }

    //Permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length>0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                makePhoneCall();
                makePhoneCall102();
            }else {
                Toast.makeText(this, "PERMISSION DENIED",Toast.LENGTH_SHORT).show();
            }
        }
    }
}