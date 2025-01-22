package com.increff.server.api;

import java.util.List;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import com.increff.server.dao.ProductDao;
import com.increff.server.entity.Product;
import com.increff.commons.exception.ApiException;

@Service
public class ProductApi {

    @Autowired
    private ProductDao dao;

    @Transactional(rollbackFor = ApiException.class)
    public Product add(Product product) throws ApiException {
        checkValid(product);
        Product existing = dao.selectByBarcode(product.getProductBarcode());
        if (Objects.nonNull(existing)) {
            throw new ApiException("Product with barcode '" + product.getProductBarcode() + "' already exists");
        }
        dao.insert(product);
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return dao.selectAll();
    }

    @Transactional(rollbackFor = ApiException.class)
    public void update(Integer id, Product product) throws ApiException {
        checkValid(product);
        Product existingProduct = dao.select(id);
        if (Objects.isNull(existingProduct)) {
            throw new ApiException("Product with given ID does not exist");
        }
        
        if (!existingProduct.getProductBarcode().equals(product.getProductBarcode())) {
            Product duplicateCheck = dao.selectByBarcode(product.getProductBarcode());
            if (Objects.nonNull(duplicateCheck)) {
                throw new ApiException("Product with barcode '" + product.getProductBarcode() + "' already exists");
            }
        }
        
        existingProduct.setProductBarcode(product.getProductBarcode());
        existingProduct.setProductName(product.getProductName());
        existingProduct.setClient(product.getClient());
        existingProduct.setProductImagePath(product.getProductImagePath());
        existingProduct.setProductMrp(product.getProductMrp());
        dao.update(existingProduct);
    }

    @Transactional(readOnly = true)
    public Product get(Integer id) throws ApiException {
        Product product = dao.select(id);
        if (Objects.isNull(product)) {
            throw new ApiException("Product with id " + id + " not found");
        }
        return product;
    }

    private void checkValid(Product product) throws ApiException {
        if (Objects.isNull(product)) {
            throw new ApiException("Product cannot be null");
        }
        
        // Barcode validation
        if (StringUtils.isEmpty(product.getProductBarcode())) {
            throw new ApiException("Product barcode cannot be empty");
        }
        if (product.getProductBarcode().length() > 255) {
            throw new ApiException("Product barcode cannot exceed 255 characters");
        }
        
        // Name validation
        if (StringUtils.isEmpty(product.getProductName())) {
            throw new ApiException("Product name cannot be empty");
        }
        if (product.getProductName().length() > 255) {
            throw new ApiException("Product name cannot exceed 255 characters");
        }
        
        // Client validation
        if (Objects.isNull(product.getClient()) || Objects.isNull(product.getClient().getClientId())) {
            throw new ApiException("Client reference cannot be empty");
        }
        
        // Image path validation (optional field)
        if (Objects.nonNull(product.getProductImagePath()) && 
            product.getProductImagePath().length() > 1000) {
            throw new ApiException("Product image path cannot exceed 1000 characters");
        }
        
        // MRP validation
        if (Objects.isNull(product.getProductMrp())) {
            throw new ApiException("Product MRP cannot be null");
        }
        if (product.getProductMrp() <= 0) {
            throw new ApiException("Product MRP must be positive");
        }
    }
}
