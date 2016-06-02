package com.example.arono.minesweeper.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by arono on 02/01/2016.
 */
public class MyService extends Service implements SensorEventListener{

    public static final String SENSOR_SERVICE_BROADCAST_ACTION = "ANGLE_CHANGED";
    public static final String SENSOR_SERVICE_VALUES_KEY = "ANGLE_CHANGE_KEY";

    private int delta = 5;
    private float x;
    private float y;
    private float z;
    boolean isFirstMeasure = true;
    private final IBinder binder = new LocalBinder();
    private Sensor sensor;
    private SensorManager sensorManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        return binder;
    }


    public class LocalBinder extends Binder {
        public MyService getService(){
            return MyService.this;
        }
    }


    @Override
    public boolean onUnbind(Intent intent) {
        sensorManager.unregisterListener(this);
        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(isFirstMeasure) {
            x = sensorEvent.values[0];
            y = sensorEvent.values[1];
            z = sensorEvent.values[2];

            isFirstMeasure = false;
        }

        else {
            if(Math.abs(sensorEvent.values[0] - x) > delta || Math.abs(sensorEvent.values[1] - y) > delta
                    || Math.abs(sensorEvent.values[2] - z) > delta ) {
                notifyAngleChanged(true);
            }

            else
                notifyAngleChanged(false);

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void notifyAngleChanged(boolean isAngleChanged) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SENSOR_SERVICE_BROADCAST_ACTION);
        broadcastIntent.putExtra(SENSOR_SERVICE_VALUES_KEY, isAngleChanged);

        sendBroadcast(broadcastIntent);
    }
}
