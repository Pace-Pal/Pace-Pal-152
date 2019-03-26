package com.group2.pacepal;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.core.MapboxService;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.squareup.picasso.Picasso;


import java.math.BigDecimal;
import java.math.RoundingMode;

import timber.log.Timber;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static java.util.logging.Logger.global;

public class MyMap extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private MapView mapView;              //declares map

    private double locLong = 0;          //declares variables for location
    private double locLat = 0;           //"loc" is always the local player
    double oldLon,oldLat;
    double localDistance = 0;            //variables for calculating total distance
    double p2Dist = 0;
    double p2Long = 0;                   //"p2" is always the remote player
    double p2Lat = 0;
    double colabDistance = 0;
    String textColabDistance = "0";

    String sessionType;
    String sessionID;
    Boolean sessionHost = false;

    TextView sessionStatus;

    Boolean stopUpdates = false;             //stop interacting with database after session end

    boolean locLineInit = false;            //variables for creating the line on the map view
    boolean p2LineInit = false;

    private GoogleApiClient googleApiClient;         //for location API
    private LocationRequest mLocationRequest;



    String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();     //gets firebase info for current user and databases
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference rtdb = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //various map elements
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.my_map);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        sessionStatus = findViewById(R.id.sessionStatus);

        //gets shared preferences to find current session
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyMap.this);


        findViewById(R.id.sessionExitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endingInit();
            }
        });

        //start updating location variables
        startLocationUpdates();

        //pulls info on where to find session in database
        sessionID = sharedPref.getString("sessionID", "");

        //accessess and displays profile for current user from firestore
        DocumentReference docRef = db.collection("users").document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        TextView textViewUName = findViewById(R.id.localSessionUname);
                        textViewUName.setText(document.get("username").toString());
                        ImageView localUserPic = findViewById(R.id.localSessionPic);
                        Picasso.with(getApplicationContext()).load(document.get("profilepic").toString()).into(localUserPic);
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    // Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //accesses and displays player 2 from firestore
        DocumentReference docRef2 = db.collection("users").document(friendUID);
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        TextView paltextViewUName = findViewById(R.id.palSessionUname);
                        paltextViewUName.setText(document.get("username").toString());
                        ImageView palUserPic = findViewById(R.id.palSessionPic);
                        Picasso.with(getApplicationContext()).load(document.get("profilepic").toString()).into(palUserPic);
                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    // Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //updates location data from other player on each change
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                if(sessionHost){
                    p2Dist = dataSnapshot.child("p2distance").getValue(Double.class);
                    p2Long = dataSnapshot.child("p2long").getValue(Double.class);
                    p2Lat = dataSnapshot.child("p2lat").getValue(Double.class);
                }
                else{
                    p2Dist =  dataSnapshot.child("p1distance").getValue(Double.class);
                    p2Lat = dataSnapshot.child("p1lat").getValue(Double.class);
                    p2Long  = dataSnapshot.child("p1long").getValue(Double.class);
                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };


        //starts listening for changes above
        rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").addValueEventListener(postListener);



        //NOT SURE IF NEEDED
        //Instantiating the GoogleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        //creates handler to handle runnable for updating location

        //interval to update location
        int delay = 2000; //milliseconds



        //declares local players line on the map
        PolylineOptions locLine = new com.mapbox.mapboxsdk.annotations.PolylineOptions();
        PolylineOptions p2Line = new com.mapbox.mapboxsdk.annotations.PolylineOptions();

        TextView palDistText = findViewById(R.id.palSessionMiles);
        Handler handler = new Handler();

        //runnable for updating location
        handler.postDelayed(new Runnable() {
            public void run() {
                /*MarkerOptions marker2 = new MarkerOptions()
                        .position(new LatLng(locLat, locLong))
                        .title("Player1")
                        .snippet("Player 1 location");
                MarkerOptions marker3 = new MarkerOptions()
                        .position(new LatLng(p2Lat,p2Long))
                        .title("Player 2")
                        .snippet("Player 2 location");*/

                //case for ending session
                if(p2Dist == -1 || p2Dist == -1){
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").removeEventListener(postListener);
                    rtdb.child("sessionManager").child("sessionIndex").child(sessionID).removeValue();
                    finish();
                    return;
                }
                else if(stopUpdates) {

                    return;
                }

                //case for collaborative session
                if(sessionType == "2"){
                    colabDistance = localDistance + p2Dist;
                    textColabDistance = "Total Distance: " + String.valueOf(round(colabDistance,2));
                    sessionStatus.setText(textColabDistance);
                }

                if(sessionType == "1"){
                    if(localDistance > p2Dist)
                        textColabDistance = "You are leading by: " + String.valueOf(round(localDistance - p2Dist,2));
                    else
                        textColabDistance = "You are behind by: " + String.valueOf(round(p2Dist - localDistance,2));

                    sessionStatus.setText(textColabDistance);
                }

                //associates distance texts to update

                palDistText.setText(String.valueOf(round(p2Dist,2)));

                TextView localDistText = findViewById(R.id.localSessionMiles);
                String setMilesText = String.valueOf(round(localDistance,2)) + " Miles";
                localDistText.setText(setMilesText);

                //code for updating map view markers
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(MapboxMap mapboxMap) {

                        //if the line had not been initialized, add it to the map
                        if(!locLineInit){
                            locLine.add(new LatLng(locLat, locLong));
                            locLine.color(Color.GREEN);
                            locLine.width(3);
                            mapboxMap.addPolyline(locLine);
                            locLineInit = true;
                        }
                        else {   //add new point to polyline
                            locLine.add(new LatLng(locLat, locLong));
                        }
                        if(!p2LineInit){
                            p2Line.add(new LatLng(locLat, locLong));
                            p2Line.color(Color.GRAY);
                            p2Line.width(3);
                            mapboxMap.addPolyline(p2Line);
                            p2LineInit = true;
                        }
                        else{
                            p2Line.add(new LatLng(p2Lat, p2Long));
                        }


                        Log.d("MapRefresh", "refreshed");


                    }
                });
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    public void onStart() {
        super.onStart();
        // Initiating the connection
        googleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        // Disconnecting the connection
        googleApiClient.disconnect();
    }

    //Callback invoked once the GoogleApiClient is connected successfully
    @Override
    public void onConnected(Bundle bundle) {
        //Fetching the last known location using the FusedLocationProviderApi
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    //Callback invoked if the GoogleApiClient connection fails
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    //location code I dont entirely understand

    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1500);
        mLocationRequest.setFastestInterval(1500);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    Location loc1 = new Location("");
    Location loc2 = new Location("");

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(oldLat == 0)
            oldLat = location.getLatitude();
        else
            oldLat = locLat;
        locLat = location.getLatitude();
        if(oldLon == 0)
            oldLon = location.getLongitude();
        else
            oldLon = locLong;
        locLong = location.getLongitude();

        distance(oldLat,locLat,oldLon,locLong);

    }



    public void distance(double lat1, double lat2, double lon1,
                         double lon2) {

        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);
        localDistance += loc1.distanceTo(loc2) * 0.00062137;


        rtdb = FirebaseDatabase.getInstance().getReference();
        Log.d("mymapsesid",sessionID);
        Log.d("mymapuid",userid);
        if(sessionHost) {
            rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p1distance").setValue(localDistance);
            rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p1long").setValue(lon2);
            rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p1lat").setValue(lat2);
        }
        else{
            rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p2distance").setValue(localDistance);
            rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p2long").setValue(lon2);
            rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("locations").child("p2lat").setValue(lat2);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void endingInit(){
        rtdb = FirebaseDatabase.getInstance().getReference();
        stopUpdates = true;
        finish();
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }






}