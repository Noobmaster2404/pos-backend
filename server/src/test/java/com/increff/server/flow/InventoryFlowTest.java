package com.increff.server.flow;

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
import com.increff.server.api.ProductApi;
import com.increff.server.api.ClientApi;

public class InventoryFlowTest extends AbstractUnitTest {

    @Autowired
    private InventoryFlow inventoryFlow;

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
        Inventory added = inventoryFlow.addInventory(inventory);
        assertNotNull(added.getInventoryId());
        assertEquals(Integer.valueOf(100), added.getQuantity());
    }

    @Test
    public void testGetAllInventory() throws ApiException {
        Inventory inventory = createTestInventory(100);
        inventoryFlow.addInventory(inventory);
        
        List<Inventory> inventories = inventoryFlow.getAllInventory(0);
        assertEquals(1, inventories.size());
    }

    @Test
    public void testGetInventoryByProductId() throws ApiException {
        Inventory inventory = createTestInventory(100);
        inventoryFlow.addInventory(inventory);
        
        Inventory retrieved = inventoryFlow.getInventoryByProductId(testProduct.getProductId());
        assertEquals(Integer.valueOf(100), retrieved.getQuantity());
    }

    @Test
    public void testUpdateInventory() throws ApiException {
        Inventory inventory = createTestInventory(100);
        Inventory added = inventoryFlow.addInventory(inventory);
        
        Inventory updateData = createTestInventory(200);
        Inventory updated = inventoryFlow.updateInventoryById(added.getInventoryId(), updateData);
        
        assertEquals(Integer.valueOf(200), updated.getQuantity());
    }

    @Test
    public void testBulkAddInventory() throws ApiException {
        // Create another product
        Product product2 = new Product();
        product2.setBarcode("test_barcode_2");
        product2.setProductName("Test Product 2");
        product2.setClient(testClient);
        product2.setMrp(200.0);
        product2.setImagePath("test2.jpg");
        Product testProduct2 = productApi.addProduct(product2);

        // Create inventory items
        Inventory inventory1 = createTestInventory(100);
        Inventory inventory2 = new Inventory();
        inventory2.setProduct(testProduct2);
        inventory2.setQuantity(200);

        List<Inventory> inventories = Arrays.asList(inventory1, inventory2);
        List<Inventory> added = inventoryFlow.bulkAddInventory(inventories);
        
        assertEquals(2, added.size());
        assertEquals(Integer.valueOf(100), added.get(0).getQuantity());
        assertEquals(Integer.valueOf(200), added.get(1).getQuantity());
    }

    @Test(expected = ApiException.class)
    public void testGetNonexistentInventory() throws ApiException {
        inventoryFlow.getInventoryByProductId(999); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonexistentInventory() throws ApiException {
        Inventory inventory = createTestInventory(100);
        inventoryFlow.updateInventoryById(999, inventory); // Should throw ApiException
    }

    @Test
    public void testAddInventoryToExisting() throws ApiException {
        // First addition
        Inventory inventory1 = createTestInventory(100);
        Inventory added1 = inventoryFlow.addInventory(inventory1);
        
        // Second addition should add to existing quantity
        Inventory inventory2 = createTestInventory(50);
        Inventory added2 = inventoryFlow.addInventory(inventory2);
        
        assertEquals(Integer.valueOf(150), added2.getQuantity());
        assertEquals(added1.getInventoryId(), added2.getInventoryId());
    }

    @Test
    public void testBulkAddWithExistingInventory() throws ApiException {
        // First add some inventory
        Inventory initial = createTestInventory(100);
        inventoryFlow.addInventory(initial);
        
        // Then try bulk add including the same product
        List<Inventory> inventories = Arrays.asList(createTestInventory(50));
        List<Inventory> added = inventoryFlow.bulkAddInventory(inventories);
        
        assertEquals(1, added.size());
        assertEquals(Integer.valueOf(150), added.get(0).getQuantity());
    }
} 