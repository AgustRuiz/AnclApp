package es.agustruiz.anclapp.event;

import com.google.android.gms.maps.model.LatLng;

public class EventsUtil extends EventDispatcher {

    public static final String LOG_TAG = EventsUtil.class.getName()+"[A]";
    public static final String FAB_CENTER_MAP = "fabCenterMapEvent";
    public static final String MAP_LONG_PRESS = "mapLongPress";
    private static EventsUtil ourInstance = new EventsUtil();

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

    public void mapLongPress(LatLng latLng){
        dispatchEvent(new Event(MAP_LONG_PRESS, latLng));
    }

    //endregion
}
