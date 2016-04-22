package es.agustruiz.anclapp.model;

public class AnchorColor {

    protected String mEntry;
    protected String mEntryValue;
    protected boolean mChecked = false;

    public AnchorColor(String entry, String value, boolean checked){
        mEntry = entry;
        mEntryValue = value;
        mChecked = checked;
    }

    public String getEntry() {
        return mEntry;
    }

    public void setEntry(String entry) {
        mEntry = entry;
    }

    public String getEntryValue() {
        return mEntryValue;
    }

    public void setEntryValue(String entryValue) {
        mEntryValue = entryValue;
    }

    public void setChecked(boolean checked){
        mChecked = checked;
    }

    public boolean isChecked(){
        return mChecked;
    }
}
