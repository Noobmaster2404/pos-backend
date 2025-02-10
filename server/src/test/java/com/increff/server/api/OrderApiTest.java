package com.increff.server.api;

import static org.junit.Assert.*;

import java.time.ZonedDateTime;
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

public class OrderApiTest extends AbstractUnitTest {

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
        product.setImagePath("test.jpg");
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
    public void testAddOrder() throws ApiException {
        Order order = createTestOrder(100.0);
        OrderItem item = createTestOrderItem(order, 1, 100.0);
        order.getOrderItems().add(item);
        
        Order added = orderApi.addOrder(order);
        assertNotNull(added.getOrderId());
        assertEquals(1, added.getOrderItems().size());
    }

    @Test
    public void testGetOrderById() throws ApiException {
        Order order = createTestOrder(100.0);
        OrderItem item = createTestOrderItem(order, 1, 100.0);
        order.getOrderItems().add(item);
        Order added = orderApi.addOrder(order);
        
        Order retrieved = orderApi.getOrderById(added.getOrderId());
        assertNotNull(retrieved);
        assertEquals(added.getOrderId(), retrieved.getOrderId());
    }

    @Test(expected = ApiException.class)
    public void testGetNonexistentOrder() throws ApiException {
        orderApi.getOrderById(999);
    }

    @Test
    public void testGetOrdersByDateRange() throws ApiException {
        Order order1 = createTestOrder(100.0);
        Order order2 = createTestOrder(200.0);
        
        OrderItem item1 = createTestOrderItem(order1, 1, 100.0);
        OrderItem item2 = createTestOrderItem(order2, 2, 100.0);
        
        order1.getOrderItems().add(item1);
        order2.getOrderItems().add(item2);
        
        orderApi.addOrder(order1);
        orderApi.addOrder(order2);
        
        ZonedDateTime startDate = TimeZoneUtil.toUTC(ZonedDateTime.now().minusDays(1));
        ZonedDateTime endDate = TimeZoneUtil.toUTC(ZonedDateTime.now().plusDays(1));
        
        List<Order> orders = orderApi.getOrdersByDateRange(startDate, endDate, 0);
        assertEquals(2, orders.size());
    }

    @Test(expected = ApiException.class)
    public void testInvalidDateRange() throws ApiException {
        ZonedDateTime startDate = TimeZoneUtil.toUTC(ZonedDateTime.now());
        ZonedDateTime endDate = TimeZoneUtil.toUTC(ZonedDateTime.now().minusDays(1));
        
        orderApi.getOrdersByDateRange(startDate, endDate, 0);
    }

    @Test
    public void testUpdateOrder() throws ApiException {
        Order order = createTestOrder(100.0);
        OrderItem item = createTestOrderItem(order, 1, 100.0);
        order.getOrderItems().add(item);
        Order added = orderApi.addOrder(order);
        
        added.setInvoiceGenerated(true);
        added.setInvoicePath("test/path.pdf");
        
        Order updated = orderApi.updateOrder(added);
        assertTrue(updated.getInvoiceGenerated());
        assertEquals("test/path.pdf", updated.getInvoicePath());
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonexistentOrder() throws ApiException {
        Order order = createTestOrder(100.0);
        order.setOrderId(999);
        orderApi.updateOrder(order);
    }

    @Test
    public void testOrderWithMultipleItems() throws ApiException {
        Order order = createTestOrder(300.0);
        OrderItem item1 = createTestOrderItem(order, 1, 100.0);
        OrderItem item2 = createTestOrderItem(order, 2, 100.0);
        
        order.getOrderItems().add(item1);
        order.getOrderItems().add(item2);
        
        Order added = orderApi.addOrder(order);
        assertEquals(2, added.getOrderItems().size());
    }
} 