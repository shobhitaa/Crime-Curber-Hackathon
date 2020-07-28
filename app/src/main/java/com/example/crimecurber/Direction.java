package com.example.crimecurber;

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

public class Direction extends AppCompatActivity {
    private Button btnPolice,btnMedical;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        btnPolice = (Button)findViewById(R.id.btnPolice);
        btnMedical = (Button)findViewById(R.id.btnMedical);
        btnPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directPoliceStation();
            }
        });
        btnMedical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directHospital();
            }
        });

    }

    private void directHospital() {
        if(ContextCompat.checkSelfPermission(Direction.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Direction.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);

        }else {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=Hospital");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }

    private void directPoliceStation() {
        if(ContextCompat.checkSelfPermission(Direction.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Direction.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST_CODE);

        }else {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=Police Station");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }



}
