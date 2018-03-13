package com.example.cowboy.filemodule.maps;

import com.example.cowboy.filemodule.GoogleMarkerActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Cowboy on 06.03.2018.
 */

public class IRouteReadyImpl extends GoogleMarkerActivity implements IRouteReady
{
    @Override
    public void showRoute(GoogleMap mMap, List<LatLng> routePoints) {
        super.setAnimation(mMap, routePoints);
    }
}
