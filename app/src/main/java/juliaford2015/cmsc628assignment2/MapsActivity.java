package juliaford2015.cmsc628assignment2;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnClickListener, OnMapClickListener, OnMapReadyCallback, OnMyLocationChangeListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private MarkerOptions myMarkerOptions;
    private MarkerOptions goalMarkerOptions;
    private Marker myMarker;
    private Marker goalMarker;

    private static final double RADIUS = 200;
    private static final String STATE_MY_LOCATION = "MyLocation";
    private static final String STATE_DESTINATION = "Destination";

    private LatLng myLatLng;
    private LatLng goalLatLng;
    private static Location tempLocation = new Location("temp");

    private CircleOptions circleOptions = null;
    private Circle circle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        (findViewById(R.id.fixButton)).setOnClickListener(this);
        (findViewById(R.id.clearButton)).setOnClickListener(this);
        if (savedInstanceState != null) {
            try {
                double[] myLoc = savedInstanceState.getDoubleArray(STATE_MY_LOCATION);
                myLatLng = new LatLng(myLoc[0], myLoc[1]);
            }
            catch (Exception e) {e.printStackTrace();}
            try {
                double[] destLoc = savedInstanceState.getDoubleArray(STATE_DESTINATION);
                goalLatLng = new LatLng(destLoc[0], destLoc[1]);
                onMapClick(goalLatLng); //not the cleanest, but it works.
            }
            catch (Exception e) {e.printStackTrace();}
        }
    }

    /**
     * pass LatLng -> move destination marker and circle
     */
    @Override
    public void onMapClick(LatLng ll) {
        goalLatLng = ll;
        if (goalMarkerOptions == null)
            goalMarkerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        if (goalMarker != null)
            goalMarker.remove();
        goalMarker = (mMap.addMarker(goalMarkerOptions.position(goalLatLng).title(goalLatLng.toString())));
        if (circleOptions == null)
            circleOptions = new CircleOptions().radius(RADIUS).strokeColor(Color.RED).strokeWidth(2);
        if (circle != null)
            circle.remove();
        circle = mMap.addCircle(circleOptions.center(goalLatLng));
        //circle.setCenter(goalLatLng);
    }

    /**
     * store current position and destination position data
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (myLatLng != null) savedInstanceState.putDoubleArray(STATE_MY_LOCATION,
                    new double[]{myLatLng.latitude, myLatLng.longitude});
        if (goalLatLng != null) savedInstanceState.putDoubleArray(STATE_DESTINATION,
                    new double[]{goalLatLng.latitude, goalLatLng.longitude});
    }


    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        //startLocationUpdates();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
        if (myLatLng != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLatLng).zoom(20).build();
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
            mMap.animateCamera(update);
        }
    }

    /**
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationChangeListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onMyLocationChange(Location location) {
        myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        // (set up and) move current position marker
        if (myMarkerOptions == null)
            myMarkerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        if (myMarker != null)
            myMarker.remove();
        myMarker = mMap.addMarker(myMarkerOptions.position(myLatLng).title(myLatLng.toString()));
        //  if there is no destination specified, just zoom to current position:
        if (goalLatLng == null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        //  or, try to calculate distance between current and destination positions:
        if (myLatLng != null && goalLatLng != null) {
            // we want straight-line distance, not pathfinding calculation
            tempLocation.setLatitude(goalLatLng.latitude);
            tempLocation.setLongitude(goalLatLng.longitude);
            double magDistance = (location.distanceTo(tempLocation));
            if (magDistance <= RADIUS) {
                Toast.makeText(getApplicationContext(), "Distance: "+magDistance, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * custom button changes the map zoom to show both the current location marker
     * and the destination marker, zooming out to a comfortable distance
     */
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.fixButton:
                if (goalLatLng != null) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(myLatLng);
                    builder.include(goalLatLng);
                    LatLngBounds bounds = builder.build();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1));
                    // zoom out a bit, otherwise markers will be on screen edges
                    mMap.moveCamera(CameraUpdateFactory.zoomOut());
                }
                break;
            case R.id.clearButton:
                goalLatLng = null;
                goalMarker.remove();
                circle.remove();
                break;
        }


    }

}
