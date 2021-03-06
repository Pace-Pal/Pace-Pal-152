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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static java.util.logging.Logger.global;

public class MyMap extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private MapView mapView;              //declares map

    private double locLong = 0;          //declares variables for location
    private double locLat = 0;           //"loc" is always the local player
    private int locPlace = 0;
    private int locOldPlace = 0;
    double oldLon,oldLat;
    double localDistance = 0;            //variables for calculating total distance

    private TextToSpeech TSS;

    String sessionType;
    String sessionID;

    Boolean stopUpdates = false;             //stop interacting with database after session end

    boolean lineInit = false;            //variables for creating the line on the map view


    private GoogleApiClient googleApiClient;         //for location API
    private LocationRequest mLocationRequest;

    private ArrayList<RemotePlayer> remotePlayers =new ArrayList<RemotePlayer>();
    private ArrayList<PolylineOptions> polylines=new ArrayList<PolylineOptions>();


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

        TSS = new TextToSpeech(this);

        //sessionStatus = findViewById(R.id.sessionStatus);

        //gets shared preferences to find current session
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MyMap.this);
        sessionID = sharedPref.getString("sessionID","");
        sessionType = sharedPref.getString("sessionType","");

        Log.d("sessionType",sessionType);

        findViewById(R.id.sessionExitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endingInit();
            }
        });

        //start updating location variables
        startLocationUpdates();

        //gets remote players from database

        GetRemotePlayers remotePlayerClass = new GetRemotePlayers(sessionID, this);
        remotePlayers = remotePlayerClass.getPlayerList();



        //gets end goal if there is one


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



        RecyclerView recyclerView = findViewById(R.id.remotePlayerRecycler);
        RemotePlayerRecyclerAdapter adapter = new RemotePlayerRecyclerAdapter(remotePlayers,"players",this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);
        Log.d("mymap",remotePlayers.toString());



        //Not sure if actually needed
        //Instantiating the GoogleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        //declares local players line on the map
        PolylineOptions locLine = new com.mapbox.mapboxsdk.annotations.PolylineOptions();

        //TextView palDistText = findViewById(R.id.palSessionMiles);

        //creates handler to handle runnable for updating location
        //interval to update location
        int delay = 2000; //milliseconds
        Handler handler = new Handler();
        TextView sessionStatus = findViewById(R.id.sessionStatus);

        //runnable for updating location
        handler.postDelayed(new Runnable() {
            public void run() {

                if(remotePlayerClass.getSessionEnded())
                    endingInit();

                sortPlayers(remotePlayers);

                TextView localDistText = findViewById(R.id.localSessionMiles);
                String setMilesText = String.valueOf(round(localDistance,2)) + " Miles";
                localDistText.setText(setMilesText);
                Log.d("mymap",remotePlayers.toString());


                Log.d("sessionType", sessionType);
                if(sessionType.equals("1")){
                    sessionStatus.setText("You are in place " + locPlace);
                    if(localDistance >= remotePlayerClass.getWinCondition()){
                        endingInit();
                    }
                }
                if (sessionType.equals("2")){
                    Double totalDistanceText = localDistance;
                    for(int i = 0; i < remotePlayers.size();i++){
                        totalDistanceText += remotePlayers.get(i).getDistance();
                    }
                    totalDistanceText = round(totalDistanceText,2);
                    sessionStatus.setText("Your total distance is: " + totalDistanceText.toString());
                    if(totalDistanceText >= remotePlayerClass.getWinCondition()){
                        endingInit();
                    }
                }

                if(remotePlayers.size() != 0)
                {
                    adapter.notifyDataSetChanged();
                }

                placeChangeSpeech();
                //code for updating map view markers
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(MapboxMap mapboxMap) {

                        Player tempPlayer = new Player(locLong,locLat,localDistance);
                        rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("players")
                                .child(userid).setValue(tempPlayer);


                        //if the line had not been initialized, add it to the map
                        if(!lineInit){
                            locLine.add(new LatLng(locLat, locLong));
                            locLine.color(Color.GREEN);
                            locLine.width(3);
                            mapboxMap.addPolyline(locLine);
                            Log.d("myMap", "remotePlayerCount " + remotePlayerClass.getPlayerCount());
                            for(int x = 0; x < remotePlayerClass.getPlayerCount();x++){
                                //PolylineOptions templine = new PolylineOptions();


                                //templine.add(new LatLng(remotePlayers.get(x).getLat(),remotePlayers.get(x).getLat()));
                                //templine.add(new LatLng(remotePlayers.get(x).getLat(),remotePlayers.get(x).getLat()));
                                //templine.color(Color.RED);
                                //templine.width(3);
                                //polylines.add(templine);
                                mapboxMap.addPolyline(remotePlayers.get(x).getPolyline());
                            }
                            lineInit = true;
                        }



                        locLine.add(new LatLng(locLat, locLong));

                        /*for(int x = 0; x < remotePlayerClass.getPlayerCount();x++){
                            PolylineOptions templine = new PolylineOptions();
                            templine = polylines.get(x);
                            templine.add(new LatLng(remotePlayers.get(x).getLat(),remotePlayers.get(x).getLat()));
                        }*/


                        //mapboxMap.clear();/
                        //add simple marker to map
                        //mapboxMap.addMarker(marker2);
                        //mapboxMap.addMarker(marker3);

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

        Location loc1 = new Location(""); //old location
        Location loc2 = new Location(""); //new location

        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);
        localDistance += loc1.distanceTo(loc2) * 0.00062137;


        Log.d("mymapsesid", sessionID);
        Log.d("mymapuid", userid);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void endingInit(){
        rtdb = FirebaseDatabase.getInstance().getReference();
        rtdb.child("sessionManager")
                .child("sessionIndex")
                .child(sessionID)
                .child("sessionEnded")
                .setValue(true);
        stopUpdates = true;
        finish();
        //TODO:Post session screen
    }


    private void sortPlayers(ArrayList<RemotePlayer> l){
        insertSortPlayers(l);


        boolean locPlayerRanked = true;

        for(int i = 0; i < l.size(); i++){
            if(l.get(i).getDistance() > localDistance) {
                l.get(i).setPlace(i + 1);
            }
            else{
                if(locPlayerRanked) {
                    locPlayerRanked = false;
                    locPlace = i + 1;
                }
                l.get(i).setPlace(i+2);
            }

        }
    }

    private void insertSortPlayers(ArrayList<RemotePlayer> l){
        for (int i = 0; i < l.size() - 1; i++)  //runs for every element -1
        {
            int pos = i;
            if (l.get(pos + 1).getDistance() > l.get(pos).getDistance())  //if next value is less than current value
            {
                while (l.get(pos + 1).getDistance() > l.get(pos).getDistance()) //searches for proper place for value
                {
                    Collections.swap(l,pos+1,pos);
                    if (pos == 0)
                        break;
                    else
                        pos -= 1;
                }
            }
        }
    }

    private void placeChangeSpeech(){
        if(locPlace != locOldPlace){
            TSS.speak("you are now in place" + locPlace);
            locOldPlace = locPlace;
        }
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
        TSS.pause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }






}