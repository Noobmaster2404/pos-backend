package com.increff.server.dto;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.increff.commons.model.ProductForm;
import com.increff.commons.model.ProductData;
import com.increff.commons.model.ClientForm;
import com.increff.commons.exception.ApiException;
import com.increff.server.AbstractUnitTest;
import com.increff.commons.model.PaginatedData;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class ProductDtoTest extends AbstractUnitTest {

    @Autowired
    private ProductDto dto;

    @Autowired
    private ClientDto clientDto;

    private Integer testClientId;

    @Before
    public void setUp() throws ApiException {
        // Create a test client
        ClientForm clientForm = new ClientForm();
        clientForm.setClientName("Test Client");
        clientForm.setPhone("1234567890");
        clientForm.setEmail("test@test.com");
        testClientId = clientDto.addClient(clientForm).getClientId();
    }

    private ProductForm createTestProductForm(String barcode, String name, Double mrp) {
        ProductForm form = new ProductForm();
        form.setBarcode(barcode);
        form.setProductName(name);
        form.setClientId(testClientId);
        form.setMrp(mrp);
        return form;
    }

    @Test
    public void testAdd() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "Test Product", 100.0);
        ProductData data = dto.addProduct(form);
        
        assertNotNull(data.getProductId());
        assertEquals("barcode1", data.getBarcode());
        assertEquals("test product", data.getProductName());
        assertEquals(Double.valueOf(100.0), data.getMrp());
        assertEquals(testClientId, data.getClientId());
    }

    @Test(expected = ApiException.class)
    public void testAddDuplicateBarcode() throws ApiException {
        ProductForm form1 = createTestProductForm("barcode1", "Test Product 1", 100.0);
        ProductForm form2 = createTestProductForm("barcode1", "Test Product 2", 200.0);
        
        dto.addProduct(form1);
        dto.addProduct(form2); // Should throw ApiException
    }

    @Test
    public void testGetAll() throws ApiException {
        ProductForm form1 = createTestProductForm("barcode1", "Test Product 1", 100.0);
        ProductForm form2 = createTestProductForm("barcode2", "Test Product 2", 200.0);
        
        dto.addProduct(form1);
        dto.addProduct(form2);
        
        PaginatedData<ProductData> products = dto.getAllProducts(0);
        assertEquals(2, products.getData().size());
        assertEquals(2, products.getTotalItems());
        assertEquals(0, products.getPage());
        assertEquals(1, products.getTotalPages());
        assertFalse(products.isHasNext());
    }

    @Test
    public void testPagination() throws ApiException {
        // Add multiple products
        for(int i = 0; i < 25; i++) {
            ProductForm form = createTestProductForm(
                "barcode" + i, 
                "Test Product " + i, 
                100.0 + i
            );
            dto.addProduct(form);
        }
        
        // Test first page (page size is 10)
        PaginatedData<ProductData> page1 = dto.getAllProducts(0);
        assertEquals(10, page1.getData().size());
        assertEquals(25, page1.getTotalItems());
        assertEquals(0, page1.getPage());
        assertEquals(3, page1.getTotalPages()); // 25 items / 10 per page = 3 pages
        assertTrue(page1.isHasNext());
        
        // Test second page
        PaginatedData<ProductData> page2 = dto.getAllProducts(1);
        assertEquals(10, page2.getData().size());
        assertEquals(25, page2.getTotalItems());
        assertEquals(1, page2.getPage());
        assertEquals(3, page2.getTotalPages());
        assertTrue(page2.isHasNext());

        // Test third (last) page
        PaginatedData<ProductData> page3 = dto.getAllProducts(2);
        assertEquals(5, page3.getData().size());
        assertEquals(25, page3.getTotalItems());
        assertEquals(2, page3.getPage());
        assertEquals(3, page3.getTotalPages());
        assertFalse(page3.isHasNext());
    }

    @Test
    public void testUpdate() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "Test Product", 100.0);
        ProductData added = dto.addProduct(form);
        
        ProductForm updateForm = createTestProductForm("barcode1", "Updated Product", 200.0);
        ProductData updated = dto.updateProductById(added.getProductId(), updateForm);
        
        assertEquals("updated product", updated.getProductName());
        assertEquals(Double.valueOf(200.0), updated.getMrp());
    }

    @Test(expected = ApiException.class)
    public void testUpdateNonexistentProduct() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "Test Product", 100.0);
        dto.updateProductById(999, form); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testAddInvalidMrp() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "Test Product", -100.0);
        dto.addProduct(form); // Should throw ApiException for negative MRP
    }

    @Test(expected = ApiException.class)
    public void testAddBlankBarcode() throws ApiException {
        ProductForm form = createTestProductForm("", "Test Product", 100.0);
        dto.addProduct(form); // Should throw ApiException for blank barcode
    }

    @Test(expected = ApiException.class)
    public void testAddBlankName() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "", 100.0);
        dto.addProduct(form); // Should throw ApiException for blank name
    }

    @Test
    public void testAddNameNormalization() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "  Test Product  ", 100.0);
        ProductData data = dto.addProduct(form);
        assertEquals("test product", data.getProductName()); // Should be normalized
    }

    @Test(expected = ApiException.class)
    public void testAddNameTooLong() throws ApiException {
        StringBuilder longName = new StringBuilder();
        for(int i = 0; i < 256; i++) {
            longName.append("a");
        }
        ProductForm form = createTestProductForm("barcode1", longName.toString(), 100.0);
        dto.addProduct(form); // Should throw ApiException for name too long
    }

    @Test(expected = ApiException.class)
    public void testAddBarcodeTooLong() throws ApiException {
        StringBuilder longBarcode = new StringBuilder();
        for(int i = 0; i < 256; i++) {
            longBarcode.append("a");
        }
        ProductForm form = createTestProductForm(longBarcode.toString(), "Test Product", 100.0);
        dto.addProduct(form); // Should throw ApiException for barcode too long
    }

    @Test(expected = ApiException.class)
    public void testAddInvalidClientId() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "Test Product", 100.0);
        form.setClientId(999); // Non-existent client ID
        dto.addProduct(form); // Should throw ApiException
    }

    @Test
    public void testGetByClientId() throws ApiException {
        ProductForm form1 = createTestProductForm("barcode1", "Test Product 1", 100.0);
        ProductForm form2 = createTestProductForm("barcode2", "Test Product 2", 200.0);
        
        dto.addProduct(form1);
        dto.addProduct(form2);
        
        PaginatedData<ProductData> products = dto.getProductsByClientId(testClientId, 0);
        assertEquals(2, products.getData().size());
        assertEquals(2, products.getTotalItems());
        assertEquals(0, products.getPage());
        assertEquals(1, products.getTotalPages());
        assertFalse(products.isHasNext());
    }

    @Test
    public void testGetByBarcode() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "Test Product", 100.0);
        ProductData added = dto.addProduct(form);
        
        ProductData found = dto.getProductByBarcode("barcode1");
        assertEquals(added.getProductId(), found.getProductId());
    }

    @Test(expected = ApiException.class)
    public void testGetByNonexistentBarcode() throws ApiException {
        dto.getProductByBarcode("nonexistent"); // Should throw ApiException
    }

    @Test
    public void testUpdateMrp() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "Test Product", 100.0);
        ProductData added = dto.addProduct(form);
        
        ProductForm updateForm = createTestProductForm("barcode1", "Test Product", 200.0);
        ProductData updated = dto.updateProductById(added.getProductId(), updateForm);
        
        assertEquals(Double.valueOf(200.0), updated.getMrp());
    }

    @Test
    public void testEmptyPage() throws ApiException {
        PaginatedData<ProductData> products = dto.getAllProducts(0);
        assertEquals(0, products.getData().size());
        assertEquals(0, products.getTotalItems());
        assertEquals(0, products.getPage());
        assertEquals(0, products.getTotalPages());
        assertFalse(products.isHasNext());
    }

    @Test
    public void testPageBeyondResults() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "Test Product 1", 100.0);
        dto.addProduct(form);
        
        PaginatedData<ProductData> products = dto.getAllProducts(1); // Second page
        assertEquals(0, products.getData().size());
        assertEquals(1, products.getTotalItems());
        assertEquals(1, products.getPage());
        assertEquals(1, products.getTotalPages());
        assertFalse(products.isHasNext());
    }

    @Test
    public void testGetProductsByNamePrefix() throws ApiException {
        // Add multiple products with similar names
        ProductForm form1 = createTestProductForm("barcode1", "Test Product A", 100.0);
        ProductForm form2 = createTestProductForm("barcode2", "Test Product B", 200.0);
        ProductForm form3 = createTestProductForm("barcode3", "Different Name", 300.0);
        
        dto.addProduct(form1);
        dto.addProduct(form2);
        dto.addProduct(form3);
        
        // Search with prefix "Test"
        PaginatedData<ProductData> result = dto.getProductsByNamePrefix("Test", 0);
        
        assertEquals(2, result.getData().size());
        assertEquals(2, result.getTotalItems());
        assertEquals(0, result.getPage());
        assertEquals(1, result.getTotalPages());
        assertFalse(result.isHasNext());
        
        // Verify product details
        List<String> productNames = result.getData().stream()
            .map(ProductData::getProductName)
            .collect(Collectors.toList());
        assertTrue(productNames.contains("test product a"));
        assertTrue(productNames.contains("test product b"));
    }

    @Test
    public void testBulkAddProducts() throws ApiException {
        // Create multiple product forms
        List<ProductForm> forms = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            ProductForm form = createTestProductForm(
                "barcode" + i,
                "Test Product " + i,
                100.0 * i
            );
            forms.add(form);
        }
        
        // Bulk add products
        List<ProductData> addedProducts = dto.bulkAddProducts(forms);
        
        // Verify results
        assertEquals(3, addedProducts.size());
        
        // Verify each product
        for (int i = 0; i < addedProducts.size(); i++) {
            ProductData product = addedProducts.get(i);
            assertEquals("barcode" + (i + 1), product.getBarcode());
            assertEquals("test product " + (i + 1), product.getProductName());
            assertEquals(Double.valueOf(100.0 * (i + 1)), product.getMrp());
            assertEquals(testClientId, product.getClientId());
            assertEquals("test client", product.getClientName());
        }
        
        // Verify products are in database
        PaginatedData<ProductData> allProducts = dto.getAllProducts(0);
        assertEquals(3, allProducts.getTotalItems());
    }

    @Test(expected = ApiException.class)
    public void testBulkAddProductsWithDuplicateBarcode() throws ApiException {
        List<ProductForm> forms = new ArrayList<>();
        
        // Add two products with same barcode
        forms.add(createTestProductForm("barcode1", "Test Product 1", 100.0));
        forms.add(createTestProductForm("barcode1", "Test Product 2", 200.0));
        
        dto.bulkAddProducts(forms); // Should throw ApiException
    }

    @Test(expected = ApiException.class)
    public void testBulkAddTooManyProducts() throws ApiException {
        List<ProductForm> forms = new ArrayList<>();
        
        // Try to add more than 5000 products
        for (int i = 0; i < 5001; i++) {
            forms.add(createTestProductForm(
                "barcode" + i,
                "Test Product " + i,
                100.0
            ));
        }
        
        dto.bulkAddProducts(forms); // Should throw ApiException
    }

    @Test
    public void testGetProductsByNamePrefixCaseInsensitive() throws ApiException {
        ProductForm form = createTestProductForm("barcode1", "Test Product", 100.0);
        dto.addProduct(form);
        
        // Search with different cases
        PaginatedData<ProductData> result1 = dto.getProductsByNamePrefix("test", 0);
        PaginatedData<ProductData> result2 = dto.getProductsByNamePrefix("TEST", 0);
        PaginatedData<ProductData> result3 = dto.getProductsByNamePrefix("Test", 0);
        
        assertEquals(1, result1.getTotalItems());
        assertEquals(1, result2.getTotalItems());
        assertEquals(1, result3.getTotalItems());
    }
} 