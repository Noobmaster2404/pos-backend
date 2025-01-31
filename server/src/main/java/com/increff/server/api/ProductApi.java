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

    @Transactional(rollbackFor = Exception.class)
    public Product addProduct(Product product) throws ApiException {
        // checkValid(product);
        Product existing = dao.selectByBarcode(product.getBarcode());
        if (Objects.nonNull(existing)) {
            throw new ApiException("Product with barcode '" + product.getBarcode() + "' already exists");
        }
        dao.insert(product);
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return dao.selectAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public Product updateProductByBarcode(String barcode, Product product) throws ApiException {
        // checkValid(product);
        Product existingProduct = dao.selectByBarcode(barcode);
        if (Objects.isNull(existingProduct)) {
            throw new ApiException("Product with given barcode does not exist");
        }
        
        if (!existingProduct.getBarcode().equals(product.getBarcode())) {
            Product duplicateCheck = dao.selectByBarcode(product.getBarcode());
            if (Objects.nonNull(duplicateCheck)) {
                throw new ApiException("Product with barcode '" + product.getBarcode() + "' already exists");
            }
        }
        
        existingProduct.setBarcode(product.getBarcode());
        existingProduct.setProductName(product.getProductName());
        existingProduct.setClient(product.getClient());
        existingProduct.setImagePath(product.getImagePath());
        existingProduct.setMrp(product.getMrp());
        dao.update(existingProduct);
        return existingProduct;
    }

    // @Transactional(readOnly = true)
    // public Product getProductById(Integer productId) throws ApiException {
    //     Product product = dao.select(productId);
    //     if (Objects.isNull(product)) {
    //         throw new ApiException("Product with id " + productId + " not found");
    //     }
    //     return product;
    // }

    @Transactional(readOnly = true)
    public List<Product> getProductsByName(String productName) throws ApiException {
        if (StringUtils.isEmpty(productName)) {
            return getAllProducts();
        }
        return dao.selectByNamePrefix(productName);
    }

    @Transactional(readOnly = true)
    public Product getProductByBarcode(String barcode) throws ApiException {
        Product product = dao.selectByBarcode(barcode);
        if(Objects.nonNull(product)){
            return product;
        }else{
            throw new ApiException("Product with barcode '" + barcode + "' does not exist");
        }
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByClientId(Integer clientId) {
        return dao.selectByClientId(clientId);
    }

    // private void checkValid(Product product) throws ApiException {
    //     if (Objects.isNull(product)) {
    //         throw new ApiException("Product cannot be null");
    //     }
        
    //     // Barcode validation
    //     if (StringUtils.isEmpty(product.getBarcode())) {
    //         throw new ApiException("Product barcode cannot be empty");
    //     }
    //     if (product.getBarcode().length() > 255) {
    //         throw new ApiException("Product barcode cannot exceed 255 characters");
    //     }
        
    //     // Name validation
    //     if (StringUtils.isEmpty(product.getProductName())) {
    //         throw new ApiException("Product name cannot be empty");
    //     }
    //     if (product.getProductName().length() > 255) {
    //         throw new ApiException("Product name cannot exceed 255 characters");
    //     }
        
    //     // Client validation
    //     if (Objects.isNull(product.getClient()) || Objects.isNull(product.getClient().getClientId())) {
    //         throw new ApiException("Client reference cannot be empty");
    //     }
        
    //     // Image path validation (optional field)
    //     if (Objects.nonNull(product.getImagePath()) && 
    //         product.getImagePath().length() > 1000) {
    //         throw new ApiException("Product image path cannot exceed 1000 characters");
    //     }
        
    //     // MRP validation
    //     if (Objects.isNull(product.getMrp())) {
    //         throw new ApiException("Product MRP cannot be null");
    //     }
    //     if (product.getMrp() <= 0) {
    //         throw new ApiException("Product MRP must be positive");
    //     }
    // }
}
