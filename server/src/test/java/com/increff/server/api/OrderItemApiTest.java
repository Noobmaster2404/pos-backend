package com.increff.server.api;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.server.entity.Order;
import com.increff.server.entity.OrderItem;
import com.increff.server.entity.Product;
import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;
import com.increff.commons.util.TimeZoneUtil;
import java.time.ZonedDateTime;

public class OrderItemApiTest extends AbstractUnitTest {

    @Autowired
    private OrderItemApi orderItemApi;

    @Autowired
    private OrderApi orderApi;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ClientApi clientApi;

    private Product testProduct;
    private Client testClient;

    @Before
    public void setUp() throws ApiException {
        // Create a test client
        Client client = new Client();
        client.setClientName("Test Client");
        client.setPhone("1234567890");
        client.setEmail("test@test.com");
        client.setEnabled(true);
        testClient = clientApi.addClient(client);

        // Create a test product
        Product product = new Product();
        product.setBarcode("test_barcode");
        product.setProductName("Test Product");
        product.setClient(testClient);
        product.setMrp(100.0);
        testProduct = productApi.addProduct(product);
    }

    private Order createTestOrder(Double total) {
        Order order = new Order();
        order.setOrderTime(TimeZoneUtil.toUTC(ZonedDateTime.now()));
        order.setOrderTotal(total);
        order.setInvoiceGenerated(false);
        order.setOrderItems(new ArrayList<>());
        return order;
    }

    private OrderItem createTestOrderItem(Order order, Integer quantity, Double sellingPrice) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(testProduct);
        item.setQuantity(quantity);
        item.setSellingPrice(sellingPrice);
        return item;
    }

    @Test
    public void testGetOrderItemsByOrderId() throws ApiException {
        Order order = createTestOrder(100.0);
        OrderItem item1 = createTestOrderItem(order, 1, 100.0);
        OrderItem item2 = createTestOrderItem(order, 2, 100.0);
        order.getOrderItems().add(item1);
        order.getOrderItems().add(item2);
        
        Order addedOrder = orderApi.addOrder(order);
        
        List<OrderItem> items = orderItemApi.getOrderItemsByOrderId(addedOrder.getOrderId());
        assertEquals(2, items.size());
    }

    @Test
    public void testGetOrderItemsByOrderIdEmpty() throws ApiException {
        Order order = createTestOrder(100.0);
        Order addedOrder = orderApi.addOrder(order);
        
        List<OrderItem> items = orderItemApi.getOrderItemsByOrderId(addedOrder.getOrderId());
        assertTrue(items.isEmpty());
    }

    @Test(expected = ApiException.class)
    public void testGetOrderItemsByNonexistentOrderId() throws ApiException {
        // Attempt to retrieve order items for a nonexistent order ID
        orderItemApi.getOrderItemsByOrderId(999); // Nonexistent order ID
    }

    @Test
    public void testOrderItemsWithMultipleProducts() throws ApiException {
        // Create a second product
        Product product2 = new Product();
        product2.setBarcode("test_barcode_2");
        product2.setProductName("Test Product 2");
        product2.setClient(testClient);
        product2.setMrp(200.0);
        Product testProduct2 = productApi.addProduct(product2);

        Order order = createTestOrder(300.0);
        
        // Create order items with different products
        OrderItem item1 = createTestOrderItem(order, 1, 100.0);
        
        OrderItem item2 = new OrderItem();
        item2.setOrder(order);
        item2.setProduct(testProduct2);
        item2.setQuantity(2);
        item2.setSellingPrice(200.0);
        
        order.getOrderItems().add(item1);
        order.getOrderItems().add(item2);
        
        Order addedOrder = orderApi.addOrder(order);
        
        List<OrderItem> items = orderItemApi.getOrderItemsByOrderId(addedOrder.getOrderId());
        assertEquals(2, items.size());
    }

    @Test
    public void testOrderItemDetails() throws ApiException {
        Order order = createTestOrder(100.0);
        OrderItem item = createTestOrderItem(order, 1, 100.0);
        order.getOrderItems().add(item);
        
        Order addedOrder = orderApi.addOrder(order);
        
        List<OrderItem> items = orderItemApi.getOrderItemsByOrderId(addedOrder.getOrderId());
        assertEquals(1, items.size());
        
        OrderItem retrievedItem = items.get(0);
        assertEquals(testProduct.getProductId(), retrievedItem.getProduct().getProductId());
        assertEquals(Integer.valueOf(1), retrievedItem.getQuantity());
        assertEquals(Double.valueOf(100.0), retrievedItem.getSellingPrice());
    }
} 