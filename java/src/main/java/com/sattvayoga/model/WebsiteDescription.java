package com.sattvayoga.model;

public class WebsiteDescription {

    private int webDescriptionID;
    private String locationName;
    private String description;

    public WebsiteDescription(int webDescriptionID, String locationName, String description) {
        this.webDescriptionID = webDescriptionID;
        this.locationName = locationName;
        this.description = description;
    }

    public WebsiteDescription() {

    }

    public int getWebDescriptionID() {
        return webDescriptionID;
    }

    public void setWebDescriptionID(int webDescriptionID) {
        this.webDescriptionID = webDescriptionID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
