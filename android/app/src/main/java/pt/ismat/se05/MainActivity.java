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
import android.support.v4.app.ActivityCompat;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;

public class MainActivity extends FlutterActivity implements LocationListener {

  private Intent forService;

  private LocationManager locationManager;
  private String provider;
  private String tt;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);

    forService = new Intent(MainActivity.this,MyService.class);

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
    }

    // Get the location manager
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Criteria criteria = new Criteria();
    provider = locationManager.getBestProvider(criteria, false);
    Location location = locationManager.getLastKnownLocation(provider);

//     Initialize the location fields
    if (location != null) {
      System.out.println("Provider " + provider + " has been selected.");
      onLocationChanged(location);
    } else {
      tt = "Location not available";
    }

//    locationManager.requestLocationUpdates(provider, 400, 1, this);

    new MethodChannel(getFlutterView(),"pt.ismat.se05.messages")
            .setMethodCallHandler(new MethodChannel.MethodCallHandler() {
              @Override
              public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                if(methodCall.method.equals("startService")){
                  startService();

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
//  @Override
//  protected void onResume() {
//    super.onResume();
//    locationManager.requestLocationUpdates(provider, 400, 1, this);
//  }

  /* Remove the locationlistener updates when Activity is paused */
//  @Override
//  protected void onPause() {
//    super.onPause();
//    locationManager.removeUpdates(this);
//  }

  private void startService(){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
      startForegroundService(forService);
    } else {
      startService(forService);
    }
  }

  @Override
  public void onLocationChanged(Location location) {
    double lat = location.getLatitude();
    double lng = location.getLongitude();
    double alt = location.getAltitude();
    double vel = location.getSpeed();
    double dir = location.getBearing();
  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) {

  }

  @Override
  public void onProviderEnabled(String s) {
    tt = "Location not available";
  }

  @Override
  public void onProviderDisabled(String s) {

  }
}