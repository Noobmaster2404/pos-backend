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
    private Product createTestProduct(String barcode, String productName, Double mrp) {
        Product product = new Product();
        product.setBarcode(barcode);
        product.setProductName(productName.toLowerCase());
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

    @Test
    public void testGetProductsByClientIdAndProductName() throws ApiException {
        // Add multiple products with lowercase names
        Product product1 = createTestProduct("barcode1", "test apple", 100.0);
        Product product2 = createTestProduct("barcode2", "test banana", 200.0);
        Product product3 = createTestProduct("barcode3", "test apple", 300.0);
        productApi.addProduct(product1);
        productApi.addProduct(product2);
        productApi.addProduct(product3);

        // Test with matching product name
        List<Product> appleProducts = productApi.getProductsByClientIdAndProductName(testClient.getClientId(), "test ap", 0);
        assertEquals(2, appleProducts.size());
        assertTrue(appleProducts.stream().anyMatch(p -> p.getBarcode().equals("barcode1")));
        assertTrue(appleProducts.stream().anyMatch(p -> p.getBarcode().equals("barcode3")));

        // Test with non-matching product name
        List<Product> noProducts = productApi.getProductsByClientIdAndProductName(testClient.getClientId(), "orange", 0);
        assertEquals(0, noProducts.size());

        // Test with null product name (should return all products for the client)
        List<Product> allProducts = productApi.getProductsByClientIdAndProductName(testClient.getClientId(), null, 0);
        assertEquals(3, allProducts.size());
    }

    @Test
    public void testGetProductsByClientIdAndProductNamePagination() throws ApiException {
        // Add 15 products
        for (int i = 0; i < 15; i++) {
            Product product = createTestProduct("barcode" + i, "test apple", 100.0 + i);
            productApi.addProduct(product);
        }

        // Test pagination - page 0 (PAGE_SIZE is 10)
        List<Product> firstPage = productApi.getProductsByClientIdAndProductName(testClient.getClientId(), "test", 0);
        assertEquals(10, firstPage.size());

        // Test pagination - page 1
        List<Product> secondPage = productApi.getProductsByClientIdAndProductName(testClient.getClientId(), "test", 1);
        assertEquals(5, secondPage.size());
    }

    @Test
    public void testGetProductsByClientIdAndProductNamePartialMatch() throws ApiException {
        // Create products with lowercase names
        Product product1 = createTestProduct("barcode1", "test apple red", 100.0);
        Product product2 = createTestProduct("barcode2", "test apple green", 200.0);
        productApi.addProduct(product1);
        productApi.addProduct(product2);

        // Test with partial product name
        List<Product> products = productApi.getProductsByClientIdAndProductName(testClient.getClientId(), "test apple", 0);
        assertEquals(2, products.size());
    }
} 