package com.travelmate.dto.itinerary;

import java.math.BigDecimal;

public class ActivityResponse {
    private Long id;
    private String placeName;
    private String category;
    private String description;
    private String startTime;
    private String endTime;
    private Integer durationMinutes = 60;
    private BigDecimal estimatedCost = BigDecimal.ZERO;
    private String costType = "ESTIMATED";
    private String placeReference;
    private String photoUrl;
    private String address;
    private Double rating;
    private String openingHours;
    private Double latitude;
    private Double longitude;
    private Integer sequenceNumber = 1;

    public ActivityResponse() {}

    public ActivityResponse(Long id, String placeName, String category, String description,
                            String startTime, String endTime, Integer durationMinutes, BigDecimal estimatedCost,
                            String costType, String placeReference, String photoUrl, String address, Double rating,
                            String openingHours, Double latitude, Double longitude, Integer sequenceNumber) {
        this.id = id;
        this.placeName = placeName;
        this.category = category;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes != null ? durationMinutes : 60;
        this.estimatedCost = estimatedCost != null ? estimatedCost : BigDecimal.ZERO;
        this.costType = costType != null ? costType : "ESTIMATED";
        this.placeReference = placeReference;
        this.photoUrl = photoUrl;
        this.address = address;
        this.rating = rating;
        this.openingHours = openingHours;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sequenceNumber = sequenceNumber != null ? sequenceNumber : 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String placeName;
        private String category;
        private String description;
        private String startTime;
        private String endTime;
        private Integer durationMinutes = 60;
        private BigDecimal estimatedCost = BigDecimal.ZERO;
        private String costType = "ESTIMATED";
        private String placeReference;
        private String photoUrl;
        private String address;
        private Double rating;
        private String openingHours;
        private Double latitude;
        private Double longitude;
        private Integer sequenceNumber = 1;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder placeName(String placeName) { this.placeName = placeName; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder startTime(String startTime) { this.startTime = startTime; return this; }
        public Builder endTime(String endTime) { this.endTime = endTime; return this; }
        public Builder durationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; return this; }
        public Builder estimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; return this; }
        public Builder costType(String costType) { this.costType = costType; return this; }
        public Builder placeReference(String placeReference) { this.placeReference = placeReference; return this; }
        public Builder photoUrl(String photoUrl) { this.photoUrl = photoUrl; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder rating(Double rating) { this.rating = rating; return this; }
        public Builder openingHours(String openingHours) { this.openingHours = openingHours; return this; }
        public Builder latitude(Double latitude) { this.latitude = latitude; return this; }
        public Builder longitude(Double longitude) { this.longitude = longitude; return this; }
        public Builder sequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; return this; }
        public ActivityResponse build() {
            return new ActivityResponse(id, placeName, category, description, startTime, endTime,
                    durationMinutes, estimatedCost, costType, placeReference, photoUrl, address, rating,
                    openingHours, latitude, longitude, sequenceNumber);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
    public String getCostType() { return costType; }
    public void setCostType(String costType) { this.costType = costType; }
    public String getPlaceReference() { return placeReference; }
    public void setPlaceReference(String placeReference) { this.placeReference = placeReference; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }
}
