package com.increff.server.dto;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.commons.model.SalesReportForm;
import com.increff.commons.model.SalesReportData;
import com.increff.commons.model.DailySalesForm;
import com.increff.commons.model.DailySalesData;
import com.increff.commons.model.OrderForm;
import com.increff.commons.model.OrderItemForm;
import com.increff.commons.model.ClientForm;
import com.increff.commons.model.ProductForm;
import com.increff.commons.model.InventoryForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;
import com.increff.commons.model.OrderData;
import com.increff.server.entity.Order;
import com.increff.server.api.OrderApi;
import com.increff.server.entity.DailySales;
import com.increff.server.dao.SalesReportDao;

public class SalesReportDtoTest extends AbstractUnitTest {

    @Autowired
    private SalesReportDto dto;

    @Autowired
    private OrderDto orderDto;

    @Autowired
    private OrderApi orderApi;

    @Autowired
    private ClientDto clientDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private InventoryDto inventoryDto;

    @Autowired
    private SalesReportDao salesReportDao;

    private Integer testClientId;
    private String testBarcode = "test_barcode";

    @Before
    public void setUp() throws ApiException {
        // Create a test client
        ClientForm clientForm = new ClientForm();
        clientForm.setClientName("Test Client");
        clientForm.setPhone("1234567890");
        clientForm.setEmail("test@test.com");
        testClientId = clientDto.addClient(clientForm).getClientId();

        // Create a test product
        ProductForm productForm = new ProductForm();
        productForm.setBarcode(testBarcode);
        productForm.setProductName("Test Product");
        productForm.setClientId(testClientId);
        productForm.setMrp(100.0);
        productDto.addProduct(productForm);

        // Add inventory for the test product
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(testBarcode);
        inventoryForm.setQuantity(1000);
        inventoryDto.addInventory(inventoryForm);
    }

    private OrderForm createTestOrder(String barcode, Integer quantity, Double sellingPrice) {
        OrderForm orderForm = new OrderForm();
        List<OrderItemForm> items = new ArrayList<>();
        
        OrderItemForm item = new OrderItemForm();
        item.setBarcode(barcode);
        item.setQuantity(quantity);
        item.setSellingPrice(sellingPrice);
        items.add(item);
        
        orderForm.setOrderItems(items);
        return orderForm;
    }

    @Test
    public void testGetSalesReport() throws ApiException {
        // Create an order
        OrderForm orderForm = createTestOrder(testBarcode, 10, 90.0);
        OrderData orderData = orderDto.addOrder(orderForm);
        
        // Get actual order date in IST
        Order order = orderApi.getOrderById(orderData.getOrderId());
        LocalDate orderDate = order.getOrderTime()
            .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
            .toLocalDate();
        
        // Create sales report form with actual order date
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(orderDate);
        form.setEndDate(orderDate);

        // Get report
        List<SalesReportData> report = dto.getSalesReport(form);

        assertFalse("Report should not be empty for order: " + order.getOrderTime(), report.isEmpty());
        assertEquals(1, report.size());
        SalesReportData data = report.get(0);
        assertEquals(testBarcode, data.getBarcode());
        assertEquals("test product", data.getProductName());
        assertEquals("test client", data.getClientName());
        assertEquals(Integer.valueOf(10), data.getQuantity());
        assertEquals(Double.valueOf(900.0), data.getRevenue());
        assertEquals(Double.valueOf(90.0), data.getAverageSellingPrice());
    }

    @Test
    public void testGetSalesReportWithClientFilter() throws ApiException {
        // Create second client and product
        ClientForm clientForm2 = new ClientForm();
        clientForm2.setClientName("Test Client 2");
        clientForm2.setPhone("9876543210");
        clientForm2.setEmail("test2@test.com");
        Integer client2Id = clientDto.addClient(clientForm2).getClientId();

        ProductForm productForm2 = new ProductForm();
        productForm2.setBarcode("test_barcode_2");
        productForm2.setProductName("Test Product 2");
        productForm2.setClientId(client2Id);
        productForm2.setMrp(200.0);
        productDto.addProduct(productForm2);

        InventoryForm inventoryForm2 = new InventoryForm();
        inventoryForm2.setBarcode("test_barcode_2");
        inventoryForm2.setQuantity(2000);
        inventoryDto.addInventory(inventoryForm2);

        // Create orders for both clients
        OrderData order1 = orderDto.addOrder(createTestOrder(testBarcode, 10, 90.0));
        orderDto.addOrder(createTestOrder("test_barcode_2", 20, 180.0));

        // Get actual order date in IST
        Order orderEntity = orderApi.getOrderById(order1.getOrderId());
        LocalDate orderDate = orderEntity.getOrderTime()
            .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
            .toLocalDate();

        // Create sales report form with client filter and actual order date
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(orderDate);
        form.setEndDate(orderDate);
        form.setClientId(testClientId);

        List<SalesReportData> report = dto.getSalesReport(form);

        assertEquals(1, report.size());
        assertEquals(testBarcode, report.get(0).getBarcode());
    }

