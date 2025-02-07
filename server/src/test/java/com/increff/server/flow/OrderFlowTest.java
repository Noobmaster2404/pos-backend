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
import com.increff.commons.model.OrderForm;
import com.increff.commons.model.OrderItemForm;
import com.increff.server.dto.OrderDto;
public class OrderFlowTest extends AbstractUnitTest {

    @Autowired
    private OrderFlow orderFlow;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ClientApi clientApi;

    @Autowired
    private InventoryApi inventoryApi;

    @Autowired
    private OrderDto orderDto;

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

        // Create test inventory
        Inventory inventory = new Inventory();
        inventory.setProduct(testProduct);
        inventory.setQuantity(1000); // Set a sufficient initial quantity
        inventoryApi.addInventory(inventory);
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
        assertEquals(Integer.valueOf(999), updatedInventory.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testAddOrderInsufficientInventory() throws ApiException {
        Order order = createTestOrder(100.0);
        OrderItem item = createTestOrderItem(order, 1001, 100.0); // More than available inventory
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
        // Initial inventory check
        Inventory initialInventory = inventoryApi.getCheckInventoryByProductId(testProduct.getProductId());
        assertEquals(Integer.valueOf(1000), initialInventory.getQuantity());

        // Create first order
        OrderForm order1 = createTestOrderForm(10); // Order 10 items
        orderDto.addOrder(order1);

        // Check inventory after first order
        Inventory afterFirstOrder = inventoryApi.getCheckInventoryByProductId(testProduct.getProductId());
        assertEquals(Integer.valueOf(990), afterFirstOrder.getQuantity()); // 1000 - 10 = 990

        // Create second order
        OrderForm order2 = createTestOrderForm(20); // Order 20 more items
        orderDto.addOrder(order2);

        // Check final inventory
        Inventory finalInventory = inventoryApi.getCheckInventoryByProductId(testProduct.getProductId());
        assertEquals(Integer.valueOf(970), finalInventory.getQuantity()); // 990 - 20 = 970
    }

    @Test
    public void testOrderWithMultipleItems() throws ApiException {
        // Create second product and its inventory
        Product product2 = new Product();
        product2.setBarcode("test_barcode_2");
        product2.setProductName("Test Product 2");
        product2.setClient(testClient);
        product2.setMrp(200.0);
        Product addedProduct2 = productApi.addProduct(product2);

        Inventory inventory2 = new Inventory();
        inventory2.setProduct(addedProduct2);
        inventory2.setQuantity(1000);
        inventoryApi.addInventory(inventory2);

        // Create order with multiple items
        OrderForm orderForm = new OrderForm();
        List<OrderItemForm> items = new ArrayList<>();
        
        OrderItemForm item1 = new OrderItemForm();
        item1.setBarcode("test_barcode");
        item1.setQuantity(10);
        item1.setSellingPrice(90.0); // Set selling price less than or equal to MRP
        items.add(item1);

        OrderItemForm item2 = new OrderItemForm();
        item2.setBarcode("test_barcode_2");
        item2.setQuantity(20);
        item2.setSellingPrice(180.0); // Set selling price less than or equal to MRP
        items.add(item2);

        orderForm.setOrderItems(items);
        orderDto.addOrder(orderForm);

        // Check inventory levels after order
        Inventory inventory1Final = inventoryApi.getCheckInventoryByProductId(testProduct.getProductId());
        assertEquals(Integer.valueOf(990), inventory1Final.getQuantity()); // 1000 - 10 = 990

        Inventory inventory2Final = inventoryApi.getCheckInventoryByProductId(addedProduct2.getProductId());
        assertEquals(Integer.valueOf(980), inventory2Final.getQuantity()); // 1000 - 20 = 980
    }

    private OrderForm createTestOrderForm(int quantity) {
        OrderForm form = new OrderForm();
        List<OrderItemForm> items = new ArrayList<>();
        
        OrderItemForm item = new OrderItemForm();
        item.setBarcode("test_barcode");
        item.setQuantity(quantity);
        item.setSellingPrice(90.0); // Setting a valid price below MRP (100.0)
        items.add(item);
        
        form.setOrderItems(items);
        return form;
    }
} 