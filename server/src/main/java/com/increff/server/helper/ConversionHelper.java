package com.increff.server.helper;

import com.increff.commons.model.*;
import com.increff.server.entity.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.time.ZonedDateTime;

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

    public static List<Product> convertToProduct(List<ProductForm> forms, List<Client> clients){
        Map<Integer, Client> clientMap = clients.stream()
            .collect(Collectors.toMap(Client::getClientId, client -> client));
        return forms.stream()
                .map(form -> convertToProduct(form, clientMap.get(form.getClientId())))
                .collect(Collectors.toList());
    }

    public static ProductData convertToProductData(Product product,String clientName){
        ProductData data = new ProductData();
        data.setProductId(product.getProductId());
        data.setBarcode(product.getBarcode());
        data.setProductName(product.getProductName());
        data.setClientName(clientName);
        data.setClientId(product.getClient().getClientId());
        data.setImagePath(product.getImagePath());
        data.setMrp(product.getMrp());
        return data;
    }

    public static List<ProductData> convertToProductData(List<Product> products, String clientName){
        return products.stream()
                .map(product -> convertToProductData(product, clientName))
                .collect(Collectors.toList());
    }

    public static List<ProductData> convertToProductData(List<Product> products, Map<Integer, String> clientNamesMap) {
        Map<Product, String> productClientNameMap = products.stream()
            .collect(Collectors.toMap(
                product -> product,
                product -> clientNamesMap.get(product.getClient().getClientId())
            ));
        return productClientNameMap.entrySet().stream()
                .map(element -> convertToProductData(element.getKey(), element.getValue()))
                .collect(Collectors.toList());
    }

    public static List<ProductData> convertToProductData(List<Product> products, List<Client> clients) {
        Map<Integer, Client> clientMap = clients.stream()
            .collect(Collectors.toMap(Client::getClientId, client -> client));
        return products.stream()
                .map(product -> convertToProductData(product, clientMap.get(product.getClient().getClientId()).getClientName()))
                .collect(Collectors.toList());
    }

    // Inventory conversions
    public static Inventory convertToInventory(InventoryForm form, Product product) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(form.getQuantity());
        return inventory;
    }

    public static List<Inventory> convertToInventory(List<InventoryForm> forms, List<Product> products){
        Map<String, Product> barcodeProductMap = products.stream()
            .collect(Collectors.toMap(Product::getBarcode, product -> product));
        return forms.stream()
                .map(form -> convertToInventory(form, barcodeProductMap.get(form.getBarcode())))
                .collect(Collectors.toList());
    }

    public static InventoryData convertToInventoryData(Inventory inventory, String barcode){
        InventoryData data = new InventoryData();
        data.setProductId(inventory.getProduct().getProductId());
        data.setQuantity(inventory.getQuantity());
        data.setBarcode(barcode);
        data.setInventoryId(inventory.getInventoryId());
        return data;
    }

    public static List<InventoryData> convertToInventoryData(List<Inventory> inventories, Map<Integer, String> productIdBarcodeMap){
        return inventories.stream()
                .map(inventory -> convertToInventoryData(inventory, 
                    productIdBarcodeMap.get(inventory.getProduct().getProductId())))
                .collect(Collectors.toList());
    }

    public static List<InventoryData> convertToInventoryData(List<Inventory> inventories, List<Product> products){
        Map<Integer, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getProductId, product -> product));
        return inventories.stream()
                .map(inventory -> convertToInventoryData(inventory, 
                    productMap.get(inventory.getProduct().getProductId()).getBarcode()))
                .collect(Collectors.toList());
    }

    // Order conversions
    public static OrderData convertToOrderData(Order order, List<OrderItem> orderItems, List<Product> products) {
        Map<Integer, Product> productMap = products.stream()
            .collect(Collectors.toMap(Product::getProductId, product -> product));

        OrderData data = new OrderData();
        data.setOrderId(order.getOrderId());
        data.setOrderTime(order.getOrderTime());
        data.setOrderTotal(order.getOrderTotal());
        data.setInvoicePath(order.getInvoicePath());
        data.setInvoiceGenerated(order.getInvoiceGenerated());
        
        List<OrderItemData> itemDataList = orderItems.stream()
            .map(item -> convertToOrderItemData(item, productMap.get(item.getProduct().getProductId())))
            .collect(Collectors.toList());

        data.setOrderItems(itemDataList);
        
        return data;
    }

    public static OrderItemData convertToOrderItemData(OrderItem item, Product product) {
        OrderItemData data = new OrderItemData();
        data.setOrderItemId(item.getOrderItemId());
        data.setProductId(product.getProductId());
        data.setProductName(product.getProductName());
        data.setBarcode(product.getBarcode());
        data.setQuantity(item.getQuantity());
        data.setSellingPrice(item.getSellingPrice());
        data.setItemTotal(item.getQuantity() * item.getSellingPrice());
        return data;
    }

    public static OrderData convertToOrderData(Order order, List<OrderItem> orderItems, Map<Integer, Product> productMap) {
        OrderData data = new OrderData();
        data.setOrderId(order.getOrderId());
        data.setOrderTime(order.getOrderTime());
        data.setOrderTotal(order.getOrderTotal());
        data.setInvoicePath(order.getInvoicePath());
        data.setInvoiceGenerated(order.getInvoiceGenerated());
        data.setOrderItems(orderItems.stream()
            .map(item -> convertToOrderItemData(item, productMap.get(item.getProduct().getProductId())))
            .collect(Collectors.toList()));
        return data;
    }

    public static List<OrderData> convertToOrderData(List<Order> orders, Map<Integer, List<OrderItem>> orderItemsMap, Map<Integer, Product> productMap) {
        return orders.stream()
            .map(order -> convertToOrderData(order, orderItemsMap.get(order.getOrderId()), productMap))
            .collect(Collectors.toList());
    }

    public static OrderItem convertToOrderItem(OrderItemForm form, Product product, Order order) {
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(form.getQuantity());
        item.setSellingPrice(form.getSellingPrice());
        item.setOrder(order);
        // The circular dependency is not an issue because:
        // We set the Order reference on each OrderItem before persistence
        // JPA/Hibernate handles the actual foreign key assignment after the Order ID is generated
        //so no need to persist order first and then order items
        return item;
    }

    public static Order convertToOrder(OrderForm form, Map<String, Product> barcodeToProduct) {
        Order order = new Order();
        order.setOrderItems(form.getOrderItems().stream()
            .map(itemForm -> convertToOrderItem(itemForm, barcodeToProduct.get(itemForm.getBarcode()), order))
            .collect(Collectors.toList()));
        order.setOrderTotal(form.getOrderItems().stream()
            .mapToDouble(itemForm -> itemForm.getQuantity() * itemForm.getSellingPrice())
            .sum());
        order.setInvoiceGenerated(false);
        order.setInvoicePath(null);
        order.setOrderTime(ZonedDateTime.now());
        return order;
    }

    public static DailySalesData convertToDailySalesData(DailySales report) {
        DailySalesData data = new DailySalesData();
        data.setDate(report.getDate());
        data.setInvoicedOrderCount(report.getInvoicedOrdersCount());
        data.setTotalItems(report.getItemCount());
        data.setTotalRevenue(report.getTotalRevenue());
        return data;
    }
    public static List<DailySalesData> convertToDailySalesData(List<DailySales> reports) {
        return reports.stream()
                .map(ConversionHelper::convertToDailySalesData)
                .collect(Collectors.toList());
    }
}
