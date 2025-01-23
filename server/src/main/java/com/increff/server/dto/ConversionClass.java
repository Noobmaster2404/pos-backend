package com.increff.server.dto;

import com.increff.commons.model.*;
import com.increff.server.entity.*;
import com.increff.commons.exception.ApiException;
import java.util.Objects;

public class ConversionClass {

    // Client conversions
    public static Client convert(ClientForm form) {
        Client client = new Client();
        client.setClientName(form.getClientName());
        client.setPhone(form.getPhone());
        client.setEmail(form.getEmail());
        client.setEnabled(form.getEnabled());
        return client;
    }

    public static ClientData convert(Client client) {
        ClientData data = new ClientData();
        data.setClientId(client.getClientId());
        data.setClientName(client.getClientName());
        data.setPhone(client.getPhone());
        data.setEmail(client.getEmail());
        data.setEnabled(client.getEnabled());
        return data;
    }

    // Product conversions
    public static Product convert(ProductForm form, Client client) throws ApiException {
        try {
            Product product = new Product();
            product.setProductBarcode(form.getProductBarcode());
            product.setProductName(form.getProductName());
            if (Objects.isNull(client)) {
                throw new ApiException("Client not found with ID: " + form.getClientId());
            }
            product.setClient(client);
            product.setProductImagePath(form.getProductImagePath());
            product.setProductMrp(form.getProductMrp());
            return product;
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Error converting product form: " + e.getMessage());
        }
    }

    public static ProductData convert(Product product) throws ApiException {
        try {
            ProductData data = new ProductData();
            data.setProductId(product.getProductId());
            data.setProductBarcode(product.getProductBarcode());
            data.setProductName(product.getProductName());
            if (Objects.isNull(product.getClient())) {
                throw new ApiException("Product has no associated client");
            }
            data.setClientId(product.getClient().getClientId());
            data.setProductImagePath(product.getProductImagePath());
            data.setProductMrp(product.getProductMrp());
            return data;
        } catch (Exception e) {
            throw new ApiException("Error converting product to data: " + e.getMessage());
        }
    }

    // Inventory conversions
    public static Inventory convert(InventoryForm form, Product product) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setProductBarcode(product.getProductBarcode());
        inventory.setQuantity(form.getQuantity());
        return inventory;
    }

    public static InventoryData convert(Inventory inventory) {
        InventoryData data = new InventoryData();
        data.setInventoryId(inventory.getInventoryId());
        data.setProductId(inventory.getProduct().getProductId());
        data.setQuantity(inventory.getQuantity());
        data.setProductBarcode(inventory.getProductBarcode());
        return data;
    }
}