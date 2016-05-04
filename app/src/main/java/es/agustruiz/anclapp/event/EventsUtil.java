package es.agustruiz.anclapp.event;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class EventsUtil extends EventDispatcher {

    public static final String LOG_TAG = EventsUtil.class.getName()+"[A]";
    private static EventsUtil ourInstance = new EventsUtil();

    public static final String CENTER_MAP_ON_CURRENT_LOCATION = "centerMapOnCurrentLocation";
    public static final String MAP_NEW_MARKER = "mapNewMarker";
    public static final String MAP_CANCEL_NEW_MARKER = "mapCancelNewMarker";
    public static final String CURRENT_LOCATION_CHANGE = "currentLocationChange";
    public static final String GET_MARKER_DETAILS = "setMarkerDetails";
    public static final String REFRESH_ANCHOR_MARKERS = "refreshAnchorMarkers";

    //region [Singleton constructor]

    public static EventsUtil getInstance() {
        return ourInstance;
    }

    //private EventsUtil() {}

    //endregion

    //region [Public events]

    public void centerMapOnCurrentLocationEvent(boolean state){
        dispatchEvent(new Event(CENTER_MAP_ON_CURRENT_LOCATION, state));
    }

    public void mapClick(LatLng latLng){
        dispatchEvent(new Event(MAP_NEW_MARKER, latLng));
    }

    public void cancelNewMarker(){
        dispatchEvent(new Event(MAP_CANCEL_NEW_MARKER));
    }

    public void currentLocationChange(Location location){
        dispatchEvent(new Event(CURRENT_LOCATION_CHANGE, location));
    }

    public void setMarkerDetails(Location location){
        dispatchEvent(new Event(GET_MARKER_DETAILS, location));
    }

    public void refreshAnchorMarkers(){
        dispatchEvent(new Event(REFRESH_ANCHOR_MARKERS));
    }

    //endregion
}
