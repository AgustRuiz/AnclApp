package es.agustruiz.anclapp.event;

public class EventsUtil extends EventDispatcher {

    public static final String LOG_TAG = EventsUtil.class.getName()+"[A]";
    public static final String FAB_CENTER_MAP = "fabCenterMapEvent";
    private static EventsUtil ourInstance = new EventsUtil();

    //region [Singleton constructor]

    public static EventsUtil getInstance() {
        return ourInstance;
    }

    //private EventsUtil() {}

    //endregion

    //region [Public events]

    public void centerMapOnLocationEvent(){
        dispatchEvent(new Event(FAB_CENTER_MAP));
    }

    //endregion
}
