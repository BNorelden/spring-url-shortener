package shortener.link;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.security.SecureRandom;

@Service
public class ShortLinkService {

    private static final String BASE62 =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 7;
    private static final int MAX_ATTEMPTS = 5;

    private final ShortLinkRepository repo;
    private final SecureRandom random = new SecureRandom();

    public ShortLinkService(ShortLinkRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public ShortLink create(String longUrl) {
        String code = generateUniqueCode();
        ShortLink link = new ShortLink(code, longUrl);
        return repo.save(link);
    }

    @Transactional(readOnly = true)
    public ShortLink resolve(String code) {
        return repo.findByShortCode(code)
                .orElseThrow(() -> new LinkNotFoundException(code));
    }

    @Transactional(readOnly = true)
    public List<ShortLink> listAll() {
        return repo.findAll();
    }

    private String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String code = randomCode();
            if (!repo.existsByShortCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException(
                "Could not generate unique code after " + MAX_ATTEMPTS + " attempts");
    }

    private String randomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(BASE62.charAt(random.nextInt(BASE62.length())));
        }
        return sb.toString();
    }
}