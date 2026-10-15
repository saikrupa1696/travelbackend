package com.travelmate.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Must be a valid email")
    private String email;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private String email;

        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public UpdateProfileRequest build() { return new UpdateProfileRequest(name, email); }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
