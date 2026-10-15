package com.travelmate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelmate.dto.places.PlaceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlacesService {

    private static final Logger log = LoggerFactory.getLogger(PlacesService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String userAgent;

    // In-memory caches to respect open API rate limits
    private final Map<String, List<PlaceDto>> placesCache = new ConcurrentHashMap<>();
    private final Map<String, List<PlaceDto>> geocodeCache = new ConcurrentHashMap<>();

    public PlacesService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${app.places.user-agent:TravelMateAI/1.0}") String userAgent) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.userAgent = userAgent;
    }

    /**
     * Search destination suggestions with structured coordinates, display name, and place id.
     */
    public List<PlaceDto> geocode(String query) {
        if (query == null || query.trim().length() < 2) {
            return Collections.emptyList();
        }

        String normalizedKey = query.trim().toLowerCase();
        if (geocodeCache.containsKey(normalizedKey)) {
            return geocodeCache.get(normalizedKey);
        }

        List<PlaceDto> results = fetchGeocodeFromNominatim(query);
        if (results.isEmpty()) {
            results = getCuratedGeocodeMatches(query);
        }

        geocodeCache.put(normalizedKey, results);
        return results;
    }

    private List<PlaceDto> fetchGeocodeFromNominatim(String query) {
        List<PlaceDto> list = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format("https://nominatim.openstreetmap.org/search?q=%s&format=json&addressdetails=1&limit=5", encodedQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", userAgent);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        String name = node.path("name").asText();
                        String displayName = node.path("display_name").asText();
                        if (name == null || name.isBlank()) {
                            name = displayName.split(",")[0].trim();
                        }

                        JsonNode addrNode = node.path("address");
                        String country = addrNode.path("country").asText("");
                        String state = addrNode.path("state").asText("");
                        String addressSummary = (!state.isBlank() && !country.isBlank())
                                ? state + ", " + country
                                : (!country.isBlank() ? country : displayName);

                        list.add(PlaceDto.builder()
                                .name(name)
                                .displayName(displayName)
                                .placeId(node.path("place_id").asText(String.valueOf(node.path("osm_id").asLong())))
                                .category(node.path("class").asText("boundary"))
                                .type(node.path("type").asText("administrative"))
                                .latitude(node.path("lat").asDouble())
                                .longitude(node.path("lon").asDouble())
                                .address(addressSummary)
                                .importance(node.path("importance").asDouble(0.5))
                                .build());
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Nominatim geocoding lookup failed for query '{}': {}. Falling back to curated catalog.", query, ex.getMessage());
        }
        return list;
    }

    private List<PlaceDto> getCuratedGeocodeMatches(String query) {
        String q = query.toLowerCase().trim();
        List<PlaceDto> allCurated = List.of(
                new PlaceDto("Goa", "Goa, India", "boundary", "state", 15.2993, 74.1240, "Goa, India", 0.95),
                new PlaceDto("Hyderabad", "Hyderabad, Telangana, India", "boundary", "city", 17.3850, 78.4867, "Telangana, India", 0.95),
                new PlaceDto("Bengaluru", "Bengaluru, Karnataka, India", "boundary", "city", 12.9716, 77.5946, "Karnataka, India", 0.95),
                new PlaceDto("Manali", "Manali, Kullu, Himachal Pradesh, India", "boundary", "town", 32.2432, 77.1892, "Himachal Pradesh, India", 0.92),
                new PlaceDto("Jaipur", "Jaipur, Rajasthan, India", "boundary", "city", 26.9124, 75.7873, "Rajasthan, India", 0.95),
                new PlaceDto("Visakhapatnam", "Visakhapatnam, Andhra Pradesh, India", "boundary", "city", 17.6868, 83.2185, "Andhra Pradesh, India", 0.90),
                new PlaceDto("New Delhi", "New Delhi, Delhi, India", "boundary", "capital", 28.6139, 77.2090, "Delhi, India", 0.98),
                new PlaceDto("Mumbai", "Mumbai, Maharashtra, India", "boundary", "city", 19.0760, 72.8777, "Maharashtra, India", 0.98),
                new PlaceDto("Kochi", "Kochi, Ernakulam, Kerala, India", "boundary", "city", 9.9312, 76.2673, "Kerala, India", 0.90),
                new PlaceDto("Paris", "Paris, Île-de-France, France", "boundary", "capital", 48.8566, 2.3522, "France", 0.98),
                new PlaceDto("Tokyo", "Tokyo, Japan", "boundary", "capital", 35.6762, 139.6503, "Japan", 0.98)
        );

        return allCurated.stream()
                .filter(p -> p.getName().toLowerCase().contains(q) || p.getDisplayName().toLowerCase().contains(q))
                .toList();
    }

    /**
     * Search recommended places, attractions, viewpoints, and restaurants for a destination.
     */
    public List<PlaceDto> searchPlaces(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedKey = query.trim().toLowerCase();
        if (placesCache.containsKey(normalizedKey)) {
            return placesCache.get(normalizedKey);
        }

        List<PlaceDto> results = getCuratedFallbackPlaces(query);
        if (results.isEmpty()) {
            results = fetchFromNominatim(query);
        }

        placesCache.put(normalizedKey, results);
        return results;
    }

    private List<PlaceDto> fetchFromNominatim(String query) {
        List<PlaceDto> places = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = String.format("https://nominatim.openstreetmap.org/search?q=%s&format=json&addressdetails=1&limit=8", encodedQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", userAgent);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.isArray()) {
                    for (JsonNode node : root) {
                        String cat = node.path("class").asText("tourism");
                        String photo = getCategoryFallbackPhoto(cat);
                        places.add(PlaceDto.builder()
                                .name(node.path("name").asText(node.path("display_name").asText().split(",")[0]))
                                .displayName(node.path("display_name").asText())
                                .category(cat.toUpperCase())
                                .type(node.path("type").asText("attraction"))
                                .latitude(node.path("lat").asDouble())
                                .longitude(node.path("lon").asDouble())
                                .address(node.path("display_name").asText())
                                .importance(node.path("importance").asDouble(0.5))
                                .photoUrl(photo)
                                .rating(4.5)
                                .reviewCount(350)
                                .openingHours("09:00 AM - 06:00 PM")
                                .visitDuration("2 hours")
                                .estimatedCost(new BigDecimal("200"))
                                .build());
                    }
                }
            }
        } catch (Exception ex) {
            log.warn("Nominatim places lookup failed for query '{}': {}", query, ex.getMessage());
        }
        return places;
    }

    public List<PlaceDto> getCuratedFallbackPlaces(String destination) {
        String destLower = destination.toLowerCase();
        List<PlaceDto> list = new ArrayList<>();

        if (destLower.contains("goa")) {
            list.add(PlaceDto.builder()
                    .name("Baga Beach")
                    .displayName("Baga Beach, North Goa")
                    .category("BEACH")
                    .type("attraction")
                    .latitude(15.5553).longitude(73.7517)
                    .address("Calangute-Baga Road, North Goa, 403516")
                    .photoUrl("https://images.unsplash.com/photo-1512343879784-a960bf40e7f2?w=800&auto=format&fit=crop")
                    .rating(4.6).reviewCount(12400)
                    .openingHours("Open 24 Hours (Watersports 9 AM - 6 PM)")
                    .visitDuration("3 hours")
                    .estimatedCost(new BigDecimal("500.00"))
                    .importance(0.95).build());

            list.add(PlaceDto.builder()
                    .name("Basilica of Bom Jesus")
                    .displayName("Basilica of Bom Jesus, Old Goa")
                    .category("HERITAGE")
                    .type("monument")
                    .latitude(15.5009).longitude(73.9116)
                    .address("Old Goa Road, Bainguinim, Goa, 403402")
                    .photoUrl("https://images.unsplash.com/photo-1587974928442-77dc3e0dba72?w=800&auto=format&fit=crop")
                    .rating(4.7).reviewCount(8900)
                    .openingHours("09:00 AM - 06:30 PM")
                    .visitDuration("1.5 hours")
                    .estimatedCost(BigDecimal.ZERO)
                    .importance(0.93).build());

            list.add(PlaceDto.builder()
                    .name("Fort Aguada")
                    .displayName("Fort Aguada & Lighthouse, Candolim")
                    .category("SIGHTSEEING")
                    .type("fort")
                    .latitude(15.4925).longitude(73.7736)
                    .address("Sinquerim, Candolim, North Goa, 403515")
                    .photoUrl("https://images.unsplash.com/photo-1614082242765-7c98ca0f3df3?w=800&auto=format&fit=crop")
                    .rating(4.5).reviewCount(7600)
                    .openingHours("09:30 AM - 06:00 PM")
                    .visitDuration("2 hours")
                    .estimatedCost(new BigDecimal("50.00"))
                    .importance(0.90).build());

            list.add(PlaceDto.builder()
                    .name("Dudhsagar Waterfalls")
                    .displayName("Dudhsagar Waterfalls, Bhagwan Mahaveer Sanctuary")
                    .category("NATURE")
                    .type("waterfall")
                    .latitude(15.3144).longitude(74.3143)
                    .address("Sonaulim, South Goa, 403410")
                    .photoUrl("https://images.unsplash.com/photo-1596761226922-38a4cb70f032?w=800&auto=format&fit=crop")
                    .rating(4.8).reviewCount(6200)
                    .openingHours("07:00 AM - 05:00 PM")
                    .visitDuration("4 hours")
                    .estimatedCost(new BigDecimal("750.00"))
                    .importance(0.88).build());

            list.add(PlaceDto.builder()
                    .name("Martin's Corner Restaurant")
                    .displayName("Martin's Corner, Betalbatim")
                    .category("DINING")
                    .type("restaurant")
                    .latitude(15.2954).longitude(73.9161)
                    .address("Binwaddo, Betalbatim, Salcete, Goa, 403713")
                    .photoUrl("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop")
                    .rating(4.6).reviewCount(9100)
                    .openingHours("11:30 AM - 11:30 PM")
                    .visitDuration("2 hours")
                    .estimatedCost(new BigDecimal("800.00"))
                    .importance(0.87).build());

        } else if (destLower.contains("hyderabad")) {
            list.add(PlaceDto.builder()
                    .name("Charminar")
                    .displayName("Charminar, Old City, Hyderabad")
                    .category("HERITAGE")
                    .type("monument")
                    .latitude(17.3616).longitude(78.4747)
                    .address("Char Kaman, Ghansi Bazaar, Hyderabad, 500002")
                    .photoUrl("https://images.unsplash.com/photo-1616190419596-e2839e7380d7?w=800&auto=format&fit=crop")
                    .rating(4.7).reviewCount(18500)
                    .openingHours("09:30 AM - 05:30 PM")
                    .visitDuration("1.5 hours")
                    .estimatedCost(new BigDecimal("25.00"))
                    .importance(0.96).build());

            list.add(PlaceDto.builder()
                    .name("Golconda Fort")
                    .displayName("Golconda Fort, Hyderabad")
                    .category("SIGHTSEEING")
                    .type("fort")
                    .latitude(17.3833).longitude(78.4011)
                    .address("Khair Complex, Ibrahim Bagh, Hyderabad, 500008")
                    .photoUrl("https://images.unsplash.com/photo-1605649487212-47bdab064df8?w=800&auto=format&fit=crop")
                    .rating(4.6).reviewCount(14300)
                    .openingHours("09:00 AM - 05:30 PM (Sound & Light 6:30 PM)")
                    .visitDuration("3 hours")
                    .estimatedCost(new BigDecimal("80.00"))
                    .importance(0.94).build());

            list.add(PlaceDto.builder()
                    .name("Paradise Biryani & Cuisine")
                    .displayName("Paradise Restaurant, Secunderabad")
                    .category("DINING")
                    .type("restaurant")
                    .latitude(17.4418).longitude(78.4907)
                    .address("MG Road, Sappu Bagh Apartment, Secunderabad, 500003")
                    .photoUrl("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop")
                    .rating(4.5).reviewCount(21000)
                    .openingHours("11:00 AM - 11:00 PM")
                    .visitDuration("1.5 hours")
                    .estimatedCost(new BigDecimal("450.00"))
                    .importance(0.91).build());

            list.add(PlaceDto.builder()
                    .name("Hussain Sagar & Buddha Statue")
                    .displayName("Hussain Sagar Lake, Tank Bund, Hyderabad")
                    .category("NATURE")
                    .type("lake")
                    .latitude(17.4239).longitude(78.4738)
                    .address("Tank Bund Road, Hyderabad, 500029")
                    .photoUrl("https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop")
                    .rating(4.5).reviewCount(11200)
                    .openingHours("08:00 AM - 10:00 PM")
                    .visitDuration("2 hours")
                    .estimatedCost(new BigDecimal("100.00"))
                    .importance(0.89).build());

        } else if (destLower.contains("bengaluru") || destLower.contains("bangalore")) {
            list.add(PlaceDto.builder()
                    .name("Bangalore Palace")
                    .displayName("Bangalore Palace, Vasanth Nagar")
                    .category("HERITAGE")
                    .type("palace")
                    .latitude(12.9988).longitude(77.5921)
                    .address("Vasanth Nagar, Bengaluru, Karnataka, 560052")
                    .photoUrl("https://images.unsplash.com/photo-1596176530529-78163a4f7af2?w=800&auto=format&fit=crop")
                    .rating(4.4).reviewCount(9800)
                    .openingHours("10:00 AM - 05:30 PM")
                    .visitDuration("2.5 hours")
                    .estimatedCost(new BigDecimal("250.00"))
                    .importance(0.93).build());

            list.add(PlaceDto.builder()
                    .name("Lalbagh Botanical Garden")
                    .displayName("Lalbagh Botanical Garden, Mavalli")
                    .category("NATURE")
                    .type("garden")
                    .latitude(12.9507).longitude(77.5848)
                    .address("Mavalli, Bengaluru, Karnataka, 560004")
                    .photoUrl("https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=800&auto=format&fit=crop")
                    .rating(4.6).reviewCount(16500)
                    .openingHours("06:00 AM - 07:00 PM")
                    .visitDuration("2 hours")
                    .estimatedCost(new BigDecimal("30.00"))
                    .importance(0.92).build());

            list.add(PlaceDto.builder()
                    .name("Vidyarthi Bhavan")
                    .displayName("Vidyarthi Bhavan, Gandhi Bazaar, Basavanagudi")
                    .category("DINING")
                    .type("restaurant")
                    .latitude(12.9438).longitude(77.5708)
                    .address("32 Gandhi Bazaar Main Rd, Basavanagudi, Bengaluru, 560004")
                    .photoUrl("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop")
                    .rating(4.5).reviewCount(14800)
                    .openingHours("06:30 AM - 11:30 AM, 02:00 PM - 08:00 PM")
                    .visitDuration("1 hour")
                    .estimatedCost(new BigDecimal("150.00"))
                    .importance(0.89).build());

        } else if (destLower.contains("manali")) {
            list.add(PlaceDto.builder()
                    .name("Solang Valley")
                    .displayName("Solang Valley, Manali")
                    .category("ADVENTURE")
                    .type("valley")
                    .latitude(32.3166).longitude(77.1575)
                    .address("Solang Valley, Burwa, Manali, HP, 175131")
                    .photoUrl("https://images.unsplash.com/photo-1626621341517-bbf3d9990a23?w=800&auto=format&fit=crop")
                    .rating(4.7).reviewCount(11800)
                    .openingHours("09:00 AM - 06:00 PM")
                    .visitDuration("4 hours")
                    .estimatedCost(new BigDecimal("1200.00"))
                    .importance(0.95).build());

            list.add(PlaceDto.builder()
                    .name("Hadimba Devi Temple")
                    .displayName("Hadimba Devi Temple, Dhungri")
                    .category("HERITAGE")
                    .type("temple")
                    .latitude(32.2483).longitude(77.1706)
                    .address("Hadimba Temple Rd, Old Manali, Manali, HP, 175131")
                    .photoUrl("https://images.unsplash.com/photo-1605649487212-47bdab064df8?w=800&auto=format&fit=crop")
                    .rating(4.6).reviewCount(13200)
                    .openingHours("08:00 AM - 06:00 PM")
                    .visitDuration("1.5 hours")
                    .estimatedCost(BigDecimal.ZERO)
                    .importance(0.92).build());

            list.add(PlaceDto.builder()
                    .name("Old Manali Cafe 1947")
                    .displayName("Cafe 1947, Old Manali")
                    .category("DINING")
                    .type("cafe")
                    .latitude(32.2570).longitude(77.1780)
                    .address("Near Nehru Kund, Old Manali, HP, 175131")
                    .photoUrl("https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=800&auto=format&fit=crop")
                    .rating(4.5).reviewCount(4200)
                    .openingHours("12:00 PM - 11:00 PM")
                    .visitDuration("2 hours")
                    .estimatedCost(new BigDecimal("600.00"))
                    .importance(0.88).build());

        } else if (destLower.contains("jaipur")) {
            list.add(PlaceDto.builder()
                    .name("Hawa Mahal")
                    .displayName("Hawa Mahal, Badi Choupad, Jaipur")
                    .category("HERITAGE")
                    .type("palace")
                    .latitude(26.9239).longitude(75.8267)
                    .address("Hawa Mahal Rd, Badi Choupad, J.D.A. Market, Jaipur, 302002")
                    .photoUrl("https://images.unsplash.com/photo-1599661046289-e31897846e41?w=800&auto=format&fit=crop")
                    .rating(4.7).reviewCount(24000)
                    .openingHours("09:00 AM - 05:00 PM")
                    .visitDuration("1.5 hours")
                    .estimatedCost(new BigDecimal("50.00"))
                    .importance(0.96).build());

            list.add(PlaceDto.builder()
                    .name("Amber Palace")
                    .displayName("Amber Palace, Amer, Jaipur")
                    .category("SIGHTSEEING")
                    .type("fort")
                    .latitude(26.9855).longitude(75.8513)
                    .address("Devisinghpura, Amer, Jaipur, Rajasthan, 302001")
                    .photoUrl("https://images.unsplash.com/photo-1603262110263-fb010d6e75dc?w=800&auto=format&fit=crop")
                    .rating(4.7).reviewCount(21500)
                    .openingHours("08:00 AM - 05:30 PM")
                    .visitDuration("3.5 hours")
                    .estimatedCost(new BigDecimal("100.00"))
                    .importance(0.94).build());

            list.add(PlaceDto.builder()
                    .name("LMB (Laxmi Mishthan Bhandar)")
                    .displayName("LMB Hotel & Restaurant, Johari Bazaar")
                    .category("DINING")
                    .type("restaurant")
                    .latitude(26.9189).longitude(75.8243)
                    .address("98-101 Johari Bazar, Jaipur, Rajasthan, 302003")
                    .photoUrl("https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=800&auto=format&fit=crop")
                    .rating(4.4).reviewCount(8700)
                    .openingHours("07:00 AM - 11:00 PM")
                    .visitDuration("1.5 hours")
                    .estimatedCost(new BigDecimal("500.00"))
                    .importance(0.88).build());

        } else if (destLower.contains("visakhapatnam") || destLower.contains("vizag")) {
            list.add(PlaceDto.builder()
                    .name("Ramakrishna Beach (RK Beach)")
                    .displayName("RK Beach, Visakhapatnam")
                    .category("BEACH")
                    .type("beach")
                    .latitude(17.7107).longitude(83.3184)
                    .address("Beach Road, Pandurangapuram, Visakhapatnam, 530002")
                    .photoUrl("https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop")
                    .rating(4.6).reviewCount(15200)
                    .openingHours("Open 24 Hours")
                    .visitDuration("2.5 hours")
                    .estimatedCost(BigDecimal.ZERO)
                    .importance(0.94).build());

            list.add(PlaceDto.builder()
                    .name("INS Kursura Submarine Museum")
                    .displayName("Submarine Museum, Beach Road")
                    .category("SIGHTSEEING")
                    .type("museum")
                    .latitude(17.7169).longitude(83.3323)
                    .address("RK Beach Road, Kirlampudi Layout, Visakhapatnam, 530017")
                    .photoUrl("https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=800&auto=format&fit=crop")
                    .rating(4.7).reviewCount(11900)
                    .openingHours("02:00 PM - 08:30 PM (Sun 10 AM - 8:30 PM)")
                    .visitDuration("1.5 hours")
                    .estimatedCost(new BigDecimal("40.00"))
                    .importance(0.92).build());

            list.add(PlaceDto.builder()
                    .name("Kailasagiri Hilltop Park")
                    .displayName("Kailasagiri Park, Visakhapatnam")
                    .category("NATURE")
                    .type("park")
                    .latitude(17.7492).longitude(83.3421)
                    .address("Hill Top Rd, Kailasagiri, Visakhapatnam, 530043")
                    .photoUrl("https://images.unsplash.com/photo-1506744038136-46273834b3fb?w=800&auto=format&fit=crop")
                    .rating(4.5).reviewCount(10400)
                    .openingHours("06:00 AM - 07:30 PM")
                    .visitDuration("2.5 hours")
                    .estimatedCost(new BigDecimal("50.00"))
                    .importance(0.90).build());

            list.add(PlaceDto.builder()
                    .name("Sea Inn - Raju Gari Dhaba")
                    .displayName("Raju Gari Dhaba, Rushikonda")
                    .category("DINING")
                    .type("restaurant")
                    .latitude(17.7812).longitude(83.3854)
                    .address("Rushikonda Beach Rd, Visakhapatnam, 530045")
                    .photoUrl("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop")
                    .rating(4.5).reviewCount(6800)
                    .openingHours("12:00 PM - 10:30 PM")
                    .visitDuration("1.5 hours")
                    .estimatedCost(new BigDecimal("450.00"))
                    .importance(0.86).build());

        } else if (destLower.contains("paris")) {
            list.add(PlaceDto.builder()
                    .name("Eiffel Tower")
                    .displayName("Eiffel Tower, Champ de Mars, Paris")
                    .category("SIGHTSEEING")
                    .type("monument")
                    .latitude(48.8584).longitude(2.2945)
                    .address("Champ de Mars, 5 Av. Anatole France, 75007 Paris")
                    .photoUrl("https://images.unsplash.com/photo-1511739001486-6bfe10ce785f?w=800&auto=format&fit=crop")
                    .rating(4.8).reviewCount(42000)
                    .openingHours("09:00 AM - 11:45 PM")
                    .visitDuration("3 hours")
                    .estimatedCost(new BigDecimal("2200.00"))
                    .importance(0.98).build());

            list.add(PlaceDto.builder()
                    .name("Louvre Museum")
                    .displayName("Musée du Louvre, Paris")
                    .category("HERITAGE")
                    .type("museum")
                    .latitude(48.8606).longitude(2.3376)
                    .address("Rue de Rivoli, 75001 Paris, France")
                    .photoUrl("https://images.unsplash.com/photo-1564507592333-c60657eea523?w=800&auto=format&fit=crop")
                    .rating(4.7).reviewCount(38000)
                    .openingHours("09:00 AM - 06:00 PM (Closed Tue)")
                    .visitDuration("3.5 hours")
                    .estimatedCost(new BigDecimal("1800.00"))
                    .importance(0.96).build());

        } else if (destLower.contains("tokyo")) {
            list.add(PlaceDto.builder()
                    .name("Senso-ji Temple")
                    .displayName("Senso-ji, Asakusa, Tokyo")
                    .category("HERITAGE")
                    .type("temple")
                    .latitude(35.7148).longitude(139.7967)
                    .address("2 Chome-3-1 Asakusa, Taito City, Tokyo 111-0032")
                    .photoUrl("https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=800&auto=format&fit=crop")
                    .rating(4.7).reviewCount(29000)
                    .openingHours("06:00 AM - 05:00 PM")
                    .visitDuration("2 hours")
                    .estimatedCost(BigDecimal.ZERO)
                    .importance(0.96).build());

            list.add(PlaceDto.builder()
                    .name("Shibuya Crossing & Hachiko")
                    .displayName("Shibuya Scramble Crossing, Tokyo")
                    .category("SIGHTSEEING")
                    .type("landmark")
                    .latitude(35.6595).longitude(139.7005)
                    .address("Dogenzaka, Shibuya City, Tokyo 150-0043")
                    .photoUrl("https://images.unsplash.com/photo-1542051841857-5f90071e7989?w=800&auto=format&fit=crop")
                    .rating(4.6).reviewCount(18000)
                    .openingHours("Open 24 Hours")
                    .visitDuration("1.5 hours")
                    .estimatedCost(BigDecimal.ZERO)
                    .importance(0.92).build());

        } else {
            // High quality generic fallback for other destinations
            list.add(PlaceDto.builder()
                    .name(destination + " Historic Old Town & Heritage Promenade")
                    .displayName(destination + " Heritage District")
                    .category("HERITAGE")
                    .type("walking tour")
                    .latitude(0.0).longitude(0.0)
                    .address("Central Heritage Quarter, " + destination)
                    .photoUrl("https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&auto=format&fit=crop")
                    .rating(4.6).reviewCount(1450)
                    .openingHours("09:00 AM - 06:00 PM")
                    .visitDuration("2.5 hours")
                    .estimatedCost(new BigDecimal("150.00"))
                    .importance(0.90).build());

            list.add(PlaceDto.builder()
                    .name(destination + " Botanical Garden & Scenic Nature Park")
                    .displayName(destination + " Scenic Gardens")
                    .category("NATURE")
                    .type("park")
                    .latitude(0.0).longitude(0.0)
                    .address("Greenway Promenade, " + destination)
                    .photoUrl("https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=800&auto=format&fit=crop")
                    .rating(4.5).reviewCount(980)
                    .openingHours("06:00 AM - 06:30 PM")
                    .visitDuration("2 hours")
                    .estimatedCost(new BigDecimal("50.00"))
                    .importance(0.85).build());

            list.add(PlaceDto.builder()
                    .name(destination + " Authentic Regional Food Street & Bistro")
                    .displayName(destination + " Culinary Lane")
                    .category("DINING")
                    .type("restaurant")
                    .latitude(0.0).longitude(0.0)
                    .address("Market Square, " + destination)
                    .photoUrl("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop")
                    .rating(4.6).reviewCount(1200)
                    .openingHours("11:30 AM - 10:30 PM")
                    .visitDuration("1.5 hours")
                    .estimatedCost(new BigDecimal("400.00"))
                    .importance(0.85).build());

            list.add(PlaceDto.builder()
                    .name(destination + " Artisan Craft Market & Souvenirs")
                    .displayName(destination + " Bazaar")
                    .category("SHOPPING")
                    .type("market")
                    .latitude(0.0).longitude(0.0)
                    .address("Main Bazaar Road, " + destination)
                    .photoUrl("https://images.unsplash.com/photo-1555529669-e69e7aa0ba9a?w=800&auto=format&fit=crop")
                    .rating(4.4).reviewCount(850)
                    .openingHours("10:00 AM - 08:30 PM")
                    .visitDuration("2 hours")
                    .estimatedCost(new BigDecimal("300.00"))
                    .importance(0.80).build());
        }

        return list;
    }

    public String getCategoryFallbackPhoto(String category) {
        if (category == null) return "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=800&auto=format&fit=crop";
        return switch (category.toUpperCase()) {
            case "BEACH", "RELAXATION" -> "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop";
            case "DINING", "FOOD" -> "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=800&auto=format&fit=crop";
            case "HERITAGE", "CULTURE" -> "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=800&auto=format&fit=crop";
            case "NATURE", "ADVENTURE" -> "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=800&auto=format&fit=crop";
            case "SHOPPING" -> "https://images.unsplash.com/photo-1555529669-e69e7aa0ba9a?w=800&auto=format&fit=crop";
            case "TRANSIT" -> "https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=800&auto=format&fit=crop";
            default -> "https://images.unsplash.com/photo-1564507592333-c60657eea523?w=800&auto=format&fit=crop";
        };
    }
}
