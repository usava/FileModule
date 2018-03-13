package com.example.cowboy.filemodule;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
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
        OnMapReadyCallback
{

    public static final LatLng DNEPR = new LatLng(48.487306, 34.932022);
    public double latitude;
    public double longitude;
    public static List<LatLng> routePoints;

    private Marker mMarkerA;
    private Marker mMarkerB;

    private LatLng currentLocation;

    private View mWindow;
    private ClusterManager<MyItem> mClusterManager;


    public GoogleMap mMap;

    private Marker activeMarker;

    protected void route_to_location(LatLng dest)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DNEPR, 11));
        mMap.setOnMarkerDragListener(this);

        mMap.clear();
        addMarkersToMap();

        LatLng origin = new LatLng(latitude, longitude);

        //mPolyline = mMap.addPolyline(new PolylineOptions().geodesic(true));

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask(mMap);

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

        Toast.makeText(this, "Drag the markers!", Toast.LENGTH_LONG).show();
        showDistance(origin, dest);
    }

    private String showDistance(LatLng origin, LatLng dest) {
        double distance = SphericalUtil.computeDistanceBetween(origin, dest);
        //mTextView.setText("The markers are " + formatNumber(distance) + " apart.");
        Toast.makeText(this, "The distance is " + formatNumber(distance), Toast.LENGTH_LONG).show();

        mMarkerA = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Start point")
                .snippet("The distance is " + formatNumber(distance))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_pin))
                .draggable(false)
        );

        mMarkerB = mMap.addMarker(new MarkerOptions()
                .position(dest)
                .title(activeMarker.getTitle())
                .snippet("The distance is " + formatNumber(distance))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                .draggable(false));

        return "The distance are " + formatNumber(distance) + " apart.";
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

    public Boolean isTargetMarker(){
        for(MyItem item : MyItemReader.items){
            if(item.getPosition().equals(activeMarker.getPosition())){
                return true;
            }
        }
        return false;
    }

    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements InfoWindowAdapter {

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

        private void render(Marker marker, View view)
        {
            if (isTargetMarker()) {
                String title = marker.getTitle();
                if(title != null) {
                    TextView titleUi = ((TextView) view.findViewById(R.id.title));
                    SpannableString titleText = new SpannableString(title);

                    titleText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, titleText.length(), 0);
                    titleUi.setText(titleText);
                }
                String snippet = marker.getSnippet();
                TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
                if(snippet != null) {
                    SpannableString snippetText = new SpannableString(snippet);
                    snippetText.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, snippet.length(), 0);
                    snippetUi.setText(snippetText);
                }
                view.findViewById(R.id.iv_map_route_button).setVisibility(View.VISIBLE);
            }else{
                view.findViewById(R.id.iv_map_route_button).setVisibility(View.GONE);
                String title = marker.getTitle();
                if (title != null) {
                    TextView titleUi = ((TextView) view.findViewById(R.id.title));
                    SpannableString titleText = new SpannableString(title);

                    // Spannable string allows us to edit the formatting of the text.

                    titleText.setSpan(new ForegroundColorSpan(Color.WHITE), 0, titleText.length(), 0);
                    titleUi.setText(titleText);
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
        //getCurrentLocation();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;


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
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                mMap.getUiSettings().setZoomControlsEnabled(true);

                moveMap(new LatLng(latitude, longitude));
            }
        });

        // Add lots of markers to the map.
        addMarkersToMap();

        getCurrentLocation();

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

            mMarkerA = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("Here I am")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_current_location_man))
                    .draggable(false)
            );
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
        if(isTargetMarker()) {
            route_to_location(activeMarker.getPosition());
        }
        marker.hideInfoWindow();
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

    public static void setAnimation(GoogleMap myMap, final List<LatLng> directionPoint) {
        Log.d("map","setanimation");

        Marker marker = myMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car_map))
                .position(directionPoint.get(0))
                .flat(true));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(directionPoint.get(0)));
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(directionPoint.get(0), 12));

        animateMarker(myMap, marker, directionPoint, true);
    }

    private static void animateMarker(final GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                                      final boolean hideMarker) {
        Log.d("map","anoamte markern");
        Log.d("map","directionPoint"+directionPoint);
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis()-1600;
        Projection proj = myMap.getProjection();
        Log.d("map","proj"+proj);
        final long duration = 70000;

        //final Interpolator interpolator = new Interpolator();
        final LinearInterpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {

            int i = 0;

            //Log.d("map","")
            @Override
            public void run() {
                Log.d("map", "run");

                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                Log.d("map", "i and then check" + i);
                if (i < directionPoint.size()) {
                    Log.d("map", "directionPoint.get(i):" + directionPoint.get(i));
                    marker.setPosition(directionPoint.get(i));
                    i++;
                }
                Log.d("map", "i" + i);
                Log.d("map", "tss" + t);


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 100);

                } else {

                    if (hideMarker) {
                        marker.setVisible(false);
                        Log.d("map", "visible false");

                    } else {
                        marker.setVisible(true);
                        Log.d("map", "visible true");

                    }
                }
            }
        });
    }

}
