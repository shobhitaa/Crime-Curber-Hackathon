package com.example.crimecurber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView cameraCard,uploadCard,emergencyCard,callCard,directionCard,helpCard;
    public static final int RC_SIGN_IN=1; // for sigin purpose

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();


        //defining cards
        cameraCard = (CardView) findViewById(R.id.camera_card);//casting
        uploadCard = (CardView) findViewById(R.id.upload_card);
        emergencyCard = (CardView) findViewById(R.id.emergency_card);
        callCard = (CardView) findViewById(R.id.call_card);
        directionCard = (CardView) findViewById(R.id.direction_card);
        helpCard = (CardView) findViewById(R.id.help_card);
        Button btnsignout= (Button)findViewById(R.id.btnsignout);


        cameraCard.setOnClickListener(this);
        uploadCard.setOnClickListener(this);
        emergencyCard.setOnClickListener(this);
        callCard.setOnClickListener(this);
        directionCard.setOnClickListener(this);
        helpCard.setOnClickListener(this);

        //Imageview setup
        cameraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(intent,0);
            }
        });


        //Initialization of Firebase Auth
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {
                    // already signed
//                    Toast.makeText(getApplicationContext() , "You are now signed In. Welcome to Crime Curber.", Toast.LENGTH_SHORT).show();

                } else {
                    // not signed in
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.AnonymousBuilder().build()))
                                    .setTheme(R.style.LoginTheme) // Theme for firebase Ui
                                    .setLogo(R.mipmap.ic_launcher_foreground) // Background Logo
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };


    }
    @Override
    public  void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(getApplicationContext() , "You are now Signed In. Welcome to Crime Curber.", Toast.LENGTH_SHORT).show();
            }else if (resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext() , "Sign in Cancelled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    //Firebase methods
    @Override
    protected void onPause(){
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
    @Override
    protected void onResume(){
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    //Firebase Signout Impemntation
    @Override
    public void onClick(View v){
        Intent i;
        if (v.getId() == R.id.btnsignout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            Toast.makeText(getApplicationContext() , "Signed Out", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
        //Firebase Signout Finished

        switch (v.getId()){
            case R.id.upload_card : i = new Intent(this,Upload.class); startActivity(i); break;
            case R.id.emergency_card : i = new Intent(this,Emergency.class); startActivity(i); break;
            case R.id.call_card : i = new Intent(this,Call.class); startActivity(i); break;
            case R.id.direction_card : i = new Intent(this, Direction.class); startActivity(i); break;
            case R.id.help_card : i = new Intent(this,Help.class); startActivity(i); break;
            default:break;
        }

    }
}