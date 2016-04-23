package es.agustruiz.anclapp.model;

import android.graphics.Color;
import android.preference.PreferenceManager;

public class Anchor {

    Double latitude;
    Double longitude;
    String title;
    String description;
    Color color;
    Boolean reminder;

    //region [Public Methods]

    /**
     * Default constructor
     */
    public Anchor() {
    }


    /**
     * Full constructor
     */
    public Anchor(Double latitude, Double longitude, String title, String description, Color color, Boolean reminder) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        this.color = color;
        this.reminder = reminder;
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
    public Color getColor() {
        return color;
    }

    /**
     * Color setter
     *
     * @param color Anchor color
     */
    public void setColor(Color color) {
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

    //enregion

    //region [Private Methods]


    //enregion

}
