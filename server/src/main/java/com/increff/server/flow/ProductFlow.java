package com.increff.server.flow;

import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Product;
import com.increff.server.api.ProductApi;
import com.increff.server.api.ClientApi;
import com.increff.server.api.InventoryApi;

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


    @Autowired
    private InventoryApi inventoryApi;

    public Product addProduct(Product product) throws ApiException {
        Product savedProduct = productApi.addProduct(product);
        inventoryApi.initializeInventory(savedProduct);

        return savedProduct;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productApi.getAllProducts();
    }

    public Product updateProductByBarcode(String barcode, Product product) throws ApiException {
        return productApi.updateProductByBarcode(barcode, product);
    }

    public List<Product> bulkAddProducts(List<Product> products) throws ApiException {
        List<Product> addedProducts = new ArrayList<>();
        for (Product product : products) {
            addedProducts.add(addProduct(product));
        }
        return addedProducts;
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByNamePrefix(String productName) throws ApiException {
        return productApi.getCheckProductsByNamePrefix(productName);
    }

    @Transactional(readOnly = true)
    public Product getProductByBarcode(String barcode) throws ApiException {
        return productApi.getCheckProductByBarcode(barcode);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByClientId(Integer clientId) throws ApiException {
        clientApi.getCheckClientById(clientId);
        return productApi.getCheckProductsByClientId(clientId);
    }
}
