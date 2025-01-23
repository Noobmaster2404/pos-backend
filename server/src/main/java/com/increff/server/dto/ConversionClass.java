package com.increff.server.dto;

import com.increff.commons.model.*;
import com.increff.server.entity.*;
import com.increff.commons.exception.ApiException;
import java.util.Objects;

public class ConversionClass {

    // Client conversions
    public static Client convert(ClientForm form) throws ApiException {
        try {
            Client client = new Client();
            client.setClientName(form.getClientName());
            client.setPhone(form.getPhone());
            client.setEmail(form.getEmail());
            client.setEnabled(form.getEnabled());
            return client;
        } catch (Exception e) {
            throw new ApiException("Error converting client form: " + e.getMessage());
        }
    }

    public static ClientData convert(Client client) throws ApiException {
        try {
            ClientData data = new ClientData();
            data.setClientId(client.getClientId());
            data.setClientName(client.getClientName());
            data.setPhone(client.getPhone());
            data.setEmail(client.getEmail());
            data.setEnabled(client.getEnabled());
            return data;
        } catch (Exception e) {
            throw new ApiException("Error converting client to data: " + e.getMessage());
        }
    }

    // Product conversions
    public static Product convert(ProductForm form, Client client) throws ApiException {
        try {
            Product product = new Product();
            product.setBarcode(form.getBarcode());
            product.setProductName(form.getProductName());
            if (Objects.isNull(client)) {
                throw new ApiException("Client not found with ID: " + form.getClientId());
            }
            product.setClient(client);
            product.setImagePath(form.getImagePath());
            product.setMrp(form.getMrp());
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
            data.setBarcode(product.getBarcode());
            data.setProductName(product.getProductName());
            if (Objects.isNull(product.getClient())) {
                throw new ApiException("Product has no associated client");
            }
            data.setClientId(product.getClient().getClientId());
            data.setImagePath(product.getImagePath());
            data.setMrp(product.getMrp());
            return data;
        } catch (Exception e) {
            throw new ApiException("Error converting product to data: " + e.getMessage());
        }
    }

    // Inventory conversions
    public static Inventory convert(InventoryForm form, Product product) throws ApiException {
        try {
            Inventory inventory = new Inventory();
            inventory.setProduct(product);
            inventory.setBarcode(product.getBarcode());
            inventory.setQuantity(form.getQuantity());
            return inventory;
        } catch (Exception e) {
            throw new ApiException("Error converting inventory form: " + e.getMessage());
        }
    }

    public static InventoryData convert(Inventory inventory) throws ApiException {
        try {
            InventoryData data = new InventoryData();
            data.setProductId(inventory.getProduct().getProductId());
            data.setQuantity(inventory.getQuantity());
            data.setBarcode(inventory.getBarcode());
            return data;
        } catch (Exception e) {
            throw new ApiException("Error converting inventory to data: " + e.getMessage());
        }
    }
}