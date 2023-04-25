package co.develhope.redisCache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class RedisCacheMock {

    //il RedisServer lo prendo da Redis embedded
    private RedisServer redisServer;

    @Value("${redis.port}") int redisPort;

    //questi sono concetti di Interceptor?
    @PostConstruct
    public void postConstruct() {
        try {
            redisServer = new RedisServer(redisPort);
            redisServer.start();
        }catch(Exception e){
            System.out.println(e);
            throw e;
        }
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
