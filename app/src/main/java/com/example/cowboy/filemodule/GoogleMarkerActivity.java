package com.example.cowboy.filemodule;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cowboy.filemodule.lib.SphericalUtil;
import com.example.cowboy.filemodule.maps.DownloadTask;
import com.example.cowboy.filemodule.maps.MyItem;
import com.example.cowboy.filemodule.maps.MyItemReader;
import com.example.cowboy.filemodule.maps.OwnIconRendered;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowCloseListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONException;

import java.io.InputStream;
import java.util.List;

/**
 * This shows how to place markers on a map.
 */
public class GoogleMarkerActivity extends AppCompatActivity implements
        OnMarkerClickListener,
        OnMarkerDragListener,
        OnInfoWindowLongClickListener,
        OnInfoWindowClickListener,
        OnInfoWindowCloseListener,
        OnMapReadyCallback{

    private static final LatLng DNEPR = new LatLng(48.487306, 34.932022);
    private double latitude;
    private double longitude;

    private Marker mMarkerA;
    private Marker mMarkerB;

    private LatLng currentLocation;

    private View mWindow;
    private ClusterManager<MyItem> mClusterManager;


    public GoogleMap mMap;

    private Marker mSydney;
    private Marker activeMarker;

    protected void route_to_location(LatLng dest)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DNEPR, 11));
        mMap.setOnMarkerDragListener(this);

        LatLng origin = new LatLng(latitude, longitude);
        mMarkerA = mMap.addMarker(new MarkerOptions().position(origin).draggable(true));
        mMarkerB = mMap.addMarker(new MarkerOptions().position(dest).draggable(true));
        //mPolyline = mMap.addPolyline(new PolylineOptions().geodesic(true));

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask(mMap);

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

        Toast.makeText(this, "Drag the markers!", Toast.LENGTH_LONG).show();
        showDistance();
        //updatePolyline();
    }

    private void showDistance() {
        double distance = SphericalUtil.computeDistanceBetween(mMarkerA.getPosition(), mMarkerB.getPosition());
        //mTextView.setText("The markers are " + formatNumber(distance) + " apart.");
        Toast.makeText(this, "The distance are " + formatNumber(distance) + " apart.", Toast.LENGTH_LONG).show();
    }

    private String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
    }


    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements InfoWindowAdapter {

        //private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            //mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }

        //@Override
        //public View getInfoContents(Marker marker) {

            //render(marker, mContents);
            //return mContents;
        //}

        private void render(Marker marker, View view) {
            int badge;
            // Use the equals() method on a Marker to check for equals.  Do not use ==.

            if (marker.equals(mSydney)) {
                badge = R.drawable.map_route_button;
            } else {
                // Passing 0 to setImageResource will clear the image view.
                badge = 0;
            }

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if(snippet != null) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, snippet.length(), 0);
                snippetUi.setText(snippetText);
            }
        }
    }

    /**
     * Keeps track of the last selected marker (though it may no longer be selected).  This is
     * useful for refreshing the info window.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Now get user location
        getCurrentLocation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Add lots of markers to the map.
        addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnInfoWindowCloseListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        mMap.setContentDescription("Map with lots of markers.");

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(currentLocation)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                mMap.getUiSettings().setZoomControlsEnabled(true);

                moveMap(new LatLng(latitude, longitude));
            }
        });



        //initialize marker clustering
        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        mClusterManager.setRenderer(new OwnIconRendered(this.getApplicationContext(), mMap, mClusterManager));
        mMap.setOnCameraIdleListener(mClusterManager);

        try {
            readItems();
        } catch (JSONException e) {
            Log.e("SLAVA", e.toString());
            Toast.makeText(this, "Problem reading list of markers.", Toast.LENGTH_LONG).show();
        }

    }

    //Getting current location
    private void getCurrentLocation() {
        //mMap.clear();
        GPSTrackingService gps = new GPSTrackingService(GoogleMarkerActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){
            this.latitude = gps.getLatitude();
            this.longitude = gps.getLongitude();

            this.currentLocation = new LatLng(latitude, longitude);
            // \n is for new line
            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    private void moveMap(LatLng latlng) {
        /**
         * Creating the latlng object to store lat, long coordinates
         * adding marker to map
         * move the camera with animation
         */

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }


    private void addMarkersToMap() {
        // Uses a colored icon.

        // Uses a custom icon with the info window popping out of the center of the icon.
        /*mSydney = mMap.addMarker(new MarkerOptions()
                .position(DNEPR)
                .title("Android Developer")
                .snippet("Sviatoslav Poliakov")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                .infoWindowAnchor(3.2f, 1f)
        );*/

        // Vector drawable resource as a marker icon.
        // Creates a marker rainbow demonstrating how to create default marker icons of different
        // hues (colors).

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getApplicationContext(), "Click on route button!", Toast.LENGTH_SHORT).show();
        route_to_location(activeMarker.getPosition());
        marker.hideInfoWindow();
    }



    /**
     * Demonstrates converting a {@link Drawable} to a {@link BitmapDescriptor},
     * for use as a marker icon.
     */
    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /** Called when the Clear button is clicked. */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        mMap.clear();
    }

    /** Called when the Reset button is clicked. */
    public void onResetMap(View view) {
        if (!checkReady()) {
            return;
        }
        // Clear the map because we don't want duplicates of the markers.
        mMap.clear();
        addMarkersToMap();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        activeMarker = marker;
        // Markers have a z-index that is settable and gettable.
        float zIndex = marker.getZIndex() + 1.0f;
        marker.setZIndex(zIndex);
        Toast.makeText(this, marker.getPosition() + " is position ",
                Toast.LENGTH_SHORT).show();

        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).

        return false;
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        Toast.makeText(this, "Close Info Window", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Toast.makeText(this, "Info Window long click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Toast.makeText(this, "onMarkerDragStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Toast.makeText(this, "onMarkerDragEnd", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Toast.makeText(this, "onMarkerDrag.  Current Position: " + marker.getPosition(), Toast.LENGTH_SHORT).show();
    }

    /**
     * A class to parse the Google Places in JSON format
     */


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private void readItems() throws JSONException {
        InputStream inputStream = getResources().openRawResource(R.raw.radar_search);
        List<MyItem> items = new MyItemReader().read(inputStream);
        mClusterManager.addItems(items);
    }

}
