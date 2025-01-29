package com.increff.server.dto;

import com.increff.commons.model.*;
import com.increff.server.entity.*;
import com.increff.commons.exception.ApiException;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

public class ConversionHelper {

    // Client conversions
    public static Client convertToClient(ClientForm form){
        Client client = new Client();
        client.setClientName(form.getClientName());
        client.setPhone(form.getPhone());
        client.setEmail(form.getEmail());
        client.setEnabled(form.getEnabled());
        return client;
    }

    public static ClientData convertToClientData(Client client){
        ClientData data = new ClientData();
        data.setClientId(client.getClientId());
        data.setClientName(client.getClientName());
        data.setPhone(client.getPhone());
        data.setEmail(client.getEmail());
        data.setEnabled(client.getEnabled());
        return data;
    }

    public static List<ClientData> convertToClientData(List<Client> clients){
        return clients.stream()
                .map(ConversionHelper::convertToClientData)
                .collect(Collectors.toList());
    }

    // Product conversions
    public static Product convertToProduct(ProductForm form, Client client) throws ApiException {
        try {
            Product product = new Product();
            product.setBarcode(form.getBarcode());
            product.setProductName(form.getProductName());
            if (Objects.isNull(client)) {
                throw new ApiException("Client not found with name: " + form.getClientName());
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

    public static ProductData convertToProductData(Product product) throws ApiException {
        ProductData data = new ProductData();
        data.setProductId(product.getProductId());
        data.setBarcode(product.getBarcode());
        data.setProductName(product.getProductName());
        data.setClientName(product.getClient().getClientName());
        data.setImagePath(product.getImagePath());
        data.setMrp(product.getMrp());
        return data;
    }

    // Inventory conversions
    public static Inventory convertToInventory(InventoryForm form, Product product){
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setBarcode(product.getBarcode());
        inventory.setQuantity(form.getQuantity());
        return inventory;
    }

    public static List<Inventory> convertToInventory(Map<InventoryForm, Product> inventoryProductMap) throws ApiException {
        return inventoryProductMap.entrySet().stream()
                .map(element -> convertToInventory(element.getKey(), element.getValue()))
                .collect(Collectors.toList());
    }

    public static InventoryData convertToInventoryData(Inventory inventory){
        InventoryData data = new InventoryData();
        data.setProductId(inventory.getProduct().getProductId());
        data.setQuantity(inventory.getQuantity());
        data.setBarcode(inventory.getBarcode());
        return data;
    }

    public static List<InventoryData> convertToInventoryData(List<Inventory> inventories){
        return inventories.stream()
                .map(ConversionHelper::convertToInventoryData)
                .collect(Collectors.toList());
    }

    // Order conversions
    public static OrderData convertToOrderData(Order order) throws ApiException {
        try {
            OrderData data = new OrderData();
            data.setOrderId(order.getOrderId());
            data.setOrderTime(order.getOrderTime());
            data.setOrderTotal(order.getOrderTotal());
            data.setInvoicePath(order.getInvoicePath());
            
            List<OrderItemData> itemDataList = order.getOrderItems()
                    .stream()
                    .map(item -> {
                        try {
                            return convertToOrderItemData(item);
                        } catch (ApiException e) {
                            throw new RuntimeException(e); //TODO: catching api throwing runtime?
                        }
                    })
                    .collect(Collectors.toList());
            data.setOrderItems(itemDataList);
            
            return data;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ApiException) {
                throw (ApiException) e.getCause();
            }
            throw new ApiException("Error converting order to data: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException("Error converting order to data: " + e.getMessage());
        }
    }

    public static OrderItemData convertToOrderItemData(OrderItem item) throws ApiException {
        try {
            OrderItemData data = new OrderItemData();
            data.setOrderItemId(item.getOrderItemId());
            data.setProductId(item.getProduct().getProductId());
            data.setProductName(item.getProduct().getProductName());
            data.setBarcode(item.getProduct().getBarcode());
            data.setQuantity(item.getQuantity());
            data.setSellingPrice(item.getSellingPrice());
            data.setItemTotal(item.getQuantity() * item.getSellingPrice());
            return data;
        } catch (Exception e) {
            throw new ApiException("Error converting order item to data: " + e.getMessage());
        }
    }
}