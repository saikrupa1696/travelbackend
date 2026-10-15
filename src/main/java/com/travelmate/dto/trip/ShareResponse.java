package com.travelmate.dto.trip;

import java.time.LocalDateTime;

public class ShareResponse {
    private String shareToken;
    private String shareUrl;
    private LocalDateTime expiresAt;
    private boolean isRevoked;

    public ShareResponse() {}

    public ShareResponse(String shareToken, String shareUrl, LocalDateTime expiresAt, boolean isRevoked) {
        this.shareToken = shareToken;
        this.shareUrl = shareUrl;
        this.expiresAt = expiresAt;
        this.isRevoked = isRevoked;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String shareToken;
        private String shareUrl;
        private LocalDateTime expiresAt;
        private boolean isRevoked;

        public Builder shareToken(String shareToken) { this.shareToken = shareToken; return this; }
        public Builder shareUrl(String shareUrl) { this.shareUrl = shareUrl; return this; }
        public Builder expiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; return this; }
        public Builder isRevoked(boolean isRevoked) { this.isRevoked = isRevoked; return this; }
        public ShareResponse build() { return new ShareResponse(shareToken, shareUrl, expiresAt, isRevoked); }
    }

    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }
    public String getShareUrl() { return shareUrl; }
    public void setShareUrl(String shareUrl) { this.shareUrl = shareUrl; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public boolean isRevoked() { return isRevoked; }
    public void setRevoked(boolean revoked) { isRevoked = revoked; }
}
