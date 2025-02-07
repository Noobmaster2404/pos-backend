package com.increff.server.flow;

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
import com.increff.server.entity.Inventory;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;
import com.increff.commons.util.TimeZoneUtil;
import com.increff.server.api.ProductApi;
import com.increff.server.api.ClientApi;
import com.increff.server.api.InventoryApi;

public class OrderFlowTest extends AbstractUnitTest {

    @Autowired
    private OrderFlow orderFlow;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ClientApi clientApi;

    @Autowired
    private InventoryApi inventoryApi;

    private Product testProduct;
    private Client testClient;
    private Inventory testInventory;

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

        // Create test inventory
        Inventory inventory = new Inventory();
        inventory.setProduct(testProduct);
        inventory.setQuantity(100);
        testInventory = inventoryApi.addInventory(inventory);
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
        
        Order added = orderFlow.addOrder(order);
        assertNotNull(added.getOrderId());
        assertEquals(1, added.getOrderItems().size());
        
        // Verify inventory was reduced
        Inventory updatedInventory = inventoryApi.getCheckInventoryByProductId(testProduct.getProductId());
        assertEquals(Integer.valueOf(99), updatedInventory.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testAddOrderInsufficientInventory() throws ApiException {
        Order order = createTestOrder(100.0);
        OrderItem item = createTestOrderItem(order, 101, 100.0); // More than available inventory
        order.getOrderItems().add(item);
        
        orderFlow.addOrder(order);
    }

    @Test
    public void testGetOrderById() throws ApiException {
        Order order = createTestOrder(100.0);
        OrderItem item = createTestOrderItem(order, 1, 100.0);
        order.getOrderItems().add(item);
        Order added = orderFlow.addOrder(order);
        
        Order retrieved = orderFlow.getOrderById(added.getOrderId());
        assertNotNull(retrieved);
        assertEquals(added.getOrderId(), retrieved.getOrderId());
    }

    @Test
    public void testGetOrdersByDateRange() throws ApiException {
        Order order1 = createTestOrder(100.0);
        Order order2 = createTestOrder(200.0);
        
        OrderItem item1 = createTestOrderItem(order1, 1, 100.0);
        OrderItem item2 = createTestOrderItem(order2, 2, 100.0);
        
        order1.getOrderItems().add(item1);
        order2.getOrderItems().add(item2);
        
        orderFlow.addOrder(order1);
        orderFlow.addOrder(order2);
        
        ZonedDateTime startDate = TimeZoneUtil.toUTC(ZonedDateTime.now().minusDays(1));
        ZonedDateTime endDate = TimeZoneUtil.toUTC(ZonedDateTime.now().plusDays(1));
        
        List<Order> orders = orderFlow.getOrdersByDateRange(startDate, endDate, 0);
        assertEquals(2, orders.size());
    }

    @Test
    public void testMultipleOrdersInventoryUpdate() throws ApiException {
        // First order
        Order order1 = createTestOrder(100.0);
        OrderItem item1 = createTestOrderItem(order1, 10, 100.0);
        order1.getOrderItems().add(item1);
        orderFlow.addOrder(order1);
        
        // Second order
        Order order2 = createTestOrder(200.0);
        OrderItem item2 = createTestOrderItem(order2, 20, 100.0);
        order2.getOrderItems().add(item2);
        orderFlow.addOrder(order2);
        
        Inventory updatedInventory = inventoryApi.getCheckInventoryByProductId(testProduct.getProductId());
        assertEquals(Integer.valueOf(70), updatedInventory.getQuantity()); // 100 - 10 - 20
    }

    @Test
    public void testOrderWithMultipleItems() throws ApiException {
        // Create a second product and its inventory
        Product product2 = new Product();
        product2.setBarcode("test_barcode_2");
        product2.setProductName("Test Product 2");
        product2.setClient(testClient);
        product2.setMrp(200.0);
        product2.setImagePath("test2.jpg");
        Product testProduct2 = productApi.addProduct(product2);

        // Add inventory for second product
        Inventory inventory2 = new Inventory();
        inventory2.setProduct(testProduct2);
        inventory2.setQuantity(100);
        inventoryApi.addInventory(inventory2);

        Order order = createTestOrder(300.0);
        
        // Create order items with different products
        OrderItem item1 = createTestOrderItem(order, 10, 100.0);
        
        OrderItem item2 = new OrderItem();
        item2.setOrder(order);
        item2.setProduct(testProduct2);
        item2.setQuantity(20);
        item2.setSellingPrice(100.0);
        
        order.getOrderItems().add(item1);
        order.getOrderItems().add(item2);
        
        Order added = orderFlow.addOrder(order);
        assertEquals(2, added.getOrderItems().size());
        
        // Verify inventory was reduced for both products
        Inventory updatedInventory1 = inventoryApi.getCheckInventoryByProductId(testProduct.getProductId());
        Inventory updatedInventory2 = inventoryApi.getCheckInventoryByProductId(testProduct2.getProductId());
        
        assertEquals(Integer.valueOf(90), updatedInventory1.getQuantity()); // 100 - 10
        assertEquals(Integer.valueOf(80), updatedInventory2.getQuantity()); // 100 - 20
    }
} 