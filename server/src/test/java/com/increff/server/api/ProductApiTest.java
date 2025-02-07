package com.increff.server.api;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.server.entity.Product;
import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;

public class ProductApiTest extends AbstractUnitTest {

    @Autowired
    private ProductApi productApi;

    @Autowired
    private ClientApi clientApi;

    private Client testClient;

    @Before
    public void setUp() throws ApiException {
        // Create a test client to use in product tests
        Client client = new Client();
        client.setClientName("Test Client");
        client.setPhone("1234567890");
        client.setEmail("test@test.com");
        client.setEnabled(true);
        testClient = clientApi.addClient(client);
    }

    // Helper method to create a test product
    private Product createTestProduct(String barcode, String name, Double mrp) {
        Product product = new Product();
        product.setBarcode(barcode);
        product.setProductName(name);
        product.setClient(testClient);
        product.setMrp(mrp);
        product.setImagePath("test.jpg");
        return product;
    }

    @Test
    public void testAddProduct() throws ApiException {
        Product product = createTestProduct("barcode1", "test product", 100.0);
        Product added = productApi.addProduct(product);
        assertNotNull(added.getProductId());
        assertEquals("barcode1", added.getBarcode());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicateBarcode() throws ApiException {
        Product product1 = createTestProduct("barcode1", "test product 1", 100.0);
        Product product2 = createTestProduct("barcode1", "test product 2", 200.0);
        productApi.addProduct(product1);
        productApi.addProduct(product2); // Should throw ApiException
    }

    @Test
    public void testGetAllProducts() throws ApiException {
        Product product1 = createTestProduct("barcode1", "test product 1", 100.0);
        Product product2 = createTestProduct("barcode2", "test product 2", 200.0);
        productApi.addProduct(product1);
        productApi.addProduct(product2);
        
        List<Product> products = productApi.getAllProducts(0);
        assertEquals(2, products.size());
    }

    @Test
    public void testGetProductsByNamePrefix() throws ApiException {
        Product product1 = createTestProduct("barcode1", "apple", 100.0);
        Product product2 = createTestProduct("barcode2", "banana", 200.0);
        productApi.addProduct(product1);
        productApi.addProduct(product2);
        
        List<Product> products = productApi.getCheckProductsByNamePrefix("app", 0);
        assertEquals(1, products.size());
        assertEquals("apple", products.get(0).getProductName());
    }

    @Test
    public void testGetProductByBarcode() throws ApiException {
        Product product = createTestProduct("barcode1", "test product", 100.0);
        productApi.addProduct(product);
        
        Product retrieved = productApi.getCheckProductByBarcode("barcode1");
        assertEquals("barcode1", retrieved.getBarcode());
    }

    @Test(expected = ApiException.class)
    public void testGetProductByInvalidBarcode() throws ApiException {
        productApi.getCheckProductByBarcode("nonexistent"); // Should throw ApiException
    }

    @Test
    public void testUpdateProduct() throws ApiException {
        Product product = createTestProduct("barcode1", "test product", 100.0);
        Product added = productApi.addProduct(product);
        
        Product updateData = createTestProduct("barcode2", "updated product", 200.0);
        Product updated = productApi.updateProductById(added.getProductId(), updateData);
        
        assertEquals("barcode2", updated.getBarcode());
        assertEquals("updated product", updated.getProductName());
        assertEquals(Double.valueOf(200.0), updated.getMrp());
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonexistentProduct() throws ApiException {
        Product updateData = createTestProduct("barcode1", "test product", 100.0);
        productApi.updateProductById(999, updateData); // Should throw ApiException
    }

    @Test
    public void testGetBarcodesByProductIds() throws ApiException {
        Product product1 = createTestProduct("barcode1", "test product 1", 100.0);
        Product product2 = createTestProduct("barcode2", "test product 2", 200.0);
        Product added1 = productApi.addProduct(product1);
        Product added2 = productApi.addProduct(product2);
        
        Map<Integer, String> barcodes = productApi.getBarcodesByProductIds(
            Arrays.asList(added1.getProductId(), added2.getProductId())
        );
        
        assertEquals(2, barcodes.size());
        assertEquals("barcode1", barcodes.get(added1.getProductId()));
        assertEquals("barcode2", barcodes.get(added2.getProductId()));
    }

    @Test
    public void testGetProductsByClientId() throws ApiException {
        Product product1 = createTestProduct("barcode1", "test product 1", 100.0);
        Product product2 = createTestProduct("barcode2", "test product 2", 200.0);
        productApi.addProduct(product1);
        productApi.addProduct(product2);
        
        List<Product> products = productApi.getCheckProductsByClientId(testClient.getClientId(), 0);
        assertEquals(2, products.size());
    }

    @Test
    public void testGetCountMethods() throws ApiException {
        Product product1 = createTestProduct("barcode1", "apple", 100.0);
        Product product2 = createTestProduct("barcode2", "banana", 200.0);
        productApi.addProduct(product1);
        productApi.addProduct(product2);
        
        assertEquals(2, productApi.getTotalCount());
        assertEquals(1, productApi.getCountByNamePrefix("app"));
        assertEquals(2, productApi.getCountByClientId(testClient.getClientId()));
    }

    @Test
    public void testGetProductsByBarcodes() throws ApiException {
        Product product1 = createTestProduct("barcode1", "test product 1", 100.0);
        Product product2 = createTestProduct("barcode2", "test product 2", 200.0);
        productApi.addProduct(product1);
        productApi.addProduct(product2);
        
        List<Product> products = productApi.getCheckProductsByBarcodes(Arrays.asList("barcode1", "barcode2"));
        assertEquals(2, products.size());
    }

    @Test(expected = ApiException.class)
    public void testGetProductsByInvalidBarcodes() throws ApiException {
        Product product = createTestProduct("barcode1", "test product", 100.0);
        productApi.addProduct(product);
        
        productApi.getCheckProductsByBarcodes(Arrays.asList("barcode1", "nonexistent")); // Should throw ApiException
    }
} 