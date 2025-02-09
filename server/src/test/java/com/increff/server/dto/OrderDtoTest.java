package com.increff.server.dto;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.commons.model.OrderForm;
import com.increff.commons.model.OrderData;
import com.increff.commons.model.OrderItemForm;
import com.increff.commons.model.PaginatedData;
import com.increff.commons.model.ClientForm;
import com.increff.commons.model.ProductForm;
import com.increff.commons.model.InventoryForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;
import com.increff.commons.model.OrderSearchForm;

public class OrderDtoTest extends AbstractUnitTest {

    @Autowired
    private OrderDto dto;

    @Autowired
    private ClientDto clientDto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private InventoryDto inventoryDto;

    private Integer testClientId;
    private String testBarcode;

    @Before
    public void setUp() throws ApiException {
        // Create test client
        ClientForm clientForm = new ClientForm();
        clientForm.setClientName("Test Client");
        clientForm.setPhone("1234567890");
        clientForm.setEmail("test@test.com");
        testClientId = clientDto.addClient(clientForm).getClientId();

        // Create test product
        ProductForm productForm = new ProductForm();
        productForm.setBarcode("test_barcode");
        productForm.setProductName("Test Product");
        productForm.setClientId(testClientId);
        productForm.setMrp(100.0);
        testBarcode = productForm.getBarcode();
        productDto.addProduct(productForm);

        // Add inventory
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode(testBarcode);
        inventoryForm.setQuantity(1000);
        inventoryDto.addInventory(inventoryForm);
    }

    private OrderForm createTestOrderForm(List<OrderItemForm> items) {
        OrderForm form = new OrderForm();
        form.setOrderItems(items);
        return form;
    }

    private OrderItemForm createTestOrderItem(String barcode, Integer quantity, Double sellingPrice) {
        OrderItemForm item = new OrderItemForm();
        item.setBarcode(barcode);
        item.setQuantity(quantity);
        item.setSellingPrice(sellingPrice);
        return item;
    }

