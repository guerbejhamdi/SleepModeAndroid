package com.example.testsleep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends FragmentActivity {
    static final int RESULT_ENABLE = 1;
    DevicePolicyManager deviceManger;
    ComponentName compName;
    Button btnEnable, btnLock;
    PowerManager.WakeLock wakeLock;
    PowerManager.WakeLock mWakeLock;
    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
   /* getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/
        setContentView(R.layout.activity_main);
        btnEnable = findViewById(R.id.btnEnable);
        btnLock = findViewById(R.id.btnLock);
        deviceManger = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, DeviceAdmin.class);
        boolean active = deviceManger.isAdminActive(compName);
        if (active) {
            btnEnable.setText("Disable");
            btnLock.setVisibility(View.VISIBLE);

        } else {
            btnEnable.setText("Enable");
            btnLock.setVisibility(View.GONE);
        }

        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguard = km.newKeyguardLock("MyApp");

        String cmd = "su -c dpm set-active-admin --user current com.example.testsleep/.DeviceAdmin";

        try {
            Runtime.getRuntime().exec(cmd);

        } catch (IOException e) {
            e.printStackTrace();
        }

       // finishWakeLocker();
        if(mWakeLock!=null) {
            if(mWakeLock.isHeld()){
                mWakeLock.release();

            }
        }

/*        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("MyKeyguardLock");
                kl.disableKeyguard();
                Log.d("STARTED", "task started");
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
                if(mWakeLock.isHeld()){
                    mWakeLock.release();

                }else{
                    mWakeLock.acquire();

                }
            }
        }, 5000);*/
    }

    @Override
    protected void onResume() {
        if(mWakeLock!=null) {
            if(mWakeLock.isHeld()){
                mWakeLock.release();

            }
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
       /* finishWakeLocker();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("MyKeyguardLock");
                kl.disableKeyguard();
                Log.d("STARTED", "task started");
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
                if(mWakeLock.isHeld()){
                    mWakeLock.release();

                }else{
                    mWakeLock.acquire();

                }
            }
        }, 5000);*/

        super.onPause();
    }

    @Override
    protected void onStop() {

      /*  final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("MyKeyguardLock");
                kl.disableKeyguard();
                Log.d("STARTED", "task started");
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
                if(mWakeLock.isHeld()){
                    mWakeLock.release();

                }else{
                    mWakeLock.acquire();

                }
            }
        }, 5000);*/
        super.onStop();
    }

    private void runShellCommand(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
    }


    public void enablePhone(View view) {
        boolean active = deviceManger.isAdminActive(compName);
        if (active) {
            deviceManger.removeActiveAdmin(compName);
            btnEnable.setText("Enable");
            btnLock.setVisibility(View.GONE);
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You should enable the app!");
            startActivityForResult(intent, RESULT_ENABLE);
        }
    }

    @SuppressLint("InvalidWakeLockTag")
    public void lockPhone(View view) {
       PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "TEST");
        wakeLock.acquire();
        AlarmManager alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent inten = new Intent(getApplicationContext(), WakeUpActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, inten, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 04);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pi);

     //   alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+600000, pi);



       /* final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("MyKeyguardLock");
                kl.disableKeyguard();
                Log.d("STARTED", "task started");
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                 mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
                if(mWakeLock.isHeld()){
                    mWakeLock.release();

                }else{
                    mWakeLock.acquire();

                }
            }
        }, 15000);*/



         deviceManger.lockNow() ;

    }

    public void finishWakeLocker() {
        if (wakeLock != null){
            Log.d("FINISHED","finihed");
            wakeLock.release();

        }
    }


    @Override
    protected void onActivityResult ( int requestCode , int resultCode , @Nullable Intent
            data) {
        super .onActivityResult(requestCode , resultCode , data) ;
        switch (requestCode) {
            case RESULT_ENABLE :
                if (resultCode == Activity.RESULT_OK ) {
                    btnEnable .setText( "Disable" ) ;
                    btnLock .setVisibility(View. VISIBLE ) ;
                } else {
                    Toast. makeText (getApplicationContext() , "Failed!" ,
                            Toast. LENGTH_SHORT ).show() ;
                }
                return;
        }
    }
}