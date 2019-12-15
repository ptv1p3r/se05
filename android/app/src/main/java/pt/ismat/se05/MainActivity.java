package pt.ismat.se05;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity implements LocationListener {

    public final int REQUEST_CODE_INTERNET = 103;
    public final int REQUEST_CODE_ACCESS_FINE_LOCATION = 100;
    public final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 101;
    public final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 102;

    private Intent forService;

    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneratedPluginRegistrant.registerWith(this);

        forService = new Intent(MainActivity.this,MyService.class);

        new MethodChannel(getFlutterView(),"pt.ismat.se05.messages")
                .setMethodCallHandler(new MethodChannel.MethodCallHandler() {
                    @Override
                    public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                        if(methodCall.method.equals("startService")){
                            startService();
                            startDataTracking();
                            result.success("Service Started");
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(forService);
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
    }

    private void startDataTracking(){

        // Permission check and request
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_ACCESS_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);

        locationManager.requestLocationUpdates(provider, 5000, 0, this);
//    Location location = locationManager.getLastKnownLocation(provider);
//
//    // Initialize the location fields
//    if (location != null) {
//      System.out.println("Provider " + provider + " has been selected.");
//      onLocationChanged(location);
//    } else {
//      System.out.println("Location not available");
//    }

    }

    private void startService(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(forService);
        } else {
            startService(forService);
        }
    }

    private void writeDataToFile(double _latitude, double _longitude, double _altitude, double _speed, double _bearing){
        FileWriter writer = null;

        // Permission check and request
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }

        try
        {
            File root = new File(Environment.getExternalStorageDirectory()+ File.separator + "Trajetorias");
            if (!root.exists())
            {
                root.mkdirs();
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date now = new Date();
            String fileName = formatter.format(now) + ".csd";
            File dataFile = new File(root, fileName);

            if (!dataFile.exists())
            {
                writer = new FileWriter(dataFile,false);
                writer.write("ISMAT SE 2019-2020 Data point file" + "\n\n");
                writer.write("Inicials,Latitude,Longitude,Altitude,Speed,Bearing,Datetime"+ "\n");
                writer.flush();
                writer.close();
            }

            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            writer = new FileWriter(dataFile,true);
            writer.append("RS," + _latitude + "," + _longitude + "," + _altitude + "," + _speed + "," + _bearing + "," + currentDateTime + "\n");

            writer.flush();
            writer.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//    System.out.println("Coordenadas lat: " + location.getLatitude() + " lng: " + location.getLongitude());

        if (location != null){
            writeDataToFile(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getSpeed(), location.getBearing());
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        System.out.println("onStatusChanged: " + s);
    }

    @Override
    public void onProviderEnabled(String s) {
        System.out.println("onProviderEnabled: " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        System.out.println("onProviderDisabled: " + s);
    }
}