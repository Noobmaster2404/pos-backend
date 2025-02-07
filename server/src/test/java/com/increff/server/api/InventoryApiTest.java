package com.increff.server.api;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.server.entity.Inventory;
import com.increff.server.entity.Product;
import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;

public class InventoryApiTest extends AbstractUnitTest {

    @Autowired
    private InventoryApi inventoryApi;

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

    private Inventory createTestInventory(Integer quantity) {
        Inventory inventory = new Inventory();
        inventory.setProduct(testProduct);
        inventory.setQuantity(quantity);
        return inventory;
    }

    @Test
    public void testAddInventory() throws ApiException {
        Inventory inventory = createTestInventory(100);
        Inventory added = inventoryApi.addInventory(inventory);
        assertNotNull(added.getInventoryId());
        assertEquals(Integer.valueOf(100), added.getQuantity());
    }

    @Test
    public void testAddInventoryToExisting() throws ApiException {
        // First addition
        Inventory inventory1 = createTestInventory(100);
        Inventory added1 = inventoryApi.addInventory(inventory1);
        
        // Second addition should add to existing quantity
        Inventory inventory2 = createTestInventory(50);
        Inventory added2 = inventoryApi.addInventory(inventory2);
        
        assertEquals(Integer.valueOf(150), added2.getQuantity());
        assertEquals(added1.getInventoryId(), added2.getInventoryId());
    }

    @Test
    public void testGetAllInventory() throws ApiException {
        Inventory inventory = createTestInventory(100);
        inventoryApi.addInventory(inventory);
        
        List<Inventory> inventories = inventoryApi.getAllInventory(0);
        assertEquals(1, inventories.size());
    }

    @Test
    public void testUpdateInventory() throws ApiException {
        Inventory inventory = createTestInventory(100);
        Inventory added = inventoryApi.addInventory(inventory);
        
        Inventory updateData = createTestInventory(200);
        Inventory updated = inventoryApi.updateInventoryById(added.getInventoryId(), updateData);
        
        assertEquals(Integer.valueOf(200), updated.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonexistentInventory() throws ApiException {
        Inventory inventory = createTestInventory(100);
        inventoryApi.updateInventoryById(999, inventory); // Should throw ApiException
    }

    @Test
    public void testGetInventoryByProductId() throws ApiException {
        Inventory inventory = createTestInventory(100);
        inventoryApi.addInventory(inventory);
        
        Inventory retrieved = inventoryApi.getCheckInventoryByProductId(testProduct.getProductId());
        assertEquals(Integer.valueOf(100), retrieved.getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testGetInventoryByNonexistentProductId() throws ApiException {
        inventoryApi.getCheckInventoryByProductId(999); // Should throw ApiException
    }

    @Test
    public void testGetInventoriesByProductIds() throws ApiException {
        // Create another product and its inventory
        Product product2 = new Product();
        product2.setBarcode("test_barcode_2");
        product2.setProductName("Test Product 2");
        product2.setClient(testClient);
        product2.setMrp(200.0);
        product2.setImagePath("test2.jpg");
        Product testProduct2 = productApi.addProduct(product2);

        // Add inventory for both products
        Inventory inventory1 = createTestInventory(100);
        Inventory inventory2 = new Inventory();
        inventory2.setProduct(testProduct2);
        inventory2.setQuantity(200);

        inventoryApi.addInventory(inventory1);
        inventoryApi.addInventory(inventory2);

        List<Inventory> inventories = inventoryApi.getCheckInventoriesByProductIds(
            Arrays.asList(testProduct.getProductId(), testProduct2.getProductId())
        );
        assertEquals(2, inventories.size());
    }

    @Test(expected = ApiException.class)
    public void testGetInventoriesByInvalidProductIds() throws ApiException {
        Inventory inventory = createTestInventory(100);
        inventoryApi.addInventory(inventory);
        
        inventoryApi.getCheckInventoriesByProductIds(
            Arrays.asList(testProduct.getProductId(), 999)
        ); // Should throw ApiException
    }

    @Test
    public void testGetTotalCount() throws ApiException {
        Inventory inventory = createTestInventory(100);
        inventoryApi.addInventory(inventory);
        
        assertEquals(1, inventoryApi.getTotalCount());
    }

    @Test
    public void testAddZeroQuantity() throws ApiException {
        Inventory inventory = createTestInventory(0);
        Inventory added = inventoryApi.addInventory(inventory);
        assertEquals(Integer.valueOf(0), added.getQuantity());
    }

    @Test
    public void testMultipleUpdates() throws ApiException {
        // Initial addition
        Inventory inventory = createTestInventory(100);
        Inventory added = inventoryApi.addInventory(inventory);
        
        // First update
        Inventory update1 = createTestInventory(150);
        inventoryApi.updateInventoryById(added.getInventoryId(), update1);
        
        // Second update
        Inventory update2 = createTestInventory(200);
        Inventory finalUpdate = inventoryApi.updateInventoryById(added.getInventoryId(), update2);
        
        assertEquals(Integer.valueOf(200), finalUpdate.getQuantity());
    }
} 