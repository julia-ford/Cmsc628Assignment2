package juliaford2015.cmsc628assignment2;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
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

    public static final int REQUEST_CODE = 12345;
    public static final double RADIUS = 200;
    public static final String KEY_MY_LOCATION = "MyLocation";
    public static final String KEY_DESTINATION = "Destination";
    public static final String KEY_SEARCH_TEXT = "SearchText";

    private LatLng myPosition;
    private LatLng destination;
    private static Location tempLocation = new Location("temp");

    private CircleOptions circleOptions = null;
    private Circle circle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mMap.setOnMapClickListener(this);
        mMap.setOnMyLocationChangeListener(this);
        (findViewById(R.id.fixButton)).setOnClickListener(this);
        (findViewById(R.id.clearButton)).setOnClickListener(this);
        (findViewById(R.id.goToSearchButton)).setOnClickListener(this);
        if (savedInstanceState != null) {
            try {
                double[] myLoc = savedInstanceState.getDoubleArray(KEY_MY_LOCATION);
                myPosition = new LatLng(myLoc[0], myLoc[1]);
            }
            catch (Exception e) {e.printStackTrace();}
            try {
                double[] destLoc = savedInstanceState.getDoubleArray(KEY_DESTINATION);
                destination = new LatLng(destLoc[0], destLoc[1]);
                setDestination(destination); //not the cleanest, but it works.
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                ((EditText)(findViewById(R.id.search_bar))).setText(savedInstanceState.getString(KEY_SEARCH_TEXT));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setDestination(LatLng LL) {
        destination = LL;
        Toast.makeText(getApplicationContext(), "Go to: " +
                String.format("%3.3f", destination.latitude) + " , " +
                String.format("%3.3f", destination.longitude), Toast.LENGTH_SHORT).show();
        try {
            if (goalMarkerOptions == null)
                goalMarkerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            if (goalMarker != null)
                goalMarker.remove();
            goalMarker = (mMap.addMarker(goalMarkerOptions.position(destination).title(destination.toString())));

            if (circleOptions == null)
                circleOptions = new CircleOptions().radius(RADIUS).strokeColor(Color.RED).strokeWidth(2);
            if (circle != null)
                circle.remove();
            circle = mMap.addCircle(circleOptions.center(destination));
        }
        catch (Exception e) {
            e.printStackTrace(); // maybe it's not in focus or something
        }
    }

    /**
     * pass LatLng -> move destination marker and circle
     */
    @Override
    public void onMapClick(LatLng LL) {
        setDestination(LL);
    }

    /**
     * store current position and destination position data
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (myPosition != null) savedInstanceState.putDoubleArray(KEY_MY_LOCATION,
                new double[]{myPosition.latitude, myPosition.longitude});
        if (destination != null) savedInstanceState.putDoubleArray(KEY_DESTINATION,
                new double[]{destination.latitude, destination.longitude});
//        EditText searchbar = (EditText)findViewById(R.id.searchbar);
//        if (searchbar != null) savedInstanceState.putString(KEY_SEARCH_TEXT,
//                searchbar.getText().toString());
    }
//
//    // Helper method for formatting map queries
//    private String toFormattedQuery(String unformStr, LatLng unformLL)
//    {
//        return "geo:" + unformLL.latitude + ',' + unformLL.longitude + "?q=" + unformStr;
//    }

    @Override
    protected void onPause() {
        super.onPause();
        //locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        reCenter();
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
        if (myPosition != null) {
            //CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosition).zoom(20).build();
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myPosition).build();
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public void onMyLocationChange(Location location) {
        myPosition = new LatLng(location.getLatitude(), location.getLongitude());
        // (set up and) move current position marker
        if (myMarkerOptions == null)
            myMarkerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        if (myMarker != null)
            myMarker.remove();
        myMarker = mMap.addMarker(myMarkerOptions.position(myPosition).title(myPosition.toString()));
        //  if there is no destination specified, just zoom to current position:
        if (destination == null)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition));
        //  or, try to calculate distance between current and destination positions:
        if (myPosition != null && destination != null) {
            // we want straight-line distance, not pathfinding calculation
            tempLocation.setLatitude(destination.latitude);
            tempLocation.setLongitude(destination.longitude);
            double magDistance = (location.distanceTo(tempLocation));
            if (magDistance <= RADIUS) {
                Toast.makeText(getApplicationContext(),
                        "You are  "+magDistance + "m away!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void reCenter() {
        if (destination != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(myPosition);
            builder.include(destination);
            LatLngBounds bounds = builder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 1));
            // zoom out a bit, otherwise markers will be on screen edges
            mMap.moveCamera(CameraUpdateFactory.zoomOut());
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            double[] coords = data.getDoubleArrayExtra(KEY_DESTINATION);
            if (coords.length == 2) {
                setDestination( new LatLng(coords[0], coords[1]) );
                reCenter();
            }
        }
    }

    /**
     * custom button changes the map zoom to show both the current location marker
     * and the destination marker, zooming out to a comfortable distance
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fixButton:
                reCenter();
                break;
            case R.id.clearButton:
                destination = null;
                if (goalMarker != null) goalMarker.remove();
                if (circle != null) circle.remove();
                break;
            case R.id.goToSearchButton:
                if (myPosition != null) {
                    Intent intent = new Intent(view.getContext(), SearchActivity.class);
                    intent.putExtra(KEY_MY_LOCATION,
                            new double[]{myPosition.latitude, myPosition.longitude});
//                    //
                    //                   intent.setPackage("com.google.android.apps.maps");
                    //
//
//                    EditText searchbar = (EditText) findViewById(R.id.searchbar);
//                    String searchtext = "walmart";//searchbar.getText().toString();
//                    String formatted = toFormattedQuery(searchtext, myLatLng);
////                    if (!searchtext.equals("")) {
//                        Uri myIntentUri = Uri.parse(formatted);
//                        Intent intent = new Intent(Intent.ACTION_VIEW, myIntentUri);
                    intent.setPackage("com.google.android.apps.maps");
                    //startActivity(intent);
                    startActivityForResult(intent, REQUEST_CODE);
//                    }
                }
        }
    }

}
