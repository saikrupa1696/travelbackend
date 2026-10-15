package com.travelmate.repository;

import com.travelmate.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Trip> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Trip> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT t FROM Trip t WHERE t.user.id = :userId AND " +
           "(:destination IS NULL OR LOWER(t.destination) LIKE LOWER(CONCAT('%', :destination, '%'))) AND " +
           "(:travelType IS NULL OR t.travelType = :travelType) " +
           "ORDER BY t.createdAt DESC")
    List<Trip> searchTrips(@Param("userId") Long userId,
                           @Param("destination") String destination,
                           @Param("travelType") String travelType);
}
