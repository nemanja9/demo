package com.example.demo.controller;

import com.example.demo.dto.ProductCreateDto;
import com.example.demo.dto.ProductDto;
import com.example.demo.dto.ProductSummaryDto;
import com.example.demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

	private final ProductService productService;

	@Operation(summary = "Create a new product")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Product successfully created"),
			@ApiResponse(responseCode = "400", description = "Invalid input data")
	})
	@PostMapping
	public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductCreateDto productCreateDto) {
		ProductDto createdProduct = productService.createProduct(productCreateDto);
		return ResponseEntity
				.created(URI.create("/api/v1/products/" + createdProduct.getId()))
				.body(createdProduct);
	}

	@Operation(summary = "Get all products (paginated)")
	@ApiResponse(responseCode = "200", description = "Paginated list of products retrieved")
	@GetMapping
	public ResponseEntity<Page<ProductDto>> getAllProducts(
			@ParameterObject Pageable pageable) {
		return ResponseEntity.ok(productService.getAllProducts(pageable));
	}


	@Operation(summary = "Search products by query")
	@ApiResponse(responseCode = "200", description = "List of matching products retrieved")
	@GetMapping("/search")
	public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam @NotBlank String query) {
		return ResponseEntity.ok(productService.searchProducts(query));
	}

	@Operation(summary = "Update product quantity")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Quantity updated successfully"),
			@ApiResponse(responseCode = "422", description = "Product not found or invalid quantity")
	})
	@PutMapping("/{id}/quantity")
	public ResponseEntity<ProductDto> updateQuantity(@PathVariable @NotNull Long id,
													 @RequestParam @NotNull Integer quantity) {
		ProductDto updatedProduct = productService.updateProductQuantity(id, quantity);
		return ResponseEntity.ok(updatedProduct);
	}

	@Operation(summary = "Delete a product")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Product deleted successfully"),
			@ApiResponse(responseCode = "422", description = "Product not found")
	})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable @NotNull Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Get product summary (statistics, totals, etc.)")
	@ApiResponse(responseCode = "200", description = "Summary retrieved successfully")
	@GetMapping("/summary")
	public ResponseEntity<ProductSummaryDto> getProductSummary() {
		return ResponseEntity.ok(productService.getProductSummary());
	}
}
