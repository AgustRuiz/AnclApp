package es.agustruiz.anclapp.event;

public class Listener {
    protected String type;
    protected IEventHandler handler;

    public Listener(String type, IEventHandler handler){
        this.type = type;
        this.handler = handler;
    }

    public String getType(){
        return type;
    }

    public IEventHandler getHandler(){
        return handler;
    }
}
