package rounderall.redis.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import rounderall.redis.domain.Product
import rounderall.redis.lock.DistributedLock
import java.util.concurrent.ConcurrentHashMap

@Service
class ProductService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    
    // 메모리 저장소 (실제 프로젝트에서는 DB 사용)
    private val products = ConcurrentHashMap<Long, Product>()
    private var nextId = 1L

    // 캐시에서 조회, 없으면 메모리에서 조회 후 캐시에 저장
    @Cacheable(cacheNames = ["product"], key = "#id", unless = "#result == null")
    fun getProduct(id: Long): Product? {
        return products[id]
    }

    // 메모리에 저장 후 캐시 업데이트
    @CachePut(cacheNames = ["product"], key = "#result.id", unless = "#result == null")
    fun saveProduct(product: Product): Product {
        val newProduct = if (product.id == 0L) {
            product.copy(id = nextId++)
        } else {
            product
        }
        products[newProduct.id] = newProduct
        return newProduct
    }

    // 메모리에서 삭제 후 캐시 제거
    @CacheEvict(cacheNames = ["product"], key = "#id")
    fun deleteProduct(id: Long) {
        products.remove(id)
    }

    // 분산락을 사용한 재고 감소 (동시성 제어)
    @DistributedLock(key = "lock:product:#{#productId}", waitMillis = 1000, leaseMillis = 5000)
    fun decreaseStock(productId: Long, quantity: Int): Product {
        val product = products[productId] 
            ?: throw IllegalArgumentException("상품이 존재하지 않습니다: $productId")
        
        require(quantity > 0) { "감소 수량은 0보다 커야 합니다" }
        require(product.stock >= quantity) { "재고 부족: 현재 ${product.stock}, 요청 ${quantity}" }
        
        val updatedProduct = product.copy(stock = product.stock - quantity)
        products[productId] = updatedProduct
        
        // 캐시도 업데이트
        redisTemplate.opsForValue().set("product::${productId}", updatedProduct.toString())
        
        return updatedProduct
    }

    // 모든 상품 조회
    fun getAllProducts(): List<Product> = products.values.toList()
}
