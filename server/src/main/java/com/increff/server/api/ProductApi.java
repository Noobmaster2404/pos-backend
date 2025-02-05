package com.increff.server.api;

import java.util.List;
import java.util.Objects;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.server.dao.ProductDao;
import com.increff.server.entity.Product;
import com.increff.commons.exception.ApiException;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProductApi {

    @Autowired
    private ProductDao dao;

    public Product addProduct(Product product) throws ApiException {
        Product existingProduct = dao.selectByBarcode(product.getBarcode());
        if (Objects.nonNull(existingProduct)) {
            throw new ApiException("Product with barcode '" + product.getBarcode() + "' already exists");
        }
        dao.insert(product);
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return dao.selectAll();
    }

    public Product updateProductByBarcode(String barcode, Product product) throws ApiException {
        Product existingProduct = dao.selectByBarcode(barcode);
        if (Objects.isNull(existingProduct)) {
            throw new ApiException("Product with given barcode does not exist");
        }
        
        if (!existingProduct.getBarcode().equals(product.getBarcode())) {
            Product existingProductWithNewBarcode = dao.selectByBarcode(product.getBarcode());
            if (Objects.nonNull(existingProductWithNewBarcode)) {
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

    @Transactional(readOnly = true)
    public List<Product> getCheckProductsByNamePrefix(String productName) throws ApiException {
        if (Objects.isNull(productName) || productName.isEmpty()) {
            return getAllProducts();
        }
        List<Product> products = dao.selectByNamePrefix(productName);
        if (products.isEmpty()) {
            throw new ApiException("No products found with name prefix: " + productName);
        }
        return products;
    }

    @Transactional(readOnly = true)
    public Product getCheckProductByBarcode(String barcode) throws ApiException {
        Product product = dao.selectByBarcode(barcode);
        if(Objects.isNull(product)){
            throw new ApiException("Product with barcode '" + barcode + "' does not exist");
        }
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> getCheckProductsByClientId(Integer clientId) throws ApiException {
        List<Product> products = dao.selectByClientId(clientId);
        if (products.isEmpty()) {
            throw new ApiException("No products found for client ID: " + clientId);
        }
        return products;
    }
}
