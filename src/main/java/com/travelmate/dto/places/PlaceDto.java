package com.travelmate.dto.places;

import java.math.BigDecimal;

public class PlaceDto {
    private String name;
    private String displayName;
    private String category;
    private String type;
    private Double latitude;
    private Double longitude;
    private String address;
    private Double importance;
    private String photoUrl;
    private Double rating;
    private Integer reviewCount;
    private String openingHours;
    private String placeId;
    private String visitDuration;
    private BigDecimal estimatedCost;

    public PlaceDto() {}

    public PlaceDto(String name, String displayName, String category, String type,
                    Double latitude, Double longitude, String address, Double importance) {
        this(name, displayName, category, type, latitude, longitude, address, importance,
                null, 4.5, 120, "09:00 AM - 06:00 PM", null, "2 hours", BigDecimal.ZERO);
    }

    public PlaceDto(String name, String displayName, String category, String type,
                    Double latitude, Double longitude, String address, Double importance,
                    String photoUrl, Double rating, Integer reviewCount, String openingHours,
                    String placeId, String visitDuration, BigDecimal estimatedCost) {
        this.name = name;
        this.displayName = displayName;
        this.category = category;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.importance = importance;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.openingHours = openingHours;
        this.placeId = placeId;
        this.visitDuration = visitDuration;
        this.estimatedCost = estimatedCost;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String displayName;
        private String category;
        private String type;
        private Double latitude;
        private Double longitude;
        private String address;
        private Double importance;
        private String photoUrl;
        private Double rating;
        private Integer reviewCount;
        private String openingHours;
        private String placeId;
        private String visitDuration;
        private BigDecimal estimatedCost;

        public Builder name(String name) { this.name = name; return this; }
        public Builder displayName(String displayName) { this.displayName = displayName; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder latitude(Double latitude) { this.latitude = latitude; return this; }
        public Builder longitude(Double longitude) { this.longitude = longitude; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder importance(Double importance) { this.importance = importance; return this; }
        public Builder photoUrl(String photoUrl) { this.photoUrl = photoUrl; return this; }
        public Builder rating(Double rating) { this.rating = rating; return this; }
        public Builder reviewCount(Integer reviewCount) { this.reviewCount = reviewCount; return this; }
        public Builder openingHours(String openingHours) { this.openingHours = openingHours; return this; }
        public Builder placeId(String placeId) { this.placeId = placeId; return this; }
        public Builder visitDuration(String visitDuration) { this.visitDuration = visitDuration; return this; }
        public Builder estimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; return this; }

        public PlaceDto build() {
            return new PlaceDto(name, displayName, category, type, latitude, longitude, address, importance,
                    photoUrl, rating, reviewCount, openingHours, placeId, visitDuration, estimatedCost);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getImportance() { return importance; }
    public void setImportance(Double importance) { this.importance = importance; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }
    public String getVisitDuration() { return visitDuration; }
    public void setVisitDuration(String visitDuration) { this.visitDuration = visitDuration; }
    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }
}
