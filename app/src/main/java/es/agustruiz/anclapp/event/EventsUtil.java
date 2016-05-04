package es.agustruiz.anclapp.event;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class EventsUtil extends EventDispatcher {

    public static final String LOG_TAG = EventsUtil.class.getName()+"[A]";
    private static EventsUtil ourInstance = new EventsUtil();

    public static final String CENTER_MAP_ON_CURRENT_LOCATION = "centerMapOnCurrentLocation";
    public static final String ADD_NEW_ANCHOR_ON_MAP = "addNewAnchorOnMap";
    public static final String GET_NEW_MARKER_DATA = "getNewMarkerData";
    public static final String MAP_CANCEL_NEW_MARKER = "mapCancelNewMarker";
    public static final String REFRESH_ANCHOR_MARKERS = "refreshAnchorMarkers";
    public static final String DISMISS_FAB_CENTER_MAP = "dismissFabCenterMap";
    public static final String HIDE_LOCATION_CARD = "hideLocationCard";
    public static final String NOTIFIY_CURRENT_LOCATION_CHANGED = "notifyCurrentLocationChanged";

    //region [Singleton constructor]

    public static EventsUtil getInstance() {
        return ourInstance;
    }

    //endregion

    //region [Public events]

    public void centerMapOnCurrentLocationEvent(boolean state){
        dispatchEvent(new Event(CENTER_MAP_ON_CURRENT_LOCATION, state));
    }

    public void addNewAnchorOnMap(){
        dispatchEvent(new Event(ADD_NEW_ANCHOR_ON_MAP));
    }

    public void getNewMarkerData(String[] data){
        dispatchEvent(new Event(GET_NEW_MARKER_DATA, data));
    }

    public void mapCancelNewMarker(){
        dispatchEvent(new Event(MAP_CANCEL_NEW_MARKER));
    }

    public void refreshAnchorMarkers(){
        dispatchEvent(new Event(REFRESH_ANCHOR_MARKERS));
    }

    public void dismissFabCenterMap(){
        dispatchEvent(new Event(DISMISS_FAB_CENTER_MAP));
    }

    public void hideLocationCard(){
        dispatchEvent(new Event(HIDE_LOCATION_CARD));
    }

    public void notifyCurrentLocationChanged(Location location){
        dispatchEvent(new Event(NOTIFIY_CURRENT_LOCATION_CHANGED, location));
    }

    //endregion
}
