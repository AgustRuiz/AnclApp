package es.agustruiz.anclapp.event;

public class Event {

    protected String type;
    protected Object parameter;

    //region [Constructor]

    public Event(String type){
        initProperties(type, null);
    }

    public Event(String type, Object parameters){
        initProperties(type, parameters);
    }

    //endregion

    //region [Protected methods]

    protected void initProperties(String type, Object parameter){
        this.type = type;
        this.parameter = parameter;
    }

    //endregion

    //region [Public methods]

    public String getType(){
        return type;
    }

    public Object getParameter(){
        return parameter;
    }

    //endregion
}
