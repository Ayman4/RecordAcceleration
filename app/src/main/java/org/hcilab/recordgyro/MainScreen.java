package org.hcilab.recordgyro;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainScreen extends AppCompatActivity implements SensorEventListener, LocationListener {

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    SensorManager sm;
    Sensor s;
    Sensor Gyro;
    TextView txtviewx;
    TextView txtviewy;
    TextView txtviewz;

    TextView txtviewGyrox;
    TextView txtviewGyroy;
    TextView txtviewGyroz;

    TextView txtviewloc;
    float x;
    float y;
    float z;
    LocationManager lm;
    String timeStamp;

    ArrayList<String> AllPoints=new ArrayList<String>();
    private FirebaseDatabase database;
    DatabaseReference nodeReference ;


int count;

    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);
        txtviewloc = (TextView) findViewById(R.id.textViewloc);
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                ToggleButton tb=(ToggleButton) findViewById(R.id.toggleButton);
                StartRecording();
                tb.setChecked(true);


                //txtviewloc.setText("Action was DOWN" + count);
                return true;
            case (MotionEvent.ACTION_UP):
                tb=(ToggleButton) findViewById(R.id.toggleButton);
                tb.setChecked(false);
                return true;

           /* case (MotionEvent.ACTION_MOVE):
                txtviewloc.setText("Move");
                return true;
            case (MotionEvent.ACTION_UP):
                txtviewloc.setText("Up");
                return true;

            case (MotionEvent.ACTION_CANCEL):
                txtviewloc.setText("Cancel");
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                txtviewloc.setText("Out Bounds");
                return true;*/
            default:
                return super.onTouchEvent(event);
        }
    }


    public MainScreen() throws IOException {
        database = FirebaseDatabase.getInstance();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {



     //   timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
     //   nodeReference = database.getReference().child(timeStamp);
     //   count=0;


        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        //super.onCreate(savedInstanceState);
        //  setContentView(R.layout.activity_main);
        txtviewx = (TextView) findViewById(R.id.textViewx);
        txtviewy = (TextView) findViewById(R.id.textViewY);
        txtviewz = (TextView) findViewById(R.id.textViewZ);


        txtviewGyrox = (TextView) findViewById(R.id.textViewGyroX);
        txtviewGyroy = (TextView) findViewById(R.id.textViewGyroY);
        txtviewGyroz = (TextView) findViewById(R.id.textViewGyroZ);

        txtviewloc = (TextView) findViewById(R.id.textViewloc);

        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        s = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        Gyro = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);







       /* lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return false;
        }
        lm.requestLocationUpdates(lm.GPS_PROVIDER, 1000, 1, this);
*/
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void TbClicked(View view)
    {
       StartRecording();

    }

    private void StartRecording() {

        ToggleButton tb=(ToggleButton) findViewById(R.id.toggleButton);
       timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        nodeReference = database.getReference().child(timeStamp);
        count=0;


    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        x=event.values[0];
        y=event.values[1];
        z=event.values[2];

        ToggleButton tb=(ToggleButton) findViewById(R.id.toggleButton);


        if (tb.isChecked()) {

            if (event.sensor.equals(Gyro)) {
                txtviewGyrox.setText("Gyrox= " + x);
                txtviewGyroy.setText("Gyroy= " + y);
                txtviewGyroz.setText("Gyroz= " + z);
                // Write a message to the database
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        String timeStamp1 = new SimpleDateFormat("HH-mm-ss-SSS").format(new Date());

                        nodeReference.child(timeStamp).child(String.valueOf(count));

                        nodeReference.child(timeStamp).child(String.valueOf(count)).child("TimeStamp").setValue(timeStamp1);


                        nodeReference.child(timeStamp).child(String.valueOf(count)).child("AccZ").setValue(z);
                        nodeReference.child(timeStamp).child(String.valueOf(count)).child("AccY").setValue(y);
                        nodeReference.child(timeStamp).child(String.valueOf(count)).child("AccX").setValue(x);
                        count++;
                    }
                });

                t.start();



            } else {

                nodeReference.child("LinearX").setValue(x);
                txtviewx.setText("LinearAccx= " + x);
                txtviewy.setText("Accy= " + y);
                txtviewz.setText("Accz= " + z);
                //finish();

            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        txtviewloc.setText("Long : "+location.getLongitude()+"Altitude: "+location.getAltitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }
    protected  void onResume()
    {
        super.onResume();
        try {
            sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
            sm.registerListener(this, Gyro, SensorManager.SENSOR_DELAY_NORMAL);
        }
        catch (Exception e)
        {
            txtviewx.setText(e.getMessage());

        }

    }

}
