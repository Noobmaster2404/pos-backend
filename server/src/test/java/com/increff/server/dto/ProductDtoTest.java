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
    public void testGetProductsByClientIdAndProductNameExactMatch() throws ApiException {
        // Add test products
        ProductForm form1 = createTestProductForm("barcode1", "test apple", 100.0);
        ProductForm form2 = createTestProductForm("barcode2", "test banana", 200.0);
        dto.addProduct(form1);
        dto.addProduct(form2);

        // Test exact match
        PaginatedData<ProductData> result = dto.getProductsByClientIdAndProductName(testClientId, "test apple", 0);
        assertEquals(1, result.getData().size());
        assertEquals("test apple", result.getData().get(0).getProductName());
        assertEquals(1, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        assertFalse(result.isHasNext());
    }

    @Test
    public void testGetProductsByClientIdAndProductNamePartialMatch() throws ApiException {
        // Add test products
        ProductForm form1 = createTestProductForm("barcode1", "test apple red", 100.0);
        ProductForm form2 = createTestProductForm("barcode2", "test apple green", 200.0);
        dto.addProduct(form1);
        dto.addProduct(form2);

        // Test partial match
        PaginatedData<ProductData> result = dto.getProductsByClientIdAndProductName(testClientId, "test app", 0);
        assertEquals(2, result.getData().size());
        assertEquals(2, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        assertFalse(result.isHasNext());
    }

    @Test
    public void testGetProductsByClientIdAndProductNameCaseInsensitive() throws ApiException {
        // Add test product
        ProductForm form = createTestProductForm("barcode1", "test apple", 100.0);
        dto.addProduct(form);

        // Test with different cases
        PaginatedData<ProductData> result1 = dto.getProductsByClientIdAndProductName(testClientId, "TEST APPLE", 0);
        PaginatedData<ProductData> result2 = dto.getProductsByClientIdAndProductName(testClientId, "Test Apple", 0);
        PaginatedData<ProductData> result3 = dto.getProductsByClientIdAndProductName(testClientId, "test apple", 0);

        assertEquals(1, result1.getData().size());
        assertEquals(1, result2.getData().size());
        assertEquals(1, result3.getData().size());
    }

    @Test
    public void testGetProductsByClientIdAndProductNameNoMatch() throws ApiException {
        // Add test product
        ProductForm form = createTestProductForm("barcode1", "test apple", 100.0);
        dto.addProduct(form);

        // Test no match
        PaginatedData<ProductData> result = dto.getProductsByClientIdAndProductName(testClientId, "orange", 0);
        assertEquals(0, result.getData().size());
        assertEquals(0, result.getTotalItems());
        assertEquals(0, result.getTotalPages());
        assertFalse(result.isHasNext());
    }

    @Test
    public void testGetProductsByClientIdAndProductNameNullProductName() throws ApiException {
        // Add test products
        ProductForm form1 = createTestProductForm("barcode1", "test apple", 100.0);
        ProductForm form2 = createTestProductForm("barcode2", "test banana", 200.0);
        dto.addProduct(form1);
        dto.addProduct(form2);

        // Test null product name
        PaginatedData<ProductData> result = dto.getProductsByClientIdAndProductName(testClientId, null, 0);
        assertEquals(2, result.getData().size());
        assertEquals(2, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        assertFalse(result.isHasNext());
    }

    @Test
    public void testGetProductsByClientIdAndProductNamePagination() throws ApiException {
        // Add 25 products
        for (int i = 0; i < 25; i++) {
            ProductForm form = createTestProductForm(
                "barcode" + i, 
                "test product " + i, 
                100.0 + i
            );
            dto.addProduct(form);
        }

        // Test first page
        PaginatedData<ProductData> page1 = dto.getProductsByClientIdAndProductName(testClientId, "test", 0);
        assertEquals(10, page1.getData().size());
        assertEquals(25, page1.getTotalItems());
        assertEquals(3, page1.getTotalPages());
        assertTrue(page1.isHasNext());

        // Test second page
        PaginatedData<ProductData> page2 = dto.getProductsByClientIdAndProductName(testClientId, "test", 1);
        assertEquals(10, page2.getData().size());
        assertEquals(25, page2.getTotalItems());
        assertEquals(3, page2.getTotalPages());
        assertTrue(page2.isHasNext());

        // Test last page
        PaginatedData<ProductData> page3 = dto.getProductsByClientIdAndProductName(testClientId, "test", 2);
        assertEquals(5, page3.getData().size());
        assertEquals(25, page3.getTotalItems());
        assertEquals(3, page3.getTotalPages());
        assertFalse(page3.isHasNext());
    }

    @Test
    public void testGetProductsByClientIdAndProductNameWithNullClientId() throws ApiException {
        // Add test products
        ProductForm form1 = createTestProductForm("barcode1", "test apple", 100.0);
        ProductForm form2 = createTestProductForm("barcode2", "test banana", 200.0);
        dto.addProduct(form1);
        dto.addProduct(form2);

        // Test with null clientId
        PaginatedData<ProductData> result = dto.getProductsByClientIdAndProductName(null, "test", 0);
        assertEquals(2, result.getData().size());
        assertEquals(2, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        assertFalse(result.isHasNext());
        
        // Verify client names are present in response
        assertEquals("test client", result.getData().get(0).getClientName());
        assertEquals("test client", result.getData().get(1).getClientName());
    }

    @Test
    public void testGetProductsByClientIdAndProductNameWithInvalidPage() throws ApiException {
        try {
            dto.getProductsByClientIdAndProductName(testClientId, "test", -1);
            fail("Expected ApiException was not thrown");
        } catch (ApiException e) {
            assertEquals("Page number cannot be negative", e.getMessage());
        }
    }

    @Test
    public void testGetProductsByClientIdAndProductNameWithEmptyString() throws ApiException {
        // Add test products
        ProductForm form1 = createTestProductForm("barcode1", "test apple", 100.0);
        ProductForm form2 = createTestProductForm("barcode2", "test banana", 200.0);
        dto.addProduct(form1);
        dto.addProduct(form2);

        // Test with empty string (should behave same as null)
        PaginatedData<ProductData> result = dto.getProductsByClientIdAndProductName(testClientId, "", 0);
        assertEquals(2, result.getData().size());
        assertEquals(2, result.getTotalItems());
        assertEquals(1, result.getTotalPages());
        assertFalse(result.isHasNext());
    }
} 