package com.chrms.application.port.out;

import java.time.Duration;
import java.util.Optional;

public interface CacheService {
    void put(String key, Object value);
    void put(String key, Object value, Duration ttl);
    <T> Optional<T> get(String key, Class<T> type);
    void delete(String key);
    void clear();
    boolean exists(String key);
}
