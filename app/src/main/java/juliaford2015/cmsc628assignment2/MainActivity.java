package juliaford2015.cmsc628assignment2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

// reference: https://developer.android.com/training/location/receive-location-updates.html

public class MainActivity extends ActionBarActivity implements OnMapClickListener, OnMarkerClickListener {

    static float Latitude;
    static float Longitude;

    private LatLng destination;
    private Marker marker;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (savedInstanceState==null)
//        {
//            Latitude = 90.0f; // defaults to North Pole
//            Longitude = 0.0f;
//        }
//        destination = new LatLng(Latitude, Longitude);
//        map = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
//        map.setMyLocationEnabled(true);
//        map.getUiSettings().setCompassEnabled(true);
//        map.getUiSettings().setZoomControlsEnabled(true);
//        map.getUiSettings().setMyLocationButtonEnabled(true);
        LatLng HAMBURG  = new LatLng(53.558, 9.927);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG).title("HAMBURG"));
        map.setMyLocationEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setTrafficEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(HAMBURG).zoom(20).build();
        CameraUpdate update = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(update);
        CircleOptions circleOptions = new CircleOptions().center(HAMBURG).radius(100).strokeColor(Color.RED);
        Circle circle = map.addCircle(circleOptions);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onMapClick(LatLng latLng) {
        String str = String.valueOf("clicked: " + latLng.latitude) + " " + String.valueOf(latLng.longitude);
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
