package com.example.taxirider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taxirider.Model.Rider;
import com.example.taxirider.common.Common;
import com.example.taxirider.helper.CustomerInforWindow;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Arrays;

public class Home extends AppCompatActivity
implements  NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private AppBarConfiguration mAppBarConfiguration;
    SupportMapFragment mapFragment;


    PlaceAutocompleteFragment editLocation;

    //Location
    private GoogleMap mMap;
    //play service
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICE_REQ_CODE = 7001;

    // private LocationRequest mLocationRequest;
    LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVAL = 5000;//5s
    private static int FATEST_INTERVAL = 5000;
    private static int DISPLACEMENT = 5000;

    DatabaseReference mDrivers;
    GeoFire geoFire;
    Marker mUserMarket;

    //
    ImageView img;
    BottomSheetRider bottomSheetRider;

    //
    Button btnRequest;
    boolean isDriverFound=false;
    String driverId;
    int radius=1; //1km
    final int LIMIT=12;//find
    int distance=1;




    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_home );
        Toolbar toolbar = findViewById ( R.id.toolbar );
        setSupportActionBar ( toolbar );

        DrawerLayout drawer = findViewById ( R.id.drawer_layout );
        NavigationView navigationView = findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( this );
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder (
                R.id.nav_home,R.id.nav_gallery,R.id.nav_slideshow )
                .setDrawerLayout ( drawer )
                .build ();

        //Maps
        mapFragment = (SupportMapFragment) getSupportFragmentManager ().findFragmentById ( R.id.map );


        //
        mDrivers = FirebaseDatabase.getInstance().getReference(Common.driver);
        geoFire = new GeoFire(mDrivers);

        //initialize variable

        img= findViewById(R.id.imdExpand);
        bottomSheetRider= (BottomSheetRider) BottomSheetRider.newInstance("Rider bottom sheet");
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetRider.show(getSupportFragmentManager(),bottomSheetRider.mTag);
            }
        });

        btnRequest=(Button)findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPickupHere(FirebaseAuth.getInstance().getCurrentUser().getUid());
            }
        });






        setUpLocation();




      /*  NavController navController = Navigation.findNavController ( this,R.id.nav_host_fragment );
        NavigationUI.setupActionBarWithNavController ( this,navController,mAppBarConfiguration );
        NavigationUI.setupWithNavController ( navigationView,navController );*/
    }

    private void requestPickupHere(String uid) {

        DatabaseReference dbRequest= FirebaseDatabase.getInstance().getReference(Common.pickup_request);
        GeoLocation location = new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        GeoFire geoFire= new GeoFire(dbRequest);
        geoFire.setLocation(uid,location);

        //check
        if(mUserMarket.isVisible())
            mUserMarket.remove();
        //add new market
        MarkerOptions markerOptions;
        mUserMarket= mMap.addMarker(new MarkerOptions()
                .title("I'm here")
                .snippet("")
                .position(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mUserMarket.showInfoWindow();
        btnRequest.setText("Getting driver...");
        findDriver();


    }

    private void findDriver() {

        DatabaseReference driver = FirebaseDatabase.getInstance().getReference(Common.driver);
        GeoFire geoFire= new GeoFire(driver);
        GeoQuery geoQuery= geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),radius);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //if found
                if(!isDriverFound){
                    isDriverFound=true;
                    driverId=key;
                    btnRequest.setText("Call Driver");
                    Toast.makeText(Home.this, "Dirver id"+key, Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //if not find, radius++
                if (!isDriverFound){
                    radius++;
                    findDriver();
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request run time
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);

        } else {
            if (checkGooglePlayService()) {
                builGoogleClientApi();
                createLocationRequest();
                displayLocation();

            }
        }

    }

    public void createLocationRequest () {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void builGoogleClientApi () {
        Context context;
        Api api;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    private boolean checkGooglePlayService () {
        int resultRequest = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultRequest != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultRequest))
                GooglePlayServicesUtil.getErrorDialog(resultRequest, this, PLAY_SERVICE_REQ_CODE).show();
            else {
                Toast.makeText(this, "dien thaoi k dc ho tro", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;

    }



    private void displayLocation () {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
                final double lat = mLastLocation.getLatitude();//lay vi do
                final double longitude = mLastLocation.getLongitude();//lay kinh do
                Toast.makeText(Home.this, "hello" + String.valueOf(lat), Toast.LENGTH_SHORT).show();
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        mMap.setTrafficEnabled(false);
                        mMap.setIndoorEnabled(false);
                        mMap.setBuildingsEnabled(false);
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        mMap.setInfoWindowAdapter(new CustomerInforWindow(Home.this));
                        LatLng sydney = new LatLng(lat, longitude);
                        Toast.makeText(Home.this, "kinh do" + longitude, Toast.LENGTH_SHORT).show();
                        MarkerOptions options = new MarkerOptions().position(sydney).title("i am here");
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f));
                        mUserMarket = googleMap.addMarker(options);
                    }
                });

               loadAllDriverAvailable();

        } else {
            Log.d("Error", "displayLocation: khong the get your location");
        }
    }

    private void loadAllDriverAvailable() {
        //load all available drive on distance 3km
        DatabaseReference df= FirebaseDatabase.getInstance().getReference(Common.driver);
        GeoFire geoFire= new GeoFire(df);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()),distance);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //get key from table User
                FirebaseDatabase.getInstance()
                        .getReference(Common.driverInformation)
                        .child(key)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Rider user= dataSnapshot.getValue(Rider.class);
                                    System.out.println(user.toString());
                                    String title= user.getName()+"\n"+user.getPhone()+"\n"+user.getSex()+"\n"+user.getYear();
                                    //add driver to map
                                    Log.d("test", "onDataChange: "+title);

                                    mMap.addMarker(new MarkerOptions()
                                        .title(title)
                                            .position(new LatLng(location.latitude,location.longitude))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //distance just find 3km
                if (distance<=LIMIT){
                    distance++;
                    loadAllDriverAvailable();

                }


            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate ( R.menu.home,menu );
        return true;
    }

    /* @Override
   public boolean onSupportNavigateUp () {
        NavController navController = Navigation.findNavController ( this,R.id.nav_host_fragment );
        return NavigationUI.navigateUp ( navController,mAppBarConfiguration )
                || super.onSupportNavigateUp ();
    }*/


    @Override
    public boolean onNavigationItemSelected (@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdate();

    }

    private void startLocationUpdate () {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();

    }
}
