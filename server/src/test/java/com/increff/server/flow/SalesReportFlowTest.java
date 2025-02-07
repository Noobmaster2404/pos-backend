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
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;
import com.increff.commons.util.TimeZoneUtil;
import com.increff.commons.model.SalesReportData;
import com.increff.server.api.OrderApi;
import com.increff.server.api.ProductApi;
import com.increff.server.api.ClientApi;

public class SalesReportFlowTest extends AbstractUnitTest {

    @Autowired
    private SalesReportFlow salesReportFlow;

    @Autowired
    private OrderApi orderApi;

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ClientApi clientApi;

    private Client testClient;
    private Product testProduct;

    @Before
    public void setUp() throws ApiException {
        // Create test client
        Client client = new Client();
        client.setClientName("Test Client");
        client.setPhone("1234567890");
        client.setEmail("test@test.com");
        client.setEnabled(true);
        testClient = clientApi.addClient(client);

        // Create test product
        Product product = new Product();
        product.setBarcode("test_barcode");
        product.setProductName("Test Product");
        product.setClient(testClient);
        product.setMrp(100.0);
        testProduct = productApi.addProduct(product);
    }

    private Order createTestOrder(Double total, ZonedDateTime orderTime) {
        Order order = new Order();
        order.setOrderTime(orderTime);
        order.setOrderTotal(total);
        order.setInvoiceGenerated(true);
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
    public void testGenerateSalesReport() throws ApiException {
        ZonedDateTime orderTime = TimeZoneUtil.getCurrentUTCDateTime();
        Order order = createTestOrder(200.0, orderTime);
        OrderItem item = createTestOrderItem(order, 2, 100.0);
        order.getOrderItems().add(item);
        orderApi.addOrder(order);

        List<SalesReportData> report = salesReportFlow.generateSalesReport(
            orderTime.minusDays(1),
            orderTime.plusDays(1),
            null
        );

        assertEquals(1, report.size());
        SalesReportData data = report.get(0);
        assertEquals("test_barcode", data.getBarcode());
        assertEquals(Integer.valueOf(2), data.getQuantity());
        assertEquals(Double.valueOf(200.0), data.getRevenue());
        assertEquals(Double.valueOf(100.0), data.getAverageSellingPrice());
    }

    @Test
    public void testGenerateSalesReportWithClientFilter() throws ApiException {
        // Create another client and product
        Client client2 = new Client();
        client2.setClientName("Test Client 2");
        client2.setPhone("9876543210");
        client2.setEmail("test2@test.com");
        client2.setEnabled(true);
        Client testClient2 = clientApi.addClient(client2);

        Product product2 = new Product();
        product2.setBarcode("test_barcode_2");
        product2.setProductName("Test Product 2");
        product2.setClient(testClient2);
        product2.setMrp(200.0);
        Product testProduct2 = productApi.addProduct(product2);

        // Create orders for both clients
        ZonedDateTime orderTime = TimeZoneUtil.getCurrentUTCDateTime();
        
        // Order for first client
        Order order1 = createTestOrder(200.0, orderTime);
        OrderItem item1 = createTestOrderItem(order1, 2, 100.0);
        order1.getOrderItems().add(item1);
        orderApi.addOrder(order1);

        // Order for second client
        Order order2 = createTestOrder(400.0, orderTime);
        OrderItem item2 = new OrderItem();
        item2.setOrder(order2);
        item2.setProduct(testProduct2);
        item2.setQuantity(2);
        item2.setSellingPrice(200.0);
        order2.getOrderItems().add(item2);
        orderApi.addOrder(order2);

        // Get report for first client only
        List<SalesReportData> report = salesReportFlow.generateSalesReport(
            orderTime.minusDays(1),
            orderTime.plusDays(1),
            testClient.getClientId()
        );

        assertEquals(1, report.size());
        assertEquals("test_barcode", report.get(0).getBarcode());
    }

    @Test
    public void testGenerateSalesReportEmpty() throws ApiException {
        ZonedDateTime currentTime = TimeZoneUtil.getCurrentUTCDateTime();
        List<SalesReportData> report = salesReportFlow.generateSalesReport(
            currentTime,
            currentTime.plusDays(1),
            null
        );
        assertTrue(report.isEmpty());
    }

    @Test
    public void testGenerateSalesReportMultipleItems() throws ApiException {
        ZonedDateTime orderTime = TimeZoneUtil.getCurrentUTCDateTime();
        Order order = createTestOrder(300.0, orderTime);
        
        OrderItem item1 = createTestOrderItem(order, 2, 100.0);
        OrderItem item2 = createTestOrderItem(order, 1, 100.0);
        
        order.getOrderItems().add(item1);
        order.getOrderItems().add(item2);
        
        orderApi.addOrder(order);

        List<SalesReportData> report = salesReportFlow.generateSalesReport(
            orderTime.minusDays(1),
            orderTime.plusDays(1),
            null
        );

        assertEquals(1, report.size());
        assertEquals(Integer.valueOf(3), report.get(0).getQuantity());
        assertEquals(Double.valueOf(300.0), report.get(0).getRevenue());
    }
} 