package com.increff.server.dto;

import com.increff.commons.model.*;
import com.increff.server.entity.*;
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
    public static Product convertToProduct(ProductForm form, Client client){
        Product product = new Product();
        product.setBarcode(form.getBarcode());
        product.setProductName(form.getProductName());
        product.setClient(client);
        product.setImagePath(form.getImagePath());
        product.setMrp(form.getMrp());
        return product;
    }

    public static List<Product> convertToProduct(Map<ProductForm, Client> productClientMap){
        return productClientMap.entrySet().stream()
                .map(element -> convertToProduct(element.getKey(), element.getValue()))
                .collect(Collectors.toList());
    }

    public static ProductData convertToProductData(Product product){
        ProductData data = new ProductData();
        data.setProductId(product.getProductId());
        data.setBarcode(product.getBarcode());
        data.setProductName(product.getProductName());
        data.setClientName(product.getClient().getClientName());
        data.setClientId(product.getClient().getClientId());
        data.setImagePath(product.getImagePath());
        data.setMrp(product.getMrp());
        return data;
    }

    public static List<ProductData> convertToProductData(List<Product> products) {
        return products.stream()
                .map(ConversionHelper::convertToProductData)
                .collect(Collectors.toList());
    }

    // Inventory conversions
    public static Inventory convertToInventory(InventoryForm form, Product product) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setBarcode(product.getBarcode());
        inventory.setQuantity(form.getQuantity());
        return inventory;
    }

    public static List<Inventory> convertToInventory(Map<InventoryForm, Product> inventoryProductMap){
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
    public static OrderItem convertToOrderItem(OrderItemForm form, Product product, Order order){
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(form.getQuantity());
        orderItem.setSellingPrice(form.getSellingPrice());
        orderItem.setOrder(order);
        return orderItem;
    }

    public static OrderData convertToOrderData(Order order){
        OrderData data = new OrderData();
        data.setOrderId(order.getOrderId());
        data.setOrderTime(order.getOrderTime());
        data.setOrderTotal(order.getOrderTotal());
        data.setInvoicePath(order.getInvoicePath());
        
        List<OrderItemData> itemDataList = order.getOrderItems()
                .stream()
                .map(item -> {
                    return convertToOrderItemData(item);
                })
                .collect(Collectors.toList());
        data.setOrderItems(itemDataList);
        
        return data;
    }

    public static OrderItemData convertToOrderItemData(OrderItem item){
        OrderItemData data = new OrderItemData();
        data.setOrderItemId(item.getOrderItemId());
        data.setProductId(item.getProduct().getProductId());
        data.setProductName(item.getProduct().getProductName());
        data.setBarcode(item.getProduct().getBarcode());
        data.setQuantity(item.getQuantity());
        data.setSellingPrice(item.getSellingPrice());
        data.setItemTotal(item.getQuantity() * item.getSellingPrice());
        return data;
    }

    public static DailySalesData convertToDailySalesData(DailySales report) {
        DailySalesData data = new DailySalesData();
        data.setDate(report.getDate());
        data.setInvoicedOrderCount(report.getInvoicedOrders());
        data.setTotalItems(report.getTotalItems());
        data.setTotalRevenue(report.getTotalRevenue());
        return data;
    }
    public static List<DailySalesData> convertToDailySalesData(List<DailySales> reports) {
        return reports.stream()
                .map(ConversionHelper::convertToDailySalesData)
                .collect(Collectors.toList());
    }
}
