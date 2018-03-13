package com.example.cowboy.filemodule.maps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Cowboy on 06.03.2018.
 */

public interface IRouteReady {
    void showRoute(GoogleMap mMap, List<LatLng> routePoints);
}
