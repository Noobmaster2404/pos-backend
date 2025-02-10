package com.increff.server.flow;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.server.entity.Product;
import com.increff.server.entity.Client;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;
import com.increff.server.api.ClientApi;

public class ProductFlowTest extends AbstractUnitTest {

    @Autowired
    private ProductFlow productFlow;

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
        Product added = productFlow.addProduct(product);
        assertNotNull(added.getProductId());
        assertEquals("barcode1", added.getBarcode());
    }

    @Test
    public void testGetAllProducts() throws ApiException {
        Product product1 = createTestProduct("barcode1", "test product 1", 100.0);
        Product product2 = createTestProduct("barcode2", "test product 2", 200.0);
        productFlow.addProduct(product1);
        productFlow.addProduct(product2);
        
        List<Product> products = productFlow.getAllProducts(0);
        assertEquals(2, products.size());
    }

    @Test
    public void testUpdateProduct() throws ApiException {
        Product product = createTestProduct("barcode1", "test product", 100.0);
        Product added = productFlow.addProduct(product);
        
        Product updateData = createTestProduct("barcode2", "updated product", 200.0);
        Product updated = productFlow.updateProductById(added.getProductId(), updateData);
        
        assertEquals("barcode2", updated.getBarcode());
        assertEquals("updated product", updated.getProductName());
        assertEquals(Double.valueOf(200.0), updated.getMrp());
    }

    @Test
    public void testBulkAddProducts() throws ApiException {
        List<Product> products = Arrays.asList(
            createTestProduct("barcode1", "test product 1", 100.0),
            createTestProduct("barcode2", "test product 2", 200.0)
        );
        
        List<Product> addedProducts = productFlow.bulkAddProducts(products);
        assertEquals(2, addedProducts.size());
    }

    @Test
    public void testGetProductByBarcode() throws ApiException {
        Product product = createTestProduct("barcode1", "test product", 100.0);
        productFlow.addProduct(product);
        
        Product retrieved = productFlow.getProductByBarcode("barcode1");
        assertEquals("barcode1", retrieved.getBarcode());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicateBarcode() throws ApiException {
        Product product1 = createTestProduct("barcode1", "test product 1", 100.0);
        Product product2 = createTestProduct("barcode1", "test product 2", 200.0);
        productFlow.addProduct(product1);
        productFlow.addProduct(product2); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonexistentProduct() throws ApiException {
        Product updateData = createTestProduct("barcode1", "test product", 100.0);
        productFlow.updateProductById(999, updateData); // Should throw ApiException
    }

    @Test
    public void testGetProductsByClientIdAndProductNameExactMatch() throws ApiException {
        // Create and add test products
        Product product1 = createTestProduct("barcode1", "test apple", 100.0);
        Product product2 = createTestProduct("barcode2", "test banana", 200.0);
        productFlow.addProduct(product1);
        productFlow.addProduct(product2);

        // Test exact match
        List<Product> products = productFlow.getProductsByClientIdAndProductName(testClient.getClientId(), "test apple", 0);
        assertEquals(1, products.size());
        assertEquals("test apple", products.get(0).getProductName());
    }

    @Test
    public void testGetProductsByClientIdAndProductNamePartialMatch() throws ApiException {
        // Create and add test products
        Product product1 = createTestProduct("barcode1", "test apple red", 100.0);
        Product product2 = createTestProduct("barcode2", "test apple green", 200.0);
        productFlow.addProduct(product1);
        productFlow.addProduct(product2);

        // Test partial match
        List<Product> products = productFlow.getProductsByClientIdAndProductName(testClient.getClientId(), "test appl", 0);
        assertEquals(2, products.size());
    }

    @Test
    public void testGetProductsByClientIdAndProductNameNoMatch() throws ApiException {
        // Create and add test product
        Product product = createTestProduct("barcode1", "test apple", 100.0);
        productFlow.addProduct(product);

        // Test no match
        List<Product> products = productFlow.getProductsByClientIdAndProductName(testClient.getClientId(), "orange", 0);
        assertEquals(0, products.size());
    }

    @Test
    public void testGetProductsByClientIdAndProductNameNullProductName() throws ApiException {
        // Create and add test products
        Product product1 = createTestProduct("barcode1", "test apple", 100.0);
        Product product2 = createTestProduct("barcode2", "test banana", 200.0);
        productFlow.addProduct(product1);
        productFlow.addProduct(product2);

        // Test null product name (should return all products for client)
        List<Product> products = productFlow.getProductsByClientIdAndProductName(testClient.getClientId(), null, 0);
        assertEquals(2, products.size());
    }

    @Test
    public void testGetProductsByClientIdAndProductNamePagination() throws ApiException {
        // Add 15 products
        for (int i = 0; i < 15; i++) {
            Product product = createTestProduct("barcode" + i, "test product " + i, 100.0 + i);
            productFlow.addProduct(product);
        }

        // Test first page (should return 10 products)
        List<Product> firstPage = productFlow.getProductsByClientIdAndProductName(testClient.getClientId(), "test", 0);
        assertEquals(10, firstPage.size());

        // Test second page (should return 5 products)
        List<Product> secondPage = productFlow.getProductsByClientIdAndProductName(testClient.getClientId(), "test", 1);
        assertEquals(5, secondPage.size());
    }

    @Test
    public void testGetProductsByClientIdAndProductNameMultipleClients() throws ApiException {
        // Create second client
        Client client2 = new Client();
        client2.setClientName("test client 2");
        client2.setPhone("9876543210");
        client2.setEmail("test2@test.com");
        client2.setEnabled(true);
        Client secondClient = clientApi.addClient(client2);

        // Add products for both clients
        Product product1 = createTestProduct("barcode1", "test apple", 100.0);
        product1.setClient(testClient);
        Product product2 = createTestProduct("barcode2", "test apple", 200.0);
        product2.setClient(secondClient);
        
        productFlow.addProduct(product1);
        productFlow.addProduct(product2);

        // Test products for first client
        List<Product> clientProducts = productFlow.getProductsByClientIdAndProductName(testClient.getClientId(), "test appl", 0);
        assertEquals(1, clientProducts.size());
        assertEquals(testClient.getClientId(), clientProducts.get(0).getClient().getClientId());
    }
} 