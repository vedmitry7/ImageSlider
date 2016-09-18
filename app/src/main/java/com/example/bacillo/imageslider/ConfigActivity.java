package com.example.bacillo.imageslider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class ConfigActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private int startHour , startMinute, endHour, endMinute;
    private Calendar calendar;
    private Context context = this;
    private TextView textInterval, textPath;
    private SeekBar seekBar;
    private int interval;
    private Intent intent;
    private boolean FLAG_seekBarChanged = false, FLAG_pathChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        initView();
        initToolBar();
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.settings);
    }

    @Override
    public void finish() {
        if(FLAG_seekBarChanged && !FLAG_pathChanged){
            Intent intent = new Intent();
            intent.putExtra(MainActivity.INTERVAL, interval * 1000);
            setResult(MainActivity.RESULT_CODE_INTERVAL_CHANGED, intent);
            super.finish();
            return;
        }
        if(FLAG_seekBarChanged && FLAG_pathChanged ){
            Intent intent1 = new Intent(intent);
            intent1.putExtra(MainActivity.INTERVAL, interval * 1000);
            setResult(RESULT_OK, intent1);
            super.finish();
            return;
        }
        if(!FLAG_seekBarChanged && FLAG_pathChanged){
            setResult(MainActivity.RESULT_CODE_PATH_CHANGED, intent);
            super.finish();
            return;
        }
        else {
            setResult(RESULT_CANCELED);
        }
        super.finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initView() {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        textPath = (TextView) findViewById(R.id.textpath);
        textInterval = (TextView) findViewById(R.id.text_interval);

        Intent intent = getIntent();
        int i = intent.getIntExtra(MainActivity.INTERVAL, MainActivity.DEFAULT_INTERVAL);
        String path = intent.getStringExtra(MainActivity.PATH);
        textInterval.setText(String.valueOf(i / 1000) + getString(R.string.seconds));

        seekBar.setProgress(i / 1000 - 1);
        textPath.setText(path);
    }

    public void onClickChooseFolder(View v){
        Intent intent = new Intent(this, ChooseFolderActivity.class);
        startActivityForResult(intent, 5);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data==null) {
            return;
        }
        switch (resultCode){
            case RESULT_OK:
                String path = data.getStringExtra(MainActivity.PATH);
                textPath.setText(path);
                intent = data;
                FLAG_pathChanged = true;
        }
    }

    public void setSlideShow(View v){
        calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute1 = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(view.isShown()) {

                    startHour = hourOfDay;
                    startMinute = minute;
                    TimePickerDialog timePickerDialog2 = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            endHour = hourOfDay;
                            endMinute = minute;

                            calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, startHour);
                            calendar.set(Calendar.MINUTE, startMinute);
                            calendar.set(Calendar.SECOND, 0);

                            Intent intent = new Intent(getApplicationContext(), SlideShowReceiver.class);
                            intent.setAction(MainActivity.ACTION_ALARM);
                            intent.putExtra(MainActivity.HOURS, endHour);
                            intent.putExtra(MainActivity.MINUTES, endMinute);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                        }
                    }, startHour, startMinute, true);
                    timePickerDialog2.setTitle(getString(R.string.end_of_show));
                    timePickerDialog2.show();
                }
            }
        }, hour, minute1, true);
        timePickerDialog.setTitle(getString(R.string.start_of_show));
        timePickerDialog.show();
    }



    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        textInterval.setText(String.valueOf(progress + 1) + getString(R.string.seconds));
        interval = progress + 1;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        FLAG_seekBarChanged = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
