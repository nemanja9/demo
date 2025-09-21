package com.example.demo.service;

import com.example.demo.dto.ProductCreateDto;
import com.example.demo.dto.ProductDto;
import com.example.demo.dto.ProductSummaryDto;
import com.example.demo.dto.SimpleProductDto;
import com.example.demo.entity.ProductEntity;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	/**
	 * Creates a new product after validating the input data
	 * @param productCreateDto the DTO containing product creation data
	 * @return the ID of the newly created product
	 */
	public ProductDto createProduct(@NotNull ProductCreateDto productCreateDto) {
		// validate name
		var existingName = productRepository.findByName(productCreateDto.getName());
		if (existingName.isPresent()) {
			throw new ApiException( "Product with same name already exists");
		}

		// validate quantity
		if (productCreateDto.getQuantity() != null && productCreateDto.getQuantity() < 0) {
			throw new ApiException( "Quantity cannot be negative");
		}
		var quantity = productCreateDto.getQuantity() == null ? 0 : productCreateDto.getQuantity();

		// validate price
		if (productCreateDto.getPrice().compareTo( BigDecimal.ZERO ) < 0) {
			throw new ApiException( "Price cannot be negative");
		}

		// saving the new product
		var productEntity = ProductEntity.builder()
				.name(productCreateDto.getName())
				.quantity(quantity)
				.price(productCreateDto.getPrice())
				.build();
		productEntity = productRepository.save(productEntity);
		return mapToDto( productEntity );
	}

	/**
	 * Retrieves all products from the repository and maps them to DTOs
	 * @return a list of ProductDto representing all products
	 */
	public Page<ProductDto> getAllProducts(Pageable pageable) {
		return productRepository.findAll(pageable)
								.map(this::mapToDto);
	}

	/**
	 * Searches for products by name containing the given query string (case-insensitive)
	 * @param query the search query string
	 * @return a list of ProductDto matching the search criteria
	 */
	public List<ProductDto> searchProducts(@NotNull String query) {
		return productRepository.findAllByNameContainingIgnoreCase(query).stream()
				.map( this::mapToDto )
				.toList();
	}

	/**
	 * Updates the quantity of a product identified by its ID
	 * @param id the ID of the product to update
	 * @param quantity the new quantity to set
	 * @return the updated productDto
	 */
	public ProductDto updateProductQuantity(@NotNull Long id, @NotNull Integer quantity) {
		var productOpt = productRepository.findById(id);
		if (productOpt.isEmpty()) {
			throw new ApiException( "Product with given ID not found");
		}
		if (quantity < 0) {
			throw new ApiException( "Quantity cannot be negative");
		}
		var product = productOpt.get();
		product.setQuantity(quantity);
		product = productRepository.save(product);
		return mapToDto(product);
	}

	/**
	 * Generates a summary of all products including total count, total quantity,
	 * average price, and a list of out-of-stock products
	 * @return a ProductSummaryDto containing the summary information
	 */
	public ProductSummaryDto getProductSummary() {
		var allProducts = productRepository.findAll();
		var totalProducts = allProducts.size();
		var totalQuantity = 0;
		var totalPrice = BigDecimal.ZERO;
		var averagePrice = BigDecimal.ZERO;
		var outOfStock = new ArrayList<SimpleProductDto>();

		if (totalProducts > 0) {
			for (ProductEntity product : allProducts) {
				totalQuantity += product.getQuantity();
				totalPrice = totalPrice.add( product.getPrice() );
				if (product.getQuantity() == 0) {
					outOfStock.add( SimpleProductDto.builder()
													.id( product.getId() )
													.name( product.getName() )
													.build() );
				}
			}
			averagePrice = totalPrice.divide(BigDecimal.valueOf(totalProducts), RoundingMode.HALF_UP );
		}

		return ProductSummaryDto.builder()
				.totalProducts( totalProducts )
				.totalQuantity( totalQuantity )
				.averagePrice( averagePrice )
				.outOfStock( outOfStock )
				.build();
	}

	/**
	 * Deletes a product identified by its ID
	 * @param id the ID of the product to delete
	 */
	public void deleteProduct(@NotNull Long id) {
		var productOpt = productRepository.findById( id );
		if (productOpt.isEmpty()) {
			throw new ApiException( "Product with given ID not found");
		}
		productRepository.delete( productOpt.get() );
	}

	/**
	 * Maps a ProductEntity to a ProductDto
	 * @param productEntity the entity to map
	 * @return the corresponding ProductDto
	 */
	private ProductDto mapToDto(ProductEntity productEntity) {
		return ProductDto.builder()
						 .id(productEntity.getId())
						 .name(productEntity.getName())
						 .quantity(productEntity.getQuantity())
						 .price(productEntity.getPrice())
						 .build();
	}

}
