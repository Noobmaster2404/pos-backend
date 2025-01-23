package com.increff.server.flow;

import com.increff.commons.exception.ApiException;
import com.increff.server.entity.Product;
import com.increff.server.entity.Client;
import com.increff.server.api.ProductApi;
import com.increff.server.api.ClientApi;
import com.increff.server.entity.Inventory;
import com.increff.server.api.InventoryApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackFor = ApiException.class)
public class ProductFlow {

    @Autowired
    private ProductApi productApi;
    
    @Autowired
    private ClientApi clientApi;


    @Autowired
    private InventoryApi inventoryApi;

    public Product add(Product product) throws ApiException {
        // First create the product and get the saved entity with ID
        Product savedProduct = productApi.add(product);
        
        // Then create initial inventory with 0 quantity
        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setProductBarcode(savedProduct.getProductBarcode());
        inventory.setQuantity(0);
        inventoryApi.add(inventory);
        
        return savedProduct;
    }

    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return productApi.getAll();
    }

    @Transactional(readOnly = true)
    public Product get(Integer id) throws ApiException {
        return productApi.get(id);
    }

    @Transactional(rollbackFor = ApiException.class)
    public Product update(Integer id, Product product) throws ApiException {
        validateProduct(product);
        validateClient(product.getClient().getClientId());
        return productApi.update(id, product);
    }

    @Transactional(rollbackFor = ApiException.class)
    public void bulkAdd(List<Product> products) throws ApiException {
        for (Product product : products) {
            add(product);
        }
    }

    private void validateProduct(Product product) throws ApiException {
        if (Objects.isNull(product)) {
            throw new ApiException("Product cannot be null");
        }
        if (Objects.isNull(product.getProductBarcode()) || product.getProductBarcode().isEmpty()) {
            throw new ApiException("Product barcode cannot be empty");
        }
        if (Objects.isNull(product.getProductName()) || product.getProductName().isEmpty()) {
            throw new ApiException("Product name cannot be empty");
        }
        if (Objects.isNull(product.getClient()) || Objects.isNull(product.getClient().getClientId())) {
            throw new ApiException("Client ID cannot be empty");
        }
    }

    private void validateClient(Integer clientId) throws ApiException {
        Client client = clientApi.get(clientId);
        if (Objects.isNull(client)) {
            throw new ApiException("Client with id " + clientId + " does not exist");
        }
    }
}
