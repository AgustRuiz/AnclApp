package es.agustruiz.anclapp.event;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

public class EventDispatcher implements IEventDispatcher {

    public static final String LOG_TAG = EventDispatcher.class.getName() + "[A]";
    protected final ArrayList<Listener> listenerList = new ArrayList<>();

    //region [IEventDispatcher overriden methods]

    @Override
    public void addEventListener(String type, IEventHandler handler) {
        Log.d(LOG_TAG, "add listener " + type);
        synchronized (listenerList) {
            Listener listener = new Listener(type, handler);
            removeEventListener(type);
            listenerList.add(0, listener);
        }
    }

    @Override
    public void removeEventListener(String type) {
        Log.d(LOG_TAG, "remove listener " + type);
        // TODO handle concurrent modification exception. See this: http://stackoverflow.com/questions/6621991/how-to-handle-concurrentmodificationexception-in-android
        synchronized (listenerList) {
            for (Listener listener : listenerList) {
                if (listener.getType().equals(type)) {
                    listenerList.remove(listener);
                }
            }
        }
    }

    @Override
    public void dispatchEvent(Event event) {
        Log.d(LOG_TAG, "dispatch listener " + event.getType());
        synchronized (listenerList) {
            for (Listener listener : listenerList) {
                if (event.getType().equals(listener.getType())) {
                    IEventHandler eventHandler = listener.getHandler();
                    eventHandler.callback(event);
                }
            }
        }
    }

    @Override
    public boolean hasEventListener(String type) {
        Log.d(LOG_TAG, "has listener " + type);
        synchronized (listenerList) {
            for (Listener listener : listenerList) {
                if (listener.getType().equals(type))
                    return true;
            }
            return false;
        }
    }

    @Override
    public void removeAllListeners() {
        Log.d(LOG_TAG, "remove all listeners");
        synchronized (listenerList) {
            listenerList.clear();
        }
    }

    //endregion

}
