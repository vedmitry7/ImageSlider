package com.example.bacillo.imageslider;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final int RESULT_CODE_INTERVAL_CHANGED = 110;
    public static final int RESULT_CODE_PATH_CHANGED = 112;
    public static final int DEFAULT_INTERVAL = 5000;
    public static final int HIDE_FAB_INTERVAL = 3000;
    public static final     String INTERVAL = "interval";
    public static final     String PATH  = "path";
    public static final     String ARRAY  = "array";
    public static final     String HOURS  = "hours";
    public static final     String MINUTES  = "minutes";
    public static final     String SET_FINISH  = "set finish";
    public static final     String ACTION_ALARM  = "action.alarm";
    public static final     String DEFAULT_PATH = "//sdcard/dcim/100MEDIA";
    public static final     String POSITION = "position";
    private final           String NAME_PREFERENCES  = "pref";

    private String currentPath;
    private FloatingActionButton fab;
    private SharedPreferences sharedPreferences;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ImageFragment fragment;

    private ArrayList<String> stringsList;
    private int interval = DEFAULT_INTERVAL;
    private int position = 0;
    private Timer timer;
    private TimerTask task;

    private Timer fabTimer;
    private TimerTask fabTask;
    private Handler fabHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("tag", "onCreate() " + getIntent().getAction());
        setContentView(R.layout.activity_main);

        initView();
        loadPreferences();
        onNewIntent(this.getIntent());

        try {
            stringsList = ChooseFolderActivity.getFiles(currentPath);
        }
        catch (NullPointerException e) {
        }
    }

    @Override
    public void onAttachedToWindow() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

            if (intent.getAction().equals(SET_FINISH)) {

                int hours = intent.getIntExtra(HOURS, 1);
                int minutes = intent.getIntExtra(MINUTES, 1);
                Timer timerFinish = new Timer();

                TimerTask taskFinish = new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                    }
                };

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hours);
                calendar.set(Calendar.MINUTE, minutes);

                long delay = calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                if (delay < 50000) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    delay = calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                }
                timerFinish.schedule(taskFinish, delay);
            }
    }

    @Override
    protected void onPause() {
        if(timer!=null)
        timer.cancel();
        savePreferences();
        super.onPause();
    }


    private void loadPreferences(){
        sharedPreferences = this.getSharedPreferences(NAME_PREFERENCES, Context.MODE_PRIVATE);
        currentPath = sharedPreferences.getString(PATH, DEFAULT_PATH);
        if(sharedPreferences.contains(PATH))
            position = sharedPreferences.getInt(POSITION, 0);
        else
            position = 0;
        interval = sharedPreferences.getInt(INTERVAL, DEFAULT_INTERVAL);
    }

    @Override
    protected void onResume(){
        super.onResume();
        newTimerTask(interval);
    }


    private void savePreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PATH, currentPath);

        if(position==0) {
            editor.putInt(POSITION, position);
        }
        else {
            editor.putInt(POSITION, position - 1);
        }
        editor.putInt(INTERVAL, interval);
        editor.commit();
    }

    private void initView(){

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fabHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                fab.hide();
            }
        };

        fabTimer = new Timer();

        fragmentManager = getFragmentManager();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null)
                    timer.cancel();
                Intent intent = new Intent(getApplicationContext(), ConfigActivity.class);
                intent.putExtra(INTERVAL, interval);
                intent.putExtra(PATH, currentPath);
                startActivityForResult(intent, 10);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

        });
        fab.hide();
   }

    public void onClick(View v){
        fab.show();
        if(fabTask!=null)
            fabTask.cancel();
        fabTask = new TimerTask() {
            @Override
            public void run() {
                fabHandler.sendEmptyMessage(1);
            }
        };

        fabTimer.schedule(fabTask, HIDE_FAB_INTERVAL);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case RESULT_CODE_INTERVAL_CHANGED:
                interval = data.getIntExtra(INTERVAL, DEFAULT_INTERVAL);
                break;

            case RESULT_CODE_PATH_CHANGED:
                currentPath = data.getStringExtra(PATH);
                synchronized (this) {
                    stringsList = data.getStringArrayListExtra(ARRAY);
                    position = 0;
                }
                break;

            case RESULT_OK:
                interval = data.getIntExtra(INTERVAL, DEFAULT_INTERVAL);
                currentPath = data.getStringExtra(PATH);
                synchronized (this) {
                    stringsList = data.getStringArrayListExtra(ARRAY);
                    position = 0;
                }
                break;

            case RESULT_CANCELED:
                break;
        }
    }

    private void newTimerTask(int interval){
        if( stringsList != null && stringsList.size() > 0) {
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {
                    if (position == stringsList.size())
                        position = 0;
                    fragmentTransaction = fragmentManager.beginTransaction();
                    synchronized (this) {
                        fragment = ImageFragment.newInstance(stringsList.get(position++));
                        fragmentTransaction.replace(R.id.container, fragment);
                    }
                    fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            };

            timer.schedule(task, 0, interval);
        } else {
            fragmentTransaction = fragmentManager.beginTransaction();
            ErrorFragment errorFragment = ErrorFragment.newInstance(currentPath);
            fragmentTransaction.replace(R.id.container, errorFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}


