package com.teddyxiong53.www.callmail;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences preferences ;
    private SharedPreferences.Editor editor;

    private Switch mServiceSwitch = null;
    private Button mTestButton = null;


    private String mSenderEmail = "callmail123@163.com";
    private String mSenderPassword = "callmail8888";
    private String mReceiverEmail = null;


    //public static final String MY_ACTION = "com.teddyxiong53.www.callmail.MY_ACTION";
    private TelephonyManager mTelMng;
    private PhoneStateListener phoneStateMng;

    private boolean bServing = false;
    AlertDialog.Builder mParamErrorDlg;
    AlertDialog.Builder mNetNotReadyDlg;

    private String currentNumber;
    private CallMailService callMailService;
    public static final String CALL_MAIL_SERVICE = "com.teddyxiong53.www.callmail.CallMailService";

    private static final int BACK_PRESS_ITV = 2000;//press back in 2 secnod interval to exit.
    private long pressTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        init();
    }


    private void init() {
        preferences = getSharedPreferences("callmail", MODE_PRIVATE);
        editor = preferences.edit();

        mParamErrorDlg = createDialog("Email Not Config", "Please set the email addr and password firstly and try again");
        mNetNotReadyDlg = createDialog("Network Not Connect", "Please open network connection and try again");
        mParamErrorDlg.setPositiveButton("Go to Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        mNetNotReadyDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });




        mServiceSwitch = (Switch)findViewById(R.id.service_switch);
        //get the service state to show whether the switch is on
        boolean isWorking = isCallMailServiceWorking();
        if(isWorking) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mServiceSwitch.setChecked(true);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mServiceSwitch.setChecked(false);
            }
        }

        final Intent intent = new Intent();
        intent.setAction(CALL_MAIL_SERVICE);
        intent.setPackage(getPackageName());
        mServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    //1.check params
                    if(!checkParam()) {

                        mServiceSwitch.setChecked(false);
                        mParamErrorDlg.show();
                        return ;
                    }
                    //2.check net
                    if(!checkNetwork()) {

                        mServiceSwitch.setChecked(false);
                        mNetNotReadyDlg.show();
                        return ;
                    }

                    bServing = true;
                    startService(intent);
                    Toast.makeText(MainActivity.this, "Turn on CallMail service", Toast.LENGTH_SHORT).show();
                } else {
                    bServing = false;
                    stopService(intent);
                    Toast.makeText(MainActivity.this, "Turn off CallMail service", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean isCallMailServiceWorking() {
        ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);
        for(int i=0; i<serviceList.size(); i++) {
            if(CALL_MAIL_SERVICE.equals(serviceList.get(i).service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private AlertDialog.Builder createDialog(String title, String message) {
        return new AlertDialog.Builder(this).setTitle(title).setMessage(message);
    }
    /**
     *
     */
    private boolean checkParam() {
        Map<String,?> content = preferences.getAll();
		/*
        for(Map.Entry<String,?> entry : content.entrySet()) {
            Log.d(entry.getKey(), (String) entry.getValue());

        }
        */
        //read param from preference
        String senderEmail = preferences.getString("receiver_email", "xxx@huawei.com");
        if(senderEmail.equals("xxx@huawei.com")) {
            return false;
        }

        return true;
    }

    private boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if((ni != null) && (ni.isConnectedOrConnecting())) {
            return true;
        }
        return false;
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(System.currentTimeMillis() - pressTime > BACK_PRESS_ITV) {
                Toast.makeText(MainActivity.this, "press back again to exit", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
			Log.d("xhl", "settings click");
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            // Handle the camera action
            Log.d("xhl", "nav_setting click");
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);

            startActivity(intent);
        } else if (id == R.id.nav_share) {
			
        } else if (id == R.id.nav_send) {
            /*
            Uri uri = Uri.parse("mailto:1073167306@qq.com");
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            String[] email = {"1073167306@qq.com"};
            intent.putExtra(Intent.EXTRA_CC, email);
            intent.putExtra(Intent.EXTRA_SUBJECT, "CallMail Report");
            intent.putExtra(Intent.EXTRA_TEXT, "CallMail is good");
            startActivity(Intent.createChooser(intent, "Please choose the app"));
            */
            /*
            String emailID = "1073167306@qq.com";
            String Subject = "subject";
            String Text = "text";
            String Choosertitle = "choose";

            Intent emailIntent = new Intent(Intent.ACTION_SEND, Uri.fromParts(
                    "mailto", emailID, null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                    Subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT,
                    Text);
            startActivity(Intent.createChooser(emailIntent, Choosertitle));
            */
        } else {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
