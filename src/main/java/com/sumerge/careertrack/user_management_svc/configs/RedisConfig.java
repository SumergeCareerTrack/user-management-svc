package com.sumerge.careertrack.user_management_svc.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableRedisHttpSession
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private Integer redisPort;

    @Value("${spring.data.redis.pass}")
    private String redisPass;


        @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        return new JedisPool(poolConfig, redisHost, redisPort); 
    }

    @Bean
    public Jedis jedis(JedisPool jedisPool) {
        return jedisPool.getResource(); 
    }
    // @Bean
    // @Primary
    // JedisConnectionFactory jedisConnectionFactory() {
    // RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
    // redisStandaloneConfiguration.setPassword(RedisPassword.of(redisPass));
    // return new JedisConnectionFactory(redisStandaloneConfiguration);
}






