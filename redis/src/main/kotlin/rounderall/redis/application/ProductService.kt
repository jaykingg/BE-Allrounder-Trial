package rounderall.redis.application

import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import rounderall.redis.domain.Product
import rounderall.redis.lock.DistributedLock
import rounderall.redis.repository.ProductRedisRepository

@Service
class ProductService(
    private val productRedisRepository: ProductRedisRepository
) {
    // 캐싱: 조회는 캐시에 저장하고, 없으면 Repository에서 로드
    @Cacheable(cacheNames = ["product"], key = "#id", unless = "#result == null")
    fun getProduct(id: Long): Product? = productRedisRepository.findById(id)

    // 캐시 갱신: 저장/수정 후 캐시 업데이트
    @CachePut(cacheNames = ["product"], key = "#result.id", unless = "#result == null")
    fun upsertProduct(product: Product): Product = productRedisRepository.save(product)

    // 캐시 제거: 삭제 후 캐시 제거
    @CacheEvict(cacheNames = ["product"], key = "#id")
    fun deleteProduct(id: Long) = productRedisRepository.delete(id)

    // 분산락 예제: 재고 감소는 동시성 제어 필요
    @DistributedLock(key = "lock:product:#{#productId}", waitMillis = 2_000, leaseMillis = 5_000)
    fun decreaseStock(productId: Long, quantity: Int): Product {
        val current = productRedisRepository.findById(productId)
            ?: throw IllegalArgumentException("상품이 존재하지 않습니다: $productId")
        require(quantity > 0) { "감소 수량은 0보다 커야 합니다" }
        require(current.stock >= quantity) { "재고 부족" }
        val updated = current.copy(stock = current.stock - quantity)
        return productRedisRepository.save(updated)
    }
}


