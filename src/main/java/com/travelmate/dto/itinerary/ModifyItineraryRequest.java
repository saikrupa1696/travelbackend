package com.travelmate.dto.itinerary;

import jakarta.validation.constraints.NotBlank;

public class ModifyItineraryRequest {

    @NotBlank(message = "Modification prompt/instruction is required")
    private String instruction;

    private Integer targetDay;
    private String travelPace;
    private String accommodationPref;
    private String foodPref;

    public ModifyItineraryRequest() {}

    public ModifyItineraryRequest(String instruction, Integer targetDay, String travelPace, String accommodationPref, String foodPref) {
        this.instruction = instruction;
        this.targetDay = targetDay;
        this.travelPace = travelPace;
        this.accommodationPref = accommodationPref;
        this.foodPref = foodPref;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String instruction;
        private Integer targetDay;
        private String travelPace;
        private String accommodationPref;
        private String foodPref;

        public Builder instruction(String instruction) { this.instruction = instruction; return this; }
        public Builder targetDay(Integer targetDay) { this.targetDay = targetDay; return this; }
        public Builder travelPace(String travelPace) { this.travelPace = travelPace; return this; }
        public Builder accommodationPref(String accommodationPref) { this.accommodationPref = accommodationPref; return this; }
        public Builder foodPref(String foodPref) { this.foodPref = foodPref; return this; }
        public ModifyItineraryRequest build() {
            return new ModifyItineraryRequest(instruction, targetDay, travelPace, accommodationPref, foodPref);
        }
    }

    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
    public Integer getTargetDay() { return targetDay; }
    public void setTargetDay(Integer targetDay) { this.targetDay = targetDay; }
    public String getTravelPace() { return travelPace; }
    public void setTravelPace(String travelPace) { this.travelPace = travelPace; }
    public String getAccommodationPref() { return accommodationPref; }
    public void setAccommodationPref(String accommodationPref) { this.accommodationPref = accommodationPref; }
    public String getFoodPref() { return foodPref; }
    public void setFoodPref(String foodPref) { this.foodPref = foodPref; }
}
