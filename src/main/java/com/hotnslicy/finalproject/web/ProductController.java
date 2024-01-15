package com.hotnslicy.finalproject.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotnslicy.finalproject.domain.Product;

@RestController
public class ProductController {
	
	private List<Product> allProducts = new ArrayList<>();
	
	public ProductController() {
		allProducts.add(new Product(1, "Product # 1", new BigDecimal(19.99)));
		allProducts.add(new Product(2, "Product # 2", new BigDecimal(19.99)));
		allProducts.add(new Product(3, "Product # 3", new BigDecimal(19.99)));
		allProducts.add(new Product(5, "Product # 4", new BigDecimal(19.99)));
		allProducts.add(new Product(4, "Product # 5", new BigDecimal(19.99)));
	}
	
	@GetMapping("/products")
	public ResponseEntity<List<Product>> getAllProducts(){
		return ResponseEntity.ok(allProducts);
	}

}
