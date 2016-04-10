package es.agustruiz.anclapp.event;

public interface IEventDispatcher {
    void addEventListener(String type, IEventHandler handler);
    void removeEventListener(String type);
    void dispatchEvent(Event event);
    boolean hasEventListener(String type);
    void removeAllListeners();
}
