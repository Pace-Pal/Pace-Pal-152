package com.group2.pacepal

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.Polyline
import com.mapbox.mapboxsdk.annotations.PolylineOptions
import com.mapbox.mapboxsdk.constants.Style
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.math.RoundingMode
import kotlinx.android.synthetic.main.my_map.*

class MyMap : AppCompatActivity() {
    private lateinit var mapView: MapView
    private var locLong = 0.0
    private var oldLong = 0.0
    private var locLat = 0.0
    private var oldLat = 0.0
    private var localDistance: Double = 0.0
    private var colabDistance: Double = 0.0
    var players: MutableList<String> = ArrayList()
    var playerClass: MutableList<RemotePlayer> = ArrayList()
    var distances: MutableList<Double> = ArrayList()
    var polylines: MutableList<PolylineOptions> = ArrayList()
    var polylineInit = false

    //ONLY TEMPORARY until Players become their own class, will be removed in future iterations
    private var p2Dist = 0.0
    private var p2Long = 0.0
    private var p2Lat = 0.0

    //private var sessionType: Int = 0

    private var googleApiClient: GoogleApiClient? = null         //for location API
    private val mLocationRequest = LocationRequest()

    internal var userid = FirebaseAuth.getInstance().currentUser!!.uid     //gets firebase info for current user and databases
    internal val fsdb = FirebaseFirestore.getInstance()
    internal val rtdb = FirebaseDatabase.getInstance().reference

    private val delay:Long = 2000
    private val mHandler = Handler()
    private lateinit var mLocUpdate:Runnable



    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("myMap","on Create Started")
        Mapbox.getInstance(this, getString(R.string.access_token))             //sets up map view
        setContentView(R.layout.my_map)
        mapView = findViewById<View>(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)

        startLocationUpdates()

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this@MyMap)           //gets session to load
        val sessionID = sharedPref.getString("sessionID", "")
        val sessionType = sharedPref.getString("sessionType", "")

        //gets local player info from firestore
        val docRef = fsdb.collection("users").document(userid)
        docRef.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    val textViewUName = findViewById<TextView>(R.id.localSessionUname)
                    textViewUName.text = document.get("username")!!.toString()
                    val localUserPic = findViewById<ImageView>(R.id.localSessionPic)
                    Picasso.with(applicationContext).load(document.get("profilepic")!!.toString()).into(localUserPic)
                } else {
                    //Log.d(TAG, "No such document");
                }
            } else {
                // Log.d(TAG, "get failed with ", task.getException());
            }
        })

        /*val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.d("myMap", dataSnapshot.toString())
                p2Dist = dataSnapshot.child("distance").value as Double
                p2Long = dataSnapshot.child("long").value as Double
                p2Lat = dataSnapshot.child("lat").value as Double
                // ...
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        }*/

        // TODO: load other players usernames and pictures
        val playersGet = object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                Log.d("myMap",p0.toString())
                p0.children.forEach{
                    if(it.key != userid) {
                        //players.add(it.key.toString())
                        //rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("players").child(it.key.toString()).addValueEventListener(postListener)
                        playerClass.add(RemotePlayer(it.key.toString(),sessionID))
                        polylines.add(com.mapbox.mapboxsdk.annotations.PolylineOptions())
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("players").addListenerForSingleValueEvent(playersGet)


        //starts listening for changes above


        val locLine = com.mapbox.mapboxsdk.annotations.PolylineOptions()
        //val p2Line = com.mapbox.mapboxsdk.annotations.PolylineOptions()
        //polylines.add(com.mapbox.mapboxsdk.annotations.PolylineOptions())

        //p2Line.add(LatLng(p2Lat,p2Long))
        //p2Line.color(Color.RED)


        /*mapView.getMapAsync { mapboxMap ->

            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
                mapboxMap.addPolyline(locLine)
                mapboxMap.addPolyline(p2Line)
            }
        }*/


        mLocUpdate = Runnable {

            //updates local user distance locally
            distance(oldLat,locLat,oldLong,locLong)

            //updates location of local user in database
            val user = Player(locLong,locLat,localDistance)
            rtdb.child("sessionManager").child("sessionIndex").child(sessionID).child("players").child(userid).setValue(user)



            //case for collaborative session
            if (sessionType === "2") {
                colabDistance = localDistance + p2Dist
                //val textColabDistance = "Total Distance: " + round(colabDistance, 2).toString()
                sessionStatus.text = "Total Distance: " + round(colabDistance, 2).toString()
            }
            //case for competitive sessions
            if (sessionType === "1") {
                if (localDistance > p2Dist)
                    sessionStatus.text = "You are leading by: " + round(localDistance - p2Dist, 2).toString()
                else
                    sessionStatus.text = "You are behind by: " + round(p2Dist - localDistance, 2).toString()
            }


            //TODO: Map marker updates
            mapView.getMapAsync { mapboxMap ->

                mapboxMap.setStyle(Style.MAPBOX_STREETS) {

                    if(!polylineInit) {
                        locLine.add(LatLng(locLat, locLong))
                        locLine.color(Color.GREEN)
                        locLine.width(3F)
                        mapboxMap.addPolyline(locLine)
                        var x = 0
                        while(x < playerClass.size){
                            polylines[x].add(LatLng(playerClass[0].getLat(),playerClass[0].getLong()))
                            polylines[x].color(Color.RED)
                            polylines[x].width(3F)
                            mapboxMap.addPolyline(polylines[x])
                            x++
                        }
                        polylineInit = true
                    }

                    locLine.add(LatLng(locLat,locLong))

                    var x = 0
                    while(x < playerClass.size){
                        polylines[0].add(LatLng(playerClass[0].getLat(),playerClass[0].getLong()))
                        x++
                    }

                }

            }

        }

        mHandler.postDelayed(mLocUpdate,delay)
        super.onCreate(savedInstanceState)
    }

    data class Player(
            var long: Double = 0.0,
            var lat: Double = 0.0,
            var distance: Double = 0.0
    )



    private fun startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest!!.setPriority(PRIORITY_HIGH_ACCURACY)
        mLocationRequest!!.setInterval(1500)
        mLocationRequest!!.setFastestInterval(1500)

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                // do work here
                onLocationChanged(locationResult!!.lastLocation)
            }
        },
                Looper.myLooper())
    }

    internal var loc1 = Location("")
    internal var loc2 = Location("")

    fun onLocationChanged(location: Location) {
        // New location has now been determined
        val msg = "Updated Location: " +
                java.lang.Double.toString(location.latitude) + "," +
                java.lang.Double.toString(location.longitude)
        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if (oldLat == 0.0)
            oldLat = location.latitude
        else
            oldLat = locLat
        locLat = location.latitude
        if (oldLong == 0.0)
            oldLong = location.longitude
        else
            oldLong = locLong
        locLong = location.longitude

        //distance(oldLat, locLat, oldLon, locLong)

    }

    fun distance(lat1: Double, lat2: Double,
                 lon1: Double, lon2: Double) {
        loc1.latitude = lat1
        loc1.longitude = lon1
        loc2.latitude = lat2
        loc2.longitude = lon2
        localDistance += loc1.distanceTo(loc2) * 0.00062137

    }

    fun round(value: Double, places: Int): Double {
        if (places < 0) throw IllegalArgumentException()

        var bd = BigDecimal(value)
        bd = bd.setScale(places, RoundingMode.HALF_UP)
        return bd.toDouble()
    }
}
