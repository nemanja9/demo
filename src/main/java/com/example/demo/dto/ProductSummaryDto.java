package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSummaryDto {

	private int totalProducts;
	private int                    totalQuantity;
	private BigDecimal             averagePrice;
	private List<SimpleProductDto> outOfStock;
}
