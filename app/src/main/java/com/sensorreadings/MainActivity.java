package com.sensorreadings;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";
    private static final int PERMISSIONICONTACT = 1;
    private String inputFile;
    private SensorManager sensorManager;
    int rowNumberDynamic = 1;
    String[] PERMISSIONCONTACT = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Sensor accelerometer, gyroscope, magnetometer, light;
    TextView Accelx, Accely, Accelz, Gyrox, Gyroy, Gyroz, Magx, Magy, Magz, Light;
    String sensorDataAx = "", sensorDataAy = "", sensorDataAz = "", sensorDataGx = "",
            sensorDataGy = "", sensorDataGz = "", sensorDataMx = "", sensorDataMy = "", sensorDataMz = "",
            sLight = "", dataToWrite = "";
    Button buttonStart;
    Button buttonStop;
    ArrayList<String> AcX = new ArrayList<String>();
    ArrayList<String> AcY = new ArrayList<String>();
    ArrayList<String> AcZ = new ArrayList<String>();
    ArrayList<String> GyX = new ArrayList<String>();
    ArrayList<String> GyY = new ArrayList<String>();
    ArrayList<String> GyZ = new ArrayList<String>();
    ArrayList<String> MyX = new ArrayList<String>();
    ArrayList<String> MyY = new ArrayList<String>();
    ArrayList<String> MyZ = new ArrayList<String>();
    ArrayList<String> LightA = new ArrayList<String>();

    boolean isStarted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkPermissions(PERMISSIONCONTACT)) {
            ActivityCompat.requestPermissions(this, PERMISSIONCONTACT, PERMISSIONICONTACT);
        }

        Accelx = (TextView) findViewById(R.id.Accelx);
        Accely = (TextView) findViewById(R.id.Accely);
        Accelz = (TextView) findViewById(R.id.Accelz);

        Gyrox = (TextView) findViewById(R.id.Gyrox);
        Gyroy = (TextView) findViewById(R.id.Gyroy);
        Gyroz = (TextView) findViewById(R.id.Gyroz);

        Magx = (TextView) findViewById(R.id.Magx);
        Magy = (TextView) findViewById(R.id.Magy);
        Magz = (TextView) findViewById(R.id.Magz);

        Light = (TextView) findViewById(R.id.Light);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcX.clear();
                AcY.clear();
                AcZ.clear();
                GyX.clear();
                GyY.clear();
                GyZ.clear();
                MyX.clear();
                MyY.clear();
                MyZ.clear();
                LightA.clear();
                isStarted = true;
                dataToWrite = "Accel x, Accel y, Accel z, Gyro x, Gyro y, Gyro z, Mag x, Mag y, Mag z, Light intensity"+"\n";
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarted = false;
                for (int i = 0; i < AcX.size(); i++) {
                    try {
                        if (AcX.get(i) != null && AcY.get(i) != null && AcZ.get(i) != null && GyX.get(i) != null && GyY.get(i) != null
                                && GyZ.get(i) != null && MyX.get(i) != null && MyY.get(i) != null && MyZ.get(i) != null && LightA.get(i) != null) {
                            addRowToString(AcX.get(i), AcY.get(i), AcZ.get(i), GyX.get(i), GyY.get(i), GyZ.get(i),
                                    MyX.get(i), MyY.get(i), MyZ.get(i), LightA.get(i));
                        }
                    } catch (Exception ignored) {
                    }
                }
                sendToCSV();
            }
        });

        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(MainActivity.this, accelerometer, 5000000, 5000000);
            Log.d(TAG, "onCreate: Registered accelerometer listener");
        } else {
            Accelx.setText("Accelerometer not supported");
            Accely.setText("Accelerometer not supported");
            Accelz.setText("Accelerometer not supported");
        }

        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope != null) {
            sensorManager.registerListener(MainActivity.this, gyroscope, 5000000, 5000000);
            Log.d(TAG, "onCreate: Registered gyroscope listener");
        } else {
            Gyrox.setText("gyroscope not supported");
            Gyroy.setText("gyroscope not supported");
            Gyroz.setText("gyroscope not supported");
        }

        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magnetometer != null) {
            sensorManager.registerListener(MainActivity.this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered magnetometer listener");
        } else {
            Magx.setText("magnetometer not supported");
            Magy.setText("magnetometer not supported");
            Magz.setText("magnetometer not supported");
        }

        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (light != null) {
            sensorManager.registerListener(MainActivity.this, light, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered light listener");
        } else {
            Light.setText("light not supported");
        }


    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (isStarted) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                Accelx.setText("Accelx:" + sensorEvent.values[0]);//Ax
                sensorDataAx = sensorEvent.values[0] + "";
                AcX.add(sensorDataAx);
                Accely.setText("Accely:" + sensorEvent.values[1]);//Ay
                sensorDataAy = sensorEvent.values[1] + "";
                AcY.add(sensorDataAy);
                Accelz.setText("Accelz:" + sensorEvent.values[2]);//Az
                sensorDataAz = sensorEvent.values[2] + "";
                AcZ.add(sensorDataAz);

            } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                Gyrox.setText("Gyrox:" + sensorEvent.values[0]);
                sensorDataGx = sensorEvent.values[0] + "";
                GyX.add(sensorDataGx);
                Gyroy.setText("Gyroy:" + sensorEvent.values[1]);
                sensorDataGy = sensorEvent.values[0] + "";
                GyY.add(sensorDataGy);
                Gyroz.setText("Gyroz:" + sensorEvent.values[2]);
                sensorDataGz = sensorEvent.values[0] + "";
                GyZ.add(sensorDataGz);
            } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                Magx.setText("Magx:" + sensorEvent.values[0]);
                sensorDataMx = sensorEvent.values[0] + "";
                MyX.add(sensorDataMx);
                Magy.setText("Magy:" + sensorEvent.values[1]);
                sensorDataMy = sensorEvent.values[0] + "";
                MyY.add(sensorDataMy);
                Magz.setText("Magz:" + sensorEvent.values[2]);
                sensorDataMz = sensorEvent.values[0] + "";
                MyZ.add(sensorDataMz);
            } else if (sensor.getType() == Sensor.TYPE_LIGHT) {
                Light.setText("Light:" + sensorEvent.values[0]);
                sLight = sensorEvent.values[0] + "";
                LightA.add(sLight);
            }
        }
    }

    public boolean checkPermissions(String[] PERMISSIONNAME) {
        boolean checkk = true;
        int[] res = new int[PERMISSIONNAME.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = getApplicationContext().checkCallingOrSelfPermission(PERMISSIONNAME[i]);
            if (res[i] != PackageManager.PERMISSION_GRANTED) {
                checkk = false;
                break;
            }
        }
        return checkk;
    }

    void addRowToString(String sensorDataAx, String sensorDataAy, String sensorDataAz, String sensorDataGx, String sensorDataGy,
                        String sensorDataGz, String sensorDataMx, String sensorDataMy, String sensorDataMz, String Light) {
        dataToWrite += (sensorDataAx + "," + sensorDataAy + "," + sensorDataAz + "," + sensorDataGx + "," + sensorDataGy + "," + sensorDataGz + "," +
                sensorDataMx + "," + sensorDataMy + "," + sensorDataMz + "," + Light + "\n");

    }

    void sendToCSV() {

        try {
            File root = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/download");
            dir.mkdirs();
            File file = new File(dir, "SensorReadings.txt");
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.append(dataToWrite);
                myOutWriter.close();
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                Log.e("Exception", "File write failed: " + e.toString());
            }


        } catch (Exception ignored) {
        }


    }


}




