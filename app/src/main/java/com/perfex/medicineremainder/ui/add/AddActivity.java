package com.perfex.medicineremainder.ui.add;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.perfex.medicineremainder.R;
import com.perfex.medicineremainder.databinding.ActivityAddBinding;
import com.perfex.medicineremainder.utils.Constants;

import java.io.IOException;

public class AddActivity extends AppCompatActivity implements SensorEventListener{

    ActivityAddBinding binding;
    private  long lastTime;
    private   float speed;
    private  float lastX;
    private  float lastY;
    private  float lastZ;
    private  float x, y, z;
    private int count;
    static final int SHAKE_THRESHOLD = 100;
    public static  int DATA_X = SensorManager.DATA_X;
    public static  int DATA_Y = SensorManager.DATA_Y;
    public static  int DATA_Z = SensorManager.DATA_Z;
    public SensorManager sensorManager;
    public Sensor accelerometerSensor;
    private PowerManager pm;
    private PowerManager.WakeLock wl;
    private Vibrator vibrator;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        Intent data = getIntent()   ;
        String title = data.getStringExtra(Constants.TITLE);
        setData(title,binding.title);
        String  description = data.getStringExtra(Constants.DESCRIPTION);
        setData(description,binding.description);
        String  time = data.getStringExtra(Constants.TIME);
        setData(time,binding.time);
        String  location = data.getStringExtra(Constants.LOCATION);
        setData(location,binding.time);
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if(alert == null){
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        mMediaPlayer = MediaPlayer.create(this,alert);
        mMediaPlayer.start();
        mMediaPlayer.setLooping(true);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final Button dismiss = findViewById(R.id.dismiss_btn);
        dismiss.setOnClickListener(v -> {
            vibrator.cancel();
            mMediaPlayer.release();
           finish();
        });
        pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
        startvibe();
    }

    private void setData(String data, TextView textView) {
        if(data == null){
            textView.setVisibility(View.GONE);
            return;
        }
        textView.setText(data);
    }

    @Override
    public void onStart(){
        super.onStart();
        if(accelerometerSensor !=null)
            sensorManager.registerListener((SensorEventListener) this, accelerometerSensor,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer!=null){

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) {
                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];
                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;
                if (speed > SHAKE_THRESHOLD) {
                    lastTime=currentTime;
                    count++;//for shake test
                    if(count==10)
                    {
                        vibrator.cancel();
                        finish();
                    }
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];
            }
        }
    }

    public void startvibe(){
        vibrator.vibrate(new long[]{100,1000,100,500,100,1000},0);
    }

}