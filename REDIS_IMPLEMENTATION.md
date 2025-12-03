# Redis Implementation Summary

## ✅ Đã Implement

### 1. Redis Configuration
- **RedisConfig.java**: Cấu hình Redis connection, RedisTemplate, và CacheManager
- TTL mặc định: 5 phút (có thể config trong `application.yml`)

### 2. Redis Cache Service
- **RedisCacheService.java**: Service để thao tác với Redis
  - Set/Get/Delete operations
  - JWT Blacklist support
  - Cache key prefixes cho các loại data

### 3. Caching Implementations

#### a. Medical Records Search
- **SearchMedicalRecordsUseCase**: Cache kết quả search với `@Cacheable`
- Cache key: `symptoms:diagnosis` hoặc `diagnosis:diagnosis`
- TTL: 5 phút

#### b. Chat Messages
- **GetChatMessagesUseCase**: Cache recent messages (last 50)
- Cache key: `chat:{appointmentId}`
- TTL: 5 phút
- **Note**: Polling requests (với `after` parameter) không cache để đảm bảo real-time

#### c. Doctor Ratings
- **FeedbackController**: Cache average rating
- Cache key: `doctor:rating:{doctorId}`
- TTL: 10 phút
- Invalidate khi có feedback mới

#### d. JWT Token Blacklist
- **JwtTokenProvider**: Check blacklist khi validate token
- **AuthController**: Blacklist token khi logout
- Cache key: `jwt:blacklist:{token}`
- TTL: Tự động set theo expiration time của token

## 📋 Cache Keys Structure

```
jwt:blacklist:{token}          - JWT blacklist
search:{symptoms}:{diagnosis}  - Search results
chat:{appointmentId}           - Chat messages
doctor:rating:{doctorId}       - Doctor average rating
notification:{userId}          - User notifications (future)
```

## 🔧 Configuration

### application.yml
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 60000

app:
  cache:
    ttl: 300  # 5 minutes in seconds
```

### Docker Compose
Redis đã được config trong `docker-compose.yml`:
- Port: 6379
- Health check enabled
- Volume persistence

## 🚀 Usage Examples

### 1. Cache Search Results
```java
@Cacheable(value = "medicalRecords", key = "#symptoms")
public List<MedicalRecord> searchBySymptoms(String symptoms) {
    return recordRepository.searchBySymptoms(symptoms);
}
```

### 2. Manual Cache
```java
String cacheKey = "doctor:rating:" + doctorId;
Object cached = cacheService.get(cacheKey);
if (cached == null) {
    // Get from DB
    Double rating = getRating();
    cacheService.set(cacheKey, rating, 10, TimeUnit.MINUTES);
}
```

### 3. JWT Blacklist
```java
// On logout
cacheService.blacklistToken(token, expirationTime);

// On token validation
if (cacheService.isTokenBlacklisted(token)) {
    return false; // Token invalid
}
```

## 📊 Performance Benefits

1. **Search Results**: Giảm load database khi search nhiều lần
2. **Chat Messages**: Faster loading cho recent messages
3. **Doctor Ratings**: Giảm tính toán lại average rating
4. **JWT Blacklist**: Fast token validation và logout

## 🔄 Cache Invalidation

- **Automatic**: TTL-based expiration
- **Manual**: `cacheService.delete(key)` khi cần
- **Future**: Event-driven invalidation khi data thay đổi

## ⚠️ Notes

1. Polling endpoints (chat với `after` parameter) không cache để đảm bảo real-time
2. Unread messages không cache
3. JWT blacklist TTL = token expiration time
4. Cache size: Monitor Redis memory usage trong production

