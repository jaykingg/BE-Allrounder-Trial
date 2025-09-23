package rounderall.redis.repository

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import rounderall.redis.domain.Product
import java.time.Duration

@Repository
class ProductRedisRepository(
    @Qualifier("productRedisTemplate")
    private val redisTemplate: RedisTemplate<String, Product>
) {
    private fun key(id: Long) = "product:$id"

    fun save(product: Product, ttlSeconds: Long? = 600): Product {
        val ops = redisTemplate.opsForValue()
        ttlSeconds?.let {
            ops.set(key(product.id), product, Duration.ofSeconds(it))
        } ?: ops.set(key(product.id), product)
        return product
    }

    fun findById(id: Long): Product? =
        redisTemplate.opsForValue().get(key(id))

    fun delete(id: Long) {
        redisTemplate.delete(key(id))
    }
}