package com.travelmate.dto.trip;

import com.travelmate.dto.itinerary.ItineraryResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TripResponse {
    private Long id;
    private Long userId;
    private String destination;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private String destinationPlaceId;
    private LocalDate startDate;
    private Integer durationDays;
    private String travelType;
    private Integer travellerCount;
    private BigDecimal budgetAmount;
    private String budgetMode;
    private String interests;
    private String travelPace;
    private String accommodationPref;
    private String foodPref;
    private String additionalNotes;
    private ItineraryResponse itinerary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TripResponse() {}

    public TripResponse(Long id, Long userId, String destination, Double destinationLatitude, Double destinationLongitude,
                        String destinationPlaceId, LocalDate startDate, Integer durationDays, String travelType,
                        Integer travellerCount, BigDecimal budgetAmount, String budgetMode, String interests,
                        String travelPace, String accommodationPref, String foodPref, String additionalNotes,
                        ItineraryResponse itinerary, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.destination = destination;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
        this.destinationPlaceId = destinationPlaceId;
        this.startDate = startDate;
        this.durationDays = durationDays;
        this.travelType = travelType;
        this.travellerCount = travellerCount;
        this.budgetAmount = budgetAmount;
        this.budgetMode = budgetMode;
        this.interests = interests;
        this.travelPace = travelPace;
        this.accommodationPref = accommodationPref;
        this.foodPref = foodPref;
        this.additionalNotes = additionalNotes;
        this.itinerary = itinerary;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private String destination;
        private Double destinationLatitude;
        private Double destinationLongitude;
        private String destinationPlaceId;
        private LocalDate startDate;
        private Integer durationDays;
        private String travelType;
        private Integer travellerCount;
        private BigDecimal budgetAmount;
        private String budgetMode;
        private String interests;
        private String travelPace;
        private String accommodationPref;
        private String foodPref;
        private String additionalNotes;
        private ItineraryResponse itinerary;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder destination(String destination) { this.destination = destination; return this; }
        public Builder destinationLatitude(Double destinationLatitude) { this.destinationLatitude = destinationLatitude; return this; }
        public Builder destinationLongitude(Double destinationLongitude) { this.destinationLongitude = destinationLongitude; return this; }
        public Builder destinationPlaceId(String destinationPlaceId) { this.destinationPlaceId = destinationPlaceId; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder durationDays(Integer durationDays) { this.durationDays = durationDays; return this; }
        public Builder travelType(String travelType) { this.travelType = travelType; return this; }
        public Builder travellerCount(Integer travellerCount) { this.travellerCount = travellerCount; return this; }
        public Builder budgetAmount(BigDecimal budgetAmount) { this.budgetAmount = budgetAmount; return this; }
        public Builder budgetMode(String budgetMode) { this.budgetMode = budgetMode; return this; }
        public Builder interests(String interests) { this.interests = interests; return this; }
        public Builder travelPace(String travelPace) { this.travelPace = travelPace; return this; }
        public Builder accommodationPref(String accommodationPref) { this.accommodationPref = accommodationPref; return this; }
        public Builder foodPref(String foodPref) { this.foodPref = foodPref; return this; }
        public Builder additionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; return this; }
        public Builder itinerary(ItineraryResponse itinerary) { this.itinerary = itinerary; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public TripResponse build() {
            return new TripResponse(id, userId, destination, destinationLatitude, destinationLongitude, destinationPlaceId,
                    startDate, durationDays, travelType, travellerCount, budgetAmount, budgetMode, interests,
                    travelPace, accommodationPref, foodPref, additionalNotes, itinerary, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Double getDestinationLatitude() { return destinationLatitude; }
    public void setDestinationLatitude(Double destinationLatitude) { this.destinationLatitude = destinationLatitude; }
    public Double getDestinationLongitude() { return destinationLongitude; }
    public void setDestinationLongitude(Double destinationLongitude) { this.destinationLongitude = destinationLongitude; }
    public String getDestinationPlaceId() { return destinationPlaceId; }
    public void setDestinationPlaceId(String destinationPlaceId) { this.destinationPlaceId = destinationPlaceId; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }
    public String getTravelType() { return travelType; }
    public void setTravelType(String travelType) { this.travelType = travelType; }
    public Integer getTravellerCount() { return travellerCount; }
    public void setTravellerCount(Integer travellerCount) { this.travellerCount = travellerCount; }
    public BigDecimal getBudgetAmount() { return budgetAmount; }
    public void setBudgetAmount(BigDecimal budgetAmount) { this.budgetAmount = budgetAmount; }
    public String getBudgetMode() { return budgetMode; }
    public void setBudgetMode(String budgetMode) { this.budgetMode = budgetMode; }
    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }
    public String getTravelPace() { return travelPace; }
    public void setTravelPace(String travelPace) { this.travelPace = travelPace; }
    public String getAccommodationPref() { return accommodationPref; }
    public void setAccommodationPref(String accommodationPref) { this.accommodationPref = accommodationPref; }
    public String getFoodPref() { return foodPref; }
    public void setFoodPref(String foodPref) { this.foodPref = foodPref; }
    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
    public ItineraryResponse getItinerary() { return itinerary; }
    public void setItinerary(ItineraryResponse itinerary) { this.itinerary = itinerary; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
