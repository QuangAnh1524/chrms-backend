package com.chrms.infrastructure.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setExpire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    // JWT Blacklist
    public void blacklistToken(String token, long expirationTime) {
        long ttl = expirationTime - System.currentTimeMillis();
        if (ttl > 0) {
            set("jwt:blacklist:" + token, "true", ttl, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return exists("jwt:blacklist:" + token);
    }

    // Cache keys
    public static final String CACHE_SEARCH_PREFIX = "search:";
    public static final String CACHE_CHAT_PREFIX = "chat:";
    public static final String CACHE_NOTIFICATION_PREFIX = "notification:";
    public static final String CACHE_DOCTOR_RATING_PREFIX = "doctor:rating:";
}

