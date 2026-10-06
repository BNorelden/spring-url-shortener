package shortener.link;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class ShortLinkController {

    private final ShortLinkService service;

    public ShortLinkController(ShortLinkService service) {
        this.service = service;
    }

    @PostMapping("/api/links")
    @ResponseStatus(HttpStatus.CREATED)
    public LinkResponse create(@Valid @RequestBody CreateLinkRequest req) {
        ShortLink link = service.create(req.longUrl());
        return toResponse(link);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        ShortLink link = service.resolve(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(link.getLongUrl()))
                .build();
    }

    @GetMapping("/api/links")
    public List<LinkResponse> list() {
        return service.listAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private LinkResponse toResponse(ShortLink link) {
        String shortUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{code}")
                .buildAndExpand(link.getShortCode())
                .toUriString();
        return new LinkResponse(link.getShortCode(), shortUrl, link.getLongUrl());
    }
}