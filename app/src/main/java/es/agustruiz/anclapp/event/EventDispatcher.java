package es.agustruiz.anclapp.event;

import java.util.ArrayList;
import java.util.Iterator;

public class EventDispatcher implements IEventDispatcher {

    protected ArrayList<Listener> listenerList = new ArrayList<>();

    //region [IEventDispatcher overriden methods]

    @Override
    public void addEventListener(String type, IEventHandler handler) {
        Listener listener = new Listener(type, handler);
        removeEventListener(type);
        listenerList.add(0, listener);
    }

    @Override
    public void removeEventListener(String type) {
        // TODO handle concurrent modification exception. See this: http://stackoverflow.com/questions/6621991/how-to-handle-concurrentmodificationexception-in-android
        for (Listener listener : listenerList) {
            if (listener.getType().equals(type)) {
                listenerList.remove(listener);
            }
        }
    }

    @Override
    public void dispatchEvent(Event event) {
        for (Listener listener : listenerList) {
            if (event.getType().equals(listener.getType())) {
                IEventHandler eventHandler = listener.getHandler();
                eventHandler.callback(event);
            }
        }
    }

    @Override
    public boolean hasEventListener(String type) {
        for (Listener listener : listenerList) {
            if (listener.getType().equals(type))
                return true;
        }
        return false;
    }

    @Override
    public void removeAllListeners() {
        listenerList.clear();
    }

    //endregion

}
