package com.hiddenpirates.rootchecker;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.DataOutputStream;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button checkBtn;
    TextView statusTV;
    ImageView imageView;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    boolean doubleBackToExitPressedOnce = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.material_toolbar);
        setSupportActionBar(toolbar);

//_______________________________________________________________________________________
        MobileAds.initialize(this, initializationStatus -> {
            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
            for (String adapterClass : statusMap.keySet()) {
                AdapterStatus status = statusMap.get(adapterClass);
                assert status != null;
                Log.d("MyApp", String.format(
                        "Adapter name: %s, Description: %s, Latency: %d",
                        adapterClass, status.getDescription(), status.getLatency()));
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        MediationTestSuite.launch(MainActivity.this);

//_______________________________________________________________________________________
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_drawer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,0,0);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//_______________________________________________________________________________________

        navigationView.setNavigationItemSelectedListener(item -> {
            Intent intent;

            if (item.getItemId() == R.id.send_mail_action) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:hiddenpiratesofficial@gmail.com?subject=Application-RootChecker"));
                startActivity(intent);
                return true;
            }
            else if (item.getItemId() == R.id.share_app_action){
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,"Try this awesome root checker app and gets accurate result. \n https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                startActivity(Intent.createChooser(intent, "Share through"));
                return true;
            }
            else if (item.getItemId() == R.id.rate_app_action){
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID));
                startActivity(intent);
                return true;
            }
            else if (item.getItemId() == R.id.more_app_action){
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Hidden+Pirates"));
                startActivity(intent);
                return true;
            }
            else if (item.getItemId() == R.id.visitWeb_app_action){
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://hidden-pirates.blogspot.com"));
                startActivity(intent);
                return true;
            }
            else{
                return false;
            }
        });

//______________________________________________________________________________________

        checkBtn = findViewById(R.id.checkBtn);
        statusTV = findViewById(R.id.statusTV);
        imageView = findViewById(R.id.imageView);

        @SuppressLint("UseCompatLoadingForDrawables") Drawable crossResource = getResources().getDrawable(R.drawable.ic_close);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable tickResource = getResources().getDrawable(R.drawable.ic_tick);

        String deviceModel = android.os.Build.MODEL;

        statusTV.setText("Device: "+deviceModel);

        checkBtn.setOnClickListener(v -> {

            if (isDeviceRooted()){
                imageView.setImageDrawable(tickResource);
                statusTV.setText("Congratulations! Your device "+deviceModel+" is rooted.");
                statusTV.setTextColor(Color.GREEN);
            }
            else {
                imageView.setImageDrawable(crossResource);
                statusTV.setText("Oops! Your device "+deviceModel+" is not rooted.");
                statusTV.setTextColor(Color.RED);
            }
        });
    }

//_______________________________________________________________________________________

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar.make(drawerLayout,"Double press to exit!", Snackbar.LENGTH_LONG).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

//_______________________________________________________________________________________

    public static boolean isDeviceRooted() {

        Process p;

        try {
            p = Runtime.getRuntime().exec("su");

            // Attempt to write a file to a root-only
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");

            // Close the terminal
            os.writeBytes("exit\n");
            os.flush();
            try {
                p.waitFor();
                return p.exitValue() != 255;
            }
            catch (Exception e) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
    }
}