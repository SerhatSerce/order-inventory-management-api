package com.serhat.orderinventory.service;

import com.serhat.orderinventory.dto.product.ProductRequest;
import com.serhat.orderinventory.dto.product.ProductResponse;
import com.serhat.orderinventory.dto.product.UpdateStockRequest;
import com.serhat.orderinventory.entity.Product;
import com.serhat.orderinventory.exception.ProductNotFoundException;
import com.serhat.orderinventory.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product savedProduct = productRepository.save(product);

        log.info("Product created. productId={}, name={}, stockQuantity={}",
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getStockQuantity()
        );

        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .filter(product -> !product.isDeleted())
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = findProductById(id);
        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductById(id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated. productId={}, name={}, stockQuantity={}",
                updatedProduct.getId(),
                updatedProduct.getName(),
                updatedProduct.getStockQuantity()
        );

        return mapToResponse(updatedProduct);
    }

    @Transactional
    public ProductResponse updateStock(Long id, UpdateStockRequest request) {
        Product product = findProductById(id);

        Integer oldStock = product.getStockQuantity();
        product.setStockQuantity(request.getStockQuantity());

        Product updatedProduct = productRepository.save(product);

        log.info("Product stock updated. productId={}, oldStock={}, newStock={}",
                updatedProduct.getId(),
                oldStock,
                updatedProduct.getStockQuantity()
        );

        return mapToResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductById(id);
        product.setDeleted(true);
        productRepository.save(product);

        log.info("Product soft deleted. productId={}, name={}",
                product.getId(),
                product.getName()
        );
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .filter(product -> !product.isDeleted())
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setVersion(product.getVersion());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }
}