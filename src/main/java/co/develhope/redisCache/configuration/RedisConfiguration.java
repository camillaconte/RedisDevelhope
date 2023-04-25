package co.develhope.redisCache.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.password}")
    private String redisPassword;

    @Value("${redis.database}")
    private String redisDatabase;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPassword));
        //con questo client non posso inserire proprio il nome del DB
        //ma me lo tengo salvato in application.yml
        //perché magari con altri client sarà possibile utilizzarlo
        redisStandaloneConfiguration.setDatabase(0);
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    //RedisTemplate sarà utilizzato dalla nostra applicazione in generale
    //per andare ad abilitare la RedisCache
    //Gli dico: istanzia una redisCache
    //e i dati di connessione te li dò attraverso il Bean che abbiamo scritto sopra

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }


}
