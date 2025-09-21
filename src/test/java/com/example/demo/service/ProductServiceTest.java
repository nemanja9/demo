package com.example.demo.service;

import com.example.demo.dto.ProductCreateDto;
import com.example.demo.dto.ProductDto;
import com.example.demo.dto.ProductSummaryDto;
import com.example.demo.entity.ProductEntity;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private ProductService productService;

	private AutoCloseable closeable;

	@BeforeEach
	void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
		closeable.close();
	}

	@Test
	void createProduct_success() {
		ProductCreateDto dto = new ProductCreateDto("Test", 5, BigDecimal.TEN);

		when(productRepository.findByName("Test")).thenReturn(Optional.empty());
		when(productRepository.save(any())).thenAnswer(invocation -> {
			ProductEntity e = invocation.getArgument(0);
			e.setId(1L);
			return e;
		});

		ProductDto result = productService.createProduct(dto);

		assertNotNull(result);
		assertEquals("Test", result.getName());
		assertEquals(BigDecimal.TEN, result.getPrice());
		verify(productRepository).save(any());
	}

	@Test
	void createProduct_duplicateName_throwsException() {
		ProductCreateDto dto = new ProductCreateDto("Test", 5, BigDecimal.ONE);
		when(productRepository.findByName("Test")).thenReturn(Optional.of(new ProductEntity()));

		assertThrows(ApiException.class, () -> productService.createProduct(dto));
	}

	@Test
	void getAllProducts_returnsPage() {
		ProductEntity e = ProductEntity.builder().id(1L).name("Test").price(BigDecimal.ONE).quantity(2).build();
		when(productRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(e)));

		Page<ProductDto> result = productService.getAllProducts(PageRequest.of(0, 10));

		assertEquals(1, result.getTotalElements());
		assertEquals("Test", result.getContent().getFirst().getName());
	}

	@Test
	void searchProducts_returnsList() {
		ProductEntity e = ProductEntity.builder().id(1L).name("Apple").price(BigDecimal.ONE).quantity(3).build();
		when(productRepository.findAllByNameContainingIgnoreCase("app")).thenReturn(List.of(e));

		List<ProductDto> result = productService.searchProducts("app");

		assertEquals(1, result.size());
		assertEquals("Apple", result.getFirst().getName());
	}

	@Test
	void updateProductQuantity_success() {
		ProductEntity e = ProductEntity.builder().id(1L).name("Test").price(BigDecimal.ONE).quantity(1).build();
		when(productRepository.findById(1L)).thenReturn(Optional.of(e));
		when(productRepository.save(any())).thenReturn(e);

		ProductDto result = productService.updateProductQuantity(1L, 10);

		assertEquals(10, result.getQuantity());
	}

	@Test
	void updateProductQuantity_notFound_throwsException() {
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ApiException.class, () -> productService.updateProductQuantity(1L, 10));
	}

	@Test
	void getProductSummary_calculatesCorrectly() {
		ProductEntity e1 = ProductEntity.builder().id(1L).name("A").price(BigDecimal.TEN).quantity(0).build();
		ProductEntity e2 = ProductEntity.builder().id(2L).name("B").price(BigDecimal.valueOf(20)).quantity(5).build();
		when(productRepository.findAll()).thenReturn(List.of(e1, e2));

		ProductSummaryDto summary = productService.getProductSummary();

		assertEquals(2, summary.getTotalProducts());
		assertEquals(5, summary.getTotalQuantity());
		assertEquals(BigDecimal.valueOf(15), summary.getAveragePrice());
		assertEquals(1, summary.getOutOfStock().size());
	}

	@Test
	void deleteProduct_success() {
		ProductEntity e = ProductEntity.builder().id(1L).name("Test").price(BigDecimal.ONE).quantity(1).build();
		when(productRepository.findById(1L)).thenReturn(Optional.of(e));

		productService.deleteProduct(1L);

		verify(productRepository).delete(e);
	}

	@Test
	void deleteProduct_notFound_throwsException() {
		when(productRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ApiException.class, () -> productService.deleteProduct(1L));
	}
}
