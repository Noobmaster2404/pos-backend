package com.increff.server.flow;

import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Product;
import com.increff.server.api.ProductApi;
import com.increff.server.api.ClientApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProductFlow {

    @Autowired
    private ProductApi productApi;
    
    @Autowired
    private ClientApi clientApi;

    public Product addProduct(Product product) throws ApiException {
        return productApi.addProduct(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts(Integer page) {
        return productApi.getAllProducts(page);
    }

    public Product updateProductById(Integer productId, Product product) throws ApiException {
        return productApi.updateProductById(productId, product);
    }

    public List<Product> bulkAddProducts(List<Product> products) throws ApiException {
        List<Product> addedProducts = new ArrayList<>();
        for (Product product : products) {
            addedProducts.add(addProduct(product));
        }
        return addedProducts;
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByNamePrefix(String productName, Integer page) throws ApiException {
        return productApi.getCheckProductsByNamePrefix(productName, page);
    }

    @Transactional(readOnly = true)
    public Product getProductByBarcode(String barcode) throws ApiException {
        return productApi.getCheckProductByBarcode(barcode);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByClientId(Integer clientId, Integer page) throws ApiException {
        clientApi.getCheckClientById(clientId);
        
        return productApi.getCheckProductsByClientId(clientId, page);
    }
}
