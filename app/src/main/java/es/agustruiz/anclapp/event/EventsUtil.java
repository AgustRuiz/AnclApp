package es.agustruiz.anclapp.event;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class EventsUtil extends EventDispatcher {

    public static final String LOG_TAG = EventsUtil.class.getName()+"[A]";
    private static EventsUtil ourInstance = new EventsUtil();
    public static final String FAB_CENTER_MAP = "fabCenterMapEvent";
    public static final String MAP_CLICK = "mapClick";
    public static final String CANCEL_NEW_MARKER = "cancelNewMarker";
    public static final String CURRENT_LOCATION_CHANGE = "currentLocationChange";
    public static final String GET_MARKER_DETAILS = "setMarkerDetails";

    //region [Singleton constructor]

    public static EventsUtil getInstance() {
        return ourInstance;
    }

    //private EventsUtil() {}

    //endregion

    //region [Public events]

    public void centerMapOnLocationEvent(boolean state){
        dispatchEvent(new Event(FAB_CENTER_MAP, state));
    }

    public void mapClick(LatLng latLng){
        dispatchEvent(new Event(MAP_CLICK, latLng));
    }

    public void cancelNewMarker(){
        dispatchEvent(new Event(CANCEL_NEW_MARKER));
    }

    public void currentLocationChange(Location location){
        dispatchEvent(new Event(CURRENT_LOCATION_CHANGE, location));
    }

    public void setMarkerDetails(Location location){
        dispatchEvent(new Event(GET_MARKER_DETAILS, location));
    }

    //endregion
}
