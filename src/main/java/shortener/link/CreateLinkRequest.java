package shortener.link;

import jakarta.validation.constraints.NotBlank;

public record CreateLinkRequest(
        @NotBlank(message = "longUrl is required")
        String longUrl
) {}