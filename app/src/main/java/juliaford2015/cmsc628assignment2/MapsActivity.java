package juliaford2015.cmsc628assignment2;

import android.location.Location;
import android.location.LocationListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements LocationListener, OnMapClickListener, OnMapReadyCallback {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private static final String myString = "Me";
    private static final String destString = "End";
    private static final String STATE_MY_LOCATION = "MyLocation";
    private static final String STATE_DESTINATION = "Destination";

    private LatLng myLatLng = null;
    private LatLng destination = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        if (savedInstanceState == null) {
            setMyLatLng();
        }
        else {
            /*
                it's probably most efficient to try temporarily storing the user's location
                data. it can be retrieved faster than getting info from the google map.
             */
            try {
                double[] myLoc = savedInstanceState.getDoubleArray(STATE_MY_LOCATION);
                myLatLng = new LatLng(myLoc[0], myLoc[1]);
            }
            catch (Exception e) {
                setMyLatLng(); // didn't work. try again.
            }
            try {
                double[] destLoc = savedInstanceState.getDoubleArray(STATE_DESTINATION);
                destination = new LatLng(destLoc[0], destLoc[1]);
            }
            catch (Exception e) {
                // also automatically occurs if user hasn't selected a location yet.
                e.printStackTrace();
            }
        }

    }

    /**
     * uses the google maps API to find the user's location
     * then those values are read into this app
     */
    private void setMyLatLng() {
        Location myLocation = mMap.getMyLocation();
        // TODO: can't get location... why?
        if (myLocation != null) {
            myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(myLatLng).title(myString));
            Toast.makeText(getApplicationContext(),
                    "Me: " + myLatLng.latitude + "," + myLatLng.longitude, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Could not access my location!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapClick(LatLng d) {
        destination = d;
        mMap.clear();
        if (myLatLng != null) {
            mMap.addMarker(new MarkerOptions().position(myLatLng).title(myString));
        }
        mMap.addMarker(new MarkerOptions().position(destination).title(destString));
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (myLatLng != null)
            savedInstanceState.putDoubleArray(STATE_MY_LOCATION,
                    new double[]{myLatLng.latitude, myLatLng.longitude});
        if (destination != null)
            savedInstanceState.putDoubleArray(STATE_DESTINATION,
                    new double[]{destination.latitude, destination.longitude});
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
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
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
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (myLatLng != null && destination != null) {
            double latDistance = Math.abs(myLatLng.latitude - destination.latitude);
            double lonDistance = Math.abs(myLatLng.longitude - destination.longitude);
            // get squared distance
            double distance = Math.sqrt(Math.pow(latDistance, 2) + Math.pow(lonDistance, 2));
            if (distance <= 200) {
                // ?
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        setMyLatLng();
        if (myLatLng != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 13));
        }
    }
}
