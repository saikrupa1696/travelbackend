package com.travelmate.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_trips")
public class SharedTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "share_token", nullable = false, unique = true, length = 64)
    private String shareToken;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_revoked", nullable = false)
    private Boolean isRevoked = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public SharedTrip() {}

    public SharedTrip(Long id, Trip trip, String shareToken, LocalDateTime expiresAt, Boolean isRevoked, LocalDateTime createdAt) {
        this.id = id;
        this.trip = trip;
        this.shareToken = shareToken;
        this.expiresAt = expiresAt;
        this.isRevoked = isRevoked != null ? isRevoked : false;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Trip trip;
        private String shareToken;
        private LocalDateTime expiresAt;
        private Boolean isRevoked = false;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder trip(Trip trip) { this.trip = trip; return this; }
        public Builder shareToken(String shareToken) { this.shareToken = shareToken; return this; }
        public Builder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder isRevoked(Boolean isRevoked) { this.isRevoked = isRevoked; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public SharedTrip build() {
            return new SharedTrip(id, trip, shareToken, expiresAt, isRevoked, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public Boolean getIsRevoked() { return isRevoked; }
    public void setIsRevoked(Boolean isRevoked) { this.isRevoked = isRevoked; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
