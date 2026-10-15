package com.travelmate.service.ai;

import com.travelmate.dto.itinerary.ItineraryResponse;
import com.travelmate.dto.itinerary.ModifyItineraryRequest;
import com.travelmate.entity.Itinerary;
import com.travelmate.entity.Trip;

public interface ItineraryGenerationService {
    ItineraryResponse generateItinerary(Trip trip);
    ItineraryResponse modifyItinerary(Trip trip, Itinerary existingItinerary, ModifyItineraryRequest request);
}
