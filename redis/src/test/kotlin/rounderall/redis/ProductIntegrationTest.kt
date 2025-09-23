package rounderall.redis

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import rounderall.redis.application.ProductService
import rounderall.redis.domain.Product
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class ProductIntegrationTest {
    companion object {
        @Container
        private val redis = GenericContainer(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun redisProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.redis.host") { redis.host }
            registry.add("spring.redis.port") { redis.firstMappedPort }
        }
    }

    @Autowired
    private lateinit var productService: ProductService

    @Test
    fun `캐시 및 기본 흐름`() {
        val p = productService.upsertProduct(Product(1, "Apple", 100))
        assertThat(p.id).isEqualTo(1)

        val first = productService.getProduct(1)
        val second = productService.getProduct(1) // 캐시 히트 기대
        assertThat(first).isEqualTo(second)

        productService.deleteProduct(1)
        assertThat(productService.getProduct(1)).isNull()
    }

    @Test
    fun `분산락 - 동시 감소 시 일관성 보장`() {
        productService.upsertProduct(Product(2, "Orange", 50))

        val threads = 10
        val executor = Executors.newFixedThreadPool(threads)
        val latch = CountDownLatch(threads)
        repeat(threads) {
            executor.execute {
                try {
                    productService.decreaseStock(2, 1)
                } finally {
                    latch.countDown()
                }
            }
        }
        latch.await()
        val result = productService.getProduct(2)!!
        assertThat(result.stock).isEqualTo(40)
    }
}



