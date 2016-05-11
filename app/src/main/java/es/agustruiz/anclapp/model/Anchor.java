package es.agustruiz.anclapp.model;

import android.graphics.Color;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.Comparator;
import java.util.Date;

public class Anchor {

    Long id;
    Double latitude;
    Double longitude;
    String title;
    String description;
    String color;
    Boolean reminder;
    Boolean isDeleted;
    Long deletedTimestam;

    static Location referenceLocation = null;

    //region [Public Methods]

    /**
     * Default constructor
     */
    public Anchor() {
    }


    /**
     * Full constructor
     */
    public Anchor(Long id, Double latitude, Double longitude, String title, String description, String color, Boolean reminder, Boolean isDeleted, Long deletedTimestam) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        this.color = color;
        this.reminder = reminder;
        this.isDeleted = isDeleted;
        this.deletedTimestam = deletedTimestam;
    }

    /**
     * Id getter
     *
     * @return Anchor id
     */
    public Long getId() {
        return id;
    }

    /**
     * Id setter
     *
     * @param id Anchor id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Latitude getter
     *
     * @return Anchor latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Latitude setter
     *
     * @param latitude Anchor latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Longitude getter
     *
     * @return Anchor longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Longitude setter
     *
     * @param longitude Anchor longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Title getter
     *
     * @return Anchor title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Title setter
     *
     * @param title Anchor title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Description getter
     *
     * @return Anchor description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Description setter
     *
     * @param description Anchor description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Color getter
     *
     * @return Anchor color
     */
    public String getColor() {
        return color;
    }

    /**
     * Color setter
     *
     * @param color Anchor color
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Reminder status getter
     *
     * @return true if reminder is activated, false otherwise
     */
    public Boolean isReminder() {
        return reminder;
    }

    /**
     * Reminder setter
     *
     * @param reminder Anchor reminder status
     */
    public void setReminder(Boolean reminder) {
        this.reminder = reminder;
    }

    public Boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deletedStatus) {
        isDeleted = deletedStatus;
    }

    public Long getDeletedTimestam(){
        return deletedTimestam;
    }

    public void setDeletedTimestam(Long deletedTimestam){
        this.deletedTimestam = deletedTimestam;
    }

    /**
     * Check if anchor is ok to save
     *
     * @return
     */
    public boolean isOk() {
        boolean status = true;
        if (title.length() == 0)
            status = false;
        return status;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public Float getDistanceInKms() {
        if (referenceLocation != null) {
            Location location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            return Math.round(referenceLocation.distanceTo(location) / 10f) / 100f;
        } else {
            return -1f;
        }
    }

    static public void setReferenceLocation(Location location) {
        referenceLocation = location;
    }

    //enregion

    //region [Comparator]

    static public class Comparator implements java.util.Comparator<Anchor> {
        @Override
        public int compare(Anchor a, Anchor b) {
            return a.getDistanceInKms() < b.getDistanceInKms() ? -1 : a.getDistanceInKms() > b.getDistanceInKms() ? 1 : 0;
        }
    }

    //endregion

}
