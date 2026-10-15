package com.travelmate.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelmate.dto.itinerary.ActivityResponse;
import com.travelmate.dto.itinerary.BudgetBreakdownResponse;
import com.travelmate.dto.itinerary.DayResponse;
import com.travelmate.dto.itinerary.ItineraryResponse;
import com.travelmate.dto.itinerary.ModifyItineraryRequest;
import com.travelmate.entity.Itinerary;
import com.travelmate.entity.Trip;
import com.travelmate.service.BudgetCalculationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service("aiItineraryProvider")
@Primary
public class AiItineraryProvider implements ItineraryGenerationService {

    private static final Logger log = LoggerFactory.getLogger(AiItineraryProvider.class);

    private final RuleBasedItineraryProvider fallbackProvider;
    private final BudgetCalculationService budgetCalculationService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.ai.provider:RULE_BASED}")
    private String aiProvider;

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.model:gemini-1.5-flash}")
    private String model;

    @Value("${app.ai.base-url:}")
    private String baseUrl;

    public AiItineraryProvider(
            RuleBasedItineraryProvider fallbackProvider,
            BudgetCalculationService budgetCalculationService,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.fallbackProvider = fallbackProvider;
        this.budgetCalculationService = budgetCalculationService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public ItineraryResponse generateItinerary(Trip trip) {
        // If no API key is configured or provider is set to RULE_BASED, immediately use reliable fallback
        if (apiKey == null || apiKey.trim().isEmpty() || "RULE_BASED".equalsIgnoreCase(aiProvider)) {
            log.info("AI Provider key not configured or RULE_BASED selected. Using rule-based fallback generator.");
            return fallbackProvider.generateItinerary(trip);
        }

        try {
            if ("GEMINI".equalsIgnoreCase(aiProvider)) {
                return callGeminiApi(trip, null);
            } else if ("OPENAI".equalsIgnoreCase(aiProvider)) {
                return callOpenAiApi(trip, null);
            } else {
                return fallbackProvider.generateItinerary(trip);
            }
        } catch (Exception ex) {
            log.warn("AI generation encountered an error: {}. Gracefully falling back to rule-based itinerary.", ex.getMessage());
            ItineraryResponse fallback = fallbackProvider.generateItinerary(trip);
            fallback.setSummary(fallback.getSummary() + " (Note: AI service unavailable; rendered high-fidelity fallback).");
            return fallback;
        }
    }

    @Override
    public ItineraryResponse modifyItinerary(Trip trip, Itinerary existingItinerary, ModifyItineraryRequest request) {
        if (apiKey == null || apiKey.trim().isEmpty() || "RULE_BASED".equalsIgnoreCase(aiProvider)) {
            return fallbackProvider.modifyItinerary(trip, existingItinerary, request);
        }

        try {
            if ("GEMINI".equalsIgnoreCase(aiProvider)) {
                return callGeminiApi(trip, request.getInstruction());
            } else if ("OPENAI".equalsIgnoreCase(aiProvider)) {
                return callOpenAiApi(trip, request.getInstruction());
            } else {
                return fallbackProvider.modifyItinerary(trip, existingItinerary, request);
            }
        } catch (Exception ex) {
            log.warn("AI modification failed: {}. Falling back to rule-based modification.", ex.getMessage());
            return fallbackProvider.modifyItinerary(trip, existingItinerary, request);
        }
    }

    private ItineraryResponse callGeminiApi(Trip trip, String modificationPrompt) throws Exception {
        String prompt = buildPrompt(trip, modificationPrompt);
        String endpoint = (baseUrl != null && !baseUrl.isBlank())
                ? baseUrl
                : "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Gemini API returned status " + response.getStatusCode());
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        String text = root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText();

        return parseAiJsonResponse(text, trip, "AI_GENERATED");
    }

    private ItineraryResponse callOpenAiApi(Trip trip, String modificationPrompt) throws Exception {
        String prompt = buildPrompt(trip, modificationPrompt);
        String endpoint = (baseUrl != null && !baseUrl.isBlank())
                ? baseUrl
                : "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", "You are an expert travel planner. Return strictly valid JSON with no markdown backticks or commentary."
        );
        Map<String, Object> userMessage = Map.of("role", "user", "content", prompt);

        Map<String, Object> requestBody = Map.of(
                "model", model.startsWith("gpt") ? model : "gpt-4o-mini",
                "messages", List.of(systemMessage, userMessage),
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("OpenAI API returned status " + response.getStatusCode());
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        String text = root.path("choices").path(0).path("message").path("content").asText();

        return parseAiJsonResponse(text, trip, "AI_GENERATED");
    }

    private String buildPrompt(Trip trip, String modificationPrompt) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert travel assistant. Generate a realistic day-wise travel plan in STRICT JSON format with NO markdown formatting, NO triple backticks.\n");
        sb.append("Destination: ").append(trip.getDestination()).append("\n");
        sb.append("Days: ").append(trip.getDurationDays()).append("\n");
        sb.append("Start Date: ").append(trip.getStartDate()).append("\n");
        sb.append("Travel Type: ").append(trip.getTravelType()).append(" with ").append(trip.getTravellerCount()).append(" travellers.\n");
        sb.append("Budget: ").append(trip.getBudgetAmount()).append(" (").append(trip.getBudgetMode()).append(")\n");
        sb.append("Interests: ").append(trip.getInterests()).append("\n");
        sb.append("Pace: ").append(trip.getTravelPace()).append("\n");
        sb.append("Accommodation: ").append(trip.getAccommodationPref()).append("\n");
        sb.append("Food: ").append(trip.getFoodPref()).append("\n");
        if (trip.getAdditionalNotes() != null && !trip.getAdditionalNotes().isBlank()) {
            sb.append("User Notes: ").append(trip.getAdditionalNotes()).append("\n");
        }
        if (modificationPrompt != null && !modificationPrompt.isBlank()) {
            sb.append("User Modification Request: ").append(modificationPrompt).append("\n");
        }

        sb.append("\nJSON Schema required:\n");
        sb.append("{\n");
        sb.append("  \"title\": \"Title string\",\n");
        sb.append("  \"summary\": \"Summary paragraph\",\n");
        sb.append("  \"days\": [\n");
        sb.append("    {\n");
        sb.append("      \"dayNumber\": 1,\n");
        sb.append("      \"theme\": \"Day Theme\",\n");
        sb.append("      \"activities\": [\n");
        sb.append("        {\n");
        sb.append("          \"sequenceNumber\": 1,\n");
        sb.append("          \"placeName\": \"Attraction or Place Name\",\n");
        sb.append("          \"category\": \"SIGHTSEEING|DINING|ADVENTURE|CULTURE|RELAXATION|TRANSIT|SHOPPING\",\n");
        sb.append("          \"startTime\": \"09:00 AM\",\n");
        sb.append("          \"endTime\": \"11:30 AM\",\n");
        sb.append("          \"durationMinutes\": 150,\n");
        sb.append("          \"estimatedCost\": 25.00,\n");
        sb.append("          \"description\": \"Detailed description of activity\"\n");
        sb.append("        }\n");
        sb.append("      ]\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}\n");

        return sb.toString();
    }

    private ItineraryResponse parseAiJsonResponse(String rawResponse, Trip trip, String source) throws Exception {
        String cleanJson = rawResponse.trim();
        if (cleanJson.startsWith("```json")) {
            cleanJson = cleanJson.substring(7);
        } else if (cleanJson.startsWith("```")) {
            cleanJson = cleanJson.substring(3);
        }
        if (cleanJson.endsWith("```")) {
            cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
        }
        cleanJson = cleanJson.trim();

        JsonNode root = objectMapper.readTree(cleanJson);
        String title = root.path("title").asText(trip.getDurationDays() + "-Day Trip to " + trip.getDestination());
        String summary = root.path("summary").asText("AI-crafted personalized itinerary.");

        List<DayResponse> days = new ArrayList<>();
        BigDecimal totalActivitiesCost = BigDecimal.ZERO;
        LocalDate startDate = trip.getStartDate() != null ? trip.getStartDate() : LocalDate.now().plusDays(7);

        JsonNode daysNode = root.path("days");
        if (daysNode.isArray()) {
            for (JsonNode dNode : daysNode) {
                int dayNumber = dNode.path("dayNumber").asInt(days.size() + 1);
                String theme = dNode.path("theme").asText("Day " + dayNumber + " Exploration");
                LocalDate dayDate = startDate.plusDays(dayNumber - 1);

                List<ActivityResponse> activities = new ArrayList<>();
                BigDecimal dayCost = BigDecimal.ZERO;

                JsonNode actsNode = dNode.path("activities");
                if (actsNode.isArray()) {
                    int seq = 1;
                    for (JsonNode aNode : actsNode) {
                        BigDecimal actCost = BigDecimal.valueOf(aNode.path("estimatedCost").asDouble(0.0));
                        dayCost = dayCost.add(actCost);

                        activities.add(ActivityResponse.builder()
                                .sequenceNumber(aNode.path("sequenceNumber").asInt(seq++))
                                .placeName(aNode.path("placeName").asText("Place in " + trip.getDestination()))
                                .category(aNode.path("category").asText("SIGHTSEEING"))
                                .startTime(aNode.path("startTime").asText("10:00 AM"))
                                .endTime(aNode.path("endTime").asText("12:00 PM"))
                                .durationMinutes(aNode.path("durationMinutes").asInt(120))
                                .estimatedCost(actCost)
                                .costType("ESTIMATED")
                                .description(aNode.path("description").asText("Explore local sights."))
                                .build());
                    }
                }

                totalActivitiesCost = totalActivitiesCost.add(dayCost);

                days.add(DayResponse.builder()
                        .dayNumber(dayNumber)
                        .date(dayDate)
                        .theme(theme)
                        .dailyBudget(BigDecimal.ZERO)
                        .dailyEstimatedCost(dayCost)
                        .activities(activities)
                        .build());
            }
        }

        BudgetBreakdownResponse budgetBreakdown = budgetCalculationService.calculateBudgetBreakdown(trip, totalActivitiesCost);

        return ItineraryResponse.builder()
                .tripId(trip.getId())
                .destination(trip.getDestination())
                .title(title)
                .summary(summary)
                .generationSource(source)
                .totalEstimatedCost(budgetBreakdown.getTotalEstimatedCost())
                .remainingBudget(budgetBreakdown.getRemainingBudget())
                .budgetStatus(budgetBreakdown.getBudgetStatus())
                .days(days)
                .budgetBreakdown(budgetBreakdown)
                .build();
    }
}
