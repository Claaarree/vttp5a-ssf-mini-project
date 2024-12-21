package sg.edu.nus.iss.vttp5a_ssf_mini_project.repo;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ValueRepo {
    
    @Autowired
    @Qualifier("String-String")
    RedisTemplate<String, String> redisTemplate;

    public void createValue(String redisKey, String redisValue) {
        redisTemplate.opsForValue().set(redisKey, redisValue);
    }

    public String readValue(String redisKey) {
        String redisValue = redisTemplate.opsForValue().get(redisKey);
        return redisValue;
    }

    public void updateValue(String redisKey, String updatedRedisValue) {
        redisTemplate.opsForValue().setIfPresent(redisKey, updatedRedisValue);
    }

    public String deleteValue(String redisKey) {
        // method 1
        // redisTemplate.delete(redisKey);

        // method 2
        String deletedValue = redisTemplate.opsForValue().getAndDelete(redisKey);
        return deletedValue;
    }

    public Boolean isExists(String redisKey) {
        return redisTemplate.hasKey(redisKey);
    }

    // Methods below only works for Integer values
    public void increaseValue(String redisKey, Integer increment ) {
        redisTemplate.opsForValue().increment(redisKey, increment);
    }

    public void decreaseValue(String redisKey, Integer decrement) {
        redisTemplate.opsForValue().decrement(redisKey, decrement);
    }

    public void expire(String redisKey, Long expireValue) {
        Duration expireDuration = Duration.ofSeconds(expireValue);
        redisTemplate.expire(redisKey, expireDuration);
    }
}
