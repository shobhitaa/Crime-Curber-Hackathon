package com.example.crimecurber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class Emergency extends AppCompatActivity implements LocationListener
{
    DbHandler db = new DbHandler( Emergency.this);

    String copy_phone = "";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    int i = 1;
    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.

    String[] permissions= new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    private int TRACK_LOCATION_REQUEST_CODE = 1;
    private Button sos_btn,fetchBtn;

    protected LocationManager locationManager;

    public String GlobalMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        fetchBtn = (Button) findViewById ( R.id.fetchBtn );
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FetchContacts()!=null) {

                    FetchContacts fetchContacts = new FetchContacts();
                    fetchContacts.setDb(db);
                    DisplayContacts();
                }
                else
                    Toast.makeText(getApplicationContext(), "No contacts added", Toast.LENGTH_SHORT).show();
            }
        });

        sos_btn = findViewById(R.id.sos_btn);

        if(checkLocation())
            sos_btn.setEnabled(true);
        else
            requestLocPermission();

        if(checkPermission())
        {
            sos_btn.setEnabled(true);
        }
        else
        {
            requestSmsPermission();
        }



        sos_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                FetchContacts();
                for (Contacts contacts : FetchContacts()) {

                    String message = "Help me, I am sending you my location \n" + GlobalMessage;
                    String phoneNo = contacts.getPhoneNumber();

                    if (checkPermission()) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    } else {
                        Toast.makeText( Emergency.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
                 == PackageManager.PERMISSION_GRANTED){

            locationManager = (LocationManager) getSystemService ( Context.LOCATION_SERVICE );
            if (locationManager != null) {
                locationManager.requestLocationUpdates ( LocationManager.GPS_PROVIDER, 0, 0, this );
            }
            if (locationManager != null) {
                locationManager.requestLocationUpdates ( LocationManager.NETWORK_PROVIDER, 0, 0, this );
            }
        }
        else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    public void AddContacts(View view)
    {
        EditText ContactName = findViewById(R.id.name);
        String name = ContactName.getText().toString();

        EditText phone_number = findViewById(R.id.phone_number);
        String phoneNo = phone_number.getText().toString();

        if(!copy_phone.equals(phoneNo))
        {
            if (i <= 5) {
                Contacts con = new Contacts();

                con.setName(name);
                con.setPhoneNumber(phoneNo);
                db.addContact(con);

                Log.d("DbMill",""+con.getName()+" "+con.getPhoneNumber());

                i++;
                copy_phone = phoneNo;
            }
            else
            {
                Toast.makeText(getApplicationContext(), "You cannot add more than 5 contacts", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "You have not provided any new number", Toast.LENGTH_SHORT).show();
        }
    }

    public List<Contacts> FetchContacts()
    {
        return db.getAllContacts();
    }

    public void DisplayContacts()
    {
        Intent intent = new Intent(this, FetchContacts.class);
        startActivity(intent);
    }

    public void DeleteContacts(View v) {
        EditText del_contact = findViewById(R.id.delete_number);
        String del_con = del_contact.getText().toString();

        if (FetchContacts() != null)
        {
            for (Contacts contacts : FetchContacts()) {
                if (contacts.getPhoneNumber().equals(del_con)) {
                    db.deleteContact(contacts.getPhoneNumber());
                    copy_phone = "";
                    Log.d("DbMill", ""+contacts.getPhoneNumber());
                    break;
                } else {
                    Toast.makeText(getApplicationContext(), "No such contact available", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE && requestCode == TRACK_LOCATION_REQUEST_CODE)
        {
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                sos_btn.setEnabled(true);
            }
            else
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermission()
    {
        int checkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkLocation()
    {
        int checkLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return (checkLocation ==PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        try
        {
            String myLatitude = String.valueOf(location.getLatitude());
            String myLongitude = String.valueOf(location.getLongitude());
            
            GlobalMessage = " https://www.google.com/maps?q="+ myLatitude + "," + myLongitude;


        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    private void requestLocPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions( Emergency.this,
                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, TRACK_LOCATION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, TRACK_LOCATION_REQUEST_CODE);
        }
    }

    private void requestSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.SEND_SMS)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions( Emergency.this,
                                    new String[] {Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }
}
