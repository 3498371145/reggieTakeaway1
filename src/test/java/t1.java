import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.Date;
public class t1 {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    public void t1(){
        System.out.println(LocalDateTime.now());
    }

}
