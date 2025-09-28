package rounderall.redis.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rounderall.redis.domain.Product
import rounderall.redis.service.ProductService

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getAllProducts())
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<Product?> {
        return ResponseEntity.ok(productService.getProduct(id))
    }

    @PostMapping
    fun createProduct(@RequestBody request: CreateProductRequest): ResponseEntity<Product> {
        val product = Product(id = 0, name = request.name, stock = request.stock)
        return ResponseEntity.ok(productService.saveProduct(product))
    }

    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody request: UpdateProductRequest
    ): ResponseEntity<Product> {
        val product = Product(id = id, name = request.name, stock = request.stock)
        return ResponseEntity.ok(productService.saveProduct(product))
    }

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/decrease-stock")
    fun decreaseStock(
        @PathVariable id: Long,
        @RequestParam quantity: Int
    ): ResponseEntity<Product> {
        return ResponseEntity.ok(productService.decreaseStock(id, quantity))
    }
}

data class CreateProductRequest(
    val name: String,
    val stock: Int
)

data class UpdateProductRequest(
    val name: String,
    val stock: Int
)
