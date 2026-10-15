package com.travelmate.repository;

import com.travelmate.entity.SharedTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SharedTripRepository extends JpaRepository<SharedTrip, Long> {
    Optional<SharedTrip> findByShareToken(String shareToken);
    Optional<SharedTrip> findByTripId(Long tripId);
    void deleteByTripId(Long tripId);
}
