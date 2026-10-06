package shortener.link;

public record LinkResponse(String shortCode, String shortUrl, String longUrl) {}