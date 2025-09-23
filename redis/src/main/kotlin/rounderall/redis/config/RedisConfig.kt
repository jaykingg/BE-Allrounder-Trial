package rounderall.redis.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import rounderall.redis.domain.Product
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig {

    @Bean
    fun redisTemplate(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper
    ): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(connectionFactory)
        val keySerializer = StringRedisSerializer()
        val valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        template.keySerializer = keySerializer
        template.hashKeySerializer = keySerializer
        template.valueSerializer = valueSerializer
        template.hashValueSerializer = valueSerializer
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun productRedisTemplate(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper,
    ): RedisTemplate<String, Product> {
        val template = RedisTemplate<String, Product>()
        template.setConnectionFactory(connectionFactory)
        val keySerializer = StringRedisSerializer()
        val valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        template.keySerializer = keySerializer
        template.hashKeySerializer = keySerializer
        template.valueSerializer = valueSerializer
        template.hashValueSerializer = valueSerializer
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun cacheManager(
        connectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper
    ): CacheManager {
        val keySerializer = StringRedisSerializer()
        val valueSerializer = GenericJackson2JsonRedisSerializer(objectMapper)

        val defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10))
            .disableCachingNullValues()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))

        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .build()
    }

    @Bean
    fun cacheKeyGenerator(): KeyGenerator = KeyGenerator { target, method, params ->
        buildString {
            append(target::class.java.simpleName)
            append("::")
            append(method.name)
            if (params.isNotEmpty()) {
                append("::")
                append(params.joinToString("|") { it?.toString() ?: "null" })
            }
        }
    }
}