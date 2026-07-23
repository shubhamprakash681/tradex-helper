package in.shubhamprakash681.notification_service.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.shubhamprakash681.notification_service.dtos.NotificationDtos.WatchlistResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class WatchlistCacheService {
    private static final String KEY_PREFIX = "tradex:watchlist:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Duration ttl;

    public WatchlistCacheService(StringRedisTemplate redisTemplate,
                                 ObjectMapper objectMapper,
                                 @Value("${tradex.notifications.watchlist-cache-ttl-minutes:10}") long ttlMinutes) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    public Optional<List<WatchlistResponse>> get(Long userId) {
        try {
            String json = redisTemplate.opsForValue().get(key(userId));
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, new TypeReference<>() {
            }));
        } catch (RuntimeException | JsonProcessingException exception) {
            return Optional.empty();
        }
    }

    public void put(Long userId, List<WatchlistResponse> watchlist) {
        try {
            redisTemplate.opsForValue().set(key(userId), objectMapper.writeValueAsString(watchlist), ttl);
        } catch (RuntimeException | JsonProcessingException ignored) {
            // Redis is an optimization; database reads remain authoritative.
        }
    }

    public void evict(Long userId) {
        try {
            redisTemplate.delete(key(userId));
        } catch (RuntimeException ignored) {
            // Cache eviction is best effort.
        }
    }

    private String key(Long userId) {
        return KEY_PREFIX + userId;
    }
}