    @Test
    public void testGetSalesReportEmpty() throws ApiException {
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDate.now().minusDays(2));
        form.setEndDate(LocalDate.now().minusDays(1));

        List<SalesReportData> report = dto.getSalesReport(form);
        assertTrue(report.isEmpty());
    }

    @Test
    public void testGetDailySalesReport() throws ApiException {
        // Create and process some orders first
        OrderData order1 = orderDto.addOrder(createTestOrder(testBarcode, 10, 90.0));
        orderDto.addOrder(createTestOrder(testBarcode, 20, 85.0));
        
        // Get actual order date in IST
        Order orderEntity = orderApi.getOrderById(order1.getOrderId());
        LocalDate orderDate = orderEntity.getOrderTime()
            .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
            .toLocalDate();
        
        // Manually create daily sales record (simulating cron job)
        DailySales dailySales = new DailySales();
        dailySales.setDate(orderEntity.getOrderTime()
            .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
            .truncatedTo(ChronoUnit.DAYS));  // Use IST day boundary
        dailySales.setInvoicedOrdersCount(2);
        dailySales.setItemCount(30);  // 10 + 20
        dailySales.setTotalRevenue(2600.0);  // (10 * 90) + (20 * 85)
        salesReportDao.insert(dailySales);
        
        // Create daily sales form with actual order date
        DailySalesForm form = new DailySalesForm();
        form.setStartDate(orderDate);
        form.setEndDate(orderDate);

        List<DailySalesData> report = dto.getDailySalesReport(form);
        assertFalse("Report should not be empty for date: " + orderDate, report.isEmpty());
        
        // Verify report contents
        assertEquals(1, report.size());
        DailySalesData dailyData = report.get(0);
        assertEquals(orderDate, dailyData.getDate()
            .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
            .toLocalDate());
        assertEquals(Integer.valueOf(2), dailyData.getInvoicedOrderCount());
        assertEquals(Integer.valueOf(30), dailyData.getTotalItems());
        assertEquals(Double.valueOf(2600.0), dailyData.getTotalRevenue(), 0.01);
    }

    @Test(expected = ApiException.class)
    public void testGetSalesReportInvalidDateRange() throws ApiException {
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(LocalDate.now().plusDays(1));
        form.setEndDate(LocalDate.now());
        
        dto.getSalesReport(form);
    }

    @Test(expected = ApiException.class)
    public void testGetDailySalesReportInvalidDateRange() throws ApiException {
        DailySalesForm form = new DailySalesForm();
        form.setStartDate(LocalDate.now().plusDays(1));
        form.setEndDate(LocalDate.now());
        
        dto.getDailySalesReport(form);
    }

    @Test
    public void testGetSalesReportMultipleOrders() throws ApiException {
        // Create multiple orders for the same product
        OrderData order1 = orderDto.addOrder(createTestOrder(testBarcode, 10, 90.0));
        orderDto.addOrder(createTestOrder(testBarcode, 20, 85.0));
        
        // Get actual order date in IST
        Order orderEntity1 = orderApi.getOrderById(order1.getOrderId());
        LocalDate orderDate = orderEntity1.getOrderTime()
            .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
            .toLocalDate();
        
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(orderDate);
        form.setEndDate(orderDate);

        List<SalesReportData> report = dto.getSalesReport(form);

        assertEquals(1, report.size());
        SalesReportData data = report.get(0);
        assertEquals(Integer.valueOf(30), data.getQuantity()); // Total quantity
        assertEquals(2600.0, data.getRevenue(), 0.01); // (10 * 90) + (20 * 85)
        assertEquals(86.67, data.getAverageSellingPrice(), 0.01); // Average price
    }

    @Test
    public void testGetSalesReportDateBoundaries() throws ApiException {
        // Create an order
        OrderForm orderForm = createTestOrder(testBarcode, 10, 90.0);
        OrderData orderData = orderDto.addOrder(orderForm);
        
        // Get actual order date in IST
        Order order = orderApi.getOrderById(orderData.getOrderId());
        LocalDate orderDate = order.getOrderTime()
            .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
            .toLocalDate();
        
        // Create sales report form with actual order date
        SalesReportForm form = new SalesReportForm();
        form.setStartDate(orderDate);
        form.setEndDate(orderDate);

        List<SalesReportData> report = dto.getSalesReport(form);
        assertFalse("Report should not be empty for order: " + order.getOrderTime(), report.isEmpty());
    }
} 