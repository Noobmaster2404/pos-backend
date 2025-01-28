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

import java.util.ArrayList;
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

    public Product addProduct(Product product) throws ApiException {
        Product savedProduct = productApi.addProduct(product);

        Inventory inventory = new Inventory();
        inventory.setProduct(savedProduct);
        inventory.setBarcode(savedProduct.getBarcode());
        inventory.setQuantity(0);
        inventoryApi.addInventory(inventory);
        
        return savedProduct;
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productApi.getAllProducts();
    }

    // @Transactional(readOnly = true)
    // public Product getProductById(Integer id) throws ApiException {
    //     return productApi.getProductById(id);
    // }

    @Transactional(rollbackFor = ApiException.class)
    public Product updateProductByBarcode(String barcode, Product product) throws ApiException {
        validateProduct(product);
        validateClient(product.getClient().getClientId());
        return productApi.updateProductByBarcode(barcode, product);
    }

    @Transactional(rollbackFor = ApiException.class)
    public List<Product> bulkAddProducts(List<Product> products) throws ApiException {
        List<Product> addedProducts = new ArrayList<>();
        for (Product product : products) {
            addedProducts.add(addProduct(product));
        }
        return addedProducts;
    }

    private void validateProduct(Product product) throws ApiException {
        if (Objects.isNull(product)) {
            throw new ApiException("Product cannot be null");
        }
        if (Objects.isNull(product.getBarcode()) || product.getBarcode().isEmpty()) {
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
        Client client = clientApi.getClientById(clientId);
        if (Objects.isNull(client)) {
            throw new ApiException("Client with id " + clientId + " does not exist");
        }
    }

    public List<Product> getProductsByName(String productName) throws ApiException {
        return productApi.getProductsByName(productName);
    }

    public Product getProductByBarcode(String barcode) throws ApiException {
        return productApi.getProductByBarcode(barcode);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByClientId(Integer clientId) throws ApiException {
        // Verify client exists
        clientApi.getClientById(clientId);
        return productApi.getProductsByClientId(clientId);
    }
}
