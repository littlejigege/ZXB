package com.qg.zxb.sensor;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.qg.zxb.viewmodel.MainViewModel;

import static android.content.ContentValues.TAG;

/**
 * Created by linzongzhan on 2017/10/18.
 */

public class DateGetter {
    private float result;
    private float Z;
    private float temp;
    private float resultY;
    private float resultZ;
    private int change;
    private int control = 0;
    private SensorManager sensorManager;
    private Sensor sensorG;
    private Sensor sensorA;
    private static final float NS2S = 1.0f / 1000000000.0f;
    private float timeStamp;
    private float degreeY;
    private float degreeZ;
    private float dy;
    private float dz;
    private float y;
    private int isMoving;
    private double G;
    private int index;
    private Activity activity;
    private MainViewModel bluetooth;
    private boolean isStart;
    private SensorEventListener mySensorEventListener1 = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {


                if (timeStamp != 0) {
                    float dT = (sensorEvent.timestamp - timeStamp) * NS2S;
                    degreeY += sensorEvent.values[1] * dT;
                    degreeZ += sensorEvent.values[2] * dT;
                    dy = (float) Math.toDegrees(degreeY);
                    dz = (float) Math.toDegrees(degreeZ);
                }
                timeStamp = sensorEvent.timestamp;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    private SensorEventListener mySensorEventListener2 = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                y = sensorEvent.values[2];
            }

            float xG = sensorEvent.values[0];
            float yG = sensorEvent.values[1];
            float zG = sensorEvent.values[2];
            Z = sensorEvent.values[2];

            if (index == 1) {
                G = Math.sqrt(xG * xG + yG * yG + zG * zG);
                index--;
            }
            if (Math.abs(G - Math.pow(xG * xG + yG * yG + zG * zG, 2)) - Math.abs(G) <= 1E4) {
                isMoving = 0;
            } else {
                isMoving = 1;
            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public static DateGetter with(Activity activity, MainViewModel bluetooth) {
        DateGetter dateGetter = new DateGetter();
        dateGetter.activity = activity;
        dateGetter.bluetooth = bluetooth;
        dateGetter.init();
        return dateGetter;
    }

    private void init() {
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensorG = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorA = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    public void start() {
        change = 1;
        sensorManager.registerListener(mySensorEventListener1, sensorG, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(mySensorEventListener2, sensorA, SensorManager.SENSOR_DELAY_GAME);
        isStart = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isStart) {
                    try {

                        Thread.sleep(100);

//                        if (change == 1) {
//                            if (Math.abs(Z) > G * 0.8) {
//                                result = dz;
//                                control = 1;
//                            } else {
//                                result = dy;
//                                control = 0;
//                            }
//                            change = 0;
//                        }
//                        if (Math.abs(Z) > G * 0.8 && control == 0) {
//                            temp = result - dz;
//                            control = 1;
//                        }
//                        if (Math.abs(Z) <= G * 0.8 && control == 1) {
//                            temp = result - dy;
//                            control = 0;
//                        }
//                        if (Math.abs(Z) > G * 0.8) {
//                            //水平
//                            result = temp + dz;
//                            bluetooth.sendText(result + "#" + isMoving);
//                            Log.d(TAG, "run: " + (int) result);
//                        } else {
//                            //垂直
//                            result = temp + dy;
//                            bluetooth.sendText(result + "#" + isMoving);
//                            Log.d(TAG, "run: " + (int) result);
//                        }
                        bluetooth.sendText((int) dz + "#" + isMoving);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();
    }

    public void stop() {
        sensorManager.unregisterListener(mySensorEventListener1);
        sensorManager.unregisterListener(mySensorEventListener2);
        isStart = false;
    }

    private Thread thread;

}
