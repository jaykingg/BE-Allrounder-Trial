package rounderall.redis.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rounderall.redis.application.ProductService
import rounderall.redis.domain.Product

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<Product?> =
        ResponseEntity.ok(productService.getProduct(id))

    @PostMapping
    fun upsert(@RequestBody request: UpsertProductRequest): ResponseEntity<Product> {
        val product = Product(id = request.id, name = request.name, stock = request.stock)
        return ResponseEntity.ok(productService.upsertProduct(product))
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/decrease")
    fun decrease(@PathVariable id: Long, @RequestParam quantity: Int): ResponseEntity<Product> =
        ResponseEntity.ok(productService.decreaseStock(id, quantity))
}

data class UpsertProductRequest(
    val id: Long,
    val name: String,
    val stock: Int
)



