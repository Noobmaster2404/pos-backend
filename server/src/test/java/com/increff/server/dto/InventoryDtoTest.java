package com.increff.server.dto;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.commons.model.InventoryForm;
import com.increff.commons.model.InventoryData;
import com.increff.commons.model.ProductForm;
import com.increff.commons.model.ClientForm;
import com.increff.commons.model.PaginatedData;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;

public class InventoryDtoTest extends AbstractUnitTest {

    @Autowired
    private InventoryDto dto;

    @Autowired
    private ProductDto productDto;

    @Autowired
    private ClientDto clientDto;

    private Integer testProductId;
    private Integer testClientId;

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
        productForm.setBarcode("test_barcode");
        productForm.setProductName("Test Product");
        productForm.setClientId(testClientId);
        productForm.setMrp(100.0);
        testProductId = productDto.addProduct(productForm).getProductId();
    }

    private InventoryForm createTestInventoryForm(String barcode, Integer quantity) {
        InventoryForm form = new InventoryForm();
        form.setBarcode(barcode);
        form.setQuantity(quantity);
        return form;
    }

    @Test
    public void testAdd() throws ApiException {
        InventoryForm form = createTestInventoryForm("test_barcode", 100);
        InventoryData data = dto.addInventory(form);
        
        assertNotNull(data.getInventoryId());
        assertEquals("test_barcode", data.getBarcode());
        assertEquals(Integer.valueOf(100), data.getQuantity());
    }

    @Test
    public void testGetAll() throws ApiException {
        InventoryForm form = createTestInventoryForm("test_barcode", 100);
        dto.addInventory(form);
        
        PaginatedData<InventoryData> inventories = dto.getAllInventory(0);
        assertEquals(1, inventories.getData().size());
        assertEquals(1, inventories.getTotalItems());
        assertEquals(0, inventories.getPage());
        assertEquals(1, inventories.getTotalPages());
        assertFalse(inventories.isHasNext());
    }

    @Test
    public void testPagination() throws ApiException {
        // Create multiple products and their inventory
        for(int i = 0; i < 25; i++) {
            ProductForm productForm = new ProductForm();
            productForm.setBarcode("barcode" + i);
            productForm.setProductName("Product " + i);
            productForm.setClientId(testClientId);
            productForm.setMrp(100.0);
            Integer productId = productDto.addProduct(productForm).getProductId();
            
            InventoryForm inventoryForm = createTestInventoryForm("barcode" + i, 100 + i);
            dto.addInventory(inventoryForm);
        }
        
        // Test first page
        PaginatedData<InventoryData> page1 = dto.getAllInventory(0);
        assertEquals(10, page1.getData().size());
        assertEquals(25, page1.getTotalItems());
        assertEquals(0, page1.getPage());
        assertEquals(3, page1.getTotalPages());
        assertTrue(page1.isHasNext());
        
        // Test second page
        PaginatedData<InventoryData> page2 = dto.getAllInventory(1);
        assertEquals(10, page2.getData().size());
        assertEquals(25, page2.getTotalItems());
        assertEquals(1, page2.getPage());
        assertEquals(3, page2.getTotalPages());
        assertTrue(page2.isHasNext());

        // Test last page
        PaginatedData<InventoryData> page3 = dto.getAllInventory(2);
        assertEquals(5, page3.getData().size());
        assertEquals(25, page3.getTotalItems());
        assertEquals(2, page3.getPage());
        assertEquals(3, page3.getTotalPages());
        assertFalse(page3.isHasNext());
    }

    @Test
    public void testUpdate() throws ApiException {
        InventoryForm form = createTestInventoryForm("test_barcode", 100);
        InventoryData added = dto.addInventory(form);
        
        InventoryForm updateForm = createTestInventoryForm("test_barcode", 200);
        InventoryData updated = dto.updateInventoryById(added.getInventoryId(), updateForm);
        
        assertEquals(Integer.valueOf(200), updated.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonexistentInventory() throws ApiException {
        InventoryForm form = createTestInventoryForm("test_barcode", 100);
        dto.updateInventoryById(999, form); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testAddNegativeQuantity() throws ApiException {
        InventoryForm form = createTestInventoryForm("test_barcode", -100);
        dto.addInventory(form); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testAddToNonexistentProduct() throws ApiException {
        InventoryForm form = createTestInventoryForm("nonexistent_barcode", 100);
        dto.addInventory(form); // Should throw ApiException
    }

    @Test
    public void testGetByBarcode() throws ApiException {
        InventoryForm form = createTestInventoryForm("test_barcode", 100);
        InventoryData added = dto.addInventory(form);
        
        InventoryData found = dto.getInventoryByBarcode("test_barcode");
        assertEquals(added.getInventoryId(), found.getInventoryId());
        assertEquals(Integer.valueOf(100), found.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testGetByNonexistentBarcode() throws ApiException {
        dto.getInventoryByBarcode("nonexistent_barcode"); // Should throw ApiException
    }

    @Test
    public void testAddToExistingInventory() throws ApiException {
        // First addition
        InventoryForm form1 = createTestInventoryForm("test_barcode", 100);
        InventoryData added1 = dto.addInventory(form1);
        
        // Second addition should update existing quantity
        InventoryForm form2 = createTestInventoryForm("test_barcode", 50);
        InventoryData added2 = dto.addInventory(form2);
        
        assertEquals(Integer.valueOf(150), added2.getQuantity());
        assertEquals(added1.getInventoryId(), added2.getInventoryId());
    }

    @Test
    public void testEmptyPage() throws ApiException {
        PaginatedData<InventoryData> inventories = dto.getAllInventory(0);
        assertEquals(0, inventories.getData().size());
        assertEquals(0, inventories.getTotalItems());
        assertEquals(0, inventories.getPage());
        assertEquals(0, inventories.getTotalPages());
        assertFalse(inventories.isHasNext());
    }

    @Test
    public void testPageBeyondResults() throws ApiException {
        InventoryForm form = createTestInventoryForm("test_barcode", 100);
        dto.addInventory(form);
        
        PaginatedData<InventoryData> inventories = dto.getAllInventory(1); // Second page
        assertEquals(0, inventories.getData().size());
        assertEquals(1, inventories.getTotalItems());
        assertEquals(1, inventories.getPage());
        assertEquals(1, inventories.getTotalPages());
        assertFalse(inventories.isHasNext());
    }
} 