package com.alpha.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CustomerserviceApplication {
	public static void main(String[] args) {
		SpringApplication.run(CustomerserviceApplication.class, args);
	}

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, String> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}