    @Test
    public void testCreateOrder() throws ApiException {
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 10, 90.0));
        
        OrderForm form = createTestOrderForm(items);
        OrderData data = dto.addOrder(form);
        
        assertNotNull(data.getOrderId());
        assertEquals(1, data.getOrderItems().size());
        assertEquals(Double.valueOf(900.0), data.getOrderTotal());
    }

    @Test
    public void testGetOrders() throws ApiException {
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 10, 90.0));
        OrderForm form = createTestOrderForm(items);
        dto.addOrder(form);
        
        OrderSearchForm searchForm = new OrderSearchForm();
        searchForm.setStartDate(getStartDate());
        searchForm.setEndDate(getEndDate());
        PaginatedData<OrderData> orders = dto.getOrdersByDateRange(searchForm, 0);
        assertEquals(1, orders.getData().size());
        assertEquals(1, orders.getTotalItems());
        assertEquals(0, orders.getPage());
        assertEquals(1, orders.getTotalPages());
        assertFalse(orders.isHasNext());
    }

    @Test
    public void testEmptyPage() throws ApiException {
        OrderSearchForm searchForm = new OrderSearchForm();
        searchForm.setStartDate(getStartDate());
        searchForm.setEndDate(getEndDate());
        PaginatedData<OrderData> orders = dto.getOrdersByDateRange(searchForm, 0);
        assertEquals(0, orders.getData().size());
        assertEquals(0, orders.getTotalItems());
        assertEquals(0, orders.getPage());
        assertEquals(0, orders.getTotalPages());
        assertFalse(orders.isHasNext());
    }

    @Test
    public void testPagination() throws ApiException {
        // Create 25 orders
        for(int i = 0; i < 25; i++) {
            List<OrderItemForm> items = new ArrayList<>();
            items.add(createTestOrderItem(testBarcode, 1, 90.0));
            OrderForm form = createTestOrderForm(items);
            dto.addOrder(form);
        }
        
        // Test first page
        OrderSearchForm searchForm = new OrderSearchForm();
        searchForm.setStartDate(getStartDate());
        searchForm.setEndDate(getEndDate());
        PaginatedData<OrderData> page1 = dto.getOrdersByDateRange(searchForm, 0);
        assertEquals(10, page1.getData().size());
        assertEquals(25, page1.getTotalItems());
        assertEquals(0, page1.getPage());
        assertEquals(3, page1.getTotalPages());
        assertTrue(page1.isHasNext());
        
        // Test second page
        searchForm.setStartDate(getStartDate());
        searchForm.setEndDate(getEndDate());
        PaginatedData<OrderData> page2 = dto.getOrdersByDateRange(searchForm, 1);
        assertEquals(10, page2.getData().size());
        assertEquals(25, page2.getTotalItems());
        assertEquals(1, page2.getPage());
        assertEquals(3, page2.getTotalPages());
        assertTrue(page2.isHasNext());

        // Test last page
        searchForm.setStartDate(getStartDate());
        searchForm.setEndDate(getEndDate());
        PaginatedData<OrderData> page3 = dto.getOrdersByDateRange(searchForm, 2);
        assertEquals(5, page3.getData().size());
        assertEquals(25, page3.getTotalItems());
        assertEquals(2, page3.getPage());
        assertEquals(3, page3.getTotalPages());
        assertFalse(page3.isHasNext());
    }

    @Test(expected = ApiException.class)
    public void testCreateOrderWithInvalidBarcode() throws ApiException {
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem("invalid_barcode", 10, 90.0));
        
        OrderForm form = createTestOrderForm(items);
        dto.addOrder(form);
    }

    @Test(expected = ApiException.class)
    public void testCreateOrderWithInsufficientInventory() throws ApiException {
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 2000, 90.0));
        
        OrderForm form = createTestOrderForm(items);
        dto.addOrder(form);
    }

    @Test(expected = ApiException.class)
    public void testCreateOrderWithNegativeQuantity() throws ApiException {
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, -10, 90.0));
        
        OrderForm form = createTestOrderForm(items);
        dto.addOrder(form);
    }

    @Test(expected = ApiException.class)
    public void testCreateOrderWithNegativePrice() throws ApiException {
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 10, -90.0));
        
        OrderForm form = createTestOrderForm(items);
        dto.addOrder(form);
    }

    @Test(expected = ApiException.class)
    public void testCreateOrderWithEmptyItems() throws ApiException {
        OrderForm form = createTestOrderForm(new ArrayList<>());
        dto.addOrder(form);
    }

    @Test
    public void testCreateOrderWithMultipleItems() throws ApiException {
        // Create another product
        ProductForm productForm = new ProductForm();
        productForm.setBarcode("test_barcode_2");
        productForm.setProductName("Test Product 2");
        productForm.setClientId(testClientId);
        productForm.setMrp(200.0);
        productDto.addProduct(productForm);
        
        // Add inventory
        InventoryForm inventoryForm = new InventoryForm();
        inventoryForm.setBarcode("test_barcode_2");
        inventoryForm.setQuantity(1000);
        inventoryDto.addInventory(inventoryForm);

        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 10, 90.0));
        items.add(createTestOrderItem("test_barcode_2", 5, 180.0));
        
        OrderForm form = createTestOrderForm(items);
        OrderData data = dto.addOrder(form);
        
        assertEquals(2, data.getOrderItems().size());
        assertEquals(Double.valueOf(1800.0), data.getOrderTotal());
    }

    @Test
    public void testPageBeyondResults() throws ApiException {
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 10, 90.0));
        OrderForm form = createTestOrderForm(items);
        dto.addOrder(form);
        
        OrderSearchForm searchForm = new OrderSearchForm();
        searchForm.setStartDate(getStartDate());
        searchForm.setEndDate(getEndDate());
        PaginatedData<OrderData> orders = dto.getOrdersByDateRange(searchForm, 1);
        assertEquals(0, orders.getData().size());
        assertEquals(1, orders.getTotalItems());
        assertEquals(1, orders.getPage());
        assertEquals(1, orders.getTotalPages());
        assertFalse(orders.isHasNext());
    }

    @Test(expected = ApiException.class)
    public void testCreateOrderWithPriceHigherThanMrp() throws ApiException {
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 10, 150.0));
        
        OrderForm form = createTestOrderForm(items);
        dto.addOrder(form);
    }

    private LocalDate getStartDate() {
        return LocalDate.now().minusDays(30); // 30 days ago
    }

    private LocalDate getEndDate() {
        return LocalDate.now().plusDays(1); // Tomorrow
    }

    @Test
    public void testGetOrdersByDateRange() throws ApiException {
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 10, 90.0));
        OrderForm form = createTestOrderForm(items);
        dto.addOrder(form);
        
        OrderSearchForm searchForm = new OrderSearchForm();
        searchForm.setStartDate(getStartDate());
        searchForm.setEndDate(getEndDate());
        PaginatedData<OrderData> orders = dto.getOrdersByDateRange(searchForm, 0);
        assertEquals(1, orders.getData().size());
        assertEquals(1, orders.getTotalItems());
        assertEquals(0, orders.getPage());
        assertEquals(1, orders.getTotalPages());
        assertFalse(orders.isHasNext());
    }

    @Test
    public void testEmptyDateRange() throws ApiException {
        OrderSearchForm searchForm = new OrderSearchForm();
        searchForm.setStartDate(LocalDate.now().plusDays(1));
        searchForm.setEndDate(LocalDate.now().plusDays(2));
        
        PaginatedData<OrderData> orders = dto.getOrdersByDateRange(searchForm, 0);
        assertEquals(0, orders.getData().size());
        assertEquals(0, orders.getTotalItems());
        assertEquals(0, orders.getPage());
        assertEquals(0, orders.getTotalPages());
        assertFalse(orders.isHasNext());
    }

    @Test
    public void testPageBeyondResults2() throws ApiException {
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 10, 90.0));
        OrderForm form = createTestOrderForm(items);
        dto.addOrder(form);
        
        OrderSearchForm searchForm = new OrderSearchForm();
        searchForm.setStartDate(getStartDate());
        searchForm.setEndDate(getEndDate());
        PaginatedData<OrderData> orders = dto.getOrdersByDateRange(searchForm, 1);
        assertEquals(0, orders.getData().size());
        assertEquals(1, orders.getTotalItems());
        assertEquals(1, orders.getPage());
        assertEquals(1, orders.getTotalPages());
        assertFalse(orders.isHasNext());
    }

    @Test(expected = ApiException.class)
    public void testInvalidDateRange() throws ApiException {
        OrderSearchForm searchForm = new OrderSearchForm();
        LocalDate today = LocalDate.now();
        searchForm.setStartDate(today);
        searchForm.setEndDate(today.minusDays(1));
        dto.getOrdersByDateRange(searchForm, 0);
    }

    @Test(expected = ApiException.class)
    public void testGenerateInvoiceForNonExistentOrder() throws ApiException {
        dto.generateInvoice(999); // Non-existent order ID
    }

    @Test(expected = ApiException.class)
    public void testDownloadInvoiceForNonExistentOrder() throws ApiException {
        dto.downloadInvoice(999); // Non-existent order ID
    }

    @Test(expected = ApiException.class)
    public void testDownloadInvoiceBeforeGeneration() throws ApiException {
        // Create and add an order
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 10, 90.0));
        OrderForm form = createTestOrderForm(items);
        OrderData orderData = dto.addOrder(form);
        
        // Try to download before generating
        dto.downloadInvoice(orderData.getOrderId());
    }

    @Test
    public void testGenerateInvoiceForAlreadyInvoicedOrder() throws ApiException {
        // Create and add an order
        List<OrderItemForm> items = new ArrayList<>();
        items.add(createTestOrderItem(testBarcode, 10, 90.0));
        OrderForm form = createTestOrderForm(items);
        OrderData orderData = dto.addOrder(form);
        
        // Generate invoice first time
        String firstInvoicePath = dto.generateInvoice(orderData.getOrderId());
        
        // Try to generate again
        String secondInvoicePath = dto.generateInvoice(orderData.getOrderId());
        
        // Verify both paths are the same
        assertEquals(firstInvoicePath, secondInvoicePath);
        
        // Verify order status
        OrderData updatedOrder = dto.getOrder(orderData.getOrderId());
        assertTrue(updatedOrder.getInvoiceGenerated());
        assertEquals(firstInvoicePath, updatedOrder.getInvoicePath());
    }
} 